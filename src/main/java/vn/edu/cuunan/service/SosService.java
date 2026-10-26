package vn.edu.cuunan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cuunan.dto.SosRequestDto;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.SosRequestRepository;
import vn.edu.cuunan.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SosService {

    private static final Logger log = LoggerFactory.getLogger(SosService.class);

    private final SosRequestRepository sosRequestRepository;
    private final UserRepository userRepository;

    public SosService(SosRequestRepository sosRequestRepository, UserRepository userRepository) {
        this.sosRequestRepository = sosRequestRepository;
        this.userRepository = userRepository;
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
        return sosRequestRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    /**
     * Lấy danh sách SOS do một người dùng cụ thể đã gửi.
     */
    @Transactional(readOnly = true)
    public List<SosRequest> getSosBySender(Long senderId) {
        return sosRequestRepository.findBySenderIdOrderByCreatedAtDesc(senderId);
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
        sos.setStatus(upperStatus);

        if ("RESOLVED".equals(upperStatus) || "COMPLETED".equals(upperStatus)) {
            sos.setResolvedAt(LocalDateTime.now());
        } else {
            sos.setResolvedAt(null);
        }

        SosRequest updated = sosRequestRepository.save(sos);
        log.info(">>> CẬP NHẬT TRẠNG THÁI SOS #{}: -> {}", id, upperStatus);
        return updated;
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

    private String normalizeUrgencyLevel(String urgencyLevel) {
        if (urgencyLevel == null) {
            return "HIGH";
        }
        String u = urgencyLevel.trim().toUpperCase();
        return switch (u) {
            case "CRITICAL", "HIGH", "MEDIUM", "LOW" -> u;
            default -> "HIGH";
        };
    }
}
