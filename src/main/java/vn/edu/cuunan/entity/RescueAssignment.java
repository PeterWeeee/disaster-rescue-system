package vn.edu.cuunan.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rescue_assignments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RescueAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sos_request_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private SosRequest sosRequest;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private RescueTeam team;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_by")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "role"})
    private User assignedBy;

    @Column(length = 30)
    private String status = "PENDING"; // PENDING, ACCEPTED, REJECTED, IN_PROGRESS, COMPLETED

    @Column(name = "rejection_reason", length = 255)
    private String rejectionReason;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "report_notes", columnDefinition = "TEXT")
    private String reportNotes;

    @Column(name = "report_image_url", length = 500)
    private String reportImageUrl;

    @Column(name = "vehicle_type", length = 50)
    private String vehicleType;

    @Column(name = "selected_equipment", columnDefinition = "TEXT")
    private String selectedEquipment;

    @Column(name = "terrain_type", length = 50)
    private String terrainType;

    @Column(name = "route_distance_km")
    private Double routeDistanceKm;

    @Column(name = "route_duration_minutes")
    private Integer routeDurationMinutes;

    public RescueAssignment() {
    }

    @PrePersist
    protected void onAssign() {
        if (this.assignedAt == null) {
            this.assignedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SosRequest getSosRequest() {
        return sosRequest;
    }

    public void setSosRequest(SosRequest sosRequest) {
        this.sosRequest = sosRequest;
    }

    public RescueTeam getTeam() {
        return team;
    }

    public void setTeam(RescueTeam team) {
        this.team = team;
    }

    public User getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(User assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getReportNotes() {
        return reportNotes;
    }

    public void setReportNotes(String reportNotes) {
        this.reportNotes = reportNotes;
    }

    public String getReportImageUrl() {
        return reportImageUrl;
    }

    public void setReportImageUrl(String reportImageUrl) {
        this.reportImageUrl = reportImageUrl;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getSelectedEquipment() {
        return selectedEquipment;
    }

    public void setSelectedEquipment(String selectedEquipment) {
        this.selectedEquipment = selectedEquipment;
    }

    public String getTerrainType() {
        return terrainType;
    }

    public void setTerrainType(String terrainType) {
        this.terrainType = terrainType;
    }

    public Double getRouteDistanceKm() {
        return routeDistanceKm;
    }

    public void setRouteDistanceKm(Double routeDistanceKm) {
        this.routeDistanceKm = routeDistanceKm;
    }

    public Integer getRouteDurationMinutes() {
        return routeDurationMinutes;
    }

    public void setRouteDurationMinutes(Integer routeDurationMinutes) {
        this.routeDurationMinutes = routeDurationMinutes;
    }
}
