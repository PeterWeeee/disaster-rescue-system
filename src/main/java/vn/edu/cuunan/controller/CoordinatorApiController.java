package vn.edu.cuunan.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.cuunan.dto.AssignTaskRequestDto;
import vn.edu.cuunan.dto.RescueTeamDistanceDto;
import vn.edu.cuunan.entity.RescueAssignment;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.repository.RescueTeamRepository;
import vn.edu.cuunan.security.CustomUserDetails;
import vn.edu.cuunan.service.CoordinatorService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coordinator")
@PreAuthorize("hasAnyRole('COORDINATOR', 'ADMIN')")
public class CoordinatorApiController {

    private final CoordinatorService coordinatorService;
    private final RescueTeamRepository rescueTeamRepository;

    public CoordinatorApiController(CoordinatorService coordinatorService,
                                    RescueTeamRepository rescueTeamRepository) {
        this.coordinatorService = coordinatorService;
        this.rescueTeamRepository = rescueTeamRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(coordinatorService.getDashboardStats());
    }

    @GetMapping("/sos-list")
    public ResponseEntity<List<SosRequest>> getSosList(@RequestParam(value = "status", required = false) String status) {
        return ResponseEntity.ok(coordinatorService.getSosRequests(status));
    }

    @GetMapping("/nearest-teams")
    public ResponseEntity<List<RescueTeamDistanceDto>> getNearestTeams(@RequestParam("sosId") Long sosId) {
        return ResponseEntity.ok(coordinatorService.getAvailableTeamsWithDistance(sosId));
    }

    @PostMapping("/assign")
    public ResponseEntity<?> assignTeam(@Valid @RequestBody AssignTaskRequestDto dto,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long coordinatorId = (userDetails != null && userDetails.getUser() != null)
                    ? userDetails.getUser().getId()
                    : null;

            RescueAssignment assignment = coordinatorService.assignSosToTeam(dto, coordinatorId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã phân công thành công đội cứu hộ '" + assignment.getTeam().getTeamName() + "' cho ca SOS #" + dto.getSosRequestId());
            response.put("assignmentId", assignment.getId());
            response.put("status", assignment.getStatus());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Đã xảy ra lỗi nội bộ khi phân công: " + ex.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/map-data")
    public ResponseEntity<Map<String, Object>> getMapData(@RequestParam(value = "province", required = false, defaultValue = "ALL") String province) {
        Map<String, Object> data = new HashMap<>();
        data.put("sosRequests", coordinatorService.getSosMapPins("ALL", province));
        data.put("rescueTeams", coordinatorService.getRescueTeamMapPins(province));
        vn.edu.cuunan.util.ProvinceRegion region = vn.edu.cuunan.util.ProvinceRegion.fromCode(province);
        data.put("centerLat", region.getCenterLat());
        data.put("centerLng", region.getCenterLng());
        data.put("defaultZoom", region.getDefaultZoom());
        data.put("regionName", region.getName());
        return ResponseEntity.ok(data);
    }
}
