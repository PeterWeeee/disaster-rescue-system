package vn.edu.cuunan.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.cuunan.entity.MediaFile;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.MediaFileRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class CloudinaryService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);

    private final Cloudinary cloudinary;
    private final MediaFileRepository mediaFileRepository;

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif", "image/bmp"
    );

    public CloudinaryService(Cloudinary cloudinary, MediaFileRepository mediaFileRepository) {
        this.cloudinary = cloudinary;
        this.mediaFileRepository = mediaFileRepository;
    }

    /**
     * Kiểm tra xem thông tin tài khoản Cloudinary đã được cấu hình hợp lệ chưa.
     */
    public boolean isCloudinaryConfigured() {
        return cloudName != null && !cloudName.isBlank() && !cloudName.contains("your_cloud_name")
                && apiKey != null && !apiKey.isBlank() && !apiKey.contains("your_api_key")
                && apiSecret != null && !apiSecret.isBlank() && !apiSecret.contains("your_api_secret");
    }

    /**
     * Tải ảnh lên Cloudinary (hoặc tự động fallback lưu vào thư mục cục bộ nếu chưa cấu hình Cloudinary).
     *
     * @param file       File ảnh từ client
     * @param folderName Thư mục phân loại (ví dụ: "sos_images", "rescue_reports")
     * @return URL an toàn (HTTPS link từ Cloudinary hoặc URL nội bộ /uploads/...)
     */
    public String uploadImage(MultipartFile file, String folderName) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File tải lên không được để trống!");
        }

        validateImageFile(file);

        String safeFolder = (folderName != null && !folderName.isBlank()) ? folderName.trim() : "sos_images";

        // 1. Thử tải lên Cloudinary nếu đã có cấu hình
        if (isCloudinaryConfigured()) {
            try {
                log.info("Đang tải ảnh '{}' lên Cloudinary [folder: cuunan247/{}]...", file.getOriginalFilename(), safeFolder);

                @SuppressWarnings("rawtypes")
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                        "folder", "cuunan247/" + safeFolder,
                        "resource_type", "image"
                ));

                String secureUrl = (String) uploadResult.get("secure_url");
                String publicId = (String) uploadResult.get("public_id");
                log.info(">>> Tải ảnh lên Cloudinary thành công! URL: {}", secureUrl);

                // Lưu vết vào bảng media_files
                recordMediaFile(file, secureUrl, "CLOUDINARY", publicId, null, null);

                return secureUrl;
            } catch (Exception ex) {
                log.warn("Lỗi khi tải ảnh lên Cloudinary: {}. Tự động chuyển sang lưu trữ cục bộ (Local Storage Fallback)!", ex.getMessage());
            }
        } else {
            log.info("Chưa cấu hình tài khoản Cloudinary hợp lệ. Sử dụng cơ chế lưu trữ cục bộ (Local Storage).");
        }

        // 2. Fallback: Lưu vào thư mục uploads/ cục bộ
        return uploadToLocalStorage(file, safeFolder, null, null);
    }

    /**
     * Tải ảnh kèm liên kết trực tiếp với ca SOS và Người tải.
     */
    public String uploadImageWithContext(MultipartFile file, String folderName, SosRequest sosRequest, User uploadedBy) {
        String url = uploadImage(file, folderName);

        // Cập nhật lại context vào bản ghi MediaFile mới nhất
        try {
            MediaFile media = MediaFile.builder()
                    .fileName(extractFileNameFromUrl(url))
                    .originalName(file.getOriginalFilename())
                    .fileUrl(url)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .storageType(isCloudinaryConfigured() && url.startsWith("http") ? "CLOUDINARY" : "LOCAL")
                    .sosRequest(sosRequest)
                    .uploadedBy(uploadedBy)
                    .build();
            mediaFileRepository.save(media);
        } catch (Exception ex) {
            log.warn("Không thể lưu liên kết MediaFile: {}", ex.getMessage());
        }

        return url;
    }

    /**
     * Lưu ảnh vào thư mục cục bộ uploads/
     */
    private String uploadToLocalStorage(MultipartFile file, String folderName, SosRequest sosRequest, User uploadedBy) {
        try {
            Path targetDir = Paths.get("uploads", folderName);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            } else {
                extension = ".jpg";
            }

            String uniqueName = UUID.randomUUID().toString() + extension;
            Path targetPath = targetDir.resolve(uniqueName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String localUrl = "/uploads/" + folderName + "/" + uniqueName;
            log.info(">>> Lưu ảnh cục bộ thành công: {}", localUrl);

            recordMediaFile(file, localUrl, "LOCAL", null, sosRequest, uploadedBy);

            return localUrl;
        } catch (IOException ioEx) {
            log.error("Lỗi khi lưu ảnh vào bộ nhớ cục bộ: ", ioEx);
            throw new RuntimeException("Không thể lưu ảnh hiện trường: " + ioEx.getMessage());
        }
    }

    private void recordMediaFile(MultipartFile file, String url, String storageType, String publicId,
                                 SosRequest sosRequest, User uploadedBy) {
        try {
            MediaFile record = MediaFile.builder()
                    .fileName(extractFileNameFromUrl(url))
                    .originalName(file.getOriginalFilename())
                    .fileUrl(url)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .storageType(storageType)
                    .publicId(publicId)
                    .sosRequest(sosRequest)
                    .uploadedBy(uploadedBy)
                    .build();
            mediaFileRepository.save(record);
        } catch (Exception ex) {
            log.warn("Ghi log MediaFile thất bại (không ảnh hưởng luồng chính): {}", ex.getMessage());
        }
    }

    private void validateImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            // Kiểm tra thêm theo đuôi file đề phòng trình duyệt nhận diện application/octet-stream
            String originalName = file.getOriginalFilename();
            if (originalName == null || !originalName.matches(".*\\.(?i)(jpg|jpeg|png|webp|gif|bmp)$")) {
                throw new IllegalArgumentException("Chỉ chấp nhận các định dạng ảnh: JPG, PNG, WEBP, GIF!");
            }
        }

        // Tối đa 10MB
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Dung lượng ảnh vượt quá giới hạn cho phép (tối đa 10MB)!");
        }
    }

    private String extractFileNameFromUrl(String url) {
        if (url == null) return "unknown";
        int lastSlash = url.lastIndexOf('/');
        return (lastSlash != -1 && lastSlash < url.length() - 1) ? url.substring(lastSlash + 1) : url;
    }
}
