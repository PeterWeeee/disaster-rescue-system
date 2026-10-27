package vn.edu.cuunan.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_fund_requests")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TeamFundRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private RescueTeam rescueTeam;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requester_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "role"})
    private User requester;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 500)
    private String purpose;

    @Column(name = "category", length = 50)
    private String category = "NHU_YEU_PHAM"; // NHU_YEU_PHAM, XANG_DAU, Y_TE, TRANG_BI, KHAC

    @Column(name = "urgency_level", length = 30)
    private String urgencyLevel = "HIGH"; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(nullable = false, length = 30)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewed_by")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "role"})
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public TeamFundRequest() {
    }

    public TeamFundRequest(Long id, RescueTeam rescueTeam, User requester, BigDecimal amount, String purpose,
                           String category, String urgencyLevel, String status, User reviewedBy,
                           LocalDateTime reviewedAt, String rejectReason, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.rescueTeam = rescueTeam;
        this.requester = requester;
        this.amount = amount;
        this.purpose = purpose;
        this.category = category != null ? category : "NHU_YEU_PHAM";
        this.urgencyLevel = urgencyLevel != null ? urgencyLevel : "HIGH";
        this.status = status != null ? status : "PENDING";
        this.reviewedBy = reviewedBy;
        this.reviewedAt = reviewedAt;
        this.rejectReason = rejectReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getFormattedAmount() {
        if (amount == null) return "0 ₫";
        DecimalFormat df = new DecimalFormat("#,### ₫");
        return df.format(amount);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RescueTeam getRescueTeam() {
        return rescueTeam;
    }

    public void setRescueTeam(RescueTeam rescueTeam) {
        this.rescueTeam = rescueTeam;
    }

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(String urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static TeamFundRequestBuilder builder() {
        return new TeamFundRequestBuilder();
    }

    public static class TeamFundRequestBuilder {
        private Long id;
        private RescueTeam rescueTeam;
        private User requester;
        private BigDecimal amount;
        private String purpose;
        private String category = "NHU_YEU_PHAM";
        private String urgencyLevel = "HIGH";
        private String status = "PENDING";
        private User reviewedBy;
        private LocalDateTime reviewedAt;
        private String rejectReason;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public TeamFundRequestBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TeamFundRequestBuilder rescueTeam(RescueTeam rescueTeam) {
            this.rescueTeam = rescueTeam;
            return this;
        }

        public TeamFundRequestBuilder requester(User requester) {
            this.requester = requester;
            return this;
        }

        public TeamFundRequestBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public TeamFundRequestBuilder purpose(String purpose) {
            this.purpose = purpose;
            return this;
        }

        public TeamFundRequestBuilder category(String category) {
            this.category = category;
            return this;
        }

        public TeamFundRequestBuilder urgencyLevel(String urgencyLevel) {
            this.urgencyLevel = urgencyLevel;
            return this;
        }

        public TeamFundRequestBuilder status(String status) {
            this.status = status;
            return this;
        }

        public TeamFundRequestBuilder reviewedBy(User reviewedBy) {
            this.reviewedBy = reviewedBy;
            return this;
        }

        public TeamFundRequestBuilder reviewedAt(LocalDateTime reviewedAt) {
            this.reviewedAt = reviewedAt;
            return this;
        }

        public TeamFundRequestBuilder rejectReason(String rejectReason) {
            this.rejectReason = rejectReason;
            return this;
        }

        public TeamFundRequestBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public TeamFundRequestBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public TeamFundRequest build() {
            return new TeamFundRequest(id, rescueTeam, requester, amount, purpose, category, urgencyLevel, status, reviewedBy, reviewedAt, rejectReason, createdAt, updatedAt);
        }
    }
}
