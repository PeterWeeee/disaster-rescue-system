package vn.edu.cuunan.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "rescue_equipment")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RescueEquipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "leader"})
    private RescueTeam team;

    @Column(name = "equipment_code", nullable = false, length = 50)
    private String equipmentCode;

    @Column(name = "equipment_name", nullable = false, length = 150)
    private String equipmentName;

    @Column(length = 50)
    private String category = "SAFETY"; // SAFETY, MEDICAL, GEAR, SUPPLY

    @Column(length = 30)
    private String unit = "chiếc"; // chiếc, bộ, cuộn, thùng...

    @Column(name = "unit_price", precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity = 0;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity = 0;

    @Column(name = "dispatched_quantity", nullable = false)
    private int dispatchedQuantity = 0;

    @Column(name = "damaged_quantity", nullable = false)
    private int damagedQuantity = 0;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public RescueEquipment() {
    }

    public RescueEquipment(RescueTeam team, String equipmentCode, String equipmentName, String category, String unit, int totalQuantity, String notes) {
        this.team = team;
        this.equipmentCode = equipmentCode;
        this.equipmentName = equipmentName;
        this.category = category;
        this.unit = unit;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = totalQuantity;
        this.dispatchedQuantity = 0;
        this.damagedQuantity = 0;
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public String getEquipmentCode() {
        return equipmentCode;
    }

    public void setEquipmentCode(String equipmentCode) {
        this.equipmentCode = equipmentCode;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public int getDispatchedQuantity() {
        return dispatchedQuantity;
    }

    public void setDispatchedQuantity(int dispatchedQuantity) {
        this.dispatchedQuantity = dispatchedQuantity;
    }

    public int getDamagedQuantity() {
        return damagedQuantity;
    }

    public void setDamagedQuantity(int damagedQuantity) {
        this.damagedQuantity = damagedQuantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
