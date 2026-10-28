package vn.edu.cuunan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import vn.edu.cuunan.dto.RouteTerrainAnalysisDto;
import vn.edu.cuunan.dto.StartMissionRequestDto;
import vn.edu.cuunan.entity.RescueAssignment;
import vn.edu.cuunan.entity.RescueTeam;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.RescueAssignmentRepository;
import vn.edu.cuunan.repository.RescueTeamRepository;
import vn.edu.cuunan.repository.SosRequestRepository;
import vn.edu.cuunan.repository.TeamMemberRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RescuerServiceTest {

    @Mock
    private RescueAssignmentRepository rescueAssignmentRepository;

    @Mock
    private RescueTeamRepository rescueTeamRepository;

    @Mock
    private SosRequestRepository sosRequestRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private RescuerService rescuerService;

    private User rescuerUser;
    private RescueTeam team;
    private SosRequest sos;
    private RescueAssignment assignment;

    @BeforeEach
    void setUp() {
        rescuerService = new RescuerService(
                rescueAssignmentRepository,
                rescueTeamRepository,
                sosRequestRepository,
                teamMemberRepository,
                cloudinaryService,
                messagingTemplate
        );

        rescuerUser = User.builder()
                .id(10L)
                .username("rescuer_leader")
                .fullName("Đội Trưởng Thử Nghiệm")
                .build();

        team = new RescueTeam(1L, "Đội Hải Châu", rescuerUser, "0900000003", 16.06, 108.22, 5, "Xuồng", "AVAILABLE");

        sos = SosRequest.builder()
                .id(100L)
                .contactName("Trần Văn A")
                .contactPhone("0912345678")
                .latitude(16.05)
                .longitude(108.21)
                .status("DISPATCHED")
                .build();

        assignment = new RescueAssignment();
        assignment.setId(50L);
        assignment.setTeam(team);
        assignment.setSosRequest(sos);
        assignment.setStatus("PENDING");
    }

    @Test
    @DisplayName("Đội trưởng chấp nhận nhiệm vụ cứu nạn thành công")
    void testAcceptAssignmentSuccess() {
        when(rescueAssignmentRepository.findById(50L)).thenReturn(Optional.of(assignment));
        when(rescueAssignmentRepository.save(any(RescueAssignment.class))).thenAnswer(inv -> inv.getArgument(0));

        RescueAssignment result = rescuerService.acceptAssignment(50L, rescuerUser);

        assertEquals("ACCEPTED", result.getStatus());
        assertNotNull(result.getAcceptedAt());
        assertEquals("IN_PROGRESS", sos.getStatus());
        assertEquals("BUSY", team.getStatus());
        verify(rescueAssignmentRepository).save(assignment);
        verify(sosRequestRepository).save(sos);
        verify(rescueTeamRepository).save(team);
    }

    @Test
    @DisplayName("Từ chối nhiệm vụ: chuyển thành REJECTED và trả ca SOS về PENDING")
    void testRejectAssignmentSuccess() {
        when(rescueAssignmentRepository.findById(50L)).thenReturn(Optional.of(assignment));
        when(rescueAssignmentRepository.save(any(RescueAssignment.class))).thenAnswer(inv -> inv.getArgument(0));

        RescueAssignment result = rescuerService.rejectAssignment(50L, rescuerUser, "Mực nước quá sâu xuồng không vào được");

        assertEquals("REJECTED", result.getStatus());
        assertEquals("Mực nước quá sâu xuồng không vào được", result.getRejectionReason());
        assertEquals("PENDING", sos.getStatus(), "Ca SOS phải được trả về PENDING để Coordinator phân công lại");
        assertEquals("AVAILABLE", team.getStatus(), "Đội từ chối phải được giải phóng để nhận nhiệm vụ khác");
        verify(rescueAssignmentRepository).save(assignment);
        verify(sosRequestRepository).save(sos);
        verify(rescueTeamRepository).save(team);
    }

    @Test
    @DisplayName("Báo cáo hoàn thành nhiệm vụ và nghiệm thu ca SOS")
    void testCompleteAssignmentSuccess() {
        assignment.setStatus("IN_PROGRESS");
        when(rescueAssignmentRepository.findById(50L)).thenReturn(Optional.of(assignment));
        when(rescueAssignmentRepository.save(any(RescueAssignment.class))).thenAnswer(inv -> inv.getArgument(0));

        RescueAssignment result = rescuerService.completeAssignment(50L, rescuerUser, "Đã cứu an toàn 4 người", null);

        assertEquals("COMPLETED", result.getStatus());
        assertNotNull(result.getCompletedAt());
        assertEquals("RESOLVED", sos.getStatus(), "Ca SOS phải đổi thành RESOLVED");
        assertNotNull(sos.getResolvedAt());
        assertEquals("AVAILABLE", team.getStatus(), "Đội cứu hộ quay về trạng thái AVAILABLE");
        verify(rescueAssignmentRepository).save(assignment);
        verify(sosRequestRepository).save(sos);
        verify(rescueTeamRepository).save(team);
    }

    @Test
    @DisplayName("Lấy dữ liệu phân tích tuyến đường và địa hình trước khi xuất phát")
    void testGetRouteAndTerrainAnalysisSuccess() {
        assignment.setStatus("ACCEPTED");
        when(rescueAssignmentRepository.findById(50L)).thenReturn(Optional.of(assignment));

        RouteTerrainAnalysisDto result = rescuerService.getRouteAndTerrainAnalysis(50L, rescuerUser);

        assertNotNull(result);
        assertEquals(50L, result.getAssignmentId());
        assertNotNull(result.getTerrainType());
        assertNotNull(result.getRecommendedVehicles());
        assertFalse(result.getRecommendedVehicles().isEmpty());
    }

    @Test
    @DisplayName("Bắt đầu di chuyển với phương tiện và trang thiết bị đã chọn")
    void testStartMovingWithEquipmentAndVehicle() {
        assignment.setStatus("ACCEPTED");
        when(rescueAssignmentRepository.findById(50L)).thenReturn(Optional.of(assignment));
        when(rescueAssignmentRepository.save(any(RescueAssignment.class))).thenAnswer(inv -> inv.getArgument(0));

        StartMissionRequestDto dto = new StartMissionRequestDto();
        dto.setVehicleType("BOAT_MOTOR");
        dto.setSelectedEquipment(List.of("Áo phao (4 cái)", "Cáng cứu thương", "Túi sơ cứu"));
        dto.setCustomNotes("Xuất phát khẩn cấp bằng ca-nô cao tốc");

        RescueAssignment result = rescuerService.startMoving(50L, rescuerUser, dto);

        assertEquals("IN_PROGRESS", result.getStatus());
        assertEquals("BOAT_MOTOR", result.getVehicleType());
        assertEquals("Áo phao (4 cái), Cáng cứu thương, Túi sơ cứu", result.getSelectedEquipment());
        assertEquals("IN_PROGRESS", sos.getStatus());
        verify(rescueAssignmentRepository).save(assignment);
        verify(sosRequestRepository).save(sos);
        verify(messagingTemplate).convertAndSend(eq("/topic/map-updates"), any(java.util.Map.class));
    }
}
