package vn.edu.cuunan.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.cuunan.entity.Notification;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.security.CustomUserDetails;
import vn.edu.cuunan.service.NotificationService;

@Controller
@PreAuthorize("isAuthenticated()")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public String notificationsView(@AuthenticationPrincipal CustomUserDetails userDetails,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "15") int size,
                                    Model model) {
        User user = userDetails != null ? userDetails.getUser() : null;
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        Page<Notification> notificationPage = notificationService.getAllNotifications(user, pageable);
        long unreadCount = notificationService.getUnreadCount(user);

        model.addAttribute("pageTitle", "Thông Báo Hệ Thống");
        model.addAttribute("notifications", notificationPage.getContent());
        model.addAttribute("currentPage", notificationPage.getNumber());
        model.addAttribute("totalPages", notificationPage.getTotalPages());
        model.addAttribute("totalElements", notificationPage.getTotalElements());
        model.addAttribute("unreadCount", unreadCount);

        return "notification/index";
    }
}
