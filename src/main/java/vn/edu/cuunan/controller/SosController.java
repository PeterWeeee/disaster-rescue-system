package vn.edu.cuunan.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.cuunan.dto.SosRequestDto;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.security.CustomUserDetails;
import vn.edu.cuunan.service.CloudinaryService;
import vn.edu.cuunan.service.SosService;
import vn.edu.cuunan.entity.RescueTeam;
import vn.edu.cuunan.repository.RescueAssignmentRepository;
import vn.edu.cuunan.util.GeoUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/sos")
public class SosController {

    private static final Logger log = LoggerFactory.getLogger(SosController.class);

    private final SosService sosService;
    private final CloudinaryService cloudinaryService;
    private final RescueAssignmentRepository rescueAssignmentRepository;

    @Value("${app.hotlines.rescue:112}")
    private String rescueHotline;

    @Value("${app.hotlines.fire:114}")
    private String fireHotline;

    @Value("${app.hotlines.medical:115}")
    private String medicalHotline;

    public SosController(SosService sosService,
                         CloudinaryService cloudinaryService,
                         RescueAssignmentRepository rescueAssignmentRepository) {
        this.sosService = sosService;
        this.cloudinaryService = cloudinaryService;
        this.rescueAssignmentRepository = rescueAssignmentRepository;
    }

    /**
     * Hiển thị Form gửi tín hiệu cứu nạn khẩn cấp.
     */
    @GetMapping({"", "/request"})
    public String sosForm(@RequestParam(value = "lat", required = false) Double lat,
                          @RequestParam(value = "lng", required = false) Double lng,
                          @AuthenticationPrincipal CustomUserDetails userDetails,
                          Model model) {
        populateHotlines(model);
        model.addAttribute("pageTitle", "Gửi Tín Hiệu Cứu Nạn Khẩn Cấp (SOS)");

        SosRequestDto dto = new SosRequestDto();
        if (lat != null && lng != null) {
            dto.setLatitude(lat);
            dto.setLongitude(lng);
        }

        // Tự động điền thông tin nếu người dân đã đăng nhập
        if (userDetails != null && userDetails.getUser() != null) {
            dto.setContactName(userDetails.getUser().getFullName());
            dto.setContactPhone(userDetails.getUser().getPhone());
            model.addAttribute("currentUser", userDetails.getUser());
        }

        model.addAttribute("sosRequestDto", dto);
        return "sos/request";
    }

    /**
     * Đường dẫn phụ /sos/create chuyển hướng về /sos.
     */
    @GetMapping("/create")
    public String sosCreateRedirect() {
        return "redirect:/sos";
    }

    /**
     * Tiếp nhận phiếu gửi cứu nạn từ form Thymeleaf (hỗ trợ upload ảnh hiện trường).
     */
    @PostMapping
    public String submitSos(@Valid @ModelAttribute("sosRequestDto") SosRequestDto dto,
                            BindingResult bindingResult,
                            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateHotlines(model);
            model.addAttribute("pageTitle", "Gửi Tín Hiệu Cứu Nạn Khẩn Cấp (SOS)");
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại các trường thông tin bắt buộc!");
            return "sos/request";
        }

        try {
            // Tải ảnh hiện trường lên Cloudinary (hoặc local fallback) nếu có đính kèm
            if (imageFile != null && !imageFile.isEmpty()) {
                String uploadedUrl = cloudinaryService.uploadImage(imageFile, "sos_images");
                dto.setImageUrl(uploadedUrl);
                log.info("Đã gắn link ảnh hiện trường vào phiếu SOS: {}", uploadedUrl);
            }

            Long senderId = (userDetails != null) ? userDetails.getId() : null;
            SosRequest saved = sosService.createSosRequest(dto, senderId);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Phát tín hiệu cứu nạn thành công! Mã ca cứu hộ của bạn là #SOS-" + saved.getId());
            return "redirect:/sos/track/" + saved.getId();
        } catch (Exception ex) {
            log.error("Lỗi khi lưu yêu cầu SOS: ", ex);
            populateHotlines(model);
            model.addAttribute("pageTitle", "Gửi Tín Hiệu Cứu Nạn Khẩn Cấp (SOS)");
            model.addAttribute("errorMessage", "Đã có lỗi xảy ra: " + ex.getMessage());
            return "sos/request";
        }
    }

    /**
     * Trang theo dõi tiến độ chi tiết của 1 ca SOS theo ID.
     */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    @GetMapping("/track/{id}")
    public String trackDetail(@PathVariable("id") Long id, Model model) {
        populateHotlines(model);
        Optional<SosRequest> optSos = sosService.findById(id);

        if (optSos.isEmpty()) {
            model.addAttribute("pageTitle", "Không Tìm Thấy Ca Cứu Nạn");
            model.addAttribute("sosNotFound", true);
            model.addAttribute("searchedId", id);
            return "sos/track";
        }

        SosRequest sos = optSos.get();
        model.addAttribute("sos", sos);
        model.addAttribute("pageTitle", "Theo Dõi Tiến Độ Cứu Hộ #SOS-" + sos.getId());
        model.addAttribute("stepIndex", calculateStepIndex(sos.getStatus()));

        rescueAssignmentRepository.findTopBySosRequestIdOrderByAssignedAtDesc(sos.getId()).ifPresent(assignment -> {
            model.addAttribute("assignment", assignment);
            if (assignment.getTeam() != null) {
                RescueTeam team = assignment.getTeam();
                model.addAttribute("assignedTeam", team);
                model.addAttribute("teamName", team.getTeamName());
                model.addAttribute("teamPhone", team.getContactPhone());
                model.addAttribute("teamMemberCount", team.getMemberCount());
                model.addAttribute("teamLat", team.getCurrentLatitude());
                model.addAttribute("teamLng", team.getCurrentLongitude());
                if (sos.getLatitude() != null && sos.getLongitude() != null
                        && team.getCurrentLatitude() != null && team.getCurrentLongitude() != null) {
                    double dist = GeoUtils.calculateDistance(sos.getLatitude(), sos.getLongitude(), team.getCurrentLatitude(), team.getCurrentLongitude());
                    model.addAttribute("distanceKm", Math.round(dist * 100.0) / 100.0);
                }
            }
        });

        return "sos/track";
    }

    /**
     * Trang tìm kiếm / theo dõi danh sách ca SOS theo SĐT hoặc mã số.
     */
    @GetMapping("/track")
    public String trackSearch(@RequestParam(value = "code", required = false) String code,
                              @RequestParam(value = "phone", required = false) String phone,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              Model model) {
        populateHotlines(model);
        model.addAttribute("pageTitle", "Tra Cứu Tiến Độ Ca Cứu Hộ");

        // 1. Tìm theo mã số trực tiếp
        if (code != null && !code.trim().isEmpty()) {
            String cleanCode = code.trim().toUpperCase().replace("#SOS-", "").replace("SOS-", "").replace("#", "");
            try {
                Long id = Long.parseLong(cleanCode);
                return "redirect:/sos/track/" + id;
            } catch (NumberFormatException ignored) {}
        }

        // 2. Tìm theo số điện thoại
        String searchPhone = phone;
        if ((searchPhone == null || searchPhone.trim().isEmpty()) && userDetails != null && userDetails.getUser() != null) {
            searchPhone = userDetails.getUser().getPhone();
        }

        if (searchPhone != null && !searchPhone.trim().isEmpty()) {
            List<SosRequest> results = sosService.findByContactPhone(searchPhone.trim());
            model.addAttribute("results", results);
            model.addAttribute("searchResults", results);
            model.addAttribute("searchedPhone", searchPhone.trim());
        } else {
            model.addAttribute("results", Collections.emptyList());
            model.addAttribute("searchResults", Collections.emptyList());
        }

        return "sos/track";
    }

    private int calculateStepIndex(String status) {
        if (status == null) return 1;
        return switch (status.toUpperCase()) {
            case "PENDING" -> 1;
            case "DISPATCHED" -> 2;
            case "IN_PROGRESS" -> 3;
            case "RESOLVED", "COMPLETED" -> 4;
            default -> 0; // CANCELLED
        };
    }

    private void populateHotlines(Model model) {
        model.addAttribute("rescueHotline", rescueHotline);
        model.addAttribute("fireHotline", fireHotline);
        model.addAttribute("medicalHotline", medicalHotline);
    }
}
