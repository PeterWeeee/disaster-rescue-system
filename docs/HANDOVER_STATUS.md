# BẢNG BÀN GIAO & TRẠNG THÁI DỰ ÁN (HANDOVER STATUS)
> **Mục đích:** File này là "bàn giao ca". Khi chuyển đổi thiết bị hoặc chuyển phiên làm việc AI, file này cho biết chính xác dự án đang ở đâu, đã làm được gì, và bước tiếp theo cần làm là gì.

---

## 1. Trạng Thái Hiện Tại (Current Snapshot)
- **Thời điểm cập nhật:** 29/10/2026
- **Nhánh Git hiện tại:** `develop` (Đã tích hợp toàn bộ nâng cấp giao diện, cẩm nang thiên tai, live chat, và chuẩn hóa Unicode SQL Server)
- **Giai đoạn (Phase):** ĐÃ HOÀN THÀNH 100% CÁC PHÂN HỆ CỐT LÕI (SESSIONS 01 - 14):
  - **SESSION 09 (Tân trang giao diện & Tinh gọn nội dung thừa - Nhánh `ui-redesign`)**
  - **SESSION 10 (Nâng cấp giao diện, sửa lỗi bản đồ, quyên góp dark theme & phân trang toàn diện - Nhánh `ui-fixes`)**
  - **SESSION 11 (Bổ sung chức năng Thống kê Thu - Chi & Quản trị tài chính Quỹ cứu trợ cho Admin)**
  - **SESSION 12 (Khung Chat Trực Tuyến Thời Gian Thực Giữa Nạn Nhân SOS và Đội Cứu Hộ Khi Đang Tiếp Cận - Nhánh `ui-fixes`)** ✅ ĐÃ HOÀN THÀNH 100%
  - **SESSION 13 (Cẩm nang ứng phó thiên tai chuyên sâu & Cloudinary CDN - Nhánh `ui-fixes`)** ✅ ĐÃ HOÀN THÀNH 100%
- **Trạng thái tổng quan:** Toàn bộ **128/128 unit & integration tests đạt BUILD SUCCESS 100%**.

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
  - [x] Chuẩn hóa toàn bộ trường văn bản dài từ `@Column(columnDefinition = "TEXT")` sang JPA tiêu chuẩn `@Lob` trên 10 Entities (`Notification`, `SosRequest`, `CommunityPost`, `DisasterAlert`, `ActivityLog`, `RescueTeam`, `RescueAssignment`, `RescueEquipment`, `EquipmentAssignment`, `RescueChatMessage`). Hibernate 6 tự động ánh xạ thông minh thành `NVARCHAR(MAX)` trên SQL Server (hỗ trợ so sánh `=`, `LIKE`) và `LONGTEXT` trên MySQL.
  - [x] Bổ sung cấu hình `sendStringParametersAsUnicode=true` vào connection string trong `application-sqlserver.yml` bảo đảm tham số truy vấn luôn gửi dạng Unicode `N'...'` (UTF-16).
  - [x] Phát hiện và sửa lỗi nghiêm trọng: Gỡ bỏ triệt để cấu hình `use_nationalized_character_data: true` và annotation `@Nationalized` gây crash `SQLServerException: The conversion from varchar to NCHAR is unsupported` khi truy vấn các cột `VARCHAR(50)` (`roles.name`, `users.username`). Quay về cơ chế `rs.getString()` đọc mượt cả `VARCHAR` lẫn `NVARCHAR`.
  - [x] Bổ sung phương thức tự động tương thích `ensureSqlServerUnicodeCompatibility()` trong `DataInitializer.java` (chỉ chạy khi kết nối SQL Server, an toàn cho MySQL/PostgreSQL).
  - [x] Thực hiện quy trình làm sạch toàn diện cơ sở dữ liệu: Xóa sạch dữ liệu lỗi font cũ trên toàn bộ 19 bảng, reset Identity về 0, nạp lại `03_seed_data.sql` và khởi tạo mới Notifications/ActivityLogs chuẩn tiếng Việt 100%.
  - [x] Toàn bộ **128/128 unit & integration tests đạt BUILD SUCCESS 100%**.

---

## 3. Phiên Tiếp Theo / Tồn Đọng (Backlog)
- **Tên phiên:** [`session-chua-lam-mobile-testing-demo-script.md`](roadmap/session-chua-lam-mobile-testing-demo-script.md)
- **Trạng thái:** ⏸️ **Chưa thực hiện trong project này** (Backlog / Tồn đọng cho các giai đoạn nâng cấp tiếp theo)
- **Nội dung dự kiến:**
  1. Tối ưu hóa sâu responsive 100% trên các dòng màn hình điện thoại di động cụ thể.
  2. Kịch bản thuyết trình bảo vệ đồ án 5 bước dành cho hội đồng chấm thi.
