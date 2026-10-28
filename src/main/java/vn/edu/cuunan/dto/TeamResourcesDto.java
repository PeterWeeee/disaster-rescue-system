package vn.edu.cuunan.dto;

import vn.edu.cuunan.entity.RescueEquipment;

import java.util.List;

public class TeamResourcesDto {

    private Long teamId;
    private String teamName;
    private String contactPhone;
    private String teamStatus;

    // Quân số
    private int totalMembers;
    private int readyMembers;
    private int onMissionMembers;
    private int offDutyMembers;
    private List<MemberItemDto> members;

    // Trang bị
    private int totalEquipmentUnits;
    private int availableEquipmentUnits;
    private int dispatchedEquipmentUnits;
    private int damagedEquipmentUnits;
    private List<RescueEquipment> equipmentList;

    public TeamResourcesDto() {
    }

    public static class MemberItemDto {
        private Long userId;
        private String fullName;
        private String phone;
        private String roleInTeam; // LEADER, MEMBER
        private String readinessStatus; // READY, ON_MISSION, OFF_DUTY
        private String currentMissionDescription;

        public MemberItemDto() {
        }

        public MemberItemDto(Long userId, String fullName, String phone, String roleInTeam, String readinessStatus, String currentMissionDescription) {
            this.userId = userId;
            this.fullName = fullName;
            this.phone = phone;
            this.roleInTeam = roleInTeam;
            this.readinessStatus = readinessStatus;
            this.currentMissionDescription = currentMissionDescription;
        }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getRoleInTeam() { return roleInTeam; }
        public void setRoleInTeam(String roleInTeam) { this.roleInTeam = roleInTeam; }
        public String getReadinessStatus() { return readinessStatus; }
        public void setReadinessStatus(String readinessStatus) { this.readinessStatus = readinessStatus; }
        public String getCurrentMissionDescription() { return currentMissionDescription; }
        public void setCurrentMissionDescription(String currentMissionDescription) { this.currentMissionDescription = currentMissionDescription; }
    }

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getTeamStatus() { return teamStatus; }
    public void setTeamStatus(String teamStatus) { this.teamStatus = teamStatus; }
    public int getTotalMembers() { return totalMembers; }
    public void setTotalMembers(int totalMembers) { this.totalMembers = totalMembers; }
    public int getReadyMembers() { return readyMembers; }
    public void setReadyMembers(int readyMembers) { this.readyMembers = readyMembers; }
    public int getOnMissionMembers() { return onMissionMembers; }
    public void setOnMissionMembers(int onMissionMembers) { this.onMissionMembers = onMissionMembers; }
    public int getOffDutyMembers() { return offDutyMembers; }
    public void setOffDutyMembers(int offDutyMembers) { this.offDutyMembers = offDutyMembers; }
    public List<MemberItemDto> getMembers() { return members; }
    public void setMembers(List<MemberItemDto> members) { this.members = members; }
    public int getTotalEquipmentUnits() { return totalEquipmentUnits; }
    public void setTotalEquipmentUnits(int totalEquipmentUnits) { this.totalEquipmentUnits = totalEquipmentUnits; }
    public int getAvailableEquipmentUnits() { return availableEquipmentUnits; }
    public void setAvailableEquipmentUnits(int availableEquipmentUnits) { this.availableEquipmentUnits = availableEquipmentUnits; }
    public int getDispatchedEquipmentUnits() { return dispatchedEquipmentUnits; }
    public void setDispatchedEquipmentUnits(int dispatchedEquipmentUnits) { this.dispatchedEquipmentUnits = dispatchedEquipmentUnits; }
    public int getDamagedEquipmentUnits() { return damagedEquipmentUnits; }
    public void setDamagedEquipmentUnits(int damagedEquipmentUnits) { this.damagedEquipmentUnits = damagedEquipmentUnits; }
    public List<RescueEquipment> getEquipmentList() { return equipmentList; }
    public void setEquipmentList(List<RescueEquipment> equipmentList) { this.equipmentList = equipmentList; }
}
