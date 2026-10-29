package vn.edu.cuunan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import vn.edu.cuunan.dto.AssignTaskRequestDto;
import vn.edu.cuunan.dto.RescueTeamDistanceDto;
import vn.edu.cuunan.entity.RescueAssignment;
import vn.edu.cuunan.entity.RescueTeam;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.RescueAssignmentRepository;
import vn.edu.cuunan.repository.RescueTeamRepository;
import vn.edu.cuunan.repository.SosRequestRepository;
import vn.edu.cuunan.repository.UserRepository;
import vn.edu.cuunan.util.GeoUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoordinatorServiceTest {

    @Mock
    private SosRequestRepository sosRequestRepository;

    @Mock
    private RescueTeamRepository rescueTeamRepository;

    @Mock
    private RescueAssignmentRepository rescueAssignmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private CoordinatorService coordinatorService;

    @BeforeEach
    void setUp() {
        coordinatorService = new CoordinatorService(
                sosRequestRepository,
                rescueTeamRepository,
                rescueAssignmentRepository,
                userRepository,
                messagingTemplate
        );
    }

    @Test
    @DisplayName("Kiểm tra công thức Haversine tính khoảng cách địa lý chính xác")
    void testHaversineFormula() {
        // Tọa độ Cầu Rồng Đà Nẵng [16.0611, 108.2238] đến Bán đảo Sơn Trà [16.1158, 108.2570]
        double dist = GeoUtils.calculateDistance(16.0611, 108.2238, 16.1158, 108.2570);
        assertTrue(dist > 5.0 && dist < 10.0, "Khoảng cách phải xấp xỉ 6-8 km");

        // Khoảng cách cùng 1 điểm phải bằng 0
        assertEquals(0.0, GeoUtils.calculateDistance(16.0, 108.0, 16.0, 108.0));
    }

    @Test
    @DisplayName("Thống kê số liệu Dashboard cho Coordinator")
    void testGetDashboardStats() {
        when(sosRequestRepository.countByStatus("PENDING")).thenReturn(5L);
        when(sosRequestRepository.countByStatus("DISPATCHED")).thenReturn(3L);
        when(sosRequestRepository.countByStatus("IN_PROGRESS")).thenReturn(2L);
        when(sosRequestRepository.countByStatus("RESOLVED")).thenReturn(10L);
        when(rescueTeamRepository.countByStatus("AVAILABLE")).thenReturn(4L);
        when(rescueTeamRepository.countByStatus("BUSY")).thenReturn(2L);
        when(rescueTeamRepository.count()).thenReturn(6L);
        when(sosRequestRepository.sumRescuedPeople()).thenReturn(25L);

        Map<String, Object> stats = coordinatorService.getDashboardStats();

        assertEquals(5L, stats.get("pendingCount"));
        assertEquals(3L, stats.get("dispatchedCount"));
        assertEquals(4L, stats.get("availableTeamsCount"));
        assertEquals(25L, stats.get("rescuedPeopleCount"));
    }

    @Test
    @DisplayName("Gợi ý các đội cứu hộ gần nhất sắp xếp theo khoảng cách Haversine")
    void testGetAvailableTeamsWithDistance() {
        SosRequest sos = SosRequest.builder()
                .id(1L)
                .latitude(16.06)
                .longitude(108.20)
                .build();
        when(sosRequestRepository.findById(1L)).thenReturn(Optional.of(sos));

        RescueTeam nearTeam = new RescueTeam(1L, "Đội Gần", null, "0901", 16.065, 108.205, 5, "Xuồng", "AVAILABLE");
        RescueTeam farTeam = new RescueTeam(2L, "Đội Xa", null, "0902", 16.200, 108.350, 4, "Xe lội nước", "AVAILABLE");

        when(rescueTeamRepository.findAll()).thenReturn(List.of(farTeam, nearTeam));

        List<RescueTeamDistanceDto> teams = coordinatorService.getAvailableTeamsWithDistance(1L);

        assertEquals(2, teams.size());
        assertEquals("Đội Gần", teams.get(0).getTeamName(), "Đội gần nhất phải được xếp đầu tiên");
        assertTrue(teams.get(0).getDistanceKm() < teams.get(1).getDistanceKm());
    }

    @Test
    @DisplayName("Phân công ca SOS cho đội cứu hộ thành công")
    void testAssignSosToTeamSuccess() {
        SosRequest sos = SosRequest.builder()
                .id(10L)
                .status("PENDING")
                .contactName("Lê Văn Cứu")
                .contactPhone("0912345678")
                .latitude(16.05)
                .longitude(108.21)
                .build();
        when(sosRequestRepository.findById(10L)).thenReturn(Optional.of(sos));

        RescueTeam team = new RescueTeam(2L, "Đội Hải Châu", null, "0988", 16.06, 108.22, 6, "2 xuồng", "AVAILABLE");
        when(rescueTeamRepository.findById(2L)).thenReturn(Optional.of(team));

        User coord = User.builder().id(99L).fullName("Điều Phối Viên").build();
        when(userRepository.findById(99L)).thenReturn(Optional.of(coord));

        when(rescueAssignmentRepository.save(any(RescueAssignment.class))).thenAnswer(inv -> {
            RescueAssignment a = inv.getArgument(0);
            a.setId(500L);
            return a;
        });

        AssignTaskRequestDto req = AssignTaskRequestDto.builder()
                .sosRequestId(10L)
                .teamId(2L)
                .notes("Ưu tiên trẻ nhỏ")
                .build();

        RescueAssignment result = coordinatorService.assignSosToTeam(req, 99L);

        assertNotNull(result);
        assertEquals(500L, result.getId());
        assertEquals("DISPATCHED", sos.getStatus(), "Trạng thái ca SOS phải chuyển thành DISPATCHED");
        assertEquals("BUSY", team.getStatus(), "Trạng thái đội phải chuyển thành BUSY");
        verify(rescueAssignmentRepository).save(any(RescueAssignment.class));
        verify(sosRequestRepository).save(sos);
        verify(rescueTeamRepository).save(team);
    }

    @Test
    @DisplayName("Lọc danh sách SOS và thống kê theo Tỉnh/Vùng động")
    void testFilterSosAndStatsByProvince() {
        SosRequest sosDN = SosRequest.builder()
                .id(1L)
                .contactName("Trần Văn A")
                .addressDetail("Số 45 đường Mẹ Suốt, Liên Chiểu, Đà Nẵng")
                .status("PENDING")
                .peopleCount(4)
                .build();
        SosRequest sosGL = SosRequest.builder()
                .id(2L)
                .contactName("Siu H'Búp")
                .addressDetail("Xã Ia Dêr, Huyện Ia Grai, Gia Lai")
                .status("DISPATCHED")
                .peopleCount(3)
                .build();

        when(sosRequestRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(sosDN, sosGL));
        when(sosRequestRepository.findAll()).thenReturn(List.of(sosDN, sosGL));

        RescueTeam teamDN = new RescueTeam(1L, "Đội Cứu Hộ Hải Châu (Đà Nẵng)", null, "0901", 16.06, 108.22, 6, "Xuồng", "AVAILABLE");
        RescueTeam teamGL = new RescueTeam(2L, "Đội Cứu Hộ Gia Lai 01", null, "0902", 13.98, 108.00, 5, "Xe 4x4", "AVAILABLE");
        when(rescueTeamRepository.findAll()).thenReturn(List.of(teamDN, teamGL));

        // 1. Khi chọn DA_NANG
        List<SosRequest> filteredDN = coordinatorService.getSosRequests("ALL", "DA_NANG");
        assertEquals(1, filteredDN.size());
        assertEquals("Trần Văn A", filteredDN.get(0).getContactName());

        List<RescueTeam> teamsDN = coordinatorService.getRescueTeams("DA_NANG");
        assertEquals(1, teamsDN.size());
        assertEquals("Đội Cứu Hộ Hải Châu (Đà Nẵng)", teamsDN.get(0).getTeamName());

        Map<String, Object> statsDN = coordinatorService.getDashboardStats("DA_NANG");
        assertEquals(1L, statsDN.get("pendingCount"));
        assertEquals(0L, statsDN.get("dispatchedCount"));
        assertEquals(1L, statsDN.get("totalTeamsCount"));

        // 2. Khi chọn ALL
        List<SosRequest> allSos = coordinatorService.getSosRequests("ALL", "ALL");
        assertEquals(2, allSos.size());
    }

    @Test
    @DisplayName("Điều phối viên có thể giao ca SOS thứ 2 cho đội cứu hộ đang BUSY")
    void testAssignMultipleMissionsToSameTeamSuccess() {
        SosRequest sos2 = SosRequest.builder()
                .id(20L)
                .status("PENDING")
                .contactName("Nguyễn Thị Hoa")
                .build();
        when(sosRequestRepository.findById(20L)).thenReturn(Optional.of(sos2));

        RescueTeam busyTeam = new RescueTeam(2L, "Đội Hải Châu", null, "0988", 16.06, 108.22, 6, "2 xuồng", "BUSY");
        when(rescueTeamRepository.findById(2L)).thenReturn(Optional.of(busyTeam));

        when(rescueAssignmentRepository.save(any(RescueAssignment.class))).thenAnswer(inv -> {
            RescueAssignment a = inv.getArgument(0);
            a.setId(501L);
            return a;
        });

        AssignTaskRequestDto req = AssignTaskRequestDto.builder()
                .sosRequestId(20L)
                .teamId(2L)
                .notes("Ca thứ 2 trên cùng trục đường ngập")
                .build();

        RescueAssignment result = coordinatorService.assignSosToTeam(req, 99L);

        assertNotNull(result);
        assertEquals(501L, result.getId());
        assertEquals("DISPATCHED", sos2.getStatus());
        assertEquals("BUSY", busyTeam.getStatus());
    }

    @Test
    @DisplayName("Phân công cùng một ca SOS 2 lần cho cùng một đội sẽ bị từ chối")
    void testAssignDuplicateSosToSameTeamThrows() {
        SosRequest sos = SosRequest.builder().id(30L).status("PENDING").build();
        when(sosRequestRepository.findById(30L)).thenReturn(Optional.of(sos));

        RescueTeam team = new RescueTeam(2L, "Đội Hải Châu", null, "0988", 16.06, 108.22, 6, "2 xuồng", "BUSY");
        when(rescueTeamRepository.findById(2L)).thenReturn(Optional.of(team));

        RescueAssignment existing = new RescueAssignment();
        existing.setTeam(team);
        existing.setStatus("ACCEPTED");
        when(rescueAssignmentRepository.findBySosRequestId(30L)).thenReturn(List.of(existing));

        AssignTaskRequestDto req = AssignTaskRequestDto.builder()
                .sosRequestId(30L)
                .teamId(2L)
                .build();

        assertThrows(IllegalStateException.class, () -> coordinatorService.assignSosToTeam(req, 99L));
    }
}
