package vn.edu.cuunan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.edu.cuunan.dto.DisasterGuideDto;
import vn.edu.cuunan.service.DisasterGuideService;

import java.util.List;
import java.util.Optional;

@Controller
public class DisasterGuideController {

    private final DisasterGuideService disasterGuideService;

    public DisasterGuideController(DisasterGuideService disasterGuideService) {
        this.disasterGuideService = disasterGuideService;
    }

    @GetMapping("/guide/disaster")
    public String disasterGuide(Model model) {
        List<DisasterGuideDto> guides = disasterGuideService.getAllGuides();
        model.addAttribute("guides", guides);
        model.addAttribute("pageTitle", "Cẩm nang ứng phó thiên tai – Cứu Nạn 24/7");
        return "guide/disaster-guide";
    }

    @GetMapping({"/guide/disaster/{slug}", "/guide/disaster/detail/{slug}"})
    public String disasterDetail(@PathVariable String slug, Model model) {
        Optional<DisasterGuideDto> guideOpt = disasterGuideService.getGuideBySlug(slug);
        if (guideOpt.isEmpty()) {
            return "redirect:/guide/disaster";
        }

        DisasterGuideDto guide = guideOpt.get();
        List<DisasterGuideDto> allGuides = disasterGuideService.getAllGuides();
        DisasterGuideDto prevGuide = disasterGuideService.getPreviousGuide(slug);
        DisasterGuideDto nextGuide = disasterGuideService.getNextGuide(slug);

        model.addAttribute("guide", guide);
        model.addAttribute("allGuides", allGuides);
        model.addAttribute("prevGuide", prevGuide);
        model.addAttribute("nextGuide", nextGuide);
        model.addAttribute("pageTitle", "Chi tiết: " + guide.getTitle() + " – Cẩm nang Cứu Nạn 24/7");

        return "guide/disaster-detail";
    }
}
