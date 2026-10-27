package vn.edu.cuunan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.cuunan.dto.AuthResponseDto;
import vn.edu.cuunan.dto.LoginRequestDto;
import vn.edu.cuunan.dto.RegisterRequestDto;
import vn.edu.cuunan.entity.Role;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.RoleRepository;
import vn.edu.cuunan.repository.UserRepository;
import vn.edu.cuunan.security.JwtTokenProvider;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private OtpService otpService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;
    private Role citizenRole;

    @BeforeEach
    void setUp() {
        citizenRole = new Role(1, "ROLE_CITIZEN");
        sampleUser = User.builder()
                .id(10L)
                .username("thinh")
                .password("$2a$10$hashedpassword")
                .fullName("Võ Văn Thịnh")
                .phone("0987654321")
                .email("thinh@gmail.com")
                .role(citizenRole)
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("Đăng nhập thành công với đúng tài khoản và mật khẩu")
    void testAuthenticateSuccess() {
        LoginRequestDto loginDto = new LoginRequestDto("thinh", "password123");

        when(userRepository.findByUsername("thinh")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("password123", "$2a$10$hashedpassword")).thenReturn(true);
        when(tokenProvider.generateToken("thinh", "ROLE_CITIZEN", "Võ Văn Thịnh")).thenReturn("fake.jwt.token");

        AuthResponseDto result = authService.authenticate(loginDto);

        assertNotNull(result);
        assertEquals("fake.jwt.token", result.getToken());
        assertEquals("thinh", result.getUsername());
        assertEquals("ROLE_CITIZEN", result.getRole());
    }

    @Test
    @DisplayName("Đăng nhập thất bại khi sai mật khẩu ném BadCredentialsException")
    void testAuthenticateWrongPassword() {
        LoginRequestDto loginDto = new LoginRequestDto("thinh", "wrongpass");

        when(userRepository.findByUsername("thinh")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrongpass", "$2a$10$hashedpassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(loginDto));
    }

    @Test
    @DisplayName("Đăng ký tài khoản mới thành công")
    void testRegisterSuccess() {
        RegisterRequestDto regDto = new RegisterRequestDto(
                "Nguyễn Văn B",
                "nguyenvanb",
                "0912345678",
                "rawpass",
                "b@gmail.com",
                "Đà Nẵng",
                "ROLE_CITIZEN"
        );

        when(userRepository.existsByUsername("nguyenvanb")).thenReturn(false);
        when(userRepository.existsByPhone("0912345678")).thenReturn(false);
        when(roleRepository.findByName("ROLE_CITIZEN")).thenReturn(Optional.of(citizenRole));
        when(passwordEncoder.encode("rawpass")).thenReturn("$2a$10$encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(99L);
            return u;
        });

        User registered = authService.register(regDto);

        assertNotNull(registered);
        assertEquals("nguyenvanb", registered.getUsername());
        assertEquals("$2a$10$encoded", registered.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Đăng ký thất bại khi trùng tên đăng nhập ném IllegalArgumentException")
    void testRegisterDuplicateUsername() {
        RegisterRequestDto regDto = new RegisterRequestDto(
                "Nguyễn Văn B",
                "thinh",
                "0912345678",
                "rawpass",
                "b@gmail.com",
                "Đà Nẵng",
                "ROLE_CITIZEN"
        );

        when(userRepository.existsByUsername("thinh")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(regDto));
    }

    @Test
    @DisplayName("Đăng ký với OTP tạo user trạng thái PENDING_ACTIVATION và gọi generateAndSendOtp")
    void testRegisterWithOtpSuccess() {
        RegisterRequestDto regDto = new RegisterRequestDto(
                "Trần Văn C",
                "tranvanc",
                "0933333333",
                "pass123",
                "c@gmail.com",
                "Huế",
                "ROLE_CITIZEN"
        );

        when(userRepository.existsByUsername("tranvanc")).thenReturn(false);
        when(userRepository.existsByPhone("0933333333")).thenReturn(false);
        when(userRepository.existsByEmail("c@gmail.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_CITIZEN")).thenReturn(Optional.of(citizenRole));
        when(passwordEncoder.encode("pass123")).thenReturn("$2a$10$encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User user = authService.register(regDto, true);

        assertNotNull(user);
        assertEquals("PENDING_ACTIVATION", user.getStatus());
        verify(otpService, times(1)).generateAndSendOtp("c@gmail.com", "REGISTRATION");
    }

    @Test
    @DisplayName("Kích hoạt tài khoản thành công khi mã OTP hợp lệ")
    void testActivateAccountSuccess() {
        User pendingUser = User.builder()
                .id(11L)
                .username("pendinguser")
                .email("pending@gmail.com")
                .status("PENDING_ACTIVATION")
                .build();

        when(otpService.verifyOtp("pending@gmail.com", "123456", "REGISTRATION")).thenReturn(true);
        when(userRepository.findByEmail("pending@gmail.com")).thenReturn(Optional.of(pendingUser));

        assertDoesNotThrow(() -> authService.activateAccount("pending@gmail.com", "123456"));
        assertEquals("ACTIVE", pendingUser.getStatus());
        verify(userRepository, times(1)).save(pendingUser);
    }

    @Test
    @DisplayName("Đặt lại mật khẩu thành công khi OTP đúng")
    void testResetPasswordSuccess() {
        when(otpService.verifyOtp("thinh@gmail.com", "654321", "FORGOT_PASSWORD")).thenReturn(true);
        when(userRepository.findByEmail("thinh@gmail.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.encode("newsecretpass")).thenReturn("$2a$10$newhash");

        assertDoesNotThrow(() -> authService.resetPassword("thinh@gmail.com", "654321", "newsecretpass"));
        assertEquals("$2a$10$newhash", sampleUser.getPassword());
        verify(userRepository, times(1)).save(sampleUser);
    }
}
