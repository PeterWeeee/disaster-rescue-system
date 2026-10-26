package vn.edu.cuunan.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.cuunan.entity.RescueAssignment;
import vn.edu.cuunan.entity.RescueTeam;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.security.CustomUserDetails;
import vn.edu.cuunan.service.RescuerService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/rescuer")
@PreAuthorize("hasAnyRole('RESCUER', 'ADMIN')")
public class RescuerController {

    private final RescuerService rescuerService;

    public RescuerController(RescuerService rescuerService) {
        this.rescuerService = rescuerService;
    }

    @GetMapping("/missions")
    public String missions(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("pageTitle", "Tác Nghiệp Cứu Hộ Hiện Trường - CứuNạn 24/7");

        User user = (userDetails != null) ? userDetails.getUser() : null;
        Optional<RescueTeam> teamOpt = rescuerService.getMyTeam(user);

        model.addAttribute("myTeam", teamOpt.orElse(null));

        List<RescueAssignment> assignments = rescuerService.getMyAssignments(user);
        model.addAttribute("assignments", assignments);

        // Phân loại nhiệm vụ theo trạng thái
        long pendingCount = assignments.stream().filter(a -> "PENDING".equalsIgnoreCase(a.getStatus())).count();
        long activeCount = assignments.stream().filter(a -> "ACCEPTED".equalsIgnoreCase(a.getStatus()) || "IN_PROGRESS".equalsIgnoreCase(a.getStatus())).count();
        long completedCount = assignments.stream().filter(a -> "COMPLETED".equalsIgnoreCase(a.getStatus())).count();

        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("completedCount", completedCount);

        return "rescuer/missions";
    }
}
