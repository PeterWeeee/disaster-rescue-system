package vn.edu.cuunan.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.cuunan.entity.OtpVerification;
import vn.edu.cuunan.repository.OtpVerificationRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private OtpVerificationRepository otpVerificationRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OtpService otpService;

    @Test
    @DisplayName("Tạo mã OTP 6 chữ số thành công và gọi gửi email")
    void testGenerateOtp() {
        String email = "test@gmail.com";
        String type = "REGISTRATION";

        when(otpVerificationRepository.save(any(OtpVerification.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        String otp = otpService.generateAndSendOtp(email, type);

        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("\\d{6}"));
        verify(emailService, times(1)).sendOtpEmail(email, otp, type);
    }

    @Test
    @DisplayName("Xác thực OTP thành công và đánh dấu đã sử dụng")
    void testVerifyOtpSuccess() {
        String email = "test@gmail.com";
        String code = "123456";
        String type = "REGISTRATION";

        OtpVerification otpEntity = OtpVerification.builder()
                .id(1L)
                .email(email)
                .otpCode(code)
                .type(type)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .isUsed(false)
                .build();

        when(otpVerificationRepository.findTopByEmailAndOtpCodeAndTypeAndIsUsedFalseOrderByCreatedAtDesc(email, code, type))
                .thenReturn(Optional.of(otpEntity));

        boolean isValid = otpService.verifyOtp(email, code, type);

        assertTrue(isValid);
        assertTrue(otpEntity.isUsed());
        verify(otpVerificationRepository, times(1)).save(otpEntity);
    }

    @Test
    @DisplayName("Xác thực thất bại khi OTP đã hết hạn")
    void testVerifyOtpExpired() {
        String email = "test@gmail.com";
        String code = "123456";
        String type = "REGISTRATION";

        OtpVerification expiredOtp = OtpVerification.builder()
                .id(1L)
                .email(email)
                .otpCode(code)
                .type(type)
                .expiryTime(LocalDateTime.now().minusMinutes(1)) // Expired
                .isUsed(false)
                .build();

        when(otpVerificationRepository.findTopByEmailAndOtpCodeAndTypeAndIsUsedFalseOrderByCreatedAtDesc(email, code, type))
                .thenReturn(Optional.of(expiredOtp));

        boolean isValid = otpService.verifyOtp(email, code, type);

        assertFalse(isValid);
    }
}
