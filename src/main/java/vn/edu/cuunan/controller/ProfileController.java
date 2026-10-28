package vn.edu.cuunan.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.cuunan.security.CustomUserDetails;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @GetMapping
    public String profile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("pageTitle", "Hồ Sơ Cá Nhân - CứuNạn 24/7");
        model.addAttribute("user", userDetails != null ? userDetails.getUser() : null);
        return "profile/index";
    }
}
