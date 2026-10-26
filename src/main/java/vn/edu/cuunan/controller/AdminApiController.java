package vn.edu.cuunan.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.cuunan.dto.AdminUserUpdateDto;
import vn.edu.cuunan.dto.BroadcastAlertDto;
import vn.edu.cuunan.dto.RescueTeamAdminDto;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.security.CustomUserDetails;
import vn.edu.cuunan.service.AdminService;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminApiController {

    private final AdminService adminService;

    public AdminApiController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> users() { return ResponseEntity.ok(adminService.getUsers()); }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody AdminUserUpdateDto dto,
                                        @AuthenticationPrincipal CustomUserDetails principal) {
        try {
            if (principal != null && principal.getId().equals(id)
                    && (!"ROLE_ADMIN".equalsIgnoreCase(dto.getRole()) || "BLOCKED".equalsIgnoreCase(dto.getStatus()))) {
                return ResponseEntity.badRequest().body(Map.of("message", "Không thể tự khóa hoặc hạ quyền tài khoản quản trị đang đăng nhập"));
            }
            return ResponseEntity.ok(adminService.updateUser(id, dto));
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/teams")
    public ResponseEntity<?> teams() { return ResponseEntity.ok(adminService.getTeams()); }

    @PostMapping("/teams")
    public ResponseEntity<?> createTeam(@Valid @RequestBody RescueTeamAdminDto dto) {
        try {
            return ResponseEntity.ok(adminService.createTeam(dto));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @PutMapping("/teams/{id}")
    public ResponseEntity<?> updateTeam(@PathVariable Long id, @Valid @RequestBody RescueTeamAdminDto dto) {
        try {
            return ResponseEntity.ok(adminService.updateTeam(id, dto));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> statistics() { return ResponseEntity.ok(adminService.getStatistics()); }

    @PostMapping("/broadcast")
    public ResponseEntity<?> broadcast(@Valid @RequestBody BroadcastAlertDto dto,
                                       @AuthenticationPrincipal CustomUserDetails principal) {
        try {
            User admin = principal == null ? null : principal.getUser();
            return ResponseEntity.ok(adminService.broadcast(dto, admin));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }
}