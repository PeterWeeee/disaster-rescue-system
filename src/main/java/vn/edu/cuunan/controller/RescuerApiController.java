package vn.edu.cuunan.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.cuunan.dto.*;
import vn.edu.cuunan.entity.*;
import vn.edu.cuunan.security.CustomUserDetails;
import vn.edu.cuunan.service.BudgetService;
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
    private final BudgetService budgetService;

    public RescuerApiController(RescuerService rescuerService,
                                EquipmentService equipmentService,
                                BudgetService budgetService) {
        this.rescuerService = rescuerService;
        this.equipmentService = equipmentService;
        this.budgetService = budgetService;
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

    @PostMapping("/budget-requests")
    public ResponseEntity<?> requestBudget(@Valid @RequestBody TeamFundRequestDto dto,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Chưa đăng nhập"));
        }
        try {
            TeamFundRequest request = budgetService.createFundRequest(userDetails.getUser(), dto);
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("message", "Đã gửi yêu cầu tiếp tế ngân sách tới Người Điều Phối thành công!");
            res.put("requestId", request.getId());
            res.put("amount", request.getFormattedAmount());
            res.put("status", request.getStatus());
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Lỗi tạo yêu cầu tiếp tế: " + ex.getMessage()));
        }
    }

    @GetMapping("/budget-requests")
    public ResponseEntity<?> getBudgetRequests(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Chưa đăng nhập"));
        }
        Optional<RescueTeam> teamOpt = rescuerService.getMyTeam(userDetails.getUser());
        if (teamOpt.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(budgetService.getRequestsByTeam(teamOpt.get().getId()));
    }

    @GetMapping({"/assignments/{id}/route", "/assignments/{id}/route-terrain-analysis"})
    public ResponseEntity<?> getRouteAnalysis(@PathVariable("id") Long id,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Chưa đăng nhập"));
        }
        try {
            User user = userDetails.getUser();
            RouteTerrainAnalysisDto result = rescuerService.getRouteAndTerrainAnalysis(id, user);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Lỗi phân tích lộ trình: " + ex.getMessage()));
        }
    }

    @PostMapping("/assignments/{id}/accept")
    public ResponseEntity<?> acceptMission(@PathVariable("id") Long id,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Chưa đăng nhập"));
        }
        try {
            User user = userDetails.getUser();
            RescueAssignment assignment = rescuerService.acceptAssignment(id, user);
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("message", "Đã tiếp nhận nhiệm vụ cứu hộ #" + id + " thành công!");
            res.put("assignmentId", assignment.getId());
            res.put("status", assignment.getStatus());
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @PostMapping("/assignments/{id}/reject")
    public ResponseEntity<?> rejectMission(@PathVariable("id") Long id,
                                           @RequestBody(required = false) Map<String, String> body,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Chưa đăng nhập"));
        }
        String reason = (body != null) ? body.get("reason") : null;
        try {
            User user = userDetails.getUser();
            RescueAssignment assignment = rescuerService.rejectAssignment(id, user, reason);
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("message", "Đã từ chối nhiệm vụ #" + id + ". Đã thông báo tới điều phối viên.");
            res.put("assignmentId", assignment.getId());
            res.put("status", assignment.getStatus());
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @PostMapping("/assignments/{id}/start")
    public ResponseEntity<?> startMission(@PathVariable("id") Long id,
                                          @RequestBody(required = false) StartMissionRequestDto dto,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Chưa đăng nhập"));
        }
        try {
            User user = userDetails.getUser();
            RescueAssignment assignment = rescuerService.startMoving(id, user, dto);
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("message", "Đội cứu hộ đang trên đường tiếp cận hiện trường!");
            res.put("assignmentId", assignment.getId());
            res.put("status", assignment.getStatus());
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @PostMapping(value = "/assignments/{id}/complete", consumes = {"multipart/form-data"})
    public ResponseEntity<?> completeMission(@PathVariable("id") Long id,
                                             @RequestParam("reportNotes") String reportNotes,
                                             @RequestParam(value = "reportImage", required = false) MultipartFile reportImage,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Chưa đăng nhập"));
        }
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
