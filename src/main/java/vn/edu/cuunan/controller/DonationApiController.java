package vn.edu.cuunan.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.cuunan.dto.SepayWebhookDto;
import vn.edu.cuunan.entity.Donation;
import vn.edu.cuunan.service.DonationService;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/donations")
public class DonationApiController {

    private static final Logger log = LoggerFactory.getLogger(DonationApiController.class);

    private final DonationService donationService;

    @Value("${sepay.api-key:}")
    private String sepayApiKey;

    @Autowired
    public DonationApiController(DonationService donationService) {
        this.donationService = donationService;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(donationService.getDonationStats());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Map<String, Object>>> getRecent() {
        List<Donation> list = donationService.getRecentDonations();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Donation d : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("donorName", d.getDonorName());
            item.put("amount", d.getAmount());
            item.put("formattedAmount", d.getFormattedAmount());
            item.put("message", d.getMessage());
            item.put("targetProvince", d.getTargetProvince());
            item.put("paidAt", d.getPaidAt());
            result.add(item);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status/{code}")
    public ResponseEntity<Map<String, Object>> checkStatus(@PathVariable("code") String code) {
        Optional<Donation> optionalDonation = donationService.getDonationByCode(code);
        Map<String, Object> response = new HashMap<>();
        if (optionalDonation.isPresent()) {
            response.put("status", optionalDonation.get().getStatus());
            response.put("amount", optionalDonation.get().getAmount());
            response.put("transactionCode", code);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "NOT_FOUND");
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * Webhook tiếp nhận thông báo biến động số dư tự động từ SePay.
     * Khi có người chuyển khoản vào tài khoản ngân hàng thật (BIDV),
     * SePay sẽ gửi thông tin giao dịch qua webhook này để tự động cập nhật hệ thống.
     */
    @PostMapping("/sepay-webhook")
    public ResponseEntity<Map<String, Object>> handleSepayWebhook(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) SepayWebhookDto payload) {

        log.info(">>> Nhận SePay Webhook - Auth: [{}], Payload: [{}]", authHeader, payload);

        // 1. Kiểm tra xác thực API Key từ SePay
        if (sepayApiKey != null && !sepayApiKey.isBlank()) {
            boolean authorized = false;
            if (authHeader != null && !authHeader.isBlank()) {
                String token = authHeader.replace("Apikey ", "")
                                         .replace("apikey ", "")
                                         .replace("Bearer ", "")
                                         .replace("bearer ", "")
                                         .trim();
                if (token.equalsIgnoreCase(sepayApiKey.trim())) {
                    authorized = true;
                }
            }
            if (!authorized) {
                log.warn(">>> SePay Webhook bị từ chối: Sai hoặc thiếu API Key! Header: [{}]", authHeader);
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized API Key"));
            }
        }

        if (payload == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Payload is null"));
        }

        // 2. Chỉ xử lý giao dịch tiền vào (transferType = "in")
        if (payload.getTransferType() != null && !"in".equalsIgnoreCase(payload.getTransferType())) {
            log.info(">>> Bỏ qua giao dịch tiền ra (transferType: {})", payload.getTransferType());
            return ResponseEntity.ok(Map.of("success", true, "message", "Ignored non-in transfer"));
        }

        // 3. Trích xuất mã giao dịch quyên góp (dạng CN123456...) từ nội dung chuyển khoản
        String content = payload.getContent();
        if (content == null || content.isBlank()) {
            content = payload.getDescription();
        }

        if (content != null && !content.isBlank()) {
            Pattern pattern = Pattern.compile("CN\\d+", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                String txCode = matcher.group().toUpperCase();
                log.info(">>> Tìm thấy mã ủng hộ: [{}] từ nội dung ngân hàng: [{}]", txCode, content);

                Optional<Donation> updated = donationService.confirmSepayPayment(txCode, payload.getTransferAmount());
                if (updated.isPresent()) {
                    Donation donation = updated.get();
                    log.info(">>> Đã kích hoạt thành công đơn ủng hộ [{}], số tiền ghi nhận: [{} đ]",
                            txCode, donation.getAmount());
                    return ResponseEntity.ok(Map.of(
                            "success", true,
                            "message", "Payment processed successfully",
                            "transactionCode", txCode,
                            "amount", donation.getAmount()
                    ));
                } else {
                    log.warn(">>> Không tìm thấy đơn ủng hộ tương ứng với mã: [{}]", txCode);
                }
            } else {
                log.warn(">>> Không tìm thấy mẫu mã giao dịch (CNxxxx) trong nội dung: [{}]", content);
            }
        }

        return ResponseEntity.ok(Map.of("success", true, "message", "Webhook received but no matching donation found"));
    }
}
