package vn.edu.cuunan.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.cuunan.service.BudgetService;
import vn.edu.cuunan.service.CoordinatorService;
import vn.edu.cuunan.util.ProvinceRegion;

@Controller
@RequestMapping("/coordinator")
@PreAuthorize("hasAnyRole('COORDINATOR', 'ADMIN')")
public class CoordinatorController {

    private final CoordinatorService coordinatorService;
    private final BudgetService budgetService;

    public CoordinatorController(CoordinatorService coordinatorService, BudgetService budgetService) {
        this.coordinatorService = coordinatorService;
        this.budgetService = budgetService;
    }

    @GetMapping
    public String index() {
        return "redirect:/coordinator/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
                            @RequestParam(value = "province", required = false, defaultValue = "ALL") String province,
                            Model model) {
        String cleanProvince = (province == null || province.isBlank()) ? "ALL" : province.trim().toUpperCase();
        String cleanStatus = (status == null || status.isBlank()) ? "ALL" : status.trim().toUpperCase();

        ProvinceRegion selectedRegion = ProvinceRegion.fromCode(cleanProvince);

        model.addAttribute("pageTitle", "Trung Tâm Điều Phối Cứu Nạn - " + selectedRegion.getName());
        model.addAttribute("currentStatusFilter", cleanStatus);
        model.addAttribute("currentProvince", selectedRegion.getCode());
        model.addAttribute("selectedRegion", selectedRegion);
        model.addAttribute("provinces", ProvinceRegion.values());

        model.addAttribute("stats", coordinatorService.getDashboardStats(cleanProvince));
        model.addAttribute("sosList", coordinatorService.getSosRequests(cleanStatus, cleanProvince));
        model.addAttribute("rescueTeams", coordinatorService.getRescueTeams(cleanProvince));
        model.addAttribute("sosMapPins", coordinatorService.getSosMapPins(cleanStatus, cleanProvince));
        model.addAttribute("teamMapPins", coordinatorService.getRescueTeamMapPins(cleanProvince));

        // Thống kê Ngân sách chung & Quản lý yêu cầu tiếp tế từ các đội
        model.addAttribute("generalFund", budgetService.getOrganizationGeneralFund());
        model.addAttribute("budgetRequests", budgetService.getAllRequests());
        model.addAttribute("pendingBudgetRequestsCount", budgetService.getPendingRequests().size());

        return "coordinator/dashboard";
    }
}
