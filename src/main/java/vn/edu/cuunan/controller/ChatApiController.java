package vn.edu.cuunan.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.cuunan.dto.ChatMessageDto;
import vn.edu.cuunan.dto.SendChatMessageRequest;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.UserRepository;
import vn.edu.cuunan.security.CustomUserDetails;
import vn.edu.cuunan.service.ChatService;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
public class ChatApiController {

    private static final Logger log = LoggerFactory.getLogger(ChatApiController.class);

    private final ChatService chatService;
    private final UserRepository userRepository;

    public ChatApiController(ChatService chatService, UserRepository userRepository) {
        this.chatService = chatService;
        this.userRepository = userRepository;
    }

    /**
     * REST API lấy danh sách toàn bộ lịch sử tin nhắn của ca SOS.
     */
    @GetMapping("/api/chat/sos/{sosId}/messages")
    public ResponseEntity<?> getMessages(@PathVariable("sosId") Long sosId,
                                         @RequestParam(value = "phone", required = false) String phone,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User currentUser = (userDetails != null) ? userDetails.getUser() : null;
            List<ChatMessageDto> messages = chatService.getMessagesBySosId(sosId, currentUser, phone);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    /**
     * REST API gửi tin nhắn qua HTTP (phục vụ fallback khi kết nối WebSocket STOMP bị gián đoạn).
     */
    @PostMapping("/api/chat/sos/{sosId}/messages")
    public ResponseEntity<?> sendMessage(@PathVariable("sosId") Long sosId,
                                         @Valid @RequestBody SendChatMessageRequest request,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User currentUser = (userDetails != null) ? userDetails.getUser() : null;
            ChatMessageDto result = chatService.saveAndBroadcastMessage(sosId, request, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("success", false, "message", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Lỗi khi gửi tin nhắn qua REST API: ", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Không thể gửi tin nhắn: " + ex.getMessage()));
        }
    }

    /**
     * REST API kiểm tra điều kiện mở phòng chat và thông tin ca cứu nạn.
     */
    @GetMapping("/api/chat/sos/{sosId}/status")
    public ResponseEntity<?> getChatStatus(@PathVariable("sosId") Long sosId) {
        try {
            Map<String, Object> status = chatService.getChatStatus(sosId);
            return ResponseEntity.ok(status);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    /**
     * REST API kiểm tra ca cứu nạn đang hoạt động cần chat của người dùng hiện tại (Citizen hoặc Rescuer).
     * Phục vụ khung chat nổi toàn hệ thống phong cách Facebook Messenger.
     */
    @GetMapping("/api/chat/active-sessions")
    public ResponseEntity<?> getActiveChatSessions(@RequestParam(value = "sosId", required = false) Long requestedSosId,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User currentUser = (userDetails != null) ? userDetails.getUser() : null;
            Map<String, Object> sessionMap = chatService.getActiveChatSession(currentUser, requestedSosId);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> sessions = (List<Map<String, Object>>) sessionMap.get("activeSessions");
            if (sessions == null) {
                sessions = chatService.getActiveChatSessions(currentUser);
            }
            return ResponseEntity.ok(sessions);
        } catch (Exception ex) {
            log.error("Lỗi khi lấy active chat sessions: ", ex);
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/api/chat/active-session")
    public ResponseEntity<?> getActiveChatSession(@RequestParam(value = "sosId", required = false) Long requestedSosId,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User currentUser = (userDetails != null) ? userDetails.getUser() : null;
            Map<String, Object> session = chatService.getActiveChatSession(currentUser, requestedSosId);
            return ResponseEntity.ok(session);
        } catch (Exception ex) {
            log.error("Lỗi khi lấy active chat session: ", ex);
            return ResponseEntity.ok(Map.of("hasActiveChat", false));
        }
    }

    /**
     * WebSocket Message Handler: Nhận tin nhắn STOMP từ client tại /app/chat/{sosId}/send
     * và lưu DB + broadcast tới /topic/chat/{sosId}.
     */
    @MessageMapping("/chat/{sosId}/send")
    public void handleWebSocketMessage(@DestinationVariable("sosId") Long sosId,
                                       @Valid @Payload SendChatMessageRequest request,
                                       Principal principal) {
        try {
            User currentUser = null;
            if (principal != null && principal.getName() != null) {
                currentUser = userRepository.findByUsername(principal.getName())
                        .or(() -> userRepository.findByEmail(principal.getName()))
                        .orElse(null);
            }
            chatService.saveAndBroadcastMessage(sosId, request, currentUser);
        } catch (Exception ex) {
            log.warn("Lỗi xử lý tin nhắn WebSocket STOMP ca #{}: {}", sosId, ex.getMessage());
        }
    }
}
