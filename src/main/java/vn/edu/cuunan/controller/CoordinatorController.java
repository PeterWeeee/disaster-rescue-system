package vn.edu.cuunan.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.cuunan.repository.RescueTeamRepository;
import vn.edu.cuunan.service.CoordinatorService;

@Controller
@RequestMapping("/coordinator")
@PreAuthorize("hasAnyRole('COORDINATOR', 'ADMIN')")
public class CoordinatorController {

    private final CoordinatorService coordinatorService;
    private final RescueTeamRepository rescueTeamRepository;

    public CoordinatorController(CoordinatorService coordinatorService,
                                 RescueTeamRepository rescueTeamRepository) {
        this.coordinatorService = coordinatorService;
        this.rescueTeamRepository = rescueTeamRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
                            Model model) {
        model.addAttribute("pageTitle", "Trung Tâm Điều Phối Cứu Nạn - CứuNạn 24/7");
        model.addAttribute("stats", coordinatorService.getDashboardStats());
        model.addAttribute("sosList", coordinatorService.getSosRequests(status));
        model.addAttribute("currentStatusFilter", status);
        model.addAttribute("rescueTeams", rescueTeamRepository.findAll());
        return "coordinator/dashboard";
    }
}
