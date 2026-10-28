package vn.edu.cuunan.dto;

import jakarta.validation.constraints.NotBlank;

public class RescueTeamAdminDto {

    @NotBlank
    private String teamName;
    private Long leaderId;
    @NotBlank
    private String contactPhone;
    private Double currentLatitude;
    private Double currentLongitude;
    private Integer memberCount;
    private String equipmentNotes;
    private String status;

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public Long getLeaderId() { return leaderId; }
    public void setLeaderId(Long leaderId) { this.leaderId = leaderId; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public Double getCurrentLatitude() { return currentLatitude; }
    public void setCurrentLatitude(Double currentLatitude) { this.currentLatitude = currentLatitude; }
    public Double getCurrentLongitude() { return currentLongitude; }
    public void setCurrentLongitude(Double currentLongitude) { this.currentLongitude = currentLongitude; }
    public Integer getMemberCount() { return memberCount; }
    public void setMemberCount(Integer memberCount) { this.memberCount = memberCount; }
    public String getEquipmentNotes() { return equipmentNotes; }
    public void setEquipmentNotes(String equipmentNotes) { this.equipmentNotes = equipmentNotes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}