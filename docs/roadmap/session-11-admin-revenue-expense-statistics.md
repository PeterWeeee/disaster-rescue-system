# PHIÊN LÀM VIỆC #11: BỔ SUNG CHỨC NĂNG THỐNG KÊ THU - CHI DÀNH CHO QUẢN TRỊ VIÊN (ADMIN REVENUE & EXPENDITURE STATISTICS)
> **Trạng thái:** ✅ ĐÃ HOÀN THÀNH (100%)  
> **Người phụ trách:** **Tri Thai (PeterWeeee - `NguyenTT162.4@gmail.com`)**  
> **Thời điểm hoàn thành:** 28/10/2026  
> **Nhánh thực hiện:** `ui-fixes`

---

## 1. Mục Tiêu Phiên
- **Bổ sung phân hệ Báo cáo & Thống kê Thu - Chi minh bạch:** Cung cấp cho Quản trị viên (`ROLE_ADMIN`) cái nhìn tổng thể và tức thời về toàn bộ dòng tiền của chiến dịch cứu trợ thiên tai miền Trung.
- **Quản lý dòng tiền Thu:** Theo dõi chi tiết số tiền quyên góp từ đồng bào và các nhà hảo tâm trên cả nước thông qua các cổng thanh toán điện tử (VNPay Sandbox, MoMo, VietQR Napas 24/7).
- **Quản lý dòng tiền Chi:** Theo dõi các khoản phê duyệt giải ngân, tiếp tế kinh phí xăng dầu, xuồng máy, áo phao và nhu yếu phẩm cứu nạn cho các Đội cứu hộ đang tác nghiệp ngoài hiện trường.
- **Biểu diễn số liệu tài chính trực quan:** Sử dụng Chart.js để vẽ biểu đồ so sánh tương quan Thu - Chi và cơ cấu tỷ trọng nguồn thu theo từng phương thức thanh toán.
- **Tra cứu giao dịch dòng tiền kèm phân trang:** Bảng kê chi tiết các giao dịch Thu và Chi có hỗ trợ lọc và phân trang dữ liệu (Pagination).

---

## 2. Danh Sách Công Việc Đã Thực Hiện

### 2.1. Nâng Cấp Backend Controller & Tích Hợp Dữ Liệu Tài Chính
- [x] **Cập nhật `vn.edu.cuunan.controller.AdminController.java`:**
  - Tiêm phụ thuộc (Dependency Injection) `BudgetService` và `DonationService`.
  - Bổ sung định tuyến `GET /admin/dashboard?view=finance` và alias route `GET /admin/finance`.
  - Cung cấp toàn bộ dữ liệu tài chính vào Thymeleaf Model:
    - `fundSummary`: Dữ liệu `GeneralFundSummaryDto` (Vốn ban đầu, Tổng thu quyên góp, Tổng chi giải ngân, Số dư khả dụng, Số yêu cầu chờ duyệt).
    - `allDonations` & `recentDonations`: Danh sách các khoản tiền ủng hộ thành công kèm thông tin nhà hảo tâm và mã giao dịch.
    - `fundRequests`: Danh sách các lệnh chi tiếp tế ngân sách cho các đội cứu hộ.
    - `donationStats`: Số liệu thống kê phân tích phương thức ủng hộ và phân bố vùng miền.

### 2.2. Xây Dựng Màn Hình Thống Kê Thu - Chi Trên Admin Dashboard (`templates/admin/dashboard.html`)
- [x] **Hệ thống 4 Thẻ KPI Tài Chính Nổi Bật:**
  1. **Tổng Quỹ Cứu Trợ (Gross Fund):** Tổng hợp từ vốn cơ sở ban đầu (500 triệu đồng) và toàn bộ tiền đóng góp từ cộng đồng.
  2. **Tổng Chi Giải Ngân (Disbursed Funds):** Tổng số tiền đã được điều phối viên duyệt cấp phát cho các đội cứu hộ.
  3. **Số Dư Khả Dụng (Available Fund):** Ngân sách còn lại sẵn sàng giải ngân tức thì cho các tình huống ngập lụt khẩn cấp.
  4. **Tỷ Lệ Giải Ngân & Số Lượt Đóng Góp:** Tỷ lệ phần trăm giải ngân ngân sách và tổng số giao dịch quyên góp thành công.
- [x] **Tích Hợp Biểu Đồ Tài Chính Trực Quan (Chart.js):**
  - **Biểu đồ Cột (Bar Chart - Thu vs Chi):** So sánh trực quan giữa Tổng nguồn thu và Tổng ngân sách đã chi ra.
  - **Biểu đồ Tròn (Doughnut Chart - Phương Thức Quyên Góp):** Thống kê tỷ lệ đóng góp qua cổng VNPay, Ví điện tử MoMo và Chuyển khoản VietQR.
- [x] **Bảng Kê Dòng Tiền Thu (Quyên Góp Từ Nhà Hảo Tâm):**
  - Hiển thị: Mã giao dịch, Tên nhà hảo tâm, Số điện thoại/Email, Số tiền ủng hộ, Cổng thanh toán, Tỉnh cứu trợ chỉ định, Thời gian giao dịch.
  - Tích hợp thanh phân trang Client-side mượt mà (`#donationsPaginationNav`).
- [x] **Bảng Kê Dòng Tiền Chi (Giải Ngân Tiếp Tế Đội Cứu Hộ):**
  - Hiển thị: Mã phiếu chi, Đội cứu hộ tiếp nhận, Số tiền yêu cầu, Số tiền phê duyệt, Hạng mục/Mục đích giải ngân, Trạng thái phê duyệt, Ngày giải ngân.
  - Tích hợp thanh phân trang Client-side mượt mà (`#disbursementsPaginationNav`).

### 2.3. Cập Nhật Thanh Điều Hướng Sidebar (`templates/fragments/sidebar.html`)
- [x] Bổ sung liên kết menu chuyên biệt dành cho Admin:
  - `<a th:href="@{/admin/dashboard(view='finance')}" class="sidebar-link"><i class="fa-solid fa-chart-pie"></i><span>Thống kê thu chi</span></a>`
  - Đảm bảo script highlight link tự động nhận diện tab `view=finance` để kích hoạt trạng thái active.

### 2.4. Kiểm Thử Hệ Thống (Automated Testing)
- [x] Mở rộng bộ kiểm thử tích hợp `CoordinatorControllerTest.java` (hoặc `AdminControllerTest`):
  - Kiểm thử render thành công trang `/admin/dashboard?view=finance` với quyền `ROLE_ADMIN` trả về mã HTTP 200 OK.
  - Xác thực giao diện chứa đầy đủ các bảng dữ liệu: Bảng thống kê thu, Bảng thống kê chi và các thẻ KPI tài chính.

---

## 3. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)
- [x] Quản trị viên (Admin) đăng nhập và chọn menu "Thống kê thu chi" hiển thị đầy đủ, chính xác toàn bộ số liệu thu từ quyên góp và chi cho đội cứu hộ.
- [x] Các biểu đồ Chart.js thể hiện trực quan cán cân thu - chi và phân bổ phương thức thanh toán.
- [x] Cả hai bảng dữ liệu Thu và Chi đều có phân trang rõ ràng, định dạng tiền tệ VNĐ chuẩn xác.
- [x] Toàn bộ bộ kiểm thử tự động đạt 100% BUILD SUCCESS.
