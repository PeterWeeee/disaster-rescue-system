package vn.edu.cuunan.dto;

import java.util.List;

public class StartMissionRequestDto {

    private String vehicleType;
    private List<String> selectedEquipment;
    private String customNotes;
    private Double routeDistanceKm;
    private Integer routeDurationMinutes;
    private String terrainType;

    public StartMissionRequestDto() {
    }

    public StartMissionRequestDto(String vehicleType, List<String> selectedEquipment, String customNotes) {
        this.vehicleType = vehicleType;
        this.selectedEquipment = selectedEquipment;
        this.customNotes = customNotes;
    }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public List<String> getSelectedEquipment() { return selectedEquipment; }
    public void setSelectedEquipment(List<String> selectedEquipment) { this.selectedEquipment = selectedEquipment; }

    public String getCustomNotes() { return customNotes; }
    public void setCustomNotes(String customNotes) { this.customNotes = customNotes; }

    public Double getRouteDistanceKm() { return routeDistanceKm; }
    public void setRouteDistanceKm(Double routeDistanceKm) { this.routeDistanceKm = routeDistanceKm; }

    public Integer getRouteDurationMinutes() { return routeDurationMinutes; }
    public void setRouteDurationMinutes(Integer routeDurationMinutes) { this.routeDurationMinutes = routeDurationMinutes; }

    public String getTerrainType() { return terrainType; }
    public void setTerrainType(String terrainType) { this.terrainType = terrainType; }
}
