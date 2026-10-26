package vn.edu.cuunan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import vn.edu.cuunan.dto.AdminUserUpdateDto;
import vn.edu.cuunan.dto.BroadcastAlertDto;
import vn.edu.cuunan.entity.DisasterAlert;
import vn.edu.cuunan.entity.Role;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.DisasterAlertRepository;
import vn.edu.cuunan.repository.RescueTeamRepository;
import vn.edu.cuunan.repository.RoleRepository;
import vn.edu.cuunan.repository.SosRequestRepository;
import vn.edu.cuunan.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private RescueTeamRepository rescueTeamRepository;
    @Mock private SosRequestRepository sosRequestRepository;
    @Mock private DisasterAlertRepository disasterAlertRepository;
    @Mock private SimpMessagingTemplate messagingTemplate;

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminService(userRepository, roleRepository, rescueTeamRepository,
                sosRequestRepository, disasterAlertRepository, messagingTemplate);
    }

    @Test
    void updateUserChangesRoleAndStatus() {
        Role citizenRole = Role.builder().id(1).name("ROLE_CITIZEN").build();
        User user = User.builder().id(5L).role(Role.builder().id(2).name("ROLE_RESCUER").build()).status("ACTIVE").build();
        AdminUserUpdateDto dto = new AdminUserUpdateDto();
        dto.setRole("ROLE_CITIZEN");
        dto.setStatus("BLOCKED");
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_CITIZEN")).thenReturn(Optional.of(citizenRole));
        when(userRepository.save(user)).thenReturn(user);

        User result = adminService.updateUser(5L, dto);

        assertSame(citizenRole, result.getRole());
        assertEquals("BLOCKED", result.getStatus());
    }

    @Test
    void cannotDemoteLastActiveAdmin() {
        User admin = User.builder().id(1L).role(Role.builder().id(4).name("ROLE_ADMIN").build()).status("ACTIVE").build();
        AdminUserUpdateDto dto = new AdminUserUpdateDto();
        dto.setRole("ROLE_CITIZEN");
        dto.setStatus("ACTIVE");
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(roleRepository.findByName("ROLE_CITIZEN")).thenReturn(Optional.of(Role.builder().id(1).name("ROLE_CITIZEN").build()));
        when(userRepository.findAll()).thenReturn(List.of(admin));

        assertThrows(IllegalStateException.class, () -> adminService.updateUser(1L, dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void broadcastIsPersistedAndPublishedToTopic() {
        BroadcastAlertDto dto = new BroadcastAlertDto();
        dto.setTitle("Cảnh báo lũ");
        dto.setContent("Di chuyển đến nơi cao");
        dto.setSeverity("DANGER_RED");
        dto.setAffectedRegion("Hòa Vang");
        User admin = User.builder().id(2L).build();
        when(disasterAlertRepository.save(any(DisasterAlert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DisasterAlert result = adminService.broadcast(dto, admin);

        assertTrue(result.getIsBroadcast());
        assertSame(admin, result.getCreatedBy());
        assertEquals("DANGER_RED", result.getSeverity());
        verify(messagingTemplate).convertAndSend(eq("/topic/broadcast-alerts"), any());
    }
}
