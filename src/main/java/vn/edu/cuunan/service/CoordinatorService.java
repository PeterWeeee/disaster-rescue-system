package vn.edu.cuunan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CoordinatorService {

    private static final Logger log = LoggerFactory.getLogger(CoordinatorService.class);

    private final SosRequestRepository sosRequestRepository;
    private final RescueTeamRepository rescueTeamRepository;
    private final RescueAssignmentRepository rescueAssignmentRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public CoordinatorService(SosRequestRepository sosRequestRepository,
                              RescueTeamRepository rescueTeamRepository,
                              RescueAssignmentRepository rescueAssignmentRepository,
                              UserRepository userRepository,
                              @Autowired(required = false) SimpMessagingTemplate messagingTemplate) {
        this.sosRequestRepository = sosRequestRepository;
        this.rescueTeamRepository = rescueTeamRepository;
        this.rescueAssignmentRepository = rescueAssignmentRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Thống kê tổng hợp số liệu cho Dashboard Điều phối viên.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        long pending = sosRequestRepository.countByStatus("PENDING");
        long dispatched = sosRequestRepository.countByStatus("DISPATCHED");
        long inProgress = sosRequestRepository.countByStatus("IN_PROGRESS");
        long resolved = sosRequestRepository.countByStatus("RESOLVED");
        long availableTeams = rescueTeamRepository.countByStatus("AVAILABLE");
        long busyTeams = rescueTeamRepository.countByStatus("BUSY");
        long totalTeams = rescueTeamRepository.count();
        Long rescuedPeople = sosRequestRepository.sumRescuedPeople();

        stats.put("pendingCount", pending);
        stats.put("dispatchedCount", dispatched);
        stats.put("inProgressCount", inProgress);
        stats.put("resolvedCount", resolved);
        stats.put("availableTeamsCount", availableTeams);
        stats.put("busyTeamsCount", busyTeams);
        stats.put("totalTeamsCount", totalTeams);
        stats.put("rescuedPeopleCount", rescuedPeople != null ? rescuedPeople : 0L);

        return stats;
    }

    /**
     * Lấy danh sách các ca SOS theo bộ lọc trạng thái.
     */
    @Transactional(readOnly = true)
    public List<SosRequest> getSosRequests(String statusFilter) {
        if (statusFilter == null || statusFilter.trim().isEmpty() || "ALL".equalsIgnoreCase(statusFilter)) {
            return sosRequestRepository.findAllByOrderByCreatedAtDesc();
        }
        return sosRequestRepository.findByStatusOrderByCreatedAtDesc(statusFilter.trim().toUpperCase());
    }

    /**
     * Gợi ý danh sách đội cứu hộ kèm khoảng cách Haversine (tính từ vị trí ca SOS).
     * Sắp xếp tăng dần theo khoảng cách (gần nhất lên đầu).
     */
    @Transactional(readOnly = true)
    public List<RescueTeamDistanceDto> getAvailableTeamsWithDistance(Long sosId) {
        SosRequest sos = sosRequestRepository.findById(sosId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca cứu nạn #" + sosId));

        List<RescueTeam> teams = rescueTeamRepository.findByStatusNot("OFFLINE");
        List<RescueTeamDistanceDto> result = new ArrayList<>();

        for (RescueTeam team : teams) {
            double distanceKm = GeoUtils.calculateDistance(
                    sos.getLatitude(), sos.getLongitude(),
                    team.getCurrentLatitude(), team.getCurrentLongitude()
            );

            String leaderName = (team.getLeader() != null) ? team.getLeader().getFullName() : "Chưa chỉ định";

            RescueTeamDistanceDto dto = RescueTeamDistanceDto.builder()
                    .id(team.getId())
                    .teamName(team.getTeamName())
                    .leaderName(leaderName)
                    .contactPhone(team.getContactPhone())
                    .currentLatitude(team.getCurrentLatitude())
                    .currentLongitude(team.getCurrentLongitude())
                    .memberCount(team.getMemberCount())
                    .equipmentNotes(team.getEquipmentNotes())
                    .status(team.getStatus())
                    .distanceKm(distanceKm)
                    .build();

            result.add(dto);
        }

        // Sắp xếp các đội theo khoảng cách km từ gần nhất đến xa nhất
        result.sort(Comparator.comparingDouble(RescueTeamDistanceDto::getDistanceKm));
        return result;
    }

    /**
     * Phân công nhiệm vụ cứu nạn cho Đội cứu hộ.
     */
    @Transactional
    public RescueAssignment assignSosToTeam(AssignTaskRequestDto dto, Long coordinatorUserId) {
        SosRequest sos = sosRequestRepository.findById(dto.getSosRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca cứu nạn #" + dto.getSosRequestId()));

        if ("RESOLVED".equalsIgnoreCase(sos.getStatus()) || "CANCELLED".equalsIgnoreCase(sos.getStatus())) {
            throw new IllegalStateException("Ca cứu hộ #" + sos.getId() + " đã kết thúc hoặc đã bị hủy, không thể phân công!");
        }

        RescueTeam team = rescueTeamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đội cứu hộ #" + dto.getTeamId()));

        if ("OFFLINE".equalsIgnoreCase(team.getStatus())) {
            throw new IllegalStateException("Đội cứu hộ '" + team.getTeamName() + "' hiện đang không hoạt động (Offline)!");
        }

        User coordinator = null;
        if (coordinatorUserId != null) {
            coordinator = userRepository.findById(coordinatorUserId).orElse(null);
        }

        RescueAssignment assignment = new RescueAssignment();
        assignment.setSosRequest(sos);
        assignment.setTeam(team);
        assignment.setAssignedBy(coordinator);
        assignment.setStatus("PENDING");
        assignment.setAssignedAt(LocalDateTime.now());
        if (dto.getNotes() != null && !dto.getNotes().trim().isEmpty()) {
            assignment.setReportNotes("Chỉ đạo điều phối: " + dto.getNotes().trim());
        }

        RescueAssignment savedAssignment = rescueAssignmentRepository.save(assignment);

        // Cập nhật trạng thái ca SOS thành DISPATCHED
        sos.setStatus("DISPATCHED");
        sosRequestRepository.save(sos);

        // Cập nhật trạng thái đội thành BUSY (nếu đang AVAILABLE)
        if ("AVAILABLE".equalsIgnoreCase(team.getStatus())) {
            team.setStatus("BUSY");
            rescueTeamRepository.save(team);
        }

        log.info(">>> ĐIỀU PHỐI THÀNH CÔNG: Ca SOS #{} -> Đội '{}' (Mã phân công #{})",
                sos.getId(), team.getTeamName(), savedAssignment.getId());

        // Gửi thông báo WebSocket Realtime (nếu có)
        sendRealtimeNotifications(savedAssignment, sos, team);

        return savedAssignment;
    }

    private void sendRealtimeNotifications(RescueAssignment assignment, SosRequest sos, RescueTeam team) {
        if (messagingTemplate == null) {
            return;
        }
        try {
            // 1. Cập nhật trạng thái ca cứu hộ trên bản đồ điều phối chung
            Map<String, Object> mapUpdate = new HashMap<>();
            mapUpdate.put("type", "ASSIGNED");
            mapUpdate.put("sosId", sos.getId());
            mapUpdate.put("status", "DISPATCHED");
            mapUpdate.put("teamName", team.getTeamName());
            messagingTemplate.convertAndSend("/topic/map-updates", mapUpdate);

            // 2. Bắn thông báo nhận việc cho đội cứu hộ
            Map<String, Object> assignmentMsg = new HashMap<>();
            assignmentMsg.put("assignmentId", assignment.getId());
            assignmentMsg.put("teamId", team.getId());
            assignmentMsg.put("sosId", sos.getId());
            assignmentMsg.put("contactName", sos.getContactName());
            assignmentMsg.put("contactPhone", sos.getContactPhone());
            assignmentMsg.put("address", sos.getAddressDetail());
            assignmentMsg.put("urgency", sos.getUrgencyLevel());
            messagingTemplate.convertAndSend("/topic/assignments", assignmentMsg);
        } catch (Exception ex) {
            log.warn("Lỗi khi gửi WebSocket notification: {}", ex.getMessage());
        }
    }
}
