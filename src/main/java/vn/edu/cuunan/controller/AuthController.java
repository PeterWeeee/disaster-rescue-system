package vn.edu.cuunan.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.cuunan.dto.AuthResponseDto;
import vn.edu.cuunan.dto.LoginRequestDto;
import vn.edu.cuunan.dto.RegisterRequestDto;
import vn.edu.cuunan.security.JwtTokenProvider;
import vn.edu.cuunan.service.AuthService;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthService authService, JwtTokenProvider tokenProvider) {
        this.authService = authService;
        this.tokenProvider = tokenProvider;
    }

    @Value("${app.name:CứuNạn 24/7}")
    private String appName;

    @GetMapping("/login")
    public String loginPage(Model model,
                            @RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "registered", required = false) String registered) {
        model.addAttribute("appName", appName);
        model.addAttribute("pageTitle", "Đăng Nhập - CứuNạn 24/7");

        if (error != null) {
            if ("unauthorized".equals(error)) {
                model.addAttribute("errorMessage", "Bạn cần đăng nhập để truy cập tính năng này!");
            } else {
                model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không chính xác!");
            }
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Bạn đã đăng xuất tài khoản an toàn.");
        }
        if (registered != null) {
            model.addAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
        }

        return "auth/login";
    }

    @PostMapping("/login")
    public String handleLogin(@ModelAttribute LoginRequestDto loginDto,
                              HttpServletResponse response,
                              RedirectAttributes redirectAttributes) {
        try {
            AuthResponseDto authResponse = authService.authenticate(loginDto);

            // Lưu JWT vào HttpOnly Cookie
            Cookie jwtCookie = new Cookie(tokenProvider.getCookieName(), authResponse.getToken());
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(86400); // 24 giờ
            response.addCookie(jwtCookie);

            log.info("Người dùng '{}' đăng nhập thành công với vai trò: {}", authResponse.getUsername(), authResponse.getRole());

            // Điều hướng theo vai trò người dùng
            if ("ROLE_ADMIN".equals(authResponse.getRole())) {
                return "redirect:/admin/dashboard";
            } else if ("ROLE_COORDINATOR".equals(authResponse.getRole())) {
                return "redirect:/coordinator/dashboard";
            } else if ("ROLE_RESCUER".equals(authResponse.getRole())) {
                return "redirect:/rescuer/missions";
            } else {
                return "redirect:/";
            }

        } catch (Exception ex) {
            log.warn("Đăng nhập thất bại cho user: {} - Lý do: {}", loginDto.getUsername(), ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/auth/login?error=true";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("pageTitle", "Đăng Ký Tài Khoản - CứuNạn 24/7");
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute RegisterRequestDto registerDto,
                                 RedirectAttributes redirectAttributes) {
        try {
            authService.register(registerDto);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký tài khoản thành công! Bạn có thể đăng nhập ngay.");
            return "redirect:/auth/login?registered=true";
        } catch (Exception ex) {
            log.warn("Đăng ký tài khoản thất bại: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/auth/register";
        }
    }

    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public String handleLogout(HttpServletResponse response) {
        // Hủy bỏ cookie JWT
        Cookie jwtCookie = new Cookie(tokenProvider.getCookieName(), "");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        SecurityContextHolder.clearContext();
        return "redirect:/auth/login?logout=true";
    }

    @GetMapping("/forbidden")
    public String forbiddenPage(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("pageTitle", "403 Không Có Quyền Truy Cập");
        return "auth/forbidden";
    }
}
