package vn.edu.cuunan.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cuunan.entity.Role;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.RoleRepository;
import vn.edu.cuunan.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // 1. Khởi tạo 4 vai trò chuẩn
        Role adminRole = getOrCreateRole("ROLE_ADMIN");
        Role coordRole = getOrCreateRole("ROLE_COORDINATOR");
        Role rescuerRole = getOrCreateRole("ROLE_RESCUER");
        Role citizenRole = getOrCreateRole("ROLE_CITIZEN");

        // 2. Khởi tạo các tài khoản mặc định phục vụ kiểm thử & vận hành (mật khẩu mặc định: 123456)
        getOrCreateUser("admin", "123456", "Quản Trị Viên Hệ Thống", "0900000001", "admin@cuunan247.vn", adminRole);
        getOrCreateUser("coordinator", "123456", "Điều Phối Viên TT Chỉ Huy", "0900000002", "coord@cuunan247.vn", coordRole);
        getOrCreateUser("rescuer", "123456", "Đội Trưởng Cứu Hộ Hải Châu", "0900000003", "rescuer@cuunan247.vn", rescuerRole);
        getOrCreateUser("citizen", "123456", "Nguyễn Văn Dân", "0900000004", "dan@cuunan247.vn", citizenRole);

        log.info(">>> Khởi tạo dữ liệu người dùng & vai trò ban đầu thành công!");
    }

    private Role getOrCreateRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));
    }

    private void getOrCreateUser(String username, String rawPassword, String fullName, String phone, String email, Role role) {
        if (!userRepository.existsByUsername(username)) {
            User user = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(rawPassword))
                    .fullName(fullName)
                    .phone(phone)
                    .email(email)
                    .address("Đà Nẵng / Miền Trung")
                    .role(role)
                    .status("ACTIVE")
                    .build();
            userRepository.save(user);
            log.info("Tạo tài khoản mẫu mặc định: {} [{}]", username, role.getName());
        }
    }
}
