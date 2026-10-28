package vn.edu.cuunan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "evacuation_shelters")
public class EvacuationShelter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 255)
    private String address;

    private Double latitude;
    private Double longitude;

    @Column(name = "total_capacity")
    private Integer totalCapacity = 300;

    @Column(name = "current_occupancy")
    private Integer currentOccupancy = 0;

    @Column(name = "contact_phone", length = 30)
    private String contactPhone;

    @Column(length = 30)
    private String status = "OPEN"; // OPEN, FULL, CLOSED

    public EvacuationShelter() {
    }

    public EvacuationShelter(Long id, String name, String address, Double latitude, Double longitude, Integer totalCapacity, Integer currentOccupancy, String contactPhone, String status) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.totalCapacity = totalCapacity;
        this.currentOccupancy = currentOccupancy;
        this.contactPhone = contactPhone;
        this.status = status;
    }

    public int getRemainingCapacity() {
        int total = (totalCapacity != null) ? totalCapacity : 0;
        int current = (currentOccupancy != null) ? currentOccupancy : 0;
        return Math.max(0, total - current);
    }

    public int getOccupancyPercentage() {
        if (totalCapacity == null || totalCapacity <= 0) return 0;
        int current = (currentOccupancy != null) ? currentOccupancy : 0;
        return Math.min(100, (int) Math.round(((double) current / totalCapacity) * 100));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(Integer totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public Integer getCurrentOccupancy() {
        return currentOccupancy;
    }

    public void setCurrentOccupancy(Integer currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
