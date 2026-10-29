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
import vn.edu.cuunan.repository.PostUpvoteRepository;

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
    private final CloudinaryService cloudinaryService;
    private final SosService sosService;

    public CommunityFeedService(CommunityPostRepository postRepository,
                                EvacuationShelterRepository shelterRepository,
                                PostUpvoteRepository upvoteRepository,
                                CloudinaryService cloudinaryService,
                                SosService sosService) {
        this.postRepository = postRepository;
        this.shelterRepository = shelterRepository;
        this.upvoteRepository = upvoteRepository;
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
                    8,
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
                    12,
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
                    14,
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
                    9,
                    null
            );

            CommunityPost post6 = new CommunityPost(
                    null,
                    null,
                    "Ban Chỉ Huy PCTT & TKCN Địa Phương",
                    HazardType.LANDSLIDE,
                    "Máy xúc và đội cơ giới đang khẩn trương san ủi khối đất đá sạt lở đèo Le, dự kiến thông tuyến tạm thời trong chiều nay cho xe cứu hộ qua lại.",
                    "Đèo Le, Nông Sơn, Quảng Nam",
                    15.6320,
                    108.0670,
                    CLOUDINARY_FEED_ROAD,
                    VerificationStatus.VERIFIED,
                    29,
                    5,
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

            // Bổ sung các bản tin với các loại thiên tai đa dạng nếu chưa có
            boolean hasTreeFall = existing.stream().anyMatch(p -> p.getHazardType() == HazardType.TREE_FALL);
            if (!hasTreeFall) {
                postRepository.save(new CommunityPost(
                        null, null, "Nguyễn Văn Hùng", HazardType.TREE_FALL,
                        "Gió giật mạnh làm đổ cây xà cừ cổ thụ chắn ngang đường Hùng Vương, đè đứt đường dây điện hạ thế. Đã có tia lửa điện lóe lên, người dân chú ý không đi qua!",
                        "Đường Hùng Vương, TP Tam Kỳ, Quảng Nam",
                        15.5684, 108.4795,
                        CLOUDINARY_FEED_STORM,
                        VerificationStatus.VERIFIED, 35, 12, null
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
                        VerificationStatus.VERIFIED, 52, 14, null
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
                        VerificationStatus.VERIFIED, 41, 9, null
                ));
            }
        }
    }

    public List<CommunityPost> getRecentPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<EvacuationShelter> getAllShelters() {
        return shelterRepository.findByStatusNotOrderByNameAsc("CLOSED");
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
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết với ID: " + postId));

        Long userId = (user != null) ? user.getId() : 0L;

        boolean alreadyVoted = upvoteRepository.existsByPostIdAndUserId(postId, userId);
        boolean isVotedNow;

        if (alreadyVoted) {
            upvoteRepository.deleteByPostIdAndUserId(postId, userId);
            post.setUpvoteCount(Math.max(0, post.getUpvoteCount() - 1));
            isVotedNow = false;
        } else {
            upvoteRepository.save(new PostUpvote(postId, userId));
            post.setUpvoteCount(post.getUpvoteCount() + 1);
            isVotedNow = true;
        }

        postRepository.save(post);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("postId", postId);
        res.put("upvoteCount", post.getUpvoteCount());
        res.put("isVoted", isVotedNow);
        return res;
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
