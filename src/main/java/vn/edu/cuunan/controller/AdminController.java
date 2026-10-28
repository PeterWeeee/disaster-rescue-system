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
import vn.edu.cuunan.service.BudgetService;
import vn.edu.cuunan.service.DonationService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final ActivityLogService activityLogService;
    private final BudgetService budgetService;
    private final DonationService donationService;

    public AdminController(AdminService adminService,
                           ActivityLogService activityLogService,
                           BudgetService budgetService,
                           DonationService donationService) {
        this.adminService = adminService;
        this.activityLogService = activityLogService;
        this.budgetService = budgetService;
        this.donationService = donationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "view", defaultValue = "overview") String view, Model model) {
        var users = adminService.getUsers();
        model.addAttribute("pageTitle", "Tổng Quan Quản Trị Hệ Thống - CứuNạn 24/7");
        model.addAttribute("users", users);
        model.addAttribute("teams", adminService.getTeams());
        model.addAttribute("rescuerUsers", users.stream()
                .filter(user -> user.getRole() != null && "ROLE_RESCUER".equals(user.getRole().getName())).toList());
        model.addAttribute("stats", adminService.getStatistics());

        // Dữ liệu Báo cáo & Thống kê Thu - Chi cho Admin
        model.addAttribute("fundSummary", budgetService.getOrganizationGeneralFund());
        model.addAttribute("allDonations", donationService.getAllSuccessfulDonations());
        model.addAttribute("recentDonations", donationService.getRecentDonations());
        model.addAttribute("fundRequests", budgetService.getAllRequests());
        model.addAttribute("donationStats", donationService.getDonationStats());
        return "admin/dashboard";
    }

    @GetMapping("/finance")
    public String finance() {
        return "redirect:/admin/dashboard?view=finance";
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
