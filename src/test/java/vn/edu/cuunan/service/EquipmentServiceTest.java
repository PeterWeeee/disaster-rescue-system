package vn.edu.cuunan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.cuunan.dto.EquipmentDispatchDto;
import vn.edu.cuunan.dto.EquipmentReturnDto;
import vn.edu.cuunan.dto.TeamResourcesDto;
import vn.edu.cuunan.entity.*;
import vn.edu.cuunan.repository.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock
    private RescueEquipmentRepository equipmentRepository;

    @Mock
    private EquipmentAssignmentRepository eqAssignmentRepository;

    @Mock
    private RescueAssignmentRepository rescueAssignmentRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private NotificationService notificationService;

    private EquipmentService equipmentService;

    private RescueTeam team;
    private User rescuer;
    private RescueAssignment assignment;
    private RescueEquipment lifeJacket;

    @BeforeEach
    void setUp() {
        equipmentService = new EquipmentService(
                equipmentRepository,
                eqAssignmentRepository,
                rescueAssignmentRepository,
                teamMemberRepository,
                activityLogService,
                notificationService
        );

        rescuer = User.builder().id(10L).username("rescuer01").fullName("Nguyễn Cứu Hộ").build();
        team = new RescueTeam(1L, "Đội Đà Nẵng", rescuer, "0900000000", 16.05, 108.20, 5, "Áo phao", "AVAILABLE");

        SosRequest sos = SosRequest.builder().id(100L).contactName("Trần Dân").build();
        assignment = new RescueAssignment();
        assignment.setId(50L);
        assignment.setTeam(team);
        assignment.setSosRequest(sos);
        assignment.setStatus("ACCEPTED");

        lifeJacket = new RescueEquipment(team, "AO_PHAO", "Áo phao tiêu chuẩn", "SAFETY", "chiếc", 20, "Hàng hải");
        lifeJacket.setId(101L);
    }

    @Test
    @DisplayName("Cấp phát trang bị cho nhiệm vụ thành công - giảm tồn khả dụng")
    void testDispatchEquipment_Success() {
        EquipmentDispatchDto dto = new EquipmentDispatchDto();
        dto.setItems(List.of(new EquipmentDispatchDto.DispatchItem(101L, 4)));

        when(rescueAssignmentRepository.findById(50L)).thenReturn(Optional.of(assignment));
        when(equipmentRepository.findById(101L)).thenReturn(Optional.of(lifeJacket));
        when(eqAssignmentRepository.save(any(EquipmentAssignment.class))).thenAnswer(i -> i.getArgument(0));

        List<EquipmentAssignment> result = equipmentService.dispatchEquipment(50L, dto, rescuer);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(16, lifeJacket.getAvailableQuantity());
        assertEquals(4, lifeJacket.getDispatchedQuantity());
        verify(equipmentRepository).save(lifeJacket);
    }

    @Test
    @DisplayName("Cấp phát vượt quá số lượng khả dụng ném ngoại lệ IllegalStateException")
    void testDispatchEquipment_InsufficientStock() {
        EquipmentDispatchDto dto = new EquipmentDispatchDto();
        dto.setItems(List.of(new EquipmentDispatchDto.DispatchItem(101L, 25))); // Yêu cầu 25 trong khi chỉ có 20

        when(rescueAssignmentRepository.findById(50L)).thenReturn(Optional.of(assignment));
        when(equipmentRepository.findById(101L)).thenReturn(Optional.of(lifeJacket));

        assertThrows(IllegalStateException.class, () -> equipmentService.dispatchEquipment(50L, dto, rescuer));
    }

    @Test
    @DisplayName("Thu hồi và kiểm kê trang bị sau nhiệm vụ - khôi phục kho chính xác và chống cộng trùng")
    void testReturnEquipment_Success() {
        // Giả lập đã cấp phát 4 áo phao
        lifeJacket.setAvailableQuantity(16);
        lifeJacket.setDispatchedQuantity(4);

        EquipmentAssignment ea = new EquipmentAssignment(assignment, lifeJacket, 4);
        ea.setId(201L);
        ea.setStatus("DISPATCHED");

        when(rescueAssignmentRepository.findById(50L)).thenReturn(Optional.of(assignment));
        when(eqAssignmentRepository.findByRescueAssignmentIdAndStatus(50L, "DISPATCHED")).thenReturn(List.of(ea));

        // Thu hồi: 3 nguyên vẹn, 1 hư hỏng
        EquipmentReturnDto dto = new EquipmentReturnDto();
        dto.setItems(List.of(new EquipmentReturnDto.ReturnItem(201L, 3, 1, 0, 0)));

        equipmentService.returnEquipment(50L, dto, rescuer);

        assertEquals(19, lifeJacket.getAvailableQuantity()); // 16 + 3 nguyên vẹn = 19
        assertEquals(1, lifeJacket.getDamagedQuantity());    // 1 hư hỏng
        assertEquals(0, lifeJacket.getDispatchedQuantity()); // 4 - 4 = 0
        assertEquals("RETURNED", ea.getStatus());
        verify(eqAssignmentRepository).save(ea);
    }

    @Test
    @DisplayName("Thống kê quân số và trang bị trả về số liệu chính xác")
    void testGetTeamResources() {
        when(teamMemberRepository.findByTeamId(1L)).thenReturn(List.of());
        when(rescueAssignmentRepository.findByTeamId(1L)).thenReturn(List.of(assignment));
        when(equipmentRepository.findByTeamOrderByCategoryAscEquipmentNameAsc(team)).thenReturn(List.of(lifeJacket));

        TeamResourcesDto resources = equipmentService.getTeamResources(team);

        assertNotNull(resources);
        assertEquals(1L, resources.getTeamId());
        assertEquals("Đội Đà Nẵng", resources.getTeamName());
        assertEquals(1, resources.getTotalMembers());
        assertEquals(20, resources.getTotalEquipmentUnits());
        assertEquals(20, resources.getAvailableEquipmentUnits());
    }
}
