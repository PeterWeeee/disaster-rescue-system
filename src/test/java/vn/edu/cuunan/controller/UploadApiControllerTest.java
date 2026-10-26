package vn.edu.cuunan.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import vn.edu.cuunan.service.CloudinaryService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class UploadApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CloudinaryService cloudinaryService;

    @Test
    @DisplayName("POST /api/upload/image với file hợp lệ -> Trả về 200 OK kèm URL")
    void testUploadImageSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "flood.jpg", "image/jpeg", "image bytes".getBytes()
        );

        Mockito.when(cloudinaryService.uploadImage(any(), anyString()))
                .thenReturn("https://res.cloudinary.com/cuunan247/image/upload/v1234/flood.jpg");

        mockMvc.perform(multipart("/api/upload/image")
                        .file(file)
                        .param("folder", "sos_images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.url").value("https://res.cloudinary.com/cuunan247/image/upload/v1234/flood.jpg"))
                .andExpect(jsonPath("$.originalName").value("flood.jpg"));
    }

    @Test
    @DisplayName("POST /api/upload/image với file rỗng -> Trả về 400 Bad Request")
    void testUploadEmptyFile() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.jpg", "image/jpeg", new byte[0]
        );

        mockMvc.perform(multipart("/api/upload/image").file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
