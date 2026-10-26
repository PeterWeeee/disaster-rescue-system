package vn.edu.cuunan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.cuunan.entity.CommunityPost;
import vn.edu.cuunan.entity.EvacuationShelter;
import vn.edu.cuunan.entity.User;
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
            err.put("message", "Vui lòng đăng nhập để xác nhận bài viết!");
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
}
