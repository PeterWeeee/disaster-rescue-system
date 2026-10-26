# BẢNG BÀN GIAO & TRẠNG THÁI DỰ ÁN (HANDOVER STATUS)
> **Mục đích:** File này là "bàn giao ca". Khi chuyển đổi thiết bị hoặc chuyển phiên làm việc AI, file này cho biết chính xác dự án đang ở đâu, đã làm được gì, và bước tiếp theo cần làm là gì.

---

## 1. Trạng Thái Hiện Tại (Current Snapshot)
- **Thời điểm cập nhật:** 26/10/2026
- **Nhánh Git hiện tại:** `develop`
- **Giai đoạn (Phase):** ĐÃ HOÀN THÀNH 100% CÁC PHÂN HỆ:
  - **SESSION 06 (Điều Phối Viên & Thuật Toán Haversine Formula)**
  - **SESSION 07 (Tác Nghiệp Cứu Hộ & Nghiệm Thu Hiện Trường)**
  - **SESSION 08 (Quản Trị Admin, Broadcast Khẩn Cấp Toàn Màn Hình & Thống Kê Chart.js)**
- **Trạng thái tổng quan:** Toàn bộ **54/54 unit & integration tests đạt BUILD SUCCESS 100%**. Sẵn sàng cho Session 09 (Tối ưu UI/UX Mobile toàn diện & Kịch bản bảo vệ đồ án).

---

## 2. Những Việc ĐÃ HOÀN THÀNH (Completed)
- [x] **SESSION 01 (PeterWeeee):** Khởi tạo Spring Boot 3.3.5, đa CSDL (MySQL, SQL Server, H2), Base Layout Thymeleaf + Bootstrap 5.
- [x] **SESSION 02 (Võ Văn Thịnh):** 7 JPA Entities, Spring Security 6 + Stateless JWT HttpOnly Cookie, Đăng ký / Đăng nhập 4 Roles.
- [x] **SESSION 03 (Võ Văn Thịnh):** Phân hệ SOS, GPS 1 chạm, Reverse geocoding 3 cấp tiếng Việt, Bản đồ Leaflet.js tương tác, Trang theo dõi tiến độ `/sos/track/{id}`.
- [x] **SESSION 04 (PeterWeeee):** Cloudinary Media Upload SDK & Fallback Local Storage (`/uploads/**`), Modal phóng to ảnh và xem ảnh trong popup Leaflet.
- [x] **MỞ RỘNG (Community Feed):** Bản tin thiên tai cộng đồng, trạm sơ tán an toàn, upvote xác minh tin và chuyển đổi bài đăng thành ca cứu nạn SOS.
- [x] **SESSION 06 (SV 24110329):**
  - [x] Cài đặt thuật toán khoảng cách địa lý **Haversine Formula** (`GeoUtils.java`).
  - [x] `CoordinatorService.java` & `CoordinatorApiController.java`: Lấy thống kê số liệu, tính khoảng cách km từ vị trí SOS tới các đội cứu hộ, sắp xếp từ gần nhất đến xa nhất.
  - [x] Tạo và quản lý phân công nhiệm vụ cứu nạn `RescueAssignment`, cập nhật trạng thái SOS sang `DISPATCHED` và đội sang `BUSY`.
  - [x] Bố cục Split Screen chuyên nghiệp trên `coordinator/dashboard.html` (danh sách SOS + Leaflet Live Map + Modal gợi ý đội gần nhất kèm km).
  - [x] Bộ kiểm thử `CoordinatorServiceTest`.
- [x] **SESSION 07 (SV 24110329):**
  - [x] `RescuerService.java` & `RescuerApiController.java`: Xác định đội của user (`getMyTeam`), xem nhiệm vụ được phân công.
  - [x] Luồng trạng thái nhiệm vụ: Tiếp nhận (`ACCEPTED`), Từ chối (`REJECTED` kèm lý do và tự động trả ca SOS về hàng chờ `PENDING`), Bắt đầu di chuyển (`IN_PROGRESS`).
  - [x] Nghiệm thu hoàn thành (`COMPLETED`): Nhập ghi chú, tải ảnh bằng chứng hiện trường lên Cloudinary/Local, chuyển ca SOS sang `RESOLVED` và đưa đội cứu hộ về `AVAILABLE`.
  - [x] Giao diện Mobile-First trên `rescuer/missions.html` với 3 tabs rõ ràng, nút gọi điện thoại nhanh, chỉ đường Google Maps.
  - [x] Bộ kiểm thử `RescuerServiceTest`.
- [x] **SESSION 08 (SV 24110329):**
  - [x] `AdminService.java` & `AdminApiController.java`: Quản trị người dùng (đổi vai trò, khóa/mở khóa ACTIVE/BLOCKED, chặn tự khóa admin cuối cùng).
  - [x] Quản trị đội cứu hộ: Xem danh sách, thêm mới, cập nhật tọa độ, quân số, trang thiết bị, chỉ định đội trưởng.
  - [x] Phát cảnh báo khẩn cấp diện rộng (Broadcast Alert): Tạo `DisasterAlert`, gửi thông điệp STOMP tới `/topic/broadcast-alerts`.
  - [x] `alert-client.js` & `#emergencyBroadcastModal` trong `layout/main.html`: Tự động hiện popup modal cảnh báo khẩn cấp màu đỏ/cam/vàng đè toàn màn hình trên mọi trang web kèm còi báo động Web Audio API.
  - [x] Dashboard Thống kê Chart.js: Biểu đồ tròn phân bố mức độ khẩn cấp, biểu đồ cột số ca theo quận/huyện, tính thời gian phản ứng trung bình từ dữ liệu thực 100% trong CSDL.
  - [x] Bộ kiểm thử `AdminServiceTest`.

---

## 3. Những Việc CẦN LÀM TIẾP THEO (Next Steps - Session 09)
1. **Tối ưu hóa Responsive Mobile toàn diện** cho các màn hình điện thoại thực tế.
2. **Kịch bản thuyết trình bảo vệ đồ án 5 bước** dành cho nhóm sinh viên trước hội đồng chấm thi.
