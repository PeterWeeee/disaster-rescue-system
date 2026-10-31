package vn.edu.cuunan.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.UserRepository;
import vn.edu.cuunan.security.CustomUserDetails;
import vn.edu.cuunan.service.OtpService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Test
    @DisplayName("GET /profile khi chưa đăng nhập sẽ chuyển hướng về trang login")
    void testProfileUnauthenticated() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?error=unauthorized"));
    }

    @Test
    @DisplayName("GET /profile khi đã đăng nhập trả về mã 200 và model hợp lệ")
    void testProfileAuthenticated() throws Exception {
        User citizen = userRepository.findByUsername("citizen")
                .orElseGet(() -> userRepository.findAll().get(0));
        CustomUserDetails userDetails = new CustomUserDetails(citizen);

        mockMvc.perform(get("/profile").with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/index"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("sosList"))
                .andExpect(model().attributeExists("donationList"));
    }

    @Test
    @DisplayName("POST /profile/update cập nhật thành công khi email không đổi")
    void testUpdateProfileSuccess() throws Exception {
        User citizen = userRepository.findByUsername("citizen")
                .orElseGet(() -> userRepository.findAll().get(0));
        CustomUserDetails userDetails = new CustomUserDetails(citizen);

        mockMvc.perform(post("/profile/update")
                        .with(user(userDetails))
                        .with(csrf())
                        .param("fullName", "Nguyễn Văn Đã Cập Nhật")
                        .param("phone", citizen.getPhone())
                        .param("email", citizen.getEmail() != null ? citizen.getEmail() : "")
                        .param("address", "TP. Đà Nẵng")
                        .param("avatarUrl", "https://example.com/avatar.jpg"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?tab=edit"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("POST /profile/update cập nhật thành công email mới khi có OTP hợp lệ")
    void testUpdateProfileWithNewEmailAndOtp() throws Exception {
        User citizen = userRepository.findByUsername("citizen")
                .orElseGet(() -> userRepository.findAll().get(0));
        CustomUserDetails userDetails = new CustomUserDetails(citizen);

        String newEmail = "new.email." + System.currentTimeMillis() + "@cuunan247.vn";
        String otp = otpService.generateAndSendOtp(newEmail, "CHANGE_EMAIL");

        mockMvc.perform(post("/profile/update")
                        .with(user(userDetails))
                        .with(csrf())
                        .param("fullName", "Nguyễn Văn Đã Cập Nhật")
                        .param("phone", citizen.getPhone())
                        .param("email", newEmail)
                        .param("emailOtp", otp)
                        .param("address", "TP. Đà Nẵng")
                        .param("avatarUrl", "https://example.com/avatar.jpg"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?tab=edit"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("POST /profile/update thất bại khi thay đổi email mà không nhập OTP")
    void testUpdateProfileWithNewEmailWithoutOtp() throws Exception {
        User citizen = userRepository.findByUsername("citizen")
                .orElseGet(() -> userRepository.findAll().get(0));
        CustomUserDetails userDetails = new CustomUserDetails(citizen);

        String newEmail = "new.email.no.otp@cuunan247.vn";

        mockMvc.perform(post("/profile/update")
                        .with(user(userDetails))
                        .with(csrf())
                        .param("fullName", "Nguyễn Văn Đã Cập Nhật")
                        .param("phone", citizen.getPhone())
                        .param("email", newEmail)
                        .param("address", "TP. Đà Nẵng")
                        .param("avatarUrl", "https://example.com/avatar.jpg"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?tab=edit"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("POST /profile/send-email-otp gửi OTP thành công cho email hợp lệ")
    void testSendEmailOtp() throws Exception {
        User citizen = userRepository.findByUsername("citizen")
                .orElseGet(() -> userRepository.findAll().get(0));
        CustomUserDetails userDetails = new CustomUserDetails(citizen);

        mockMvc.perform(post("/profile/send-email-otp")
                        .with(user(userDetails))
                        .with(csrf())
                        .param("email", "otp.check@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /profile/change-password thành công với mật khẩu đúng")
    void testChangePasswordSuccess() throws Exception {
        User citizen = userRepository.findByUsername("citizen")
                .orElseGet(() -> userRepository.findAll().get(0));
        CustomUserDetails userDetails = new CustomUserDetails(citizen);

        mockMvc.perform(post("/profile/change-password")
                        .with(user(userDetails))
                        .with(csrf())
                        .param("currentPassword", "123456")
                        .param("newPassword", "MatKhauMoi@2026")
                        .param("confirmPassword", "MatKhauMoi@2026"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?tab=security"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("POST /profile/change-password báo lỗi khi sai mật khẩu hiện tại")
    void testChangePasswordWrongOldPassword() throws Exception {
        User citizen = userRepository.findByUsername("citizen")
                .orElseGet(() -> userRepository.findAll().get(0));
        CustomUserDetails userDetails = new CustomUserDetails(citizen);

        mockMvc.perform(post("/profile/change-password")
                        .with(user(userDetails))
                        .with(csrf())
                        .param("currentPassword", "SaiMatKhau123")
                        .param("newPassword", "MatKhauMoi@2026")
                        .param("confirmPassword", "MatKhauMoi@2026"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?tab=security"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}
