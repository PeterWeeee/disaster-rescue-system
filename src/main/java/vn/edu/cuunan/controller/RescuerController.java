package vn.edu.cuunan.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.cuunan.dto.TeamFundRequestDto;
import vn.edu.cuunan.dto.TeamResourcesDto;
import vn.edu.cuunan.entity.RescueAssignment;
import vn.edu.cuunan.entity.RescueTeam;
import vn.edu.cuunan.entity.TeamFundRequest;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.security.CustomUserDetails;
import vn.edu.cuunan.service.BudgetService;
import vn.edu.cuunan.service.EquipmentService;
import vn.edu.cuunan.service.RescuerService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/rescuer")
@PreAuthorize("hasAnyRole('RESCUER', 'ADMIN')")
public class RescuerController {

    private final RescuerService rescuerService;
    private final EquipmentService equipmentService;
    private final BudgetService budgetService;

    public RescuerController(RescuerService rescuerService,
                             EquipmentService equipmentService,
                             BudgetService budgetService) {
        this.rescuerService = rescuerService;
        this.equipmentService = equipmentService;
        this.budgetService = budgetService;
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
        model.addAttribute("pageTitle", "Thống Kê Quân Số, Kho Trang Bị & Ngân Sách Tiếp Tế - CứuNạn 24/7");

        User user = (userDetails != null) ? userDetails.getUser() : null;
        Optional<RescueTeam> teamOpt = rescuerService.getMyTeam(user);
        RescueTeam team = teamOpt.orElse(null);
        model.addAttribute("myTeam", team);

        TeamResourcesDto resources = equipmentService.getTeamResources(team);
        model.addAttribute("resources", resources);

        if (team != null) {
            List<TeamFundRequest> budgetRequests = budgetService.getRequestsByTeam(team.getId());
            model.addAttribute("budgetRequests", budgetRequests);
            model.addAttribute("teamBudget", team.getFormattedBudget());
        } else {
            model.addAttribute("budgetRequests", Collections.emptyList());
            model.addAttribute("teamBudget", "0 ₫");
        }

        model.addAttribute("newBudgetRequest", new TeamFundRequestDto());
        return "rescuer/resources";
    }

    @PostMapping("/resources/request-budget")
    public String submitBudgetRequest(@Valid @ModelAttribute("newBudgetRequest") TeamFundRequestDto dto,
                                      BindingResult bindingResult,
                                      @AuthenticationPrincipal CustomUserDetails userDetails,
                                      RedirectAttributes redirectAttributes) {
        if (userDetails == null || userDetails.getUser() == null) {
            return "redirect:/auth/login";
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Dữ liệu yêu cầu tiếp tế ngân sách không hợp lệ!");
            return "redirect:/rescuer/resources";
        }
        try {
            budgetService.createFundRequest(userDetails.getUser(), dto);
            redirectAttributes.addFlashAttribute("successMessage", "Đã gửi yêu cầu tiếp tế ngân sách tới Người Điều Phối thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể gửi yêu cầu tiếp tế: " + ex.getMessage());
        }
        return "redirect:/rescuer/resources";
    }
}
