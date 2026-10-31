# Báo Cáo Kỹ Thuật Session 23: Sửa Nút Chọn Ảnh Hồ Sơ & Nâng Cấp Luồng Gửi OTP Email (Gmail SMTP + Dev Simulator)

## 1. Bối cảnh & Mục tiêu
Trong quá trình kiểm thử thực tế sau Session 22, phát sinh hai vấn đề trải nghiệm người dùng:
1. **Nút chọn ảnh trong chỉnh sửa hồ sơ (`/profile`) không hoạt động:** Khi người dùng nhấn vào icon ảnh hoặc nút "Chọn ảnh", hộp thoại chọn tệp của hệ điều hành không xuất hiện.
2. **OTP không gửi về hộp thư Gmail khi đăng ký hoặc sửa tài khoản:** Người dùng đăng ký hoặc đổi email nhưng không thấy mã OTP về hòm thư Gmail, dẫn đến việc không thể xác thực tài khoản.

## 2. Nguyên nhân cốt lõi (Root Cause Analysis)
1. **Thymeleaf Layout Fragment stripping `<script>`:**
   - Trong `src/main/resources/templates/profile/index.html`, trang sử dụng cú pháp:
     `th:replace="~{layout/main :: layout(${pageTitle}, ~{::#profile-content})}"`
   - Khối `<script>` của trang trước đó được đặt sau thẻ đóng `</div>` của `#profile-content` (dòng 492-760).
   - Cơ chế layout của Thymeleaf chỉ trích xuất duy nhất phần tử có ID `#profile-content`, toàn bộ thẻ HTML và `<script>` nằm ngoài phần tử này bị loại bỏ hoàn toàn trong HTML phản hồi về trình duyệt.
   - Hậu quả: Không có JavaScript nào (kể cả event listener click của nút chọn ảnh và gửi OTP) được gắn vào trang `/profile`.

2. **Chưa cấu hình mật khẩu ứng dụng Gmail SMTP (Google App Password):**
   - Gmail SMTP (`smtp.gmail.com:587`) bắt buộc tài khoản Google phải bật xác thực 2 bước (2-Step Verification) và sử dụng Mật khẩu ứng dụng 16 ký tự (App Password).
   - Mặc định biến `SPRING_MAIL_PASSWORD` rỗng `""`. Khi gọi `mailSender.send()`, máy chủ Gmail từ chối xác thực (`AuthenticationFailedException: 535-5.7.8`).
   - `EmailService` trước đó bắt ngoại lệ và trả về `true` (Dev Simulator), chỉ in mã OTP ra console log của backend. Người dùng trên giao diện web không nhìn thấy OTP và không thể hoàn tất kích hoạt.

## 3. Giải pháp & Chi tiết triển khai

### A. Sửa triệt để nút chọn ảnh đại diện (`/profile`)
- **Di chuyển toàn bộ `<script>` vào bên trong `#profile-content`:** Đảm bảo 100% mã JavaScript được Thymeleaf đóng gói vào nội dung layout và chuyển xuống trình duyệt.
- **Thêm inline handler dự phòng:** Gắn trực tiếp `onclick="document.getElementById('avatarFileInput').click();"` vào:
  1. Thẻ icon ảnh `#avatarUploadIconBtn`.
  2. Nút bấm `#btnBrowseAvatar`.
  3. Thẻ avatar wrapper `#profileAvatarWrapper` ở thẻ danh tính bên trái (kèm icon máy ảnh).
- **Trải nghiệm tức thì:** Khi chọn tệp từ máy tính, API `/api/upload/image` (folder `avatars`) tự động tải lên và cập nhật ảnh preview ngay lập tức.

### B. Nâng cấp cơ chế gửi OTP Email: Hỗ trợ kép Gmail SMTP thực tế & Dev Simulator thông minh
1. **`EmailService.java`:**
   - Thêm các phương thức: `isSmtpConfigured()`, `isLastEmailSentReal()`, `getLastSentOtp()`, `getLastError()`.
   - Nếu `spring.mail.password` được cung cấp và hợp lệ, gửi email thực tế qua Gmail SMTP kèm giao diện HTML chuẩn cứu nạn.
   - Nếu chưa cấu hình mật khẩu hoặc SMTP gặp lỗi, kích hoạt chế độ **Dev Simulator**: ghi nhận `lastEmailSentReal = false` và lưu giữ mã OTP để giao diện có thể hiển thị hỗ trợ người dùng dev/test.

2. **`OtpService.java` & `AuthService.java`:**
   - Bổ sung `getLatestOtp(email, type)` tra cứu mã OTP chưa sử dụng mới nhất từ cơ sở dữ liệu `OtpVerification`.
   - Bổ sung `isLastEmailSentReal()` để các controller dễ dàng phân luồng thông báo.

3. **`AuthController.java` & Giao diện Xác thực OTP (`verify-otp.html`, `reset-password.html`):**
   - Khi đăng ký (`/auth/register`) hoặc yêu cầu gửi lại OTP (`/auth/resend-otp`):
     - Nếu SMTP thực tế thành công: Thông báo mã đã gửi về hòm thư Gmail (hướng dẫn kiểm tra hộp thư đến và Spam).
     - Nếu đang ở chế độ Dev Simulator: Hiển thị ngay hộp thông báo **[Chế Độ Thử Nghiệm (Dev Simulator)]** với mã OTP 6 số in đậm màu đỏ và **tự động điền trước** vào ô nhập liệu OTP. Người dùng chỉ cần bấm nút "Xác Nhận Kích Hoạt" để tiếp tục luồng một cách mượt mà.

4. **Đổi Email Hồ Sơ (`ProfileController.java` & `/profile` AJAX):**
   - API `/profile/send-email-otp` trả về `devOtp` khi chưa có mật khẩu SMTP.
   - Giao diện profile lập tức hiển thị mã OTP thử nghiệm và tự động điền vào ô mã xác nhận `emailOtp`.

5. **Cấu hình SMTP trong `application.yml` & Hướng dẫn trong `application-secret.example.yml`:**
   - Bổ sung các thuộc tính SMTP:
     ```yaml
     spring:
       mail:
         properties:
           mail:
             smtp:
               auth: true
               starttls:
                 enable: true
                 required: true
               ssl:
                 trust: smtp.gmail.com
     ```
   - Cung cấp hướng dẫn từng bước 5 thao tác tạo Mật khẩu ứng dụng Google (App Password) trong `application-secret.example.yml` để sinh viên/giảng viên/nhóm dự án có thể kết nối hòm thư Gmail thật bất kỳ lúc nào.

## 4. Kết quả kiểm thử
- **Toàn bộ 146 ca kiểm thử unit & integration:** Chạy thành công 100% không có lỗi (`mvn clean test`, BUILD SUCCESS).
  - Bổ sung `EmailServiceTest`: Kiểm tra chính xác logic Dev Simulator fallback và điều kiện cấu hình SMTP.
  - Bổ sung ca kiểm thử trong `OtpServiceTest`: Kiểm tra tra cứu `getLatestOtp` và ủy quyền trạng thái gửi email.
- **Kiểm thử giao diện:**
  - Nút chọn ảnh và icon ảnh mở file picker mượt mà.
  - Gửi OTP không bị treo; khi không có mật khẩu SMTP, mã được cấp trực tiếp trên màn hình; khi có mật khẩu SMTP, thư được chuyển phát đến hòm thư thực tế.
