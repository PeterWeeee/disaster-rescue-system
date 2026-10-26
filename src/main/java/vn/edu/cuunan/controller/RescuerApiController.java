package vn.edu.cuunan.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.cuunan.dto.RejectAssignmentDto;
import vn.edu.cuunan.entity.RescueAssignment;
import vn.edu.cuunan.entity.RescueTeam;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.security.CustomUserDetails;
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

    public RescuerApiController(RescuerService rescuerService) {
        this.rescuerService = rescuerService;
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
            res.put("assignment", assignment);
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
            res.put("assignment", assignment);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @PostMapping("/assignments/{id}/start")
    public ResponseEntity<?> startMoving(@PathVariable("id") Long id,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails.getUser();
            RescueAssignment assignment = rescuerService.startMoving(id, user);
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("message", "Đội đang tiếp cận hiện trường nhiệm vụ #" + id);
            res.put("assignment", assignment);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
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
            res.put("assignment", assignment);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Lỗi tải ảnh hoặc hoàn thành nhiệm vụ: " + ex.getMessage()));
        }
    }
}
