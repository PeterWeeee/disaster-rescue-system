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
                            @RequestParam(value = "registered", required = false) String registered,
                            @RequestParam(value = "passwordReset", required = false) String passwordReset) {
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
            model.addAttribute("successMessage", "Kích hoạt tài khoản thành công! Vui lòng đăng nhập.");
        }
        if (passwordReset != null) {
            model.addAttribute("successMessage", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập với mật khẩu mới.");
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
            // Đăng ký và sinh mã OTP gửi về Email
            authService.register(registerDto, true);

            boolean realSent = authService.isLastEmailSentReal();
            if (realSent) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Mã OTP xác thực 6 chữ số đã được gửi đến hộp thư Gmail " + registerDto.getEmail() + ". Vui lòng kiểm tra hộp thư đến (và thư rác/spam) để kích hoạt tài khoản!");
            } else {
                String devOtp = authService.getLatestOtp(registerDto.getEmail(), "REGISTRATION").orElse("");
                String smtpError = authService.getLastError();
                redirectAttributes.addFlashAttribute("devOtpCode", devOtp);
                redirectAttributes.addFlashAttribute("smtpError", smtpError);
                if (smtpError != null && !smtpError.isBlank()) {
                    redirectAttributes.addFlashAttribute("warningMessage",
                            "Không thể gửi qua Gmail SMTP (" + smtpError + "). Đã cấp mã OTP kích hoạt dự phòng bên dưới.");
                } else {
                    redirectAttributes.addFlashAttribute("successMessage",
                            "Mã OTP kích hoạt tài khoản của bạn là: " + devOtp);
                }
            }
            return "redirect:/auth/verify-otp?email=" + registerDto.getEmail();
        } catch (Exception ex) {
            log.warn("Đăng ký tài khoản thất bại: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/auth/register";
        }
    }

    @GetMapping("/verify-otp")
    public String verifyOtpPage(@RequestParam(value = "email", required = false) String email,
                                @RequestParam(value = "resend", required = false) String resend,
                                Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("pageTitle", "Xác Thực Kích Hoạt Tài Khoản - CứuNạn 24/7");
        model.addAttribute("email", email);
        if (resend != null) {
            model.addAttribute("successMessage", "Đã gửi lại mã OTP mới đến email của bạn.");
        }
        if (!model.containsAttribute("devOtpCode") && email != null && !authService.isLastEmailSentReal()) {
            authService.getLatestOtp(email, "REGISTRATION").ifPresent(otp -> model.addAttribute("devOtpCode", otp));
            String smtpErr = authService.getLastError();
            if (smtpErr != null && !smtpErr.isBlank()) {
                model.addAttribute("smtpError", smtpErr);
            }
        }
        return "auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String handleVerifyOtp(@RequestParam("email") String email,
                                  @RequestParam("otpCode") String otpCode,
                                  RedirectAttributes redirectAttributes) {
        try {
            authService.activateAccount(email, otpCode);
            redirectAttributes.addFlashAttribute("successMessage", "Kích hoạt tài khoản thành công! Bạn có thể đăng nhập ngay.");
            return "redirect:/auth/login?registered=true";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/auth/verify-otp?email=" + email;
        }
    }

    @PostMapping("/resend-otp")
    public String handleResendOtp(@RequestParam("email") String email,
                                  RedirectAttributes redirectAttributes) {
        try {
            authService.resendRegistrationOtp(email);
            boolean realSent = authService.isLastEmailSentReal();
            if (realSent) {
                redirectAttributes.addFlashAttribute("successMessage", "Đã gửi lại mã OTP mới đến hộp thư Gmail " + email + ". Vui lòng kiểm tra hộp thư!");
            } else {
                String devOtp = authService.getLatestOtp(email, "REGISTRATION").orElse("");
                String smtpError = authService.getLastError();
                redirectAttributes.addFlashAttribute("devOtpCode", devOtp);
                redirectAttributes.addFlashAttribute("smtpError", smtpError);
                if (smtpError != null && !smtpError.isBlank()) {
                    redirectAttributes.addFlashAttribute("warningMessage", "Chưa thể gửi qua Gmail SMTP (" + smtpError + "). Đã cấp mã OTP mới bên dưới.");
                } else {
                    redirectAttributes.addFlashAttribute("successMessage", "Mã OTP mới của bạn là: " + devOtp);
                }
            }
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/auth/verify-otp?email=" + email;
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("pageTitle", "Quên Mật Khẩu - CứuNạn 24/7");
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email,
                                       RedirectAttributes redirectAttributes) {
        try {
            authService.initiateForgotPassword(email);
            boolean realSent = authService.isLastEmailSentReal();
            if (realSent) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Mã OTP khôi phục mật khẩu đã được gửi đến email " + email + ". Vui lòng kiểm tra hộp thư!");
            } else {
                String devOtp = authService.getLatestOtp(email, "FORGOT_PASSWORD").orElse("");
                redirectAttributes.addFlashAttribute("devOtpCode", devOtp);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Chế độ Thử Nghiệm (Dev): Mã OTP đặt lại mật khẩu là: " + devOtp);
            }
            return "redirect:/auth/reset-password?email=" + email;
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/auth/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(value = "email", required = false) String email,
                                    Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("pageTitle", "Đặt Lại Mật Khẩu Mới - CứuNạn 24/7");
        model.addAttribute("email", email);
        if (!model.containsAttribute("devOtpCode") && email != null && !authService.isLastEmailSentReal()) {
            authService.getLatestOtp(email, "FORGOT_PASSWORD").ifPresent(otp -> model.addAttribute("devOtpCode", otp));
        }
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam("email") String email,
                                      @RequestParam("otpCode") String otpCode,
                                      @RequestParam("newPassword") String newPassword,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      RedirectAttributes redirectAttributes) {
        try {
            if (!newPassword.equals(confirmPassword)) {
                throw new IllegalArgumentException("Mật khẩu xác nhận không khớp!");
            }
            authService.resetPassword(email, otpCode, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập với mật khẩu mới.");
            return "redirect:/auth/login?passwordReset=true";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/auth/reset-password?email=" + email;
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
