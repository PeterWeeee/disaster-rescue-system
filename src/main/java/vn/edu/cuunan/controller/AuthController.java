package vn.edu.cuunan.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Value("${app.name:CứuNạn 24/7}")
    private String appName;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("pageTitle", "Đăng Nhập - CứuNạn 24/7");
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("pageTitle", "Đăng Ký Tài Khoản - CứuNạn 24/7");
        return "auth/register";
    }
}
