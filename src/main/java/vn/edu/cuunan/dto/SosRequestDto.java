package vn.edu.cuunan.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class SosRequestDto {

    @NotBlank(message = "Họ và tên người liên hệ không được để trống")
    private String contactName;

    @NotBlank(message = "Số điện thoại liên hệ không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{8,10}$", message = "Số điện thoại liên hệ không đúng định dạng (vd: 0987654321)")
    private String contactPhone;

    private String secondaryPhone;

    @NotNull(message = "Vui lòng cung cấp tọa độ vĩ độ (Latitude)")
    @DecimalMin(value = "-90.0", message = "Vĩ độ phải nằm trong khoảng -90 đến 90")
    @DecimalMax(value = "90.0", message = "Vĩ độ phải nằm trong khoảng -90 đến 90")
    private Double latitude;

    @NotNull(message = "Vui lòng cung cấp tọa độ kinh độ (Longitude)")
    @DecimalMin(value = "-180.0", message = "Kinh độ phải nằm trong khoảng -180 đến 180")
    @DecimalMax(value = "180.0", message = "Kinh độ phải nằm trong khoảng -180 đến 180")
    private Double longitude;

    private String addressDetail;

    private String urgencyLevel = "HIGH"; // CRITICAL, HIGH, MEDIUM, LOW

    @NotNull(message = "Số lượng người không được để trống")
    @Min(value = 1, message = "Số lượng người cần cứu nạn tối thiểu là 1")
    private Integer peopleCount = 1;

    private Boolean hasElderly = false;
    private Boolean hasChildren = false;
    private Boolean hasInjured = false;
    private String description;
    private String imageUrl;

    public SosRequestDto() {
    }

    public SosRequestDto(String contactName, String contactPhone, String secondaryPhone, Double latitude, Double longitude, String addressDetail, String urgencyLevel, Integer peopleCount, Boolean hasElderly, Boolean hasChildren, Boolean hasInjured, String description, String imageUrl) {
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.secondaryPhone = secondaryPhone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.addressDetail = addressDetail;
        this.urgencyLevel = urgencyLevel != null ? urgencyLevel : "HIGH";
        this.peopleCount = peopleCount != null ? peopleCount : 1;
        this.hasElderly = Boolean.TRUE.equals(hasElderly);
        this.hasChildren = Boolean.TRUE.equals(hasChildren);
        this.hasInjured = Boolean.TRUE.equals(hasInjured);
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public static SosRequestDtoBuilder builder() {
        return new SosRequestDtoBuilder();
    }

    public static class SosRequestDtoBuilder {
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

        public SosRequestDtoBuilder contactName(String contactName) { this.contactName = contactName; return this; }
        public SosRequestDtoBuilder contactPhone(String contactPhone) { this.contactPhone = contactPhone; return this; }
        public SosRequestDtoBuilder secondaryPhone(String secondaryPhone) { this.secondaryPhone = secondaryPhone; return this; }
        public SosRequestDtoBuilder latitude(Double latitude) { this.latitude = latitude; return this; }
        public SosRequestDtoBuilder longitude(Double longitude) { this.longitude = longitude; return this; }
        public SosRequestDtoBuilder addressDetail(String addressDetail) { this.addressDetail = addressDetail; return this; }
        public SosRequestDtoBuilder urgencyLevel(String urgencyLevel) { this.urgencyLevel = urgencyLevel; return this; }
        public SosRequestDtoBuilder peopleCount(Integer peopleCount) { this.peopleCount = peopleCount; return this; }
        public SosRequestDtoBuilder hasElderly(Boolean hasElderly) { this.hasElderly = hasElderly; return this; }
        public SosRequestDtoBuilder hasChildren(Boolean hasChildren) { this.hasChildren = hasChildren; return this; }
        public SosRequestDtoBuilder hasInjured(Boolean hasInjured) { this.hasInjured = hasInjured; return this; }
        public SosRequestDtoBuilder description(String description) { this.description = description; return this; }
        public SosRequestDtoBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }

        public SosRequestDto build() {
            return new SosRequestDto(contactName, contactPhone, secondaryPhone, latitude, longitude, addressDetail, urgencyLevel, peopleCount, hasElderly, hasChildren, hasInjured, description, imageUrl);
        }
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
}
