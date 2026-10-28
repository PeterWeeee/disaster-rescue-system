package vn.edu.cuunan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import vn.edu.cuunan.dto.GeneralFundSummaryDto;
import vn.edu.cuunan.dto.TeamFundRequestDto;
import vn.edu.cuunan.entity.RescueTeam;
import vn.edu.cuunan.entity.RescueEquipment;
import vn.edu.cuunan.entity.Role;
import vn.edu.cuunan.entity.TeamFundRequest;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.DonationRepository;
import vn.edu.cuunan.repository.RescueTeamRepository;
import vn.edu.cuunan.repository.RescueEquipmentRepository;
import vn.edu.cuunan.repository.TeamFundRequestRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private TeamFundRequestRepository fundRequestRepository;

    @Mock
    private RescueTeamRepository rescueTeamRepository;

    @Mock
    private RescueEquipmentRepository equipmentRepository;

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private RescuerService rescuerService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private BudgetService budgetService;

    private User rescuerUser;
    private User coordUser;
    private RescueTeam rescueTeam;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(budgetService, "equipmentRepository", equipmentRepository);
        Role rescuerRole = new Role(3, "ROLE_RESCUER");
        Role coordRole = new Role(2, "ROLE_COORDINATOR");

        rescuerUser = User.builder()
                .id(10L)
                .username("rescuer_test")
                .fullName("Nguyễn Cứu Hộ")
                .role(rescuerRole)
                .build();

        coordUser = User.builder()
                .id(20L)
                .username("coord_test")
                .fullName("Trần Điều Phối")
                .role(coordRole)
                .build();

        rescueTeam = new RescueTeam(
                1L,
                "Đội Cứu Hộ Hải Châu",
                rescuerUser,
                "0901234567",
                16.05,
                108.20,
                8,
                "Xuồng máy, áo phao",
                "AVAILABLE",
                new BigDecimal("10000000") // 10 triệu
        );
    }

    @Test
    @DisplayName("Tính tổng Quỹ Cứu Trợ Chung của tổ chức chính xác")
    void testGetOrganizationGeneralFund() {
        when(donationRepository.sumAmountByStatus("SUCCESS")).thenReturn(new BigDecimal("50000000")); // 50tr
        when(fundRequestRepository.sumApprovedAmount()).thenReturn(new BigDecimal("20000000")); // 20tr

        GeneralFundSummaryDto summary = budgetService.getOrganizationGeneralFund();

        assertNotNull(summary);
        assertEquals(new BigDecimal("500000000"), summary.getInitialBaseFund()); // 500tr
        assertEquals(new BigDecimal("50000000"), summary.getTotalDonationsReceived());
        assertEquals(new BigDecimal("550000000"), summary.getTotalGrossFund());
        assertEquals(new BigDecimal("20000000"), summary.getTotalDisbursedFunds());
        assertEquals(new BigDecimal("530000000"), summary.getAvailableGeneralFund()); // 500 + 50 - 20 = 530tr
        assertTrue(summary.getFormattedAvailableGeneralFund().contains("530.000.000") || summary.getFormattedAvailableGeneralFund().contains("530,000,000"));
    }

    @Test
    @DisplayName("Đội cứu hộ gửi yêu cầu tiếp tế ngân sách thành công")
    void testCreateFundRequestSuccess() {
        TeamFundRequestDto dto = new TeamFundRequestDto(
                new BigDecimal("5000000"),
                "Mua 50 thùng mì tôm và nước sạch tiếp tế rốn lũ",
                "SUPPLY",
                "HIGH"
        );

        when(rescuerService.getMyTeam(rescuerUser)).thenReturn(Optional.of(rescueTeam));
        when(fundRequestRepository.save(any(TeamFundRequest.class))).thenAnswer(inv -> {
            TeamFundRequest req = inv.getArgument(0);
            req.setId(101L);
            return req;
        });

        TeamFundRequest created = budgetService.createFundRequest(rescuerUser, dto);

        assertNotNull(created);
        assertEquals(101L, created.getId());
        assertEquals("PENDING", created.getStatus());
        assertEquals(new BigDecimal("5000000"), created.getAmount());
        assertEquals("SUPPLY", created.getCategory());
        assertEquals("HIGH", created.getUrgencyLevel());
        assertEquals(rescueTeam, created.getRescueTeam());
        assertEquals(rescuerUser, created.getRequester());

        verify(fundRequestRepository, times(1)).save(any(TeamFundRequest.class));
        verify(notificationService, times(1)).notifyBudgetRequestCreated(any(TeamFundRequest.class));
    }

    @Test
    @DisplayName("Gửi yêu cầu tiếp tế thất bại khi người dùng không thuộc đội nào")
    void testCreateFundRequestNoTeamThrows() {
        TeamFundRequestDto dto = new TeamFundRequestDto(
                new BigDecimal("5000000"),
                "Mua nhu yếu phẩm",
                "SUPPLY",
                "HIGH"
        );

        when(rescuerService.getMyTeam(rescuerUser)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> budgetService.createFundRequest(rescuerUser, dto));
        verify(fundRequestRepository, never()).save(any(TeamFundRequest.class));
    }

    @Test
    @DisplayName("Giỏ tiếp tế lưu tên vật dụng xác thực từ kho và giữ trạng thái chờ duyệt")
    void testCreateFundRequestWithValidatedCart() {
        RescueEquipment equipment = new RescueEquipment(rescueTeam, "MED-1", "Túi sơ cứu", "MEDICAL", "bộ", 4, null);
        equipment.setId(31L);
        equipment.setUnitPrice(new BigDecimal("150000"));
        TeamFundRequestDto dto = new TeamFundRequestDto(new BigDecimal("2000000"), "Bổ sung cho ca cứu hộ sắp tới", "MEDICAL", "HIGH");
        dto.setItems(List.of(new TeamFundRequestDto.CartItem(31L, 3)));
        when(rescuerService.getMyTeam(rescuerUser)).thenReturn(Optional.of(rescueTeam));
        when(equipmentRepository.findById(31L)).thenReturn(Optional.of(equipment));
        when(fundRequestRepository.save(any(TeamFundRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        TeamFundRequest created = budgetService.createFundRequest(rescuerUser, dto);

        assertEquals("PENDING", created.getStatus());
        assertEquals(new BigDecimal("450000"), created.getAmount());
        assertTrue(created.getPurpose().contains("Túi sơ cứu x 3 bộ"));
        verify(fundRequestRepository).save(any(TeamFundRequest.class));
    }

    @Test
    @DisplayName("Không nhận số lượng giỏ bằng 0")
    void testCreateFundRequestRejectsInvalidCartQuantity() {
        TeamFundRequestDto dto = new TeamFundRequestDto(new BigDecimal("2000000"), "Bổ sung cho nhiệm vụ", "SUPPLY", "HIGH");
        dto.setItems(List.of(new TeamFundRequestDto.CartItem(31L, 0)));
        when(rescuerService.getMyTeam(rescuerUser)).thenReturn(Optional.of(rescueTeam));

        assertThrows(IllegalArgumentException.class, () -> budgetService.createFundRequest(rescuerUser, dto));
        verify(equipmentRepository, never()).findById(anyLong());
        verify(fundRequestRepository, never()).save(any(TeamFundRequest.class));
    }

    @Test
    @DisplayName("Điều phối viên duyệt tiếp tế: Quỹ chung bị trừ, ngân sách đội được cộng thêm")
    void testApproveFundRequestSuccess() {
        TeamFundRequest pendingRequest = TeamFundRequest.builder()
                .id(201L)
                .rescueTeam(rescueTeam)
                .requester(rescuerUser)
                .amount(new BigDecimal("5000000")) // 5tr
                .purpose("Mua nhiên liệu xuồng máy")
                .category("FUEL")
                .urgencyLevel("CRITICAL")
                .status("PENDING")
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        when(fundRequestRepository.findById(201L)).thenReturn(Optional.of(pendingRequest));
        when(donationRepository.sumAmountByStatus("SUCCESS")).thenReturn(BigDecimal.ZERO);
        when(fundRequestRepository.sumApprovedAmount()).thenReturn(BigDecimal.ZERO); // Quỹ khả dụng = 500tr
        when(rescueTeamRepository.save(any(RescueTeam.class))).thenAnswer(inv -> inv.getArgument(0));
        when(fundRequestRepository.save(any(TeamFundRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        TeamFundRequest approved = budgetService.approveFundRequest(201L, coordUser);

        assertNotNull(approved);
        assertEquals("APPROVED", approved.getStatus());
        assertEquals(coordUser, approved.getReviewedBy());
        assertNotNull(approved.getReviewedAt());

        // Ngân sách đội ban đầu là 10tr + 5tr được duyệt = 15tr
        assertEquals(new BigDecimal("15000000"), rescueTeam.getBudget());
        verify(rescueTeamRepository, times(1)).save(rescueTeam);
        verify(fundRequestRepository, times(1)).save(pendingRequest);
        verify(notificationService, times(1)).notifyBudgetRequestApproved(pendingRequest);
        verify(activityLogService, times(1)).log(eq(coordUser), eq("APPROVE_BUDGET_REQUEST"), anyString(), anyString(), isNull());
    }

    @Test
    @DisplayName("Duyệt thất bại khi số tiền yêu cầu vượt quá Quỹ Cứu Trợ Chung khả dụng")
    void testApproveFundRequestInsufficientFundThrows() {
        TeamFundRequest largeRequest = TeamFundRequest.builder()
                .id(202L)
                .rescueTeam(rescueTeam)
                .requester(rescuerUser)
                .amount(new BigDecimal("600000000")) // 600tr (lớn hơn 500tr)
                .purpose("Mua canô cứu hộ mới")
                .category("GEAR")
                .urgencyLevel("HIGH")
                .status("PENDING")
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        when(fundRequestRepository.findById(202L)).thenReturn(Optional.of(largeRequest));
        when(donationRepository.sumAmountByStatus("SUCCESS")).thenReturn(BigDecimal.ZERO);
        when(fundRequestRepository.sumApprovedAmount()).thenReturn(BigDecimal.ZERO); // Quỹ khả dụng chỉ có 500tr

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> budgetService.approveFundRequest(202L, coordUser));

        assertTrue(ex.getMessage().contains("không đủ để giải ngân"));
        verify(rescueTeamRepository, never()).save(any(RescueTeam.class));
    }

    @Test
    @DisplayName("Duyệt thất bại khi yêu cầu đã được xử lý trước đó")
    void testApproveFundRequestAlreadyProcessedThrows() {
        TeamFundRequest alreadyApproved = TeamFundRequest.builder()
                .id(203L)
                .rescueTeam(rescueTeam)
                .requester(rescuerUser)
                .amount(new BigDecimal("5000000"))
                .purpose("Mua mì tôm")
                .category("SUPPLY")
                .urgencyLevel("HIGH")
                .status("APPROVED") // đã duyệt rồi
                .build();

        when(fundRequestRepository.findById(203L)).thenReturn(Optional.of(alreadyApproved));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> budgetService.approveFundRequest(203L, coordUser));

        assertTrue(ex.getMessage().contains("đã được xử lý"));
    }

    @Test
    @DisplayName("Điều phối viên từ chối tiếp tế: Ghi nhận lý do, không thay đổi ngân sách đội và quỹ chung")
    void testRejectFundRequestSuccess() {
        TeamFundRequest pendingRequest = TeamFundRequest.builder()
                .id(204L)
                .rescueTeam(rescueTeam)
                .requester(rescuerUser)
                .amount(new BigDecimal("10000000"))
                .purpose("Mua lều bạt dự phòng")
                .category("GEAR")
                .urgencyLevel("MEDIUM")
                .status("PENDING")
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        when(fundRequestRepository.findById(204L)).thenReturn(Optional.of(pendingRequest));
        when(fundRequestRepository.save(any(TeamFundRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        TeamFundRequest rejected = budgetService.rejectFundRequest(204L, "Ưu tiên phân bổ cho khu vực lũ quét rốn lũ", coordUser);

        assertNotNull(rejected);
        assertEquals("REJECTED", rejected.getStatus());
        assertEquals("Ưu tiên phân bổ cho khu vực lũ quét rốn lũ", rejected.getRejectReason());
        assertEquals(coordUser, rejected.getReviewedBy());
        assertNotNull(rejected.getReviewedAt());

        // Ngân sách đội giữ nguyên 10tr
        assertEquals(new BigDecimal("10000000"), rescueTeam.getBudget());
        verify(rescueTeamRepository, never()).save(any(RescueTeam.class));
        verify(fundRequestRepository, times(1)).save(pendingRequest);
        verify(notificationService, times(1)).notifyBudgetRequestRejected(pendingRequest);
        verify(activityLogService, times(1)).log(eq(coordUser), eq("REJECT_BUDGET_REQUEST"), anyString(), anyString(), isNull());
    }

    @Test
    @DisplayName("Từ chối thất bại khi yêu cầu đã được xử lý trước đó")
    void testRejectFundRequestAlreadyProcessedThrows() {
        TeamFundRequest alreadyRejected = TeamFundRequest.builder()
                .id(205L)
                .rescueTeam(rescueTeam)
                .status("REJECTED")
                .build();

        when(fundRequestRepository.findById(205L)).thenReturn(Optional.of(alreadyRejected));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> budgetService.rejectFundRequest(205L, "Lý do", coordUser));

        assertTrue(ex.getMessage().contains("đã được xử lý"));
    }
}
