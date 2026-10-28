package vn.edu.cuunan.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.FileWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class CoordinatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "coordinator", roles = {"COORDINATOR"})
    void testRenderDashboard() throws Exception {
        MvcResult result = mockMvc.perform(get("/coordinator/dashboard"))
                .andExpect(status().isOk())
                .andReturn();

        String html = result.getResponse().getContentAsString();
        assertTrue(html.contains("TRUNG TÂM ĐIỀU PHỐI CỨU NẠN"), "Tiêu đề tiếng Việt chuẩn");
        assertTrue(html.contains("/auth/logout"), "Chứa link/form đăng xuất");
        try (FileWriter fw = new FileWriter("target/rendered_dashboard.html")) {
            fw.write(html);
        }
        System.out.println("Rendered Coordinator Dashboard HTML length: " + html.length());
    }

    @Test
    @WithMockUser(username = "coordinator", roles = {"COORDINATOR"})
    void testRenderTeams() throws Exception {
        MvcResult result = mockMvc.perform(get("/coordinator/teams"))
                .andExpect(status().isOk())
                .andReturn();

        String html = result.getResponse().getContentAsString();
        assertTrue(html.contains("QUẢN LÝ CÁC ĐỘI CỨU HỘ & QUÂN TRANG"), "Tiêu đề quản lý đội cứu hộ chuẩn");
        assertTrue(html.contains("/auth/logout"), "Chứa link/form đăng xuất");
        System.out.println("Rendered Teams HTML length: " + html.length());
    }

    @Test
    @WithMockUser(username = "coordinator", roles = {"COORDINATOR"})
    void testRenderMissions() throws Exception {
        MvcResult result = mockMvc.perform(get("/coordinator/missions"))
                .andExpect(status().isOk())
                .andReturn();

        String html = result.getResponse().getContentAsString();
        assertTrue(html.contains("THEO DÕI CỨU HỘ & LỘ TRÌNH TÁC CHIẾN"), "Tiêu đề theo dõi cứu hộ & lộ trình chuẩn");
        assertTrue(html.contains("id=\"missionsMap\""), "Chứa container bản đồ Leaflet");
        assertTrue(html.contains("initMissionsMap"), "Chứa hàm khởi tạo bản đồ GIS lộ trình");
        assertTrue(html.contains("renderMissionsOnMap"), "Chứa hàm vẽ lộ trình và đội cứu hộ");
        assertTrue(html.contains("focusMissionFromButton"), "Chứa hàm định vị nhiệm vụ trên bản đồ");
        assertTrue(html.contains("evidenceModal"), "Chứa modal nghiệm thu hiện trường");
        assertTrue(html.contains("/auth/logout"), "Chứa link/form đăng xuất");
        System.out.println("Rendered Missions HTML length: " + html.length());
    }

    @Test
    @WithMockUser(username = "coordinator", roles = {"COORDINATOR"})
    void testRenderBudget() throws Exception {
        MvcResult result = mockMvc.perform(get("/coordinator/budget"))
                .andExpect(status().isOk())
                .andReturn();

        String html = result.getResponse().getContentAsString();
        assertTrue(html.contains("QUẢN LÝ TIẾP TẾ NGÂN SÁCH & QUỸ CỨU TRỢ TỔ CHỨC"), "Tiêu đề quỹ cứu trợ & tiếp tế ngân sách chuẩn");
        assertTrue(html.contains("rejectBudgetModal"), "Chứa modal từ chối tiếp tế ngân sách");
        assertTrue(html.contains("submitRejectBudget"), "Chứa script xử lý tiếp tế ngân sách");
        assertTrue(html.contains("/auth/logout"), "Chứa link/form đăng xuất");
        System.out.println("Rendered Budget HTML length: " + html.length());
    }

    @Test
    @WithMockUser(username = "coordinator", roles = {"COORDINATOR"})
    void testRenderCoordinatorHistory() throws Exception {
        MvcResult result = mockMvc.perform(get("/coordinator/history"))
                .andExpect(status().isOk())
                .andReturn();

        String html = result.getResponse().getContentAsString();
        assertTrue(html.contains("LỊCH SỬ ĐIỀU PHỐI CỨU NẠN"), "Tiêu đề lịch sử điều phối chuẩn");
        assertTrue(html.contains("historyTable"), "Chứa bảng lịch sử điều phối");
        assertTrue(html.contains("historyPaginationNav"), "Chứa phân trang lịch sử điều phối");
        assertTrue(html.contains("evidenceModal"), "Chứa modal nghiệm thu minh chứng");
        assertTrue(html.contains("/auth/logout"), "Chứa link/form đăng xuất");
        System.out.println("Rendered History HTML length: " + html.length());
    }

    @Test
    @WithMockUser(username = "rescuer", roles = {"RESCUER"})
    void testRenderRescuerResources() throws Exception {
        MvcResult result = mockMvc.perform(get("/rescuer/resources"))
                .andExpect(status().isOk())
                .andReturn();

        String html = result.getResponse().getContentAsString();
        assertTrue(html.contains("THỐNG KÊ QUÂN SỐ & KHO TRANG BỊ CỨU HỘ"), "Tiêu đề trang quân số và trang bị chuẩn");
        assertTrue(html.contains("membersTable"), "Chứa bảng quân số");
        assertTrue(html.contains("equipmentTable"), "Chứa bảng kho trang bị");
        assertTrue(html.contains("budgetHistoryTable"), "Chứa bảng lịch sử tiếp tế ngân sách");
        assertTrue(html.contains("/auth/logout"), "Chứa link/form đăng xuất");
        System.out.println("Rendered Rescuer Resources HTML length: " + html.length());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testRenderAdminDashboard() throws Exception {
        MvcResult result = mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andReturn();

        String html = result.getResponse().getContentAsString();
        assertTrue(html.contains("Trung tâm quản trị"), "Tiêu đề tiếng Việt chuẩn");
        assertTrue(html.contains("/auth/logout"), "Chứa link/form đăng xuất");
        System.out.println("Rendered Admin Dashboard HTML length: " + html.length());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testRenderAdminDashboardFinanceTab() throws Exception {
        MvcResult result = mockMvc.perform(get("/admin/dashboard?view=finance"))
                .andExpect(status().isOk())
                .andReturn();

        String html = result.getResponse().getContentAsString();
        assertTrue(html.contains("Báo cáo thống kê thu chi"), "Tiêu đề tab thống kê thu chi");
        assertTrue(html.contains("donationsTable"), "Chứa bảng dòng tiền thu");
        assertTrue(html.contains("disbursementsTable"), "Chứa bảng dòng tiền chi");
        System.out.println("Rendered Admin Finance HTML length: " + html.length());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAdminFinanceRedirect() throws Exception {
        mockMvc.perform(get("/admin/finance"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard?view=finance"));
    }

    @Test
    @WithMockUser(username = "rescuer", roles = {"RESCUER"})
    void testRenderRescuerMissions() throws Exception {
        MvcResult result = mockMvc.perform(get("/rescuer/missions"))
                .andExpect(status().isOk())
                .andReturn();

        String html = result.getResponse().getContentAsString();
        assertTrue(html.contains("TÁC NGHIỆP CỨU HỘ HIỆN TRƯỜNG"), "Tiêu đề tiếng Việt chuẩn");
        assertTrue(html.contains("/auth/logout"), "Chứa link/form đăng xuất");
        System.out.println("Rendered Rescuer Missions HTML length: " + html.length());
    }

    @Test
    void testLogoutViaGetAndPost() throws Exception {
        mockMvc.perform(get("/auth/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?logout=true"));

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?logout=true"));
    }

    @Test
    @WithMockUser(username = "coordinator", roles = {"COORDINATOR"})
    void testCoordinatorHomeRedirect() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/coordinator/dashboard"));
    }
}
