package vn.edu.cuunan.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cuunan.entity.Role;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.RoleRepository;
import vn.edu.cuunan.repository.SosRequestRepository;
import vn.edu.cuunan.repository.UserRepository;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SosRequestRepository sosRequestRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           SosRequestRepository sosRequestRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.sosRequestRepository = sosRequestRepository;
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
        User citizenUser = getOrCreateUser("citizen", "123456", "Nguyễn Văn Dân", "0900000004", "dan@cuunan247.vn", citizenRole);

        // 3. Khởi tạo một số ca SOS mẫu thực tế tại Đà Nẵng / Miền Trung phục vụ hiển thị bản đồ Leaflet
        if (sosRequestRepository.count() == 0) {
            sosRequestRepository.save(SosRequest.builder()
                    .sender(citizenUser)
                    .contactName("Trần Thị Bé")
                    .contactPhone("0912345678")
                    .secondaryPhone("0905123987")
                    .latitude(16.0594)
                    .longitude(108.1585)
                    .addressDetail("Số 45 đường Mẹ Suốt, P. Hòa Khánh Nam, Liên Chiểu, Đà Nẵng")
                    .urgencyLevel("CRITICAL")
                    .peopleCount(4)
                    .hasElderly(true)
                    .hasChildren(true)
                    .hasInjured(false)
                    .description("Nước ngập ngang ngực, dâng rất nhanh, nhà cấp 4 có 1 cụ già 80 tuổi và 1 bé 3 tuổi, cần xuồng cứu hộ gấp!")
                    .status("PENDING")
                    .build());

            sosRequestRepository.save(SosRequest.builder()
                    .sender(null)
                    .contactName("Lê Văn Hùng")
                    .contactPhone("0988776655")
                    .latitude(15.9982)
                    .longitude(108.1965)
                    .addressDetail("Gần cầu Đỏ, Xã Hòa Châu, Hòa Vang, Đà Nẵng")
                    .urgencyLevel("HIGH")
                    .peopleCount(3)
                    .hasElderly(false)
                    .hasChildren(false)
                    .hasInjured(true)
                    .description("Đang bị cô lập do lũ cắt đứt đường, có 1 người bị thương ở chân do tôn cứa cần băng bó y tế.")
                    .status("DISPATCHED")
                    .build());

            sosRequestRepository.save(SosRequest.builder()
                    .sender(null)
                    .contactName("Phạm Minh Tâm")
                    .contactPhone("0933221100")
                    .latitude(16.0825)
                    .longitude(108.2224)
                    .addressDetail("Đường Như Nguyệt, P. Thuận Phước, Hải Châu, Đà Nẵng")
                    .urgencyLevel("MEDIUM")
                    .peopleCount(2)
                    .hasElderly(false)
                    .hasChildren(false)
                    .hasInjured(false)
                    .description("Nước ngập tầng trệt, đã được đội xuồng ứng cứu và đưa về nhà sinh hoạt cộng đồng an toàn.")
                    .status("RESOLVED")
                    .resolvedAt(LocalDateTime.now().minusHours(2))
                    .build());

            log.info(">>> Khởi tạo 3 ca SOS cứu nạn mẫu thực tế trên bản đồ thành công!");
        }

        log.info(">>> Khởi tạo dữ liệu người dùng & vai trò ban đầu thành công!");
    }

    private Role getOrCreateRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));
    }

    private User getOrCreateUser(String username, String rawPassword, String fullName, String phone, String email, Role role) {
        return userRepository.findByUsername(username).orElseGet(() -> {
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
            User saved = userRepository.save(user);
            log.info("Tạo tài khoản mẫu mặc định: {} [{}]", username, role.getName());
            return saved;
        });
    }
}
