package vn.edu.cuunan.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.cuunan.dto.TeamResourcesDto;
import vn.edu.cuunan.entity.RescueAssignment;
import vn.edu.cuunan.entity.RescueTeam;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.security.CustomUserDetails;
import vn.edu.cuunan.service.EquipmentService;
import vn.edu.cuunan.service.RescuerService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/rescuer")
@PreAuthorize("hasAnyRole('RESCUER', 'ADMIN')")
public class RescuerController {

    private final RescuerService rescuerService;
    private final EquipmentService equipmentService;

    public RescuerController(RescuerService rescuerService, EquipmentService equipmentService) {
        this.rescuerService = rescuerService;
        this.equipmentService = equipmentService;
    }

    @GetMapping("/missions")
    public String missions(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("pageTitle", "Nhiệm Vụ Cứu Hộ - CứuNạn 24/7");

        User user = (userDetails != null) ? userDetails.getUser() : null;
        Optional<RescueTeam> teamOpt = rescuerService.getMyTeam(user);
        model.addAttribute("myTeam", teamOpt.orElse(null));

        List<RescueAssignment> assignments = rescuerService.getMyAssignments(user);
        model.addAttribute("assignments", assignments);

        long pendingCount = assignments.stream().filter(a -> "PENDING".equalsIgnoreCase(a.getStatus())).count();
        long activeCount = assignments.stream().filter(
                a -> "ACCEPTED".equalsIgnoreCase(a.getStatus()) || "IN_PROGRESS".equalsIgnoreCase(a.getStatus()))
                .count();
        long completedCount = assignments.stream().filter(a -> "COMPLETED".equalsIgnoreCase(a.getStatus())).count();

        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("completedCount", completedCount);

        return "rescuer/missions";
    }

    @GetMapping("/routes")
    public String routesView(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("pageTitle", "Lộ Trình Di Chuyển Cứu Hộ - CứuNạn 24/7");

        User user = (userDetails != null) ? userDetails.getUser() : null;
        Optional<RescueTeam> teamOpt = rescuerService.getMyTeam(user);
        model.addAttribute("myTeam", teamOpt.orElse(null));

        List<RescueAssignment> assignments = rescuerService.getMyAssignments(user);
        List<RescueAssignment> activeMissions = assignments.stream()
                .filter(a -> "ACCEPTED".equalsIgnoreCase(a.getStatus()) || "IN_PROGRESS".equalsIgnoreCase(a.getStatus())
                        || "PENDING".equalsIgnoreCase(a.getStatus()))
                .toList();

        model.addAttribute("assignments", activeMissions);
        model.addAttribute("allAssignments", assignments);
        return "rescuer/routes";
    }

    @GetMapping("/resources")
    public String resourcesView(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("pageTitle", "Thống Kê Quân Số & Kho Trang Bị - CứuNạn 24/7");

        User user = (userDetails != null) ? userDetails.getUser() : null;
        Optional<RescueTeam> teamOpt = rescuerService.getMyTeam(user);
        RescueTeam team = teamOpt.orElse(null);
        model.addAttribute("myTeam", team);

        TeamResourcesDto resources = equipmentService.getTeamResources(team);
        model.addAttribute("resources", resources);

        return "rescuer/resources";
    }
}
