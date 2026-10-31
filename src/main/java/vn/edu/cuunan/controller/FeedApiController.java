package vn.edu.cuunan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.cuunan.entity.CommunityPost;
import vn.edu.cuunan.entity.EvacuationShelter;
import vn.edu.cuunan.entity.PostComment;
import org.springframework.security.access.AccessDeniedException;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.entity.VerificationStatus;
import vn.edu.cuunan.security.CustomUserDetails;
import vn.edu.cuunan.service.CommunityFeedService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feed")
public class FeedApiController {

    private final CommunityFeedService feedService;

    public FeedApiController(CommunityFeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/posts")
    public ResponseEntity<List<CommunityPost>> getPosts() {
        return ResponseEntity.ok(feedService.getRecentPosts());
    }

    @GetMapping("/shelters")
    public ResponseEntity<List<EvacuationShelter>> getShelters() {
        return ResponseEntity.ok(feedService.getAllShelters());
    }

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestParam("content") String content,
                                        @RequestParam(value = "hazardType", required = false) String hazardType,
                                        @RequestParam(value = "locationName", required = false) String locationName,
                                        @RequestParam(value = "latitude", required = false) Double latitude,
                                        @RequestParam(value = "longitude", required = false) Double longitude,
                                        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Vui lòng đăng nhập trước khi đăng tin cảnh báo!");
            return ResponseEntity.status(401).body(err);
        }

        try {
            User author = userDetails.getUser();
            CommunityPost post = feedService.createPost(author, hazardType, content, locationName, latitude, longitude, imageFile);
            
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("post", post);
            res.put("message", "Đăng bài cảnh báo thành công!");
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @PostMapping("/posts/{id}/upvote")
    public ResponseEntity<?> upvotePost(@PathVariable("id") Long id,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Vui lòng đăng nhập để tương tác bài viết!");
            return ResponseEntity.status(401).body(err);
        }

        try {
            Map<String, Object> res = feedService.toggleUpvote(id, userDetails.getUser());
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @PostMapping("/posts/{id}/react")
    public ResponseEntity<?> reactPost(@PathVariable("id") Long id,
                                       @RequestParam(value = "type", defaultValue = "LIKE") String reactionType,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Vui lòng đăng nhập để thả cảm xúc bài viết!");
            return ResponseEntity.status(401).body(err);
        }

        try {
            Map<String, Object> res = feedService.reactToPost(id, userDetails.getUser(), reactionType);
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @GetMapping("/posts/{id}/comments")
    public ResponseEntity<?> getComments(@PathVariable("id") Long id) {
        try {
            List<PostComment> comments = feedService.getCommentsByPostId(id);
            return ResponseEntity.ok(comments);
        } catch (Exception ex) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @PostMapping("/posts/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable("id") Long id,
                                        @RequestParam("content") String content,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Vui lòng đăng nhập để gửi bình luận!");
            return ResponseEntity.status(401).body(err);
        }

        try {
            PostComment comment = feedService.addComment(id, userDetails.getUser(), content);
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("comment", comment);
            res.put("message", "Đã gửi bình luận thành công!");
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @PostMapping("/posts/{id}/convert-sos")
    public ResponseEntity<?> convertToSos(@PathVariable("id") Long id,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Vui lòng đăng nhập để thực hiện chức năng này!");
            return ResponseEntity.status(401).body(err);
        }

        try {
            Map<String, Object> res = feedService.convertToSos(id, userDetails.getUser());
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<?> getPostById(@PathVariable("id") Long id) {
        try {
            CommunityPost post = feedService.getPostById(id);
            Map<String, Object> data = new HashMap<>();
            data.put("id", post.getId());
            data.put("authorName", post.getAuthorName());
            data.put("authorId", post.getAuthor() != null ? post.getAuthor().getId() : null);
            data.put("hazardType", post.getHazardType() != null ? post.getHazardType().name() : "OTHER");
            data.put("content", post.getContent());
            data.put("locationName", post.getLocationName());
            data.put("latitude", post.getLatitude());
            data.put("longitude", post.getLongitude());
            data.put("imageUrl", post.getImageUrl());
            data.put("verificationStatus", post.getVerificationStatus() != null ? post.getVerificationStatus().name() : "PENDING");
            data.put("upvoteCount", post.getUpvoteCount());
            data.put("commentCount", post.getCommentCount());
            return ResponseEntity.ok(data);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @PostMapping("/posts/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> verifyPost(@PathVariable("id") Long id,
                                        @RequestParam("status") String statusStr,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Vui lòng đăng nhập!"));
        }

        try {
            VerificationStatus status = VerificationStatus.valueOf(statusStr.trim().toUpperCase());
            CommunityPost post = feedService.verifyPost(id, status, userDetails.getUser());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "postId", id,
                    "status", post.getVerificationStatus().name(),
                    "label", post.getVerificationStatus().getLabel(),
                    "color", post.getVerificationStatus().getColor(),
                    "message", "Đã cập nhật trạng thái phê duyệt bài viết #" + id + " thành '" + post.getVerificationStatus().getLabel() + "'!"
            ));
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Lỗi phê duyệt: " + ex.getMessage()));
        }
    }

    @PostMapping("/posts/{id}/edit")
    public ResponseEntity<?> editPost(@PathVariable("id") Long id,
                                      @RequestParam("content") String content,
                                      @RequestParam(value = "hazardType", required = false) String hazardType,
                                      @RequestParam(value = "locationName", required = false) String locationName,
                                      @RequestParam(value = "latitude", required = false) Double latitude,
                                      @RequestParam(value = "longitude", required = false) Double longitude,
                                      @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                      @RequestParam(value = "removeImage", defaultValue = "false") boolean removeImage,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Vui lòng đăng nhập!"));
        }

        try {
            CommunityPost updated = feedService.updatePost(id, userDetails.getUser(), hazardType, content,
                    locationName, latitude, longitude, imageFile, removeImage);
            Map<String, Object> postMap = new HashMap<>();
            postMap.put("id", updated.getId());
            postMap.put("content", updated.getContent());
            postMap.put("hazardType", updated.getHazardType() != null ? updated.getHazardType().name() : "OTHER");
            postMap.put("locationName", updated.getLocationName());
            postMap.put("imageUrl", updated.getImageUrl());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "post", postMap,
                    "message", "Cập nhật bài viết thành công!"
            ));
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Lỗi cập nhật bài viết: " + ex.getMessage()));
        }
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable("id") Long id,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Vui lòng đăng nhập!"));
        }

        try {
            feedService.deletePost(id, userDetails.getUser());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "postId", id,
                    "message", "Đã xóa bài viết thành công!"
            ));
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Lỗi xóa bài viết: " + ex.getMessage()));
        }
    }
}
