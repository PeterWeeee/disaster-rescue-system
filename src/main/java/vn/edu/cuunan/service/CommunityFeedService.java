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
            log.info("Khởi tạo bài đăng cảnh báo cộng đồng mẫu...");
            CommunityPost post1 = new CommunityPost(
                    null,
                    null,
                    "Trần Thị Thu",
                    HazardType.LANDSLIDE,
                    "Vết nứt lớn xuất hiện trên sườn đồi phía sau trường tiểu học, đất bùn đã tràn ra đường đèo. Mọi người tránh khu vực này.",
                    "Xã Trà Mai, Quảng Nam",
                    15.2841,
                    108.0821,
                    null,
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
                    "Nước sông dâng nhanh, đã ngập tầng 1 dãy nhà ven sông. Có 3 người già chưa di chuyển được, cần xuồng gấp.",
                    "Phường Cẩm Phô, Hội An",
                    15.8782,
                    108.3243,
                    null,
                    VerificationStatus.PENDING,
                    11,
                    8,
                    null
            );

            postRepository.save(post1);
            postRepository.save(post2);
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
