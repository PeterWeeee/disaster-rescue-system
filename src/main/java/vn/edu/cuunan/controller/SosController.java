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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.cuunan.dto.SosRequestDto;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.security.CustomUserDetails;
import vn.edu.cuunan.service.SosService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/sos")
public class SosController {

    private static final Logger log = LoggerFactory.getLogger(SosController.class);

    private final SosService sosService;

    @Value("${app.hotlines.rescue:112}")
    private String rescueHotline;

    @Value("${app.hotlines.fire:114}")
    private String fireHotline;

    @Value("${app.hotlines.medical:115}")
    private String medicalHotline;

    public SosController(SosService sosService) {
        this.sosService = sosService;
    }

    /**
     * Hiển thị Form gửi tín hiệu cứu nạn khẩn cấp.
     */
    @GetMapping
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
     * Tiếp nhận phiếu gửi cứu nạn từ form Thymeleaf.
     */
    @PostMapping
    public String submitSos(@Valid @ModelAttribute("sosRequestDto") SosRequestDto dto,
                            BindingResult bindingResult,
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
        model.addAttribute("pageTitle", "Tra Cứu Tiến Độ Cứu Hộ");

        if (code != null && !code.trim().isEmpty()) {
            String cleanCode = code.replaceAll("[^0-9]", "");
            if (!cleanCode.isEmpty()) {
                return "redirect:/sos/track/" + cleanCode;
            }
        }

        if (phone != null && !phone.trim().isEmpty()) {
            List<SosRequest> results = sosService.getSosByPhone(phone.trim());
            model.addAttribute("results", results);
            model.addAttribute("searchedPhone", phone.trim());
        } else if (userDetails != null) {
            List<SosRequest> userRequests = sosService.getSosBySender(userDetails.getId());
            model.addAttribute("results", userRequests);
            model.addAttribute("userTrackList", true);
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
            case "CANCELLED" -> -1;
            default -> 1;
        };
    }

    private void populateHotlines(Model model) {
        model.addAttribute("rescueHotline", rescueHotline);
        model.addAttribute("fireHotline", fireHotline);
        model.addAttribute("medicalHotline", medicalHotline);
    }
}
