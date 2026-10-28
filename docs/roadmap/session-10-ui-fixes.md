# PHIÊN LÀM VIỆC #10: NÂNG CẤP HOÀN THIỆN GIAO DIỆN, SỬA LỖI BẢN ĐỒ, QUYÊN GÓP & PHÂN TRANG (UI FIXES)
> **Trạng thái:** ✅ ĐÃ HOÀN THÀNH (100%)  
> **Người phụ trách:** **Tri Thai (PeterWeeee - `NguyenTT162.4@gmail.com`)**  
> **Thời điểm hoàn thành:** 28/10/2026  
> **Nhánh thực hiện:** `ui-fixes` (Bản nâng cấp toàn diện trực tiếp từ nhánh `ui-redesign`)

---

## 1. Mục Tiêu Phiên
- **Nâng cấp và hoàn thiện sâu (Deep Upgrade):** Nhánh `ui-fixes` là bước phát triển tiếp nối và nâng cấp trực tiếp của `ui-redesign`, giải quyết triệt để các tồn đọng về trải nghiệm người dùng, lỗi hiển thị bản đồ, lỗi mã hóa font và tràn bảng dữ liệu.
- **Sửa lỗi hiển thị Bản đồ GIS tương tác (Leaflet Live Map):** Khắc phục lỗi che khuất điều khiển do z-index, lỗi vỡ khung bản đồ khi co giãn kích thước cửa sổ trình duyệt và lỗi render popups.
- **Sửa lỗi mã hóa ký tự UTF-8 tiếng Việt:** Khắc phục triệt để tình trạng lỗi dấu tiếng Việt trong toàn bộ các tệp SQL khởi tạo CSDL và bộ nạp dữ liệu mẫu `DataInitializer.java`.
- **Nâng cấp Cổng Quyên Góp Cứu Trợ (Donation Portal):** Bố cục Fullwidth Dark-Theme liền mạch, sang trọng, sửa lỗi tương phản chữ trắng trên nền trắng và tối ưu thao tác chọn nhanh số tiền hỗ trợ.
- **Triển khai Phân Trang Đồng Bộ (Pagination Architecture):** Bổ sung phân trang cho tất cả các bảng dữ liệu trên mọi phân hệ (Coordinator, Rescuer, Admin, Citizen Feed).
- **Tách Trang Lịch Sử Điều Phối Riêng Biệt (`coordinator/history.html`):** Giải phóng tải cho màn hình chỉ huy chính, tạo không gian tra cứu hồ sơ cứu nạn chuyên sâu.

---

## 2. Danh Sách Công Việc Đã Thực Hiện

### 2.1. Sửa Lỗi Hiển Thị & Tương Tác Bản Đồ Số GIS (`templates/map/live-map.html`)
- [x] Xử lý xung đột tầng hiển thị `z-index` giữa thanh Navbar/Sidebar và các lớp điều khiển của Leaflet (nút phóng to/thu nhỏ, chọn bản đồ vệ tinh/giao thông, nút toàn màn hình).
- [x] Bổ sung cơ chế tự động cân chỉnh khung nhìn `map.invalidateSize()` khi cửa sổ resize hoặc khi mở modal bản đồ, ngăn chặn hiện tượng bản đồ bị xám một phần.
- [x] Tối ưu hóa popup hiển thị chi tiết ca SOS: Tải nhanh ảnh hiện trường Cloudinary, nút gọi điện thoại nhanh người bị nạn và liên kết mở chỉ đường vệ tinh.

### 2.2. Khắc Phục Lỗi Encoding Ký Tự Tiếng Việt Trong Toàn Bộ Mã Nguồn & Database
- [x] Chuẩn hóa toàn bộ bộ ký tự UTF-8 cho các tệp script cơ sở dữ liệu:
  - `database/01_create_database.sql`
  - `database/02_create_tables.sql`
  - `database/03_seed_data.sql`
  - `database/mysql_init.sql`
- [x] Sửa mã hóa tiếng Việt trong trình nạp dữ liệu tự động `vn.edu.cuunan.config.DataInitializer.java`, đảm bảo tên tỉnh thành miền Trung (Đà Nẵng, Thừa Thiên Huế, Quảng Nam, Quảng Trị,...) hiển thị chuẩn xác 100%.

### 2.3. Nâng Cấp Giao Diện Cổng Quyên Góp Cứu Trợ Liền Mạch (Seamless Dark Theme)
- [x] Tái thiết kế trang chủ quyên góp (`templates/donation/index.html`) theo phong cách Fullwidth Dark Layout liền mạch, tạo cảm giác trang nghiêm và tin cậy cho hoạt động từ thiện.
- [x] Khắc phục triệt để lỗi độ tương phản (chữ trắng trên nền trắng) ở form thông tin người ủng hộ và bảng chọn nhanh các mức tiền đóng góp (50k, 100k, 200k, 500k, 1M, 2M, 5M).
- [x] Tối ưu trang xác nhận thanh toán (`donation/pay.html`) và trang báo công đức cứu trợ thành công (`donation/success.html`), hiển thị mã QR VietQR Napas 24/7 và cổng VNPay Sandbox mượt mà.

### 2.4. Triển Khai Phân Trang Dữ Liệu (Pagination) Trên Toàn Bộ Hệ Thống
- [x] Xây dựng cơ chế phân trang chuyên nghiệp cho tất cả các bảng dữ liệu:
  - **Phân hệ Điều phối (Coordinator):** Bảng ca SOS cần xử lý (`coordinator/missions.html`), bảng danh sách các đội cứu hộ (`coordinator/teams.html`), bảng yêu cầu cấp ngân sách (`coordinator/budget.html`).
  - **Phân hệ Đội cứu hộ (Rescuer):** Bảng danh sách nhiệm vụ cứu hộ (`rescuer/missions.html`), bảng kho vật tư và thiết bị cứu trợ (`rescuer/resources.html`).
  - **Bản tin thiên tai cộng đồng (Feed):** Phân trang danh sách bài viết cứu trợ và tin cảnh báo người dân (`feed/index.html`).
  - **Phân hệ Quản trị (Admin):** Phân trang bảng người dùng và bảng đội cứu hộ (`admin/dashboard.html`), phân trang danh sách nhật ký kiểm toán hệ thống (`admin/audit-logs.html`).

### 2.5. Xây Dựng Trang Lịch Sử Điều Phối Chuyên Biệt (`coordinator/history.html`)
- [x] Tạo route mới `@GetMapping("/history")` trong `CoordinatorController.java` và view `coordinator/history.html`.
- [x] Tách biệt toàn bộ các ca cứu hộ đã hoàn tất (`RESOLVED`) sang trang lịch sử riêng, kèm bộ lọc theo tỉnh thành, mức độ khẩn cấp, thời gian và phân trang linh hoạt.

### 2.6. Tinh Chỉnh Bố Cục Toàn Diện & Chuẩn Hóa CSS
- [x] Bổ sung gần 1.000 dòng CSS hiện đại trong `src/main/resources/static/css/style.css`.
- [x] Cân chỉnh tỷ lệ co giãn trang chủ (homepage scale), khoảng cách thương hiệu (brand spacing), hiệu ứng hover thẻ card và hệ thống badge trạng thái chuẩn màu quốc tế.

---

## 3. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)
- [x] Bản đồ số GIS Leaflet hoạt động trơn tru 100%, không còn xung đột layer hay lỗi co giãn màn hình.
- [x] Toàn bộ dữ liệu tiếng Việt có dấu hiển thị chuẩn xác, không bị lỗi font rác (mojibake).
- [x] Cổng quyên góp hiển thị rõ nét, không còn lỗi độ tương phản màu, thanh toán VNPay và VietQR phản hồi tức thì.
- [x] Toàn bộ các bảng danh sách lớn trong dự án đều được tích hợp thanh phân trang linh hoạt.
- [x] Đã hoàn thành và xác nhận qua chuỗi commit:
  - `3f04d8a: fix(ui): resolve map rendering, encoding, pagination, and route status bugs`
  - `d0d3d30: feat(ui): update homepage scale, donation fullwidth seamless dark layout, and brand spacing`
  - `c2bd7d7: feat(ui): refine coordinator navigation, add dedicated dispatch history, and implement pagination across all role tables`
- [x] **Toàn bộ 99/99 Unit & Integration Tests đạt BUILD SUCCESS 100%.**
