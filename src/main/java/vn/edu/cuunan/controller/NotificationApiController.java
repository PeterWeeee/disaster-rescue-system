package vn.edu.cuunan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.cuunan.entity.Notification;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.security.CustomUserDetails;
import vn.edu.cuunan.service.NotificationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("isAuthenticated()")
public class NotificationApiController {

    private final NotificationService notificationService;

    public NotificationApiController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails != null ? userDetails.getUser() : null;
        long unread = notificationService.getUnreadCount(user);
        Map<String, Object> res = new HashMap<>();
        res.put("unreadCount", unread);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Notification>> getRecentNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails != null ? userDetails.getUser() : null;
        List<Notification> list = notificationService.getRecentNotifications(user, 10);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long id,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails != null ? userDetails.getUser() : null;
        boolean success = notificationService.markAsRead(id, user);
        Map<String, Object> res = new HashMap<>();
        res.put("success", success);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails != null ? userDetails.getUser() : null;
        int count = notificationService.markAllAsRead(user);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("markedCount", count);
        return ResponseEntity.ok(res);
    }
}
