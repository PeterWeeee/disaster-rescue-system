# PHIÊN LÀM VIỆC #08: PHÂN HỆ ADMIN (QUẢN TRỊ & BROADCAST)
> **Trạng thái:** ⏳ Chưa bắt đầu  
> **Ước tính thời gian:** 1 phiên làm việc  
> **Nhánh thực hiện:** `develop`

---

## 1. Mục Tiêu Phiên
- Xây dựng bảng điều khiển quản trị toàn diện cho `ROLE_ADMIN`.
- Quản lý người dùng, phân quyền và quản lý danh sách các đội cứu hộ.
- Chức năng phát sóng khẩn cấp diện rộng (Broadcast Emergency Alert) đè cảnh báo lên toàn màn hình người dùng qua WebSocket.
- Báo cáo thống kê trực quan với biểu đồ (Chart.js).

---

## 2. Danh Sách Công Việc Cần Thực Hiện

### 2.1. Quản Lý Tài Khoản & Đội Cứu Hộ
- CRUD Người dùng: Khóa/mở khóa tài khoản, nâng quyền từ User lên Rescuer/Coordinator.
- CRUD Đội cứu hộ: Thêm đội mới, chỉ định đội trưởng, cập nhật trang thiết bị xuồng cứu sinh.

### 2.2. Phát Sóng Cảnh Báo Khẩn Cấp (WebSocket Broadcast)
- Entity `DisasterAlert.java`: `title`, `content`, `severity`, `affectedRegion`, `isBroadcast`, `createdAt`.
- Form Admin: Nhập tiêu đề (Vd: *"ĐẬP THỦY ĐIỆN XẢ LŨ LÚC 14H"*), chọn mức độ đỏ khẩn cấp.
- Server phát sự kiện tới `/topic/broadcast-alerts`.
- Phía client: Mọi trang web đang mở đều tự động hiện Modal cảnh báo màu đỏ kèm chuông báo động, bắt buộc người dùng bấm xác nhận đã đọc.

### 2.3. Báo Cáo Thống Kê (Statistics Dashboard)
- Tích hợp thư viện Chart.js.
- Thống kê các chỉ số KPI: Tổng số người đã cứu an toàn, số ca đang chờ, thời gian phản ứng trung bình.
- Biểu đồ tròn: Tỷ lệ phân bố mức độ khẩn cấp (`CRITICAL`, `HIGH`, `MEDIUM`, `LOW`).
- Biểu đồ cột: Số ca cứu nạn theo từng quận/huyện.

---

## 3. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)
- [ ] Admin bấm nút "Phát cảnh báo khẩn cấp" -> Ngay lập tức màn hình của Người dân và Điều phối viên xuất hiện cửa sổ cảnh báo khẩn cấp đè toàn màn hình.
- [ ] Trang thống kê hiển thị đầy đủ các con số thực tế và biểu đồ Chart.js trực quan.
- [ ] Khóa tài khoản thành công -> Tài khoản đó lập tức không thể đăng nhập hoặc thao tác tiếp.
