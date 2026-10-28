package vn.edu.cuunan.service;

import com.cloudinary.Cloudinary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import vn.edu.cuunan.repository.MediaFileRepository;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private MediaFileRepository mediaFileRepository;

    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setUp() {
        cloudinaryService = new CloudinaryService(cloudinary, mediaFileRepository);
    }

    @Test
    @DisplayName("Upload file null hoặc rỗng -> ném IllegalArgumentException")
    void testUploadEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);
        assertThrows(IllegalArgumentException.class, () -> cloudinaryService.uploadImage(emptyFile, "sos_images"));
        assertThrows(IllegalArgumentException.class, () -> cloudinaryService.uploadImage(null, "sos_images"));
    }

    @Test
    @DisplayName("Upload file không phải định dạng ảnh -> ném IllegalArgumentException")
    void testUploadInvalidContentType() {
        MockMultipartFile pdfFile = new MockMultipartFile(
                "file", "document.pdf", "application/pdf", "Dummy PDF content".getBytes()
        );
        assertThrows(IllegalArgumentException.class, () -> cloudinaryService.uploadImage(pdfFile, "sos_images"));
    }

    @Test
    @DisplayName("Upload file dung lượng vượt quá 10MB -> ném IllegalArgumentException")
    void testUploadOversizedFile() {
        byte[] oversizedData = new byte[11 * 1024 * 1024]; // 11MB
        MockMultipartFile largeFile = new MockMultipartFile(
                "file", "huge.jpg", "image/jpeg", oversizedData
        );
        assertThrows(IllegalArgumentException.class, () -> cloudinaryService.uploadImage(largeFile, "sos_images"));
    }

    @Test
    @DisplayName("Khi chưa cấu hình Cloudinary -> Tự động fallback lưu vào thư mục cục bộ uploads/")
    void testFallbackToLocalStorage() {
        MockMultipartFile validImage = new MockMultipartFile(
                "file", "flood_photo.jpg", "image/jpeg", "fake image binary bytes".getBytes()
        );

        String resultUrl = cloudinaryService.uploadImage(validImage, "test_sos");

        assertNotNull(resultUrl);
        assertTrue(resultUrl.startsWith("/uploads/test_sos/"));
        assertTrue(resultUrl.endsWith(".jpg"));

        // Kiểm tra file vật lý đã được tạo trong thư mục uploads/test_sos/
        Path expectedPath = Paths.get(resultUrl.substring(1)); // bỏ dấu / ở đầu
        assertTrue(Files.exists(expectedPath));

        // Dọn dẹp file test sau khi chạy
        try {
            Files.deleteIfExists(expectedPath);
        } catch (Exception ignored) {}
    }

    @Test
    @DisplayName("Upload ảnh cẩm nang khi chưa có Cloudinary -> fallback lưu vào uploads/guide_images/")
    void testUploadGuideImageFallback() {
        MockMultipartFile guideImage = new MockMultipartFile(
                "file", "prep_step1.jpg", "image/jpeg", "fake guide image bytes".getBytes()
        );

        String resultUrl = cloudinaryService.uploadGuideImage(guideImage, "preparation", 1);

        assertNotNull(resultUrl);
        assertTrue(resultUrl.startsWith("/uploads/guide_images/"));
        assertTrue(resultUrl.endsWith(".jpg"));

        Path expectedPath = Paths.get(resultUrl.substring(1));
        assertTrue(Files.exists(expectedPath));

        try {
            Files.deleteIfExists(expectedPath);
        } catch (Exception ignored) {}
    }

    @Test
    @DisplayName("Upload file cục bộ null hoặc không tồn tại -> ném IllegalArgumentException")
    void testUploadNonExistentLocalFile() {
        assertThrows(IllegalArgumentException.class, () ->
                cloudinaryService.uploadLocalFile(new File("non_existent_file.xyz"), "guide_images", "test_id"));
    }
}
