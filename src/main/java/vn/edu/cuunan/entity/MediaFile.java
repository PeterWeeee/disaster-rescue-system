package vn.edu.cuunan.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "media_files")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MediaFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "original_name", length = 255)
    private String originalName;

    @Column(name = "file_url", nullable = false, length = 1000)
    private String fileUrl;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "storage_type", length = 30)
    private String storageType = "LOCAL"; // CLOUDINARY, LOCAL

    @Column(name = "public_id", length = 255)
    private String publicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sos_id")
    private SosRequest sosRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    public MediaFile() {
    }

    public MediaFile(Long id, String fileName, String originalName, String fileUrl, Long fileSize,
                     String contentType, String storageType, String publicId,
                     SosRequest sosRequest, User uploadedBy) {
        this.id = id;
        this.fileName = fileName;
        this.originalName = originalName;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.storageType = storageType != null ? storageType : "LOCAL";
        this.publicId = publicId;
        this.sosRequest = sosRequest;
        this.uploadedBy = uploadedBy;
    }

    public static MediaFileBuilder builder() {
        return new MediaFileBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public SosRequest getSosRequest() {
        return sosRequest;
    }

    public void setSosRequest(SosRequest sosRequest) {
        this.sosRequest = sosRequest;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public static class MediaFileBuilder {
        private Long id;
        private String fileName;
        private String originalName;
        private String fileUrl;
        private Long fileSize;
        private String contentType;
        private String storageType = "LOCAL";
        private String publicId;
        private SosRequest sosRequest;
        private User uploadedBy;

        public MediaFileBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public MediaFileBuilder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public MediaFileBuilder originalName(String originalName) {
            this.originalName = originalName;
            return this;
        }

        public MediaFileBuilder fileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
            return this;
        }

        public MediaFileBuilder fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public MediaFileBuilder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public MediaFileBuilder storageType(String storageType) {
            this.storageType = storageType;
            return this;
        }

        public MediaFileBuilder publicId(String publicId) {
            this.publicId = publicId;
            return this;
        }

        public MediaFileBuilder sosRequest(SosRequest sosRequest) {
            this.sosRequest = sosRequest;
            return this;
        }

        public MediaFileBuilder uploadedBy(User uploadedBy) {
            this.uploadedBy = uploadedBy;
            return this;
        }

        public MediaFile build() {
            return new MediaFile(id, fileName, originalName, fileUrl, fileSize, contentType, storageType, publicId, sosRequest, uploadedBy);
        }
    }
}
