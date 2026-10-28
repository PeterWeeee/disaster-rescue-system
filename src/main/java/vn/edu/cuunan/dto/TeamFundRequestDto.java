package vn.edu.cuunan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TeamFundRequestDto {

    private Long teamId;

    @NotNull(message = "Số tiền xin tiếp tế không được để trống")
    @DecimalMin(value = "50000", message = "Số tiền xin tiếp tế tối thiểu là 50,000 ₫")
    private BigDecimal amount;

    @NotBlank(message = "Vui lòng nhập mục đích và danh mục nhu yếu phẩm cần mua")
    private String purpose;

    private String category = "NHU_YEU_PHAM"; // NHU_YEU_PHAM, XANG_DAU, Y_TE, TRANG_BI, KHAC

    private String urgencyLevel = "HIGH"; // LOW, MEDIUM, HIGH, CRITICAL
    private List<CartItem> items = new ArrayList<>();

    public static class CartItem {
        private Long equipmentId;
        private Integer quantity;
        public CartItem() { }
        public CartItem(Long equipmentId, Integer quantity) { this.equipmentId = equipmentId; this.quantity = quantity; }
        public Long getEquipmentId() { return equipmentId; }
        public void setEquipmentId(Long equipmentId) { this.equipmentId = equipmentId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public TeamFundRequestDto() {
    }

    public TeamFundRequestDto(BigDecimal amount, String purpose, String category, String urgencyLevel) {
        this(null, amount, purpose, category, urgencyLevel);
    }

    public TeamFundRequestDto(Long teamId, BigDecimal amount, String purpose, String category, String urgencyLevel) {
        this.teamId = teamId;
        this.amount = amount;
        this.purpose = purpose;
        this.category = category;
        this.urgencyLevel = urgencyLevel;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
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

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
}
