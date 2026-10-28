# BẢNG BÀN GIAO & TRẠNG THÁI DỰ ÁN (HANDOVER STATUS)
> **Mục đích:** File này là "bàn giao ca". Khi chuyển đổi thiết bị hoặc chuyển phiên làm việc AI, file này cho biết chính xác dự án đang ở đâu, đã làm được gì, và bước tiếp theo cần làm là gì.

---

## 1. Trạng Thái Hiện Tại (Current Snapshot)
- **Thời điểm cập nhật:** 28/10/2026
- **Nhánh Git hiện tại:** `ui-fixes` (Đã tích hợp toàn bộ nâng cấp giao diện, sửa lỗi bản đồ, phân trang và thống kê thu chi)
- **Giai đoạn (Phase):** ĐÃ HOÀN THÀNH 100% CÁC PHÂN HỆ CỐT LÕI:
  - **SESSION 09 (Tân trang giao diện & Tinh gọn nội dung thừa - Nhánh `ui-redesign`)**
  - **SESSION 10 (Nâng cấp giao diện, sửa lỗi bản đồ, quyên góp dark theme & phân trang toàn diện - Nhánh `ui-fixes`)**
  - **SESSION 11 (Bổ sung chức năng Thống kê Thu - Chi & Quản trị tài chính Quỹ cứu trợ cho Admin)**
  - **SESSION 12 (Khung Chat Trực Tuyến Thời Gian Thực Giữa Nạn Nhân SOS và Đội Cứu Hộ Khi Đang Tiếp Cận - Nhánh `ui-fixes` / `feature/mission-chat`)** 🆕 Đã lập đặc tả kỹ thuật và sẵn sàng triển khai.
- **Trạng thái tổng quan:** Toàn bộ **101/101 unit & integration tests đạt BUILD SUCCESS 100%**.

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

---

## 3. Phiên Tiếp Theo Cần Triển Khai (Upcoming Session)
- **Tên phiên:** [`session-12-mission-live-chat.md`](roadmap/session-12-mission-live-chat.md)
- **Trạng thái:** 🆕 **Sẵn sàng triển khai mã nguồn** (Specification completed)
- **Nội dung trọng tâm:**
  1. Tự động kích hoạt Khung Chat trực tuyến tại màn hình Theo dõi SOS (`templates/sos/track.html`) khi ca chuyển sang trạng thái `IN_PROGRESS` (Đội cứu hộ đang tiếp cận hiện trường).
  2. Kích hoạt Khung Chat đồng bộ tại màn hình Bản đồ & Lộ trình cứu hộ (`templates/rescuer/routes.html`) của đội cứu hộ được chỉ định.
  3. Nhắn tin 2 chiều tức thời qua WebSocket STOMP (`/topic/chat/{sosId}`) và lưu trữ bền vững trong CSDL (`rescue_chat_messages`).
  4. Bộ nút gửi tin nhắn nhanh tình huống khẩn cấp 1-chạm (Quick SOS chips).

---

## 4. Tồn Đọng / Kế Hoạch Sau Này (Session Chưa Làm - Đưa Về Cuối)
- **Tên phiên:** [`session-chua-lam-mobile-testing-demo-script.md`](roadmap/session-chua-lam-mobile-testing-demo-script.md)
- **Trạng thái:** ⏸️ **Chưa thực hiện trong project này** (Backlog / Tồn đọng cho các giai đoạn nâng cấp tiếp theo)
- **Nội dung dự kiến:**
  1. Tối ưu hóa sâu responsive 100% trên các dòng màn hình điện thoại di động cụ thể.
  2. Kịch bản thuyết trình bảo vệ đồ án 5 bước dành cho hội đồng chấm thi.
