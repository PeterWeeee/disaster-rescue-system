package vn.edu.cuunan.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Value("${app.name:CứuNạn 24/7}")
    private String appName;

    @Value("${app.hotlines.rescue:112}")
    private String rescueHotline;

    @Value("${app.hotlines.fire:114}")
    private String fireHotline;

    @Value("${app.hotlines.medical:115}")
    private String medicalHotline;

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        // Nếu người dùng đã đăng nhập với vai trò Điều phối viên, chuyển hướng thẳng vào Bàn điều phối
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            boolean isCoordinator = authentication.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_COORDINATOR".equals(a.getAuthority()));
            if (isCoordinator) {
                return "redirect:/coordinator/dashboard";
            }
        }

        model.addAttribute("appName", appName);
        model.addAttribute("pageTitle", "CứuNạn 24/7 - Hệ Thống Cứu Hộ & Cứu Nạn Khẩn Cấp");
        model.addAttribute("rescueHotline", rescueHotline);
        model.addAttribute("fireHotline", fireHotline);
        model.addAttribute("medicalHotline", medicalHotline);
        return "index";
    }

    @GetMapping("/map")
    public String liveMap(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("pageTitle", "Bản Đồ Cứu Nạn Trực Tuyến & Cảnh Báo Thiên Tai");
        return "map/live-map";
    }

    @GetMapping({"/donate", "/donate/**"})
    public String donateRedirect() {
        return "redirect:/donations";
    }
}
