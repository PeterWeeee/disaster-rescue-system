package vn.edu.cuunan.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cuunan.entity.*;
import vn.edu.cuunan.repository.CommunityPostRepository;
import vn.edu.cuunan.repository.RoleRepository;
import vn.edu.cuunan.repository.UserRepository;
import vn.edu.cuunan.security.CustomUserDetails;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
class FeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommunityPostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;
    private CustomUserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        Role roleCitizen = roleRepository.findByName("ROLE_CITIZEN")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_CITIZEN")));

        testUser = userRepository.findByUsername("feed_tester").orElseGet(() -> {
            User u = new User();
            u.setUsername("feed_tester");
            u.setPassword("encodedPassword123");
            u.setFullName("Nguyễn Văn Feed");
            u.setPhone("0988776655");
            u.setEmail("feed_tester@test.com");
            u.setRole(roleCitizen);
            u.setStatus("ACTIVE");
            return userRepository.save(u);
        });

        testUserDetails = new CustomUserDetails(testUser);
    }

    @Test
    @DisplayName("GET /feed khi chưa đăng nhập -> Chuyển hướng đến /auth/login")
    void testFeedWithoutLogin() throws Exception {
        mockMvc.perform(get("/feed"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("GET /feed khi đã đăng nhập -> Trả về View feed/index 200 OK")
    void testFeedWithLogin() throws Exception {
        mockMvc.perform(get("/feed").with(user(testUserDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("feed/index"))
                .andExpect(model().attributeExists("currentUser"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("shelters"));
    }

    @Test
    @DisplayName("POST /api/feed/posts -> Tạo bài viết cảnh báo thành công")
    void testCreatePostApi() throws Exception {
        mockMvc.perform(post("/api/feed/posts")
                        .with(user(testUserDetails))
                        .param("content", "Cảnh báo ngập đường Nguyễn Tất Thành!")
                        .param("hazardType", "FLOOD")
                        .param("locationName", "Đà Nẵng")
                        .param("latitude", "16.0544")
                        .param("longitude", "108.2022"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.post.content").value("Cảnh báo ngập đường Nguyễn Tất Thành!"));
    }

    @Test
    @DisplayName("POST /api/feed/posts/{id}/upvote -> Xác nhận bài viết (+1 upvote)")
    void testUpvotePostApi() throws Exception {
        CommunityPost post = new CommunityPost();
        post.setAuthor(testUser);
        post.setAuthorName("Tác giả Test");
        post.setContent("Nước dâng cao tại ngã tư");
        post.setHazardType(HazardType.FLOOD);
        post.setUpvoteCount(5);
        CommunityPost saved = postRepository.save(post);

        mockMvc.perform(post("/api/feed/posts/" + saved.getId() + "/upvote")
                        .with(user(testUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.upvoteCount").value(6))
                .andExpect(jsonPath("$.isVoted").value(true));
    }

    @Test
    @DisplayName("POST /api/feed/posts/{id}/convert-sos -> Chuyển bài viết thành đơn cứu hộ SOS")
    void testConvertPostToSosApi() throws Exception {
        CommunityPost post = new CommunityPost();
        post.setAuthor(testUser);
        post.setAuthorName("Người cần cứu");
        post.setContent("Mắc kẹt 2 người trên gác lửng cần cano gấp");
        post.setHazardType(HazardType.FLOOD);
        post.setLocationName("Xã Hòa Tiến, Đà Nẵng");
        post.setLatitude(15.9876);
        post.setLongitude(108.1923);
        CommunityPost saved = postRepository.save(post);

        mockMvc.perform(post("/api/feed/posts/" + saved.getId() + "/convert-sos")
                        .with(user(testUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.sosId").exists());
    }
}
