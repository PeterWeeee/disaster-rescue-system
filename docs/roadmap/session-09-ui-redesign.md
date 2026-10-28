# PHIÊN LÀM VIỆC #09: TÂN TRANG GIAO DIỆN & TINH GỌN NỘI DUNG THỪA (UI REDESIGN)
> **Trạng thái:** ✅ ĐÃ HOÀN THÀNH (100%)  
> **Người phụ trách:** **Sinh viên MSSV 24110329 (`24110329@student.hcmute.edu.vn`)**  
> **Thời điểm hoàn thành:** 28/10/2026  
> **Nhánh thực hiện:** `ui-redesign` (Merge vào luồng phát triển chính)

---

## 1. Mục Tiêu Phiên
- **Tân trang lại toàn bộ giao diện website** theo phong cách chỉ huy cứu hộ hiện đại, chuyên nghiệp, đồng bộ màu sắc nhận diện thương hiệu CứuNạn 24/7.
- **Xóa bỏ triệt để các nội dung thừa thãi, rườm rà (bloatware)** trên trang chủ và các trang phân hệ, giúp người dùng (người dân đang gặp nạn, tình nguyện viên, đội cứu hộ) tiếp cận tính năng khẩn cấp nhanh nhất, không bị phân tâm.
- **Tái cấu trúc hệ thống điều hướng (Navigation Structure):** Chuyển đổi từ thanh Navbar dồn ứ cồng kềnh sang mô hình thanh Topbar tinh gọn kết hợp Sidebar làm việc chuyên biệt cho từng vai trò (`ROLE_ADMIN`, `ROLE_COORDINATOR`, `ROLE_RESCUER`, `ROLE_CITIZEN`).
- Xây dựng bản xem trước giao diện độc lập (`ui-preview/`) để chuẩn hóa ngôn ngữ thiết kế (Design System).

---

## 2. Danh Sách Công Việc Đã Thực Hiện

### 2.1. Tái Cấu Trúc Thanh Điều Hướng (Navbar & Sidebar)
- [x] **Tinh gọn Navbar (`fragments/navbar.html`):**
  - Xóa bỏ các menu con lồng nhau phức tạp và các đường dẫn trùng lặp.
  - Tích hợp nút chuyển đổi nhanh giữa Không gian làm việc tác nghiệp và Trang thông tin cộng đồng.
  - Hiển thị gọn gàng trạng thái định danh người dùng, avatar và nút Đăng xuất an toàn.
- [x] **Xây dựng Sidebar chuyên nghiệp (`fragments/sidebar.html`):**
  - Phân vùng chức năng theo thẩm quyền đăng nhập của Spring Security:
    - **Admin:** Tổng quan quản trị, Quản lý tài khoản, Quản lý đội cứu hộ, Nhật ký hoạt động.
    - **Coordinator:** Tổng quan điều phối, Phân công & Lộ trình, Quản lý đội, Lịch sử điều phối, Quỹ & ngân sách.
    - **Rescuer:** Tổng quan nhiệm vụ, Nhiệm vụ cứu hộ, Lộ trình di chuyển, Quân số & trang bị, Lịch sử nhiệm vụ.
  - Tích hợp JavaScript tự động nhận diện và gán class `active` cho menu tương ứng với URL và query param hiện hành.

### 2.2. Tân Trang Trang Chủ & Xóa Bỏ Nội Dung Thừa (`templates/index.html`)
- [x] Cắt giảm các khối banner quảng bá chung chung, các khối nội dung thừa gây loãng thông điệp cứu trợ.
- [x] Tập trung trực diện vào **3 khối hành động khẩn cấp sống còn**:
  1. **Nút Gửi Yêu Cầu Cứu Nạn Khẩn Cấp (SOS):** Nổi bật với hiệu ứng cảnh báo, gọi định vị 1 chạm.
  2. **Bản Đồ Cứu Hộ Thời Gian Thực (GIS Live Map):** Theo dõi trực tiếp diễn biến ngập lụt, điểm nguy hiểm và trạm sơ tán.
  3. **Cổng Quyên Góp Cứu Trợ Minh Bạch:** Kết nối trực tiếp lòng hảo tâm đến đồng bào miền Trung.
- [x] Tối ưu hóa bảng tin cộng đồng (Community Feed) và danh sách điểm trú ẩn an toàn.

### 2.3. Cải Tiến Trang Đăng Nhập & Chân Trang Thống Nhất
- [x] **Trang Đăng Nhập (`templates/auth/login.html`):** Thiết kế lại theo dạng thẻ Card hiện đại, căn giữa, bổ sung gợi ý tài khoản mẫu theo từng vai trò giúp thao tác kiểm thử thuận tiện.
- [x] **Chân Trang (`templates/fragments/footer.html`):** Xóa các liên kết thừa thãi, tinh gọn và làm nổi bật các hotline khẩn cấp quốc gia: **112 (Tìm kiếm cứu nạn)**, **114 (Cứu hỏa & Cứu nạn)**, **115 (Cấp cứu y tế)**.
- [x] **Layout Khung Nhìn Thống Nhất (`templates/layout/main.html`):** Bọc layout toàn hệ thống với Sidebar trượt linh hoạt, tương thích trên mọi kích thước màn hình.

### 2.4. Xây Dựng Bộ UI Preview Độc Lập
- [x] Xây dựng thư mục `ui-preview/` gồm:
  - `ui-preview/index.html`: Bản dựng giao diện Portal cứu hộ bằng HTML thuần.
  - `ui-preview/styles.css`: Hệ màu chủ đạo (Navy `#0f2942`, Crimson Red `#dc3545`, Alert Orange `#fd7e14`).
  - `ui-preview/DESIGN.md`: Tài liệu hướng dẫn quy chuẩn thiết kế, bảng màu, typography và spacing.
  - `ui-preview/app.js`: Script tương tác mô phỏng đóng/mở sidebar và chuyển tab.

---

## 3. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)
- [x] Toàn bộ các trang chính được tân trang đồng bộ, xóa sạch các nội dung thừa và các component không phục vụ cứu nạn.
- [x] Sidebar hiển thị chính xác các tính năng theo đúng vai trò người dùng được cấp phép trong Spring Security.
- [x] Giao diện đăng nhập, chân trang, layout dùng chung hoạt động trơn tru không xảy ra lỗi hiển thị.
- [x] Commit tương ứng: `126cc3c: feat(ui): redesign rescue portal and role navigation`.
