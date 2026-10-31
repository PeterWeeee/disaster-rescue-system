package vn.edu.cuunan.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    private static final Logger log = LoggerFactory.getLogger(MailConfig.class);

    private final JavaMailSender mailSender;

    public MailConfig(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostConstruct
    public void sanitizeMailConfiguration() {
        if (mailSender instanceof JavaMailSenderImpl impl) {
            String rawPassword = impl.getPassword();
            if (rawPassword != null && rawPassword.contains(" ")) {
                String sanitized = rawPassword.replace(" ", "").trim();
                impl.setPassword(sanitized);
                log.info(">>> [MailConfig] Đã tự động loại bỏ khoảng trắng trong Mật khẩu ứng dụng Gmail: '{}' -> '{}'", rawPassword, sanitized);
            }
            if (impl.getUsername() != null) {
                impl.setUsername(impl.getUsername().trim());
            }
        }
    }
}
