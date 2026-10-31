# BẢNG BÀN GIAO & TRẠNG THÁI DỰ ÁN (HANDOVER STATUS)
> **Mục đích:** File này là "bàn giao ca". Khi chuyển đổi thiết bị hoặc chuyển phiên làm việc AI, file này cho biết chính xác dự án đang ở đâu, đã làm được gì, và bước tiếp theo cần làm là gì.

---

## 1. Trạng Thái Hiện Tại (Current Snapshot)
- **Thời điểm cập nhật:** 31/10/2026
- **Nhánh Git hiện tại:** `main`
- **Giai đoạn (Phase):** ĐÃ HOÀN THÀNH 100% CÁC PHÂN HỆ CỐT LÕI (SESSIONS 01 - 23):
  - **SESSION 20 (Đồng bộ cự ly tuyến đường OSRM & Định vị Live GPS thời gian thực cho 3 vai trò)** ✅ ĐÃ HOÀN THÀNH 100%
  - **SESSION 21 (Tích hợp chuyển khoản ngân hàng thật qua VietQR Napas 24/7 & SePay Webhook Auto-Confirmation)** ✅ ĐÃ HOÀN THÀNH 100%
  - **SESSION 22 (Khắc phục Phân hệ Hồ sơ cá nhân: Sửa lỗi 403 Forbidden, Tải ảnh đại diện từ máy tính, Xác thực OTP đổi Email & Sửa link quyên góp)** ✅ ĐÃ HOÀN THÀNH 100%
  - **SESSION 23 (Sửa triệt để nút chọn ảnh hồ sơ do layout fragment stripping & Nâng cấp luồng gửi OTP Email kép Gmail SMTP + Dev Simulator)** ✅ ĐÃ HOÀN THÀNH 100%
- **Trạng thái tổng quan:** Toàn bộ **146/146 unit & integration tests đạt BUILD SUCCESS 100%**.

---

## 2. Những Việc ĐÃ HOÀN THÀNH (Completed)
- [x] **SESSION 01 (PeterWeeee):** Khởi tạo Spring Boot 3.3.5, đa CSDL (MySQL, SQL Server, H2), Base Layout Thymeleaf + Bootstrap 5.
- [x] **SESSION 02 (Võ Văn Thịnh):** 7 JPA Entities, Spring Security 6 + Stateless JWT HttpOnly Cookie, Đăng ký / Đăng nhập 4 Roles.
- [x] **SESSION 03 (Võ Văn Thịnh):** Phân hệ SOS, GPS 1 chạm, Reverse geocoding 3 cấp tiếng Việt, Bản đồ Leaflet.js tương tác, Trang theo dõi tiến độ `/sos/track/{id}`.
- [x] **SESSION 04 (PeterWeeee):** Cloudinary Media Upload SDK & Fallback Local Storage (`/uploads/**`), Modal phóng to ảnh và xem ảnh trong popup Leaflet.
- [x] **SESSION 05 (PeterWeeee):** WebSocket Real-time STOMP over SockJS, kênh `/topic/sos-alerts`, âm thanh chuông báo động và marker đỏ động.
- [x] **SESSION 06 (SV 24110329):** Trung tâm chỉ huy Điều phối viên, thuật toán Haversine Formula gợi ý đội cứu hộ gần nhất, phân công nhiệm vụ cứu nạn.
- [x] **SESSION 07 (SV 24110329):** Tác nghiệp Đội cứu hộ, tiếp nhận/từ chối nhiệm vụ, nghiệm thu hiện trường hoàn thành kèm ảnh Cloudinary.
- [x] **SESSION 08 (SV 24110329):** Quản trị Admin, quản lý tài khoản & đội, phát cảnh báo Broadcast đè toàn màn hình kèm còi báo động Web Audio API, biểu đồ thống kê cơ bản.
- [x] **SESSION 09 (SV 24110329 - Nhánh `ui-redesign`):**
  - [x] Tân trang lại toàn bộ giao diện website theo phong cách chỉ huy cứu hộ hiện đại.
  - [x] Xóa sạch các nội dung thừa thãi, rườm rà (bloatware) trên trang chủ, tập trung vào 3 hành động khẩn cấp: Gửi SOS, Bản đồ cứu nạn và Quyên góp.
  - [x] Tái cấu trúc thanh điều hướng: Navbar tinh gọn + Sidebar chuyên dụng theo từng vai trò (`fragments/sidebar.html`).
- [x] **SESSION 10 (PeterWeeee - Nhánh `ui-fixes`):**
  - [x] Bản nâng cấp sâu và sửa lỗi toàn diện của `ui-redesign`.
  - [x] Sửa triệt để lỗi hiển thị Bản đồ số GIS Leaflet (xung đột z-index điều khiển và lỗi tự co giãn `invalidateSize`).
  - [x] Khắc phục toàn bộ lỗi mã hóa UTF-8 tiếng Việt trong CSDL và `DataInitializer.java`.
  - [x] Tân trang Cổng Quyên Góp cứu trợ: Bố cục Fullwidth Seamless Dark Theme.
  - [x] Triển khai phân trang (Pagination) trên toàn bộ bảng dữ liệu hệ thống.
  - [x] Tách riêng trang Lịch sử điều phối chuyên biệt (`coordinator/history.html`).
- [x] **SESSION 11 (PeterWeeee):**
  - [x] Bổ sung phân hệ Thống kê Thu - Chi minh bạch dành cho Quản trị viên (`ROLE_ADMIN`) tại `/admin/dashboard?view=finance` và `/admin/finance`.
  - [x] Thống kê tổng nguồn thu (Quỹ cơ sở 500tr + Quyên góp từ cộng đồng qua VNPay, MoMo, VietQR) và tổng chi giải ngân cho các đội cứu hộ.
- [x] **SESSION 12 (PeterWeeee - Nhánh `ui-fixes`):**
  - [x] Tạo bảng CSDL `rescue_chat_messages` và JPA Entity `RescueChatMessage.java`.
  - [x] Xây dựng `ChatService.java` và `ChatApiController.java` tiếp nhận REST API + WebSocket STOMP messaging kênh `/topic/chat/{sosId}` và `@MessageMapping("/chat/{sosId}/send")`.
  - [x] Tự động kích hoạt Khung Chat trực tuyến tại màn hình Theo dõi SOS (`templates/sos/track.html`) khi ca cứu nạn chuyển sang trạng thái `IN_PROGRESS`.
- [x] **SESSION 13 (PeterWeeee - Nhánh `ui-fixes`):**
  - [x] Cẩm nang ứng phó thiên tai chuyên sâu và trang Chi tiết từng bước (`/guide/disaster/{slug}`).
  - [x] Tích hợp 18 ảnh minh họa sinh tồn trực quan do AI tạo và biểu đồ đồ họa cứu nạn SVG cho 7 loại thiên tai phổ biến lưu trữ Cloudinary CDN.
- [x] **SESSION 14 (PeterWeeee - Nhánh `develop`):**
  - [x] Chuẩn hóa Unicode SQL Server: Thay thế toàn bộ trường text dài sang JPA `@Lob` (`NVARCHAR(MAX)` trên SQL Server và `LONGTEXT` trên MySQL).
  - [x] Bổ sung cấu hình `sendStringParametersAsUnicode=true`, gỡ bỏ `@Nationalized`, cơ chế tự phục hồi schema trong `DataInitializer.java`.
- [x] **SESSION 15 (PeterWeeee - Nhánh `develop`):**
  - [x] Sửa lỗi thẻ đóng sớm trên Bảng Tin (`feed/index.html`).
  - [x] Động hóa mục "Cảnh báo khẩn mới" kết nối trực tiếp với bảng `disaster_alerts`.
- [x] **SESSION 16 (PeterWeeee - Nhánh `develop`):**
  - [x] Nạp sớm thư viện STOMP trong `<head>`, chống trùng lặp tin nhắn `chat-msg-{id}`, timer tự kết nối lại, smart polling dự phòng mỗi 10 giây.
- [x] **SESSION 17 (PeterWeeee - Nhánh `develop`):**
  - [x] Tích hợp bộ ảnh thực địa thiên tai Cloudinary CDN folder `cuunan247/community_feed` cho Bảng Tin Cộng Đồng.
- [x] **SESSION 18 (Võ Văn Thịnh - Nhánh `develop`):**
  - [x] Tích hợp Khung Chat Nổi Toàn Hệ Thống (Persistent Floating Chat Dock) phong cách Facebook Messenger.
  - [x] Bảng Tin Cộng Đồng: Hệ thống đa cảm xúc 5 trạng thái (`LIKE`, `CARE`, `PRAY`, `WARNING`, `SAD`) và bình luận bài viết (`post_comments`).
- [x] **SESSION 19 (PeterWeeee - Nhánh `develop`):**
  - [x] Khắc phục triệt để lỗi thanh chọn cảm xúc `reaction-popover` bị lơ lửng đè lên số lượt tương tác bài viết.
  - [x] Khắc phục lỗi số lượng bình luận hiển thị nhưng ấn vào không thấy bình luận qua API fetch động `loadCommentsForPost(postId)`.
  - [x] Phương thức tự phục hồi Unicode tiếng Việt đa tầng `repairCorruptedUnicodeData()` trong `DataInitializer.java` và `ensureCleanSampleComments()`.
- [x] **SESSION 20 (PeterWeeee - Nhánh `main`):**
  - [x] Đồng bộ cự ly lộ trình OSRM (4.14 km, ~7 phút) giữa Bản đồ lộ trình đội cứu hộ (`rescuer/routes.html`) và màn hình theo dõi nạn nhân (`sos/track.html`).
  - [x] Vẽ tuyến đường OSRM GeoJSON trực quan trên bản đồ người bị nạn.
  - [x] Cơ chế Live GPS Tracking tự động cập nhật vị trí cho cả 3 vai trò (Đội cứu hộ, Nạn nhân và Điều phối viên).
- [x] **SESSION 21 (PeterWeeee - Nhánh `main`):**
  - [x] Cấu hình thông tin tài khoản ngân hàng thụ hưởng thật: **BIDV** (BIN: `970418`, STK: `962471ABQD`, Chủ TK: `NGUYEN TRI THAI`).
  - [x] Tạo mã Dynamic VietQR Napas 24/7 tự động điền sẵn STK, tên chủ tài khoản, số tiền đã chọn và mã giao dịch duy nhất dạng `CNxxxx`.
  - [x] Xây dựng DTO `SepayWebhookDto` và REST endpoint `POST /api/donations/sepay-webhook` tiếp nhận biến động số dư ngân hàng thời gian thực từ SePay, bảo mật bằng API Key.
  - [x] Tự động trích xuất mã giao dịch bằng Regex `CN\d+`, tự động cập nhật số tiền thực tế nhận được vào CSDL và cộng dồn trực tiếp vào Quỹ Cứu Trợ.
  - [x] Giao diện `pay.html` tự động nhận diện giao dịch thành công (Polling 3s) và tự động chuyển hướng sang trang Cảm ơn / Biên nhận điện tử.
- [x] **SESSION 22 (PeterWeeee - Nhánh `main`):**
  - [x] Khắc phục triệt để lỗi 403 Forbidden ("Bạn không có quyền truy cập chức năng này!") khi lưu hồ sơ và đổi mật khẩu bằng việc miễn trừ CSRF cho `/profile/**` trong `SecurityConfig.java`.
  - [x] Sửa liên kết "Ủng hộ quỹ cứu trợ" từ `@{/donate}` thành `@{/donations}` và bổ sung redirect fallback trong `HomeController.java`.
  - [x] Hỗ trợ chọn tệp ảnh đại diện từ máy tính khi bấm vào icon ảnh hoặc nút "Chọn ảnh", tự động upload lên `/api/upload/image?folder=avatars` và cập nhật ảnh preview tức thì.
  - [x] Tích hợp gửi mã xác thực OTP 6 số qua Email khi thay đổi email tài khoản, xác thực chặt chẽ tại `ProfileController.java` và `EmailService.java`.
- [x] **SESSION 23 (PeterWeeee - Nhánh `main`):**
  - [x] Sửa triệt để sự cố nút chọn ảnh và gửi OTP không phản hồi: Di chuyển toàn bộ `<script>` vào bên trong `#profile-content` để Thymeleaf layout không loại bỏ, kèm bổ sung inline handler `onclick="document.getElementById('avatarFileInput').click();"` trực tiếp trên icon, nút bấm và avatar wrapper.
  - [x] Nâng cấp luồng gửi OTP Email hỗ trợ kép:
    - **Gmail SMTP Thực Tế:** Hỗ trợ cấu hình `spring.mail.username` và `spring.mail.password` (Google App Password 16 số) trong `application-secret.yml` để chuyển phát trực tiếp đến hộp thư người dùng.
    - **Chế độ Thử Nghiệm Thông Minh (Dev Simulator):** Tự động phát hiện khi chưa cấu hình mật khẩu SMTP, hiển thị mã OTP 6 số trực tiếp trên giao diện `/auth/verify-otp`, `/auth/reset-password` và popup `/profile`, đồng thời tự động điền sẵn mã để quá trình kiểm thử không bị gián đoạn.
  - [x] Toàn bộ **146/146 unit & integration tests đạt BUILD SUCCESS 100%**.

---

## 3. Phiên Tiếp Theo / Tồn Đọng (Backlog)
- **Tên phiên:** [`session-chua-lam-mobile-testing-demo-script.md`](roadmap/session-chua-lam-mobile-testing-demo-script.md)
- **Trạng thái:** ⏸️ **Chưa thực hiện trong project này** (Backlog / Tồn đọng cho các giai đoạn nâng cấp tiếp theo)
- **Nội dung dự kiến:**
  1. Tối ưu hóa sâu responsive 100% trên các dòng màn hình điện thoại di động cụ thể.
  2. Kịch bản thuyết trình bảo vệ đồ án 5 bước dành cho hội đồng chấm thi.
