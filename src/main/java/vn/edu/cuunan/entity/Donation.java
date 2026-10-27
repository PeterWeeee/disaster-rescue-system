package vn.edu.cuunan.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "donations")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "donor_name", length = 100)
    private String donorName;

    @Column(name = "donor_phone", length = 20)
    private String donorPhone;

    @Column(name = "donor_email", length = 120)
    private String donorEmail;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(length = 500)
    private String message;

    @Column(name = "target_province", length = 80)
    private String targetProvince = "TOAN_MIEN_TRUNG";

    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod; // VNPAY, MOMO, VIETQR

    @Column(name = "transaction_code", unique = true, length = 100)
    private String transactionCode;

    @Column(nullable = false, length = 30)
    private String status = "PENDING"; // PENDING, SUCCESS, FAILED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    public Donation() {
    }

    public Donation(Long id, String donorName, String donorPhone, String donorEmail, BigDecimal amount,
                    String message, String targetProvince, String paymentMethod, String transactionCode,
                    String status, LocalDateTime createdAt, LocalDateTime paidAt) {
        this.id = id;
        this.donorName = donorName;
        this.donorPhone = donorPhone;
        this.donorEmail = donorEmail;
        this.amount = amount;
        this.message = message;
        this.targetProvince = targetProvince != null ? targetProvince : "TOAN_MIEN_TRUNG";
        this.paymentMethod = paymentMethod;
        this.transactionCode = transactionCode;
        this.status = status != null ? status : "PENDING";
        this.createdAt = createdAt;
        this.paidAt = paidAt;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (donorName == null || donorName.trim().isEmpty()) {
            donorName = "Nhà hảo tâm ẩn danh";
        }
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

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public String getDonorPhone() {
        return donorPhone;
    }

    public void setDonorPhone(String donorPhone) {
        this.donorPhone = donorPhone;
    }

    public String getDonorEmail() {
        return donorEmail;
    }

    public void setDonorEmail(String donorEmail) {
        this.donorEmail = donorEmail;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTargetProvince() {
        return targetProvince;
    }

    public void setTargetProvince(String targetProvince) {
        this.targetProvince = targetProvince;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public static DonationBuilder builder() {
        return new DonationBuilder();
    }

    public static class DonationBuilder {
        private Long id;
        private String donorName;
        private String donorPhone;
        private String donorEmail;
        private BigDecimal amount;
        private String message;
        private String targetProvince = "TOAN_MIEN_TRUNG";
        private String paymentMethod;
        private String transactionCode;
        private String status = "PENDING";
        private LocalDateTime createdAt;
        private LocalDateTime paidAt;

        public DonationBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DonationBuilder donorName(String donorName) {
            this.donorName = donorName;
            return this;
        }

        public DonationBuilder donorPhone(String donorPhone) {
            this.donorPhone = donorPhone;
            return this;
        }

        public DonationBuilder donorEmail(String donorEmail) {
            this.donorEmail = donorEmail;
            return this;
        }

        public DonationBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public DonationBuilder message(String message) {
            this.message = message;
            return this;
        }

        public DonationBuilder targetProvince(String targetProvince) {
            this.targetProvince = targetProvince;
            return this;
        }

        public DonationBuilder paymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public DonationBuilder transactionCode(String transactionCode) {
            this.transactionCode = transactionCode;
            return this;
        }

        public DonationBuilder status(String status) {
            this.status = status;
            return this;
        }

        public DonationBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DonationBuilder paidAt(LocalDateTime paidAt) {
            this.paidAt = paidAt;
            return this;
        }

        public Donation build() {
            return new Donation(id, donorName, donorPhone, donorEmail, amount, message, targetProvince, paymentMethod, transactionCode, status, createdAt, paidAt);
        }
    }
}
