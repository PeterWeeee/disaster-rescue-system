package vn.edu.cuunan.security;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import vn.edu.cuunan.dto.LoginRequestDto;
import vn.edu.cuunan.dto.RegisterRequestDto;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.UserRepository;
import vn.edu.cuunan.service.AuthService;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Test
    @DisplayName("DoD 1: Trang chủ và các trang công khai truy cập không cần đăng nhập")
    void testPublicEndpoints() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DoD 2: Truy cập trang bảo vệ khi chưa đăng nhập -> chuyển hướng về /auth/login")
    void testUnauthenticatedRedirect() throws Exception {
        mockMvc.perform(get("/coordinator/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?error=unauthorized"));

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DoD 3: Đăng ký người dân mới thành công -> mật khẩu được băm BCrypt")
    void testRegisterCitizen() {
        String testUser = "citizen_test_" + System.currentTimeMillis();
        String testPhone = "098" + (System.currentTimeMillis() % 10000000);

        RegisterRequestDto dto = new RegisterRequestDto(
                "Người Dân Kiểm Thử",
                testUser,
                testPhone,
                "secret123",
                "test@gmail.com",
                "Đà Nẵng",
                "ROLE_CITIZEN"
        );

        User registered = authService.register(dto);
        assertNotNull(registered.getId());
        assertNotEquals("secret123", registered.getPassword());
        assertTrue(registered.getPassword().startsWith("$2a$") || registered.getPassword().startsWith("$2b$"));
        assertEquals("ROLE_CITIZEN", registered.getRole().getName());
    }

    @Test
    @DisplayName("DoD 4: Đăng nhập thành công -> Cấp phát JWT token hợp lệ")
    void testLoginSuccess() {
        // Tài khoản 'coordinator' tạo sẵn từ DataInitializer
        LoginRequestDto loginDto = new LoginRequestDto("coordinator", "123456");
        var response = authService.authenticate(loginDto);

        assertNotNull(response.getToken());
        assertEquals("coordinator", response.getUsername());
        assertEquals("ROLE_COORDINATOR", response.getRole());
        assertTrue(tokenProvider.validateToken(response.getToken()));
    }

    @Test
    @DisplayName("DoD 5: Tài khoản CITIZEN truy cập trang COORDINATOR -> Bị chặn 403 Forbidden")
    void testCitizenForbiddenOnCoordinatorDashboard() throws Exception {
        String citizenToken = tokenProvider.generateToken("citizen", "ROLE_CITIZEN", "Nguyễn Văn Dân");

        mockMvc.perform(get("/coordinator/dashboard")
                        .cookie(new Cookie("jwt_token", citizenToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DoD 6: Tài khoản COORDINATOR có JWT hợp lệ truy cập Dashboard thành công")
    void testCoordinatorAccessDashboardSuccess() throws Exception {
        String coordToken = tokenProvider.generateToken("coordinator", "ROLE_COORDINATOR", "Điều Phối Viên");

        mockMvc.perform(get("/coordinator/dashboard")
                        .cookie(new Cookie("jwt_token", coordToken)))
                .andExpect(status().isOk());
    }
}
