package vn.edu.cuunan.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rescuer")
@PreAuthorize("hasAnyRole('RESCUER', 'ADMIN')")
public class RescuerController {

    @GetMapping("/missions")
    public String missions(Model model) {
        model.addAttribute("pageTitle", "Tác Nghiệp Cứu Hộ - CứuNạn 24/7");
        return "rescuer/missions";
    }
}
