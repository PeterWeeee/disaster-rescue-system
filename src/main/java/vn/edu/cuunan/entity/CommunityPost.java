package vn.edu.cuunan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "community_posts")
public class CommunityPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    @Column(name = "author_name", nullable = false, length = 150)
    private String authorName;

    @Enumerated(EnumType.STRING)
    @Column(name = "hazard_type", nullable = false, length = 50)
    private HazardType hazardType = HazardType.OTHER;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "location_name", length = 255)
    private String locationName;

    private Double latitude;
    private Double longitude;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", length = 30)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(name = "upvote_count")
    private int upvoteCount = 0;

    @Column(name = "comment_count")
    private int commentCount = 0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sos_request_id")
    private SosRequest convertedSosRequest;

    public CommunityPost() {
    }

    public CommunityPost(Long id, User author, String authorName, HazardType hazardType, String content, String locationName, Double latitude, Double longitude, String imageUrl, VerificationStatus verificationStatus, int upvoteCount, int commentCount, SosRequest convertedSosRequest) {
        this.id = id;
        this.author = author;
        this.authorName = authorName;
        this.hazardType = hazardType;
        this.content = content;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.verificationStatus = verificationStatus;
        this.upvoteCount = upvoteCount;
        this.commentCount = commentCount;
        this.convertedSosRequest = convertedSosRequest;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public HazardType getHazardType() {
        return hazardType;
    }

    public void setHazardType(HazardType hazardType) {
        this.hazardType = hazardType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public int getUpvoteCount() {
        return upvoteCount;
    }

    public void setUpvoteCount(int upvoteCount) {
        this.upvoteCount = upvoteCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public SosRequest getConvertedSosRequest() {
        return convertedSosRequest;
    }

    public void setConvertedSosRequest(SosRequest convertedSosRequest) {
        this.convertedSosRequest = convertedSosRequest;
    }
}
