package vn.edu.cuunan.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.cuunan.entity.ActivityLog;
import vn.edu.cuunan.service.ActivityLogService;
import vn.edu.cuunan.service.AdminService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final ActivityLogService activityLogService;

    public AdminController(AdminService adminService, ActivityLogService activityLogService) {
        this.adminService = adminService;
        this.activityLogService = activityLogService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        var users = adminService.getUsers();
        model.addAttribute("pageTitle", "Tổng Quan Quản Trị Hệ Thống - CứuNạn 24/7");
        model.addAttribute("users", users);
        model.addAttribute("teams", adminService.getTeams());
        model.addAttribute("rescuerUsers", users.stream()
                .filter(user -> user.getRole() != null && "ROLE_RESCUER".equals(user.getRole().getName())).toList());
        model.addAttribute("stats", adminService.getStatistics());
        return "admin/dashboard";
    }

    @GetMapping("/audit-logs")
    public String auditLogs(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "30") int size,
                            Model model) {
        model.addAttribute("pageTitle", "Nhật Ký Hoạt Động & Kiểm Toán Hệ Thống");
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        Page<ActivityLog> logPage = activityLogService.getLogsPage(pageable);

        model.addAttribute("logs", logPage.getContent());
        model.addAttribute("currentPage", logPage.getNumber());
        model.addAttribute("totalPages", logPage.getTotalPages());
        model.addAttribute("totalElements", logPage.getTotalElements());
        return "admin/audit-logs";
    }
}
