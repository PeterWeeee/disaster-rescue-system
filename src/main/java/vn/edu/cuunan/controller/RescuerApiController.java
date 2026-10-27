package vn.edu.cuunan.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.cuunan.dto.*;
import vn.edu.cuunan.entity.EquipmentAssignment;
import vn.edu.cuunan.entity.RescueAssignment;
import vn.edu.cuunan.entity.RescueTeam;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.security.CustomUserDetails;
import vn.edu.cuunan.service.EquipmentService;
import vn.edu.cuunan.service.RescuerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/rescuer")
@PreAuthorize("hasAnyRole('RESCUER', 'ADMIN')")
public class RescuerApiController {

    private final RescuerService rescuerService;
    private final EquipmentService equipmentService;

    public RescuerApiController(RescuerService rescuerService, EquipmentService equipmentService) {
        this.rescuerService = rescuerService;
        this.equipmentService = equipmentService;
    }

    @GetMapping("/my-team")
    public ResponseEntity<?> getMyTeam(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Chưa đăng nhập"));
        }
        Optional<RescueTeam> team = rescuerService.getMyTeam(userDetails.getUser());
        if (team.isEmpty()) {
            return ResponseEntity.ok(Map.of("hasTeam", false, "message", "Bạn chưa được chỉ định vào đội cứu hộ nào"));
        }
        return ResponseEntity.ok(Map.of("hasTeam", true, "team", team.get()));
    }

    @GetMapping("/resources")
    public ResponseEntity<?> getResources(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Chưa đăng nhập"));
        }
        Optional<RescueTeam> team = rescuerService.getMyTeam(userDetails.getUser());
        if (team.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Tài khoản chưa thuộc đội cứu hộ nào"));
        }
        TeamResourcesDto resources = equipmentService.getTeamResources(team.get());
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/assignments")
    public ResponseEntity<List<RescueAssignment>> getAssignments(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(rescuerService.getMyAssignments(userDetails.getUser()));
    }

    @PostMapping("/assignments/{id}/accept")
    public ResponseEntity<?> acceptAssignment(@PathVariable("id") Long id,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails.getUser();
            RescueAssignment assignment = rescuerService.acceptAssignment(id, user);
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("message", "Đã chấp nhận nhiệm vụ cứu nạn #" + id);
            res.put("assignmentId", assignment.getId());
            res.put("status", assignment.getStatus());
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @PostMapping("/assignments/{id}/reject")
    public ResponseEntity<?> rejectAssignment(@PathVariable("id") Long id,
                                              @Valid @RequestBody RejectAssignmentDto dto,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails.getUser();
            RescueAssignment assignment = rescuerService.rejectAssignment(id, user, dto.getReason());
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("message", "Đã từ chối nhiệm vụ #" + id + ", ca cứu nạn đã được trả về hàng chờ điều phối");
            res.put("assignmentId", assignment.getId());
            res.put("status", assignment.getStatus());
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @GetMapping("/assignments/{id}/route-terrain-analysis")
    public ResponseEntity<?> getRouteTerrainAnalysis(@PathVariable("id") Long id,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails != null ? userDetails.getUser() : null;
            RouteTerrainAnalysisDto analysis = rescuerService.getRouteAndTerrainAnalysis(id, user);
            return ResponseEntity.ok(analysis);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Lỗi phân tích địa hình: " + ex.getMessage()));
        }
    }

    @GetMapping("/assignments/{id}/route")
    public ResponseEntity<?> getRoute(@PathVariable("id") Long id,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        return getRouteTerrainAnalysis(id, userDetails);
    }

    @PostMapping("/assignments/{id}/start")
    public ResponseEntity<?> startMoving(@PathVariable("id") Long id,
                                         @RequestBody(required = false) StartMissionRequestDto dto,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails != null ? userDetails.getUser() : null;
            RescueAssignment assignment = rescuerService.startMoving(id, user, dto);

            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("message", "Đội đã xuất kích và đang tiếp cận hiện trường nhiệm vụ #" + id);
            res.put("assignmentId", assignment.getId());
            res.put("status", assignment.getStatus());
            res.put("vehicleType", assignment.getVehicleType());
            res.put("selectedEquipment", assignment.getSelectedEquipment());
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Lỗi nội bộ: " + ex.getMessage()));
        }
    }

    @PostMapping("/assignments/{id}/equipment/dispatch")
    public ResponseEntity<?> dispatchEquipment(@PathVariable("id") Long id,
                                               @RequestBody EquipmentDispatchDto dto,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails != null ? userDetails.getUser() : null;
            List<EquipmentAssignment> result = equipmentService.dispatchEquipment(id, dto, user);
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("message", "Đã cấp phát " + result.size() + " loại trang bị cho nhiệm vụ #" + id);
            res.put("count", result.size());
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @PostMapping("/assignments/{id}/equipment/return")
    public ResponseEntity<?> returnEquipment(@PathVariable("id") Long id,
                                             @RequestBody EquipmentReturnDto dto,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails != null ? userDetails.getUser() : null;
            equipmentService.returnEquipment(id, dto, user);
            return ResponseEntity.ok(Map.of("success", true, "message", "Đã thu hồi và kiểm kê trang bị nhiệm vụ #" + id));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @GetMapping("/assignments/{id}/equipment")
    public ResponseEntity<?> getAssignedEquipment(@PathVariable("id") Long id) {
        List<EquipmentAssignment> list = equipmentService.getDispatchedEquipmentForMission(id);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/assignments/{id}/complete")
    public ResponseEntity<?> completeAssignment(@PathVariable("id") Long id,
                                                @RequestParam(value = "reportNotes", required = false) String reportNotes,
                                                @RequestParam(value = "reportImage", required = false) MultipartFile reportImage,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails.getUser();
            RescueAssignment assignment = rescuerService.completeAssignment(id, user, reportNotes, reportImage);
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("message", "Chúc mừng! Báo cáo hoàn tất ca cứu nạn #" + id + " thành công!");
            res.put("assignmentId", assignment.getId());
            res.put("status", assignment.getStatus());
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Lỗi tải ảnh hoặc hoàn thành nhiệm vụ: " + ex.getMessage()));
        }
    }

    @PostMapping("/assignments/{id}/location")
    public ResponseEntity<?> updateLocation(@PathVariable("id") Long id,
                                            @RequestBody Map<String, Double> payload,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Chưa đăng nhập"));
        }
        Double lat = payload.get("latitude");
        Double lng = payload.get("longitude");
        if (lat == null || lng == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Thiếu tọa độ GPS"));
        }
        try {
            User user = userDetails.getUser();
            Map<String, Object> result = rescuerService.updateTeamLocation(id, user, lat, lng);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }
}
