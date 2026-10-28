package vn.edu.cuunan.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.cuunan.dto.DonationRequestDto;
import vn.edu.cuunan.entity.Donation;
import vn.edu.cuunan.service.DonationService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
@RequestMapping("/donations")
public class DonationController {

    private final DonationService donationService;

    // Thông tin tài khoản tiếp nhận quyên góp của hệ thống CứuNạn 24/7 (MB Bank)
    public static final String BANK_BIN = "970422"; // MB Bank (Ngân hàng Quân Đội)
    public static final String BANK_NAME = "MB Bank (Ngân hàng Quân Đội)";
    public static final String ACCOUNT_NO = "0909123456";
    public static final String ACCOUNT_NAME = "QUY CUU TRO KHAN CAP CUUNAN 247";

    @Autowired
    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    /**
     * Trang chủ cổng quyên góp / ủng hộ đồng bào bão lũ.
     */
    @GetMapping
    public String index(Model model) {
        model.addAttribute("donationDto", new DonationRequestDto());
        model.addAttribute("recentDonations", donationService.getRecentDonations());
        model.addAttribute("stats", donationService.getDonationStats());
        return "donation/index";
    }

    /**
     * Xử lý biểu mẫu ủng hộ, tạo mã giao dịch và chuyển hướng tới VNPay hoặc trang quét mã QR.
     */
    @PostMapping("/submit")
    public String submitDonation(@Valid @ModelAttribute("donationDto") DonationRequestDto dto,
                                 BindingResult bindingResult,
                                 HttpServletRequest request,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("recentDonations", donationService.getRecentDonations());
            model.addAttribute("stats", donationService.getDonationStats());
            return "donation/index";
        }

        try {
            Map<String, Object> result = donationService.createDonation(dto, request);
            String redirectUrl = (String) result.get("redirectUrl");
            return "redirect:" + redirectUrl;
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể tạo giao dịch ủng hộ: " + ex.getMessage());
            return "redirect:/donations";
        }
    }

    /**
     * Trang hiển thị mã VietQR hoặc MoMo để người dùng quét mã chuyển tiền.
     */
    @GetMapping("/pay/{code}")
    public String showPaymentPage(@PathVariable("code") String code, Model model, RedirectAttributes redirectAttributes) {
        Optional<Donation> optionalDonation = donationService.getDonationByCode(code);
        if (optionalDonation.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy giao dịch quyên góp.");
            return "redirect:/donations";
        }

        Donation donation = optionalDonation.get();
        if ("SUCCESS".equalsIgnoreCase(donation.getStatus())) {
            return "redirect:/donations/success?code=" + code;
        }

        // Tạo dynamic VietQR URL theo chuẩn Napas 24/7
        String encodedAccountName = URLEncoder.encode(ACCOUNT_NAME, StandardCharsets.UTF_8);
        String encodedAddInfo = URLEncoder.encode(donation.getTransactionCode(), StandardCharsets.UTF_8);
        String vietqrUrl = String.format(
                "https://img.vietqr.io/image/%s-%s-compact2.png?amount=%s&addInfo=%s&accountName=%s",
                BANK_BIN, ACCOUNT_NO, donation.getAmount().longValue(), encodedAddInfo, encodedAccountName
        );

        model.addAttribute("donation", donation);
        model.addAttribute("vietqrUrl", vietqrUrl);
        model.addAttribute("bankName", BANK_NAME);
        model.addAttribute("accountNo", ACCOUNT_NO);
        model.addAttribute("accountName", ACCOUNT_NAME);

        return "donation/pay";
    }

    /**
     * Xác nhận đã chuyển tiền thành công từ phía người ủng hộ (cho phương thức VietQR / MoMo).
     */
    @PostMapping("/confirm-pay")
    public String confirmPayment(@RequestParam("transactionCode") String code, RedirectAttributes redirectAttributes) {
        Optional<Donation> confirmed = donationService.confirmManualPayment(code);
        if (confirmed.isPresent()) {
            return "redirect:/donations/success?code=" + code;
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy mã giao dịch để xác nhận.");
            return "redirect:/donations";
        }
    }

    /**
     * Xử lý callback khi người dùng hoàn thành thanh toán từ cổng VNPay Sandbox.
     */
    @GetMapping("/vnpay-return")
    public String vnpayReturn(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            params.put(entry.getKey(), entry.getValue()[0]);
        }

        Optional<Donation> processed = donationService.processVnpayReturn(params);
        if (processed.isPresent()) {
            Donation donation = processed.get();
            if ("SUCCESS".equalsIgnoreCase(donation.getStatus())) {
                return "redirect:/donations/success?code=" + donation.getTransactionCode();
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Thanh toán qua VNPay không thành công hoặc đã bị hủy.");
                return "redirect:/donations";
            }
        }

        redirectAttributes.addFlashAttribute("errorMessage", "Xác thực chữ ký phản hồi VNPay không hợp lệ.");
        return "redirect:/donations";
    }

    /**
     * Trang vinh danh / cảm ơn và xuất biên nhận điện tử đóng góp cứu trợ.
     */
    @GetMapping("/success")
    public String success(@RequestParam("code") String code, Model model, RedirectAttributes redirectAttributes) {
        Optional<Donation> optionalDonation = donationService.getDonationByCode(code);
        if (optionalDonation.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin đóng góp.");
            return "redirect:/donations";
        }

        model.addAttribute("donation", optionalDonation.get());
        return "donation/success";
    }
}
