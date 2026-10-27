package vn.edu.cuunan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cuunan.dto.AssignTaskRequestDto;
import vn.edu.cuunan.dto.RescueTeamDistanceDto;
import vn.edu.cuunan.dto.RouteTerrainAnalysisDto;
import vn.edu.cuunan.entity.RescueAssignment;
import vn.edu.cuunan.entity.RescueTeam;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.RescueAssignmentRepository;
import vn.edu.cuunan.repository.RescueTeamRepository;
import vn.edu.cuunan.repository.SosRequestRepository;
import vn.edu.cuunan.repository.UserRepository;
import vn.edu.cuunan.util.GeoUtils;
import vn.edu.cuunan.util.ProvinceRegion;

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
    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;
    private final TerrainAnalysisService terrainAnalysisService;

    public CoordinatorService(SosRequestRepository sosRequestRepository,
                              RescueTeamRepository rescueTeamRepository,
                              RescueAssignmentRepository rescueAssignmentRepository,
                              UserRepository userRepository,
                              SimpMessagingTemplate messagingTemplate) {
        this(sosRequestRepository, rescueTeamRepository, rescueAssignmentRepository, userRepository,
             messagingTemplate, null, null, new TerrainAnalysisService());
    }

    @Autowired
    public CoordinatorService(SosRequestRepository sosRequestRepository,
                              RescueTeamRepository rescueTeamRepository,
                              RescueAssignmentRepository rescueAssignmentRepository,
                              UserRepository userRepository,
                              @Autowired(required = false) SimpMessagingTemplate messagingTemplate,
                              @Autowired(required = false) NotificationService notificationService,
                              @Autowired(required = false) ActivityLogService activityLogService,
                              @Autowired(required = false) TerrainAnalysisService terrainAnalysisService) {
        this.sosRequestRepository = sosRequestRepository;
        this.rescueTeamRepository = rescueTeamRepository;
        this.rescueAssignmentRepository = rescueAssignmentRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
        this.activityLogService = activityLogService;
        this.terrainAnalysisService = terrainAnalysisService != null ? terrainAnalysisService : new TerrainAnalysisService();
    }

    /**
     * Thống kê tổng hợp số liệu cho Dashboard Điều phối viên theo toàn vùng hoặc từng tỉnh/thành.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        return getDashboardStats("ALL");
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats(String provinceCode) {
        ProvinceRegion region = ProvinceRegion.fromCode(provinceCode);
        if (region == ProvinceRegion.ALL) {
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
            stats.put("regionName", region.getName());
            stats.put("regionCode", region.getCode());
            stats.put("centerLat", region.getCenterLat());
            stats.put("centerLng", region.getCenterLng());
            stats.put("defaultZoom", region.getDefaultZoom());
            return stats;
        }

        List<SosRequest> allSos = sosRequestRepository.findAll();
        List<SosRequest> filteredSos = allSos.stream()
                .filter(s -> region.matches(s.getAddressDetail()) || region.matches(s.getDescription()))
                .toList();

        List<RescueTeam> allTeams = rescueTeamRepository.findAll();
        List<RescueTeam> filteredTeams = allTeams.stream()
                .filter(t -> region.matches(t.getTeamName()) || (t.getLeader() != null && region.matches(t.getLeader().getAddress())))
                .toList();

        long pending = filteredSos.stream().filter(s -> "PENDING".equalsIgnoreCase(s.getStatus())).count();
        long dispatched = filteredSos.stream().filter(s -> "DISPATCHED".equalsIgnoreCase(s.getStatus())).count();
        long inProgress = filteredSos.stream().filter(s -> "IN_PROGRESS".equalsIgnoreCase(s.getStatus())).count();
        long resolved = filteredSos.stream().filter(s -> "RESOLVED".equalsIgnoreCase(s.getStatus())).count();

        long availableTeams = filteredTeams.stream().filter(t -> "AVAILABLE".equalsIgnoreCase(t.getStatus())).count();
        long busyTeams = filteredTeams.stream().filter(t -> "BUSY".equalsIgnoreCase(t.getStatus())).count();
        long totalTeams = filteredTeams.size();

        long rescuedPeople = filteredSos.stream()
                .filter(s -> "RESOLVED".equalsIgnoreCase(s.getStatus()))
                .mapToLong(s -> s.getPeopleCount() != null ? s.getPeopleCount() : 0)
                .sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("pendingCount", pending);
        stats.put("dispatchedCount", dispatched);
        stats.put("inProgressCount", inProgress);
        stats.put("resolvedCount", resolved);
        stats.put("availableTeamsCount", availableTeams);
        stats.put("busyTeamsCount", busyTeams);
        stats.put("totalTeamsCount", totalTeams);
        stats.put("rescuedPeopleCount", rescuedPeople);
        stats.put("regionName", region.getName());
        stats.put("regionCode", region.getCode());
        stats.put("centerLat", region.getCenterLat());
        stats.put("centerLng", region.getCenterLng());
        stats.put("defaultZoom", region.getDefaultZoom());

        return stats;
    }

    /**
     * Lấy danh sách các ca SOS theo bộ lọc trạng thái và khu vực tỉnh/thành.
     */
    @Transactional(readOnly = true)
    public List<SosRequest> getSosRequests(String statusFilter) {
        return getSosRequests(statusFilter, "ALL");
    }

    @Transactional(readOnly = true)
    public List<SosRequest> getSosRequests(String statusFilter, String provinceCode) {
        List<SosRequest> list;
        if (statusFilter == null || statusFilter.trim().isEmpty() || "ALL".equalsIgnoreCase(statusFilter)) {
            list = sosRequestRepository.findAllByOrderByCreatedAtDesc();
        } else {
            list = sosRequestRepository.findByStatusOrderByCreatedAtDesc(statusFilter.trim().toUpperCase());
        }

        ProvinceRegion region = ProvinceRegion.fromCode(provinceCode);
        if (region == ProvinceRegion.ALL) {
            return list;
        }

        return list.stream()
                .filter(s -> region.matches(s.getAddressDetail()) || region.matches(s.getDescription()))
                .toList();
    }

    /**
     * Lấy danh sách đội cứu hộ kèm thông tin đội trưởng, lọc theo tỉnh/thành.
     */
    @Transactional(readOnly = true)
    public List<RescueTeam> getRescueTeams() {
        return getRescueTeams("ALL");
    }

    @Transactional(readOnly = true)
    public List<RescueTeam> getRescueTeams(String provinceCode) {
        List<RescueTeam> teams = rescueTeamRepository.findAll();
        ProvinceRegion region = ProvinceRegion.fromCode(provinceCode);
        if (region == ProvinceRegion.ALL) {
            return teams;
        }
        return teams.stream()
                .filter(t -> region.matches(t.getTeamName()) || (t.getLeader() != null && region.matches(t.getLeader().getAddress())))
                .toList();
    }

    /**
     * Tính toán khoảng cách (Haversine) từ tọa độ ca SOS tới tất cả các đội cứu hộ.
     */
    @Transactional(readOnly = true)
    public List<RescueTeamDistanceDto> calculateTeamsDistanceToSos(Long sosRequestId) {
        SosRequest sos = sosRequestRepository.findById(sosRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca cứu nạn #" + sosRequestId));

        List<RescueTeam> teams = rescueTeamRepository.findAll();
        return toDistanceDtos(sos, teams);
    }

    /**
     * Gợi ý danh sách đội cứu hộ đang AVAILABLE kèm khoảng cách Haversine (tính từ vị trí ca SOS).
     * Sắp xếp tăng dần theo khoảng cách (gần nhất lên đầu).
     */
    @Transactional(readOnly = true)
    public List<RescueTeamDistanceDto> getAvailableTeamsWithDistance(Long sosRequestId) {
        SosRequest sos = sosRequestRepository.findById(sosRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca cứu nạn #" + sosRequestId));

        List<RescueTeam> teams = rescueTeamRepository.findByStatus("AVAILABLE");
        return toDistanceDtos(sos, teams);
    }

    private List<RescueTeamDistanceDto> toDistanceDtos(SosRequest sos, List<RescueTeam> teams) {
        List<RescueTeamDistanceDto> result = new ArrayList<>();
        double sosLat = sos.getLatitude();
        double sosLng = sos.getLongitude();

        for (RescueTeam team : teams) {
            double distanceKm = -1.0;
            if (team.getCurrentLatitude() != null && team.getCurrentLongitude() != null) {
                distanceKm = GeoUtils.calculateDistance(
                        sosLat, sosLng,
                        team.getCurrentLatitude(), team.getCurrentLongitude()
                );
            }

            RescueTeamDistanceDto dto = RescueTeamDistanceDto.builder()
                    .teamId(team.getId())
                    .id(team.getId())
                    .teamName(team.getTeamName())
                    .contactPhone(team.getContactPhone())
                    .leaderName(team.getLeader() != null ? team.getLeader().getFullName() : "Chưa chỉ định")
                    .memberCount(team.getMemberCount())
                    .equipmentNotes(team.getEquipmentNotes())
                    .currentLatitude(team.getCurrentLatitude())
                    .currentLongitude(team.getCurrentLongitude())
                    .status(team.getStatus())
                    .distanceKm(distanceKm)
                    .build();

            result.add(dto);
        }

        result.sort(Comparator.comparingDouble(RescueTeamDistanceDto::getDistanceKm));
        return result;
    }

    /**
     * Phân công nhiệm vụ cứu nạn cho Đội cứu hộ (Hỗ trợ ca PENDING và đổi đội cho ca DISPATCHED).
     */
    @Transactional
    public RescueAssignment assignSosToTeam(AssignTaskRequestDto dto, Long coordinatorUserId) {
        SosRequest sos = sosRequestRepository.findById(dto.getSosRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca cứu nạn #" + dto.getSosRequestId()));

        if (!"PENDING".equalsIgnoreCase(sos.getStatus()) && !"DISPATCHED".equalsIgnoreCase(sos.getStatus())) {
            throw new IllegalStateException("Chỉ có thể điều phối ca SOS đang ở trạng thái PENDING hoặc DISPATCHED (hiện tại: " + sos.getStatus() + ")");
        }

        RescueTeam team = rescueTeamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đội cứu hộ #" + dto.getTeamId()));

        if (!"AVAILABLE".equalsIgnoreCase(team.getStatus())) {
            throw new IllegalStateException("Đội cứu hộ '" + team.getTeamName() + "' hiện không sẵn sàng nhận nhiệm vụ!");
        }

        // Nếu ca SOS đang ở trạng thái DISPATCHED (đổi đội điều phối): Hủy phân công cũ đang PENDING
        if ("DISPATCHED".equalsIgnoreCase(sos.getStatus())) {
            List<RescueAssignment> oldAssignments = rescueAssignmentRepository.findBySosRequestId(sos.getId());
            for (RescueAssignment old : oldAssignments) {
                if ("PENDING".equalsIgnoreCase(old.getStatus())) {
                    old.setStatus("REPLACED");
                    old.setRejectionReason("Điều phối viên chuyển phân công sang đội khác");
                    rescueAssignmentRepository.save(old);

                    RescueTeam oldTeam = old.getTeam();
                    if (oldTeam != null && !oldTeam.getId().equals(team.getId())) {
                        oldTeam.setStatus("AVAILABLE");
                        rescueTeamRepository.save(oldTeam);
                    }
                }
            }
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

        // Cập nhật trạng thái đội thành BUSY
        if ("AVAILABLE".equalsIgnoreCase(team.getStatus())) {
            team.setStatus("BUSY");
            rescueTeamRepository.save(team);
        }

        log.info(">>> ĐIỀU PHỐI THÀNH CÔNG: Ca SOS #{} -> Đội '{}' (Mã phân công #{})",
                sos.getId(), team.getTeamName(), savedAssignment.getId());

        // Gửi thông báo cho Đội cứu hộ
        if (notificationService != null) {
            notificationService.sendToTeam(team,
                    "Nhiệm vụ cứu nạn mới: Ca SOS #" + sos.getId(),
                    "Bạn nhận lệnh cứu nạn tại: " + (sos.getAddressDetail() != null ? sos.getAddressDetail() : "Chưa có địa chỉ")
                            + " - Mức độ: " + sos.getUrgencyLevel() + " (" + sos.getPeopleCount() + " người). Vui lòng vào bàn tác nghiệp để xác nhận.",
                    "MISSION_NEW",
                    "/rescuer/missions");
        }

        // Ghi nhật ký kiểm toán
        if (activityLogService != null) {
            activityLogService.log(coordinator, "DISPATCH_MISSION", "Nhiệm vụ #" + savedAssignment.getId(),
                    "Điều phối ca SOS #" + sos.getId() + " cho đội " + team.getTeamName(), null);
        }

        // Gửi thông báo WebSocket Realtime
        sendRealtimeNotifications(savedAssignment, sos, team);

        return savedAssignment;
    }

    private void sendRealtimeNotifications(RescueAssignment assignment, SosRequest sos, RescueTeam team) {
        if (messagingTemplate == null) {
            return;
        }
        try {
            Map<String, Object> mapUpdate = new HashMap<>();
            mapUpdate.put("type", "ASSIGNED");
            mapUpdate.put("sosId", sos.getId());
            mapUpdate.put("status", "DISPATCHED");
            mapUpdate.put("teamName", team.getTeamName());
            messagingTemplate.convertAndSend("/topic/map-updates", mapUpdate);

            Map<String, Object> assignmentMsg = new HashMap<>();
            assignmentMsg.put("assignmentId", assignment.getId());
            assignmentMsg.put("teamId", team.getId());
            assignmentMsg.put("sosId", sos.getId());
            assignmentMsg.put("contactName", sos.getContactName());
            assignmentMsg.put("contactPhone", sos.getContactPhone());
            assignmentMsg.put("address", sos.getAddressDetail());
            assignmentMsg.put("urgency", sos.getUrgencyLevel());
            if (team.getLeader() != null && team.getLeader().getUsername() != null) {
                messagingTemplate.convertAndSendToUser(team.getLeader().getUsername(), "/queue/assignments", assignmentMsg);
            }
        } catch (Exception ex) {
            log.warn("Lỗi khi gửi WebSocket notification: {}", ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public RouteTerrainAnalysisDto getMissionRoute(Long assignmentId) {
        RescueAssignment assignment = rescueAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhiệm vụ #" + assignmentId));
        return terrainAnalysisService.analyzeRouteAndTerrain(assignment.getTeam(), assignment.getSosRequest(), assignmentId);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSosMapPins(String statusFilter) {
        return getSosMapPins(statusFilter, "ALL");
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSosMapPins(String statusFilter, String provinceCode) {
        List<SosRequest> list = getSosRequests(statusFilter, provinceCode);
        List<Map<String, Object>> pins = new ArrayList<>();
        for (SosRequest sos : list) {
            Map<String, Object> pin = new HashMap<>();
            pin.put("id", sos.getId());
            pin.put("contactName", sos.getContactName());
            pin.put("contactPhone", sos.getContactPhone());
            pin.put("peopleCount", sos.getPeopleCount());
            pin.put("urgencyLevel", sos.getUrgencyLevel());
            pin.put("status", sos.getStatus());
            pin.put("addressDetail", sos.getAddressDetail());
            pin.put("latitude", sos.getLatitude());
            pin.put("longitude", sos.getLongitude());
            pins.add(pin);
        }
        return pins;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRescueTeamMapPins() {
        return getRescueTeamMapPins("ALL");
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRescueTeamMapPins(String provinceCode) {
        List<RescueTeam> teams = getRescueTeams(provinceCode);
        List<Map<String, Object>> pins = new ArrayList<>();
        for (RescueTeam team : teams) {
            Map<String, Object> pin = new HashMap<>();
            pin.put("id", team.getId());
            pin.put("teamName", team.getTeamName());
            pin.put("status", team.getStatus());
            pin.put("contactPhone", team.getContactPhone());
            pin.put("memberCount", team.getMemberCount());
            pin.put("equipmentNotes", team.getEquipmentNotes());
            pin.put("currentLatitude", team.getCurrentLatitude());
            pin.put("currentLongitude", team.getCurrentLongitude());
            pins.add(pin);
        }
        return pins;
    }
}