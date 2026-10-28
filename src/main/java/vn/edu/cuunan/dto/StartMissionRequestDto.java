package vn.edu.cuunan.dto;

import java.util.List;

public class StartMissionRequestDto {

    private String vehicleType;
    private Long vehicleEquipmentId;
    private List<String> selectedEquipment;
    private List<SelectedEquipmentItem> equipmentItems;
    private String customNotes;
    private Double routeDistanceKm;
    private Integer routeDurationMinutes;
    private String terrainType;

    public static class SelectedEquipmentItem {
        private Long equipmentId;
        private int quantity;
        public SelectedEquipmentItem() { }
        public SelectedEquipmentItem(Long equipmentId, int quantity) { this.equipmentId = equipmentId; this.quantity = quantity; }
        public Long getEquipmentId() { return equipmentId; }
        public void setEquipmentId(Long equipmentId) { this.equipmentId = equipmentId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public StartMissionRequestDto() {
    }

    public StartMissionRequestDto(String vehicleType, List<String> selectedEquipment, String customNotes) {
        this.vehicleType = vehicleType;
        this.selectedEquipment = selectedEquipment;
        this.customNotes = customNotes;
    }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public Long getVehicleEquipmentId() { return vehicleEquipmentId; }
    public void setVehicleEquipmentId(Long vehicleEquipmentId) { this.vehicleEquipmentId = vehicleEquipmentId; }

    public List<String> getSelectedEquipment() { return selectedEquipment; }
    public void setSelectedEquipment(List<String> selectedEquipment) { this.selectedEquipment = selectedEquipment; }
    public List<SelectedEquipmentItem> getEquipmentItems() { return equipmentItems; }
    public void setEquipmentItems(List<SelectedEquipmentItem> equipmentItems) { this.equipmentItems = equipmentItems; }

    public String getCustomNotes() { return customNotes; }
    public void setCustomNotes(String customNotes) { this.customNotes = customNotes; }

    public Double getRouteDistanceKm() { return routeDistanceKm; }
    public void setRouteDistanceKm(Double routeDistanceKm) { this.routeDistanceKm = routeDistanceKm; }

    public Integer getRouteDurationMinutes() { return routeDurationMinutes; }
    public void setRouteDurationMinutes(Integer routeDurationMinutes) { this.routeDurationMinutes = routeDurationMinutes; }

    public String getTerrainType() { return terrainType; }
    public void setTerrainType(String terrainType) { this.terrainType = terrainType; }
}
