package vn.edu.cuunan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.cuunan.entity.RescueAssignment;
import vn.edu.cuunan.entity.RescueTeam;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.entity.TeamMember;
import vn.edu.cuunan.entity.User;
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
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public RescuerService(RescueAssignmentRepository rescueAssignmentRepository,
                          RescueTeamRepository rescueTeamRepository,
                          SosRequestRepository sosRequestRepository,
                          TeamMemberRepository teamMemberRepository,
                          CloudinaryService cloudinaryService,
                          @Autowired(required = false) SimpMessagingTemplate messagingTemplate) {
        this.rescueAssignmentRepository = rescueAssignmentRepository;
        this.rescueTeamRepository = rescueTeamRepository;
        this.sosRequestRepository = sosRequestRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.cloudinaryService = cloudinaryService;
        this.messagingTemplate = messagingTemplate;
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
        return rescueAssignmentRepository.findByTeamIdOrderByAssignedAtDesc(teamOpt.get().getId());
    }

    /**
     * Chấp nhận nhiệm vụ cứu nạn (từ PENDING -> ACCEPTED).
     */
    @Transactional
    public RescueAssignment acceptAssignment(Long assignmentId, User user) {
        RescueAssignment assignment = findAndValidateAssignmentAccess(assignmentId, user);

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

        RescueTeam team = assignment.getTeam();
        team.setStatus("AVAILABLE");
        rescueTeamRepository.save(team);

        RescueAssignment saved = rescueAssignmentRepository.save(assignment);
        log.info(">>> ĐỘI '{}' TỪ CHỐI NHIỆM VỤ #{}: Lý do '{}'",
                assignment.getTeam().getTeamName(), assignmentId, reason);

        sendMapUpdateNotification(sos.getId(), "PENDING", null);
        return saved;
    }

    /**
     * Bắt đầu di chuyển / tiếp cận hiện trường.
     */
    @Transactional
    public RescueAssignment startMoving(Long assignmentId, User user) {
        RescueAssignment assignment = findAndValidateAssignmentAccess(assignmentId, user);

        if (!"ACCEPTED".equalsIgnoreCase(assignment.getStatus())
                && !"IN_PROGRESS".equalsIgnoreCase(assignment.getStatus())) {
            throw new IllegalStateException("Chỉ có thể bắt đầu di chuyển sau khi đội đã chấp nhận nhiệm vụ!");
        }

        assignment.setStatus("IN_PROGRESS");
        SosRequest sos = assignment.getSosRequest();
        sos.setStatus("IN_PROGRESS");
        sosRequestRepository.save(sos);

        RescueAssignment saved = rescueAssignmentRepository.save(assignment);
        log.info(">>> ĐỘI '{}' ĐANG TIẾP CẬN HIỆN TRƯỜNG NHIỆM VỤ #{}",
                assignment.getTeam().getTeamName(), assignmentId);

        sendMapUpdateNotification(sos.getId(), "IN_PROGRESS", assignment.getTeam().getTeamName());
        return saved;
    }

    /**
     * Báo cáo hoàn thành nhiệm vụ và nghiệm thu hiện trường cứu hộ (tải ảnh bằng chứng).
     */
    @Transactional
    public RescueAssignment completeAssignment(Long assignmentId, User user, String reportNotes, MultipartFile reportImage) {
        RescueAssignment assignment = findAndValidateAssignmentAccess(assignmentId, user);

        if (!"IN_PROGRESS".equalsIgnoreCase(assignment.getStatus())) {
            throw new IllegalStateException("Chỉ có thể hoàn tất nhiệm vụ đang ở trạng thái IN_PROGRESS!");
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
        RescueTeam team = assignment.getTeam();
        team.setStatus("AVAILABLE");
        rescueTeamRepository.save(team);

        RescueAssignment saved = rescueAssignmentRepository.save(assignment);
        log.info(">>> HOÀN TẤT CỨU NẠN: Ca SOS #{} đã được đội '{}' giải cứu an toàn!",
                sos.getId(), team.getTeamName());

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
}
