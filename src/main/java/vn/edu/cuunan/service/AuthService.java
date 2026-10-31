package vn.edu.cuunan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cuunan.dto.AuthResponseDto;
import vn.edu.cuunan.dto.LoginRequestDto;
import vn.edu.cuunan.dto.RegisterRequestDto;
import vn.edu.cuunan.entity.Role;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.RoleRepository;
import vn.edu.cuunan.repository.UserRepository;
import vn.edu.cuunan.security.JwtTokenProvider;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final OtpService otpService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this(userRepository, roleRepository, passwordEncoder, tokenProvider, null);
    }

    @Autowired
    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider,
                       @Autowired(required = false) OtpService otpService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.otpService = otpService;
    }

    @Transactional(readOnly = true)
    public AuthResponseDto authenticate(LoginRequestDto loginRequest) {
        String usernameOrPhone = loginRequest.getUsername().trim();
        User user = userRepository.findByUsername(usernameOrPhone)
                .or(() -> userRepository.findByPhone(usernameOrPhone))
                .orElseThrow(() -> new BadCredentialsException("Tên đăng nhập hoặc mật khẩu không chính xác!"));

        if ("BLOCKED".equalsIgnoreCase(user.getStatus())) {
            throw new BadCredentialsException("Tài khoản của bạn đã bị khóa! Vui lòng liên hệ ban quản trị.");
        }

        if ("PENDING_ACTIVATION".equalsIgnoreCase(user.getStatus())) {
            throw new BadCredentialsException("Tài khoản chưa được kích hoạt qua mã OTP Email! Vui lòng kích hoạt tài khoản trước khi đăng nhập.");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Tên đăng nhập hoặc mật khẩu không chính xác!");
        }

        String roleName = user.getRole() != null ? user.getRole().getName() : "ROLE_CITIZEN";
        String token = tokenProvider.generateToken(user.getUsername(), roleName, user.getFullName());

        return AuthResponseDto.builder()
                .token(token)
                .tokenType("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(roleName)
                .message("Đăng nhập thành công!")
                .build();
    }

    @Transactional
    public User register(RegisterRequestDto registerRequest) {
        return register(registerRequest, false);
    }

    @Transactional
    public User register(RegisterRequestDto registerRequest, boolean requireOtp) {
        String username = registerRequest.getUsername().trim().toLowerCase();
        String phone = registerRequest.getPhone().trim();
        String email = registerRequest.getEmail() != null ? registerRequest.getEmail().trim().toLowerCase() : null;

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Tên đăng nhập '" + username + "' đã được sử dụng!");
        }

        if (userRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("Số điện thoại '" + phone + "' đã được đăng ký tài khoản!");
        }

        if (email != null && !email.isEmpty() && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Địa chỉ email '" + email + "' đã được sử dụng!");
        }

        String requestedRole = registerRequest.getRole();
        if (requestedRole == null || (!requestedRole.equals("ROLE_RESCUER") && !requestedRole.equals("ROLE_CITIZEN"))) {
            requestedRole = "ROLE_CITIZEN";
        }

        final String targetRole = requestedRole;
        Role role = roleRepository.findByName(targetRole)
                .orElseGet(() -> roleRepository.save(Role.builder().name(targetRole).build()));

        String initialStatus = requireOtp ? "PENDING_ACTIVATION" : "ACTIVE";

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName().trim())
                .phone(phone)
                .email(email)
                .address(registerRequest.getAddress())
                .role(role)
                .status(initialStatus)
                .build();

        User savedUser = userRepository.save(user);

        if (requireOtp && email != null && !email.isEmpty() && otpService != null) {
            otpService.generateAndSendOtp(email, "REGISTRATION");
            log.info(">>> Đã gửi mã OTP đăng ký tới email: {}", email);
        }

        log.info(">>> Đã tạo mới tài khoản: id={}, username={}, status={}", savedUser.getId(), savedUser.getUsername(), savedUser.getStatus());
        return savedUser;
    }

    /**
     * Kích hoạt tài khoản bằng mã OTP gửi về Email.
     */
    @Transactional
    public void activateAccount(String email, String otpCode) {
        if (otpService == null) {
            throw new IllegalStateException("Dịch vụ OTP chưa sẵn sàng!");
        }

        boolean valid = otpService.verifyOtp(email, otpCode, "REGISTRATION");
        if (!valid) {
            throw new IllegalArgumentException("Mã OTP không chính xác hoặc đã hết hạn!");
        }

        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản gắn với email này!"));

        user.setStatus("ACTIVE");
        userRepository.save(user);
        log.info(">>> Tài khoản {} đã được kích hoạt thành công qua mã OTP!", user.getUsername());
    }

    /**
     * Gửi lại mã OTP kích hoạt tài khoản đăng ký.
     */
    @Transactional
    public void resendRegistrationOtp(String email) {
        if (otpService == null) {
            throw new IllegalStateException("Dịch vụ OTP chưa sẵn sàng!");
        }

        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản gắn với email này!"));

        if ("ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new IllegalStateException("Tài khoản này đã được kích hoạt trước đó rồi!");
        }

        otpService.generateAndSendOtp(email, "REGISTRATION");
    }

    /**
     * Khởi tạo quy trình quên mật khẩu: sinh OTP và gửi vào Email.
     */
    @Transactional
    public void initiateForgotPassword(String email) {
        if (otpService == null) {
            throw new IllegalStateException("Dịch vụ OTP chưa sẵn sàng!");
        }

        String normalizedEmail = email != null ? email.trim().toLowerCase() : "";
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản nào gắn với email '" + email + "'!"));

        otpService.generateAndSendOtp(normalizedEmail, "FORGOT_PASSWORD");
        log.info(">>> Đã gửi OTP đặt lại mật khẩu cho user: {}", user.getUsername());
    }

    /**
     * Đặt lại mật khẩu mới sau khi xác thực OTP thành công.
     */
    @Transactional
    public void resetPassword(String email, String otpCode, String newPassword) {
        if (otpService == null) {
            throw new IllegalStateException("Dịch vụ OTP chưa sẵn sàng!");
        }

        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new IllegalArgumentException("Mật khẩu mới phải có tối thiểu 6 ký tự!");
        }

        String normalizedEmail = email != null ? email.trim().toLowerCase() : "";
        boolean valid = otpService.verifyOtp(normalizedEmail, otpCode, "FORGOT_PASSWORD");
        if (!valid) {
            throw new IllegalArgumentException("Mã OTP không chính xác hoặc đã hết hạn!");
        }

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản tương ứng với email!"));

        user.setPassword(passwordEncoder.encode(newPassword.trim()));
        userRepository.save(user);
        log.info(">>> Đã đặt lại mật khẩu thành công cho tài khoản: {}", user.getUsername());
    }

    public boolean isLastEmailSentReal() {
        return otpService != null && otpService.isLastEmailSentReal();
    }

    public Optional<String> getLatestOtp(String email, String type) {
        return otpService != null ? otpService.getLatestOtp(email, type) : Optional.empty();
    }
}
