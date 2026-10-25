package vn.edu.cuunan.dto;

public class SosRequestDto {
    private String contactName;
    private String contactPhone;
    private String secondaryPhone;
    private Double latitude;
    private Double longitude;
    private String addressDetail;
    private String urgencyLevel;
    private Integer peopleCount;
    private Boolean hasElderly;
    private Boolean hasChildren;
    private Boolean hasInjured;
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
        this.urgencyLevel = urgencyLevel;
        this.peopleCount = peopleCount;
        this.hasElderly = hasElderly;
        this.hasChildren = hasChildren;
        this.hasInjured = hasInjured;
        this.description = description;
        this.imageUrl = imageUrl;
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
