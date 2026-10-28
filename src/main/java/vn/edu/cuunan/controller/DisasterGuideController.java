package vn.edu.cuunan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DisasterGuideController {

    @GetMapping("/guide/disaster")
    public String disasterGuide(Model model) {
        model.addAttribute("pageTitle", "Cẩm nang ứng phó thiên tai – Cứu Nạn 24/7");
        return "guide/disaster-guide";
    }
}
