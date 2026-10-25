package vn.edu.cuunan.controller;

import org.springframework.beans.factory.annotation.Value;
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
    public String home(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("pageTitle", "CứuNạn 24/7 - Hệ Thống Cứu Hộ & Cứu Nạn Khẩn Cấp");
        model.addAttribute("rescueHotline", rescueHotline);
        model.addAttribute("fireHotline", fireHotline);
        model.addAttribute("medicalHotline", medicalHotline);
        return "index";
    }

    @GetMapping("/sos")
    public String sosQuickRequest(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("pageTitle", "Gửi Tín Hiệu Cứu Nạn Khẩn Cấp (SOS)");
        model.addAttribute("rescueHotline", rescueHotline);
        return "sos/request";
    }

    @GetMapping("/map")
    public String liveMap(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("pageTitle", "Bản Đồ Cứu Nạn Trực Tuyến & Cảnh Báo Thiên Tai");
        return "map/live-map";
    }
}
