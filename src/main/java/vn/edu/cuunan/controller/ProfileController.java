package vn.edu.cuunan.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.cuunan.entity.Donation;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.DonationRepository;
import vn.edu.cuunan.repository.SosRequestRepository;
import vn.edu.cuunan.security.CustomUserDetails;
import vn.edu.cuunan.service.OtpService;
import vn.edu.cuunan.service.UserService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final SosRequestRepository sosRequestRepository;
    private final DonationRepository donationRepository;
    private final OtpService otpService;

    public ProfileController(UserService userService,
                             SosRequestRepository sosRequestRepository,
                             DonationRepository donationRepository,
                             OtpService otpService) {
        this.userService = userService;
        this.sosRequestRepository = sosRequestRepository;
        this.donationRepository = donationRepository;
        this.otpService = otpService;
    }

    @GetMapping
    public String profile(@AuthenticationPrincipal CustomUserDetails userDetails,
                          @RequestParam(value = "tab", required = false, defaultValue = "info") String activeTab,
                          Model model) {
        if (userDetails == null || userDetails.getUser() == null) {
            return "redirect:/auth/login";
        }

        User user = userService.findById(userDetails.getUser().getId())
                .orElse(userDetails.getUser());

        List<SosRequest> sosList = sosRequestRepository.findBySenderIdOrderByCreatedAtDesc(user.getId());
        List<Donation> donationList = user.getPhone() != null
                ? donationRepository.findByDonorPhoneOrderByCreatedAtDesc(user.getPhone())
                : Collections.emptyList();

        model.addAttribute("pageTitle", "Hồ Sơ Cá Nhân & Quản Trị Danh Tính - CứuNạn 24/7");
        model.addAttribute("user", user);
        model.addAttribute("sosList", sosList);
        model.addAttribute("donationList", donationList);
        model.addAttribute("activeTab", activeTab);

        return "profile/index";
    }

    @PostMapping("/update")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @RequestParam("fullName") String fullName,
                                @RequestParam("phone") String phone,
                                @RequestParam(value = "email", required = false) String email,
                                @RequestParam(value = "emailOtp", required = false) String emailOtp,
                                @RequestParam(value = "address", required = false) String address,
                                @RequestParam(value = "avatarUrl", required = false) String avatarUrl,
                                RedirectAttributes redirectAttributes) {
        if (userDetails == null || userDetails.getUser() == null) {
            return "redirect:/auth/login";
        }

        try {
            User currentUser = userService.findById(userDetails.getUser().getId())
                    .orElse(userDetails.getUser());
            String oldEmail = currentUser.getEmail() != null ? currentUser.getEmail().trim().toLowerCase() : "";
            String newEmail = email != null ? email.trim().toLowerCase() : "";

            // Nếu người dùng thay đổi email sang một địa chỉ mới khác với địa chỉ hiện tại
            boolean emailChanged = StringUtils.hasText(newEmail) && !newEmail.equalsIgnoreCase(oldEmail);
            if (emailChanged) {
                if (!StringUtils.hasText(emailOtp)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Bạn đã thay đổi email. Vui lòng bấm 'Gửi OTP' và nhập mã xác thực gồm 6 số gửi đến email mới để xác nhận.");
                    return "redirect:/profile?tab=edit";
                }
                boolean otpValid = otpService.verifyOtp(newEmail, emailOtp.trim(), "CHANGE_EMAIL");
                if (!otpValid) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Mã OTP xác thực email không chính xác hoặc đã hết hạn!");
                    return "redirect:/profile?tab=edit";
                }
            }

            userService.updateProfile(userDetails.getUser().getId(), fullName, phone, email, address, avatarUrl);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin hồ sơ cá nhân thành công!");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi cập nhật hồ sơ: " + ex.getMessage());
        }

        return "redirect:/profile?tab=edit";
    }

    @PostMapping("/send-email-otp")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendEmailOtp(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("email") String email) {
        Map<String, Object> response = new HashMap<>();
        if (userDetails == null || userDetails.getUser() == null) {
            response.put("success", false);
            response.put("message", "Vui lòng đăng nhập để thực hiện chức năng này!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if (!StringUtils.hasText(email)) {
            response.put("success", false);
            response.put("message", "Vui lòng nhập địa chỉ email mới!");
            return ResponseEntity.badRequest().body(response);
        }

        String targetEmail = email.trim().toLowerCase();
        if (!targetEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            response.put("success", false);
            response.put("message", "Địa chỉ email không đúng định dạng!");
            return ResponseEntity.badRequest().body(response);
        }

        // Kiểm tra xem email có thuộc tài khoản khác không
        Optional<User> existingUser = userService.findByEmail(targetEmail);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(userDetails.getUser().getId())) {
            response.put("success", false);
            response.put("message", "Địa chỉ email này đã được sử dụng bởi một tài khoản khác!");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            otpService.generateAndSendOtp(targetEmail, "CHANGE_EMAIL");
            response.put("success", true);
            response.put("message", "Mã xác thực OTP gồm 6 chữ số đã được gửi tới " + targetEmail + ". Vui lòng kiểm tra hộp thư!");
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            response.put("success", false);
            response.put("message", "Không thể gửi mã OTP: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        if (userDetails == null || userDetails.getUser() == null) {
            return "redirect:/auth/login";
        }

        try {
            userService.changePassword(userDetails.getUser().getId(), currentPassword, newPassword, confirmPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công! Hãy ghi nhớ mật khẩu mới của bạn.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi đổi mật khẩu: " + ex.getMessage());
        }

        return "redirect:/profile?tab=security";
    }
}
