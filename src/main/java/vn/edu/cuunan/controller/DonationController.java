package vn.edu.cuunan.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    // Cấu hình thông tin tài khoản BIDV tiếp nhận quyên góp của hệ thống CứuNạn 24/7
    @Value("${donation.bank.bin:970418}")
    private String bankBin;

    @Value("${donation.bank.name:BIDV (Ngân hàng TMCP Đầu tư và Phát triển Việt Nam)}")
    private String bankName;

    @Value("${donation.bank.account-no:6351078034}")
    private String accountNo;

    @Value("${donation.bank.account-name:NGUYEN TRI THAI}")
    private String accountName;

    public static final String BANK_BIN = "970418";
    public static final String BANK_NAME = "BIDV (Ngân hàng TMCP Đầu tư và Phát triển Việt Nam)";
    public static final String ACCOUNT_NO = "6351078034";
    public static final String ACCOUNT_NAME = "NGUYEN TRI THAI";

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

        // Tạo dynamic VietQR URL theo chuẩn Napas 24/7 với tài khoản BIDV thật
        String curBin = (bankBin != null && !bankBin.isBlank()) ? bankBin : BANK_BIN;
        String curAccNo = (accountNo != null && !accountNo.isBlank()) ? accountNo : ACCOUNT_NO;
        String curAccName = (accountName != null && !accountName.isBlank()) ? accountName : ACCOUNT_NAME;
        String curBankName = (bankName != null && !bankName.isBlank()) ? bankName : BANK_NAME;

        String encodedAccountName = URLEncoder.encode(curAccName, StandardCharsets.UTF_8);
        String encodedAddInfo = URLEncoder.encode(donation.getTransactionCode(), StandardCharsets.UTF_8);
        String vietqrUrl = String.format(
                "https://img.vietqr.io/image/%s-%s-compact2.png?amount=%s&addInfo=%s&accountName=%s",
                curBin, curAccNo, donation.getAmount().longValue(), encodedAddInfo, encodedAccountName
        );

        model.addAttribute("donation", donation);
        model.addAttribute("vietqrUrl", vietqrUrl);
        model.addAttribute("bankName", curBankName);
        model.addAttribute("accountNo", curAccNo);
        model.addAttribute("accountName", curAccName);

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
