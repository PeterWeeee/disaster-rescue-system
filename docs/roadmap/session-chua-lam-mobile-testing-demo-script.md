# PHIÊN LÀM VIỆC CHƯA THỰC HIỆN: TỐI ƯU MOBILE, KIỂM THỬ & KỊCH BẢN BẢO VỆ ĐỒ ÁN
> **Trạng thái:** ⏸️ CHƯA LÀM TRONG PROJECT NÀY (Backlog / Tồn đọng cho tương lai)  
> **Ước tính thời gian:** 1 phiên làm việc  
> **Nhánh dự kiến:** `future/mobile-testing-demo`  
> **Ghi chú quan trọng:** Phiên làm việc này (nguyên bản là Session 09) được dời về cuối lộ trình và **tạm hoãn chưa thực hiện trong phạm vi dự án hiện tại** để tập trung nguồn lực hoàn thiện Phân hệ Tái thiết kế Giao diện (UI Redesign & UI Fixes) và Chức năng Thống kê Thu - Chi cho Admin.

---

## 1. Mục Tiêu Phiên (Kế Hoạch Dự Kiến)
- Tối ưu giao diện Responsive 100% trên thiết bị di động (Mobile First cho Người dân và Đội cứu hộ).
- Nạp sẵn bộ dữ liệu mẫu sinh động (Data Seeder: 1 Admin, 2 Coordinator, 4 Đội cứu hộ, 10 ca SOS thực tế kèm tọa độ tại khu vực miền Trung/TP.HCM).
- Chuẩn bị Kịch bản Demo thuyết trình để giảng viên chấm đồ án thấy rõ giá trị thực tế và kỹ thuật WebSocket.

---

## 2. Danh Sách Công Việc Tồn Đọng (Backlog)

### 2.1. Tối Ưu Giao Diện Di Động
- Kiểm tra các viewport điện thoại (iPhone, Samsung) trên trình duyệt DevTools.
- Đảm bảo các nút hành động (Nút gửi SOS, Nút nhận việc) có kích thước lớn, dễ bấm bằng ngón tay cái, không bị tràn màn hình.
- Ẩn bớt các cột phụ trong bảng khi hiển thị trên màn hình nhỏ.

### 2.2. Khởi Tạo Dữ Liệu Mẫu (Database Seeder)
- `DataInitializer.java` (triển khai `CommandLineRunner`):
  - Tự động kiểm tra và sinh tài khoản mẫu nếu CSDL trống:
    - Admin: `admin` / `123456`
    - Coordinator: `dieupoi1` / `123456`
    - Rescuer Leader: `cuuho1` / `123456`
    - Citizen: `nguoidan02` / `123456`
  - Sinh sẵn 4 đội cứu hộ với tọa độ thực tế.
  - Sinh sẵn 5 ca cứu hộ đang ở các trạng thái khác nhau.

### 2.3. Kịch Bản Demo Thuyết Trình Trước Hội Đồng
- **Bước 1 (1 phút):** Giới thiệu đề tài và kiến trúc đa CSDL (chạy song song MySQL/SQL Server).
- **Bước 2 (2 phút - Trọng tâm WebSocket):**
  - Mở 2 cửa sổ song song: Cửa sổ 1 (Điện thoại người dân gửi SOS) - Cửa sổ 2 (Màn hình chỉ huy Coordinator).
  - Bấm gửi SOS -> Cửa sổ chỉ huy nổ chuông và hiện marker đỏ nhấp nháy ngay lập tức không cần F5.
- **Bước 3 (1.5 phút - Điều phối):**
  - Coordinator chọn ca SOS, hệ thống tính km và gợi ý Đội cứu hộ gần nhất -> Bấm phân công.
- **Bước 4 (1.5 phút - Đội cứu hộ):**
  - Màn hình Đội cứu hộ nhận việc, tải ảnh cứu nạn thành công -> Marker trên bản đồ tự đổi sang màu xanh lá cây.
- **Bước 5 (1 phút):** Admin phát sóng cảnh báo bão khẩn cấp toàn màn hình và xem biểu đồ thống kê.

---

## 3. Tiêu Chí Nghiệm Thu (Definition of Done - DoD Khi Triển Khai)
- [ ] Khởi động project từ con số 0 trên máy mới -> Tự động có sẵn tài khoản và dữ liệu mẫu để demo ngay mà không cần nhập tay.
- [ ] Chạy trơn tru toàn bộ kịch bản 5 bước trên mà không có bất kỳ lỗi JavaScript Console hay Exception Backend.
