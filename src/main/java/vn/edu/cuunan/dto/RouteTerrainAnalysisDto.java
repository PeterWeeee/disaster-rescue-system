package vn.edu.cuunan.dto;

import java.util.List;

public class RouteTerrainAnalysisDto {

    private boolean success = true;
    private Long assignmentId;
    private Long sosId;
    private String teamName;
    private double[] teamLocation;
    private double[] sosLocation;
    private Double routeDistanceKm;
    private Integer routeDurationMinutes;
    private String terrainType; // FLOOD_DEEP, LANDSLIDE_MOUNTAIN, URBAN_INUNDATION, NORMAL_ROAD
    private String terrainTitle;
    private String terrainDescription;
    private String routeGeometryGeoJson; // Chuỗi tọa độ GeoJSON
    private List<VehicleOptionDto> recommendedVehicles;
    private List<EquipmentItemDto> recommendedEquipment;

    public RouteTerrainAnalysisDto() {
    }

    public static class VehicleOptionDto {
        private String id;
        private String name;
        private String icon;
        private String description;
        private boolean recommended;

        public VehicleOptionDto() {
        }

        public VehicleOptionDto(String id, String name, String icon, String description, boolean recommended) {
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.description = description;
            this.recommended = recommended;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public boolean isRecommended() { return recommended; }
        public void setRecommended(boolean recommended) { this.recommended = recommended; }
    }

    public static class EquipmentItemDto {
        private String id;
        private String name;
        private String category; // SAFETY, MEDICAL, GEAR, SUPPLY
        private String categoryName;
        private String icon;
        private boolean mandatory;
        private boolean defaultChecked;

        public EquipmentItemDto() {
        }

        public EquipmentItemDto(String id, String name, String category, String categoryName, String icon, boolean mandatory, boolean defaultChecked) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.categoryName = categoryName;
            this.icon = icon;
            this.mandatory = mandatory;
            this.defaultChecked = defaultChecked;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public boolean isMandatory() { return mandatory; }
        public void setMandatory(boolean mandatory) { this.mandatory = mandatory; }
        public boolean isDefaultChecked() { return defaultChecked; }
        public void setDefaultChecked(boolean defaultChecked) { this.defaultChecked = defaultChecked; }
    }

    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }

    public Long getSosId() { return sosId; }
    public void setSosId(Long sosId) { this.sosId = sosId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public double[] getTeamLocation() { return teamLocation; }
    public void setTeamLocation(double[] teamLocation) { this.teamLocation = teamLocation; }

    public double[] getSosLocation() { return sosLocation; }
    public void setSosLocation(double[] sosLocation) { this.sosLocation = sosLocation; }

    public Double getRouteDistanceKm() { return routeDistanceKm; }
    public void setRouteDistanceKm(Double routeDistanceKm) { this.routeDistanceKm = routeDistanceKm; }

    public Integer getRouteDurationMinutes() { return routeDurationMinutes; }
    public void setRouteDurationMinutes(Integer routeDurationMinutes) { this.routeDurationMinutes = routeDurationMinutes; }

    public String getTerrainType() { return terrainType; }
    public void setTerrainType(String terrainType) { this.terrainType = terrainType; }

    public String getTerrainTitle() { return terrainTitle; }
    public void setTerrainTitle(String terrainTitle) { this.terrainTitle = terrainTitle; }

    public String getTerrainDescription() { return terrainDescription; }
    public void setTerrainDescription(String terrainDescription) { this.terrainDescription = terrainDescription; }

    public String getRouteGeometryGeoJson() { return routeGeometryGeoJson; }
    public void setRouteGeometryGeoJson(String routeGeometryGeoJson) { this.routeGeometryGeoJson = routeGeometryGeoJson; }

    public List<VehicleOptionDto> getRecommendedVehicles() { return recommendedVehicles; }
    public void setRecommendedVehicles(List<VehicleOptionDto> recommendedVehicles) { this.recommendedVehicles = recommendedVehicles; }

    public List<EquipmentItemDto> getRecommendedEquipment() { return recommendedEquipment; }
    public void setRecommendedEquipment(List<EquipmentItemDto> recommendedEquipment) { this.recommendedEquipment = recommendedEquipment; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
