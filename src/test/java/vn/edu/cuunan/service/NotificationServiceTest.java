package vn.edu.cuunan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import vn.edu.cuunan.entity.Notification;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.NotificationRepository;
import vn.edu.cuunan.repository.RoleRepository;
import vn.edu.cuunan.repository.TeamMemberRepository;
import vn.edu.cuunan.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private NotificationService notificationService;

    private User recipient;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(
                notificationRepository,
                userRepository,
                roleRepository,
                teamMemberRepository,
                messagingTemplate
        );

        recipient = User.builder().id(1L).username("rescuer01").fullName("Nguyễn Cứu Hộ").build();
    }

    @Test
    @DisplayName("Gửi thông báo lưu vào database và phát qua WebSocket")
    void testSendToUser() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> {
            Notification n = i.getArgument(0);
            n.setId(99L);
            return n;
        });

        Notification result = notificationService.sendToUser(recipient, "Nhiệm vụ mới", "Có ca cứu nạn", "MISSION_NEW", "/rescuer/missions");

        assertNotNull(result);
        assertEquals(99L, result.getId());
        assertEquals("Nhiệm vụ mới", result.getTitle());
        verify(notificationRepository).save(any(Notification.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/notifications/user/1"), any(Notification.class));
    }

    @Test
    @DisplayName("Lấy số lượng thông báo chưa đọc")
    void testGetUnreadCount() {
        when(notificationRepository.countByRecipientAndIsReadFalse(recipient)).thenReturn(3L);

        long count = notificationService.getUnreadCount(recipient);
        assertEquals(3L, count);
    }

    @Test
    @DisplayName("Đánh dấu một thông báo đã đọc theo đúng ID và đúng quyền sở hữu")
    void testMarkAsRead() {
        Notification n = new Notification(recipient, "Test", "Msg", "SYSTEM", null);
        n.setId(10L);
        n.setRead(false);

        when(notificationRepository.findById(10L)).thenReturn(Optional.of(n));

        boolean success = notificationService.markAsRead(10L, recipient);

        assertTrue(success);
        assertTrue(n.isRead());
        verify(notificationRepository).save(n);
    }
}
