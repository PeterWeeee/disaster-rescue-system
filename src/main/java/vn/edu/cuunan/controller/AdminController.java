package vn.edu.cuunan.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.cuunan.service.AdminService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        var users = adminService.getUsers();
        model.addAttribute("pageTitle", "Quản Trị Hệ Thống - CứuNạn 24/7");
        model.addAttribute("users", users);
        model.addAttribute("teams", adminService.getTeams());
        model.addAttribute("rescuerUsers", users.stream()
                .filter(user -> user.getRole() != null && "ROLE_RESCUER".equals(user.getRole().getName())).toList());
        model.addAttribute("stats", adminService.getStatistics());
        return "admin/dashboard";
    }
}
