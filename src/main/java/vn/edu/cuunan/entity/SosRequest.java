package vn.edu.cuunan.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sos_requests")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SosRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "role"})
    private User sender;

    @Column(name = "contact_name", nullable = false, length = 150)
    private String contactName;

    @Column(name = "contact_phone", nullable = false, length = 20)
    private String contactPhone;

    @Column(name = "secondary_phone", length = 20)
    private String secondaryPhone;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "address_detail", length = 255)
    private String addressDetail;

    @Column(name = "urgency_level", nullable = false, length = 30)
    private String urgencyLevel = "HIGH"; // CRITICAL, HIGH, MEDIUM, LOW

    @Column(name = "people_count", nullable = false)
    private Integer peopleCount = 1;

    @Column(name = "has_elderly")
    private Boolean hasElderly = false;

    @Column(name = "has_children")
    private Boolean hasChildren = false;

    @Column(name = "has_injured")
    private Boolean hasInjured = false;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(nullable = false, length = 30)
    private String status = "PENDING"; // PENDING, DISPATCHED, IN_PROGRESS, RESOLVED, CANCELLED

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    public SosRequest() {
    }

    public static SosRequestBuilder builder() {
        return new SosRequestBuilder();
    }

    public static class SosRequestBuilder {
        private Long id;
        private User sender;
        private String contactName;
        private String contactPhone;
        private String secondaryPhone;
        private Double latitude;
        private Double longitude;
        private String addressDetail;
        private String urgencyLevel = "HIGH";
        private Integer peopleCount = 1;
        private Boolean hasElderly = false;
        private Boolean hasChildren = false;
        private Boolean hasInjured = false;
        private String description;
        private String imageUrl;
        private String status = "PENDING";
        private LocalDateTime resolvedAt;

        public SosRequestBuilder id(Long id) { this.id = id; return this; }
        public SosRequestBuilder sender(User sender) { this.sender = sender; return this; }
        public SosRequestBuilder contactName(String contactName) { this.contactName = contactName; return this; }
        public SosRequestBuilder contactPhone(String contactPhone) { this.contactPhone = contactPhone; return this; }
        public SosRequestBuilder secondaryPhone(String secondaryPhone) { this.secondaryPhone = secondaryPhone; return this; }
        public SosRequestBuilder latitude(Double latitude) { this.latitude = latitude; return this; }
        public SosRequestBuilder longitude(Double longitude) { this.longitude = longitude; return this; }
        public SosRequestBuilder addressDetail(String addressDetail) { this.addressDetail = addressDetail; return this; }
        public SosRequestBuilder urgencyLevel(String urgencyLevel) { this.urgencyLevel = urgencyLevel; return this; }
        public SosRequestBuilder peopleCount(Integer peopleCount) { this.peopleCount = peopleCount; return this; }
        public SosRequestBuilder hasElderly(Boolean hasElderly) { this.hasElderly = hasElderly; return this; }
        public SosRequestBuilder hasChildren(Boolean hasChildren) { this.hasChildren = hasChildren; return this; }
        public SosRequestBuilder hasInjured(Boolean hasInjured) { this.hasInjured = hasInjured; return this; }
        public SosRequestBuilder description(String description) { this.description = description; return this; }
        public SosRequestBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public SosRequestBuilder status(String status) { this.status = status; return this; }
        public SosRequestBuilder resolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; return this; }

        public SosRequest build() {
            SosRequest sos = new SosRequest();
            sos.setId(id);
            sos.setSender(sender);
            sos.setContactName(contactName);
            sos.setContactPhone(contactPhone);
            sos.setSecondaryPhone(secondaryPhone);
            sos.setLatitude(latitude);
            sos.setLongitude(longitude);
            sos.setAddressDetail(addressDetail);
            sos.setUrgencyLevel(urgencyLevel != null ? urgencyLevel : "HIGH");
            sos.setPeopleCount(peopleCount != null && peopleCount > 0 ? peopleCount : 1);
            sos.setHasElderly(Boolean.TRUE.equals(hasElderly));
            sos.setHasChildren(Boolean.TRUE.equals(hasChildren));
            sos.setHasInjured(Boolean.TRUE.equals(hasInjured));
            sos.setDescription(description);
            sos.setImageUrl(imageUrl);
            sos.setStatus(status != null ? status : "PENDING");
            sos.setResolvedAt(resolvedAt);
            return sos;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getSecondaryPhone() {
        return secondaryPhone;
    }

    public void setSecondaryPhone(String secondaryPhone) {
        this.secondaryPhone = secondaryPhone;
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

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(String urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public Integer getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(Integer peopleCount) {
        this.peopleCount = peopleCount;
    }

    public Boolean getHasElderly() {
        return hasElderly;
    }

    public void setHasElderly(Boolean hasElderly) {
        this.hasElderly = hasElderly;
    }

    public Boolean getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public Boolean getHasInjured() {
        return hasInjured;
    }

    public void setHasInjured(Boolean hasInjured) {
        this.hasInjured = hasInjured;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
