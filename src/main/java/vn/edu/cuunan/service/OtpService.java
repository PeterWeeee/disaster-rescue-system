package vn.edu.cuunan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cuunan.entity.OtpVerification;
import vn.edu.cuunan.repository.OtpVerificationRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);
    private final OtpVerificationRepository otpRepository;
    private final EmailService emailService;
    private final SecureRandom random = new SecureRandom();

    public OtpService(OtpVerificationRepository otpRepository, EmailService emailService) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }

    /**
     * Sinh mã OTP 6 chữ số ngẫu nhiên, lưu vào DB và gửi qua Email.
     */
    @Transactional
    public String generateAndSendOtp(String email, String type) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Địa chỉ email không được để trống!");
        }

        String normalizedEmail = email.trim().toLowerCase();
        String otpCode = String.format("%06d", random.nextInt(1000000));

        OtpVerification verification = OtpVerification.builder()
                .email(normalizedEmail)
                .otpCode(otpCode)
                .type(type)
                .expiryTime(LocalDateTime.now().plusMinutes(10))
                .isUsed(false)
                .build();

        otpRepository.save(verification);

        emailService.sendOtpEmail(normalizedEmail, otpCode, type);
        log.info(">>> Đã tạo OTP {} loại {} cho email {}", otpCode, type, normalizedEmail);

        return otpCode;
    }

    /**
     * Xác thực mã OTP người dùng nhập vào.
     */
    @Transactional
    public boolean verifyOtp(String email, String otpCode, String type) {
        if (email == null || otpCode == null) {
            return false;
        }

        String normalizedEmail = email.trim().toLowerCase();
        String normalizedCode = otpCode.trim();

        OtpVerification verification = otpRepository
                .findTopByEmailAndOtpCodeAndTypeAndIsUsedFalseOrderByCreatedAtDesc(normalizedEmail, normalizedCode, type)
                .orElse(null);

        if (verification == null) {
            log.warn(">>> Xác thực OTP thất bại: Không tìm thấy mã hợp lệ cho {} [{}]", normalizedEmail, normalizedCode);
            return false;
        }

        if (verification.isExpired()) {
            log.warn(">>> Xác thực OTP thất bại: Mã {} cho {} đã hết hạn!", normalizedCode, normalizedEmail);
            return false;
        }

        verification.setUsed(true);
        otpRepository.save(verification);
        log.info(">>> Xác thực OTP thành công cho email: {} [Loại: {}]", normalizedEmail, type);
        return true;
    }
}
