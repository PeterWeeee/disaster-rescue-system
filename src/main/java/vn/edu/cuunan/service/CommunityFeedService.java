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

    // CDN Cloudinary URLs cho các ảnh hiện trường thiên tai trên Bảng Tin Cộng Đồng (ảnh thực tế)
    public static final String CLOUDINARY_FEED_LANDSLIDE = "https://res.cloudinary.com/odwf5uoi/image/upload/v1793328584/cuunan247/community_feed/V%E1%BA%BFt_n%E1%BB%A9t_l%E1%BB%9Bn_xu%E1%BA%A5t_hi%E1%BB%87n_tr%C3%AAn_s%C6%B0%E1%BB%9Dn_%C4%91%E1%BB%93i_ph%C3%ADa_sau_tr%C6%B0%E1%BB%9Dng_ti%E1%BB%83u_h%E1%BB%8Dc_%C4%91%E1%BA%A5t_b%C3%B9n_%C4%91%C3%A3_tr%C3%A0n_ra_%C4%91%C6%B0%E1%BB%9Dng_%C4%91%C3%A8o.webp";
    public static final String CLOUDINARY_FEED_FLOOD = "https://res.cloudinary.com/odwf5uoi/image/upload/v1793328592/cuunan247/community_feed/N%C6%B0%E1%BB%9Bc_s%C3%B4ng_d%C3%A2ng_nhanh_%C4%91%C3%A3_ng%E1%BA%ADp_t%E1%BA%A7ng_1_d%C3%A3y_nh%C3%A0_ven_s%C3%B4ng.jpg";
    public static final String CLOUDINARY_FEED_BOAT = "https://res.cloudinary.com/odwf5uoi/image/upload/v1793328594/cuunan247/community_feed/Xu%E1%BB%93ng_cao_su_c%E1%BB%A9u_h%E1%BB%99_ti%E1%BA%BFp_c%E1%BA%ADn_v%C3%B9ng_ng%E1%BA%ADp_s%C3%A2u.jpg";
    public static final String CLOUDINARY_FEED_FLOOD_STREET = "https://res.cloudinary.com/odwf5uoi/image/upload/v1793328442/cuunan247/community_feed/%C4%90%C6%B0%E1%BB%9Dng_M%E1%BA%B9_Su%E1%BB%91t_%C4%91o%E1%BA%A1n_g%E1%BA%A7n_c%E1%BA%A7u_m%C6%B0%C6%A1ng_ng%E1%BA%ADp_s%C3%A2u_h%C6%A1n_1m_n%C6%B0%E1%BB%9Bc_ch%E1%BA%A3y_r%E1%BA%A5t_xi%E1%BA%BFt.jpg";
    public static final String CLOUDINARY_FEED_THACH_NHAM_TAY = "https://res.cloudinary.com/odwf5uoi/image/upload/v1793328564/cuunan247/community_feed/C%E1%BA%A7u_tr%C3%A0n_th%C3%B4n_Th%E1%BA%A1ch_Nham_T%C3%A2y_x%C3%A3_H%C3%B2a_Nh%C6%A1n_%C4%91ang_b%E1%BB%8B_ng%E1%BA%ADp_0.8m.jpg";
    public static final String CLOUDINARY_FEED_BACH_DANG = "https://res.cloudinary.com/odwf5uoi/image/upload/v1793328566/cuunan247/community_feed/%C4%90%C6%B0%E1%BB%9Dng_B%E1%BA%A1ch_%C4%90%E1%BA%B1ng_ven_s%C3%B4ng_H%C6%B0%C6%A1ng_n%C6%B0%E1%BB%9Bc_tr%C3%A0n_l%C3%AAn_b%E1%BB%9D_%C4%91i_v%C3%A0o_nh%C3%A0_d%C3%A2n_s%C3%A2u_0.5m.jpg";
    public static final String CLOUDINARY_FEED_PRENN = "https://res.cloudinary.com/odwf5uoi/image/upload/v1793328569/cuunan247/community_feed/%C4%90%C3%A8o_Prenn_%C4%91o%E1%BA%A1n_Km225_xu%E1%BA%A5t_hi%E1%BB%87n_v%E1%BA%BFt_n%E1%BB%A9t_s%C6%B0%E1%BB%9Dn_taluy_d%C6%B0%C6%A1ng_c%C3%B3_%C4%91%E1%BA%A5t_%C4%91%C3%A1_nh%E1%BB%8F_r%C6%A1i_xu%E1%BB%91ng_m%E1%BA%B7t_%C4%91%C6%B0%E1%BB%9Dng.jpg";
    public static final String CLOUDINARY_FEED_NGUYEN_DU = "https://res.cloudinary.com/odwf5uoi/image/upload/v1793328571/cuunan247/community_feed/C%C3%A2y_ph%C6%B0%E1%BB%A3ng_c%E1%BB%95_th%E1%BB%A5_b%E1%BB%8B_l%E1%BB%91c_xo%C3%A1y_qu%E1%BA%ADt_%C4%91%E1%BB%95_%C4%91%C3%A8_tr%C3%BAng_c%E1%BB%99t_%C4%91i%E1%BB%87n_h%E1%BA%A1_th%E1%BA%BF_t%E1%BA%A1i_%C4%91%C6%B0%E1%BB%9Dng_Nguy%E1%BB%85n_Du.jpg";
    public static final String CLOUDINARY_FEED_VINH_HAI = "https://res.cloudinary.com/odwf5uoi/image/upload/v1793328573/cuunan247/community_feed/Gi%C3%B3_r%C3%ADt_m%E1%BA%A1nh_t%E1%BB%ABng_c%C6%A1n_gi%E1%BA%ADt_c%E1%BA%A5p_9_t%E1%BA%A1i_ven_bi%E1%BB%83n_V%C4%A9nh_H%E1%BA%A3i.jpg";

    public static final List<String> SAMPLE_FEED_IMAGES = List.of(
            CLOUDINARY_FEED_LANDSLIDE, CLOUDINARY_FEED_FLOOD, CLOUDINARY_FEED_BOAT,
            CLOUDINARY_FEED_FLOOD_STREET, CLOUDINARY_FEED_THACH_NHAM_TAY,
            CLOUDINARY_FEED_BACH_DANG, CLOUDINARY_FEED_PRENN,
            CLOUDINARY_FEED_NGUYEN_DU, CLOUDINARY_FEED_VINH_HAI);

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
            log.info("Khởi tạo 10 bài đăng cảnh báo cộng đồng chuẩn miền Trung với ảnh thực tế Cloudinary CDN...");
            CommunityPost post1 = new CommunityPost(
                    null, null, "Trần Thị Thu", HazardType.LANDSLIDE,
                    "Vết nứt lớn xuất hiện trên sườn đồi phía sau trường tiểu học, đất bùn đã tràn ra đường đèo. Mọi người tránh khu vực này.",
                    "Xã Trà Mai, Quảng Nam", 15.2841, 108.0821,
                    CLOUDINARY_FEED_LANDSLIDE, VerificationStatus.VERIFIED, 7, 3, null
            );

            CommunityPost post2 = new CommunityPost(
                    null, null, "Lê Minh Hoàng", HazardType.FLOOD,
                    "Nước sông dâng nhanh, đã ngập tầng 1 dãy nhà ven sông. Có 3 người già chưa di chuyển được, cần xuồng gấp.",
                    "Phường Cẩm Phô, Hội An", 15.8782, 108.3243,
                    CLOUDINARY_FEED_FLOOD, VerificationStatus.PENDING, 8, 2, null
            );

            CommunityPost post3 = new CommunityPost(
                    null, null, "Nguyễn Văn An", HazardType.FLOOD,
                    "Đường Mẹ Suốt đoạn gần cầu mương ngập sâu hơn 1m, nước chảy rất xiết, bà con tuyệt đối không nên di chuyển bằng xe máy!",
                    "Đường Mẹ Suốt, P. Hòa Khánh Nam, Liên Chiểu, Đà Nẵng", 16.0590, 108.1580,
                    CLOUDINARY_FEED_FLOOD_STREET, VerificationStatus.VERIFIED, 28, 6, null
            );

            CommunityPost post4 = new CommunityPost(
                    null, null, "Trần Thị Mai", HazardType.ISOLATED,
                    "Cầu tràn thôn Thạch Nham Tây xã Hòa Nhơn đang bị ngập 0.8m, cô lập 120 hộ dân, cần xuồng tiếp tế mì và nước uống.",
                    "Thôn Thạch Nham Tây, Xã Hòa Nhơn, Hòa Vang, Đà Nẵng", 16.0120, 108.1350,
                    CLOUDINARY_FEED_THACH_NHAM_TAY, VerificationStatus.VERIFIED, 19, 4, null
            );

            CommunityPost post5 = new CommunityPost(
                    null, null, "Trần Văn Minh", HazardType.FLOOD,
                    "Đường Bạch Đằng ven sông Hương nước tràn lên bờ đi vào nhà dân sâu 0.5m, đề nghị bà con dọn đồ lên gác lửng.",
                    "Đường Bạch Đằng, Phường Phú Cát, TP. Huế", 16.4720, 107.5920,
                    CLOUDINARY_FEED_BACH_DANG, VerificationStatus.VERIFIED, 35, 12, null
            );

            CommunityPost post6 = new CommunityPost(
                    null, null, "Phạm Văn Long", HazardType.LANDSLIDE,
                    "Đèo Prenn đoạn Km225 xuất hiện vết nứt sườn taluy dương, có đất đá nhỏ rơi xuống mặt đường, các bác tài chú ý giảm tốc độ.",
                    "Đèo Prenn, Phường 3, TP. Đà Lạt, Lâm Đồng", 11.9150, 108.4450,
                    CLOUDINARY_FEED_PRENN, VerificationStatus.PENDING, 14, 2, null
            );

            CommunityPost post7 = new CommunityPost(
                    null, null, "Lê Văn Hùng", HazardType.TREE_FALL,
                    "Cây phượng cổ thụ bị lốc xoáy quật đổ đè trúng cột điện hạ thế tại đường Nguyễn Du, gây mất điện toàn khu phố.",
                    "Đường Nguyễn Du, Phường Bến Thủy, TP. Vinh, Nghệ An", 18.6650, 105.6950,
                    CLOUDINARY_FEED_NGUYEN_DU, VerificationStatus.VERIFIED, 22, 5, null
            );

            CommunityPost post8 = new CommunityPost(
                    null, null, "Lê Thị Thảo", HazardType.STORM,
                    "Gió rít mạnh từng cơn giật cấp 9 tại ven biển Vĩnh Hải, sóng cao đánh tràn qua kè công viên, người dân không nên ra bờ biển.",
                    "Bờ kè Phạm Văn Đồng, Phường Vĩnh Hải, TP. Nha Trang, Khánh Hòa", 12.2850, 109.2050,
                    CLOUDINARY_FEED_VINH_HAI, VerificationStatus.PENDING, 11, 1, null
            );

            CommunityPost post9 = new CommunityPost(
                    null, null, "Võ Văn Bình", HazardType.OTHER,
                    "Tin đồn vỡ đập thủy điện trên sông Ba là tin giả mạo, chính quyền địa phương thông báo đập vẫn vận hành xả lũ an toàn.",
                    "Huyện Krông Pa, Tỉnh Gia Lai", 13.2100, 108.6800,
                    null, VerificationStatus.REJECTED, 3, 8, null
            );

            CommunityPost post10 = new CommunityPost(
                    null, null, "Đội Cứu Hộ Tình Nguyện Miền Trung", HazardType.FLOOD,
                    "Đội đã điều động 2 xuồng máy cao su tiếp cận khu vực ngập sâu tại thôn An Lạc, đã đưa được 5 hộ dân và người già đến nhà văn hóa an toàn.",
                    "Thôn An Lạc, Xã Duy Vinh, Duy Xuyên, Quảng Nam", 15.8450, 108.3050,
                    CLOUDINARY_FEED_BOAT, VerificationStatus.VERIFIED, 52, 3, null
            );

            postRepository.save(post1);
            postRepository.save(post2);
            postRepository.save(post3);
            postRepository.save(post4);
            postRepository.save(post5);
            postRepository.save(post6);
            postRepository.save(post7);
            postRepository.save(post8);
            postRepository.save(post9);
            postRepository.save(post10);
        } else {
            // Xóa các bài đăng cũ thừa từ các bản seed trước (Đèo Le, Quế Long) nếu còn tồn tại
            List<CommunityPost> existing = postRepository.findAll();
            for (CommunityPost post : existing) {
                String loc = post.getLocationName() != null ? post.getLocationName() : "";
                if (loc.contains("Đèo Le") || loc.contains("Nông Sơn") || loc.contains("Quế Long")) {
                    try {
                        postRepository.delete(post);
                    } catch (Exception ignored) {}
                }
            }

            // Gán đúng ảnh Cloudinary thực tế khớp nội dung bài đăng
            existing = postRepository.findAll();
            for (CommunityPost post : existing) {
                String preferredImage = sampleImageFor(post);
                if (preferredImage != null && !preferredImage.equals(post.getImageUrl())) {
                    post.setImageUrl(preferredImage);
                    postRepository.save(post);
                }
            }

            boolean hasBoatRescue = existing.stream().anyMatch(p -> p.getLocationName() != null && p.getLocationName().contains("An Lạc"));
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
        }

        // Khởi tạo các bình luận mẫu cho bài đăng nếu chưa có
        initSampleComments();

        // Khởi tạo phản ứng icon mẫu nếu chưa có
        initSampleReactions();
    }

    private String sampleImageFor(CommunityPost post) {
        if (post == null) return null;
        String location = post.getLocationName() != null ? post.getLocationName().toLowerCase(Locale.ROOT) : "";
        String content = post.getContent() != null ? post.getContent().toLowerCase(Locale.ROOT) : "";

        if (location.contains("trà mai") || content.contains("trà mai") || content.contains("phía sau trường tiểu học")) return CLOUDINARY_FEED_LANDSLIDE;
        if (location.contains("cẩm phô") || content.contains("sông hoài") || content.contains("ngập sâu tầng 1")) return CLOUDINARY_FEED_FLOOD;
        if (location.contains("mẹ suốt") || content.contains("mẹ suốt")) return CLOUDINARY_FEED_FLOOD_STREET;
        if (location.contains("thạch nham") || content.contains("thạch nham")) return CLOUDINARY_FEED_THACH_NHAM_TAY;
        if (location.contains("bạch đằng") || content.contains("bạch đằng")) return CLOUDINARY_FEED_BACH_DANG;
        if (location.contains("đèo prenn") || content.contains("prenn")) return CLOUDINARY_FEED_PRENN;
        if (location.contains("nguyễn du") || content.contains("nguyễn du") || content.contains("cây phượng")) return CLOUDINARY_FEED_NGUYEN_DU;
        if (location.contains("phạm văn đồng") || location.contains("vĩnh hải") || content.contains("vĩnh hải")) return CLOUDINARY_FEED_VINH_HAI;
        if (location.contains("an lạc") || location.contains("duy vinh") || location.contains("duy xuyên") || content.contains("xuồng máy cao su") || content.contains("xuồng cao su")) return CLOUDINARY_FEED_BOAT;

        return null;
    }

    public void initSampleComments() {
        List<CommunityPost> posts = postRepository.findAll();
        if (posts.isEmpty()) return;

        Map<Integer, List<String[]>> sampleCommentsData = new HashMap<>();

        // Post 1 (Sạt lở Trà Mai / Mẹ Suốt)
        sampleCommentsData.put(0, List.of(
                new String[]{"Nguyễn Văn Hùng", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=120&q=80", "Bà con khu vực gần sườn đồi chú ý tuyệt đối không đi đường tắt qua khe suối nhé, đất bùn vẫn đang có dấu hiệu trượt tiếp!"},
                new String[]{"Trần Đình Điều Phối", "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&w=120&q=80", "Lực lượng dân quân và công an địa phương đã chốt chặn hai đầu ngõ rồi ạ. Đề nghị bà con bình tĩnh chờ lệnh sơ tán."},
                new String[]{"Lê Thị Mai", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=120&q=80", "Cầu mong không có ai bị thương, nhìn vết nứt mà thót cả tim! Mọi người cố gắng giữ an toàn nhé."}
        ));

        // Post 2 (Ngập sâu Hội An / Thạch Nham Tây)
        sampleCommentsData.put(1, List.of(
                new String[]{"Võ Thị Mỹ Linh", "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=120&q=80", "Có gia đình bà Bảy ở cụm xóm Trong đang bị kẹt trên gác lửng, nước lên sát sàn rồi, đội ca nô ghé đón với ạ!"},
                new String[]{"Đội Cứu Hộ Đà Nẵng", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=120&q=80", "Đội 02 đã tiếp nhận thông tin nhà bà Bảy, xuồng máy đang rẽ vào hẻm tiếp cận ngay đây!"}
        ));

        // Post 3 (Cây xà cừ đứt dây điện / Sông Hương)
        sampleCommentsData.put(2, List.of(
                new String[]{"Phạm Văn Bình", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=120&q=80", "Điện lực Tam Kỳ đã cắt điện trạm biến áp số 4 từ 15 phút trước rồi, bà con an tâm nhưng vẫn tránh xa dây chùng nhé."},
                new String[]{"Nguyễn Thị Đào", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=120&q=80", "Cảm ơn các bác thợ điện đã xử lý kịp thời, dông lốc kinh khủng quá!"}
        ));

        // Post 4 (Đèo Prenn sạt lở / Xuồng máy An Lạc)
        sampleCommentsData.put(3, List.of(
                new String[]{"Nguyễn Hoàng Nam", "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=120&q=80", "Lực lượng CSGT và thanh tra Lâm Đồng đã cắm biển cảnh báo hai đầu đèo Prenn rồi, anh em tài xế chú ý giảm tốc độ hoặc chuyển sang đèo Sacom!"},
                new String[]{"Trần Thị Lan", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=120&q=80", "Trời đang mưa to ở Đà Lạt, sườn đồi rất dễ sạt lở tiếp, các bác tài tuyệt đối không dừng đỗ sát chân taluy dương nhé."}
        ));

        // Post 5 (Cầu tràn Quế Long / Cây đổ Vinh)
        sampleCommentsData.put(4, List.of(
                new String[]{"Đặng Hữu Phúc", "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=120&q=80", "Bên mình đang tập kết thêm 200 suất bánh chưng và nước suối tại UBND xã Quế Long, ai cần tiếp tế thêm liên hệ nhé."},
                new String[]{"Hoàng Thị Tuyết", "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=120&q=80", "Bà con khu xóm Trong đang rất thiếu nước sạch, may quá có đoàn về hỗ trợ kịp thời!"}
        ));

        // Post 6 (Đèo Le sạt lở / Sóng biển Nha Trang)
        sampleCommentsData.put(5, List.of(
                new String[]{"Hoàng Văn Nam", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=120&q=80", "Đoạn Đèo Le đá rơi nhiều, các xe cứu trợ không có dẫn đường thì tạm thời dừng chờ ở chân đèo nhé bà con."},
                new String[]{"Nguyễn Quang Huy", "https://images.unsplash.com/photo-1501196354995-cbb51c65aaea?auto=format&fit=crop&w=120&q=80", "Lực lượng chức năng đang làm việc rất khẩn trương, hy vọng đầu giờ chiều sẽ thông được một làn đường."}
        ));

        // Post 7 (Tin đồn đập Krông Pa)
        sampleCommentsData.put(6, List.of(
                new String[]{"Bùi Thế Cường", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=120&q=80", "Mọi người theo dõi thông báo chính thống từ Ban chỉ huy PCTT địa phương nhé, tránh chia sẻ tin đồn thất thiệt gây hoang mang."},
                new String[]{"Lê Thị Hồng", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=120&q=80", "Cảm ơn chính quyền địa phương đã phát loa đính chính kịp thời cho bà con yên tâm."}
        ));

        for (int i = 0; i < posts.size(); i++) {
            CommunityPost post = posts.get(i);
            long countInDb = commentRepository.countByPostId(post.getId());
            if (countInDb == 0) {
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
            } else if (post.getCommentCount() != (int) countInDb) {
                post.setCommentCount((int) countInDb);
                postRepository.save(post);
            }
        }
    }

    public void initSampleReactions() {
        List<CommunityPost> posts = postRepository.findAll();
        String[] reactions = {"LIKE", "CARE", "PRAY", "WARNING", "SAD"};
        long fakeUserId = 100L;

        for (CommunityPost post : posts) {
            long countInDb = upvoteRepository.countByPostId(post.getId());
            if (countInDb == 0) {
                int sampleCount = 6 + (int)(post.getId() % 5);
                for (int j = 0; j < sampleCount; j++) {
                    String rType = reactions[j % reactions.length];
                    upvoteRepository.save(new PostUpvote(post.getId(), fakeUserId++, rType));
                }
                post.setUpvoteCount((int) upvoteRepository.countByPostId(post.getId()));
                postRepository.save(post);
            } else if (post.getUpvoteCount() != (int) countInDb) {
                post.setUpvoteCount((int) countInDb);
                postRepository.save(post);
            }
        }
    }

    public List<CommunityPost> getRecentPosts() {
        ensureCleanSampleComments();
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<EvacuationShelter> getAllShelters() {
        return shelterRepository.findByStatusNotOrderByNameAsc("CLOSED");
    }

    public void ensureCleanSampleComments() {
        if (postRepository.count() == 0) return;
        List<PostComment> all = commentRepository.findAll();
        boolean hasCorrupted = all.stream().anyMatch(c ->
                (c.getContent() != null && c.getContent().contains("?")) ||
                (c.getAuthorName() != null && c.getAuthorName().contains("?")));
        if (hasCorrupted || all.isEmpty()) {
            if (hasCorrupted) {
                log.warn("Phát hiện bình luận bị lỗi font (dấu hỏi ?), đang tự động làm sạch và nạp lại...");
                commentRepository.deleteAll();
            }
            initSampleComments();
        }
    }

    public List<PostComment> getCommentsByPostId(Long postId) {
        ensureCleanSampleComments();
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    public Map<Long, List<PostComment>> getAllCommentsGroupedByPostId() {
        ensureCleanSampleComments();
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
