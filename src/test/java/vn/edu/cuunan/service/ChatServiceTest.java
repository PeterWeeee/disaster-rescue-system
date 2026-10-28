package vn.edu.cuunan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import vn.edu.cuunan.dto.ChatMessageDto;
import vn.edu.cuunan.dto.SendChatMessageRequest;
import vn.edu.cuunan.entity.*;
import vn.edu.cuunan.repository.RescueAssignmentRepository;
import vn.edu.cuunan.repository.RescueChatMessageRepository;
import vn.edu.cuunan.repository.SosRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private RescueChatMessageRepository rescueChatMessageRepository;

    @Mock
    private SosRequestRepository sosRequestRepository;

    @Mock
    private RescueAssignmentRepository rescueAssignmentRepository;

    @Mock
    private RescuerService rescuerService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatService chatService;

    private SosRequest inProgressSos;
    private SosRequest pendingSos;
    private RescueTeam sampleTeam;
    private RescueAssignment sampleAssignment;
    private User rescuerUser;

    @BeforeEach
    void setUp() {
        inProgressSos = SosRequest.builder()
                .id(100L)
                .contactName("Trần Văn A")
                .contactPhone("0912345678")
                .latitude(16.0544)
                .longitude(108.2022)
                .status("IN_PROGRESS")
                .build();

        pendingSos = SosRequest.builder()
                .id(101L)
                .contactName("Lê Thị B")
                .contactPhone("0987654321")
                .latitude(16.0600)
                .longitude(108.2100)
                .status("PENDING")
                .build();

        sampleTeam = new RescueTeam();
        sampleTeam.setId(1L);
        sampleTeam.setTeamName("Đội Cứu Hộ Hải Châu");
        sampleTeam.setContactPhone("0905111222");

        sampleAssignment = new RescueAssignment();
        sampleAssignment.setId(50L);
        sampleAssignment.setSosRequest(inProgressSos);
        sampleAssignment.setTeam(sampleTeam);
        sampleAssignment.setStatus("IN_PROGRESS");

        Role rescuerRole = Role.builder().id(3).name("ROLE_RESCUER").build();
        rescuerUser = User.builder()
                .id(5L)
                .username("rescuer01")
                .fullName("Nguyễn Cứu Hộ")
                .phone("0905111222")
                .role(rescuerRole)
                .build();
    }

    @Test
    @DisplayName("Gửi tin nhắn từ phía Người dân thành công và broadcast qua STOMP WebSocket")
    void testSaveAndBroadcastMessage_Citizen_Success() {
        when(sosRequestRepository.findById(100L)).thenReturn(Optional.of(inProgressSos));
        when(rescueAssignmentRepository.findTopBySosRequestIdOrderByAssignedAtDesc(100L))
                .thenReturn(Optional.of(sampleAssignment));

        RescueChatMessage savedEntity = RescueChatMessage.builder()
                .id(1L)
                .sosRequest(inProgressSos)
                .assignment(sampleAssignment)
                .senderType("CITIZEN")
                .senderName("Trần Văn A")
                .senderPhone("0912345678")
                .content("Nước đang dâng cao thêm, nhờ đội hỗ trợ nhanh!")
                .messageType("QUICK_ALERT")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(rescueChatMessageRepository.save(any(RescueChatMessage.class))).thenReturn(savedEntity);

        SendChatMessageRequest request = new SendChatMessageRequest();
        request.setContent("Nước đang dâng cao thêm, nhờ đội hỗ trợ nhanh!");
        request.setMessageType("QUICK_ALERT");

        ChatMessageDto result = chatService.saveAndBroadcastMessage(100L, request, null);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSosId()).isEqualTo(100L);
        assertThat(result.getSenderType()).isEqualTo("CITIZEN");
        assertThat(result.getSenderName()).isEqualTo("Trần Văn A");
        assertThat(result.getContent()).isEqualTo("Nước đang dâng cao thêm, nhờ đội hỗ trợ nhanh!");
        assertThat(result.getMessageType()).isEqualTo("QUICK_ALERT");
        assertThat(result.getFormattedTime()).isNotEmpty();

        verify(rescueChatMessageRepository, times(1)).save(any(RescueChatMessage.class));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/chat/100"), any(ChatMessageDto.class));
    }

    @Test
    @DisplayName("Gửi tin nhắn từ phía Đội cứu hộ thành công")
    void testSaveAndBroadcastMessage_Rescuer_Success() {
        when(sosRequestRepository.findById(100L)).thenReturn(Optional.of(inProgressSos));
        when(rescueAssignmentRepository.findTopBySosRequestIdOrderByAssignedAtDesc(100L))
                .thenReturn(Optional.of(sampleAssignment));
        when(rescuerService.getMyTeam(rescuerUser)).thenReturn(Optional.of(sampleTeam));

        RescueChatMessage savedEntity = RescueChatMessage.builder()
                .id(2L)
                .sosRequest(inProgressSos)
                .assignment(sampleAssignment)
                .senderType("RESCUER")
                .senderName("Đội Cứu Hộ Hải Châu")
                .senderPhone("0905111222")
                .content("Đội cách bạn 300m, hãy giữ an toàn!")
                .messageType("TEXT")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(rescueChatMessageRepository.save(any(RescueChatMessage.class))).thenReturn(savedEntity);

        SendChatMessageRequest request = new SendChatMessageRequest("Đội cách bạn 300m, hãy giữ an toàn!");
        ChatMessageDto result = chatService.saveAndBroadcastMessage(100L, request, rescuerUser);

        assertThat(result).isNotNull();
        assertThat(result.getSenderType()).isEqualTo("RESCUER");
        assertThat(result.getSenderName()).isEqualTo("Đội Cứu Hộ Hải Châu");
        assertThat(result.getIsMine()).isTrue();

        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/chat/100"), any(ChatMessageDto.class));
    }

    @Test
    @DisplayName("Gửi tin nhắn thất bại khi ca cứu hộ chưa ở trạng thái IN_PROGRESS")
    void testSaveAndBroadcastMessage_Fail_NotInProgress() {
        when(sosRequestRepository.findById(101L)).thenReturn(Optional.of(pendingSos));

        SendChatMessageRequest request = new SendChatMessageRequest("Xin chào đội cứu hộ");

        assertThatThrownBy(() -> chatService.saveAndBroadcastMessage(101L, request, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("IN_PROGRESS");

        verify(rescueChatMessageRepository, never()).save(any());
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }

    @Test
    @DisplayName("Gửi tin nhắn thất bại khi nội dung rỗng")
    void testSaveAndBroadcastMessage_Fail_EmptyContent() {
        SendChatMessageRequest request = new SendChatMessageRequest("");

        assertThatThrownBy(() -> chatService.saveAndBroadcastMessage(100L, request, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("trống");
    }

    @Test
    @DisplayName("Lấy lịch sử tin nhắn của ca SOS theo thứ tự thời gian")
    void testGetMessagesBySosId_Success() {
        when(sosRequestRepository.findById(100L)).thenReturn(Optional.of(inProgressSos));

        RescueChatMessage m1 = RescueChatMessage.builder()
                .id(1L)
                .sosRequest(inProgressSos)
                .senderType("CITIZEN")
                .senderName("Trần Văn A")
                .senderPhone("0912345678")
                .content("Cần cứu gấp")
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .build();

        RescueChatMessage m2 = RescueChatMessage.builder()
                .id(2L)
                .sosRequest(inProgressSos)
                .senderType("RESCUER")
                .senderName("Đội Hải Châu")
                .senderPhone("0905111222")
                .content("Đội đang tới")
                .createdAt(LocalDateTime.now().minusMinutes(5))
                .build();

        when(rescueChatMessageRepository.findBySosRequestIdOrderByCreatedAtAsc(100L)).thenReturn(List.of(m1, m2));

        List<ChatMessageDto> messages = chatService.getMessagesBySosId(100L, null, "0912345678");

        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).getContent()).isEqualTo("Cần cứu gấp");
        assertThat(messages.get(0).getIsMine()).isTrue();
        assertThat(messages.get(1).getContent()).isEqualTo("Đội đang tới");
        assertThat(messages.get(1).getIsMine()).isFalse();
    }

    @Test
    @DisplayName("Kiểm tra điều kiện mở phòng chat isChatAllowed")
    void testIsChatAllowed() {
        when(sosRequestRepository.findById(100L)).thenReturn(Optional.of(inProgressSos));
        when(sosRequestRepository.findById(101L)).thenReturn(Optional.of(pendingSos));

        assertThat(chatService.isChatAllowed(100L)).isTrue();
        assertThat(chatService.isChatAllowed(101L)).isFalse();
        assertThat(chatService.isChatAllowed(999L)).isFalse();
    }

    @Test
    @DisplayName("Lấy trạng thái phòng chat getChatStatus")
    void testGetChatStatus() {
        when(sosRequestRepository.findById(100L)).thenReturn(Optional.of(inProgressSos));
        when(rescueAssignmentRepository.findTopBySosRequestIdOrderByAssignedAtDesc(100L))
                .thenReturn(Optional.of(sampleAssignment));

        Map<String, Object> status = chatService.getChatStatus(100L);

        assertThat(status).isNotNull();
        assertThat(status.get("canChat")).isEqualTo(true);
        assertThat(status.get("isClosed")).isEqualTo(false);
        assertThat(status.get("teamName")).isEqualTo("Đội Cứu Hộ Hải Châu");
        assertThat(status.get("status")).isEqualTo("IN_PROGRESS");
    }
}
