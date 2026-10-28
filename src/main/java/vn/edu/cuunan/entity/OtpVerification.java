package vn.edu.cuunan.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_verifications")
public class OtpVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(name = "otp_code", nullable = false, length = 6)
    private String otpCode;

    @Column(nullable = false, length = 30)
    private String type; // REGISTRATION, FORGOT_PASSWORD

    @Column(name = "expiry_time", nullable = false)
    private LocalDateTime expiryTime;

    @Column(name = "is_used", nullable = false)
    private boolean isUsed = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public OtpVerification() {
    }

    public OtpVerification(Long id, String email, String otpCode, String type, LocalDateTime expiryTime, boolean isUsed, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.otpCode = otpCode;
        this.type = type;
        this.expiryTime = expiryTime;
        this.isUsed = isUsed;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (expiryTime == null) {
            expiryTime = LocalDateTime.now().plusMinutes(10);
        }
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static OtpVerificationBuilder builder() {
        return new OtpVerificationBuilder();
    }

    public static class OtpVerificationBuilder {
        private Long id;
        private String email;
        private String otpCode;
        private String type;
        private LocalDateTime expiryTime;
        private boolean isUsed = false;
        private LocalDateTime createdAt;

        public OtpVerificationBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public OtpVerificationBuilder email(String email) {
            this.email = email;
            return this;
        }

        public OtpVerificationBuilder otpCode(String otpCode) {
            this.otpCode = otpCode;
            return this;
        }

        public OtpVerificationBuilder type(String type) {
            this.type = type;
            return this;
        }

        public OtpVerificationBuilder expiryTime(LocalDateTime expiryTime) {
            this.expiryTime = expiryTime;
            return this;
        }

        public OtpVerificationBuilder isUsed(boolean isUsed) {
            this.isUsed = isUsed;
            return this;
        }

        public OtpVerificationBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public OtpVerification build() {
            return new OtpVerification(id, email, otpCode, type, expiryTime, isUsed, createdAt);
        }
    }
}
