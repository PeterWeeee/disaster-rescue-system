package vn.edu.cuunan.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cuunan.entity.RescueAssignment;
import vn.edu.cuunan.entity.RescueTeam;
import vn.edu.cuunan.entity.Role;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.RescueAssignmentRepository;
import vn.edu.cuunan.repository.RescueTeamRepository;
import vn.edu.cuunan.repository.RoleRepository;
import vn.edu.cuunan.repository.SosRequestRepository;
import vn.edu.cuunan.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SosRequestRepository sosRequestRepository;
    private final RescueTeamRepository rescueTeamRepository;
    private final RescueAssignmentRepository rescueAssignmentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           SosRequestRepository sosRequestRepository,
                           RescueTeamRepository rescueTeamRepository,
                           RescueAssignmentRepository rescueAssignmentRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.sosRequestRepository = sosRequestRepository;
        this.rescueTeamRepository = rescueTeamRepository;
        this.rescueAssignmentRepository = rescueAssignmentRepository;
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

        // 2. Khởi tạo các tài khoản mặc định phục vụ kiểm thử & bảo vệ đồ án (mật khẩu: 123456)
        User adminUser = getOrCreateUser("admin", "123456", "Quản Trị Viên Hệ Thống", "0900000001", "admin@cuunan247.vn", adminRole);
        User coordUser = getOrCreateUser("coordinator", "123456", "Điều Phối Viên TT Chỉ Huy", "0900000002", "coord@cuunan247.vn", coordRole);
        User coordUser2 = getOrCreateUser("dieupoi1", "123456", "Điều Phối Viên Khu Vực 2", "0900000012", "dieupoi1@cuunan247.vn", coordRole);
        User rescuerUser = getOrCreateUser("rescuer", "123456", "Đội Trưởng Cứu Hộ Hải Châu", "0900000003", "rescuer@cuunan247.vn", rescuerRole);
        User rescuerUser2 = getOrCreateUser("cuuho1", "123456", "Đội Trưởng Cứu Hộ Sơn Trà", "0905556677", "cuuho1@cuunan247.vn", rescuerRole);
        User citizenUser = getOrCreateUser("citizen", "123456", "Nguyễn Văn Dân", "0900000004", "dan@cuunan247.vn", citizenRole);
        User citizenUser2 = getOrCreateUser("nguoidan1", "123456", "Lê Thị Cứu Trợ", "0900000014", "nguoidan1@cuunan247.vn", citizenRole);

        // 3. Khởi tạo 4 Đội cứu hộ mẫu với tọa độ thực tế
        RescueTeam teamHaiChau = null;
        if (rescueTeamRepository.count() == 0) {
            teamHaiChau = rescueTeamRepository.save(new RescueTeam(
                    null,
                    "Đội Cứu Hộ Hải Châu (Xuồng máy)",
                    rescuerUser,
                    "0900000003",
                    16.0678,
                    108.2208,
                    6,
                    "2 xuồng cao su gắn máy, 15 áo phao, túi sơ cứu y tế",
                    "AVAILABLE"
            ));

            rescueTeamRepository.save(new RescueTeam(
                    null,
                    "Đội Tình Nguyện Cứu Trợ Sơn Trà",
                    rescuerUser2,
                    "0905556677",
                    16.1022,
                    108.2450,
                    4,
                    "1 xuồng nhôm, áo phao, đèn pin công suất cao",
                    "AVAILABLE"
            ));

            rescueTeamRepository.save(new RescueTeam(
                    null,
                    "Lực Lượng Cơ Động Cứu Nạn Hòa Vang",
                    null,
                    "0909988776",
                    15.9800,
                    108.1800,
                    8,
                    "Xe lội nước đặc chủng, máy bơm công suất lớn",
                    "AVAILABLE"
            ));

            rescueTeamRepository.save(new RescueTeam(
                    null,
                    "Đội Cứu Nạn Thủy Bộ Thừa Thiên Huế",
                    null,
                    "0914112233",
                    16.4637,
                    107.5909,
                    7,
                    "Canô cao tốc cứu nạn vùng ngập lũ Hương Trà & Phá Tam Giang",
                    "AVAILABLE"
            ));

            log.info(">>> Khởi tạo 4 Đội cứu hộ mẫu kèm tọa độ GPS thành công!");
        } else {
            // Đảm bảo rescuerUser luôn là đội trưởng của teamHaiChau
            teamHaiChau = rescueTeamRepository.findByLeaderId(rescuerUser.getId())
                    .orElseGet(() -> {
                        RescueTeam firstTeam = rescueTeamRepository.findAll().get(0);
                        firstTeam.setLeader(rescuerUser);
                        return rescueTeamRepository.save(firstTeam);
                    });
        }

        // 4. Khởi tạo 10 ca SOS cứu nạn phong phú trên toàn quốc (Miền Trung & TP.HCM)
        if (sosRequestRepository.count() == 0) {
            // 1. CRITICAL - PENDING
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

            // 2. HIGH - DISPATCHED
            SosRequest sos2 = sosRequestRepository.save(SosRequest.builder()
                    .sender(citizenUser2)
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

            // 3. MEDIUM - RESOLVED
            SosRequest sos3 = sosRequestRepository.save(SosRequest.builder()
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

            // 4. CRITICAL - IN_PROGRESS
            SosRequest sos4 = sosRequestRepository.save(SosRequest.builder()
                    .sender(null)
                    .contactName("Hoàng Kim Oanh")
                    .contactPhone("0977112233")
                    .latitude(16.0450)
                    .longitude(108.2100)
                    .addressDetail("Khu dân cư ven sông Cẩm Lệ, P. Hòa Thọ Đông, Cẩm Lệ, Đà Nẵng")
                    .urgencyLevel("CRITICAL")
                    .peopleCount(5)
                    .hasElderly(true)
                    .hasChildren(true)
                    .hasInjured(true)
                    .description("Sạt lở bờ kè sát vách nhà, nước tràn xiết, có 1 người già khó thở cần bình oxy khẩn cấp!")
                    .status("IN_PROGRESS")
                    .build());

            // 5. HIGH - PENDING
            sosRequestRepository.save(SosRequest.builder()
                    .sender(null)
                    .contactName("Nguyễn Đức Thọ")
                    .contactPhone("0918889900")
                    .latitude(15.8801)
                    .longitude(108.3300)
                    .addressDetail("Đường Bạch Đằng, Phường Minh An, TP. Hội An, Quảng Nam")
                    .urgencyLevel("HIGH")
                    .peopleCount(3)
                    .hasElderly(false)
                    .hasChildren(true)
                    .hasInjured(false)
                    .description("Phố cổ ngập sâu 1.8m, mất điện diện rộng, hết nước uống sạch từ sáng nay.")
                    .status("PENDING")
                    .build());

            // 6. CRITICAL - PENDING
            sosRequestRepository.save(SosRequest.builder()
                    .sender(null)
                    .contactName("Đặng Văn Phúc")
                    .contactPhone("0935123456")
                    .latitude(16.4500)
                    .longitude(107.5700)
                    .addressDetail("Xã Hương Thọ, TP. Huế, Thừa Thiên Huế")
                    .urgencyLevel("CRITICAL")
                    .peopleCount(6)
                    .hasElderly(true)
                    .hasChildren(false)
                    .hasInjured(false)
                    .description("Nước sông Hương dâng nhanh vượt báo động 3, cô lập cả xóm, cần ca nô sơ tán gấp.")
                    .status("PENDING")
                    .build());

            // 7. MEDIUM - PENDING
            sosRequestRepository.save(SosRequest.builder()
                    .sender(null)
                    .contactName("Võ Thị Lành")
                    .contactPhone("0944556677")
                    .latitude(15.5680)
                    .longitude(108.4800)
                    .addressDetail("Thôn 2, Xã Bình Nam, Huyện Thăng Bình, Quảng Nam")
                    .urgencyLevel("MEDIUM")
                    .peopleCount(2)
                    .hasElderly(false)
                    .hasChildren(false)
                    .hasInjured(false)
                    .description("Ngập lụt cắt đứt giao thông liên thôn, xin hỗ trợ mì tôm và đèn cầy sạc dự phòng.")
                    .status("PENDING")
                    .build());

            // 8. HIGH - RESOLVED
            sosRequestRepository.save(SosRequest.builder()
                    .sender(null)
                    .contactName("Bùi Đình Nam")
                    .contactPhone("0966778899")
                    .latitude(16.7500)
                    .longitude(107.1800)
                    .addressDetail("Thị trấn Cửa Việt, Huyện Gio Linh, Quảng Trị")
                    .urgencyLevel("HIGH")
                    .peopleCount(4)
                    .hasElderly(true)
                    .hasChildren(false)
                    .hasInjured(false)
                    .description("Triều cường đánh sập bờ kè chắn sóng, đội xung kích đã đưa bà con vào trụ sở UBND an toàn.")
                    .status("RESOLVED")
                    .resolvedAt(LocalDateTime.now().minusHours(5))
                    .build());

            // 9. LOW - PENDING
            sosRequestRepository.save(SosRequest.builder()
                    .sender(null)
                    .contactName("Đỗ Anh Tuấn")
                    .contactPhone("0908112233")
                    .latitude(10.7769)
                    .longitude(106.7009)
                    .addressDetail("Đường Nguyễn Hữu Cảnh, Quận Bình Thạnh, TP. Hồ Chí Minh")
                    .urgencyLevel("LOW")
                    .peopleCount(1)
                    .hasElderly(false)
                    .hasChildren(false)
                    .hasInjured(false)
                    .description("Triều cường ngập sâu chết máy xe, cần xe cẩu hỗ trợ đưa phương tiện qua điểm ngập.")
                    .status("PENDING")
                    .build());

            // 10. CRITICAL - DISPATCHED
            sosRequestRepository.save(SosRequest.builder()
                    .sender(null)
                    .contactName("Ngô Thị Thanh")
                    .contactPhone("0919223344")
                    .latitude(15.2841)
                    .longitude(108.0821)
                    .addressDetail("Khu tái định cư xã Trà Mai, Huyện Nam Trà My, Quảng Nam")
                    .urgencyLevel("CRITICAL")
                    .peopleCount(4)
                    .hasElderly(true)
                    .hasChildren(true)
                    .hasInjured(true)
                    .description("Sạt lở đất đồi phía sau nhà, có 1 phụ nữ mang thai chuyển dạ cần đưa đến bệnh viện huyện gấp!")
                    .status("DISPATCHED")
                    .build());

            log.info(">>> Khởi tạo 10 ca SOS cứu nạn đa dạng trạng thái thành công!");
        }

        // 5. Khởi tạo 3 nhiệm vụ mẫu cho Đội cứu hộ Hải Châu (Đảm bảo tab PENDING, IN_PROGRESS, COMPLETED đều có dữ liệu)
        if (teamHaiChau != null && rescueAssignmentRepository.count() == 0) {
            List<SosRequest> allSos = sosRequestRepository.findAll();
            if (allSos.size() >= 3) {
                // Nhiệm vụ 1: Mới Đến Chờ Nhận (PENDING)
                RescueAssignment assignPending = new RescueAssignment();
                assignPending.setSosRequest(allSos.get(1)); // Lê Văn Hùng
                assignPending.setTeam(teamHaiChau);
                assignPending.setAssignedBy(coordUser);
                assignPending.setStatus("PENDING");
                assignPending.setAssignedAt(LocalDateTime.now().minusMinutes(10));
                assignPending.setReportNotes("Chỉ đạo điều phối: Tiếp cận bằng xuồng máy, chú ý người bị thương ở chân");
                rescueAssignmentRepository.save(assignPending);

                // Nhiệm vụ 2: Đang Thực Hiện (IN_PROGRESS)
                RescueAssignment assignActive = new RescueAssignment();
                SosRequest activeSosTarget = allSos.get(0); // Trần Thị Bé
                activeSosTarget.setStatus("IN_PROGRESS");
                sosRequestRepository.save(activeSosTarget);

                assignActive.setSosRequest(activeSosTarget);
                assignActive.setTeam(teamHaiChau);
                assignActive.setAssignedBy(coordUser);
                assignActive.setStatus("IN_PROGRESS");
                assignActive.setAssignedAt(LocalDateTime.now().minusMinutes(40));
                assignActive.setAcceptedAt(LocalDateTime.now().minusMinutes(35));
                assignActive.setReportNotes("Đội đã xuất phát mang theo áo phao và túi y tế");
                rescueAssignmentRepository.save(assignActive);

                // Nhiệm vụ 3: Lịch Sử Hoàn Thành (COMPLETED)
                RescueAssignment assignCompleted = new RescueAssignment();
                assignCompleted.setSosRequest(allSos.get(2)); // Phạm Minh Tâm
                assignCompleted.setTeam(teamHaiChau);
                assignCompleted.setAssignedBy(coordUser);
                assignCompleted.setStatus("COMPLETED");
                assignCompleted.setAssignedAt(LocalDateTime.now().minusHours(3));
                assignCompleted.setAcceptedAt(LocalDateTime.now().minusHours(2).minusMinutes(50));
                assignCompleted.setCompletedAt(LocalDateTime.now().minusHours(2));
                assignCompleted.setReportNotes("Đã đưa an toàn 2 người về nhà sinh hoạt cộng đồng, tình trạng sức khỏe tốt");
                assignCompleted.setReportImageUrl("https://images.unsplash.com/photo-1547683905-f686c993aae5?auto=format&fit=crop&w=800&q=80");
                rescueAssignmentRepository.save(assignCompleted);

                teamHaiChau.setStatus("BUSY");
                rescueTeamRepository.save(teamHaiChau);

                log.info(">>> Đã khởi tạo 3 nhiệm vụ cứu nạn phong phú (PENDING, IN_PROGRESS, COMPLETED) cho Đội Hải Châu!");
            }
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
