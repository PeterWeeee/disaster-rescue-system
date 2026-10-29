package vn.edu.cuunan.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.cuunan.dto.SosRequestDto;
import vn.edu.cuunan.entity.*;
import vn.edu.cuunan.repository.CommunityPostRepository;
import vn.edu.cuunan.repository.EvacuationShelterRepository;
import vn.edu.cuunan.repository.PostCommentRepository;
import vn.edu.cuunan.repository.PostUpvoteRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CommunityFeedService {

    private static final Logger log = LoggerFactory.getLogger(CommunityFeedService.class);

    // CDN Cloudinary URLs cho các ảnh hiện trường thiên tai trên Bảng Tin Cộng Đồng
    public static final String CLOUDINARY_FEED_LANDSLIDE = "https://res.cloudinary.com/odwf5uoi/image/upload/v1793269064/cuunan247/community_feed/feed_landslide_tra_mai.jpg";
    public static final String CLOUDINARY_FEED_FLOOD = "https://res.cloudinary.com/odwf5uoi/image/upload/v1793269093/cuunan247/community_feed/feed_flood_hoi_an.jpg";
    public static final String CLOUDINARY_FEED_BOAT = "https://res.cloudinary.com/odwf5uoi/image/upload/v1793269096/cuunan247/community_feed/feed_flood_rescue_boat.jpg";
    public static final String CLOUDINARY_FEED_STORM = "https://res.cloudinary.com/odwf5uoi/image/upload/v1793269099/cuunan247/community_feed/feed_storm_cay_do.jpg";
    public static final String CLOUDINARY_FEED_RELIEF = "https://res.cloudinary.com/odwf5uoi/image/upload/v1793269102/cuunan247/community_feed/feed_relief_supplies.jpg";
    public static final String CLOUDINARY_FEED_ROAD = "https://res.cloudinary.com/odwf5uoi/image/upload/v1793269104/cuunan247/community_feed/feed_clearing_road.jpg";

    private final CommunityPostRepository postRepository;
    private final EvacuationShelterRepository shelterRepository;
    private final PostUpvoteRepository upvoteRepository;
    private final PostCommentRepository commentRepository;
    private final CloudinaryService cloudinaryService;
    private final SosService sosService;

    public CommunityFeedService(CommunityPostRepository postRepository,
                                EvacuationShelterRepository shelterRepository,
                                PostUpvoteRepository upvoteRepository,
                                PostCommentRepository commentRepository,
                                CloudinaryService cloudinaryService,
                                SosService sosService) {
        this.postRepository = postRepository;
        this.shelterRepository = shelterRepository;
        this.upvoteRepository = upvoteRepository;
        this.commentRepository = commentRepository;
        this.cloudinaryService = cloudinaryService;
        this.sosService = sosService;
    }

    @PostConstruct
    public void initSampleData() {
        if (shelterRepository.count() == 0) {
            log.info("Khởi tạo danh sách điểm sơ tán mẫu...");
            shelterRepository.save(new EvacuationShelter(
                    null,
                    "THCS Trưng Vương",
                    "Xã Trà Mai, Nam Trà My, Quảng Nam",
                    15.2841,
                    108.0821,
                    300,
                    210,
                    "0235-3881234",
                    "OPEN"
            ));
            shelterRepository.save(new EvacuationShelter(
                    null,
                    "Nhà văn hóa phường Cẩm Phô",
                    "54 Nguyễn Thị Minh Khai, Phường Cẩm Phô, TP Hội An",
                    15.8782,
                    108.3243,
                    250,
                    110,
                    "0235-3861555",
                    "OPEN"
            ));
        }

        if (postRepository.count() == 0) {
            log.info("Khởi tạo bài đăng cảnh báo cộng đồng mẫu với ảnh hiện trường từ Cloudinary CDN...");
            CommunityPost post1 = new CommunityPost(
                    null,
                    null,
                    "Trần Thị Thu",
                    HazardType.LANDSLIDE,
                    "Vết nứt lớn xuất hiện trên sườn đồi phía sau trường tiểu học, đất bùn đã tràn ra đường đèo. Lực lượng cứu nạn đang phong tỏa khu vực nguy hiểm, người dân tuyệt đối không di chuyển qua cung đèo này.",
                    "Xã Trà Mai, Nam Trà My, Quảng Nam",
                    15.2841,
                    108.0821,
                    CLOUDINARY_FEED_LANDSLIDE,
                    VerificationStatus.VERIFIED,
                    24,
                    3,
                    null
            );

            CommunityPost post2 = new CommunityPost(
                    null,
                    null,
                    "Lê Minh Hoàng",
                    HazardType.FLOOD,
                    "Nước sông Hoài dâng nhanh vượt mức báo động 3, đã ngập sâu tầng 1 dãy nhà ven sông. Có 3 người già chưa di chuyển được, cần xuồng hỗ trợ gấp!",
                    "Phường Cẩm Phô, TP Hội An, Quảng Nam",
                    15.8782,
                    108.3243,
                    CLOUDINARY_FEED_FLOOD,
                    VerificationStatus.PENDING,
                    18,
                    2,
                    null
            );

            CommunityPost post3 = new CommunityPost(
                    null,
                    null,
                    "Nguyễn Văn Hùng",
                    HazardType.TREE_FALL,
                    "Gió giật mạnh làm đổ cây xà cừ cổ thụ chắn ngang đường Hùng Vương, đè đứt đường dây điện hạ thế. Đã có tia lửa điện lóe lên, người dân chú ý không đi qua!",
                    "Đường Hùng Vương, TP Tam Kỳ, Quảng Nam",
                    15.5684,
                    108.4795,
                    CLOUDINARY_FEED_STORM,
                    VerificationStatus.VERIFIED,
                    35,
                    2,
                    null
            );

            CommunityPost post4 = new CommunityPost(
                    null,
                    null,
                    "Đội Cứu Hộ Tình Nguyện Miền Trung",
                    HazardType.FLOOD,
                    "Đội đã điều động 2 xuồng máy cao su tiếp cận khu vực ngập sâu tại thôn An Lạc, đã đưa được 5 hộ dân và người già đến nhà văn hóa an toàn.",
                    "Thôn An Lạc, Xã Duy Vinh, Duy Xuyên, Quảng Nam",
                    15.8450,
                    108.3050,
                    CLOUDINARY_FEED_BOAT,
                    VerificationStatus.VERIFIED,
                    52,
                    3,
                    null
            );

            CommunityPost post5 = new CommunityPost(
                    null,
                    null,
                    "Phan Thanh Sơn",
                    HazardType.ISOLATED,
                    "Cầu tràn bị ngập 1.5m chia cắt hoàn toàn 60 hộ dân xóm Trong. Đội cứu trợ đã chuyển xong đợt 1 gồm 100 thùng mì tôm, 50 bình nước sạch và thuốc men thiết yếu.",
                    "Xã Quế Long, Huyện Quế Sơn, Quảng Nam",
                    15.6512,
                    108.1834,
                    CLOUDINARY_FEED_RELIEF,
                    VerificationStatus.VERIFIED,
                    41,
                    2,
                    null
            );

            CommunityPost post6 = new CommunityPost(
                    null,
                    null,
                    "Ban Chỉ Huy PCTT & TKCN Địa Phương",
                    HazardType.LANDSLIDE,
                    "Máy xúc và đội cơ giới đang khẩn trương san ủi khối đất đá sạt lở Đèo Le, dự kiến thông tuyến tạm thời trong chiều nay cho xe cứu hộ qua lại.",
                    "Đèo Le, Nông Sơn, Quảng Nam",
                    15.6320,
                    108.0670,
                    CLOUDINARY_FEED_ROAD,
                    VerificationStatus.VERIFIED,
                    29,
                    2,
                    null
            );

            postRepository.save(post1);
            postRepository.save(post2);
            postRepository.save(post3);
            postRepository.save(post4);
            postRepository.save(post5);
            postRepository.save(post6);
        } else {
            // Cập nhật bổ sung ảnh Cloudinary cho các bản tin hiện tại nếu chưa có ảnh
            List<CommunityPost> existing = postRepository.findAll();
            for (CommunityPost post : existing) {
                if (post.getImageUrl() == null || post.getImageUrl().isBlank()) {
                    if (post.getHazardType() == HazardType.LANDSLIDE) {
                        post.setImageUrl(CLOUDINARY_FEED_LANDSLIDE);
                        postRepository.save(post);
                    } else if (post.getHazardType() == HazardType.FLOOD) {
                        post.setImageUrl(CLOUDINARY_FEED_FLOOD);
                        postRepository.save(post);
                    } else if (post.getHazardType() == HazardType.TREE_FALL || post.getHazardType() == HazardType.STORM) {
                        post.setImageUrl(CLOUDINARY_FEED_STORM);
                        postRepository.save(post);
                    } else if (post.getHazardType() == HazardType.ISOLATED) {
                        post.setImageUrl(CLOUDINARY_FEED_RELIEF);
                        postRepository.save(post);
                    }
                }
            }

            boolean hasTreeFall = existing.stream().anyMatch(p -> p.getHazardType() == HazardType.TREE_FALL);
            if (!hasTreeFall) {
                postRepository.save(new CommunityPost(
                        null, null, "Nguyễn Văn Hùng", HazardType.TREE_FALL,
                        "Gió giật mạnh làm đổ cây xà cừ cổ thụ chắn ngang đường Hùng Vương, đè đứt đường dây điện hạ thế. Đã có tia lửa điện lóe lên, người dân chú ý không đi qua!",
                        "Đường Hùng Vương, TP Tam Kỳ, Quảng Nam",
                        15.5684, 108.4795,
                        CLOUDINARY_FEED_STORM,
                        VerificationStatus.VERIFIED, 35, 2, null
                ));
            }

            boolean hasBoatRescue = existing.stream().anyMatch(p -> p.getContent() != null && p.getContent().contains("xuồng máy cao su"));
            if (!hasBoatRescue) {
                postRepository.save(new CommunityPost(
                        null, null, "Đội Cứu Hộ Tình Nguyện Miền Trung", HazardType.FLOOD,
                        "Đội đã điều động 2 xuồng máy cao su tiếp cận khu vực ngập sâu tại thôn An Lạc, đã đưa được 5 hộ dân và người già đến nhà văn hóa an toàn.",
                        "Thôn An Lạc, Xã Duy Vinh, Duy Xuyên, Quảng Nam",
                        15.8450, 108.3050,
                        CLOUDINARY_FEED_BOAT,
                        VerificationStatus.VERIFIED, 52, 3, null
                ));
            }

            boolean hasRelief = existing.stream().anyMatch(p -> p.getHazardType() == HazardType.ISOLATED);
            if (!hasRelief) {
                postRepository.save(new CommunityPost(
                        null, null, "Phan Thanh Sơn", HazardType.ISOLATED,
                        "Cầu tràn bị ngập 1.5m chia cắt hoàn toàn 60 hộ dân xóm Trong. Đội cứu trợ đã chuyển xong đợt 1 gồm 100 thùng mì tôm, 50 bình nước sạch và thuốc men thiết yếu.",
                        "Xã Quế Long, Huyện Quế Sơn, Quảng Nam",
                        15.6512, 108.1834,
                        CLOUDINARY_FEED_RELIEF,
                        VerificationStatus.VERIFIED, 41, 2, null
                ));
            }
        }

        // Khởi tạo các bình luận mẫu cho bài đăng nếu chưa có
        initSampleComments();

        // Khởi tạo phản ứng icon mẫu nếu chưa có
        initSampleReactions();
    }

    public void initSampleComments() {
        if (commentRepository.count() == 0) {
            log.info("Khởi tạo danh sách bình luận mẫu cho các bài đăng cộng đồng...");
            List<CommunityPost> posts = postRepository.findAll();
            if (posts.isEmpty()) return;

            Map<Integer, List<String[]>> sampleCommentsData = new HashMap<>();

            // Post 1 (Sạt lở Trà Mai)
            sampleCommentsData.put(0, List.of(
                    new String[]{"Nguyễn Văn Hùng", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=120&q=80", "Bà con xóm 2 chú ý tuyệt đối không đi đường tắt qua khe suối nhé, đất đá trên đồi vẫn đang có dấu hiệu trượt tiếp!"},
                    new String[]{"Trần Đình Điều Phối", "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&w=120&q=80", "Đội dân quân xã và lực lượng công an đã chốt chặn hai đầu ngõ rồi ạ. Đề nghị bà con bình tĩnh chờ lệnh sơ tán."},
                    new String[]{"Lê Thị Mai", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=120&q=80", "Cầu mong không có ai bị thương, nhìn vết nứt mà thót cả tim! Mọi người cố gắng giữ an toàn nhé."}
            ));

            // Post 2 (Ngập sâu Hội An)
            sampleCommentsData.put(1, List.of(
                    new String[]{"Võ Thị Mỹ Linh", "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=120&q=80", "Có gia đình bà Bảy ở số 28 Nguyễn Thị Minh Khai đang bị kẹt trên gác lửng, nước lên sát sàn rồi, đội ca nô ghé đón với ạ!"},
                    new String[]{"Đội Cứu Hộ Đà Nẵng", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=120&q=80", "Đội 02 đã tiếp nhận thông tin nhà bà Bảy, xuồng máy đang rẽ vào hẻm tiếp cận ngay đây!"}
            ));

            // Post 3 (Cây xà cừ đứt dây điện)
            sampleCommentsData.put(2, List.of(
                    new String[]{"Phạm Văn Bình", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=120&q=80", "Điện lực Tam Kỳ đã cắt điện trạm biến áp số 4 từ 15 phút trước rồi, bà con an tâm nhưng vẫn tránh xa dây chùng nhé."},
                    new String[]{"Nguyễn Thị Đào", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=120&q=80", "Cảm ơn các bác thợ điện đã xử lý kịp thời, dông lốc kinh khủng quá!"}
            ));

            // Post 4 (Xuồng máy tiếp cận thôn An Lạc)
            sampleCommentsData.put(3, List.of(
                    new String[]{"Trần Anh Tuấn", "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=120&q=80", "Tuyệt vời quá các chiến sĩ ơi! Người nhà mình ở An Lạc vừa gọi điện báo đã được lên xuồng an toàn rồi."},
                    new String[]{"Lê Văn Thành", "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&w=120&q=80", "Ấm lòng mùa bão lũ. Chúc toàn đội nhiều sức khỏe và an toàn trên từng chuyến xuồng!"},
                    new String[]{"Bùi Thu Trang", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=120&q=80", "Biết ơn các anh cứu hộ vô cùng, bà con nhớ mặc áo phao cẩn thận."}
            ));

            // Post 5 (Cầu tràn chia cắt)
            sampleCommentsData.put(4, List.of(
                    new String[]{"Đặng Hữu Phúc", "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=120&q=80", "Bên mình đang tập kết thêm 200 suất bánh chưng và nước suối tại UBND xã Quế Long, ai cần tiếp tế thêm liên hệ nhé."},
                    new String[]{"Hoàng Thị Tuyết", "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=120&q=80", "Bà con khu xóm Trong đang rất thiếu nước sạch, may quá có đoàn về hỗ trợ kịp thời!"}
            ));

            // Post 6 (Đèo Le sạt lở)
            sampleCommentsData.put(5, List.of(
                    new String[]{"Hoàng Văn Nam", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=120&q=80", "Đoạn Đèo Le đá rơi nhiều, các xe cứu trợ không có dẫn đường thì tạm thời dừng chờ ở chân đèo nhé bà con."},
                    new String[]{"Nguyễn Quang Huy", "https://images.unsplash.com/photo-1501196354995-cbb51c65aaea?auto=format&fit=crop&w=120&q=80", "Lực lượng chức năng đang làm việc rất khẩn trương, hy vọng đầu giờ chiều sẽ thông được một làn đường."}
            ));

            for (int i = 0; i < posts.size(); i++) {
                CommunityPost post = posts.get(i);
                List<String[]> comments = sampleCommentsData.get(i % sampleCommentsData.size());
                if (comments != null) {
                    int cIndex = 0;
                    for (String[] c : comments) {
                        LocalDateTime time = LocalDateTime.now().minusHours(2).plusMinutes(cIndex * 25);
                        PostComment pc = new PostComment(post.getId(), null, c[0], c[1], c[2], time);
                        commentRepository.save(pc);
                        cIndex++;
                    }
                    post.setCommentCount(comments.size());
                    postRepository.save(post);
                }
            }
        }
    }

    public void initSampleReactions() {
        if (upvoteRepository.count() == 0) {
            log.info("Khởi tạo danh sách phản ứng icon cảm xúc mẫu cho các bài đăng...");
            List<CommunityPost> posts = postRepository.findAll();
            String[] reactions = {"LIKE", "CARE", "PRAY", "WARNING", "SAD"};
            long fakeUserId = 100L;

            for (CommunityPost post : posts) {
                // Tạo một vài lượt react mẫu phân bố các icon
                int sampleCount = 6 + (int)(post.getId() % 5);
                for (int j = 0; j < sampleCount; j++) {
                    String rType = reactions[j % reactions.length];
                    upvoteRepository.save(new PostUpvote(post.getId(), fakeUserId++, rType));
                }
                post.setUpvoteCount((int) upvoteRepository.countByPostId(post.getId()));
                postRepository.save(post);
            }
        }
    }

    public List<CommunityPost> getRecentPosts() {
        if (commentRepository.count() == 0 && postRepository.count() > 0) {
            initSampleComments();
        }
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<EvacuationShelter> getAllShelters() {
        return shelterRepository.findByStatusNotOrderByNameAsc("CLOSED");
    }

    public List<PostComment> getCommentsByPostId(Long postId) {
        if (commentRepository.count() == 0 && postRepository.count() > 0) {
            initSampleComments();
        }
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    public Map<Long, List<PostComment>> getAllCommentsGroupedByPostId() {
        if (commentRepository.count() == 0 && postRepository.count() > 0) {
            initSampleComments();
        }
        List<PostComment> all = commentRepository.findAll();
        Map<Long, List<PostComment>> map = new LinkedHashMap<>();
        for (PostComment c : all) {
            map.computeIfAbsent(c.getPostId(), k -> new ArrayList<>()).add(c);
        }
        return map;
    }

    @Transactional
    public PostComment addComment(Long postId, User user, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung bình luận không được để trống!");
        }
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết #" + postId));

        String authorName = (user != null && user.getFullName() != null) ? user.getFullName() : "Người dùng ẩn danh";
        String authorAvatar = (user != null && user.getAvatarUrl() != null) ? user.getAvatarUrl() : null;
        Long userId = (user != null) ? user.getId() : null;

        PostComment comment = new PostComment(postId, userId, authorName, authorAvatar, content.trim(), LocalDateTime.now());
        PostComment saved = commentRepository.save(comment);

        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
        return saved;
    }

    @Transactional
    public CommunityPost createPost(User author, String hazardTypeName, String content,
                                    String locationName, Double latitude, Double longitude,
                                    MultipartFile imageFile) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung cảnh báo không được để trống!");
        }

        HazardType hazardType = HazardType.OTHER;
        if (hazardTypeName != null && !hazardTypeName.isBlank()) {
            try {
                hazardType = HazardType.valueOf(hazardTypeName.trim().toUpperCase());
            } catch (Exception ignored) {
                for (HazardType h : HazardType.values()) {
                    if (h.getShortName().equalsIgnoreCase(hazardTypeName.trim())) {
                        hazardType = h;
                        break;
                    }
                }
            }
        }

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = cloudinaryService.uploadImage(imageFile, "community_feed");
        }

        String authorName = (author != null && author.getFullName() != null) ? author.getFullName() : "Người dân địa phương";

        CommunityPost post = new CommunityPost();
        post.setAuthor(author);
        post.setAuthorName(authorName);
        post.setHazardType(hazardType);
        post.setContent(content.trim());
        post.setLocationName((locationName != null && !locationName.isBlank()) ? locationName.trim() : "Vị trí địa phương");
        post.setLatitude(latitude);
        post.setLongitude(longitude);
        post.setImageUrl(imageUrl);
        post.setVerificationStatus(VerificationStatus.PENDING);
        post.setUpvoteCount(0);
        post.setCommentCount(0);

        return postRepository.save(post);
    }

    @Transactional
    public Map<String, Object> toggleUpvote(Long postId, User user) {
        return reactToPost(postId, user, "LIKE");
    }

    @Transactional
    public Map<String, Object> reactToPost(Long postId, User user, String reactionType) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết với ID: " + postId));

        Long userId = (user != null) ? user.getId() : 0L;
        String normalizedType = (reactionType != null && !reactionType.isBlank())
                ? reactionType.trim().toUpperCase() : "LIKE";

        Optional<PostUpvote> existingOpt = upvoteRepository.findByPostIdAndUserId(postId, userId);
        boolean isRemoved = false;
        String activeReaction = normalizedType;

        if (existingOpt.isPresent()) {
            PostUpvote existing = existingOpt.get();
            if (normalizedType.equalsIgnoreCase(existing.getReactionType())) {
                // Nhấn lại đúng icon đó -> Gỡ bỏ reaction (Toggle off)
                upvoteRepository.delete(existing);
                post.setUpvoteCount(Math.max(0, post.getUpvoteCount() - 1));
                isRemoved = true;
                activeReaction = null;
            } else {
                // Đổi sang reaction khác
                existing.setReactionType(normalizedType);
                existing.setCreatedAt(LocalDateTime.now());
                upvoteRepository.save(existing);
            }
        } else {
            // Thả reaction mới
            PostUpvote newVote = new PostUpvote(postId, userId, normalizedType);
            upvoteRepository.save(newVote);
            post.setUpvoteCount(post.getUpvoteCount() + 1);
        }

        postRepository.save(post);

        Map<String, Long> breakdown = getReactionBreakdown(postId);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("postId", postId);
        res.put("upvoteCount", post.getUpvoteCount());
        res.put("activeReaction", activeReaction);
        res.put("isRemoved", isRemoved);
        res.put("breakdown", breakdown);
        return res;
    }

    public Map<String, Long> getReactionBreakdown(Long postId) {
        List<PostUpvote> votes = upvoteRepository.findByPostId(postId);
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("LIKE", 0L);
        counts.put("CARE", 0L);
        counts.put("PRAY", 0L);
        counts.put("WARNING", 0L);
        counts.put("SAD", 0L);
        for (PostUpvote v : votes) {
            String r = v.getReactionType() != null ? v.getReactionType().toUpperCase() : "LIKE";
            counts.put(r, counts.getOrDefault(r, 0L) + 1L);
        }
        return counts;
    }

    public Map<Long, String> getUserReactionsMap(Long userId) {
        Map<Long, String> map = new HashMap<>();
        if (userId == null) return map;
        List<PostUpvote> votes = upvoteRepository.findByUserId(userId);
        for (PostUpvote v : votes) {
            map.put(v.getPostId(), v.getReactionType() != null ? v.getReactionType() : "LIKE");
        }
        return map;
    }

    public Map<Long, Map<String, Long>> getAllPostsReactionBreakdown() {
        List<PostUpvote> all = upvoteRepository.findAll();
        Map<Long, Map<String, Long>> map = new HashMap<>();
        for (PostUpvote v : all) {
            Map<String, Long> counts = map.computeIfAbsent(v.getPostId(), k -> {
                Map<String, Long> c = new LinkedHashMap<>();
                c.put("LIKE", 0L);
                c.put("CARE", 0L);
                c.put("PRAY", 0L);
                c.put("WARNING", 0L);
                c.put("SAD", 0L);
                return c;
            });
            String r = v.getReactionType() != null ? v.getReactionType().toUpperCase() : "LIKE";
            counts.put(r, counts.getOrDefault(r, 0L) + 1L);
        }
        return map;
    }

    @Transactional
    public Map<String, Object> convertToSos(Long postId, User currentUser) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết với ID: " + postId));

        if (post.getConvertedSosRequest() != null) {
            Map<String, Object> alreadyConverted = new HashMap<>();
            alreadyConverted.put("success", true);
            alreadyConverted.put("alreadyConverted", true);
            alreadyConverted.put("sosId", post.getConvertedSosRequest().getId());
            alreadyConverted.put("message", "Bài viết này đã được chuyển thành ca cứu hộ!");
            return alreadyConverted;
        }

        SosRequestDto dto = new SosRequestDto();
        dto.setContactName(post.getAuthorName() != null ? post.getAuthorName() : "Người phản ánh từ Bảng tin");
        dto.setContactPhone((currentUser != null && currentUser.getPhone() != null) ? currentUser.getPhone() : "0900000112");
        dto.setAddressDetail(post.getLocationName() != null ? post.getLocationName() : "Khu vực phản ánh hiểm họa");
        dto.setLatitude(post.getLatitude() != null ? post.getLatitude() : 15.8782);
        dto.setLongitude(post.getLongitude() != null ? post.getLongitude() : 108.3243);
        dto.setUrgencyLevel("CRITICAL");
        dto.setPeopleCount(1);
        dto.setDescription("[CHUYỂN TỪ BẢNG TIN CỘNG ĐỒNG #" + post.getId() + "] " + post.getContent());
        dto.setImageUrl(post.getImageUrl());

        Long senderId = (currentUser != null) ? currentUser.getId() : null;
        SosRequest savedSos = sosService.createSosRequest(dto, senderId);

        post.setConvertedSosRequest(savedSos);
        post.setVerificationStatus(VerificationStatus.VERIFIED);
        postRepository.save(post);

        log.info(">>> Đã chuyển bài đăng #{} thành ca cứu nạn khẩn cấp SOS-{}", postId, savedSos.getId());

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("sosId", savedSos.getId());
        res.put("message", "Đã chuyển thành công thành đơn cứu hộ SOS-" + savedSos.getId() + "!");
        return res;
    }
}
