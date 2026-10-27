package vn.edu.cuunan.dto;

public class RescueTeamDistanceDto {
    private Long id;
    private String teamName;
    private String leaderName;
    private String contactPhone;
    private Double currentLatitude;
    private Double currentLongitude;
    private Integer memberCount;
    private String equipmentNotes;
    private String status; // AVAILABLE, BUSY, OFFLINE
    private Double distanceKm;

    public RescueTeamDistanceDto() {
    }

    public RescueTeamDistanceDto(Long id, String teamName, String leaderName, String contactPhone, Double currentLatitude, Double currentLongitude, Integer memberCount, String equipmentNotes, String status, Double distanceKm) {
        this.id = id;
        this.teamName = teamName;
        this.leaderName = leaderName;
        this.contactPhone = contactPhone;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.memberCount = memberCount;
        this.equipmentNotes = equipmentNotes;
        this.status = status;
        this.distanceKm = distanceKm;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String teamName;
        private String leaderName;
        private String contactPhone;
        private Double currentLatitude;
        private Double currentLongitude;
        private Integer memberCount;
        private String equipmentNotes;
        private String status;
        private Double distanceKm;

        public Builder teamId(Long teamId) { this.id = teamId; return this; }
        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder teamName(String teamName) {
            this.teamName = teamName;
            return this;
        }

        public Builder leaderName(String leaderName) {
            this.leaderName = leaderName;
            return this;
        }

        public Builder contactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
            return this;
        }

        public Builder currentLatitude(Double currentLatitude) {
            this.currentLatitude = currentLatitude;
            return this;
        }

        public Builder currentLongitude(Double currentLongitude) {
            this.currentLongitude = currentLongitude;
            return this;
        }

        public Builder memberCount(Integer memberCount) {
            this.memberCount = memberCount;
            return this;
        }

        public Builder equipmentNotes(String equipmentNotes) {
            this.equipmentNotes = equipmentNotes;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder distanceKm(Double distanceKm) {
            this.distanceKm = distanceKm;
            return this;
        }

        public RescueTeamDistanceDto build() {
            return new RescueTeamDistanceDto(id, teamName, leaderName, contactPhone, currentLatitude, currentLongitude, memberCount, equipmentNotes, status, distanceKm);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public Double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public String getEquipmentNotes() {
        return equipmentNotes;
    }

    public void setEquipmentNotes(String equipmentNotes) {
        this.equipmentNotes = equipmentNotes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }
}
