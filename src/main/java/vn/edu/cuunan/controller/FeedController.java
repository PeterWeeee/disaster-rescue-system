package vn.edu.cuunan.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.cuunan.entity.HazardType;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.security.CustomUserDetails;
import vn.edu.cuunan.service.CommunityFeedService;
import vn.edu.cuunan.repository.DisasterAlertRepository;
import vn.edu.cuunan.service.SosService;

import java.util.List;

@Controller
@RequestMapping("/feed")
public class FeedController {

    private final CommunityFeedService feedService;
    private final SosService sosService;
    private final DisasterAlertRepository disasterAlertRepository;

    public FeedController(CommunityFeedService feedService,
                          SosService sosService,
                          DisasterAlertRepository disasterAlertRepository) {
        this.feedService = feedService;
        this.sosService = sosService;
        this.disasterAlertRepository = disasterAlertRepository;
    }

    @GetMapping
    public String showFeed(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null || userDetails.getUser() == null) {
            return "redirect:/auth/login?redirect=/feed";
        }

        User currentUser = userDetails.getUser();
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("posts", feedService.getRecentPosts());
        model.addAttribute("shelters", feedService.getAllShelters());
        model.addAttribute("hazardTypes", HazardType.values());
        model.addAttribute("recentAlerts", disasterAlertRepository.findTop5ByOrderByCreatedAtDesc());

        // Dữ liệu bình luận và icon cảm xúc
        model.addAttribute("commentsMap", feedService.getAllCommentsGroupedByPostId());
        model.addAttribute("userReactions", feedService.getUserReactionsMap(currentUser.getId()));
        model.addAttribute("postReactions", feedService.getAllPostsReactionBreakdown());

        // Lịch sử các yêu cầu cứu hộ do người dùng tạo.
        List<SosRequest> sosHistory = List.of();
        if (currentUser.getId() != null) {
            List<SosRequest> userRequests = sosService.getSosBySender(currentUser.getId());
            sosHistory = userRequests;
        }
        if (sosHistory.isEmpty() && currentUser.getPhone() != null) {
            List<SosRequest> phoneRequests = sosService.getSosByPhone(currentUser.getPhone());
            sosHistory = phoneRequests;
        }
        model.addAttribute("sosHistory", sosHistory);

        return "feed/index";
    }
}
