# BẢNG BÀN GIAO & TRẠNG THÁI DỰ ÁN (HANDOVER STATUS)
> **Mục đích:** File này là "bàn giao ca". Khi chuyển đổi thiết bị hoặc chuyển phiên làm việc AI, file này cho biết chính xác dự án đang ở đâu, đã làm được gì, và bước tiếp theo cần làm là gì.

---

## 1. Trạng Thái Hiện Tại (Current Snapshot)
- **Thời điểm cập nhật:** 29/10/2026
- **Nhánh Git hiện tại:** `develop` (Đã tích hợp toàn bộ nâng cấp giao diện, cẩm nang thiên tai, live chat thời gian thực 2 chiều, bảng tin cộng đồng và chuẩn hóa Unicode SQL Server)
- **Giai đoạn (Phase):** ĐÃ HOÀN THÀNH 100% CÁC PHÂN HỆ CỐT LÕI (SESSIONS 01 - 18):
  - **SESSION 09 (Tân trang giao diện & Tinh gọn nội dung thừa - Nhánh `ui-redesign`)**
  - **SESSION 10 (Nâng cấp giao diện, sửa lỗi bản đồ, quyên góp dark theme & phân trang toàn diện - Nhánh `ui-fixes`)**
  - **SESSION 11 (Bổ sung chức năng Thống kê Thu - Chi & Quản trị tài chính Quỹ cứu trợ cho Admin)**
  - **SESSION 12 (Khung Chat Trực Tuyến Thời Gian Thực Giữa Nạn Nhân SOS và Đội Cứu Hộ Khi Đang Tiếp Cận - Nhánh `ui-fixes`)** ✅ ĐÃ HOÀN THÀNH 100%
  - **SESSION 13 (Cẩm nang ứng phó thiên tai chuyên sâu & Cloudinary CDN - Nhánh `ui-fixes`)** ✅ ĐÃ HOÀN THÀNH 100%
  - **SESSION 14 (Chuẩn hóa Unicode SQL Server & Sửa triệt để lỗi font tiếng Việt - Nhánh `develop`)** ✅ ĐÃ HOÀN THÀNH 100%
  - **SESSION 15 (Sửa lỗi đăng tin cảnh báo & Động hóa cảnh báo thiên tai - Nhánh `develop`)** ✅ ĐÃ HOÀN THÀNH 100%
  - **SESSION 16 (Khắc phục lỗi WebSocket STOMP - Đồng bộ Chat trực tuyến 2 chiều thời gian thực cho người dân và đội cứu hộ - Nhánh `develop`)** ✅ ĐÃ HOÀN THÀNH 100%
  - **SESSION 17 (Tích hợp ảnh hiện trường thiên tai từ Cloudinary CDN cho Bảng Tin Cộng Đồng - Nhánh `develop`)** ✅ ĐÃ HOÀN THÀNH 100%
  - **SESSION 18 (Khung Chat Nổi Toàn Hệ Thống Phong Cách Facebook Messenger & Đa Cảm Xúc / Bình Luận Bảng Tin - Nhánh `develop`)** ✅ ĐÃ HOÀN THÀNH 100%
- **Trạng thái tổng quan:** Toàn bộ **131/131 unit & integration tests đạt BUILD SUCCESS 100%**.

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
  - [x] Tối ưu hóa trang đăng nhập, chân trang và xây dựng bộ UI Preview độc lập (`ui-preview/`).
- [x] **SESSION 10 (PeterWeeee - Nhánh `ui-fixes`):**
  - [x] Bản nâng cấp sâu và sửa lỗi toàn diện của `ui-redesign`.
  - [x] Sửa triệt để lỗi hiển thị Bản đồ số GIS Leaflet (xung đột z-index điều khiển và lỗi tự co giãn `invalidateSize`).
  - [x] Khắc phục toàn bộ lỗi mã hóa UTF-8 tiếng Việt trong CSDL và `DataInitializer.java`.
  - [x] Tân trang Cổng Quyên Góp cứu trợ: Bố cục Fullwidth Seamless Dark Theme, khắc phục độ tương phản chữ trắng trên nền trắng.
  - [x] Triển khai phân trang (Pagination) trên toàn bộ bảng dữ liệu hệ thống (Coordinator, Rescuer, Admin, Feed).
  - [x] Tách riêng trang Lịch sử điều phối chuyên biệt (`coordinator/history.html`).
- [x] **SESSION 11 (PeterWeeee):**
  - [x] Bổ sung phân hệ Thống kê Thu - Chi minh bạch dành cho Quản trị viên (`ROLE_ADMIN`) tại `/admin/dashboard?view=finance` và `/admin/finance`.
  - [x] Thống kê tổng nguồn thu (Quỹ cơ sở 500tr + Quyên góp từ cộng đồng qua VNPay, MoMo, VietQR) và tổng chi giải ngân cho các đội cứu hộ.
  - [x] Thẻ KPI tài chính, 2 biểu đồ Chart.js (So sánh Thu - Chi và Phân bổ cổng thanh toán), bảng kê dòng tiền Thu và dòng tiền Chi kèm phân trang.
- [x] **SESSION 12 (PeterWeeee - Nhánh `ui-fixes`):**
  - [x] Tạo bảng CSDL `rescue_chat_messages` (trong cả SQL Server và MySQL scripts) và JPA Entity `RescueChatMessage.java`.
  - [x] Xây dựng `ChatService.java` và `ChatApiController.java` tiếp nhận REST API + WebSocket STOMP messaging kênh `/topic/chat/{sosId}` và `@MessageMapping("/chat/{sosId}/send")`.
  - [x] Tự động kích hoạt Khung Chat trực tuyến tại màn hình Theo dõi SOS (`templates/sos/track.html`) khi ca cứu nạn chuyển sang trạng thái `IN_PROGRESS`.
  - [x] Tự động kích hoạt Khung Chat tại màn hình Bản đồ & Lộ trình cứu hộ (`templates/rescuer/routes.html`) của Đội cứu hộ với tính năng tự động chuyển đổi phòng chat khi chọn ca khác nhau.
  - [x] Bộ nút gửi tin nhắn nhanh tình huống khẩn cấp 1-chạm (Quick SOS chips cho người dân và hướng dẫn khẩn cấp cho đội cứu hộ).
  - [x] Chế độ lưu trữ hồ sơ chỉ đọc (Read-only Archive) khi ca hoàn tất (`RESOLVED` / `COMPLETED`) để phục vụ nghiệm thu cứu trợ.
  - [x] Thêm 11 unit & integration tests (`ChatServiceTest.java`, `ChatApiControllerTest.java`), nâng tổng test suite lên **128/128 tests đạt BUILD SUCCESS 100%**.
- [x] **SESSION 13 (PeterWeeee - Nhánh `ui-fixes`):**
  - [x] Xây dựng phân hệ Cẩm nang ứng phó thiên tai chuyên sâu và trang Chi tiết từng bước (`/guide/disaster/{slug}`).
  - [x] Tích hợp 18 ảnh minh họa sinh tồn trực quan do AI tạo và biểu đồ đồ họa cứu nạn SVG cho 7 loại thiên tai phổ biến.
  - [x] Tích hợp dịch vụ lưu trữ đám mây Cloudinary: Upload và phục vụ ảnh trực tiếp từ Cloudinary CDN (`CLOUDINARY_GUIDE_BASE_URL`) qua API `/api/upload/guide-image`, kèm fallback an toàn về Local Storage.
  - [x] Thiết kế thanh chọn các tình huống thiên tai ở cột bên trái màn hình (`col-lg-3`), vị trí tĩnh chuẩn (non-floating), không trôi đè lên ảnh và nội dung bài viết khi lướt cuộn trang.
  - [x] Loại bỏ hoàn toàn nút "Phát tín hiệu SOS" trên tất cả các trang cẩm nang (`/guide/disaster` và `/guide/disaster/{slug}`).
- [x] **SESSION 14 (PeterWeeee - Nhánh `develop`):**
  - [x] Khắc phục triệt để lỗi mất dấu font tiếng Việt (biến thành `?`) trong các bảng Thông báo (`notifications`), Lịch sử hoạt động (`activity_logs`), và bảng dữ liệu tác nghiệp trên SQL Server.
  - [x] Chuẩn hóa toàn bộ trường văn bản dài từ `@Column(columnDefinition = "TEXT")` sang JPA tiêu chuẩn `@Lob` trên 10 Entities. Hibernate 6 tự động ánh xạ thông minh thành `NVARCHAR(MAX)` trên SQL Server và `LONGTEXT` trên MySQL.
  - [x] Bổ sung cấu hình `sendStringParametersAsUnicode=true` vào connection string trong `application-sqlserver.yml`.
  - [x] Gỡ bỏ cấu hình `use_nationalized_character_data: true` và `@Nationalized` gây crash khi truy vấn `VARCHAR(50)`.
  - [x] Bổ sung phương thức tự động tương thích `ensureSqlServerUnicodeCompatibility()` trong `DataInitializer.java`.
  - [x] Làm sạch và nạp lại Seed Data chuẩn tiếng Việt 100%.
- [x] **SESSION 15 (PeterWeeee - Nhánh `develop`):**
  - [x] Khắc phục triệt để lỗi nút "Đăng tin cảnh báo" không hoạt động trên Bảng Tin Cộng Đồng (`feed/index.html`) do thẻ đóng sớm trong Thymeleaf fragment layout.
  - [x] Bổ sung cơ chế tự động dọn dẹp các trường rỗng `latitude`, `longitude`, `imageFile` trước khi gửi AJAX lên `/api/feed/posts`.
  - [x] Chuẩn hóa nghiệp vụ cứu trợ mục "Cảnh báo khẩn mới": Thay thế dữ liệu tĩnh mẫu bằng dữ liệu động kết nối trực tiếp với bảng `disaster_alerts` qua `DisasterAlertRepository.findTop5ByOrderByCreatedAtDesc()`.
  - [x] Phân loại màu sắc và huy hiệu theo mức độ nghiêm trọng (`DANGER_RED`, `WARNING_ORANGE`, `NOTICE_YELLOW`) kèm địa phương ảnh hưởng và mốc thời gian.
  - [x] Tích hợp tương tác click mở modal chi tiết cảnh báo khẩn cấp `emergencyBroadcastModal` để người dân nắm rõ hướng dẫn an toàn.
  - [x] Làm rõ cơ chế kiến trúc Avatar/Logo của hệ thống: Tối ưu tải nhẹ qua FontAwesome và Letter Avatar trích xuất ký tự đầu từ họ tên người dùng.
- [x] **SESSION 16 (PeterWeeee - Nhánh `develop`):**
  - [x] Khắc phục triệt để lỗi kết nối WebSocket STOMP phía người dân: Nạp sớm `sockjs.min.js` và `stomp.min.js` trong `<head>` (`fragments/head.html`), khắc phục lỗi script inline trong `sos/track.html` chạy trước khi thư viện được tải.
  - [x] Nâng cấp client chat `sos/track.html`: Bọc `DOMContentLoaded`, bổ sung timer tự động kết nối lại (`reconnectTimer`), quản lý hủy kết nối cũ an toàn.
  - [x] Bổ sung cơ chế chống trùng lặp tin nhắn `chat-msg-{id}` cho cả người dân và đội cứu hộ (`rescuer-chat-msg-{id}`).
  - [x] Tự động tải lại lịch sử tin nhắn khi kết nối thành công để bù đắp tin nhắn bị nhỡ; bổ sung smart polling định kỳ mỗi 10 giây dự phòng nếu mất kết nối WebSocket.
  - [x] Đạt chuẩn nhắn tin 2 chiều thời gian thực (Bi-directional Real-time Mission Chat): Người dân và đội cứu hộ trao đổi tức thời (< 500ms) không cần tải lại trang.
- [x] **SESSION 17 (PeterWeeee - Nhánh `develop`):**
  - [x] Tải lên bộ ảnh thực địa thiên tai đa dạng theo từng loại hiểm họa (`LANDSLIDE`, `FLOOD`, `TREE_FALL`, `ISOLATED`, v.v.) vào Cloudinary CDN folder `cuunan247/community_feed`.
  - [x] Khai báo hằng số URL CDN và nạp tự động 6 bài đăng mẫu kèm ảnh Cloudinary trong `CommunityFeedService.java`.
  - [x] Tích hợp cơ chế tự phục hồi (Self-healing backfill): Tự động quét và bổ sung URL ảnh Cloudinary cho các bài đăng hiện hữu trong CSDL nếu chưa có ảnh.
  - [x] Bổ sung Seed Data mẫu bài đăng kèm ảnh Cloudinary trong cả `03_seed_data.sql` (SQL Server) và `mysql_init.sql` (MySQL).
  - [x] Nâng cấp giao diện Bảng Tin (`feed/index.html`): Hiển thị huy hiệu `Cloudinary CDN`, modal xem ảnh phóng to `#imagePreviewModal`, nút "Mở ảnh gốc Cloudinary", chuẩn hóa `data-*` an toàn theo Thymeleaf 3.1+.
- [x] **SESSION 18 (Võ Văn Thịnh - Nhánh `develop`):**
  - [x] Tích hợp Khung Chat Nổi Toàn Hệ Thống (Persistent Floating Chat Dock) phong cách Facebook Messenger: Tự động phát hiện phiên hoạt động qua API `GET /api/chat/active-session`, launcher bubble có badge chưa đọc và pulse dot, cửa sổ chat $380 \times 540\text{ px}$ kèm nút gọi `tel:` và định vị GPS.
  - [x] Tinh gọn giao diện: Loại bỏ hoàn toàn 2 khung chat inline cục bộ tại `sos/track.html` và `rescuer/routes.html`, hợp nhất trải nghiệm trò chuyện toàn diện về Floating Chat Dock.
  - [x] Sửa triệt để nút đóng (`X`) trên Toast tin nhắn mới (`dismissToast(event)`), bổ sung cờ `isInitial = true` ngăn chặn Toast thông báo và chuông báo ảo cho tin nhắn cũ khi vừa tải trang.
  - [x] Nâng cấp Bảng Tin Cộng Đồng (`/feed`): Hệ thống đa cảm xúc 5 trạng thái (`LIKE`, `CARE`, `PRAY`, `WARNING`, `SAD`) với popover animated mượt mà.
  - [x] Xây dựng phân hệ bình luận thời gian thực cho bài đăng cộng đồng (`PostComment.java`, `PostCommentRepository`, `/api/feed/posts/{id}/comments`), hiển thị và nạp bình luận bằng AJAX.
  - [x] Đồng bộ DDL CSDL và Seed Data mẫu bình luận/cảm xúc trên cả SQL Server (`02_create_tables.sql`, `03_seed_data.sql`, `fix_sqlserver_encoding.sql`), MySQL (`mysql_init.sql`) và `DataInitializer.java`.
  - [x] Tinh gọn giao diện Bảng Tin: Xóa card "Khẩn Cấp 24/7", sửa lỗi co ép méo avatar người dùng (`flex-shrink-0`, `min-width: 44px`, `text-truncate` cho địa chỉ), xóa nút "Chuyển thành đơn SOS".
  - [x] Toàn bộ **131/131 tests đạt BUILD SUCCESS 100%**.

---

## 3. Phiên Tiếp Theo / Tồn Đọng (Backlog)
- **Tên phiên:** [`session-chua-lam-mobile-testing-demo-script.md`](roadmap/session-chua-lam-mobile-testing-demo-script.md)
- **Trạng thái:** ⏸️ **Chưa thực hiện trong project này** (Backlog / Tồn đọng cho các giai đoạn nâng cấp tiếp theo)
- **Nội dung dự kiến:**
  1. Tối ưu hóa sâu responsive 100% trên các dòng màn hình điện thoại di động cụ thể.
  2. Kịch bản thuyết trình bảo vệ đồ án 5 bước dành cho hội đồng chấm thi.
