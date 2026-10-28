package vn.edu.cuunan.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "team_members")
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private RescueTeam team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "member_role", length = 50)
    private String memberRole = "RESCUER"; // LEADER, MEDIC, DRIVER, RESCUER

    @Column(name = "current_lat")
    private Double currentLat;

    @Column(name = "current_lng")
    private Double currentLng;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    public TeamMember() {
    }

    public TeamMember(Long id, RescueTeam team, User user, String memberRole, Double currentLat, Double currentLng, LocalDateTime joinedAt) {
        this.id = id;
        this.team = team;
        this.user = user;
        this.memberRole = memberRole != null ? memberRole : "RESCUER";
        this.currentLat = currentLat;
        this.currentLng = currentLng;
        this.joinedAt = joinedAt;
    }

    @PrePersist
    protected void onJoin() {
        if (this.joinedAt == null) {
            this.joinedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RescueTeam getTeam() {
        return team;
    }

    public void setTeam(RescueTeam team) {
        this.team = team;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRoleInTeam() { return memberRole; }
    public String getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(String memberRole) {
        this.memberRole = memberRole;
    }

    public Double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(Double currentLat) {
        this.currentLat = currentLat;
    }

    public Double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(Double currentLng) {
        this.currentLng = currentLng;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
