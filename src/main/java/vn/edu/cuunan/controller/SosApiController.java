package vn.edu.cuunan.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.cuunan.dto.RouteTerrainAnalysisDto;
import vn.edu.cuunan.dto.SosRequestDto;
import vn.edu.cuunan.entity.RescueAssignment;
import vn.edu.cuunan.entity.RescueTeam;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.repository.RescueAssignmentRepository;
import vn.edu.cuunan.repository.SosRequestRepository;
import vn.edu.cuunan.security.CustomUserDetails;
import vn.edu.cuunan.service.SosService;
import vn.edu.cuunan.service.TerrainAnalysisService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sos")
public class SosApiController {

    private final SosService sosService;
    private final RescueAssignmentRepository rescueAssignmentRepository;
    private final TerrainAnalysisService terrainAnalysisService;
    private final SosRequestRepository sosRequestRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public SosApiController(SosService sosService,
                            RescueAssignmentRepository rescueAssignmentRepository,
                            TerrainAnalysisService terrainAnalysisService,
                            SosRequestRepository sosRequestRepository,
                            @Autowired(required = false) SimpMessagingTemplate messagingTemplate) {
        this.sosService = sosService;
        this.rescueAssignmentRepository = rescueAssignmentRepository;
        this.terrainAnalysisService = terrainAnalysisService;
        this.sosRequestRepository = sosRequestRepository;
        this.messagingTemplate = messagingTemplate;
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

    /**
     * REST API lấy dữ liệu tuyến đường OSRM, khoảng cách và thời gian đồng bộ cho ca SOS.
     */
    @GetMapping("/{id}/route")
    public ResponseEntity<Map<String, Object>> getSosRouteApi(@PathVariable("id") Long id) {
        SosRequest sos = sosService.getSosById(id);
        if (rescueAssignmentRepository == null || terrainAnalysisService == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Dịch vụ định tuyến chưa khả dụng"));
        }
        Optional<RescueAssignment> optAssignment = rescueAssignmentRepository.findTopBySosRequestIdOrderByAssignedAtDesc(sos.getId());
        if (optAssignment.isEmpty() || optAssignment.get().getTeam() == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Ca cứu nạn chưa có đội cứu hộ"));
        }
        RescueAssignment assignment = optAssignment.get();
        RescueTeam team = assignment.getTeam();
        RouteTerrainAnalysisDto analysis = terrainAnalysisService.analyzeRouteAndTerrain(team, sos, assignment.getId());
        if (analysis != null && analysis.getRouteDistanceKm() != null) {
            assignment.setRouteDistanceKm(analysis.getRouteDistanceKm());
            if (analysis.getRouteDurationMinutes() != null) {
                assignment.setRouteDurationMinutes(analysis.getRouteDurationMinutes());
            }
            rescueAssignmentRepository.save(assignment);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("sosId", sos.getId());
        response.put("teamId", team.getId());
        response.put("teamName", team.getTeamName());
        response.put("routeDistanceKm", analysis != null ? analysis.getRouteDistanceKm() : null);
        response.put("routeDurationMinutes", analysis != null ? analysis.getRouteDurationMinutes() : null);
        response.put("routeGeometryGeoJson", analysis != null ? analysis.getRouteGeometryGeoJson() : null);
        response.put("teamLocation", analysis != null ? analysis.getTeamLocation() : null);
        response.put("sosLocation", analysis != null ? analysis.getSosLocation() : null);
        return ResponseEntity.ok(response);
    }

    /**
     * REST API cập nhật tọa độ GPS thời gian thực của người bị nạn (Live Location Tracking).
     */
    @PostMapping("/{id}/location")
    public ResponseEntity<?> updateVictimLocation(@PathVariable("id") Long id,
                                                  @RequestBody Map<String, Double> payload) {
        Double lat = payload.get("latitude");
        Double lng = payload.get("longitude");
        if (lat == null || lng == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Thiếu tọa độ GPS"));
        }
        SosRequest sos = sosService.getSosById(id);
        sos.setLatitude(lat);
        sos.setLongitude(lng);
        if (sosRequestRepository != null) {
            sosRequestRepository.save(sos);
        }

        // Bắn WebSocket thông báo vị trí mới của nạn nhân cho Đội cứu hộ và Điều phối viên
        Map<String, Object> update = new HashMap<>();
        update.put("type", "VICTIM_LOCATION_UPDATE");
        update.put("sosId", sos.getId());
        update.put("latitude", lat);
        update.put("longitude", lng);

        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/map-updates", update);
        }

        return ResponseEntity.ok(Map.of("success", true, "latitude", lat, "longitude", lng));
    }
}
