# PHIÊN LÀM VIỆC #08: PHÂN HỆ ADMIN (QUẢN TRỊ, BROADCAST & STATS)
> **Trạng thái:** ✅ ĐÃ HOÀN THÀNH (100%)  
> **Người phụ trách:** **Sinh viên MSSV 24110329 (`24110329@student.hcmute.edu.vn`)**  
> **Thời điểm hoàn thành:** 26/10/2026  
> **Nhánh thực hiện:** `develop`

---

## 1. Mục Tiêu Phiên
- Xây dựng bảng điều khiển quản trị toàn diện cho `ROLE_ADMIN`.
- Quản lý người dùng, phân quyền và quản lý danh sách các đội cứu hộ.
- Chức năng phát sóng khẩn cấp diện rộng (Broadcast Emergency Alert) đè cảnh báo lên toàn màn hình người dùng qua WebSocket kèm còi báo động.
- Báo cáo thống kê trực quan với biểu đồ (Chart.js) dựa trên dữ liệu thật 100% từ CSDL.

---

## 2. Danh Sách Công Việc Đã Thực Hiện

### 2.1. Quản Lý Tài Khoản & Đội Cứu Hộ
- [x] Tạo `AdminUserUpdateDto.java` và `RescueTeamAdminDto.java`.
- [x] CRUD Người dùng: Khóa/mở khóa tài khoản (`ACTIVE`/`BLOCKED`), nâng/hạ quyền giữa 4 vai trò, cơ chế chống khóa tài khoản Admin đang hoạt động cuối cùng.
- [x] CRUD Đội cứu hộ: Thêm đội mới, chỉ định đội trưởng (yêu cầu role `ROLE_RESCUER`), cập nhật quân số, trang thiết bị, tọa độ trạm đóng quân.

### 2.2. Phát Sóng Cảnh Báo Khẩn Cấp (WebSocket Broadcast)
- [x] Tạo `BroadcastAlertDto.java`.
- [x] `AdminService.java`: Phương thức `broadcast` lưu `DisasterAlert` vào CSDL và phát sự kiện tới topic `/topic/broadcast-alerts`.
- [x] `layout/main.html` & `static/js/alert-client.js`: Tự động bắt sự kiện, hiển thị modal cảnh báo màu đỏ/cam/vàng đè toàn màn hình kèm còi hú Web Audio API trên mọi trang web đang mở.

### 2.3. Báo Cáo Thống Kê (Statistics Dashboard)
- [x] Tích hợp thư viện Chart.js trong `templates/admin/dashboard.html`.
- [x] Thống kê các chỉ số KPI: Tổng số ca, số ca đang chờ, số ca đang xử lý, số ca đã hoàn thành, tổng số người giải cứu an toàn, thời gian đáp ứng trung bình.
- [x] Biểu đồ tròn (Doughnut): Tỷ lệ phân bố mức độ khẩn cấp (`CRITICAL`, `HIGH`, `MEDIUM`, `LOW`).
- [x] Biểu đồ cột (Bar): Số ca cứu nạn theo từng quận/huyện.

---

## 3. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)
- [x] Admin phát cảnh báo khẩn cấp -> Mọi màn hình người dùng lập tức xuất hiện cửa sổ cảnh báo khẩn cấp đè toàn màn hình kèm còi báo động.
- [x] Trang thống kê hiển thị đầy đủ các con số thực tế và biểu đồ Chart.js trực quan.
- [x] Đổi quyền hoặc khóa tài khoản thành công -> Có hiệu lực tức thì ở request kế tiếp.
- [x] Bộ kiểm thử tự động `AdminServiceTest` đạt 100% BUILD SUCCESS.
