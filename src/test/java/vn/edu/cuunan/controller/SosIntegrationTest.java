package vn.edu.cuunan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cuunan.dto.SosRequestDto;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.repository.SosRequestRepository;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
class SosIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SosRequestRepository sosRequestRepository;

    @Test
    @DisplayName("GET /sos - Truy cập trang form gửi yêu cầu cứu nạn khẩn cấp thành công")
    void testGetSosFormPage() throws Exception {
        mockMvc.perform(get("/sos"))
                .andExpect(status().isOk())
                .andExpect(view().name("sos/request"))
                .andExpect(model().attributeExists("sosRequestDto"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(content().string(containsString("Phiếu Yêu Cầu Cứu Nạn Khẩn Cấp (SOS)")))
                .andExpect(content().string(containsString("📍 LẤY VỊ TRÍ GPS HIỆN TẠI CỦA TÔI")));
    }

    @Test
    @DisplayName("GET /sos/create - Chuyển hướng về /sos")
    void testGetSosCreateRedirect() throws Exception {
        mockMvc.perform(get("/sos/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sos"));
    }

    @Test
    @DisplayName("POST /sos - Gửi form cứu nạn thành công và chuyển hướng đến trang /sos/track/{id}")
    void testSubmitSosForm_Success() throws Exception {
        mockMvc.perform(post("/sos")
                        .param("contactName", "Lê Văn Tự Do")
                        .param("contactPhone", "0977112233")
                        .param("latitude", "16.0700")
                        .param("longitude", "108.2200")
                        .param("addressDetail", "Số 15 đường Bạch Đằng, Đà Nẵng")
                        .param("urgencyLevel", "CRITICAL")
                        .param("peopleCount", "4")
                        .param("hasElderly", "true")
                        .param("hasChildren", "true")
                        .param("hasInjured", "false")
                        .param("description", "Nhà ngập 1 mét, cúp điện toàn bộ"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/sos/track/*"))
                .andExpect(flash().attributeExists("successMessage"));

        // Kiểm tra đã được lưu vào CSDL
        boolean exists = sosRequestRepository.findAll().stream()
                .anyMatch(r -> "0977112233".equals(r.getContactPhone()) && "Lê Văn Tự Do".equals(r.getContactName()));
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("POST /sos - Thiếu trường bắt buộc (SĐT) trả về lỗi validation")
    void testSubmitSosForm_ValidationError() throws Exception {
        mockMvc.perform(post("/sos")
                        .param("contactName", "Nguyễn Không SĐT")
                        .param("contactPhone", "") // Rỗng
                        .param("latitude", "16.0500")
                        .param("longitude", "108.2000"))
                .andExpect(status().isOk())
                .andExpect(view().name("sos/request"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("GET /sos/track/{id} - Xem chi tiết tiến độ ca SOS tồn tại thành công")
    void testTrackSosDetail_Found() throws Exception {
        // Lấy 1 ca SOS có sẵn từ DataInitializer
        SosRequest firstSos = sosRequestRepository.findAll().stream().findFirst().orElseThrow();

        mockMvc.perform(get("/sos/track/" + firstSos.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("sos/track"))
                .andExpect(model().attributeExists("sos"))
                .andExpect(model().attributeExists("stepIndex"))
                .andExpect(content().string(containsString("#SOS-" + firstSos.getId())))
                .andExpect(content().string(containsString("Tiến Trình Xử Lý Cứu Hộ")));
    }

    @Test
    @DisplayName("GET /sos/track/{id} - Ca SOS không tồn tại hiển thị thông báo lỗi thân thiện")
    void testTrackSosDetail_NotFound() throws Exception {
        mockMvc.perform(get("/sos/track/999999"))
                .andExpect(status().isOk())
                .andExpect(view().name("sos/track"))
                .andExpect(model().attribute("sosNotFound", true))
                .andExpect(content().string(containsString("Không Tìm Thấy Ca Cứu Nạn")));
    }

    @Test
    @DisplayName("GET /sos/track?phone=... - Tra cứu danh sách ca SOS theo số điện thoại")
    void testTrackSearchByPhone() throws Exception {
        mockMvc.perform(get("/sos/track").param("phone", "0912345678"))
                .andExpect(status().isOk())
                .andExpect(view().name("sos/track"))
                .andExpect(model().attributeExists("results"))
                .andExpect(content().string(containsString("Danh Sách Ca Cứu Nạn")));
    }

    @Test
    @DisplayName("POST /api/sos - Gọi REST API tạo ca cứu nạn SOS trả về HTTP 201 JSON")
    void testApiCreateSos_Success() throws Exception {
        SosRequestDto dto = SosRequestDto.builder()
                .contactName("API User SOS")
                .contactPhone("0934567890")
                .latitude(16.0400)
                .longitude(108.1900)
                .urgencyLevel("HIGH")
                .peopleCount(2)
                .addressDetail("Gần bệnh viện Đa Khoa")
                .build();

        mockMvc.perform(post("/api/sos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.contactName").value("API User SOS"))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.trackUrl").exists());
    }

    @Test
    @DisplayName("GET /api/sos - Gọi REST API lấy danh sách toàn bộ các điểm SOS cho bản đồ Leaflet")
    void testApiListSos() throws Exception {
        mockMvc.perform(get("/api/sos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").isNumber())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("GET /api/sos/{id} - Gọi REST API lấy chi tiết 1 ca cứu nạn")
    void testApiGetSosDetail() throws Exception {
        SosRequest sample = sosRequestRepository.findAll().stream().findFirst().orElseThrow();

        mockMvc.perform(get("/api/sos/" + sample.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(sample.getId()))
                .andExpect(jsonPath("$.data.contactPhone").value(sample.getContactPhone()));
    }

    @Test
    @DisplayName("PATCH /api/sos/{id}/status - Cập nhật trạng thái ca SOS thành công")
    void testApiUpdateStatus() throws Exception {
        SosRequest sample = sosRequestRepository.findAll().stream().findFirst().orElseThrow();

        mockMvc.perform(patch("/api/sos/" + sample.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "DISPATCHED"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("DISPATCHED"));
    }
}
