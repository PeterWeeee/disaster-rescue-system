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
import vn.edu.cuunan.service.SosService;

import java.util.List;

@Controller
@RequestMapping("/feed")
public class FeedController {

    private final CommunityFeedService feedService;
    private final SosService sosService;

    public FeedController(CommunityFeedService feedService, SosService sosService) {
        this.feedService = feedService;
        this.sosService = sosService;
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

        // Lấy đơn cứu hộ đang hoạt động của người dùng (nếu có)
        SosRequest activeSos = null;
        if (currentUser.getId() != null) {
            List<SosRequest> userRequests = sosService.getSosBySender(currentUser.getId());
            if (!userRequests.isEmpty()) {
                activeSos = userRequests.get(0);
            }
        }
        if (activeSos == null && currentUser.getPhone() != null) {
            List<SosRequest> phoneRequests = sosService.getSosByPhone(currentUser.getPhone());
            if (!phoneRequests.isEmpty()) {
                activeSos = phoneRequests.get(0);
            }
        }
        model.addAttribute("activeSos", activeSos);

        return "feed/index";
    }
}
