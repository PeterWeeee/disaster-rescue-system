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
import vn.edu.cuunan.repository.PostCommentRepository;
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
    private PostCommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private org.springframework.web.servlet.ViewResolver viewResolver;

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
    @DisplayName("GET /feed khi đã đăng nhập -> Trả về View feed/index 200 OK với bình luận và cảm xúc")
    void testFeedWithLogin() throws Exception {
        mockMvc.perform(get("/feed").with(user(testUserDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("feed/index"))
                .andExpect(model().attributeExists("currentUser"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("shelters"))
                .andExpect(model().attributeExists("commentsMap"))
                .andExpect(model().attributeExists("userReactions"))
                .andExpect(model().attributeExists("postReactions"));
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
                .andExpect(jsonPath("$.activeReaction").value("LIKE"));
    }

    @Test
    @DisplayName("POST /api/feed/posts/{id}/react?type=CARE -> Thả icon cảm xúc Thương thương")
    void testReactPostApi() throws Exception {
        CommunityPost post = new CommunityPost();
        post.setAuthor(testUser);
        post.setAuthorName("Tác giả Cảm Xúc");
        post.setContent("Đang tập kết nhu yếu phẩm cho bà con");
        post.setHazardType(HazardType.ISOLATED);
        post.setUpvoteCount(10);
        CommunityPost saved = postRepository.save(post);

        mockMvc.perform(post("/api/feed/posts/" + saved.getId() + "/react")
                        .with(user(testUserDetails))
                        .param("type", "CARE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.upvoteCount").value(11))
                .andExpect(jsonPath("$.activeReaction").value("CARE"))
                .andExpect(jsonPath("$.breakdown.CARE").value(1));

        // Nhấn lại lần nữa -> Toggle un-react
        mockMvc.perform(post("/api/feed/posts/" + saved.getId() + "/react")
                        .with(user(testUserDetails))
                        .param("type", "CARE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.upvoteCount").value(10))
                .andExpect(jsonPath("$.isRemoved").value(true));
    }

    @Test
    @DisplayName("POST /api/feed/posts/{id}/comments và GET /comments -> Thêm và tải bình luận")
    void testCommentsApi() throws Exception {
        CommunityPost post = new CommunityPost();
        post.setAuthor(testUser);
        post.setAuthorName("Tác giả Bình Luận");
        post.setContent("Cần xuồng hỗ trợ tại xã Hòa Khương");
        post.setHazardType(HazardType.FLOOD);
        post.setCommentCount(0);
        CommunityPost saved = postRepository.save(post);

        // Gửi bình luận mới
        mockMvc.perform(post("/api/feed/posts/" + saved.getId() + "/comments")
                        .with(user(testUserDetails))
                        .param("content", "Đội 03 đang di chuyển tới, hãy giữ an toàn!"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.comment.content").value("Đội 03 đang di chuyển tới, hãy giữ an toàn!"))
                .andExpect(jsonPath("$.comment.authorName").value("Nguyễn Văn Feed"));

        // Lấy danh sách bình luận
        mockMvc.perform(get("/api/feed/posts/" + saved.getId() + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("Đội 03 đang di chuyển tới, hãy giữ an toàn!"));
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

    @Test
    @DisplayName("GET /feed/my-posts -> Trả về View feed/my-posts 200 OK")
    void testShowMyPosts() throws Exception {
        CommunityPost post = new CommunityPost();
        post.setAuthor(testUser);
        post.setAuthorName("Tác giả Của Tôi");
        post.setContent("Bài đăng mẫu của chính tài khoản này");
        post.setHazardType(HazardType.STORM);
        post.setVerificationStatus(null);
        postRepository.save(post);


        org.springframework.test.web.servlet.MvcResult mvcResult = mockMvc.perform(get("/feed/my-posts").with(user(testUserDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("feed/my-posts"))
                .andExpect(model().attributeExists("currentUser"))
                .andExpect(model().attributeExists("myPosts"))
                .andExpect(model().attributeExists("myPostsCount"))
                .andExpect(model().attributeExists("pendingCount"))
                .andReturn();
        org.springframework.web.servlet.View view = viewResolver.resolveViewName(
                mvcResult.getModelAndView().getViewName(), java.util.Locale.getDefault());
        view.render(mvcResult.getModelAndView().getModel(),
                mvcResult.getRequest(), mvcResult.getResponse());
    }

    @Test
    @DisplayName("POST /api/feed/posts/{id}/verify -> Chỉ Admin có quyền duyệt bài")
    void testVerifyPostApi() throws Exception {
        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_ADMIN")));

        User adminUser = userRepository.findByUsername("admin_tester").orElseGet(() -> {
            User u = new User();
            u.setUsername("admin_tester");
            u.setPassword("encodedPassword123");
            u.setFullName("Quản Trị Viên Test");
            u.setPhone("0911223344");
            u.setEmail("admin_tester@test.com");
            u.setRole(roleAdmin);
            u.setStatus("ACTIVE");
            return userRepository.save(u);
        });
        CustomUserDetails adminDetails = new CustomUserDetails(adminUser);

        CommunityPost post = new CommunityPost();
        post.setAuthor(testUser);
        post.setAuthorName("Người Dân Báo Tin");
        post.setContent("Đang có hiện tượng lũ quét cục bộ");
        post.setHazardType(HazardType.FLOOD);
        post.setVerificationStatus(null);
        CommunityPost saved = postRepository.save(post);

        // Admin duyệt bài thành VERIFIED
        mockMvc.perform(post("/api/feed/posts/" + saved.getId() + "/verify")
                        .with(user(adminDetails))
                        .param("status", "VERIFIED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value("VERIFIED"));

        // Admin bác bỏ bài thành REJECTED
        mockMvc.perform(post("/api/feed/posts/" + saved.getId() + "/verify")
                        .with(user(adminDetails))
                        .param("status", "REJECTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    @DisplayName("POST /api/feed/posts/{id}/edit -> Tác giả sửa bài viết thành công")
    void testEditPostApi() throws Exception {
        CommunityPost post = new CommunityPost();
        post.setAuthor(testUser);
        post.setAuthorName("Tác giả Cần Sửa");
        post.setContent("Nội dung ban đầu");
        post.setHazardType(HazardType.FLOOD);
        post.setLocationName("Đà Nẵng ban đầu");
        CommunityPost saved = postRepository.save(post);

        mockMvc.perform(post("/api/feed/posts/" + saved.getId() + "/edit")
                        .with(user(testUserDetails))
                        .param("content", "Nội dung sau khi cập nhật đã sửa đổi")
                        .param("hazardType", "LANDSLIDE")
                        .param("locationName", "Đèo Hải Vân, Đà Nẵng"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.post.content").value("Nội dung sau khi cập nhật đã sửa đổi"))
                .andExpect(jsonPath("$.post.hazardType").value("LANDSLIDE"));
    }

    @Test
    @DisplayName("DELETE /api/feed/posts/{id} -> Tác giả xóa bài viết thành công")
    void testDeletePostApi() throws Exception {
        CommunityPost post = new CommunityPost();
        post.setAuthor(testUser);
        post.setAuthorName("Tác giả Cần Xóa");
        post.setContent("Bài viết chuẩn bị xóa");
        post.setHazardType(HazardType.OTHER);
        CommunityPost saved = postRepository.save(post);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/feed/posts/" + saved.getId())
                        .with(user(testUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/feed/posts/{id} -> Lấy chi tiết bài viết")
    void testGetPostById() throws Exception {
        CommunityPost post = new CommunityPost();
        post.setAuthor(testUser);
        post.setAuthorName("Tác giả Test");
        post.setContent("Nội dung test lấy chi tiết");
        post.setHazardType(HazardType.FLOOD);
        CommunityPost saved = postRepository.save(post);

        mockMvc.perform(get("/api/feed/posts/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.content").value("Nội dung test lấy chi tiết"));
    }

}