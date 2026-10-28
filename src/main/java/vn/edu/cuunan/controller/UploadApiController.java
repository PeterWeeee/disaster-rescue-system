package vn.edu.cuunan.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.cuunan.service.CloudinaryService;
import vn.edu.cuunan.service.DisasterGuideService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadApiController {

    private static final Logger log = LoggerFactory.getLogger(UploadApiController.class);

    private final CloudinaryService cloudinaryService;
    private final DisasterGuideService disasterGuideService;

    public UploadApiController(CloudinaryService cloudinaryService, DisasterGuideService disasterGuideService) {
        this.cloudinaryService = cloudinaryService;
        this.disasterGuideService = disasterGuideService;
    }

    /**
     * REST API tiếp nhận tải ảnh hiện trường từ trình duyệt hoặc thiết bị di động.
     * Hỗ trợ tự động chuyển đổi sang Cloudinary hoặc lưu cục bộ.
     */
    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "sos_images") String folder) {

        Map<String, Object> response = new HashMap<>();

        if (file == null || file.isEmpty()) {
            response.put("success", false);
            response.put("message", "Vui lòng chọn một file ảnh hợp lệ!");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String uploadedUrl = cloudinaryService.uploadImage(file, folder);

            response.put("success", true);
            response.put("message", "Tải ảnh hiện trường thành công!");
            response.put("url", uploadedUrl);
            response.put("originalName", file.getOriginalFilename());
            response.put("fileSize", file.getSize());
            response.put("storage", cloudinaryService.isCloudinaryConfigured() && uploadedUrl.startsWith("http") ? "CLOUDINARY" : "LOCAL");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException valEx) {
            log.warn("Lỗi kiểm tra định dạng ảnh: {}", valEx.getMessage());
            response.put("success", false);
            response.put("message", valEx.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception ex) {
            log.error("Lỗi ngoại lệ khi xử lý tải ảnh: ", ex);
            response.put("success", false);
            response.put("message", "Đã xảy ra lỗi khi tải ảnh lên máy chủ: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * REST API tải ảnh chuyên dụng cho từng bước trong cẩm nang ứng phó thiên tai lên Cloudinary.
     * Tự động cập nhật trực tiếp vào DisasterGuideService để các trang xem chi tiết hiển thị tức thì.
     */
    @PostMapping("/guide-image")
    public ResponseEntity<Map<String, Object>> uploadGuideImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("guideSlug") String guideSlug,
            @RequestParam("stepNumber") int stepNumber) {

        Map<String, Object> response = new HashMap<>();

        if (file == null || file.isEmpty()) {
            response.put("success", false);
            response.put("message", "Vui lòng chọn một file ảnh cẩm nang hợp lệ!");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String uploadedUrl = cloudinaryService.uploadGuideImage(file, guideSlug, stepNumber);
            boolean updated = disasterGuideService.updateStepImage(guideSlug, stepNumber, uploadedUrl);

            response.put("success", true);
            response.put("message", "Tải ảnh cẩm nang lên Cloudinary thành công!");
            response.put("url", uploadedUrl);
            response.put("guideSlug", guideSlug);
            response.put("stepNumber", stepNumber);
            response.put("guideUpdated", updated);
            response.put("storage", cloudinaryService.isCloudinaryConfigured() && uploadedUrl.startsWith("http") ? "CLOUDINARY" : "LOCAL");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException valEx) {
            log.warn("Lỗi kiểm tra định dạng ảnh cẩm nang: {}", valEx.getMessage());
            response.put("success", false);
            response.put("message", valEx.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception ex) {
            log.error("Lỗi ngoại lệ khi xử lý tải ảnh cẩm nang: ", ex);
            response.put("success", false);
            response.put("message", "Đã xảy ra lỗi khi tải ảnh lên máy chủ: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
