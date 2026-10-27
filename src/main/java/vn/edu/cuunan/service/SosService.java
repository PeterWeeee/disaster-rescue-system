package vn.edu.cuunan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cuunan.dto.SosRequestDto;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.SosRequestRepository;
import vn.edu.cuunan.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SosService {

    private static final Logger log = LoggerFactory.getLogger(SosService.class);

    private final SosRequestRepository sosRequestRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;

    public SosService(SosRequestRepository sosRequestRepository, UserRepository userRepository) {
        this(sosRequestRepository, userRepository, null, null, null);
    }

    public SosService(SosRequestRepository sosRequestRepository, UserRepository userRepository,
                      SimpMessagingTemplate messagingTemplate) {
        this(sosRequestRepository, userRepository, messagingTemplate, null, null);
    }

    @Autowired
    public SosService(SosRequestRepository sosRequestRepository, UserRepository userRepository,
                      @Autowired(required = false) SimpMessagingTemplate messagingTemplate,
                      @Autowired(required = false) NotificationService notificationService,
                      @Autowired(required = false) ActivityLogService activityLogService) {
        this.sosRequestRepository = sosRequestRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
        this.activityLogService = activityLogService;
    }

    /**
     * Tạo yêu cầu cứu nạn khẩn cấp mới (cho phép nặc danh hoặc đã đăng nhập).
     */
    @Transactional
    public SosRequest createSosRequest(SosRequestDto dto, Long senderUserId) {
        if (dto.getLatitude() == null || dto.getLatitude() < -90.0 || dto.getLatitude() > 90.0) {
            throw new IllegalArgumentException("Vĩ độ không hợp lệ (phải từ -90 đến 90)");
        }
        if (dto.getLongitude() == null || dto.getLongitude() < -180.0 || dto.getLongitude() > 180.0) {
            throw new IllegalArgumentException("Kinh độ không hợp lệ (phải từ -180 đến 180)");
        }

        User sender = null;
        if (senderUserId != null) {
            sender = userRepository.findById(senderUserId).orElse(null);
        }

        String urgency = normalizeUrgencyLevel(dto.getUrgencyLevel());
        int peopleCount = (dto.getPeopleCount() != null && dto.getPeopleCount() > 0) ? dto.getPeopleCount() : 1;

        SosRequest sosRequest = SosRequest.builder()
                .sender(sender)
                .contactName(dto.getContactName() != null ? dto.getContactName().trim() : "")
                .contactPhone(dto.getContactPhone() != null ? dto.getContactPhone().trim() : "")
                .secondaryPhone(dto.getSecondaryPhone() != null ? dto.getSecondaryPhone().trim() : null)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .addressDetail(dto.getAddressDetail() != null ? dto.getAddressDetail().trim() : null)
                .urgencyLevel(urgency)
                .peopleCount(peopleCount)
                .hasElderly(Boolean.TRUE.equals(dto.getHasElderly()))
                .hasChildren(Boolean.TRUE.equals(dto.getHasChildren()))
                .hasInjured(Boolean.TRUE.equals(dto.getHasInjured()))
                .description(dto.getDescription() != null ? dto.getDescription().trim() : null)
                .imageUrl(dto.getImageUrl())
                .status("PENDING")
                .build();

        SosRequest saved = sosRequestRepository.save(sosRequest);
        log.info(">>> TIẾP NHẬN SOS #{}: người liên hệ '{}', SĐT '{}', vị trí [{}, {}], mức độ '{}', số người: {}",
                saved.getId(), saved.getContactName(), saved.getContactPhone(),
                saved.getLatitude(), saved.getLongitude(), saved.getUrgencyLevel(), saved.getPeopleCount());

        // Bắn thông báo cho Điều phối viên
        if (notificationService != null) {
            String addr = saved.getAddressDetail() != null ? saved.getAddressDetail() : "Chưa có địa chỉ chi tiết";
            notificationService.sendToRole("ROLE_COORDINATOR",
                    "Ca cứu nạn SOS mới #" + saved.getId() + " (" + saved.getUrgencyLevel() + ")",
                    "Người gửi: " + saved.getContactName() + " (" + saved.getContactPhone() + ") tại "
                            + addr + ". Số người: " + saved.getPeopleCount() + ". Cần điều phối ngay!",
                    "SOS_NEW",
                    "/coordinator/dashboard");
        }

        // Ghi nhật ký kiểm toán
        if (activityLogService != null) {
            activityLogService.log(sender, "CREATE_SOS", "Ca SOS #" + saved.getId(),
                    "Tạo yêu cầu cứu nạn khẩn cấp mức " + saved.getUrgencyLevel() + " tại " + saved.getAddressDetail(), null);
        }

        if (messagingTemplate != null) {
            Map<String, Object> message = new java.util.LinkedHashMap<>();
            message.put("id", saved.getId());
            message.put("latitude", saved.getLatitude());
            message.put("longitude", saved.getLongitude());
            message.put("urgencyLevel", saved.getUrgencyLevel());
            message.put("status", saved.getStatus());
            message.put("contactName", saved.getContactName());
            message.put("contactPhone", saved.getContactPhone());
            message.put("addressDetail", saved.getAddressDetail());
            message.put("peopleCount", saved.getPeopleCount());
            message.put("hasElderly", saved.getHasElderly());
            message.put("hasChildren", saved.getHasChildren());
            message.put("description", saved.getDescription());
            message.put("createdAt", saved.getCreatedAt());
            messagingTemplate.convertAndSend("/topic/sos-alerts", message);
        }

        return saved;
    }

    /**
     * Tìm yêu cầu cứu nạn theo ID (ném ngoại lệ nếu không tìm thấy).
     */
    @Transactional(readOnly = true)
    public SosRequest getSosById(Long id) {
        return sosRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu cứu nạn #" + id));
    }

    /**
     * Tìm yêu cầu cứu nạn theo ID dạng Optional.
     */
    @Transactional(readOnly = true)
    public Optional<SosRequest> findById(Long id) {
        return sosRequestRepository.findById(id);
    }

    /**
     * Lấy toàn bộ danh sách SOS sắp xếp mới nhất lên đầu.
     */
    @Transactional(readOnly = true)
    public List<SosRequest> getAllSosRequests() {
        return sosRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Lấy danh sách SOS theo trạng thái (PENDING, DISPATCHED, IN_PROGRESS, RESOLVED, CANCELLED).
     */
    @Transactional(readOnly = true)
    public List<SosRequest> getSosByStatus(String status) {
        if (status == null || status.trim().isEmpty() || "ALL".equalsIgnoreCase(status)) {
            return getAllSosRequests();
        }
        return sosRequestRepository.findByStatusOrderByCreatedAtDesc(status.trim().toUpperCase());
    }

    @Transactional(readOnly = true)
    public List<SosRequest> getRequestsByStatus(String status) {
        return getSosByStatus(status);
    }

    /**
     * Lấy danh sách tất cả yêu cầu cứu nạn đang chờ xử lý (PENDING), sắp xếp mới nhất trước.
     */
    @Transactional(readOnly = true)
    public List<SosRequest> getPendingRequests() {
        return sosRequestRepository.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    /**
     * Lấy danh sách SOS do một người dùng cụ thể đã gửi.
     */
    @Transactional(readOnly = true)
    public List<SosRequest> getSosBySender(Long senderId) {
        return sosRequestRepository.findBySenderIdOrderByCreatedAtDesc(senderId);
    }

    @Transactional(readOnly = true)
    public List<SosRequest> getRequestsByUser(Long userId) {
        return getSosBySender(userId);
    }

    /**
     * Tìm kiếm các ca SOS theo số điện thoại người liên hệ.
     */
    @Transactional(readOnly = true)
    public List<SosRequest> getSosByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return List.of();
        }
        return sosRequestRepository.findByContactPhoneOrderByCreatedAtDesc(phone.trim());
    }

    @Transactional(readOnly = true)
    public List<SosRequest> findByContactPhone(String phone) {
        return getSosByPhone(phone);
    }

    /**
     * Cập nhật trạng thái ca cứu nạn.
     */
    @Transactional
    public SosRequest updateSosStatus(Long id, String newStatus) {
        SosRequest sos = getSosById(id);
        String upperStatus = newStatus.trim().toUpperCase();

        if (!isValidStatus(upperStatus)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ: " + newStatus);
        }

        sos.setStatus(upperStatus);
        if ("RESOLVED".equals(upperStatus) || "COMPLETED".equals(upperStatus)) {
            sos.setResolvedAt(LocalDateTime.now());
        } else {
            sos.setResolvedAt(null);
        }

        SosRequest updated = sosRequestRepository.save(sos);
        log.info(">>> CẬP NHẬT TRẠNG THÁI SOS #{}: -> {}", id, upperStatus);

        if (messagingTemplate != null) {
            Map<String, Object> update = new java.util.LinkedHashMap<>();
            update.put("id", id);
            update.put("status", upperStatus);
            update.put("resolvedAt", updated.getResolvedAt());
            messagingTemplate.convertAndSend("/topic/sos-status", update);
        }

        return updated;
    }

    @Transactional
    public SosRequest updateStatus(Long id, String newStatus) {
        return updateSosStatus(id, newStatus);
    }

    /**
     * Đếm số ca đang chờ tiếp nhận (PENDING).
     */
    @Transactional(readOnly = true)
    public long countPendingRequests() {
        return sosRequestRepository.countByStatus("PENDING");
    }

    /**
     * Đếm số ca theo trạng thái bất kỳ.
     */
    @Transactional(readOnly = true)
    public long countByStatus(String status) {
        return sosRequestRepository.countByStatus(status);
    }

    /**
     * Chuẩn hóa mức độ khẩn cấp (mặc định MEDIUM nếu rỗng/sai định dạng).
     */
    private String normalizeUrgencyLevel(String urgency) {
        if (urgency == null || urgency.trim().isEmpty()) {
            return "MEDIUM";
        }
        String upper = urgency.trim().toUpperCase();
        return switch (upper) {
            case "LOW", "MEDIUM", "HIGH", "CRITICAL" -> upper;
            default -> "MEDIUM";
        };
    }

    /**
     * Kiểm tra trạng thái hợp lệ của yêu cầu cứu nạn.
     */
    private boolean isValidStatus(String status) {
        return switch (status) {
            case "PENDING", "DISPATCHED", "IN_PROGRESS", "RESOLVED", "COMPLETED", "CANCELLED" -> true;
            default -> false;
        };
    }
}
