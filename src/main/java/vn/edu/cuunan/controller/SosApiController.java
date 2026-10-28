package vn.edu.cuunan.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.cuunan.dto.SosRequestDto;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.security.CustomUserDetails;
import vn.edu.cuunan.service.SosService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sos")
public class SosApiController {

    private final SosService sosService;

    public SosApiController(SosService sosService) {
        this.sosService = sosService;
    }

    /**
     * REST API tiếp nhận yêu cầu cứu nạn SOS khẩn cấp.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> submitSosApi(@Valid @RequestBody SosRequestDto dto,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long senderId = (userDetails != null) ? userDetails.getId() : null;
        SosRequest saved = sosService.createSosRequest(dto, senderId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Phát tín hiệu cứu nạn khẩn cấp thành công! Hệ thống đang điều phối đội ứng cứu.");
        response.put("data", saved);
        response.put("trackUrl", "/sos/track/" + saved.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * REST API lấy chi tiết một ca cứu nạn theo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSosDetailApi(@PathVariable("id") Long id) {
        SosRequest sos = sosService.getSosById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", sos);
        return ResponseEntity.ok(response);
    }

    /**
     * REST API lấy danh sách ca cứu nạn (hỗ trợ lọc theo trạng thái hoặc tải toàn bộ cho Bản đồ Live).
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listSosApi(@RequestParam(value = "status", required = false) String status) {
        List<SosRequest> list;
        if (status != null && !status.trim().isEmpty()) {
            list = sosService.getSosByStatus(status.trim().toUpperCase());
        } else {
            list = sosService.getAllSosRequests();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", list.size());
        response.put("data", list);
        return ResponseEntity.ok(response);
    }

    /**
     * REST API cập nhật trạng thái ca SOS (Dành cho Điều phối viên / Đội cứu hộ).
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatusApi(@PathVariable("id") Long id,
                                                               @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        if (newStatus == null || newStatus.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Thiếu tham số 'status'"));
        }

        SosRequest updated = sosService.updateSosStatus(id, newStatus);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Cập nhật trạng thái thành công");
        response.put("data", updated);
        return ResponseEntity.ok(response);
    }
}
