package vn.edu.cuunan.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cuunan.dto.AdminUserUpdateDto;
import vn.edu.cuunan.dto.BroadcastAlertDto;
import vn.edu.cuunan.dto.RescueTeamAdminDto;
import vn.edu.cuunan.entity.DisasterAlert;
import vn.edu.cuunan.entity.RescueTeam;
import vn.edu.cuunan.entity.Role;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.DisasterAlertRepository;
import vn.edu.cuunan.repository.RescueTeamRepository;
import vn.edu.cuunan.repository.RoleRepository;
import vn.edu.cuunan.repository.SosRequestRepository;
import vn.edu.cuunan.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private static final Set<String> ROLES = Set.of("ROLE_CITIZEN", "ROLE_RESCUER", "ROLE_COORDINATOR", "ROLE_ADMIN");
    private static final Set<String> TEAM_STATUSES = Set.of("AVAILABLE", "BUSY", "OFFLINE");
    private static final Set<String> ALERT_SEVERITIES = Set.of("DANGER_RED", "WARNING_ORANGE", "NOTICE_YELLOW");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RescueTeamRepository rescueTeamRepository;
    private final SosRequestRepository sosRequestRepository;
    private final DisasterAlertRepository disasterAlertRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public AdminService(UserRepository userRepository, RoleRepository roleRepository,
                        RescueTeamRepository rescueTeamRepository, SosRequestRepository sosRequestRepository,
                        DisasterAlertRepository disasterAlertRepository, SimpMessagingTemplate messagingTemplate) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.rescueTeamRepository = rescueTeamRepository;
        this.sosRequestRepository = sosRequestRepository;
        this.disasterAlertRepository = disasterAlertRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional(readOnly = true)
    public List<User> getUsers() { return userRepository.findAll(); }

    @Transactional
    public User updateUser(Long id, AdminUserUpdateDto dto) {
        String roleName = dto.getRole().trim().toUpperCase(Locale.ROOT);
        String status = dto.getStatus().trim().toUpperCase(Locale.ROOT);
        if (!ROLES.contains(roleName)) throw new IllegalArgumentException("Vai trò không hợp lệ");
        if (!Set.of("ACTIVE", "BLOCKED").contains(status)) throw new IllegalArgumentException("Trạng thái tài khoản không hợp lệ");
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng #" + id));
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new IllegalArgumentException("Vai trò chưa được khởi tạo: " + roleName));
        boolean removingActiveAdmin = user.getRole() != null
            && "ROLE_ADMIN".equals(user.getRole().getName())
            && "ACTIVE".equalsIgnoreCase(user.getStatus())
            && (!"ROLE_ADMIN".equals(roleName) || !"ACTIVE".equals(status));
        if (removingActiveAdmin) {
            long activeAdmins = userRepository.findAll().stream()
                .filter(candidate -> candidate.getRole() != null
                    && "ROLE_ADMIN".equals(candidate.getRole().getName())
                    && "ACTIVE".equalsIgnoreCase(candidate.getStatus()))
                .count();
            if (activeAdmins <= 1) throw new IllegalStateException("Không thể khóa hoặc hạ quyền quản trị viên hoạt động cuối cùng");
        }
        user.setRole(role);
        user.setStatus(status);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<RescueTeam> getTeams() { return rescueTeamRepository.findAll(); }

    @Transactional
    public RescueTeam createTeam(RescueTeamAdminDto dto) {
        RescueTeam team = new RescueTeam();
        applyTeamChanges(team, dto);
        return rescueTeamRepository.save(team);
    }

    @Transactional
    public RescueTeam updateTeam(Long id, RescueTeamAdminDto dto) {
        RescueTeam team = rescueTeamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đội cứu hộ #" + id));
        applyTeamChanges(team, dto);
        return rescueTeamRepository.save(team);
    }

    private void applyTeamChanges(RescueTeam team, RescueTeamAdminDto dto) {
        String status = dto.getStatus() == null || dto.getStatus().isBlank()
                ? (team.getStatus() == null ? "AVAILABLE" : team.getStatus().toUpperCase(Locale.ROOT))
                : dto.getStatus().trim().toUpperCase(Locale.ROOT);
        if (!TEAM_STATUSES.contains(status)) throw new IllegalArgumentException("Trạng thái đội không hợp lệ");
        if (dto.getCurrentLatitude() != null && (dto.getCurrentLatitude() < -90 || dto.getCurrentLatitude() > 90))
            throw new IllegalArgumentException("Vĩ độ đội không hợp lệ");
        if (dto.getCurrentLongitude() != null && (dto.getCurrentLongitude() < -180 || dto.getCurrentLongitude() > 180))
            throw new IllegalArgumentException("Kinh độ đội không hợp lệ");
        User leader = null;
        if (dto.getLeaderId() != null) {
            leader = userRepository.findById(dto.getLeaderId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đội trưởng được chọn"));
            Role rescuerRole = roleRepository.findByName("ROLE_RESCUER").orElse(null);
            if (rescuerRole == null || leader.getRole() == null || !rescuerRole.getId().equals(leader.getRole().getId()))
                throw new IllegalArgumentException("Đội trưởng phải có vai trò Đội cứu hộ");
        }
        team.setTeamName(dto.getTeamName().trim());
        team.setLeader(leader);
        team.setContactPhone(dto.getContactPhone().trim());
        team.setCurrentLatitude(dto.getCurrentLatitude());
        team.setCurrentLongitude(dto.getCurrentLongitude());
        team.setMemberCount(dto.getMemberCount() == null ? 0 : Math.max(dto.getMemberCount(), 0));
        team.setEquipmentNotes(dto.getEquipmentNotes());
        team.setStatus(status);
    }

    @Transactional
    public DisasterAlert broadcast(BroadcastAlertDto dto, User admin) {
        String severity = dto.getSeverity().trim().toUpperCase(Locale.ROOT);
        if (!ALERT_SEVERITIES.contains(severity)) throw new IllegalArgumentException("Mức cảnh báo không hợp lệ");
        DisasterAlert alert = new DisasterAlert();
        alert.setTitle(dto.getTitle().trim());
        alert.setContent(dto.getContent().trim());
        alert.setSeverity(severity);
        alert.setAffectedRegion(dto.getAffectedRegion() == null ? null : dto.getAffectedRegion().trim());
        alert.setIsBroadcast(true);
        alert.setCreatedBy(admin);
        DisasterAlert saved = disasterAlertRepository.save(alert);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", saved.getId());
        payload.put("title", saved.getTitle());
        payload.put("content", saved.getContent());
        payload.put("severity", saved.getSeverity());
        payload.put("affectedRegion", saved.getAffectedRegion());
        payload.put("createdAt", saved.getCreatedAt());
        messagingTemplate.convertAndSend("/topic/broadcast-alerts", payload);
        return saved;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics() {
        List<SosRequest> requests = sosRequestRepository.findAll();
        Map<String, Long> byUrgency = new LinkedHashMap<>();
        for (String urgency : List.of("CRITICAL", "HIGH", "MEDIUM", "LOW"))
            byUrgency.put(urgency, sosRequestRepository.countByUrgencyLevel(urgency));
        Map<String, Long> byRegion = requests.stream()
                .collect(Collectors.groupingBy(this::regionOf, TreeMap::new, Collectors.counting()));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalCases", requests.size());
        result.put("pendingCases", sosRequestRepository.countByStatus("PENDING"));
        result.put("activeCases", sosRequestRepository.countByStatus("DISPATCHED") + sosRequestRepository.countByStatus("IN_PROGRESS"));
        result.put("resolvedCases", sosRequestRepository.countByStatus("RESOLVED"));
        result.put("rescuedPeople", Optional.ofNullable(sosRequestRepository.sumRescuedPeople()).orElse(0L));
        result.put("byUrgency", byUrgency);
        result.put("byRegion", byRegion);
        return result;
    }

    private String regionOf(SosRequest request) {
        String address = request.getAddressDetail();
        if (address == null || address.isBlank()) return "Chưa xác định";
        String[] parts = address.split(",");
        return parts.length > 1 ? parts[parts.length - 2].trim() : parts[0].trim();
    }
}
