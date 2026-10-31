package vn.edu.cuunan.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Test
    @DisplayName("Dev Simulator khi không có JavaMailSender hoặc chưa có password")
    void testDevSimulatorFallback() {
        EmailService service = new EmailService(null);
        ReflectionTestUtils.setField(service, "fromEmail", "cuunan247.vn@gmail.com");
        ReflectionTestUtils.setField(service, "mailPassword", "");

        assertFalse(service.isSmtpConfigured());

        boolean sent = service.sendOtpEmail("user@example.com", "123456", "REGISTRATION");
        assertTrue(sent);
        assertFalse(service.isLastEmailSentReal());
        assertEquals("123456", service.getLastSentOtp());
    }

    @Test
    @DisplayName("isSmtpConfigured trả về true khi có mật khẩu và email")
    void testIsSmtpConfiguredTrue() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        EmailService service = new EmailService(mailSender);
        ReflectionTestUtils.setField(service, "fromEmail", "myrealemail@gmail.com");
        ReflectionTestUtils.setField(service, "mailPassword", "abcdefghijklmnop");

        assertTrue(service.isSmtpConfigured());
    }

    @Test
    @DisplayName("isSmtpConfigured tự động nhận diện mật khẩu có khoảng trắng (Google App Password dạng 'xxxx xxxx xxxx xxxx')")
    void testIsSmtpConfiguredWithSpaces() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        EmailService service = new EmailService(mailSender);
        ReflectionTestUtils.setField(service, "fromEmail", "vovanthinh2006@gmail.com");
        ReflectionTestUtils.setField(service, "mailPassword", "ylug qnns jrfz ijif");

        assertTrue(service.isSmtpConfigured());
    }

    @Test
    @DisplayName("isResendConfigured nhận diện khi có RESEND_API_KEY")
    void testIsResendConfigured() {
        EmailService service = new EmailService(null);
        assertFalse(service.isResendConfigured());

        ReflectionTestUtils.setField(service, "resendApiKey", "re_123456789");
        assertTrue(service.isResendConfigured());
    }
}
