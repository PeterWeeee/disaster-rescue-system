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
     * XĂ¡c Ä‘á»‹nh Ä‘á»™i cá»©u há»™ mĂ  tĂ i khoáº£n ngÆ°á»i dĂ¹ng Ä‘ang tham gia (lĂ  Äá»™i trÆ°á»Ÿng hoáº·c ThĂ nh viĂªn).
     */
    @Transactional(readOnly = true)
    public Optional<RescueTeam> getMyTeam(User user) {
        if (user == null || user.getId() == null) {
            return Optional.empty();
        }

        // 1. Kiá»ƒm tra cĂ³ pháº£i Äá»™i trÆ°á»Ÿng khĂ´ng
        Optional<RescueTeam> leaderTeam = rescueTeamRepository.findByLeaderId(user.getId());
        if (leaderTeam.isPresent()) {
            return leaderTeam;
        }

        // 2. Kiá»ƒm tra cĂ³ pháº£i thĂ nh viĂªn Ä‘á»™i khĂ´ng
        List<TeamMember> memberships = teamMemberRepository.findByUserId(user.getId());
        if (!memberships.isEmpty() && memberships.get(0).getTeam() != null) {
            return Optional.of(memberships.get(0).getTeam());
        }

        // 3. Fallback cho Admin hoáº·c tĂ i khoáº£n cá»©u há»™ chÆ°a ká»‹p liĂªn káº¿t:
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
     * Láº¥y toĂ n bá»™ danh sĂ¡ch nhiá»‡m vá»¥ Ä‘Æ°á»£c phĂ¢n cĂ´ng cho Ä‘á»™i cá»§a ngÆ°á»i dĂ¹ng.
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
     * Cháº¥p nháº­n nhiá»‡m vá»¥ cá»©u náº¡n (tá»« PENDING -> ACCEPTED).
     */
    @Transactional
    public RescueAssignment acceptAssignment(Long assignmentId, User user) {
        RescueAssignment assignment = findAndValidateAssignmentAccess(assignmentId, user);

        if ("ACCEPTED".equalsIgnoreCase(assignment.getStatus())) {
            log.info("Nhiá»‡m vá»¥ #{} Ä‘Ă£ á»Ÿ tráº¡ng thĂ¡i ACCEPTED", assignmentId);
            return assignment;
        }

        if (!"PENDING".equalsIgnoreCase(assignment.getStatus())) {
            throw new IllegalStateException("Nhiá»‡m vá»¥ nĂ y Ä‘Ă£ Ä‘Æ°á»£c xá»­ lĂ½ (tráº¡ng thĂ¡i: " + assignment.getStatus() + ")");
        }

        assignment.setStatus("ACCEPTED");
        assignment.setAcceptedAt(LocalDateTime.now());

        // Ca SOS chuyá»ƒn sang tráº¡ng thĂ¡i IN_PROGRESS
        SosRequest sos = assignment.getSosRequest();
        sos.setStatus("IN_PROGRESS");
        sosRequestRepository.save(sos);

        // Äá»™i chuyá»ƒn sang tráº¡ng thĂ¡i BUSY
        RescueTeam team = assignment.getTeam();
        team.setStatus("BUSY");
        rescueTeamRepository.save(team);

        RescueAssignment saved = rescueAssignmentRepository.save(assignment);
        log.info(">>> Äá»˜I '{}' ÄĂƒ CHáº¤P NHáº¬N NHIá»†M Vá»¤ #{} (Ca SOS #{})",
                team.getTeamName(), assignmentId, sos.getId());

        // ThĂ´ng bĂ¡o cho Äiá»u phá»‘i viĂªn
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

        // Ghi nháº­t kĂ½ kiá»ƒm toĂ¡n
        if (activityLogService != null) {
            activityLogService.log(user, "ACCEPT_MISSION", "Nhiá»‡m vá»¥ #" + assignmentId,
                    "Äá»™i " + team.getTeamName() + " cháº¥p nháº­n ca cá»©u náº¡n #" + sos.getId(), null);
        }

        sendMapUpdateNotification(sos.getId(), "IN_PROGRESS", team.getTeamName());
        return saved;
    }

    /**
     * Tá»« chá»‘i nhiá»‡m vá»¥ cá»©u náº¡n (chuyá»ƒn sang REJECTED, tráº£ ca SOS vá» PENDING Ä‘á»ƒ Coordinator gĂ¡n láº¡i).
     */
    @Transactional
    public RescueAssignment rejectAssignment(Long assignmentId, User user, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lĂ²ng cung cáº¥p lĂ½ do tá»« chá»‘i nhiá»‡m vá»¥!");
        }

        RescueAssignment assignment = findAndValidateAssignmentAccess(assignmentId, user);

        if (!"PENDING".equalsIgnoreCase(assignment.getStatus())) {
            throw new IllegalStateException("Chá»‰ cĂ³ thá»ƒ tá»« chá»‘i nhiá»‡m vá»¥ khi Ä‘ang á»Ÿ tráº¡ng thĂ¡i chá» xĂ¡c nháº­n!");
        }

        assignment.setStatus("REJECTED");
        assignment.setRejectionReason(reason.trim());

        // Tráº£ ca SOS vá» PENDING Ä‘á»ƒ Coordinator tiáº¿p tá»¥c Ä‘iá»u phá»‘i Ä‘á»™i khĂ¡c
        SosRequest sos = assignment.getSosRequest();
        sos.setStatus("PENDING");
        sosRequestRepository.save(sos);

        RescueTeam team = assignment.getTeam();
        team.setStatus("AVAILABLE");
        rescueTeamRepository.save(team);

        RescueAssignment saved = rescueAssignmentRepository.save(assignment);
        log.info(">>> Äá»˜I '{}' Tá»ª CHá»I NHIá»†M Vá»¤ #{}: LĂ½ do '{}'",
                assignment.getTeam().getTeamName(), assignmentId, reason);

        // ThĂ´ng bĂ¡o cho Äiá»u phá»‘i viĂªn
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

        // Ghi nháº­t kĂ½ kiá»ƒm toĂ¡n
        if (activityLogService != null) {
            activityLogService.log(user, "REJECT_MISSION", "Nhiá»‡m vá»¥ #" + assignmentId,
                    "Äá»™i " + team.getTeamName() + " tá»« chá»‘i ca #" + sos.getId() + ". LĂ½ do: " + reason.trim(), null);
        }

        sendMapUpdateNotification(sos.getId(), "PENDING", null);
        return saved;
    }

    /**
     * Báº¯t Ä‘áº§u di chuyá»ƒn / tiáº¿p cáº­n hiá»‡n trÆ°á»ng.
     */
    @Transactional(readOnly = true)
    public RouteTerrainAnalysisDto getRouteAndTerrainAnalysis(Long assignmentId, User user) {
        RescueAssignment assignment = findAndValidateAssignmentAccess(assignmentId, user);
        return terrainAnalysisService.analyzeRouteAndTerrain(assignment.getTeam(), assignment.getSosRequest(), assignmentId);
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
            throw new IllegalStateException("Chá»‰ cĂ³ thá»ƒ báº¯t Ä‘áº§u di chuyá»ƒn sau khi Ä‘á»™i Ä‘Ă£ cháº¥p nháº­n nhiá»‡m vá»¥!");
        }

        assignment.setStatus("IN_PROGRESS");
        if (dto != null) {
            if (dto.getVehicleType() != null && !dto.getVehicleType().isBlank()) {
                assignment.setVehicleType(dto.getVehicleType().trim());
            }
            if (dto.getSelectedEquipment() != null && !dto.getSelectedEquipment().isEmpty()) {
                assignment.setSelectedEquipment(String.join(", ", dto.getSelectedEquipment()));
            }
            if (dto.getCustomNotes() != null && !dto.getCustomNotes().isBlank()) {
                String existing = assignment.getReportNotes() != null ? assignment.getReportNotes() + " | " : "";
                assignment.setReportNotes(existing + "Ghi chĂº xuáº¥t kĂ­ch: " + dto.getCustomNotes().trim());
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
        log.info(">>> Äá»˜I '{}' ÄANG TIáº¾P Cáº¬N HIá»†N TRÆ¯á»œNG NHIá»†M Vá»¤ #{} (PhÆ°Æ¡ng tiá»‡n: {}, Trang bá»‹: {})",
                assignment.getTeam().getTeamName(), assignmentId, assignment.getVehicleType(), assignment.getSelectedEquipment());

        // ThĂ´ng bĂ¡o cho Äiá»u phá»‘i viĂªn
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

        // Ghi nháº­t kĂ½ kiá»ƒm toĂ¡n
        if (activityLogService != null) {
            activityLogService.log(user, "START_MISSION", "Nhiá»‡m vá»¥ #" + assignmentId,
                    "Äá»™i " + assignment.getTeam().getTeamName() + " xuáº¥t kĂ­ch tiáº¿p cáº­n ca #" + sos.getId(), null);
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
            log.warn("Lá»—i gá»­i thĂ´ng bĂ¡o WebSocket xuáº¥t kĂ­ch: {}", ex.getMessage());
        }
    }

    /**
     * BĂ¡o cĂ¡o hoĂ n thĂ nh nhiá»‡m vá»¥ vĂ  nghiá»‡m thu hiá»‡n trÆ°á»ng cá»©u há»™ (táº£i áº£nh báº±ng chá»©ng).
     */
    @Transactional
    public RescueAssignment completeAssignment(Long assignmentId, User user, String reportNotes, MultipartFile reportImage) {
        RescueAssignment assignment = findAndValidateAssignmentAccess(assignmentId, user);

        if (!"IN_PROGRESS".equalsIgnoreCase(assignment.getStatus())
                && !"ACCEPTED".equalsIgnoreCase(assignment.getStatus())) {
            throw new IllegalStateException("Chá»‰ cĂ³ thá»ƒ hoĂ n táº¥t nhiá»‡m vá»¥ Ä‘ang thá»±c hiá»‡n (ACCEPTED hoáº·c IN_PROGRESS)!");
        }

        // 1. Táº£i áº£nh nghiá»‡m thu lĂªn Cloudinary / Fallback local (náº¿u cĂ³ Ä‘Ă­nh kĂ¨m áº£nh)
        if (reportImage != null && !reportImage.isEmpty()) {
            String uploadedUrl = cloudinaryService.uploadImage(reportImage, "rescue_reports");
            assignment.setReportImageUrl(uploadedUrl);
        }

        assignment.setStatus("COMPLETED");
        assignment.setCompletedAt(LocalDateTime.now());
        if (reportNotes != null && !reportNotes.trim().isEmpty()) {
            assignment.setReportNotes(reportNotes.trim());
        }

        // 2. Chuyá»ƒn tráº¡ng thĂ¡i ca SOS thĂ nh RESOLVED
        SosRequest sos = assignment.getSosRequest();
        sos.setStatus("RESOLVED");
        sos.setResolvedAt(LocalDateTime.now());
        sosRequestRepository.save(sos);

        // 3. ÄÆ°a Ä‘á»™i cá»©u há»™ trá»Ÿ vá» tráº¡ng thĂ¡i AVAILABLE
        RescueTeam team = assignment.getTeam();
        team.setStatus("AVAILABLE");
        rescueTeamRepository.save(team);

        RescueAssignment saved = rescueAssignmentRepository.save(assignment);
        log.info(">>> HOĂ€N Táº¤T Cá»¨U Náº N: Ca SOS #{} Ä‘Ă£ Ä‘Æ°á»£c Ä‘á»™i '{}' giáº£i cá»©u an toĂ n!",
                sos.getId(), team.getTeamName());

        // ThĂ´ng bĂ¡o cho Äiá»u phá»‘i viĂªn
        if (notificationService != null) {
            String noticeMsg = "Đội '" + team.getTeamName() + "' đã hoàn thành xuất sắc ca cứu nạn #"
                    + sos.getId() + " (" + sos.getContactName() + ").";
            if (assignment.getAssignedBy() != null) {
                notificationService.sendToUser(assignment.getAssignedBy(),
                        "Nhiệm vụ #" + assignmentId + " đã hoàn thành", noticeMsg, "MISSION_STATUS", "/coordinator/dashboard");
            }
            notificationService.sendToRole("ROLE_COORDINATOR",
                    "Ca SOS #" + sos.getId() + " đã hoàn tất", noticeMsg, "MISSION_STATUS", "/coordinator/dashboard");

            // Náº¿u ngÆ°á»i gá»­i SOS cĂ³ tĂ i khoáº£n trong há»‡ thá»‘ng -> thĂ´ng bĂ¡o cho ngÆ°á»i gá»­i
            if (sos.getSender() != null) {
                notificationService.sendToUser(sos.getSender(),
                        "Yêu cầu cứu nạn #" + sos.getId() + " đã được xử lý an toàn",
                        "Đội cứu hộ " + team.getTeamName() + " đã hoàn tất công tác giải cứu và hỗ trợ bạn.",
                        "SOS_STATUS",
                        "/sos/track/" + sos.getId());
            }
        }

        // Ghi nháº­t kĂ½ kiá»ƒm toĂ¡n
        if (activityLogService != null) {
            activityLogService.log(user, "COMPLETE_MISSION", "Nhiá»‡m vá»¥ #" + assignmentId,
                    "Äá»™i " + team.getTeamName() + " hoĂ n thĂ nh cá»©u náº¡n ca #" + sos.getId(), null);
        }

        sendMapUpdateNotification(sos.getId(), "RESOLVED", team.getTeamName());
        return saved;
    }

    /**
     * Kiá»ƒm tra vĂ  xĂ¡c thá»±c quyá»n thao tĂ¡c trĂªn nhiá»‡m vá»¥.
     */
    private RescueAssignment findAndValidateAssignmentAccess(Long assignmentId, User user) {
        RescueAssignment assignment = rescueAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("KhĂ´ng tĂ¬m tháº¥y nhiá»‡m vá»¥ #" + assignmentId));

        if (user == null || user.getId() == null) {
            throw new IllegalStateException("NgÆ°á»i dĂ¹ng chÆ°a Ä‘Æ°á»£c xĂ¡c thá»±c danh tĂ­nh!");
        }

        // Náº¿u lĂ  ADMIN thĂ¬ cĂ³ toĂ n quyá»n Ä‘iá»u hĂ nh
        if (user.getRole() != null && "ROLE_ADMIN".equalsIgnoreCase(user.getRole().getName())) {
            return assignment;
        }

        RescueTeam team = assignment.getTeam();
        boolean isLeader = (team.getLeader() != null && team.getLeader().getId().equals(user.getId()));
        if (isLeader) {
            return assignment;
        }

        // Kiá»ƒm tra thĂ nh viĂªn Ä‘á»™i
        boolean isMember = teamMemberRepository.findByUserId(user.getId()).stream()
                .anyMatch(tm -> tm.getTeam() != null && tm.getTeam().getId().equals(team.getId()));

        if (!isMember) {
            throw new IllegalStateException("Báº¡n khĂ´ng thuá»™c Äá»™i cá»©u há»™ '" + team.getTeamName() + "' Ä‘á»ƒ thao tĂ¡c nhiá»‡m vá»¥ nĂ y!");
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
            log.warn("Lá»—i gá»­i thĂ´ng bĂ¡o WebSocket cáº­p nháº­t báº£n Ä‘á»“: {}", ex.getMessage());
        }
    }

    /**
     * Cáº­p nháº­t vá»‹ trĂ­ GPS thá»i gian thá»±c cá»§a Ä‘á»™i cá»©u há»™ vĂ  phĂ¡t WebSocket Ä‘áº¿n náº¡n nhĂ¢n.
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
