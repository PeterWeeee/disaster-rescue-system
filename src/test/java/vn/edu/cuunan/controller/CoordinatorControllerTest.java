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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        try (FileWriter fw = new FileWriter("target/rendered_dashboard.html")) {
            fw.write(html);
        }
        System.out.println("Rendered Coordinator Dashboard HTML length: " + html.length());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testRenderAdminDashboard() throws Exception {
        MvcResult result = mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andReturn();

        String html = result.getResponse().getContentAsString();
        System.out.println("Rendered Admin Dashboard HTML length: " + html.length());
    }

    @Test
    @WithMockUser(username = "rescuer", roles = {"RESCUER"})
    void testRenderRescuerMissions() throws Exception {
        MvcResult result = mockMvc.perform(get("/rescuer/missions"))
                .andExpect(status().isOk())
                .andReturn();

        String html = result.getResponse().getContentAsString();
        System.out.println("Rendered Rescuer Missions HTML length: " + html.length());
    }
}
