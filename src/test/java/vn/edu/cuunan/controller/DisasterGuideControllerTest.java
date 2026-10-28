package vn.edu.cuunan.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class DisasterGuideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Truy cập cẩm nang khi chưa đăng nhập -> chuyển hướng về /auth/login")
    void testDisasterGuideOverviewAnonymousRedirects() throws Exception {
        mockMvc.perform(get("/guide/disaster"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?error=unauthorized"));

        mockMvc.perform(get("/guide/disaster/preparation"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?error=unauthorized"));
    }

    @Test
    @DisplayName("Citizen truy cập trang tổng quan cẩm nang -> hiển thị các nút xem chi tiết từng bước và không hiển thị nút SOS")
    @WithMockUser(username = "citizen_user", roles = {"CITIZEN"})
    void testDisasterGuideOverviewWithDetailButtons() throws Exception {
        mockMvc.perform(get("/guide/disaster"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Cẩm nang ứng phó thiên tai")))
                .andExpect(content().string(Matchers.containsString("Xem chi tiết các bước")))
                .andExpect(content().string(Matchers.containsString("/guide/disaster/preparation")))
                .andExpect(content().string(Matchers.containsString("/guide/disaster/storm")))
                .andExpect(content().string(Matchers.containsString("/guide/disaster/flood")))
                .andExpect(content().string(Matchers.containsString("kèm ảnh AI mô tả")))
                .andExpect(content().string(Matchers.not(Matchers.containsString("Mở chức năng SOS"))));
    }

    @Test
    @DisplayName("Citizen xem chi tiết thiên tai Chuẩn bị trước -> hiển thị từng bước, thanh chọn bên trái và không có nút SOS")
    @WithMockUser(username = "citizen_user", roles = {"CITIZEN"})
    void testDisasterGuideDetailPreparation() throws Exception {
        mockMvc.perform(get("/guide/disaster/preparation"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Chuẩn bị sẵn sàng cho gia đình")))
                .andExpect(content().string(Matchers.containsString("BƯỚC 01")))
                .andExpect(content().string(Matchers.containsString("Ảnh do AI tạo")))
                .andExpect(content().string(Matchers.containsString("Cloudinary CDN")))
                .andExpect(content().string(Matchers.containsString("guide-sidebar-card")))
                .andExpect(content().string(Matchers.containsString("guide-sidebar-item")))
                .andExpect(content().string(Matchers.containsString("/images/guide/prep-step1.jpg")))
                .andExpect(content().string(Matchers.containsString("/images/guide/prep-step2.jpg")))
                .andExpect(content().string(Matchers.containsString("Hành động thiết thực cần làm")))
                .andExpect(content().string(Matchers.containsString("NÊN LÀM")))
                .andExpect(content().string(Matchers.containsString("KHÔNG NÊN LÀM")))
                .andExpect(content().string(Matchers.not(Matchers.containsString("Phát tín hiệu SOS"))))
                .andExpect(content().string(Matchers.not(Matchers.containsString("Kích hoạt SOS khẩn cấp"))));
    }

    @Test
    @DisplayName("Citizen xem chi tiết bão gió mạnh -> hiển thị các bước và ảnh minh họa bão")
    @WithMockUser(username = "citizen_user", roles = {"CITIZEN"})
    void testDisasterGuideDetailStorm() throws Exception {
        mockMvc.perform(get("/guide/disaster/storm"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Ở trong nơi trú ẩn kiên cố")))
                .andExpect(content().string(Matchers.containsString("/images/guide/storm-step1.jpg")))
                .andExpect(content().string(Matchers.containsString("/images/guide/storm-step2.jpg")))
                .andExpect(content().string(Matchers.containsString("Ảnh do AI tạo")));
    }

    @Test
    @DisplayName("Citizen xem chi tiết lũ lụt -> hiển thị các bước và ảnh minh họa lũ")
    @WithMockUser(username = "citizen_user", roles = {"CITIZEN"})
    void testDisasterGuideDetailFlood() throws Exception {
        mockMvc.perform(get("/guide/disaster/flood"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Tránh nước chảy và nguy cơ điện giật")))
                .andExpect(content().string(Matchers.containsString("/images/guide/flood-step1.jpg")))
                .andExpect(content().string(Matchers.containsString("/images/guide/flood-step2.jpg")));
    }

    @Test
    @DisplayName("Slug không tồn tại -> chuyển hướng về /guide/disaster")
    @WithMockUser(username = "citizen_user", roles = {"CITIZEN"})
    void testInvalidSlugRedirects() throws Exception {
        mockMvc.perform(get("/guide/disaster/unknown-disaster"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/guide/disaster"));
    }

    @Test
    @DisplayName("Vai trò khác (COORDINATOR) truy cập cẩm nang -> bị từ chối 403 Forbidden")
    @WithMockUser(username = "coord_user", roles = {"COORDINATOR"})
    void testCoordinatorForbidden() throws Exception {
        mockMvc.perform(get("/guide/disaster/preparation"))
                .andExpect(status().isForbidden());
    }
}
