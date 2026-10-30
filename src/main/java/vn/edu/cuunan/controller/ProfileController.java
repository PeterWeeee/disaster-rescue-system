package vn.edu.cuunan.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.cuunan.entity.Donation;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.DonationRepository;
import vn.edu.cuunan.repository.SosRequestRepository;
import vn.edu.cuunan.security.CustomUserDetails;
import vn.edu.cuunan.service.UserService;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final SosRequestRepository sosRequestRepository;
    private final DonationRepository donationRepository;

    public ProfileController(UserService userService,
                             SosRequestRepository sosRequestRepository,
                             DonationRepository donationRepository) {
        this.userService = userService;
        this.sosRequestRepository = sosRequestRepository;
        this.donationRepository = donationRepository;
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
                                @RequestParam(value = "address", required = false) String address,
                                @RequestParam(value = "avatarUrl", required = false) String avatarUrl,
                                RedirectAttributes redirectAttributes) {
        if (userDetails == null || userDetails.getUser() == null) {
            return "redirect:/auth/login";
        }

        try {
            userService.updateProfile(userDetails.getUser().getId(), fullName, phone, email, address, avatarUrl);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin hồ sơ cá nhân thành công!");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi cập nhật hồ sơ: " + ex.getMessage());
        }

        return "redirect:/profile?tab=edit";
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
