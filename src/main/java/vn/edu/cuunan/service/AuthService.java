package vn.edu.cuunan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
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
        String username = registerRequest.getUsername().trim().toLowerCase();
        String phone = registerRequest.getPhone().trim();

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Tên đăng nhập '" + username + "' đã được sử dụng!");
        }

        if (userRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("Số điện thoại '" + phone + "' đã được đăng ký tài khoản!");
        }

        String requestedRole = registerRequest.getRole();
        if (requestedRole == null || (!requestedRole.equals("ROLE_RESCUER") && !requestedRole.equals("ROLE_CITIZEN"))) {
            requestedRole = "ROLE_CITIZEN";
        }

        final String targetRole = requestedRole;
        Role role = roleRepository.findByName(targetRole)
                .orElseGet(() -> roleRepository.save(Role.builder().name(targetRole).build()));

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName().trim())
                .phone(phone)
                .email(registerRequest.getEmail() != null ? registerRequest.getEmail().trim() : null)
                .address(registerRequest.getAddress())
                .role(role)
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(user);
        log.info("Đã tạo mới tài khoản thành công: id={}, username={}, role={}", savedUser.getId(), savedUser.getUsername(), targetRole);
        return savedUser;
    }
}
