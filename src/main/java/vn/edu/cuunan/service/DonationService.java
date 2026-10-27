package vn.edu.cuunan.service;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cuunan.config.VnpayConfig;
import vn.edu.cuunan.dto.DonationRequestDto;
import vn.edu.cuunan.entity.Donation;
import vn.edu.cuunan.repository.DonationRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DonationService {

    private static final Logger log = LoggerFactory.getLogger(DonationService.class);

    private final DonationRepository donationRepository;
    private final VnpayConfig vnpayConfig;
    private final EmailService emailService;

    @Autowired
    public DonationService(DonationRepository donationRepository,
                           VnpayConfig vnpayConfig,
                           EmailService emailService) {
        this.donationRepository = donationRepository;
        this.vnpayConfig = vnpayConfig;
        this.emailService = emailService;
    }

    /**
     * Khởi tạo giao dịch quyên góp mới.
     * Tạo mã giao dịch duy nhất và trả về URL thanh toán (VNPay Gateway hoặc Trang hiển thị VietQR/MoMo).
     */
    @Transactional
    public Map<String, Object> createDonation(DonationRequestDto dto, HttpServletRequest request) {
        String code = generateTransactionCode();
        String donorName = (dto.getDonorName() == null || dto.getDonorName().trim().isEmpty())
                ? "Nhà hảo tâm ẩn danh"
                : dto.getDonorName().trim();

        String paymentMethod = dto.getPaymentMethod() != null ? dto.getPaymentMethod().toUpperCase() : "VIETQR";

        Donation donation = Donation.builder()
                .donorName(donorName)
                .donorPhone(dto.getDonorPhone())
                .donorEmail(dto.getDonorEmail())
                .amount(dto.getAmount())
                .message(dto.getMessage())
                .targetProvince(dto.getTargetProvince() != null ? dto.getTargetProvince() : "TOAN_MIEN_TRUNG")
                .paymentMethod(paymentMethod)
                .transactionCode(code)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        donation = donationRepository.save(donation);
        log.info(">>> Tạo mới đóng góp cứu trợ mã: [{}], số tiền: [{}], phương thức: [{}]",
                code, dto.getAmount(), paymentMethod);

        Map<String, Object> result = new HashMap<>();
        result.put("transactionCode", code);
        result.put("donation", donation);

        if ("VNPAY".equalsIgnoreCase(paymentMethod)) {
            String orderInfo = "Ung ho dong bao lu lut ma " + code;
            String vnpayUrl = vnpayConfig.buildPaymentUrl(code, dto.getAmount().longValue(), orderInfo, request);
            result.put("redirectUrl", vnpayUrl);
        } else {
            result.put("redirectUrl", "/donations/pay/" + code);
        }

        return result;
    }

    /**
     * Xử lý callback phản hồi từ cổng thanh toán VNPay Sandbox.
     */
    @Transactional
    public Optional<Donation> processVnpayReturn(Map<String, String> queryParams) {
        String vnpSecureHash = queryParams.get("vnp_SecureHash");
        String vnpTxnRef = queryParams.get("vnp_TxnRef");
        String vnpResponseCode = queryParams.get("vnp_ResponseCode");

        if (vnpTxnRef == null) {
            log.error(">>> VNPay callback thiếu vnp_TxnRef");
            return Optional.empty();
        }

        Optional<Donation> optionalDonation = donationRepository.findByTransactionCode(vnpTxnRef);
        if (optionalDonation.isEmpty()) {
            log.error(">>> Không tìm thấy giao dịch quyên góp với mã: {}", vnpTxnRef);
            return Optional.empty();
        }

        Donation donation = optionalDonation.get();

        // Chuẩn bị dữ liệu kiểm tra checksum SHA-512
        Map<String, String> fields = new HashMap<>(queryParams);
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        boolean isValidSignature = vnpayConfig.validateSignature(fields, vnpSecureHash);
        if (!isValidSignature) {
            log.warn(">>> Sai chữ ký VNPay cho mã giao dịch: {}", vnpTxnRef);
            donation.setStatus("FAILED");
            donationRepository.save(donation);
            return Optional.of(donation);
        }

        // Kiểm tra mã thành công "00"
        if ("00".equals(vnpResponseCode)) {
            donation.setStatus("SUCCESS");
            donation.setPaidAt(LocalDateTime.now());
            donation = donationRepository.save(donation);
            log.info(">>> Thanh toán VNPay THÀNH CÔNG cho mã: [{}], số tiền: [{}]", vnpTxnRef, donation.getAmount());

            // Gửi thư cảm ơn nếu có email
            try {
                emailService.sendDonationThankYouEmail(donation);
            } catch (Exception ex) {
                log.warn(">>> Lỗi gửi email cảm ơn sau thanh toán: {}", ex.getMessage());
            }
        } else {
            donation.setStatus("FAILED");
            donation = donationRepository.save(donation);
            log.warn(">>> Giao dịch VNPay thất bại hoặc bị hủy bởi người dùng: mã lỗi {}", vnpResponseCode);
        }

        return Optional.of(donation);
    }

    /**
     * Xác nhận thanh toán thủ công (Quét mã VietQR hoặc MoMo chuyển khoản trực tiếp).
     */
    @Transactional
    public Optional<Donation> confirmManualPayment(String transactionCode) {
        Optional<Donation> optionalDonation = donationRepository.findByTransactionCode(transactionCode);
        if (optionalDonation.isEmpty()) {
            return Optional.empty();
        }

        Donation donation = optionalDonation.get();
        donation.setStatus("SUCCESS");
        donation.setPaidAt(LocalDateTime.now());
        donation = donationRepository.save(donation);
        log.info(">>> Xác nhận chuyển khoản VietQR/MoMo THÀNH CÔNG cho mã: [{}]", transactionCode);

        try {
            emailService.sendDonationThankYouEmail(donation);
        } catch (Exception ex) {
            log.warn(">>> Lỗi gửi email cảm ơn sau xác nhận chuyển khoản: {}", ex.getMessage());
        }

        return Optional.of(donation);
    }

    public Optional<Donation> getDonationByCode(String transactionCode) {
        return donationRepository.findByTransactionCode(transactionCode);
    }

    public List<Donation> getRecentDonations() {
        return donationRepository.findTop15ByStatusOrderByPaidAtDesc("SUCCESS");
    }

    public List<Donation> getAllSuccessfulDonations() {
        return donationRepository.findAllByStatusOrderByPaidAtDesc("SUCCESS");
    }

    /**
     * Lấy thống kê tổng số tiền và số lượt ủng hộ hiển thị trên Dashboard & Header.
     */
    public Map<String, Object> getDonationStats() {
        BigDecimal totalAmount = donationRepository.sumAmountByStatus("SUCCESS");
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }

        long totalCount = donationRepository.countByStatus("SUCCESS");

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAmount", totalAmount);
        stats.put("totalCount", totalCount);
        stats.put("targetAmount", new BigDecimal("500000000")); // Mục tiêu 500 triệu đồng
        return stats;
    }

    private String generateTransactionCode() {
        long timestamp = System.currentTimeMillis() % 10000000;
        int random = 1000 + new Random().nextInt(9000);
        return "CN" + timestamp + random;
    }
}
