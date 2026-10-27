package vn.edu.cuunan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cuunan.entity.*;
import vn.edu.cuunan.repository.NotificationRepository;
import vn.edu.cuunan.repository.RoleRepository;
import vn.edu.cuunan.repository.TeamMemberRepository;
import vn.edu.cuunan.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               RoleRepository roleRepository,
                               TeamMemberRepository teamMemberRepository,
                               SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public Notification sendToUser(User recipient, String title, String message, String type, String targetUrl) {
        if (recipient == null) {
            return null;
        }

        Notification notification = new Notification(recipient, title, message, type, targetUrl);
        notification = notificationRepository.save(notification);

        try {
            // Push qua STOMP WebSocket
            messagingTemplate.convertAndSend("/topic/notifications/user/" + recipient.getId(), notification);
        } catch (Exception e) {
            log.warn("Không thể gửi notification qua WebSocket tới user {}: {}", recipient.getId(), e.getMessage());
        }

        return notification;
    }

    @Transactional
    public void sendToTeam(RescueTeam team, String title, String message, String type, String targetUrl) {
        if (team == null) return;

        // Gửi cho đội trưởng
        if (team.getLeader() != null) {
            sendToUser(team.getLeader(), title, message, type, targetUrl);
        }

        // Gửi cho các thành viên trong đội
        List<TeamMember> members = teamMemberRepository.findByTeamId(team.getId());
        for (TeamMember m : members) {
            if (m.getUser() != null && (team.getLeader() == null || !m.getUser().getId().equals(team.getLeader().getId()))) {
                sendToUser(m.getUser(), title, message, type, targetUrl);
            }
        }
    }

    @Transactional
    public void sendToRole(String roleName, String title, String message, String type, String targetUrl) {
        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        if (roleOpt.isEmpty()) return;

        List<User> users = userRepository.findByRole(roleOpt.get());
        for (User u : users) {
            sendToUser(u, title, message, type, targetUrl);
        }
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(User user) {
        if (user == null) return 0;
        return notificationRepository.countByRecipientAndIsReadFalse(user);
    }

    @Transactional(readOnly = true)
    public List<Notification> getRecentNotifications(User user, int limit) {
        if (user == null) return List.of();
        return notificationRepository.findTop10ByRecipientOrderByCreatedAtDesc(user);
    }

    @Transactional(readOnly = true)
    public Page<Notification> getAllNotifications(User user, Pageable pageable) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user, pageable);
    }

    @Transactional
    public boolean markAsRead(Long id, User user) {
        Optional<Notification> notifOpt = notificationRepository.findById(id);
        if (notifOpt.isPresent()) {
            Notification n = notifOpt.get();
            if (n.getRecipient() != null && n.getRecipient().getId().equals(user.getId())) {
                n.setRead(true);
                notificationRepository.save(n);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public int markAllAsRead(User user) {
        if (user == null) return 0;
        return notificationRepository.markAllAsReadByRecipient(user);
    }
}
