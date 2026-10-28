package vn.edu.cuunan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class DonationRequestDto {

    private String donorName;
    private String donorPhone;
    private String donorEmail;

    @NotNull(message = "Số tiền ủng hộ không được để trống")
    @DecimalMin(value = "10000", message = "Số tiền ủng hộ tối thiểu là 10,000 ₫")
    private BigDecimal amount;

    private String message;
    private String targetProvince = "TOAN_MIEN_TRUNG";

    @NotBlank(message = "Vui lòng chọn phương thức thanh toán")
    private String paymentMethod; // VNPAY, MOMO, VIETQR

    public DonationRequestDto() {
    }

    public DonationRequestDto(String donorName, String donorPhone, String donorEmail, BigDecimal amount,
                              String message, String targetProvince, String paymentMethod) {
        this.donorName = donorName;
        this.donorPhone = donorPhone;
        this.donorEmail = donorEmail;
        this.amount = amount;
        this.message = message;
        this.targetProvince = targetProvince;
        this.paymentMethod = paymentMethod;
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
}
