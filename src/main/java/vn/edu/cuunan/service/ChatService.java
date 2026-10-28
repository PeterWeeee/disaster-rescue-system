package vn.edu.cuunan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cuunan.dto.ChatMessageDto;
import vn.edu.cuunan.dto.SendChatMessageRequest;
import vn.edu.cuunan.entity.RescueAssignment;
import vn.edu.cuunan.entity.RescueChatMessage;
import vn.edu.cuunan.entity.RescueTeam;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.RescueAssignmentRepository;
import vn.edu.cuunan.repository.RescueChatMessageRepository;
import vn.edu.cuunan.repository.SosRequestRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final RescueChatMessageRepository rescueChatMessageRepository;
    private final SosRequestRepository sosRequestRepository;
    private final RescueAssignmentRepository rescueAssignmentRepository;
    private final RescuerService rescuerService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatService(RescueChatMessageRepository rescueChatMessageRepository,
                       SosRequestRepository sosRequestRepository,
                       RescueAssignmentRepository rescueAssignmentRepository,
                       RescuerService rescuerService,
                       @Autowired(required = false) SimpMessagingTemplate messagingTemplate) {
        this.rescueChatMessageRepository = rescueChatMessageRepository;
        this.sosRequestRepository = sosRequestRepository;
        this.rescueAssignmentRepository = rescueAssignmentRepository;
        this.rescuerService = rescuerService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Lấy toàn bộ lịch sử tin nhắn của ca SOS theo thứ tự thời gian tăng dần.
     */
    @Transactional(readOnly = true)
    public List<ChatMessageDto> getMessagesBySosId(Long sosId, User currentUser, String citizenPhone) {
        if (sosId == null) {
            throw new IllegalArgumentException("Mã ca SOS không được để trống!");
        }

        SosRequest sos = sosRequestRepository.findById(sosId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca cứu nạn #" + sosId));

        String effectivePhone = citizenPhone;
        if (effectivePhone == null || effectivePhone.isBlank()) {
            effectivePhone = sos.getContactPhone();
        }

        final String finalPhone = effectivePhone;
        List<RescueChatMessage> messages = rescueChatMessageRepository.findBySosRequestIdOrderByCreatedAtAsc(sosId);

        return messages.stream()
                .map(msg -> ChatMessageDto.fromEntity(msg, currentUser, finalPhone))
                .toList();
    }

    /**
     * Lưu tin nhắn vào CSDL và phát sóng (broadcast) thời gian thực tới topic STOMP /topic/chat/{sosId}.
     */
    @Transactional
    public ChatMessageDto saveAndBroadcastMessage(Long sosId, SendChatMessageRequest request, User currentUser) {
        if (sosId == null) {
            throw new IllegalArgumentException("Mã ca SOS không được để trống!");
        }
        if (request == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung tin nhắn không được để trống!");
        }

        SosRequest sos = sosRequestRepository.findById(sosId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca cứu nạn #" + sosId));

        // Kiểm tra trạng thái ca cứu hộ: Chỉ cho phép gửi tin nhắn khi đang tiếp cận (IN_PROGRESS)
        if (!isChatAllowed(sosId)) {
            throw new IllegalStateException("Kênh chat trực tuyến chỉ hoạt động khi ca cứu nạn đang trong trạng thái Đang tiếp cận (IN_PROGRESS)!");
        }

        RescueAssignment assignment = rescueAssignmentRepository.findTopBySosRequestIdOrderByAssignedAtDesc(sosId)
                .orElse(null);

        // Xác định danh tính người gửi
        String senderType = "CITIZEN";
        String senderName = sos.getContactName();
        String senderPhone = sos.getContactPhone();

        if (currentUser != null) {
            String roleName = currentUser.getRole() != null ? currentUser.getRole().getName() : "";
            if ("ROLE_RESCUER".equalsIgnoreCase(roleName)) {
                senderType = "RESCUER";
                Optional<RescueTeam> teamOpt = rescuerService.getMyTeam(currentUser);
                if (teamOpt.isPresent()) {
                    senderName = teamOpt.get().getTeamName();
                    senderPhone = teamOpt.get().getContactPhone();
                } else {
                    senderName = currentUser.getFullName();
                    senderPhone = currentUser.getPhone();
                }
            } else if ("ROLE_COORDINATOR".equalsIgnoreCase(roleName) || "ROLE_ADMIN".equalsIgnoreCase(roleName)) {
                senderType = "COORDINATOR";
                senderName = "Điều Phối Viên (" + currentUser.getFullName() + ")";
                senderPhone = currentUser.getPhone();
            } else {
                senderType = "CITIZEN";
                senderName = currentUser.getFullName();
                senderPhone = (currentUser.getPhone() != null && !currentUser.getPhone().isBlank())
                        ? currentUser.getPhone() : sos.getContactPhone();
            }
        } else {
            // Trường hợp người dân dùng link tra cứu công khai
            if (request.getSenderType() != null && !request.getSenderType().isBlank()) {
                senderType = request.getSenderType().trim().toUpperCase();
            }
            if (request.getSenderName() != null && !request.getSenderName().isBlank()) {
                senderName = request.getSenderName().trim();
            }
            if (request.getSenderPhone() != null && !request.getSenderPhone().isBlank()) {
                senderPhone = request.getSenderPhone().trim();
            }
        }

        String messageType = (request.getMessageType() != null && !request.getMessageType().isBlank())
                ? request.getMessageType().trim().toUpperCase() : "TEXT";

        RescueChatMessage chatMessage = RescueChatMessage.builder()
                .sosRequest(sos)
                .assignment(assignment)
                .senderType(senderType)
                .senderName(senderName)
                .senderPhone(senderPhone)
                .content(request.getContent().trim())
                .messageType(messageType)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        RescueChatMessage saved = rescueChatMessageRepository.save(chatMessage);
        log.info(">>> [CHAT] Tin nhắn mới ca #SOS-{}: [{}] {}: {}", sosId, senderType, senderName, saved.getContent());

        ChatMessageDto dto = ChatMessageDto.fromEntity(saved, currentUser, senderPhone);

        // Broadcast qua STOMP WebSocket
        if (messagingTemplate != null) {
            try {
                messagingTemplate.convertAndSend("/topic/chat/" + sosId, dto);
            } catch (Exception ex) {
                log.warn("Không thể broadcast tin nhắn qua WebSocket STOMP: {}", ex.getMessage());
            }
        }

        return dto;
    }

    /**
     * Kiểm tra điều kiện mở phòng chat cho ca SOS (status == IN_PROGRESS).
     */
    @Transactional(readOnly = true)
    public boolean isChatAllowed(Long sosId) {
        if (sosId == null) return false;
        Optional<SosRequest> optSos = sosRequestRepository.findById(sosId);
        return optSos.map(sos -> "IN_PROGRESS".equalsIgnoreCase(sos.getStatus())).orElse(false);
    }

    /**
     * Lấy thông tin trạng thái phòng chat và các thông số cần thiết cho client.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getChatStatus(Long sosId) {
        if (sosId == null) {
            throw new IllegalArgumentException("Mã ca SOS không được để trống!");
        }

        SosRequest sos = sosRequestRepository.findById(sosId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca cứu nạn #" + sosId));

        String status = sos.getStatus() != null ? sos.getStatus().toUpperCase() : "PENDING";
        boolean canChat = "IN_PROGRESS".equalsIgnoreCase(status);
        boolean isClosed = "RESOLVED".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status)
                || "CANCELLED".equalsIgnoreCase(status);

        String message;
        if (canChat) {
            message = "Đội cứu hộ đang trên đường tiếp cận. Kênh liên lạc trực tuyến 2 chiều đang hoạt động.";
        } else if (isClosed) {
            message = "Ca cứu nạn đã hoàn tất - Kênh chat đã chuyển sang chế độ lưu trữ hồ sơ.";
        } else {
            message = "Kênh chat trực tuyến sẽ tự động mở khi đội cứu hộ bắt đầu di chuyển tiếp cận.";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("sosId", sosId);
        result.put("status", status);
        result.put("canChat", canChat);
        result.put("isClosed", isClosed);
        result.put("message", message);
        result.put("contactName", sos.getContactName());
        result.put("contactPhone", sos.getContactPhone());

        rescueAssignmentRepository.findTopBySosRequestIdOrderByAssignedAtDesc(sosId).ifPresent(assignment -> {
            result.put("assignmentId", assignment.getId());
            if (assignment.getTeam() != null) {
                result.put("teamName", assignment.getTeam().getTeamName());
                result.put("teamPhone", assignment.getTeam().getContactPhone());
            }
        });

        return result;
    }
}
