package vn.edu.cuunan.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.cuunan.service.BudgetService;
import vn.edu.cuunan.service.CoordinatorService;
import java.util.List;
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

    /**
     * 1. Trang Chính: Bản đồ tác chiến trực tiếp & Danh sách các ca SOS cần xử lý.
     */
    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
                            @RequestParam(value = "province", required = false, defaultValue = "ALL") String province,
                            Model model) {
        String cleanProvince = (province == null || province.isBlank()) ? "ALL" : province.trim().toUpperCase();
        String cleanStatus = (status == null || status.isBlank()) ? "ALL" : status.trim().toUpperCase();

        ProvinceRegion selectedRegion = ProvinceRegion.fromCode(cleanProvince);

        model.addAttribute("pageTitle", "Tổng Quan Điều Phối Cứu Nạn - " + selectedRegion.getName());
        model.addAttribute("currentStatusFilter", cleanStatus);
        model.addAttribute("currentProvince", selectedRegion.getCode());
        model.addAttribute("selectedRegion", selectedRegion);
        model.addAttribute("provinces", ProvinceRegion.values());

        model.addAttribute("stats", coordinatorService.getDashboardStats(cleanProvince));
        model.addAttribute("sosList", coordinatorService.getSosRequests(cleanStatus, cleanProvince));
        model.addAttribute("sosMapPins", coordinatorService.getSosMapPins(cleanStatus, cleanProvince));
        model.addAttribute("teamMapPins", coordinatorService.getRescueTeamMapPins(cleanProvince));

        return "coordinator/dashboard";
    }

    /**
     * 2. Trang Quản Lý Đội Cứu Hộ: Danh sách, quân số, trang bị, ngân sách tác chiến và tình trạng trực chiến.
     */
    @GetMapping("/teams")
    public String teams(@RequestParam(value = "province", required = false, defaultValue = "ALL") String province,
                        Model model) {
        String cleanProvince = (province == null || province.isBlank()) ? "ALL" : province.trim().toUpperCase();
        ProvinceRegion selectedRegion = ProvinceRegion.fromCode(cleanProvince);

        model.addAttribute("pageTitle", "Quản Lý Đội Cứu Hộ - " + selectedRegion.getName());
        model.addAttribute("currentProvince", selectedRegion.getCode());
        model.addAttribute("selectedRegion", selectedRegion);
        model.addAttribute("provinces", ProvinceRegion.values());

        model.addAttribute("stats", coordinatorService.getDashboardStats(cleanProvince));
        model.addAttribute("rescueTeams", coordinatorService.getRescueTeams(cleanProvince));

        return "coordinator/teams";
    }

    /**
     * 3. Trang Theo Dõi Cứu Hộ & Lộ Trình: Giám sát toàn bộ các nhiệm vụ điều động, lộ trình di chuyển và hiện trường.
     */
    @GetMapping("/missions")
    public String missions(@RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
                           @RequestParam(value = "province", required = false, defaultValue = "ALL") String province,
                           Model model) {
        String cleanProvince = (province == null || province.isBlank()) ? "ALL" : province.trim().toUpperCase();
        String cleanStatus = (status == null || status.isBlank()) ? "ALL" : status.trim().toUpperCase();
        ProvinceRegion selectedRegion = ProvinceRegion.fromCode(cleanProvince);

        model.addAttribute("pageTitle", "Theo Dõi Cứu Hộ & Lộ Trình - " + selectedRegion.getName());
        model.addAttribute("currentStatusFilter", cleanStatus);
        model.addAttribute("currentProvince", selectedRegion.getCode());
        model.addAttribute("selectedRegion", selectedRegion);
        model.addAttribute("provinces", ProvinceRegion.values());

        model.addAttribute("stats", coordinatorService.getDashboardStats(cleanProvince));
        model.addAttribute("assignments", coordinatorService.getAssignments(cleanStatus, cleanProvince));
        model.addAttribute("sosMapPins", coordinatorService.getSosMapPins("ALL", cleanProvince));
        model.addAttribute("teamMapPins", coordinatorService.getRescueTeamMapPins(cleanProvince));

        return "coordinator/missions";
    }

    /**
     * 4. Trang Quỹ Cứu Trợ & Ngân Sách: Minh bạch Quỹ Cứu Trợ Tổ Chức và Phê duyệt tiếp tế kinh phí cho các đội.
     */
    @GetMapping("/budget")
    public String budget(@RequestParam(value = "province", required = false, defaultValue = "ALL") String province,
                         Model model) {
        String cleanProvince = (province == null || province.isBlank()) ? "ALL" : province.trim().toUpperCase();
        ProvinceRegion selectedRegion = ProvinceRegion.fromCode(cleanProvince);

        model.addAttribute("pageTitle", "Quản Lý Quỹ Cứu Trợ & Tiếp Tế Ngân Sách");
        model.addAttribute("currentProvince", selectedRegion.getCode());
        model.addAttribute("selectedRegion", selectedRegion);
        model.addAttribute("provinces", ProvinceRegion.values());

        model.addAttribute("generalFund", budgetService.getOrganizationGeneralFund());
        model.addAttribute("budgetRequests", budgetService.getAllRequests());
        model.addAttribute("pendingBudgetRequestsCount", budgetService.getPendingRequests().size());
        model.addAttribute("rescueTeams", coordinatorService.getRescueTeams(cleanProvince));

        return "coordinator/budget";
    }
    /**
     * 5. Trang Lịch Sử Điều Phối: Toàn bộ lịch sử các ca điều phối và nhiệm vụ cứu nạn đã hoàn tất.
     */
    @GetMapping("/history")
    public String history(@RequestParam(value = "province", required = false, defaultValue = "ALL") String province,
                          Model model) {
        String cleanProvince = (province == null || province.isBlank()) ? "ALL" : province.trim().toUpperCase();
        ProvinceRegion selectedRegion = ProvinceRegion.fromCode(cleanProvince);

        model.addAttribute("pageTitle", "Lịch Sử Điều Phối Cứu Nạn - " + selectedRegion.getName());
        model.addAttribute("currentProvince", selectedRegion.getCode());
        model.addAttribute("selectedRegion", selectedRegion);
        model.addAttribute("provinces", ProvinceRegion.values());

        model.addAttribute("stats", coordinatorService.getDashboardStats(cleanProvince));
        model.addAttribute("completedAssignments", coordinatorService.getAssignments("COMPLETED", cleanProvince));
        model.addAttribute("resolvedSosList", coordinatorService.getSosRequests("RESOLVED", cleanProvince));

        return "coordinator/history";
    }


}
