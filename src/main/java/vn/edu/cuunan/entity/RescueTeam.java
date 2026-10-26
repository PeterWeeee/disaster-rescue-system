package vn.edu.cuunan.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "rescue_teams")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RescueTeam extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_name", nullable = false, length = 150)
    private String teamName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "role"})
    private User leader;

    @Column(name = "contact_phone", nullable = false, length = 20)
    private String contactPhone;

    @Column(name = "current_latitude")
    private Double currentLatitude;

    @Column(name = "current_longitude")
    private Double currentLongitude;

    @Column(name = "member_count")
    private Integer memberCount = 0;

    @Column(name = "equipment_notes", columnDefinition = "TEXT")
    private String equipmentNotes;

    @Column(length = 30)
    private String status = "AVAILABLE"; // AVAILABLE, BUSY, OFFLINE

    public RescueTeam() {
    }

    public RescueTeam(Long id, String teamName, User leader, String contactPhone, Double currentLatitude, Double currentLongitude, Integer memberCount, String equipmentNotes, String status) {
        this.id = id;
        this.teamName = teamName;
        this.leader = leader;
        this.contactPhone = contactPhone;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.memberCount = memberCount != null ? memberCount : 0;
        this.equipmentNotes = equipmentNotes;
        this.status = status != null ? status : "AVAILABLE";
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

    public User getLeader() {
        return leader;
    }

    public void setLeader(User leader) {
        this.leader = leader;
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
}
