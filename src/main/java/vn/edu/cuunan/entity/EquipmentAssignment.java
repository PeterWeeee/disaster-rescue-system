package vn.edu.cuunan.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipment_assignments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EquipmentAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rescue_assignment_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private RescueAssignment rescueAssignment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "team"})
    private RescueEquipment equipment;

    @Column(name = "quantity_dispatched", nullable = false)
    private int quantityDispatched = 0;

    @Column(name = "quantity_returned", nullable = false)
    private int quantityReturned = 0;

    @Column(name = "quantity_damaged", nullable = false)
    private int quantityDamaged = 0;

    @Column(name = "quantity_lost", nullable = false)
    private int quantityLost = 0;

    @Column(name = "quantity_consumed", nullable = false)
    private int quantityConsumed = 0;

    @Column(length = 30)
    private String status = "DISPATCHED"; // DISPATCHED, RETURNED

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Lob
    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public EquipmentAssignment() {
    }

    public EquipmentAssignment(RescueAssignment rescueAssignment, RescueEquipment equipment, int quantityDispatched) {
        this.rescueAssignment = rescueAssignment;
        this.equipment = equipment;
        this.quantityDispatched = quantityDispatched;
        this.status = "DISPATCHED";
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RescueAssignment getRescueAssignment() {
        return rescueAssignment;
    }

    public void setRescueAssignment(RescueAssignment rescueAssignment) {
        this.rescueAssignment = rescueAssignment;
    }

    public RescueEquipment getEquipment() {
        return equipment;
    }

    public void setEquipment(RescueEquipment equipment) {
        this.equipment = equipment;
    }

    public int getQuantityDispatched() {
        return quantityDispatched;
    }

    public void setQuantityDispatched(int quantityDispatched) {
        this.quantityDispatched = quantityDispatched;
    }

    public int getQuantityReturned() {
        return quantityReturned;
    }

    public void setQuantityReturned(int quantityReturned) {
        this.quantityReturned = quantityReturned;
    }

    public int getQuantityDamaged() {
        return damagedQuantity();
    }

    public int damagedQuantity() {
        return quantityDamaged;
    }

    public int getQuantityLost() {
        return quantityLost;
    }

    public void setQuantityLost(int quantityLost) {
        this.quantityLost = quantityLost;
    }

    public int getQuantityConsumed() {
        return quantityConsumed;
    }

    public void setQuantityConsumed(int quantityConsumed) {
        this.quantityConsumed = quantityConsumed;
    }

    public void setQuantityDamaged(int quantityDamaged) {
        this.quantityDamaged = quantityDamaged;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }

    public void setReturnedAt(LocalDateTime returnedAt) {
        this.returnedAt = returnedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
