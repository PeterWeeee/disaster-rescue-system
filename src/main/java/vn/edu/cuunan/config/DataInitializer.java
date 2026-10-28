package vn.edu.cuunan.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cuunan.entity.*;
import vn.edu.cuunan.repository.*;
import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SosRequestRepository sosRequestRepository;
    private final RescueTeamRepository rescueTeamRepository;
    private final RescueAssignmentRepository rescueAssignmentRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final DisasterAlertRepository disasterAlertRepository;
    private final EvacuationShelterRepository evacuationShelterRepository;
    private final CommunityPostRepository communityPostRepository;
    private final PostUpvoteRepository postUpvoteRepository;
    private final MediaFileRepository mediaFileRepository;
    private final RescueEquipmentRepository rescueEquipmentRepository;
    private final NotificationRepository notificationRepository;
    private final ActivityLogRepository activityLogRepository;
    private final TeamFundRequestRepository teamFundRequestRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private RescueChatMessageRepository rescueChatMessageRepository;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           SosRequestRepository sosRequestRepository,
                           RescueTeamRepository rescueTeamRepository,
                           RescueAssignmentRepository rescueAssignmentRepository,
                           TeamMemberRepository teamMemberRepository,
                           DisasterAlertRepository disasterAlertRepository,
                           EvacuationShelterRepository evacuationShelterRepository,
                           CommunityPostRepository communityPostRepository,
                           PostUpvoteRepository postUpvoteRepository,
                           MediaFileRepository mediaFileRepository,
                           RescueEquipmentRepository rescueEquipmentRepository,
                           NotificationRepository notificationRepository,
                           ActivityLogRepository activityLogRepository,
                           TeamFundRequestRepository teamFundRequestRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.sosRequestRepository = sosRequestRepository;
        this.rescueTeamRepository = rescueTeamRepository;
        this.rescueAssignmentRepository = rescueAssignmentRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.disasterAlertRepository = disasterAlertRepository;
        this.evacuationShelterRepository = evacuationShelterRepository;
        this.communityPostRepository = communityPostRepository;
        this.postUpvoteRepository = postUpvoteRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.rescueEquipmentRepository = rescueEquipmentRepository;
        this.notificationRepository = notificationRepository;
        this.activityLogRepository = activityLogRepository;
        this.teamFundRequestRepository = teamFundRequestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("==========================================================================");
        log.info(">>> [KHỞI TẠO CƠ SỞ DỮ LIỆU MIỀN TRUNG - HỆ THỐNG CỨU NẠN 24/7] BẮT ĐẦU...");
        log.info("==========================================================================");

        // 1. Khởi tạo 4 Vai trò (Roles) chuẩn
        Role adminRole = getOrCreateRole("ROLE_ADMIN");
        Role coordRole = getOrCreateRole("ROLE_COORDINATOR");
        Role rescuerRole = getOrCreateRole("ROLE_RESCUER");
        Role citizenRole = getOrCreateRole("ROLE_CITIZEN");

        // 2. Khởi tạo Người dùng (Users): Mật khẩu thống nhất kiểm thử là 123456
        // 2.1. Quản trị viên (Admin)
        User adminUser = getOrCreateUser("admin", "123456", "Quản Trị Viên Hệ Thống", "0901112233", "admin@cuunan247.vn", "Hà Nội / Đà Nẵng", adminRole);

        // 2.2. Điều phối viên trung tâm & 11 tỉnh/thành Miền Trung
        User coordCentral = getOrCreateUser("coordinator", "123456", "Điều Phối Viên TT Chỉ Huy", "0902223344", "coord@cuunan247.vn", "Trung Tâm Chỉ Huy Đà Nẵng", coordRole);
        User coordCentral2 = getOrCreateUser("dieupoi1", "123456", "Điều Phối Viên Khu Vực 2", "0902223355", "dieupoi1@cuunan247.vn", "Khu Vực Duyên Hải Miền Trung", coordRole);
        User coordTH = getOrCreateUser("coord_thanhhoa", "123456", "Nguyễn Văn Điều Phối Thanh Hóa", "0902010001", "coord.thanhhoa@cuunan247.vn", "Thành phố Thanh Hóa", coordRole);
        User coordNA = getOrCreateUser("coord_nghean", "123456", "Trần Đình Điều Phối Nghệ An", "0902020002", "coord.nghean@cuunan247.vn", "Thành phố Vinh, Nghệ An", coordRole);
        User coordHT = getOrCreateUser("coord_hatinh", "123456", "Lê Văn Điều Phối Hà Tĩnh", "0902030003", "coord.hatinh@cuunan247.vn", "Thành phố Hà Tĩnh", coordRole);
        User coordQT = getOrCreateUser("coord_quangtri", "123456", "Phạm Văn Điều Phối Quảng Trị", "0902040004", "coord.quangtri@cuunan247.vn", "Thành phố Đông Hà, Quảng Trị", coordRole);
        User coordHue = getOrCreateUser("coord_hue", "123456", "Hoàng Văn Điều Phối Huế", "0902050005", "coord.hue@cuunan247.vn", "Thành phố Huế", coordRole);
        User coordDN = getOrCreateUser("coord_danang", "123456", "Ngô Quốc Điều Phối Đà Nẵng", "0902060006", "coord.danang@cuunan247.vn", "Thành phố Đà Nẵng", coordRole);
        User coordQN = getOrCreateUser("coord_quangngai", "123456", "Võ Văn Điều Phối Quảng Ngãi", "0902070007", "coord.quangngai@cuunan247.vn", "Thành phố Quảng Ngãi", coordRole);
        User coordGL = getOrCreateUser("coord_gialai", "123456", "Đỗ Văn Điều Phối Gia Lai", "0902080008", "coord.gialai@cuunan247.vn", "Thành phố Pleiku, Gia Lai", coordRole);
        User coordDL = getOrCreateUser("coord_daklak", "123456", "Bùi Văn Điều Phối Đắk Lắk", "0902090009", "coord.daklak@cuunan247.vn", "Thành phố Buôn Ma Thuột, Đắk Lắk", coordRole);
        User coordKH = getOrCreateUser("coord_khanhhoa", "123456", "Đinh Văn Điều Phối Khánh Hòa", "0902100010", "coord.khanhhoa@cuunan247.vn", "Thành phố Nha Trang, Khánh Hòa", coordRole);
        User coordLD = getOrCreateUser("coord_lamdong", "123456", "Chu Văn Điều Phối Lâm Đồng", "0902110011", "coord.lamdong@cuunan247.vn", "Thành phố Đà Lạt, Lâm Đồng", coordRole);

        // 2.3. Đội trưởng cứu hộ (ROLE_RESCUER) cho từng đội thuộc 11 tỉnh/thành
        // Thanh Hóa
        User rescuerTH01 = getOrCreateUser("rescuer_th01", "123456", "Lê Văn Thành (Đội Trưởng TH 01)", "0911001001", "rescuer.th01@cuunan247.vn", "TP Thanh Hóa, Thanh Hóa", rescuerRole);
        User rescuerTH02 = getOrCreateUser("rescuer_th02", "123456", "Trần Quốc Hùng (Đội Trưởng TH 02)", "0911001002", "rescuer.th02@cuunan247.vn", "Thị xã Nghi Sơn, Thanh Hóa", rescuerRole);
        // Nghệ An
        User rescuerNA01 = getOrCreateUser("rescuer_na01", "123456", "Nguyễn Văn Nghĩa (Đội Trưởng NA 01)", "0912002001", "rescuer.na01@cuunan247.vn", "TP Vinh, Nghệ An", rescuerRole);
        User rescuerNA02 = getOrCreateUser("rescuer_na02", "123456", "Võ Thị An (Đội Trưởng NA 02)", "0912002002", "rescuer.na02@cuunan247.vn", "Huyện Kỳ Sơn, Nghệ An", rescuerRole);
        // Hà Tĩnh
        User rescuerHT01 = getOrCreateUser("rescuer_ht01", "123456", "Phạm Đình Tĩnh (Đội Trưởng HT 01)", "0913003001", "rescuer.ht01@cuunan247.vn", "Huyện Cẩm Xuyên, Hà Tĩnh", rescuerRole);
        User rescuerHT02 = getOrCreateUser("rescuer_ht02", "123456", "Lê Hồng Sơn (Đội Trưởng HT 02)", "0913003002", "rescuer.ht02@cuunan247.vn", "Huyện Hương Khê, Hà Tĩnh", rescuerRole);
        // Quảng Trị
        User rescuerQT01 = getOrCreateUser("rescuer_qt01", "123456", "Hoàng Văn Trị (Đội Trưởng QT 01)", "0914004001", "rescuer.qt01@cuunan247.vn", "TP Đông Hà, Quảng Trị", rescuerRole);
        User rescuerQT02 = getOrCreateUser("rescuer_qt02", "123456", "Đặng Thị Quảng (Đội Trưởng QT 02)", "0914004002", "rescuer.qt02@cuunan247.vn", "Huyện Hướng Hóa, Quảng Trị", rescuerRole);
        // Thành phố Huế
        User rescuerHue01 = getOrCreateUser("rescuer_hue01", "123456", "Ngô Văn Huệ (Đội Trưởng Huế 01)", "0915005001", "rescuer.hue01@cuunan247.vn", "TP Huế, Thừa Thiên Huế", rescuerRole);
        User rescuerHue02 = getOrCreateUser("rescuer_hue02", "123456", "Lý Thị Hương (Đội Trưởng Huế 02)", "0915005002", "rescuer.hue02@cuunan247.vn", "TX Hương Trà, Thừa Thiên Huế", rescuerRole);
        // Thành phố Đà Nẵng (giữ rescuer & cuuho1 để tương thích tài liệu cũ, thêm dn03)
        User rescuerDN01 = getOrCreateUser("rescuer", "123456", "Đặng Văn Cứu Hộ (Đội Trưởng Hải Châu)", "0900000003", "rescuer@cuunan247.vn", "Quận Hải Châu, Đà Nẵng", rescuerRole);
        User rescuerDN02 = getOrCreateUser("cuuho1", "123456", "Nguyễn Văn Sơn Trà (Đội Trưởng Sơn Trà)", "0905556677", "cuuho1@cuunan247.vn", "Quận Sơn Trà, Đà Nẵng", rescuerRole);
        User rescuerDN03 = getOrCreateUser("rescuer_dn03", "123456", "Dương Văn Nẵng (Đội Trưởng Hòa Vang)", "0916006003", "rescuer.dn03@cuunan247.vn", "Huyện Hòa Vang, Đà Nẵng", rescuerRole);
        // Quảng Ngãi
        User rescuerQN01 = getOrCreateUser("rescuer_qn01", "123456", "Đinh Công Ngãi (Đội Trưởng QN 01)", "0917007001", "rescuer.qn01@cuunan247.vn", "TP Quảng Ngãi, Quảng Ngãi", rescuerRole);
        User rescuerQN02 = getOrCreateUser("rescuer_qn02", "123456", "Bùi Văn Trà (Đội Trưởng QN 02)", "0917007002", "rescuer.qn02@cuunan247.vn", "Huyện Ba Tơ, Quảng Ngãi", rescuerRole);
        // Gia Lai
        User rescuerGL01 = getOrCreateUser("rescuer_gl01", "123456", "Ksor Y Lai (Đội Trưởng Gia Lai 01)", "0918008001", "rescuer.gl01@cuunan247.vn", "TP Pleiku, Gia Lai", rescuerRole);
        User rescuerGL02 = getOrCreateUser("rescuer_gl02", "123456", "Rmah Đinh (Đội Trưởng Gia Lai 02)", "0918008002", "rescuer.gl02@cuunan247.vn", "Huyện Ia Grai, Gia Lai", rescuerRole);
        // Đắk Lắk
        User rescuerDL01 = getOrCreateUser("rescuer_dl01", "123456", "Y Krin Niê (Đội Trưởng Đắk Lắk 01)", "0919009001", "rescuer.dl01@cuunan247.vn", "TP Buôn Ma Thuột, Đắk Lắk", rescuerRole);
        User rescuerDL02 = getOrCreateUser("rescuer_dl02", "123456", "H'Lan Byă (Đội Trưởng Đắk Lắk 02)", "0919009002", "rescuer.dl02@cuunan247.vn", "Huyện Ea Súp, Đắk Lắk", rescuerRole);
        // Khánh Hòa
        User rescuerKH01 = getOrCreateUser("rescuer_kh01", "123456", "Trần Văn Hòa (Đội Trưởng Khánh Hòa 01)", "0920010001", "rescuer.kh01@cuunan247.vn", "TP Nha Trang, Khánh Hòa", rescuerRole);
        User rescuerKH02 = getOrCreateUser("rescuer_kh02", "123456", "Nguyễn Thị Khánh (Đội Trưởng Khánh Hòa 02)", "0920010002", "rescuer.kh02@cuunan247.vn", "TP Cam Ranh, Khánh Hòa", rescuerRole);
        // Lâm Đồng
        User rescuerLD01 = getOrCreateUser("rescuer_ld01", "123456", "Cil Ha Đồng (Đội Trưởng Lâm Đồng 01)", "0921011001", "rescuer.ld01@cuunan247.vn", "TP Đà Lạt, Lâm Đồng", rescuerRole);
        User rescuerLD02 = getOrCreateUser("rescuer_ld02", "123456", "K'Brooke (Đội Trưởng Lâm Đồng 02)", "0921011002", "rescuer.ld02@cuunan247.vn", "TP Bảo Lộc, Lâm Đồng", rescuerRole);

        // 2.4. Người dân (ROLE_CITIZEN)
        User citizen01 = getOrCreateUser("citizen", "123456", "Nguyễn Văn Dân", "0900000004", "dan@cuunan247.vn", "Quận Hải Châu, Đà Nẵng", citizenRole);
        User citizen02 = getOrCreateUser("nguoidan1", "123456", "Lê Thị Cứu Trợ", "0900000014", "nguoidan1@cuunan247.vn", "Huyện Hòa Vang, Đà Nẵng", citizenRole);
        User citizenTH = getOrCreateUser("citizen_th", "123456", "Nguyễn Thị Thanh Hóa", "0901000101", "citizen.th@cuunan247.vn", "Thành phố Thanh Hóa", citizenRole);
        User citizenNA = getOrCreateUser("citizen_na", "123456", "Lê Văn Nghệ An", "0901000102", "citizen.na@cuunan247.vn", "Huyện Hưng Nguyên, Nghệ An", citizenRole);
        User citizenHT = getOrCreateUser("citizen_ht", "123456", "Phạm Văn Hà Tĩnh", "0901000103", "citizen.ht@cuunan247.vn", "Huyện Cẩm Xuyên, Hà Tĩnh", citizenRole);
        User citizenQT = getOrCreateUser("citizen_qt", "123456", "Hoàng Văn Quảng Trị", "0901000104", "citizen.qt@cuunan247.vn", "Thị xã Quảng Trị", citizenRole);
        User citizenHue = getOrCreateUser("citizen_hue", "123456", "Trần Thị Huế", "0901000105", "citizen.hue@cuunan247.vn", "Thành phố Huế", citizenRole);
        User citizenQN = getOrCreateUser("citizen_qn", "123456", "Đinh Văn Quảng Ngãi", "0901000106", "citizen.qn@cuunan247.vn", "Huyện Bình Sơn, Quảng Ngãi", citizenRole);
        User citizenGL = getOrCreateUser("citizen_gl", "123456", "Ksor H'Lâm", "0901000107", "citizen.gl@cuunan247.vn", "TP Pleiku, Gia Lai", citizenRole);
        User citizenDL = getOrCreateUser("citizen_dl", "123456", "Y Krin Dân", "0901000108", "citizen.dl@cuunan247.vn", "Huyện Krông Ana, Đắk Lắk", citizenRole);
        User citizenKH = getOrCreateUser("citizen_kh", "123456", "Hoàng Thị Khánh Hòa", "0901000109", "citizen.kh@cuunan247.vn", "TP Nha Trang, Khánh Hòa", citizenRole);
        User citizenLD = getOrCreateUser("citizen_ld", "123456", "Cil Ha Kôi", "0901000110", "citizen.ld@cuunan247.vn", "Huyện Lạc Dương, Lâm Đồng", citizenRole);

        // 3. Khởi tạo Đội cứu hộ (RescueTeams) - 1 đến 2 đội cho từng tỉnh/thành miền Trung
        // 3.1. Thanh Hóa
        RescueTeam teamTH01 = getOrCreateRescueTeam("Đội Cứu Hộ Thanh Hóa 01 (Sông Mã)", rescuerTH01, "0911001001", 19.8074, 105.7764, 8, "2 xuồng cao su gắn máy, 20 áo phao, máy bơm cứu hộ, cáng thương", "AVAILABLE");
        RescueTeam teamTH02 = getOrCreateRescueTeam("Đội Cứu Hộ Thanh Hóa 02 (Nghi Sơn)", rescuerTH02, "0911001002", 19.3400, 105.8000, 6, "1 xuồng nhôm, 12 áo phao, đèn pha công suất cao, thiết bị lặn", "AVAILABLE");

        // 3.2. Nghệ An
        RescueTeam teamNA01 = getOrCreateRescueTeam("Đội Cứu Hộ Nghệ An 01 (Vinh - Sông Lam)", rescuerNA01, "0912002001", 18.6799, 105.6813, 10, "3 xuồng cao su đặc chủng, 25 áo phao, máy phát điện di động, bộ đàm tầm xa", "AVAILABLE");
        RescueTeam teamNA02 = getOrCreateRescueTeam("Đội Cứu Hộ Nghệ An 02 (Kỳ Sơn - Miền Núi)", rescuerNA02, "0912002002", 19.4100, 104.1500, 6, "Xe bán tải 2 cầu, dây thừng chuyên dụng, 12 áo phao, thiết bị sơ cứu leo núi", "BUSY");

        // 3.3. Hà Tĩnh
        RescueTeam teamHT01 = getOrCreateRescueTeam("Đội Cứu Hộ Hà Tĩnh 01 (Cẩm Xuyên)", rescuerHT01, "0913003001", 18.2500, 105.9500, 8, "2 xuồng máy nhôm, 18 áo phao, máy cưa xăng mở đường, đèn rọi pin", "BUSY");
        RescueTeam teamHT02 = getOrCreateRescueTeam("Đội Cứu Hộ Hà Tĩnh 02 (Hương Khê)", rescuerHT02, "0913003002", 18.1700, 105.7000, 6, "1 canô cao tốc, 12 áo phao, thiết bị cấp cứu ngạt nước, lều bạt cứu hộ", "AVAILABLE");

        // 3.4. Quảng Trị
        RescueTeam teamQT01 = getOrCreateRescueTeam("Đội Cứu Hộ Quảng Trị 01 (Đông Hà - Thạch Hãn)", rescuerQT01, "0914004001", 16.8150, 107.1000, 8, "2 canô cứu hộ, 15 áo phao, phao tròn cứu sinh, máy lọc nước dã chiến", "AVAILABLE");
        RescueTeam teamQT02 = getOrCreateRescueTeam("Đội Cứu Hộ Quảng Trị 02 (Hướng Hóa)", rescuerQT02, "0914004002", 16.6800, 106.6600, 6, "Xe cứu nạn địa hình, cáng cứu thương cẩu, thiết bị dò tìm đất đá", "AVAILABLE");

        // 3.5. Thành phố Huế
        RescueTeam teamHue01 = getOrCreateRescueTeam("Đội Cứu Hộ Huế 01 (Sông Hương & Tam Giang)", rescuerHue01, "0915005001", 16.4637, 107.5909, 9, "3 canô cứu nạn cao tốc, 22 áo phao, túi sơ cứu y tế nâng cao, bình oxy", "BUSY");
        RescueTeam teamHue02 = getOrCreateRescueTeam("Đội Cứu Hộ Huế 02 (Hương Trà)", rescuerHue02, "0915005002", 16.4300, 107.5000, 5, "1 xuồng cao su máy, 10 áo phao, lương khô dã chiến, đèn tín hiệu SOS", "AVAILABLE");

        // 3.6. Thành phố Đà Nẵng
        RescueTeam teamDN01 = getOrCreateRescueTeam("Đội Cứu Hộ Hải Châu (Xuồng máy)", rescuerDN01, "0900000003", 16.0678, 108.2208, 6, "2 xuồng cao su gắn máy, 15 áo phao, túi sơ cứu y tế", "BUSY");
        RescueTeam teamDN02 = getOrCreateRescueTeam("Đội Tình Nguyện Cứu Trợ Sơn Trà", rescuerDN02, "0905556677", 16.1022, 108.2450, 5, "1 xuồng nhôm, áo phao, đèn pin công suất cao, thiết bị cứu hộ biển", "AVAILABLE");
        RescueTeam teamDN03 = getOrCreateRescueTeam("Đội Cứu Hộ Hòa Vang (Cơ động)", rescuerDN03, "0916006003", 15.9800, 108.1800, 8, "Xe lội nước đặc chủng, máy bơm công suất lớn, phao cứu nạn chuyên sâu", "AVAILABLE");

        // 3.7. Quảng Ngãi
        RescueTeam teamQN01 = getOrCreateRescueTeam("Đội Cứu Hộ Quảng Ngãi 01 (Sông Trà Khúc)", rescuerQN01, "0917007001", 15.1200, 108.7922, 7, "2 xuồng máy cao tốc, 15 áo phao, dây thừng neo thuyền, cáng y tế", "AVAILABLE");
        RescueTeam teamQN02 = getOrCreateRescueTeam("Đội Cứu Hộ Quảng Ngãi 02 (Ba Tơ)", rescuerQN02, "0917007002", 14.7700, 108.7200, 5, "Xe bán tải 4x4, đèn pin chống nước, lương thực dự phòng, cưa xích", "AVAILABLE");

        // 3.8. Gia Lai
        RescueTeam teamGL01 = getOrCreateRescueTeam("Đội Cứu Hộ Gia Lai 01 (Pleiku)", rescuerGL01, "0918008001", 13.9833, 108.0000, 8, "Xe cứu hộ việt dã địa hình đồi dốc, 15 áo phao, tời kéo cáp 5 tấn, túi y tế", "AVAILABLE");
        RescueTeam teamGL02 = getOrCreateRescueTeam("Đội Cứu Hộ Gia Lai 02 (Ia Grai)", rescuerGL02, "0918008002", 14.0500, 107.7500, 5, "1 xuồng nhôm vượt lũ, 10 áo phao, đèn cao áp, còi báo động", "OFFLINE");

        // 3.9. Đắk Lắk
        RescueTeam teamDL01 = getOrCreateRescueTeam("Đội Cứu Hộ Đắk Lắk 01 (Buôn Ma Thuột)", rescuerDL01, "0919009001", 12.6667, 108.0500, 9, "Xe cứu trợ cơ động, 2 xuồng cứu nạn suối sâu, 20 áo phao, máy kích điện tim", "AVAILABLE");
        RescueTeam teamDL02 = getOrCreateRescueTeam("Đội Cứu Hộ Đắk Lắk 02 (Ea Súp)", rescuerDL02, "0919009002", 13.1500, 107.8800, 6, "Canô đáy phẳng chống cạn sỏi, 12 áo phao, thuốc lọc nước khử khuẩn", "AVAILABLE");

        // 3.10. Khánh Hòa
        RescueTeam teamKH01 = getOrCreateRescueTeam("Đội Cứu Hộ Khánh Hòa 01 (Nha Trang)", rescuerKH01, "0920010001", 12.2388, 109.1967, 8, "2 canô cứu hộ bão biển, 20 áo phao, bình dưỡng khí lặn, dây ném cứu nạn", "AVAILABLE");
        RescueTeam teamKH02 = getOrCreateRescueTeam("Đội Cứu Hộ Khánh Hòa 02 (Cam Ranh)", rescuerKH02, "0920010002", 11.9200, 109.1500, 6, "1 canô cao su máy thủy, 12 áo phao, đèn laser cứu nạn biển, túi cấp cứu", "BUSY");

        // 3.11. Lâm Đồng
        RescueTeam teamLD01 = getOrCreateRescueTeam("Đội Cứu Hộ Lâm Đồng 01 (Đà Lạt - Đèo Dốc)", rescuerLD01, "0921011001", 11.9404, 108.4583, 7, "Xe cứu hộ đèo núi tời cáp, dây leo vách đá chuyên dụng, đèn pha sương mù", "AVAILABLE");
        RescueTeam teamLD02 = getOrCreateRescueTeam("Đội Cứu Hộ Lâm Đồng 02 (Bảo Lộc)", rescuerLD02, "0921011002", 11.5400, 107.8000, 6, "Xe bán tải 2 cầu, máy nén khí, cáng xếp vận chuyển người gãy xương", "AVAILABLE");

        // 4. Khởi tạo Thành viên đội cứu hộ (TeamMembers)
        initTeamMember(teamTH01, rescuerTH01, "LEADER", 19.8074, 105.7764);
        initTeamMember(teamTH01, citizenTH, "RESCUER", 19.8070, 105.7760);
        initTeamMember(teamTH02, rescuerTH02, "LEADER", 19.3400, 105.8000);

        initTeamMember(teamNA01, rescuerNA01, "LEADER", 18.6799, 105.6813);
        initTeamMember(teamNA01, citizenNA, "MEDIC", 18.6795, 105.6810);
        initTeamMember(teamNA02, rescuerNA02, "LEADER", 19.4100, 104.1500);

        initTeamMember(teamHT01, rescuerHT01, "LEADER", 18.2500, 105.9500);
        initTeamMember(teamHT01, citizenHT, "DRIVER", 18.2502, 105.9505);
        initTeamMember(teamHT02, rescuerHT02, "LEADER", 18.1700, 105.7000);

        initTeamMember(teamQT01, rescuerQT01, "LEADER", 16.8150, 107.1000);
        initTeamMember(teamQT01, citizenQT, "RESCUER", 16.8145, 107.1005);
        initTeamMember(teamQT02, rescuerQT02, "LEADER", 16.6800, 106.6600);

        initTeamMember(teamHue01, rescuerHue01, "LEADER", 16.4637, 107.5909);
        initTeamMember(teamHue01, citizenHue, "MEDIC", 16.4630, 107.5900);
        initTeamMember(teamHue02, rescuerHue02, "LEADER", 16.4300, 107.5000);

        initTeamMember(teamDN01, rescuerDN01, "LEADER", 16.0678, 108.2208);
        initTeamMember(teamDN01, citizen01, "RESCUER", 16.0675, 108.2205);
        initTeamMember(teamDN02, rescuerDN02, "LEADER", 16.1022, 108.2450);
        initTeamMember(teamDN03, rescuerDN03, "LEADER", 15.9800, 108.1800);

        initTeamMember(teamQN01, rescuerQN01, "LEADER", 15.1200, 108.7922);
        initTeamMember(teamQN01, citizenQN, "DRIVER", 15.1205, 108.7920);
        initTeamMember(teamQN02, rescuerQN02, "LEADER", 14.7700, 108.7200);

        initTeamMember(teamGL01, rescuerGL01, "LEADER", 13.9833, 108.0000);
        initTeamMember(teamGL01, citizenGL, "RESCUER", 13.9830, 108.0010);
        initTeamMember(teamGL02, rescuerGL02, "LEADER", 14.0500, 107.7500);

        initTeamMember(teamDL01, rescuerDL01, "LEADER", 12.6667, 108.0500);
        initTeamMember(teamDL01, citizenDL, "MEDIC", 12.6660, 108.0510);
        initTeamMember(teamDL02, rescuerDL02, "LEADER", 13.1500, 107.8800);

        initTeamMember(teamKH01, rescuerKH01, "LEADER", 12.2388, 109.1967);
        initTeamMember(teamKH01, citizenKH, "DRIVER", 12.2380, 109.1960);
        initTeamMember(teamKH02, rescuerKH02, "LEADER", 11.9200, 109.1500);

        initTeamMember(teamLD01, rescuerLD01, "LEADER", 11.9404, 108.4583);
        initTeamMember(teamLD01, citizenLD, "RESCUER", 11.9400, 108.4580);
        initTeamMember(teamLD02, rescuerLD02, "LEADER", 11.5400, 107.8000);

        // 5. Khởi tạo 15 ca SOS cứu nạn phong phú tại 11 tỉnh/thành Miền Trung
        // SOS 1: Đà Nẵng - CRITICAL - IN_PROGRESS
        SosRequest sosDN1 = getOrCreateSos(
                citizen01, "Trần Thị Bé", "0912345678", "0905123987",
                16.0594, 108.1585, "Số 45 đường Mẹ Suốt, P. Hòa Khánh Nam, Liên Chiểu, Đà Nẵng",
                "CRITICAL", 4, true, true, false,
                "Nước ngập ngang ngực, dâng rất nhanh, nhà cấp 4 có 1 cụ già 80 tuổi và 1 bé 3 tuổi, cần xuồng cứu hộ gấp!",
                "IN_PROGRESS", null
        );

        // SOS 2: Đà Nẵng - HIGH - DISPATCHED
        SosRequest sosDN2 = getOrCreateSos(
                citizen02, "Lê Văn Hùng", "0988776655", null,
                15.9982, 108.1965, "Gần cầu Đỏ, Xã Hòa Châu, Hòa Vang, Đà Nẵng",
                "HIGH", 3, false, false, true,
                "Đang bị cô lập do lũ cắt đứt đường, có 1 người bị thương ở chân do tôn cứa cần băng bó y tế.",
                "DISPATCHED", null
        );

        // SOS 3: Đà Nẵng - MEDIUM - RESOLVED
        SosRequest sosDN3 = getOrCreateSos(
                null, "Phạm Minh Tâm", "0933221100", null,
                16.0825, 108.2224, "Đường Như Nguyệt, P. Thuận Phước, Hải Châu, Đà Nẵng",
                "MEDIUM", 2, false, false, false,
                "Nước ngập tầng trệt, đã được đội xuồng ứng cứu và đưa về nhà sinh hoạt cộng đồng an toàn.",
                "RESOLVED", LocalDateTime.now().minusHours(3)
        );

        // SOS 4: Thanh Hóa - CRITICAL - PENDING
        SosRequest sosTH1 = getOrCreateSos(
                citizenTH, "Lê Thị Mai", "0911223344", null,
                19.8200, 105.7900, "Xã Thiệu Dương, Thành phố Thanh Hóa, Thanh Hóa",
                "CRITICAL", 5, true, true, false,
                "Nước lũ sông Mã dâng tràn đê bao, ngập sâu 1.6m, cô lập cả gia đình trên gác lửng, cần canô di dời gấp!",
                "PENDING", null
        );

        // SOS 5: Thanh Hóa - HIGH - DISPATCHED
        SosRequest sosTH2 = getOrCreateSos(
                null, "Nguyễn Trọng Tấn", "0911334455", null,
                19.3800, 105.7800, "Phường Hải Hòa, Thị xã Nghi Sơn, Thanh Hóa",
                "HIGH", 3, false, false, true,
                "Sóng biển dâng cao kết hợp triều cường đánh sập mái che, 1 người bị gãy tay cần sơ tán đến trạm xá.",
                "DISPATCHED", null
        );

        // SOS 6: Nghệ An - HIGH - DISPATCHED
        SosRequest sosNA1 = getOrCreateSos(
                citizenNA, "Ngô Trọng Nghĩa", "0912334455", null,
                18.6600, 105.6500, "Xã Hưng Lợi, Huyện Hưng Nguyên, Nghệ An",
                "HIGH", 6, true, false, false,
                "Nước lũ dâng ngập đường giao thông 1.2m, xóm bị cô lập 2 ngày nay, thiếu lương thực và nước uống sạch.",
                "DISPATCHED", null
        );

        // SOS 7: Nghệ An - CRITICAL - CANCELLED
        SosRequest sosNA2 = getOrCreateSos(
                null, "Võ Đình Thắng", "0912445566", null,
                19.3800, 104.1200, "Thị trấn Mường Xén, Huyện Kỳ Sơn, Nghệ An",
                "CRITICAL", 2, false, false, false,
                "Lũ quét cục bộ làm sạt bờ rào, gia đình đã tự di dời kịp thời sang nhà người thân cao hơn, xin hủy cứu hộ.",
                "CANCELLED", null
        );

        // SOS 8: Hà Tĩnh - CRITICAL - IN_PROGRESS
        SosRequest sosHT1 = getOrCreateSos(
                citizenHT, "Phạm Đình Nam", "0913445566", null,
                18.2300, 105.9400, "Xã Cẩm Duệ, Huyện Cẩm Xuyên, Hà Tĩnh",
                "CRITICAL", 4, true, true, true,
                "Nước hồ Kẻ Gỗ xả tràn kết hợp mưa to làm ngập 2m, có cụ già 85 tuổi khó thở và người trẹo khớp chân, cần xuồng cấp cứu!",
                "IN_PROGRESS", null
        );

        // SOS 9: Quảng Trị - HIGH - PENDING
        SosRequest sosQT1 = getOrCreateSos(
                citizenQT, "Hoàng Kim Oanh", "0914556677", null,
                16.7400, 107.1800, "Thị trấn Cửa Việt, Huyện Gio Linh, Quảng Trị",
                "HIGH", 3, false, true, false,
                "Sóng lớn đánh sạt bờ bao nuôi tôm, nước tràn vào nhà sâu 1m, có trẻ nhỏ 18 tháng tuổi cần đưa vào nơi tạm lánh.",
                "PENDING", null
        );

        // SOS 10: Thành phố Huế - CRITICAL - IN_PROGRESS
        SosRequest sosHue1 = getOrCreateSos(
                citizenHue, "Trần Văn Hương", "0915667788", null,
                16.4800, 107.5700, "Phường Hương Vinh, Thành phố Huế, Thừa Thiên Huế",
                "CRITICAL", 7, true, true, false,
                "Nước sông Hương vượt báo động 3 ngập 1.8m, khu dân cư bị cô lập hoàn toàn, nhà có phụ nữ mang thai sắp sinh!",
                "IN_PROGRESS", null
        );

        // SOS 11: Quảng Ngãi - MEDIUM - PENDING
        SosRequest sosQN1 = getOrCreateSos(
                citizenQN, "Võ Thị Lành", "0917889900", null,
                15.2200, 108.7500, "Xã Bình Đông, Huyện Bình Sơn, Quảng Ngãi",
                "MEDIUM", 4, true, false, false,
                "Gió bão làm tốc mái tôn gian bếp, nước mưa ngập sân sâu 0.6m, cần hỗ trợ bạt che chắn và nước sạch.",
                "PENDING", null
        );

        // SOS 12: Gia Lai - HIGH - PENDING
        SosRequest sosGL1 = getOrCreateSos(
                citizenGL, "Siu H'Búp", "0918990011", null,
                13.9200, 107.9500, "Xã Ia Dêr, Huyện Ia Grai, Gia Lai",
                "HIGH", 3, false, false, true,
                "Sạt lở đất sườn đồi sau vườn đè hỏng một phần tường sau, 1 người bị xây xát chân, cần xe gầm cao hỗ trợ sơ tán.",
                "PENDING", null
        );

        // SOS 13: Đắk Lắk - MEDIUM - RESOLVED
        SosRequest sosDL1 = getOrCreateSos(
                citizenDL, "Y Dũng Niê", "0919112233", null,
                12.7200, 108.1000, "Xã Hòa Đông, Huyện Krông Pắc, Đắk Lắk",
                "MEDIUM", 5, true, true, false,
                "Nước suối dâng tràn qua đường giao thông liên buôn, đội cứu trợ địa phương đã đón cả gia đình an toàn.",
                "RESOLVED", LocalDateTime.now().minusHours(6)
        );

        // SOS 14: Khánh Hòa - CRITICAL - PENDING
        SosRequest sosKH1 = getOrCreateSos(
                citizenKH, "Nguyễn Văn Đạt", "0920223344", null,
                12.2600, 109.1800, "Cồn Ngọc Thảo, Phường Ngọc Hiệp, TP Nha Trang, Khánh Hòa",
                "CRITICAL", 4, true, false, true,
                "Nước sông Cái Nha Trang chảy xiết tràn vào cù lao, cô lập 4 người, có 1 bệnh nhân tiểu đường cần tiêm insulin gấp!",
                "PENDING", null
        );

        // SOS 15: Lâm Đồng - LOW - PENDING
        SosRequest sosLD1 = getOrCreateSos(
                citizenLD, "Trương Minh Tuấn", "0921334455", null,
                11.9600, 108.4300, "Xã Lát, Huyện Lạc Dương, Lâm Đồng",
                "LOW", 2, false, false, false,
                "Cây thông lớn bị ngã chắn ngang lối vào ngõ xóm, xe ô tô không ra vào được, cần đội cứu nạn mang máy cưa giải tỏa.",
                "PENDING", null
        );

        // 6. Khởi tạo Phân công cứu hộ (RescueAssignments)
        // Nhiệm vụ 1: Đà Nẵng (Đội Hải Châu) - PENDING
        initAssignment(sosDN2, teamDN01, coordCentral, "PENDING",
                "Chỉ đạo điều phối: Tiếp cận bằng xuồng máy, chuẩn bị nẹp y tế cho người bị thương ở chân",
                LocalDateTime.now().minusMinutes(15), null, null, null, null);

        // Nhiệm vụ 2: Đà Nẵng (Đội Hải Châu) - IN_PROGRESS
        initAssignment(sosDN1, teamDN01, coordCentral, "IN_PROGRESS",
                "Đội Hải Châu đã xuất kích mang 2 xuồng cao su và áo phao trẻ em. Đang di chuyển vào đường Mẹ Suốt.",
                LocalDateTime.now().minusMinutes(45), LocalDateTime.now().minusMinutes(40), null, null, null);

        // Nhiệm vụ 3: Đà Nẵng (Đội Hải Châu) - COMPLETED
        initAssignment(sosDN3, teamDN01, coordCentral, "COMPLETED",
                "Đã giải cứu an toàn 2 người, đưa về nhà sinh hoạt cộng đồng phường Thuận Phước, sức khỏe bình thường.",
                LocalDateTime.now().minusHours(4), LocalDateTime.now().minusHours(3).minusMinutes(50),
                LocalDateTime.now().minusHours(3),
                "Đã tiếp cận tầng trệt bằng xuồng nhôm, đưa 2 người dân cùng tài sản thiết yếu ra vị trí an toàn.",
                "https://images.unsplash.com/photo-1547683905-f686c993aae5?auto=format&fit=crop&w=800&q=80");

        // Nhiệm vụ 4: Thanh Hóa (Đội Nghi Sơn) - PENDING
        initAssignment(sosTH2, teamTH02, coordTH, "PENDING",
                "Điều phối Thanh Hóa: Tiếp cận phường Hải Hòa, mang theo cáng cứu thương và túi y tế.",
                LocalDateTime.now().minusMinutes(25), null, null, null, null);

        // Nhiệm vụ 5: Hà Tĩnh (Đội Cẩm Xuyên) - IN_PROGRESS
        initAssignment(sosHT1, teamHT01, coordHT, "IN_PROGRESS",
                "Đội Cẩm Xuyên đang dùng xuồng máy vượt dòng xả lũ hồ Kẻ Gỗ để tiếp cận cụ già và bé nhỏ.",
                LocalDateTime.now().minusMinutes(50), LocalDateTime.now().minusMinutes(42), null, null, null);

        // Nhiệm vụ 6: Thừa Thiên Huế (Đội Huế 01) - IN_PROGRESS
        initAssignment(sosHue1, teamHue01, coordHue, "IN_PROGRESS",
                "Đội canô số 1 đang tiếp cận phường Hương Vinh, mang theo áo phao và nhân viên y tế hỗ trợ sản phụ.",
                LocalDateTime.now().minusMinutes(60), LocalDateTime.now().minusMinutes(52), null, null, null);

        // Nhiệm vụ 7: Nghệ An (Đội NA 01) - PENDING
        initAssignment(sosNA1, teamNA01, coordNA, "PENDING",
                "Điều phối Nghệ An: Điều 1 xuồng chở mì tôm, nước ngọt và đèn pin cứu hộ vào xóm bị cô lập.",
                LocalDateTime.now().minusMinutes(20), null, null, null, null);

        // Nhiệm vụ 8: Đắk Lắk (Đội DL 01) - COMPLETED
        initAssignment(sosDL1, teamDL01, coordDL, "COMPLETED",
                "Đã đưa 5 người dân vượt qua đoạn ngập sâu an toàn về trụ sở UBND xã Hòa Đông.",
                LocalDateTime.now().minusHours(7), LocalDateTime.now().minusHours(6).minusMinutes(50),
                LocalDateTime.now().minusHours(6),
                "Hoàn thành tốt nhiệm vụ, nước chảy xiết nhưng phương tiện cơ động vượt an toàn.",
                "https://images.unsplash.com/photo-1547683905-f686c993aae5?auto=format&fit=crop&w=800&q=80");

        // Nhiệm vụ 9: Quảng Trị (Đội Huế 01 từ chối do quá xa, để phân công lại cho Đội Quảng Trị) - REJECTED
        initAssignment(sosQT1, teamHue01, coordCentral, "REJECTED",
                "Phân công dự phòng liên tỉnh",
                LocalDateTime.now().minusHours(2), null, null, null,
                "Đội đang bận nhiệm vụ khẩn cấp tại Hương Vinh (Huế), khoảng cách xa trên 80km không kịp cơ động.");

        // 7. Khởi tạo Cảnh báo thiên tai (DisasterAlerts) cho miền Trung
        initAlert("Lũ khẩn cấp trên các lưu vực sông Miền Trung",
                "Do ảnh hưởng của bão và dải hội tụ nhiệt đới, mực nước các sông Hương, sông Thu Bồn, sông Mã dâng cao trên báo động 3. Người dân vùng trũng thấp khẩn trương kê cao tài sản và gọi 112 khi cần trợ giúp.",
                "DANGER_RED", "Thanh Hóa, Nghệ An, Hà Tĩnh, Thừa Thiên Huế, Đà Nẵng, Quảng Ngãi", true, adminUser, LocalDateTime.now().plusDays(2));

        initAlert("Cảnh báo nguy cơ lũ quét và sạt lở đồi núi tại Tây Nguyên",
                "Khu vực sườn dốc thuộc Gia Lai, Đắk Lắk, Lâm Đồng có lượng mưa tích lũy trên 200mm. Nguy cơ rất cao xảy ra trượt lở taluy các tuyến đèo dốc và lũ quét cục bộ.",
                "WARNING_ORANGE", "Gia Lai, Đắk Lắk, Lâm Đồng", true, adminUser, LocalDateTime.now().plusDays(1));

        initAlert("Triều cường và sóng lớn ven biển Nam Trung Bộ",
                "Khu vực ven biển Khánh Hòa, Bình Thuận có sóng cao 3-5m kết hợp triều cường đánh tràn bờ kè. Ngư dân neo đậu tàu thuyền an toàn, cấm tàu thuyền du lịch ra khơi.",
                "WARNING_ORANGE", "Khánh Hòa, Duyên hải Nam Trung Bộ", false, adminUser, LocalDateTime.now().plusHours(36));

        initAlert("Theo dõi diễn biến áp thấp nhiệt đới trên Biển Đông",
                "Áp thấp nhiệt đới đang di chuyển theo hướng Tây Tây Bắc với sức gió cấp 7, giật cấp 9, dự kiến ảnh hưởng trực tiếp vịnh Bắc Bộ và Bắc Trung Bộ trong 48 giờ tới.",
                "NOTICE_YELLOW", "Bắc Trung Bộ (Thanh Hóa, Nghệ An, Hà Tĩnh)", false, adminUser, LocalDateTime.now().plusDays(3));

        // 8. Khởi tạo Điểm sơ tán, tạm lánh (EvacuationShelters) tại 11 tỉnh/thành
        initShelter("Cung Thể Thao Tiên Sơn Đà Nẵng", "Đường Phan Đăng Lưu, Quận Hải Châu, TP. Đà Nẵng", 16.0350, 108.2250, 1000, 450, "0236.3822112", "OPEN");
        initShelter("Nhà Đa Năng Trường ĐH Sư Phạm Đà Nẵng", "Số 459 Tôn Đức Thắng, P. Hòa Khánh Nam, Liên Chiểu, Đà Nẵng", 16.0600, 108.1600, 600, 580, "0236.3842114", "OPEN");
        initShelter("Nhà Văn Hóa Xã Hòa Châu", "Xã Hòa Châu, Huyện Hòa Vang, TP. Đà Nẵng", 15.9950, 108.1920, 300, 300, "0236.3789115", "FULL");

        initShelter("Trung Tâm Hội Nghị Tỉnh Thanh Hóa", "Đại lộ Lê Lợi, TP. Thanh Hóa, Tỉnh Thanh Hóa", 19.8050, 105.7800, 800, 200, "0237.3852112", "OPEN");
        initShelter("Trường THPT Huỳnh Thúc Kháng", "Số 62 đường Lê Hồng Phong, TP. Vinh, Tỉnh Nghệ An", 18.6750, 105.6850, 500, 180, "0238.3844112", "OPEN");
        initShelter("Trường Tiểu Học Cẩm Nhượng", "Xã Cẩm Nhượng, Huyện Cẩm Xuyên, Tỉnh Hà Tĩnh", 18.2600, 106.0100, 350, 0, "0239.3861112", "OPEN");
        initShelter("Nhà Thi Đấu Thể Thao Tỉnh Quảng Trị", "Đường Hùng Vương, TP. Đông Hà, Tỉnh Quảng Trị", 16.8100, 107.0950, 600, 150, "0233.3853112", "OPEN");
        initShelter("Trung Tâm Thể Thao Thừa Thiên Huế", "Số 01 Hà Huy Tập, TP. Huế, Tỉnh Thừa Thiên Huế", 16.4600, 107.5950, 700, 520, "0234.3823112", "OPEN");
        initShelter("Trường THPT Chuyên Lê Khiết", "Đường Quang Trung, TP. Quảng Ngãi, Tỉnh Quảng Ngãi", 15.1150, 108.7900, 400, 90, "0255.3824112", "OPEN");
        initShelter("Trung Tâm Hoạt Động Thanh Thiếu Nhi Gia Lai", "Số 60 Hai Bà Trưng, TP. Pleiku, Tỉnh Gia Lai", 13.9800, 108.0050, 500, 80, "0269.3825112", "OPEN");
        initShelter("Nhà Thi Đấu Thể Thao Tỉnh Đắk Lắk", "Đường Nguyễn Đình Chiểu, TP. Buôn Ma Thuột, Tỉnh Đắk Lắk", 12.6700, 108.0450, 800, 260, "0262.3856112", "OPEN");
        initShelter("Sân Vận Động 19/8 Nha Trang", "Đường Yersin, TP. Nha Trang, Tỉnh Khánh Hòa", 12.2450, 109.1900, 1200, 310, "0258.3827112", "OPEN");
        initShelter("Trung Tâm Văn Hóa Nghệ Thuật Lâm Đồng", "Số 01 Trần Hưng Đạo, TP. Đà Lạt, Tỉnh Lâm Đồng", 11.9420, 108.4450, 450, 0, "0263.3828112", "OPEN");

        // 9. Khởi tạo Bài đăng cộng đồng (CommunityPosts)
        CommunityPost post1 = initPost(citizen01, "Nguyễn Văn Dân", HazardType.FLOOD,
                "Đường Mẹ Suốt đoạn gần cầu mương ngập sâu hơn 1m, nước chảy rất xiết, bà con tuyệt đối không nên di chuyển bằng xe máy!",
                "Đường Mẹ Suốt, P. Hòa Khánh Nam, Liên Chiểu, Đà Nẵng", 16.0590, 108.1580,
                VerificationStatus.VERIFIED, 28, 6, null);

        CommunityPost post2 = initPost(citizen02, "Lê Thị Cứu Trợ", HazardType.ISOLATED,
                "Cầu tràn thôn Thạch Nham Tây xã Hòa Nhơn đang bị ngập 0.8m, cô lập 120 hộ dân, cần xuồng tiếp tế mì và nước uống.",
                "Thôn Thạch Nham Tây, Xã Hòa Nhơn, Hòa Vang, Đà Nẵng", 16.0120, 108.1350,
                VerificationStatus.VERIFIED, 19, 4, null);

        CommunityPost post3 = initPost(citizenHue, "Trần Thị Huế", HazardType.FLOOD,
                "Đường Bạch Đằng ven sông Hương nước tràn lên bờ đi vào nhà dân sâu 0.5m, đề nghị bà con dọn đồ lên gác lửng.",
                "Đường Bạch Đằng, Phường Phú Cát, TP. Huế", 16.4720, 107.5920,
                VerificationStatus.VERIFIED, 35, 12, null);

        CommunityPost post4 = initPost(citizenLD, "Cil Ha Kôi", HazardType.LANDSLIDE,
                "Đèo Prenn đoạn Km225 xuất hiện vết nứt sườn taluy dương, có đất đá nhỏ rơi xuống mặt đường, các bác tài chú ý giảm tốc độ.",
                "Đèo Prenn, Phường 3, TP. Đà Lạt, Lâm Đồng", 11.9150, 108.4450,
                VerificationStatus.PENDING, 14, 2, null);

        CommunityPost post5 = initPost(citizenNA, "Lê Văn Nghệ An", HazardType.TREE_FALL,
                "Cây phượng cổ thụ bị lốc xoáy quật đổ đè trúng cột điện hạ thế tại đường Nguyễn Du, gây mất điện toàn khu phố.",
                "Đường Nguyễn Du, Phường Bến Thủy, TP. Vinh, Nghệ An", 18.6650, 105.6950,
                VerificationStatus.VERIFIED, 22, 5, null);

        CommunityPost post6 = initPost(citizenKH, "Hoàng Thị Khánh Hòa", HazardType.STORM,
                "Gió rít mạnh từng cơn giật cấp 9 tại ven biển Vĩnh Hải, sóng cao đánh tràn qua kè công viên, người dân không nên ra bờ biển.",
                "Bờ kè Phạm Văn Đồng, Phường Vĩnh Hải, TP. Nha Trang, Khánh Hòa", 12.2850, 109.2050,
                VerificationStatus.PENDING, 11, 1, null);

        CommunityPost post7 = initPost(citizenGL, "Ksor H'Lâm", HazardType.OTHER,
                "Tin đồn vỡ đập thủy điện trên sông Ba là tin giả mạo, chính quyền địa phương thông báo đập vẫn vận hành xả lũ an toàn.",
                "Huyện Krông Pa, Tỉnh Gia Lai", 13.2100, 108.6800,
                VerificationStatus.REJECTED, 3, 8, null);

        // 10. Khởi tạo Lượt bình chọn bài đăng (PostUpvotes)
        if (post1 != null) {
            initUpvote(post1.getId(), citizen01.getId());
            initUpvote(post1.getId(), citizen02.getId());
            initUpvote(post1.getId(), rescuerDN01.getId());
        }
        if (post2 != null) {
            initUpvote(post2.getId(), citizen01.getId());
            initUpvote(post2.getId(), coordCentral.getId());
        }
        if (post3 != null) {
            initUpvote(post3.getId(), citizenHue.getId());
            initUpvote(post3.getId(), rescuerHue01.getId());
        }

        // 11. Khởi tạo Tệp ảnh đính kèm minh chứng hiện trường (MediaFiles)
        initMedia(sosDN1, adminUser, "sos_me_suot_1.jpg", "Hien_truong_ngap_Me_Suot.jpg",
                "https://images.unsplash.com/photo-1547683905-f686c993aae5?auto=format&fit=crop&w=800&q=80",
                245800L, "image/jpeg");
        initMedia(sosHue1, adminUser, "sos_huong_vinh_1.jpg", "Nuoc_song_Huong_dang_cao.jpg",
                "https://images.unsplash.com/photo-1547683905-f686c993aae5?auto=format&fit=crop&w=800&q=80",
                312400L, "image/jpeg");

        // 12. Khởi tạo Trang thiết bị cứu hộ (RescueEquipment) cho các đội
        for (RescueTeam t : rescueTeamRepository.findAll()) {
            initStandardEquipment(t);
        }

        // 13. Khởi tạo Thông báo mẫu (Notifications)
        if (notificationRepository.count() == 0) {
            User coordDn = userRepository.findByUsername("coord_danang").orElse(null);
            if (coordDn != null) {
                notificationRepository.save(new vn.edu.cuunan.entity.Notification(
                        coordDn, "Tiếp nhận ca SOS #1 mới",
                        "Có yêu cầu cứu nạn khẩn cấp tại Mẹ Suốt, Liên Chiểu, Đà Nẵng cần điều phối.",
                        "SOS_NEW", "/coordinator/dashboard"));
            }
            User rescuerDn = userRepository.findByUsername("rescuer_dn01").orElse(null);
            if (rescuerDn != null) {
                notificationRepository.save(new vn.edu.cuunan.entity.Notification(
                        rescuerDn, "Lệnh phân công nhiệm vụ mới",
                        "Bạn nhận lệnh cứu nạn ca SOS #1 tại K456 Tôn Đức Thắng, Đà Nẵng. Mức độ: CRITICAL.",
                        "MISSION_NEW", "/rescuer/missions"));
            }
        }

        // 14. Khởi tạo Nhật ký hoạt động kiểm toán (ActivityLogs)
        if (activityLogRepository.count() == 0) {
            activityLogRepository.save(new vn.edu.cuunan.entity.ActivityLog(
                    adminUser, "SYSTEM_INIT", "Hệ thống",
                    "Khởi tạo toàn bộ dữ liệu mẫu hệ thống cứu nạn 24/7 miền Trung thành công", "127.0.0.1"));
            activityLogRepository.save(new vn.edu.cuunan.entity.ActivityLog(
                    coordCentral, "DISPATCH_MISSION", "Nhiệm vụ #1",
                    "Điều phối Đội cứu hộ Đà Nẵng 01 tiếp cận ca cứu nạn #1 (Nguyễn Văn An)", "192.168.1.10"));
            activityLogRepository.save(new vn.edu.cuunan.entity.ActivityLog(
                    rescuerDN01, "ACCEPT_MISSION", "Nhiệm vụ #1",
                    "Đội Cứu hộ Đà Nẵng 01 chấp nhận ca cứu nạn #1", "192.168.1.25"));
            activityLogRepository.save(new vn.edu.cuunan.entity.ActivityLog(
                    rescuerDN01, "DISPATCH_EQUIPMENT", "Nhiệm vụ #1",
                    "Xuất kho trang bị: 2 Áo phao chuyên dụng, 1 Xuồng máy cao su", "192.168.1.25"));
            activityLogRepository.save(new vn.edu.cuunan.entity.ActivityLog(
                    rescuerDN01, "START_MISSION", "Nhiệm vụ #1",
                    "Đội Cứu hộ Đà Nẵng 01 xuất kích tiếp cận ca #1 (Cự ly: 8.5 km)", "192.168.1.25"));
            activityLogRepository.save(new vn.edu.cuunan.entity.ActivityLog(
                    coordCentral, "APPROVE_BUDGET_REQUEST", "Yêu cầu tiếp tế #1",
                    "Phê duyệt kinh phí 15.000.000 ₫ cho Đội Cứu hộ Đà Nẵng 01 mua nhu yếu phẩm", "192.168.1.10"));
        }

        // 15. Khởi tạo Yêu cầu tiếp tế ngân sách mẫu (TeamFundRequests)
        if (teamFundRequestRepository.count() == 0) {
            teamFundRequestRepository.save(TeamFundRequest.builder()
                    .rescueTeam(teamDN01)
                    .requester(rescuerDN01)
                    .amount(new BigDecimal("15000000"))
                    .purpose("Mua 100 thùng mì tôm, 50 bình nước khoáng 20L và lương khô tiếp tế khẩn cấp rốn lũ Hòa Vang")
                    .category("SUPPLY")
                    .urgencyLevel("CRITICAL")
                    .status("PENDING")
                    .createdAt(LocalDateTime.now().minusHours(2))
                    .updatedAt(LocalDateTime.now().minusHours(2))
                    .build());

            teamFundRequestRepository.save(TeamFundRequest.builder()
                    .rescueTeam(teamHue01)
                    .requester(rescuerHue01)
                    .amount(new BigDecimal("8000000"))
                    .purpose("Mua 300 lít xăng pha nhớt dự trữ cho 3 canô cao tốc chạy xuyên đêm vượt lũ Tam Giang")
                    .category("FUEL")
                    .urgencyLevel("HIGH")
                    .status("PENDING")
                    .createdAt(LocalDateTime.now().minusHours(5))
                    .updatedAt(LocalDateTime.now().minusHours(5))
                    .build());

            TeamFundRequest reqNA = TeamFundRequest.builder()
                    .rescueTeam(teamNA01)
                    .requester(rescuerNA01)
                    .amount(new BigDecimal("5000000"))
                    .purpose("Mua bông băng nẹp y tế, cồn sát trùng, thuốc hạ sốt cho người dân rốn lũ Hưng Nguyên")
                    .category("MEDICAL")
                    .urgencyLevel("HIGH")
                    .status("APPROVED")
                    .reviewedBy(coordCentral)
                    .reviewedAt(LocalDateTime.now().minusHours(8))
                    .createdAt(LocalDateTime.now().minusHours(12))
                    .updatedAt(LocalDateTime.now().minusHours(8))
                    .build();
            teamFundRequestRepository.save(reqNA);

            teamNA01.setBudget(teamNA01.getBudget().add(new BigDecimal("5000000")));
            rescueTeamRepository.save(teamNA01);

            log.info("  [TeamFundRequest] Đã khởi tạo 3 yêu cầu tiếp tế ngân sách mẫu (2 Chờ duyệt, 1 Đã duyệt)");
        }

        // Khởi tạo Tin nhắn trao đổi trực tuyến mẫu (RescueChatMessages)
        if (rescueChatMessageRepository != null && rescueChatMessageRepository.count() == 0) {
            RescueAssignment assignDN1 = rescueAssignmentRepository.findTopBySosRequestIdOrderByAssignedAtDesc(sosDN1.getId()).orElse(null);
            initChatMessage(sosDN1, assignDN1, "CITIZEN", "Trần Thị Bích", "0912345678",
                    "Nước ngập lên gần gác xép rồi đội ơi, nhà có 2 cháu nhỏ đang rất lạnh!", "QUICK_ALERT", LocalDateTime.now().minusMinutes(35));
            initChatMessage(sosDN1, assignDN1, "RESCUER", "Đội Cứu Hộ Hải Châu", "0905111222",
                    "Đội Hải Châu đã tiếp nhận và đang điều xuồng máy vào hẻm số 5 đường Mẹ Suốt, mọi người giữ bình tĩnh nhé!", "TEXT", LocalDateTime.now().minusMinutes(30));
            initChatMessage(sosDN1, assignDN1, "CITIZEN", "Trần Thị Bích", "0912345678",
                    "Vâng đội ơi, nhà em ở ngay cạnh cây bàng lớn, em đang vẫy đèn pin xanh báo hiệu!", "TEXT", LocalDateTime.now().minusMinutes(22));
            initChatMessage(sosDN1, assignDN1, "RESCUER", "Đội Cứu Hộ Hải Châu", "0905111222",
                    "Đã thấy tín hiệu đèn pin của gia đình, xuồng cách nhà khoảng 200m, đội đang tiếp cận ngay!", "TEXT", LocalDateTime.now().minusMinutes(15));
            log.info("  [RescueChatMessage] Đã khởi tạo 4 tin nhắn mẫu cho ca SOS #1 (Đang tiếp cận)");
        }

        log.info("==========================================================================");
        log.info(">>> [HOÀN THÀNH SEED DỮ LIỆU MIỀN TRUNG]:");
        log.info("    + 4 Roles chuẩn");
        log.info("    + {} Tài khoản người dùng (Admin, 13 Coordinators, 22 Rescuers, 12 Citizens)", userRepository.count());
        log.info("    + {} Đội cứu hộ phân bổ đầy đủ 11 tỉnh/thành Miền Trung", rescueTeamRepository.count());
        log.info("    + {} Thành viên đội cứu hộ (LEADER, RESCUER, MEDIC, DRIVER)", teamMemberRepository.count());
        log.info("    + {} Ca cứu nạn SOS thực tế đa dạng trạng thái", sosRequestRepository.count());
        log.info("    + {} Nhiệm vụ phân công tác nghiệp", rescueAssignmentRepository.count());
        log.info("    + {} Cảnh báo thiên tai khẩn cấp", disasterAlertRepository.count());
        log.info("    + {} Điểm sơ tán tạm lánh an toàn", evacuationShelterRepository.count());
        log.info("    + {} Bài đăng cộng đồng và bình chọn", communityPostRepository.count());
        log.info("    + {} Loại trang thiết bị cứu hộ được kiểm kê", rescueEquipmentRepository.count());
        log.info("    + {} Thông báo và nhật ký kiểm toán", notificationRepository.count() + activityLogRepository.count());
        log.info("==========================================================================");
    }

    private void initStandardEquipment(RescueTeam team) {
        if (rescueEquipmentRepository.countByTeam(team) == 0) {
            rescueEquipmentRepository.save(new vn.edu.cuunan.entity.RescueEquipment(team, "AO_PHAO", "Áo phao cứu sinh tiêu chuẩn", "SAFETY", "chiếc", 25, "Phao cứu sinh chuẩn hàng hải"));
            rescueEquipmentRepository.save(new vn.edu.cuunan.entity.RescueEquipment(team, "PHAO_TRON", "Phao tròn cứu hộ có dây ném 30m", "SAFETY", "chiếc", 10, "Phao ném nổi mặt nước"));
            rescueEquipmentRepository.save(new vn.edu.cuunan.entity.RescueEquipment(team, "DEN_PIN", "Đèn pin rọi cứu nạn chống nước siêu sáng", "SAFETY", "chiếc", 12, "Đèn LED sạc điện 5000mAh"));
            rescueEquipmentRepository.save(new vn.edu.cuunan.entity.RescueEquipment(team, "TUI_SO_CUU", "Túi sơ cứu y tế khẩn cấp (băng garo, sát khuẩn)", "MEDICAL", "bộ", 8, "Chuẩn y tế dã chiến"));
            rescueEquipmentRepository.save(new vn.edu.cuunan.entity.RescueEquipment(team, "BO_DAM", "Bộ đàm liên lạc Motorola cự ly 5km", "GEAR", "bộ", 10, "Tần số khẩn cấp quốc gia"));
            rescueEquipmentRepository.save(new vn.edu.cuunan.entity.RescueEquipment(team, "DAY_CUU_HO", "Dây thừng cứu nạn chịu lực (50m)", "GEAR", "cuộn", 8, "Tải trọng chịu lực 2 tấn"));
            rescueEquipmentRepository.save(new vn.edu.cuunan.entity.RescueEquipment(team, "XUONG_CAO_SU", "Xuồng cứu nạn cao su 6 người", "GEAR", "chiếc", 2, "Kèm 2 mái chèo cơ động"));
            rescueEquipmentRepository.save(new vn.edu.cuunan.entity.RescueEquipment(team, "LUONG_KHO", "Lương khô quân đội & nước lọc đóng chai", "SUPPLY", "thùng", 30, "Hạn dùng 2028"));
        }
        initVehicle(team, "VEHICLE_TRUCK_4X4", "Xe gầm cao 2 cầu 4x4", 1, "Phương tiện cứu hộ cơ động của đội");
        initVehicle(team, "VEHICLE_AMBULANCE", "Xe cứu thương dã chiến chuyên dụng", 1, "Phương tiện y tế của đội");
        initVehicle(team, "VEHICLE_BOAT_MOTOR", "Xuồng máy cứu hộ (Ca-nô cao tốc)", 1, "Phương tiện cứu hộ đường thủy");
        initVehicle(team, "VEHICLE_BOAT_RUBBER", "Xuồng cứu nạn cao su 6 người", 2, "Phương tiện cứu hộ cơ động đường thủy");

        List<RescueEquipment> equipment = rescueEquipmentRepository.findByTeamOrderByCategoryAscEquipmentNameAsc(team);
        boolean changed = false;
        for (RescueEquipment item : equipment) {
            if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                item.setUnitPrice(estimatedUnitPrice(item));
                changed = true;
            }
        }
        if (changed) rescueEquipmentRepository.saveAll(equipment);
    }

    private void initVehicle(RescueTeam team, String code, String name, int count, String notes) {
        if (rescueEquipmentRepository.findByTeamAndEquipmentCode(team, code).isEmpty()) {
            rescueEquipmentRepository.save(new RescueEquipment(team, code, name, "GEAR", "chiếc", count, notes));
        }
    }

    /** Giá tham khảo khởi tạo để chạy demo; ADMIN có thể cập nhật theo báo giá thực tế. */
    private BigDecimal estimatedUnitPrice(RescueEquipment item) {
        String code = item.getEquipmentCode() == null ? "" : item.getEquipmentCode().toUpperCase();
        String name = item.getEquipmentName() == null ? "" : item.getEquipmentName().toLowerCase();

        if (code.contains("AO_PHAO") || name.contains("áo phao")) return new BigDecimal("350000");
        if (code.contains("PHAO_TRON") || name.contains("phao tròn")) return new BigDecimal("250000");
        if (code.contains("DEN_PIN") || name.contains("đèn pin")) return new BigDecimal("450000");
        if (code.contains("TUI_SO_CUU") || name.contains("sơ cứu") || name.contains("y tế")) return new BigDecimal("650000");
        if (code.contains("BO_DAM") || name.contains("bộ đàm") || name.contains("radio")) return new BigDecimal("2500000");
        if (code.contains("DAY_CUU_HO") || name.contains("dây thừng")) return new BigDecimal("900000");
        if (code.contains("VEHICLE_AMBULANCE")) return new BigDecimal("35000000");
        if (code.contains("VEHICLE_BOAT_MOTOR")) return new BigDecimal("18000000");
        if (code.contains("VEHICLE_BOAT_RUBBER")) return new BigDecimal("12000000");
        if (code.contains("VEHICLE_TRUCK_4X4")) return new BigDecimal("850000000");
        if (code.contains("XUONG") || name.contains("xuồng") || name.contains("cano")) return new BigDecimal("18000000");
        if (code.contains("LUONG_KHO") || name.contains("lương khô")) return new BigDecimal("450000");

        if (name.contains("máy bơm") || name.contains("động cơ")) return new BigDecimal("8500000");
        if (name.contains("máy phát")) return new BigDecimal("12000000");
        if (name.contains("cáng")) return new BigDecimal("1200000");
        if (name.contains("bạt")) return new BigDecimal("350000");
        if (name.contains("nước")) return new BigDecimal("180000");
        if (name.contains("mì") || name.contains("thực phẩm")) return new BigDecimal("400000");

        String category = item.getCategory() == null ? "" : item.getCategory().toUpperCase();
        return switch (category) {
            case "MEDICAL" -> new BigDecimal("400000");
            case "GEAR" -> new BigDecimal("1500000");
            case "SUPPLY" -> new BigDecimal("350000");
            default -> new BigDecimal("500000");
        };
    }

    private Role getOrCreateRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));
    }

    private User getOrCreateUser(String username, String rawPassword, String fullName, String phone, String email, String address, Role role) {
        return userRepository.findByUsername(username).map(existingUser -> {
            // Tự động đồng bộ mật khẩu chuẩn nếu tài khoản đã tồn tại trong DB nhưng mật khẩu không khớp rawPassword (ví dụ do chạy file SQL seed cũ)
            if (!passwordEncoder.matches(rawPassword, existingUser.getPassword())) {
                existingUser.setPassword(passwordEncoder.encode(rawPassword));
                existingUser.setStatus("ACTIVE");
                userRepository.save(existingUser);
                log.info("  [User] Đã tự động cập nhật lại mật khẩu chuẩn cho tài khoản: {}", username);
            }
            return existingUser;
        }).orElseGet(() -> {
            User user = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(rawPassword))
                    .fullName(fullName)
                    .phone(phone)
                    .email(email)
                    .address(address)
                    .role(role)
                    .status("ACTIVE")
                    .build();
            User saved = userRepository.save(user);
            log.info("  [User] Đã tạo tài khoản: {} [{}]", username, role.getName());
            return saved;
        });
    }

    private RescueTeam getOrCreateRescueTeam(String teamName, User leader, String contactPhone,
                                            Double lat, Double lng, Integer memberCount,
                                            String equipmentNotes, String status) {
        RescueTeam existing = rescueTeamRepository.findByTeamName(teamName).orElse(null);
        if (existing != null) {
            if (existing.getBudget() == null || existing.getBudget().compareTo(BigDecimal.ZERO) == 0) {
                existing.setBudget(new BigDecimal("10000000"));
                return rescueTeamRepository.save(existing);
            }
            return existing;
        }
        RescueTeam team = new RescueTeam(
                null,
                teamName,
                leader,
                contactPhone,
                lat,
                lng,
                memberCount,
                equipmentNotes,
                status,
                new BigDecimal("10000000")
        );
        RescueTeam saved = rescueTeamRepository.save(team);
        log.info("  [RescueTeam] Đã tạo đội cứu hộ: {} (Đội trưởng: {}, Ngân sách: {})", teamName, leader != null ? leader.getUsername() : "N/A", saved.getFormattedBudget());
        return saved;
    }

    private void initTeamMember(RescueTeam team, User user, String memberRole, Double lat, Double lng) {
        if (team == null || user == null) return;
        if (!teamMemberRepository.existsByTeamIdAndUserId(team.getId(), user.getId())) {
            TeamMember tm = new TeamMember(null, team, user, memberRole, lat, lng, LocalDateTime.now());
            teamMemberRepository.save(tm);
        }
    }

    private SosRequest getOrCreateSos(User sender, String contactName, String contactPhone, String secondaryPhone,
                                      Double lat, Double lng, String addressDetail,
                                      String urgencyLevel, Integer peopleCount,
                                      Boolean hasElderly, Boolean hasChildren, Boolean hasInjured,
                                      String description, String status, LocalDateTime resolvedAt) {
        return sosRequestRepository.findFirstByContactPhone(contactPhone).orElseGet(() -> {
            SosRequest sos = SosRequest.builder()
                    .sender(sender)
                    .contactName(contactName)
                    .contactPhone(contactPhone)
                    .secondaryPhone(secondaryPhone)
                    .latitude(lat)
                    .longitude(lng)
                    .addressDetail(addressDetail)
                    .urgencyLevel(urgencyLevel)
                    .peopleCount(peopleCount)
                    .hasElderly(hasElderly)
                    .hasChildren(hasChildren)
                    .hasInjured(hasInjured)
                    .description(description)
                    .status(status)
                    .resolvedAt(resolvedAt)
                    .build();
            return sosRequestRepository.save(sos);
        });
    }

    private void initAssignment(SosRequest sos, RescueTeam team, User assignedBy, String status,
                                String reportNotes, LocalDateTime assignedAt, LocalDateTime acceptedAt,
                                LocalDateTime completedAt, String reportImageUrl, String rejectionReason) {
        if (sos == null || team == null) return;
        List<RescueAssignment> existing = rescueAssignmentRepository.findBySosRequestId(sos.getId());
        boolean alreadyAssignedToTeam = existing.stream().anyMatch(a -> a.getTeam().getId().equals(team.getId()));
        if (!alreadyAssignedToTeam) {
            RescueAssignment a = new RescueAssignment();
            a.setSosRequest(sos);
            a.setTeam(team);
            a.setAssignedBy(assignedBy);
            a.setStatus(status);
            a.setReportNotes(reportNotes);
            a.setAssignedAt(assignedAt != null ? assignedAt : LocalDateTime.now());
            a.setAcceptedAt(acceptedAt);
            a.setCompletedAt(completedAt);
            a.setReportImageUrl(reportImageUrl);
            a.setRejectionReason(rejectionReason);
            rescueAssignmentRepository.save(a);
        }
    }

    private void initAlert(String title, String content, String severity, String region, boolean isBroadcast, User createdBy, LocalDateTime expiresAt) {
        if (!disasterAlertRepository.existsByTitle(title)) {
            DisasterAlert alert = new DisasterAlert();
            alert.setTitle(title);
            alert.setContent(content);
            alert.setSeverity(severity);
            alert.setAffectedRegion(region);
            alert.setIsBroadcast(isBroadcast);
            alert.setCreatedBy(createdBy);
            alert.setExpiresAt(expiresAt);
            disasterAlertRepository.save(alert);
        }
    }

    private void initShelter(String name, String address, Double lat, Double lng, Integer capacity, Integer occupancy, String phone, String status) {
        if (evacuationShelterRepository.findByName(name).isEmpty()) {
            EvacuationShelter shelter = new EvacuationShelter(null, name, address, lat, lng, capacity, occupancy, phone, status);
            evacuationShelterRepository.save(shelter);
        }
    }

    private CommunityPost initPost(User author, String authorName, HazardType hazardType, String content,
                                  String locationName, Double lat, Double lng, VerificationStatus status,
                                  int upvotes, int comments, SosRequest sosRequest) {
        Optional<CommunityPost> postOpt = (locationName != null) ? communityPostRepository.findFirstByLocationName(locationName) : Optional.empty();
        if (postOpt.isPresent()) {
            return postOpt.get();
        }
        CommunityPost post = new CommunityPost(null, author, authorName, hazardType, content, locationName, lat, lng,
                null, status, upvotes, comments, sosRequest);
        return communityPostRepository.save(post);
    }

    private void initUpvote(Long postId, Long userId) {
        if (postId == null || userId == null) return;
        if (!postUpvoteRepository.existsByPostIdAndUserId(postId, userId)) {
            postUpvoteRepository.save(new PostUpvote(postId, userId));
        }
    }

    private void initMedia(SosRequest sos, User uploader, String fileName, String origName, String url, Long size, String type) {
        if (sos == null) return;
        if (!mediaFileRepository.existsByFileName(fileName)) {
            MediaFile file = MediaFile.builder()
                    .sosRequest(sos)
                    .uploadedBy(uploader)
                    .fileName(fileName)
                    .originalName(origName)
                    .fileUrl(url)
                    .fileSize(size)
                    .contentType(type)
                    .storageType("LOCAL")
                    .build();
            mediaFileRepository.save(file);
        }
    }
    private void initChatMessage(SosRequest sos, RescueAssignment assignment, String senderType, String senderName,
                                 String senderPhone, String content, String messageType, LocalDateTime createdAt) {
        if (sos == null || rescueChatMessageRepository == null) return;
        RescueChatMessage msg = new RescueChatMessage();
        msg.setSosRequest(sos);
        msg.setAssignment(assignment);
        msg.setSenderType(senderType);
        msg.setSenderName(senderName);
        msg.setSenderPhone(senderPhone);
        msg.setContent(content);
        msg.setMessageType(messageType != null ? messageType : "TEXT");
        msg.setIsRead(false);
        msg.setCreatedAt(createdAt != null ? createdAt : LocalDateTime.now());
        rescueChatMessageRepository.save(msg);
    }
}