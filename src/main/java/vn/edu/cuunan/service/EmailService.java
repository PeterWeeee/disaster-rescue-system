package vn.edu.cuunan.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import vn.edu.cuunan.entity.Donation;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:cuunan247.vn@gmail.com}")
    private String fromEmail;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    private volatile boolean lastEmailSentReal = false;
    private volatile String lastSentOtp = null;
    private volatile String lastError = null;

    @Autowired
    public EmailService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean isSmtpConfigured() {
        return mailPassword != null && !mailPassword.trim().isEmpty() &&
               fromEmail != null && !fromEmail.trim().isEmpty();
    }

    public boolean isLastEmailSentReal() {
        return lastEmailSentReal;
    }

    public String getLastSentOtp() {
        return lastSentOtp;
    }

    public String getLastError() {
        return lastError;
    }

    /**
     * Gửi mã OTP xác thực qua Email với giao diện HTML chuẩn cứu nạn khẩn cấp.
     * Tự động chuyển sang chế độ Dev Simulator nếu chưa cấu hình SMTP hoặc gửi thất bại.
     */
    public boolean sendOtpEmail(String toEmail, String otpCode, String type) {
        String subject;
        String actionTitle;
        String actionDescription;

        if ("REGISTRATION".equalsIgnoreCase(type)) {
            subject = "[CứuNạn 24/7] Mã OTP Kích Hoạt Tài Khoản Của Bạn";
            actionTitle = "XÁC THỰC KÍCH HOẠT TÀI KHOẢN";
            actionDescription = "Bạn hoặc ai đó vừa dùng email này để đăng ký tài khoản trên Hệ Thống Cứu Nạn 24/7. Vui lòng nhập mã OTP bên dưới để kích hoạt tài khoản:";
        } else if ("CHANGE_EMAIL".equalsIgnoreCase(type)) {
            subject = "[CứuNạn 24/7] Mã OTP Xác Nhận Thay Đổi Email";
            actionTitle = "XÁC NHẬN THAY ĐỔI ĐỊA CHỈ EMAIL";
            actionDescription = "Bạn vừa yêu cầu cập nhật địa chỉ email liên kết trên Hệ Thống Cứu Nạn 24/7. Vui lòng sử dụng mã OTP bên dưới để xác thực thay đổi:";
        } else {
            subject = "[CứuNạn 24/7] Mã OTP Đặt Lại Mật Khẩu";
            actionTitle = "YÊU CẦU ĐẶT LẠI MẬT KHẨU";
            actionDescription = "Chúng tôi nhận được yêu cầu khôi phục mật khẩu cho tài khoản liên kết với email này. Vui lòng sử dụng mã OTP bên dưới để hoàn tất:";
        }

        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f8fafc; margin: 0; padding: 20px; color: #1e293b; }
                    .card { max-width: 550px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.08); border: 1px solid #e2e8f0; }
                    .header { background: #0f172a; padding: 25px 20px; text-align: center; color: #ffffff; }
                    .header h2 { margin: 0; font-size: 22px; font-weight: 800; letter-spacing: 1px; }
                    .header span { color: #ef4444; }
                    .body { padding: 30px 25px; }
                    .title { font-size: 17px; font-weight: bold; color: #0f172a; margin-bottom: 15px; }
                    .desc { font-size: 14px; line-height: 1.6; color: #475569; margin-bottom: 25px; }
                    .otp-box { background: #fef2f2; border: 2px dashed #ef4444; border-radius: 8px; text-align: center; padding: 18px; margin: 20px 0; }
                    .otp-code { font-size: 32px; font-weight: 900; letter-spacing: 8px; color: #dc2626; font-family: monospace; }
                    .note { font-size: 12px; color: #94a3b8; line-height: 1.5; margin-top: 25px; border-top: 1px solid #f1f5f9; padding-top: 15px; }
                    .footer { background: #f8fafc; padding: 15px 20px; text-align: center; font-size: 12px; color: #64748b; border-top: 1px solid #e2e8f0; }
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="header">
                        <h2>CỨUNẠN <span>24/7</span></h2>
                        <small style="color: #94a3b8;">Hệ Thống Phản Ứng Khẩn Cấp & Cứu Hộ Thiên Tai</small>
                    </div>
                    <div class="body">
                        <div class="title">%s</div>
                        <div class="desc">%s</div>
                        <div class="otp-box">
                            <div style="font-size: 12px; text-transform: uppercase; color: #64748b; margin-bottom: 6px;">Mã xác thực 6 chữ số</div>
                            <div class="otp-code">%s</div>
                        </div>
                        <div class="desc" style="color: #ef4444; font-weight: 600;">Mã OTP có hiệu lực trong vòng 10 phút. Tuyệt đối không chia sẻ mã này cho bất kỳ ai.</div>
                        <div class="note">Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email hoặc liên hệ tổng đài khẩn cấp 112 để được hỗ trợ.</div>
                    </div>
                    <div class="footer">
                        Đồ Án Nhóm 04 - Lập Trình Web HCMUTE &copy; 2026 CứuNạn 24/7.
                    </div>
                </div>
            </body>
            </html>
            """.formatted(actionTitle, actionDescription, otpCode);

        this.lastSentOtp = otpCode;

        // Luôn log ra console để hỗ trợ dev/test nhanh trong trường hợp offline/chưa có mật khẩu ứng dụng Gmail
        log.info("==========================================================================");
        log.info(">>> [EMAIL SERVICE] Gửi OTP tới: [{}] - Loại: [{}] - Mã OTP: [{}]", toEmail, type, otpCode);
        log.info("==========================================================================");

        if (mailSender == null || !isSmtpConfigured()) {
            this.lastEmailSentReal = false;
            this.lastError = "Chưa cấu hình mật khẩu ứng dụng Gmail (spring.mail.password)";
            log.warn(">>> JavaMailSender chưa được cấu hình mật khẩu SMTP. Đã kích hoạt Dev Simulator (OTP: {}).", otpCode);
            return true;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, "CứuNạn 24/7");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            this.lastEmailSentReal = true;
            this.lastError = null;
            log.info(">>> [EMAIL SERVICE] Đã gửi email thực tế thành công tới: {}", toEmail);
            return true;
        } catch (Exception e) {
            this.lastEmailSentReal = false;
            this.lastError = e.getMessage();
            log.warn(">>> [EMAIL SERVICE] Không thể gửi email thực tế qua SMTP (Lý do: {}). Vẫn chấp nhận OTP {} cho dev/test.", e.getMessage(), otpCode);
            return true; // Trả về true để không gián đoạn luồng test của sinh viên/giảng viên
        }
    }

    /**
     * Gửi thư cảm ơn & biên lai xác nhận đóng góp cứu trợ bão lũ.
     */
    public boolean sendDonationThankYouEmail(Donation donation) {
        if (donation == null || donation.getDonorEmail() == null || donation.getDonorEmail().trim().isEmpty()) {
            return false;
        }

        String toEmail = donation.getDonorEmail().trim();
        String donorName = donation.getDonorName() != null ? donation.getDonorName() : "Nhà hảo tâm ẩn danh";
        String amountFormatted = donation.getFormattedAmount();
        String code = donation.getTransactionCode();

        String subject = "[CứuNạn 24/7] Tri Ân Tấm Lòng Vàng Ủng Hộ Đồng Bào - Mã: " + code;

        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f8fafc; margin: 0; padding: 20px; color: #1e293b; }
                    .card { max-width: 580px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.08); border: 1px solid #e2e8f0; }
                    .header { background: linear-gradient(135deg, #059669 0%, #10b981 100%); padding: 30px 20px; text-align: center; color: #ffffff; }
                    .header h2 { margin: 0; font-size: 24px; font-weight: 800; }
                    .body { padding: 30px 25px; }
                    .greeting { font-size: 16px; font-weight: 600; color: #0f172a; margin-bottom: 12px; }
                    .message { font-size: 14px; line-height: 1.6; color: #475569; margin-bottom: 20px; }
                    .receipt-box { background: #f0fdf4; border: 1px solid #bbf7d0; border-radius: 8px; padding: 20px; margin: 20px 0; }
                    .receipt-row { display: flex; justify-content: space-between; margin-bottom: 8px; font-size: 14px; }
                    .receipt-label { color: #64748b; }
                    .receipt-val { font-weight: 700; color: #0f172a; }
                    .amount-highlight { font-size: 22px; color: #059669; font-weight: 900; }
                    .footer { background: #f8fafc; padding: 18px 20px; text-align: center; font-size: 12px; color: #64748b; border-top: 1px solid #e2e8f0; }
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="header">
                        <h2>THƯ TRI ÂN TẤM LÒNG VÀNG ❤️</h2>
                        <div style="font-size: 13px; opacity: 0.9; margin-top: 5px;">Hệ Thống Cứu Nạn 24/7 & Đồng Bào Vùng Lũ Xin Chân Thành Cảm Ơn</div>
                    </div>
                    <div class="body">
                        <div class="greeting">Kính gửi Ông/Bà: %s,</div>
                        <div class="message">
                            Hệ Thống Phản Ứng Khẩn Cấp & Cứu Hộ Thiên Tai CứuNạn 24/7 xin trân trọng ghi nhận và gửi lời cảm ơn sâu sắc nhất tới tấm lòng nhân ái của quý vị. Sự đóng góp kịp thời này là nguồn động viên vô cùng to lớn giúp bà con vùng bão lũ vượt qua giai đoạn hiểm nghèo.
                        </div>
                        <div class="receipt-box">
                            <div style="font-size: 15px; font-weight: bold; color: #166534; border-bottom: 1px dashed #bbf7d0; padding-bottom: 10px; margin-bottom: 12px;">
                                THÔNG TIN ĐÓNG GÓP CỨU TRỢ
                            </div>
                            <div class="receipt-row">
                                <span class="receipt-label">Mã giao dịch:</span>
                                <span class="receipt-val">%s</span>
                            </div>
                            <div class="receipt-row">
                                <span class="receipt-label">Số tiền ủng hộ:</span>
                                <span class="receipt-val amount-highlight">%s</span>
                            </div>
                            <div class="receipt-row">
                                <span class="receipt-label">Phương thức:</span>
                                <span class="receipt-val">%s</span>
                            </div>
                            <div class="receipt-row">
                                <span class="receipt-label">Khu vực ưu tiên:</span>
                                <span class="receipt-val">%s</span>
                            </div>
                        </div>
                        <div class="message" style="font-style: italic; color: #64748b;">
                            "Một miếng khi đói bằng một gói khi no" — Toàn bộ số tiền quyên góp được phân bổ trực tiếp cho các đội cứu nạn hiện trường mua áo phao, lương thực và phương tiện cứu hộ.
                        </div>
                    </div>
                    <div class="footer">
                        Mọi đóng góp đều được công khai minh bạch tại Bảng Vàng Cứu Trợ trên website.<br>
                        Đồ Án Nhóm 04 - HCMUTE &copy; 2026 CứuNạn 24/7.
                    </div>
                </div>
            </body>
            </html>
            """.formatted(donorName, code, amountFormatted, donation.getPaymentMethod(), donation.getTargetProvince());

        log.info(">>> [DONATION EMAIL] Gửi thư cảm ơn ủng hộ tới: [{}] - Số tiền: [{}]", toEmail, amountFormatted);

        if (mailSender == null) {
            log.info(">>> JavaMailSender dev fallback: Đã gửi thư cảm ơn thành công giả lập tới {}", toEmail);
            return true;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, "CứuNạn 24/7 - Ban Cứu Trợ");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info(">>> [DONATION EMAIL] Đã gửi thư cảm ơn thực tế tới {}", toEmail);
            return true;
        } catch (Exception e) {
            log.warn(">>> [DONATION EMAIL] Không thể gửi thư cảm ơn qua SMTP: {}", e.getMessage());
            return true;
        }
    }
}
