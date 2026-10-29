package vn.edu.cuunan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.cuunan.dto.RouteTerrainAnalysisDto;
import vn.edu.cuunan.dto.StartMissionRequestDto;
import vn.edu.cuunan.dto.EquipmentReturnDto;
import vn.edu.cuunan.entity.RescueAssignment;
import vn.edu.cuunan.entity.RescueTeam;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.entity.TeamMember;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.util.GeoUtils;
import vn.edu.cuunan.repository.RescueAssignmentRepository;
import vn.edu.cuunan.repository.RescueTeamRepository;
import vn.edu.cuunan.repository.SosRequestRepository;
import vn.edu.cuunan.repository.TeamMemberRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RescuerService {

    private static final Logger log = LoggerFactory.getLogger(RescuerService.class);

    private final RescueAssignmentRepository rescueAssignmentRepository;
    private final RescueTeamRepository rescueTeamRepository;
    private final SosRequestRepository sosRequestRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final CloudinaryService cloudinaryService;
    private final TerrainAnalysisService terrainAnalysisService;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;
    @Autowired(required = false)
    private EquipmentService equipmentService;

    public RescuerService(RescueAssignmentRepository rescueAssignmentRepository,
                          RescueTeamRepository rescueTeamRepository,
                          SosRequestRepository sosRequestRepository,
                          TeamMemberRepository teamMemberRepository,
                          CloudinaryService cloudinaryService,
                          SimpMessagingTemplate messagingTemplate) {
        this(rescueAssignmentRepository, rescueTeamRepository, sosRequestRepository,
             teamMemberRepository, cloudinaryService, new TerrainAnalysisService(), messagingTemplate, null, null);
    }

    public RescuerService(RescueAssignmentRepository rescueAssignmentRepository,
                          RescueTeamRepository rescueTeamRepository,
                          SosRequestRepository sosRequestRepository,
                          TeamMemberRepository teamMemberRepository,
                          CloudinaryService cloudinaryService,
                          TerrainAnalysisService terrainAnalysisService,
                          SimpMessagingTemplate messagingTemplate) {
        this(rescueAssignmentRepository, rescueTeamRepository, sosRequestRepository,
             teamMemberRepository, cloudinaryService, terrainAnalysisService, messagingTemplate, null, null);
    }

    @Autowired
    public RescuerService(RescueAssignmentRepository rescueAssignmentRepository,
                          RescueTeamRepository rescueTeamRepository,
                          SosRequestRepository sosRequestRepository,
                          TeamMemberRepository teamMemberRepository,
                          CloudinaryService cloudinaryService,
                          TerrainAnalysisService terrainAnalysisService,
                          @Autowired(required = false) SimpMessagingTemplate messagingTemplate,
                          @Autowired(required = false) NotificationService notificationService,
                          @Autowired(required = false) ActivityLogService activityLogService) {
        this.rescueAssignmentRepository = rescueAssignmentRepository;
        this.rescueTeamRepository = rescueTeamRepository;
        this.sosRequestRepository = sosRequestRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.cloudinaryService = cloudinaryService;
        this.terrainAnalysisService = terrainAnalysisService;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
        this.activityLogService = activityLogService;
    }

    /**
     * Xác định đội cứu hộ mà tài khoản người dùng đang tham gia (là Đội trưởng hoặc Thành viên).
     */
    @Transactional(readOnly = true)
    public Optional<RescueTeam> getMyTeam(User user) {
        if (user == null || user.getId() == null) {
            return Optional.empty();
        }

        // 1. Kiểm tra có phải Đội trưởng không
        Optional<RescueTeam> leaderTeam = rescueTeamRepository.findByLeaderId(user.getId());
        if (leaderTeam.isPresent()) {
            return leaderTeam;
        }

        // 2. Kiểm tra có phải thành viên đội không
        List<TeamMember> memberships = teamMemberRepository.findByUserId(user.getId());
        if (!memberships.isEmpty() && memberships.get(0).getTeam() != null) {
            return Optional.of(memberships.get(0).getTeam());
        }

        // 3. Fallback cho Admin hoặc tài khoản cứu hộ chưa kịp liên kết:
        if (user.getRole() != null && ("ROLE_ADMIN".equalsIgnoreCase(user.getRole().getName())
                || "ROLE_RESCUER".equalsIgnoreCase(user.getRole().getName()))) {
            List<RescueTeam> allTeams = rescueTeamRepository.findAll();
            if (!allTeams.isEmpty()) {
                return Optional.of(allTeams.get(0));
            }
        }

        return Optional.empty();
    }

    /**
     * Lấy toàn bộ danh sách nhiệm vụ được phân công cho đội của người dùng.
     */
    @Transactional(readOnly = true)
    public List<RescueAssignment> getMyAssignments(User user) {
        Optional<RescueTeam> teamOpt = getMyTeam(user);
        if (teamOpt.isEmpty()) {
            return Collections.emptyList();
        }
        return rescueAssignmentRepository.findByTeamIdWithSosRequest(teamOpt.get().getId());
    }

    /**
     * Chấp nhận nhiệm vụ cứu nạn (từ PENDING -> ACCEPTED).
     */
    @Transactional
    public RescueAssignment acceptAssignment(Long assignmentId, User user) {
        RescueAssignment assignment = findAndValidateAssignmentAccess(assignmentId, user);

        if ("ACCEPTED".equalsIgnoreCase(assignment.getStatus())) {
            log.info("Nhiệm vụ #{} đã ở trạng thái ACCEPTED", assignmentId);
            return assignment;
        }

        if (!"PENDING".equalsIgnoreCase(assignment.getStatus())) {
            throw new IllegalStateException("Nhiệm vụ này đã được xử lý (trạng thái: " + assignment.getStatus() + ")");
        }

        assignment.setStatus("ACCEPTED");
        assignment.setAcceptedAt(LocalDateTime.now());

        // Ca SOS chuyển sang trạng thái IN_PROGRESS
        SosRequest sos = assignment.getSosRequest();
        sos.setStatus("IN_PROGRESS");
        sosRequestRepository.save(sos);

        // Đội chuyển sang trạng thái BUSY
        RescueTeam team = assignment.getTeam();
        team.setStatus("BUSY");
        rescueTeamRepository.save(team);

        RescueAssignment saved = rescueAssignmentRepository.save(assignment);
        log.info(">>> ĐỘI '{}' ĐÃ CHẤP NHẬN NHIỆM VỤ #{} (Ca SOS #{})",
                team.getTeamName(), assignmentId, sos.getId());

        // Broadcast WebSocket mở kênh Chat nổi tức thì cho Người dân và Đội cứu hộ (Facebook-style floating chat)
        sendMapUpdateNotification(sos.getId(), "IN_PROGRESS", team.getTeamName());
        if (messagingTemplate != null) {
            try {
                Map<String, Object> chatInitEvent = new HashMap<>();
                chatInitEvent.put("type", "CHAT_ACTIVATED");
                chatInitEvent.put("sosId", sos.getId());
                chatInitEvent.put("assignmentId", assignmentId);
                chatInitEvent.put("teamName", team.getTeamName());
                chatInitEvent.put("status", "IN_PROGRESS");
                messagingTemplate.convertAndSend("/topic/map-updates", chatInitEvent);
                messagingTemplate.convertAndSend("/topic/chat/" + sos.getId() + "/status", chatInitEvent);
            } catch (Exception ex) {
                log.warn("Lỗi gửi thông báo WebSocket CHAT_ACTIVATED: {}", ex.getMessage());
            }
        }

        // Thông báo cho Điều phối viên
        if (notificationService != null) {
            if (assignment.getAssignedBy() != null) {
                notificationService.sendToUser(assignment.getAssignedBy(),
                        "Đội đã chấp nhận nhiệm vụ #" + assignmentId,
                        "Đội '" + team.getTeamName() + "' đã chấp nhận ca cứu nạn #" + sos.getId() + " (" + sos.getContactName() + ").",
                        "MISSION_STATUS",
                        "/coordinator/dashboard");
            } else {
                notificationService.sendToRole("ROLE_COORDINATOR",
                        "Đội đã chấp nhận nhiệm vụ #" + assignmentId,
                        "Đội '" + team.getTeamName() + "' đã chấp nhận ca cứu nạn #" + sos.getId() + " (" + sos.getContactName() + ").",
                        "MISSION_STATUS",
                        "/coordinator/dashboard");
            }
        }

        // Ghi nhật ký kiểm toán
        if (activityLogService != null) {
            activityLogService.log(user, "ACCEPT_MISSION", "Nhiệm vụ #" + assignmentId,
                    "Đội " + team.getTeamName() + " chấp nhận ca cứu nạn #" + sos.getId(), null);
        }

        sendMapUpdateNotification(sos.getId(), "IN_PROGRESS", team.getTeamName());
        return saved;
    }

    /**
     * Từ chối nhiệm vụ cứu nạn (chuyển sang REJECTED, trả ca SOS về PENDING để Coordinator gán lại).
     */
    @Transactional
    public RescueAssignment rejectAssignment(Long assignmentId, User user, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng cung cấp lý do từ chối nhiệm vụ!");
        }

        RescueAssignment assignment = findAndValidateAssignmentAccess(assignmentId, user);

        if (!"PENDING".equalsIgnoreCase(assignment.getStatus())) {
            throw new IllegalStateException("Chỉ có thể từ chối nhiệm vụ khi đang ở trạng thái chờ xác nhận!");
        }

        assignment.setStatus("REJECTED");
        assignment.setRejectionReason(reason.trim());

        // Trả ca SOS về PENDING để Coordinator tiếp tục điều phối đội khác
        SosRequest sos = assignment.getSosRequest();
        sos.setStatus("PENDING");
        sosRequestRepository.save(sos);

        RescueAssignment saved = rescueAssignmentRepository.save(assignment);

        RescueTeam team = assignment.getTeam();
        if (team != null) {
            long remaining = rescueAssignmentRepository.countActiveAssignmentsByTeamId(team.getId());
            team.setStatus(remaining == 0 ? "AVAILABLE" : "BUSY");
            rescueTeamRepository.save(team);
        }
        log.info(">>> ĐỘI '{}' TỪ CHỐI NHIỆM VỤ #{}: Lý do '{}'",
                assignment.getTeam().getTeamName(), assignmentId, reason);

        // Thông báo cho Điều phối viên
        if (notificationService != null) {
            if (assignment.getAssignedBy() != null) {
                notificationService.sendToUser(assignment.getAssignedBy(),
                        "Đội từ chối nhiệm vụ #" + assignmentId,
                        "Đội '" + team.getTeamName() + "' đã từ chối ca #" + sos.getId() + ". Lý do: " + reason.trim(),
                        "MISSION_STATUS",
                        "/coordinator/dashboard");
            } else {
                notificationService.sendToRole("ROLE_COORDINATOR",
                        "Đội từ chối nhiệm vụ #" + assignmentId,
                        "Đội '" + team.getTeamName() + "' đã từ chối ca #" + sos.getId() + ". Lý do: " + reason.trim(),
                        "MISSION_STATUS",
                        "/coordinator/dashboard");
            }
        }

        // Ghi nhật ký kiểm toán
        if (activityLogService != null) {
            activityLogService.log(user, "REJECT_MISSION", "Nhiệm vụ #" + assignmentId,
                    "Đội " + team.getTeamName() + " từ chối ca #" + sos.getId() + ". Lý do: " + reason.trim(), null);
        }

        sendMapUpdateNotification(sos.getId(), "PENDING", null);
        return saved;
    }

    /**
     * Bắt đầu di chuyển / tiếp cận hiện trường.
     */
    @Transactional(readOnly = true)
    public RouteTerrainAnalysisDto getRouteAndTerrainAnalysis(Long assignmentId, User user) {
        RescueAssignment assignment = findAndValidateAssignmentAccess(assignmentId, user);
        return terrainAnalysisService.analyzeRouteAndTerrain(assignment.getTeam(), assignment.getSosRequest(), assignmentId);
    }

    @Transactional(readOnly = true)
    public RescueTeam getAssignmentTeam(Long assignmentId, User user) {
        return findAndValidateAssignmentAccess(assignmentId, user).getTeam();
    }

    @Transactional(readOnly = true)
    public List<vn.edu.cuunan.entity.EquipmentAssignment> getMissionEquipment(Long assignmentId, User user) {
        findAndValidateAssignmentAccess(assignmentId, user);
        return equipmentService == null ? Collections.emptyList() : equipmentService.getDispatchedEquipmentForMission(assignmentId).stream()
                .filter(equipment -> "DISPATCHED".equalsIgnoreCase(equipment.getStatus())).toList();
    }

    @Transactional
    public RescueAssignment startMoving(Long assignmentId, User user) {
        return startMoving(assignmentId, user, null);
    }

    @Transactional
    public RescueAssignment startMoving(Long assignmentId, User user, StartMissionRequestDto dto) {
        RescueAssignment assignment = findAndValidateAssignmentAccess(assignmentId, user);

        if (!"ACCEPTED".equalsIgnoreCase(assignment.getStatus())
                && !"IN_PROGRESS".equalsIgnoreCase(assignment.getStatus())) {
            throw new IllegalStateException("Chỉ có thể bắt đầu di chuyển sau khi đội đã chấp nhận nhiệm vụ!");
        }

        if (dto != null && ((dto.getVehicleEquipmentId() != null)
                || (dto.getEquipmentItems() != null && !dto.getEquipmentItems().isEmpty()))) {
            if (equipmentService == null) throw new IllegalStateException("Không thể kết nối kho trang bị lúc này.");
            List<vn.edu.cuunan.entity.EquipmentAssignment> issued = equipmentService.dispatchMissionSelection(assignmentId, dto, user);
            String issuedNames = issued.stream().filter(ea -> !ea.getEquipment().getEquipmentCode().startsWith("VEHICLE_"))
                    .map(ea -> ea.getEquipment().getEquipmentName()
                    + " (" + ea.getQuantityDispatched() + " " + ea.getEquipment().getUnit() + ")")
                    .collect(java.util.stream.Collectors.joining(", "));
            assignment.setSelectedEquipment(issuedNames.isBlank() ? null : issuedNames);
        }

        assignment.setStatus("IN_PROGRESS");
        if (dto != null) {
            if (dto.getVehicleType() != null && !dto.getVehicleType().isBlank()) {
                assignment.setVehicleType(dto.getVehicleType().trim());
            }
            if ((dto.getEquipmentItems() == null || dto.getEquipmentItems().isEmpty())
                    && dto.getSelectedEquipment() != null && !dto.getSelectedEquipment().isEmpty()) {
                assignment.setSelectedEquipment(String.join(", ", dto.getSelectedEquipment()));
            }
            if (dto.getCustomNotes() != null && !dto.getCustomNotes().isBlank()) {
                String existing = assignment.getReportNotes() != null ? assignment.getReportNotes() + " | " : "";
                assignment.setReportNotes(existing + "Ghi chú xuất kích: " + dto.getCustomNotes().trim());
            }
            if (dto.getRouteDistanceKm() != null) {
                assignment.setRouteDistanceKm(dto.getRouteDistanceKm());
            }
            if (dto.getRouteDurationMinutes() != null) {
                assignment.setRouteDurationMinutes(dto.getRouteDurationMinutes());
            }
            if (dto.getTerrainType() != null) {
                assignment.setTerrainType(dto.getTerrainType());
            }
        }

        SosRequest sos = assignment.getSosRequest();
        sos.setStatus("IN_PROGRESS");
        sosRequestRepository.save(sos);

        RescueAssignment saved = rescueAssignmentRepository.save(assignment);
        log.info(">>> ĐỘI '{}' ĐANG TIẾP CẬN HIỆN TRƯỜNG NHIỆM VỤ #{} (Phương tiện: {}, Trang bị: {})",
                assignment.getTeam().getTeamName(), assignmentId, assignment.getVehicleType(), assignment.getSelectedEquipment());

        // Thông báo cho Điều phối viên
        if (notificationService != null) {
            String noticeMsg = "Đội '" + assignment.getTeam().getTeamName() + "' đã xuất kích tiếp cận ca SOS #"
                    + sos.getId() + " (" + sos.getContactName() + "). Cự ly: "
                    + (assignment.getRouteDistanceKm() != null ? assignment.getRouteDistanceKm() + " km" : "đang tính toán") + ".";
            if (assignment.getAssignedBy() != null) {
                notificationService.sendToUser(assignment.getAssignedBy(),
                        "Đội xuất kích nhiệm vụ #" + assignmentId, noticeMsg, "MISSION_STATUS", "/coordinator/dashboard");
            } else {
                notificationService.sendToRole("ROLE_COORDINATOR",
                        "Đội xuất kích nhiệm vụ #" + assignmentId, noticeMsg, "MISSION_STATUS", "/coordinator/dashboard");
            }
        }

        // Ghi nhật ký kiểm toán
        if (activityLogService != null) {
            activityLogService.log(user, "START_MISSION", "Nhiệm vụ #" + assignmentId,
                    "Đội " + assignment.getTeam().getTeamName() + " xuất kích tiếp cận ca #" + sos.getId(), null);
        }

        sendMissionStartNotification(saved);
        return saved;
    }

    private void sendMissionStartNotification(RescueAssignment assignment) {
        if (messagingTemplate == null) return;
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "MISSION_START");
            payload.put("sosId", assignment.getSosRequest().getId());
            payload.put("teamId", assignment.getTeam().getId());
            payload.put("teamName", assignment.getTeam().getTeamName());
            payload.put("status", "IN_PROGRESS");
            payload.put("vehicleType", assignment.getVehicleType());
            payload.put("selectedEquipment", assignment.getSelectedEquipment());
            payload.put("distanceKm", assignment.getRouteDistanceKm());
            payload.put("durationMinutes", assignment.getRouteDurationMinutes());
            payload.put("terrainType", assignment.getTerrainType());
            messagingTemplate.convertAndSend("/topic/map-updates", payload);
        } catch (Exception ex) {
            log.warn("Lỗi gửi thông báo WebSocket xuất kích: {}", ex.getMessage());
        }
    }

    /**
     * Báo cáo hoàn thành nhiệm vụ và nghiệm thu hiện trường cứu hộ (tải ảnh bằng chứng).
     */
    @Transactional
    public RescueAssignment completeAssignment(Long assignmentId, User user, String reportNotes, MultipartFile reportImage) {
        return completeAssignment(assignmentId, user, reportNotes, reportImage, null);
    }

    @Transactional
    public RescueAssignment completeAssignment(Long assignmentId, User user, String reportNotes, MultipartFile reportImage,
                                               EquipmentReturnDto equipmentReturns) {
        RescueAssignment assignment = findAndValidateAssignmentAccess(assignmentId, user);

        if (!"IN_PROGRESS".equalsIgnoreCase(assignment.getStatus())
                && !"ACCEPTED".equalsIgnoreCase(assignment.getStatus())) {
            throw new IllegalStateException("Chỉ có thể hoàn tất nhiệm vụ đang thực hiện (ACCEPTED hoặc IN_PROGRESS)!");
        }

        if (equipmentService != null) {
            if (equipmentReturns == null) equipmentService.returnAllIntact(assignmentId, user);
            else equipmentService.returnEquipment(assignmentId, equipmentReturns, user);
        }

        // 1. Tải ảnh nghiệm thu lên Cloudinary / Fallback local (nếu có đính kèm ảnh)
        if (reportImage != null && !reportImage.isEmpty()) {
            String uploadedUrl = cloudinaryService.uploadImage(reportImage, "rescue_reports");
            assignment.setReportImageUrl(uploadedUrl);
        }

        assignment.setStatus("COMPLETED");
        assignment.setCompletedAt(LocalDateTime.now());
        if (reportNotes != null && !reportNotes.trim().isEmpty()) {
            assignment.setReportNotes(reportNotes.trim());
        }

        // 2. Chuyển trạng thái ca SOS thành RESOLVED
        SosRequest sos = assignment.getSosRequest();
        sos.setStatus("RESOLVED");
        sos.setResolvedAt(LocalDateTime.now());
        sosRequestRepository.save(sos);

        // 3. Đưa đội cứu hộ trở về trạng thái AVAILABLE
        RescueAssignment saved = rescueAssignmentRepository.save(assignment);

        RescueTeam team = assignment.getTeam();
        if (team != null) {
            long remaining = rescueAssignmentRepository.countActiveAssignmentsByTeamId(team.getId());
            team.setStatus(remaining == 0 ? "AVAILABLE" : "BUSY");
            rescueTeamRepository.save(team);
        }
        log.info(">>> HOÀN TẤT CỨU NẠN: Ca SOS #{} đã được đội '{}' giải cứu an toàn!",
                sos.getId(), team.getTeamName());

        // Thông báo cho Điều phối viên
        if (notificationService != null) {
            String noticeMsg = "Đội '" + team.getTeamName() + "' đã hoàn thành xuất sắc ca cứu nạn #"
                    + sos.getId() + " (" + sos.getContactName() + ").";
            if (assignment.getAssignedBy() != null) {
                notificationService.sendToUser(assignment.getAssignedBy(),
                        "Nhiệm vụ #" + assignmentId + " đã hoàn thành", noticeMsg, "MISSION_STATUS", "/coordinator/dashboard");
            }
            notificationService.sendToRole("ROLE_COORDINATOR",
                    "Ca SOS #" + sos.getId() + " đã hoàn tất", noticeMsg, "MISSION_STATUS", "/coordinator/dashboard");

            // Nếu người gửi SOS có tài khoản trong hệ thống -> thông báo cho người gửi
            if (sos.getSender() != null) {
                notificationService.sendToUser(sos.getSender(),
                        "Yêu cầu cứu nạn #" + sos.getId() + " đã được xử lý an toàn",
                        "Đội cứu hộ " + team.getTeamName() + " đã hoàn tất công tác giải cứu và hỗ trợ bạn.",
                        "SOS_STATUS",
                        "/sos/track/" + sos.getId());
            }
        }

        // Ghi nhật ký kiểm toán
        if (activityLogService != null) {
            activityLogService.log(user, "COMPLETE_MISSION", "Nhiệm vụ #" + assignmentId,
                    "Đội " + team.getTeamName() + " hoàn thành cứu nạn ca #" + sos.getId(), null);
        }

        sendMapUpdateNotification(sos.getId(), "RESOLVED", team.getTeamName());
        return saved;
    }

    /**
     * Kiểm tra và xác thực quyền thao tác trên nhiệm vụ.
     */
    private RescueAssignment findAndValidateAssignmentAccess(Long assignmentId, User user) {
        RescueAssignment assignment = rescueAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhiệm vụ #" + assignmentId));

        if (user == null || user.getId() == null) {
            throw new IllegalStateException("Người dùng chưa được xác thực danh tính!");
        }

        // Nếu là ADMIN thì có toàn quyền điều hành
        if (user.getRole() != null && "ROLE_ADMIN".equalsIgnoreCase(user.getRole().getName())) {
            return assignment;
        }

        RescueTeam team = assignment.getTeam();
        boolean isLeader = (team.getLeader() != null && team.getLeader().getId().equals(user.getId()));
        if (isLeader) {
            return assignment;
        }

        // Kiểm tra thành viên đội
        boolean isMember = teamMemberRepository.findByUserId(user.getId()).stream()
                .anyMatch(tm -> tm.getTeam() != null && tm.getTeam().getId().equals(team.getId()));

        if (!isMember) {
            throw new IllegalStateException("Bạn không thuộc Đội cứu hộ '" + team.getTeamName() + "' để thao tác nhiệm vụ này!");
        }

        return assignment;
    }

    private void sendMapUpdateNotification(Long sosId, String newStatus, String teamName) {
        if (messagingTemplate == null) return;
        try {
            Map<String, Object> update = new HashMap<>();
            update.put("type", "STATUS_CHANGE");
            update.put("sosId", sosId);
            update.put("status", newStatus);
            if (teamName != null) {
                update.put("teamName", teamName);
            }
            messagingTemplate.convertAndSend("/topic/map-updates", update);
        } catch (Exception ex) {
            log.warn("Lỗi gửi thông báo WebSocket cập nhật bản đồ: {}", ex.getMessage());
        }
    }

    /**
     * Cập nhật vị trí GPS thời gian thực của đội cứu hộ và phát WebSocket đến nạn nhân.
     */
    @Transactional
    public Map<String, Object> updateTeamLocation(Long assignmentId, User user, Double lat, Double lng) {
        RescueAssignment assignment = findAndValidateAssignmentAccess(assignmentId, user);
        RescueTeam team = assignment.getTeam();
        SosRequest sos = assignment.getSosRequest();

        team.setCurrentLatitude(lat);
        team.setCurrentLongitude(lng);
        rescueTeamRepository.save(team);

        double distanceKm = GeoUtils.calculateDistance(lat, lng, sos.getLatitude(), sos.getLongitude());
        distanceKm = Math.round(distanceKm * 100.0) / 100.0;

        Map<String, Object> locationUpdate = new HashMap<>();
        locationUpdate.put("type", "LOCATION_UPDATE");
        locationUpdate.put("sosId", sos.getId());
        locationUpdate.put("teamId", team.getId());
        locationUpdate.put("teamName", team.getTeamName());
        locationUpdate.put("latitude", lat);
        locationUpdate.put("longitude", lng);
        locationUpdate.put("distanceKm", distanceKm);

        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/map-updates", locationUpdate);
        }

        Map<String, Object> response = new HashMap<>(locationUpdate);
        response.put("success", true);
        return response;
    }
}
