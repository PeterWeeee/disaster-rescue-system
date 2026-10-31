# SESSION 22: KHẮC PHỤC PHÂN HỆ HỒ SƠ CÁ NHÂN & QUẢN TRỊ DANH TÍNH
> **Mục tiêu:** Khắc phục triệt để lỗi 403 Forbidden ("Bạn không có quyền truy cập chức năng này!") khi cập nhật hồ sơ cá nhân và đổi mật khẩu, sửa lỗi liên kết "Ủng hộ quỹ cứu trợ" tại `/profile`, bổ sung tính năng chọn ảnh đại diện từ máy tính qua icon ảnh và triển khai xác thực OTP 6 số qua Email khi thay đổi địa chỉ Gmail.

---

## 1. Bối Cảnh & Vấn Đề (Problem Statement)
1. **Lỗi 403 Forbidden khi lưu hồ sơ và đổi mật khẩu:**
   - Hệ thống sử dụng kiến trúc Spring Security 6 với xác thực JWT lưu trong HttpOnly Cookie (`SessionCreationPolicy.STATELESS`).
   - Cấu hình CSRF bỏ qua các phân hệ `/sos/**`, `/donations/**`, `/auth/**`, `/admin/**`, nhưng đường dẫn `/profile/**` chưa được đưa vào danh sách ngoại lệ `ignoringRequestMatchers`.
   - Khi người dùng gửi form `POST /profile/update` hoặc `POST /profile/change-password`, `CsrfFilter` chặn yêu cầu với `InvalidCsrfTokenException` -> chuyển sang `CustomAccessDeniedHandler` trả về trang Whitelabel Error 403: *"Bạn không có quyền truy cập chức năng này!"*.
2. **Liên kết "Ủng hộ quỹ cứu trợ" không hoạt động:**
   - Trong tab "Quyên Góp" ở trang `/profile`, liên kết đang trỏ đến `@{/donate}` trong khi Controller thực tế của hệ thống được cấu hình tại `@RequestMapping("/donations")`.
3. **Ảnh đại diện chưa hỗ trợ chọn từ máy tính:**
   - Ô nhập đường dẫn ảnh đại diện (`Avatar URL`) chỉ cho phép dán URL văn bản, icon ảnh không có sự kiện click mở hộp thoại chọn tệp cục bộ trên máy tính.
4. **Thay đổi Email chưa có cơ chế xác nhận bảo mật:**
   - Việc thay đổi địa chỉ hòm thư liên kết cần gửi mã OTP 6 số xác thực về hòm thư mới trước khi cập nhật vào cơ sở dữ liệu để chống giả mạo email và bảo vệ tài khoản cứu trợ.

---

## 2. Giải Pháp & Các Thay Đổi Kỹ Thuật (Implementation Details)

### 2.1. Cấu hình Bảo mật (`SecurityConfig.java`)
- Bổ sung `"/profile"`, `"/profile/**"`, `"/donate"`, `"/donate/**"` vào danh sách `ignoringRequestMatchers` trong cấu hình CSRF.
- Cấp quyền truy cập công khai (`permitAll()`) cho `"/donate"`, `"/donate/**"`.
- Bổ sung trường ẩn `_csrf` vào các form trong `profile/index.html`.

### 2.2. Điều hướng Cổng Quyên Góp (`HomeController.java` & `profile/index.html`)
- Đổi toàn bộ thẻ liên kết `@{/donate}` trong tab Quyên góp của trang `/profile` thành `@{/donations}`.
- Bổ sung route tự động chuyển hướng `@GetMapping({"/donate", "/donate/**"})` chuyển hướng về `/donations` trong `HomeController.java`.

### 2.3. Tải Ảnh Đại Diện Từ Máy Tính
- Biến icon `<i class="fa-solid fa-image"></i>` và nút **Chọn ảnh** thành nút kích hoạt mở hộp thoại chọn file ảnh (`<input type="file" accept="image/*">`).
- Tự động gọi API `POST /api/upload/image?folder=avatars` với `FormData`.
- Nhận URL trả về (Cloudinary hoặc fallback `/uploads/avatars/`), điền vào ô `avatarUrl` và cập nhật tức thì ảnh xem trước (Preview) trên Thẻ Tóm Tắt Danh Tính bên trái.
- Chuyển `type="url"` sang `type="text"` của ô input để hỗ trợ linh hoạt cả link nội bộ và link CDN.

### 2.4. Xác Thực Thay Đổi Email Qua Mã OTP
- **Phía Giao diện (`profile/index.html`):**
  - Tự động phát hiện khi người dùng chỉnh sửa trường Email (khác với email ban đầu).
  - Hiển thị nút **Gửi OTP** và hộp nhập mã OTP 6 số.
  - Bấm **Gửi OTP** kích hoạt AJAX gửi yêu cầu tới `/profile/send-email-otp` kèm đếm ngược 60 giây.
  - Kiểm tra phía client trước khi submit: nếu email bị sửa mà chưa nhập OTP, chặn submit và cảnh báo người dùng.
- **Phía Backend (`ProfileController.java`, `UserService.java`, `EmailService.java`):**
  - Endpoint `POST /profile/send-email-otp`: Kiểm tra định dạng email regex, kiểm tra email chưa thuộc về tài khoản khác, sinh mã OTP 6 số ngẫu nhiên qua `OtpService.generateAndSendOtp(targetEmail, "CHANGE_EMAIL")`.
  - `EmailService.java`: Bổ sung template email HTML chuẩn cứu trợ cho loại `"CHANGE_EMAIL"` với tiêu đề `[CứuNạn 24/7] Mã OTP Xác Nhận Thay Đổi Email`.
  - Endpoint `POST /profile/update`: Tiếp nhận `@RequestParam(value = "emailOtp", required = false)`. Nếu email thay đổi, bắt buộc kiểm tra `otpService.verifyOtp(newEmail, emailOtp, "CHANGE_EMAIL")`.

---

## 3. Danh Sách Tệp Đã Chỉnh Sửa & Bổ Sung
1. `src/main/java/vn/edu/cuunan/config/SecurityConfig.java` - Miễn trừ CSRF cho `/profile/**`, permitAll `/donate/**`.
2. `src/main/java/vn/edu/cuunan/controller/HomeController.java` - Bổ sung chuyển hướng `/donate` -> `/donations`.
3. `src/main/java/vn/edu/cuunan/controller/ProfileController.java` - Bổ sung endpoint `send-email-otp` và kiểm tra OTP trong `updateProfile`.
4. `src/main/java/vn/edu/cuunan/service/UserService.java` - Bổ sung phương thức `findByEmail(email)`.
5. `src/main/java/vn/edu/cuunan/service/EmailService.java` - Bổ sung template email gửi OTP đổi email.
6. `src/main/resources/templates/profile/index.html` - Giao diện chọn ảnh từ máy tính, xem trước avatar, gửi và nhập OTP email, sửa link `/donations`.
7. `src/test/java/vn/edu/cuunan/controller/ProfileControllerTest.java` - Bộ test kiểm thử tự động toàn diện cho hồ sơ và OTP email.

---

## 4. Kết Quả Kiểm Thử (Verification)
- **ProfileControllerTest:** 8/8 tests đạt PASS 100%.
- **SecurityIntegrationTest:** 10/10 tests đạt PASS 100%.
- **Toàn bộ Test Suite Dự Án:** **144/144 tests đạt BUILD SUCCESS 100%**.
