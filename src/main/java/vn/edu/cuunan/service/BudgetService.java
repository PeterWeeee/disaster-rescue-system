package vn.edu.cuunan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cuunan.dto.GeneralFundSummaryDto;
import vn.edu.cuunan.dto.TeamFundRequestDto;
import vn.edu.cuunan.entity.RescueTeam;
import vn.edu.cuunan.entity.TeamFundRequest;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.DonationRepository;
import vn.edu.cuunan.repository.RescueTeamRepository;
import vn.edu.cuunan.repository.TeamFundRequestRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BudgetService {

    private static final Logger log = LoggerFactory.getLogger(BudgetService.class);

    private final TeamFundRequestRepository fundRequestRepository;
    private final RescueTeamRepository rescueTeamRepository;
    private final DonationRepository donationRepository;
    private final RescuerService rescuerService;
    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${app.fund.initial-base:500000000}")
    private BigDecimal initialBaseFund = new BigDecimal("500000000"); // 500 triệu quỹ nền tảng tổ chức

    @Autowired
    public BudgetService(TeamFundRequestRepository fundRequestRepository,
                         RescueTeamRepository rescueTeamRepository,
                         DonationRepository donationRepository,
                         RescuerService rescuerService,
                         @Autowired(required = false) NotificationService notificationService,
                         @Autowired(required = false) ActivityLogService activityLogService,
                         @Autowired(required = false) SimpMessagingTemplate messagingTemplate) {
        this.fundRequestRepository = fundRequestRepository;
        this.rescueTeamRepository = rescueTeamRepository;
        this.donationRepository = donationRepository;
        this.rescuerService = rescuerService;
        this.notificationService = notificationService;
        this.activityLogService = activityLogService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Tổng hợp trạng thái Quỹ cứu trợ chung của tổ chức (Minh bạch từ quyên góp & giải ngân cho các đội).
     */
    @Transactional(readOnly = true)
    public GeneralFundSummaryDto getOrganizationGeneralFund() {
        BigDecimal totalDonations = donationRepository.sumAmountByStatus("SUCCESS");
        if (totalDonations == null) {
            totalDonations = BigDecimal.ZERO;
        }

        BigDecimal totalDisbursed = fundRequestRepository.sumApprovedAmount();
        if (totalDisbursed == null) {
            totalDisbursed = BigDecimal.ZERO;
        }

        long pendingCount = fundRequestRepository.countByStatus("PENDING");

        return new GeneralFundSummaryDto(initialBaseFund, totalDonations, totalDisbursed, pendingCount);
    }

    /**
     * Đội cứu trợ gửi yêu cầu tiếp tế ngân sách mua nhu yếu phẩm.
     */
    @Transactional
    public TeamFundRequest createFundRequest(User requester, TeamFundRequestDto dto) {
        if (requester == null) {
            throw new IllegalArgumentException("Người dùng chưa được xác thực!");
        }

        RescueTeam team = null;
        if (dto.getTeamId() != null) {
            team = rescueTeamRepository.findById(dto.getTeamId()).orElse(null);
        }
        if (team == null) {
            Optional<RescueTeam> myTeamOpt = rescuerService.getMyTeam(requester);
            if (myTeamOpt.isEmpty()) {
                throw new IllegalArgumentException("Bạn chưa tham gia Đội cứu hộ nào để gửi yêu cầu tiếp tế ngân sách!");
            }
            team = myTeamOpt.get();
        }

        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền xin tiếp tế phải lớn hơn 0!");
        }

        TeamFundRequest request = TeamFundRequest.builder()
                .rescueTeam(team)
                .requester(requester)
                .amount(dto.getAmount())
                .purpose(dto.getPurpose().trim())
                .category(dto.getCategory() != null ? dto.getCategory() : "NHU_YEU_PHAM")
                .urgencyLevel(dto.getUrgencyLevel() != null ? dto.getUrgencyLevel() : "HIGH")
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        request = fundRequestRepository.save(request);
        log.info(">>> ĐỘI [{}] GỬI YÊU CẦU TIẾP TẾ NGÂN SÁCH: {} đ - Mục đích: {}",
                team.getTeamName(), request.getAmount(), request.getPurpose());

        // Báo cho Điều phối viên qua WebSocket và Notification
        if (notificationService != null && team.getLeader() != null) {
            notificationService.notifyBudgetRequestCreated(request);
        }

        if (messagingTemplate != null) {
            try {
                messagingTemplate.convertAndSend("/topic/coordinator/budget-alerts", Map.of(
                        "type", "NEW_BUDGET_REQUEST",
                        "requestId", request.getId(),
                        "teamName", team.getTeamName(),
                        "amount", request.getFormattedAmount(),
                        "purpose", request.getPurpose()
                ));
            } catch (Exception ex) {
                log.warn(">>> Không thể gửi WebSocket alert ngân sách: {}", ex.getMessage());
            }
        }

        return request;
    }

    /**
     * Người Điều Phối phê duyệt yêu cầu tiếp tế:
     * - Trừ thẳng vào Ngân sách chung của tổ chức (General Fund)
     * - Cộng tiền vào Ngân sách hiện có của Đội cứu hộ (Team Budget)
     */
    @Transactional
    public TeamFundRequest approveFundRequest(Long requestId, User coordinator) {
        TeamFundRequest request = fundRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu tiếp tế ngân sách #" + requestId));

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            throw new IllegalStateException("Yêu cầu này đã được xử lý trước đó rồi (Trạng thái: " + request.getStatus() + ")");
        }

        // Kiểm tra ngân sách chung khả dụng của tổ chức
        GeneralFundSummaryDto generalFund = getOrganizationGeneralFund();
        if (request.getAmount().compareTo(generalFund.getAvailableGeneralFund()) > 0) {
            throw new IllegalStateException("Ngân sách chung của tổ chức không đủ để giải ngân! (Yêu cầu: "
                    + request.getFormattedAmount() + ", Quỹ khả dụng hiện có: "
                    + generalFund.getFormattedAvailableGeneralFund() + ")");
        }

        // 1. Cộng ngân sách cho Đội cứu hộ
        RescueTeam team = request.getRescueTeam();
        BigDecimal oldBudget = team.getBudget();
        team.setBudget(oldBudget.add(request.getAmount()));
        rescueTeamRepository.save(team);

        // 2. Cập nhật trạng thái yêu cầu sang APPROVED (Từ đây sumApprovedAmount() tăng lên -> Quỹ chung tự động trừ)
        request.setStatus("APPROVED");
        request.setReviewedBy(coordinator);
        request.setReviewedAt(LocalDateTime.now());
        request = fundRequestRepository.save(request);

        log.info(">>> ĐIỀU PHỐI DUYỆT CẤP KINH PHÍ: Cấp {} đ cho Đội [{}] (Ngân sách mới của đội: {} đ)",
                request.getAmount(), team.getTeamName(), team.getBudget());

        // 3. Thông báo cho Đội cứu hộ
        if (notificationService != null) {
            notificationService.notifyBudgetRequestApproved(request);
        }

        // 4. Ghi nhật ký kiểm toán
        if (activityLogService != null && coordinator != null) {
            activityLogService.log(coordinator, "APPROVE_BUDGET_REQUEST",
                    "TeamFundRequest #" + requestId,
                    "Phê duyệt cấp kinh phí " + request.getFormattedAmount() + " cho đội " + team.getTeamName(),
                    null);
        }

        return request;
    }

    /**
     * Người Điều Phối từ chối yêu cầu tiếp tế ngân sách:
     * - Không trừ quỹ chung, không cộng tiền đội
     * - Lưu lý do từ chối và thông báo cho đội cứu hộ
     */
    @Transactional
    public TeamFundRequest rejectFundRequest(Long requestId, String reason, User coordinator) {
        TeamFundRequest request = fundRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu tiếp tế ngân sách #" + requestId));

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            throw new IllegalStateException("Yêu cầu này đã được xử lý trước đó rồi (Trạng thái: " + request.getStatus() + ")");
        }

        request.setStatus("REJECTED");
        request.setRejectReason(reason != null && !reason.isBlank() ? reason.trim() : "Chưa đáp ứng tiêu chuẩn phân bổ ngân sách khẩn cấp đợt này.");
        request.setReviewedBy(coordinator);
        request.setReviewedAt(LocalDateTime.now());
        request = fundRequestRepository.save(request);

        log.info(">>> ĐIỀU PHỐI TỪ CHỐI TIẾP TẾ NGÂN SÁCH #{}: Đội [{}] - Lý do: {}",
                requestId, request.getRescueTeam().getTeamName(), request.getRejectReason());

        if (notificationService != null) {
            notificationService.notifyBudgetRequestRejected(request);
        }

        if (activityLogService != null && coordinator != null) {
            activityLogService.log(coordinator, "REJECT_BUDGET_REQUEST",
                    "TeamFundRequest #" + requestId,
                    "Từ chối cấp kinh phí cho đội " + request.getRescueTeam().getTeamName() + ". Lý do: " + request.getRejectReason(),
                    null);
        }

        return request;
    }

    @Transactional(readOnly = true)
    public List<TeamFundRequest> getRequestsByTeam(Long teamId) {
        return fundRequestRepository.findByRescueTeamIdOrderByCreatedAtDesc(teamId);
    }

    @Transactional(readOnly = true)
    public List<TeamFundRequest> getPendingRequests() {
        return fundRequestRepository.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    @Transactional(readOnly = true)
    public List<TeamFundRequest> getAllRequests() {
        return fundRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Optional<TeamFundRequest> getRequestById(Long id) {
        return fundRequestRepository.findById(id);
    }
}
