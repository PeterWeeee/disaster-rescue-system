# SESSION 15: SỬA LỖI ĐĂNG TIN CẢNH BÁO CỘNG ĐỒNG & ĐỘNG HÓA CẢNH BÁO KHẨN THIÊN TAI
> **Người phụ trách:** PeterWeeee (Trưởng nhóm / Backend & Real-time Core)  
> **Thời điểm thực hiện:** 29/10/2026  
> **Nhánh Git:** `develop`  
> **Trạng thái:** ✅ ĐÃ HOÀN THÀNH 100% (Build & Test Passed)

---

## 1. Bối Cảnh & Vấn Đề Thực Tế
1. **Lỗi nút "Đăng tin cảnh báo" không phản hồi:**
   - Người dùng bấm nút "Đăng Tin Cảnh Báo" trên banner hoặc bấm vào ô nhập *"Bạn đang thấy hiểm họa gì quanh khu vực? Chia sẻ để cảnh báo..."* nhưng giao diện hoàn toàn không có phản hồi, không bật popup modal đăng tin.
   - Nguyên nhân gốc rễ: File template `feed/index.html` sử dụng Thymeleaf Layout `~{layout/main :: layout(..., ~{::#feed-content})}`. Một thẻ `</div>` thừa ở dòng 333 đã vô tình đóng vùng `#feed-content` sớm. Do đó, toàn bộ modal `#createPostModal`, mã script AJAX gửi bài, xử lý Geolocation và CSS tùy biến nằm ở cuối file bị Thymeleaf loại bỏ hoàn toàn khi kết xuất ra trình duyệt.
2. **Mục "Cảnh báo khẩn mới" ở sidebar phải đang là dữ liệu tĩnh:**
   - Dữ liệu hiển thị 3 mục cảnh báo mẫu (Mưa lớn >200mm, Lũ báo động 3 sông Thu Bồn, Sạt lở đồi) đang bị fix cứng trong file HTML, chưa được liên kết với hệ thống cảnh báo thiên tai của Ban Chỉ Huy / Quản trị viên.
   - Cần chuẩn hóa theo đúng nghiệp vụ cứu trợ: Cảnh báo khẩn cấp toàn dân phải được liên kết động với cơ sở dữ liệu `disaster_alerts`, phân cấp mức độ nguy hiểm (`DANGER_RED`, `WARNING_ORANGE`, `NOTICE_YELLOW`) và cho phép người dân bấm vào xem toàn văn chỉ đạo/biện pháp ứng phó.
3. **Làm rõ cơ chế Avatar / Logo tài khoản:**
   - Hệ thống tối ưu hiệu năng và độ nhẹ bằng cách sử dụng Icon vector FontAwesome trên Navbar và Avatar ký tự (Letter Avatar) trích xuất chữ cái đầu tiên trong họ tên người dùng (`currentUser.fullName`), tránh lỗi gãy ảnh khi chưa upload avatar lên Cloudinary.

---

## 2. Các Công Việc Đã Thực Hiện

### 2.1. Khắc Phục Lỗi Modal Đăng Tin Cảnh Báo
- Loại bỏ thẻ `</div>` đóng sớm tại dòng 333 trong [`feed/index.html`](../../src/main/resources/templates/feed/index.html), đảm bảo `#createPostModal`, thẻ `<script>` và `<style>` nằm trọn vẹn bên trong phạm vi `#feed-content`.
- Bổ sung cơ chế tiền xử lý `FormData` trong hàm JavaScript `submit`: Tự động loại bỏ các trường rỗng (`latitude`, `longitude`, `imageFile`) khi người dân đăng tin nhanh không kèm tọa độ hoặc không kèm ảnh, triệt tiêu lỗi ép kiểu Spring MVC.

### 2.2. Động Hóa Cảnh Báo Khẩn Thiên Tai Chuẩn Nghiệp Vụ
- **Repository:** Bổ sung phương thức `findTop5ByOrderByCreatedAtDesc()` trong [`DisasterAlertRepository.java`](../../src/main/java/vn/edu/cuunan/repository/DisasterAlertRepository.java).
- **Controller:** Tiêm `DisasterAlertRepository` vào [`FeedController.java`](../../src/main/java/vn/edu/cuunan/controller/FeedController.java) và truyền danh sách cảnh báo `recentAlerts` vào Model.
- **View UI/UX:**
  - Hiển thị danh sách cảnh báo khẩn cấp với màu sắc phân cấp chuẩn quốc gia:
    - `DANGER_RED`: Badge đỏ **KHẨN** (`bg-danger`), viền cảnh báo đỏ nhạt.
    - `WARNING_ORANGE`: Badge cam **CAO** (`bg-warning text-dark`), viền cảnh báo vàng nhạt.
    - `NOTICE_YELLOW` / khác: Badge xám **LƯU Ý** (`bg-secondary`), viền trung tính.
  - Hiển thị động địa bàn ảnh hưởng (`affectedRegion`) và mốc thời gian cập nhật.
  - Tích hợp hiệu ứng hover `alert-item-card` và hàm JavaScript `showAlertDetail(el)`: Khi người dùng nhấp vào thẻ cảnh báo, hệ thống mở modal `emergencyBroadcastModal` với toàn bộ nội dung hướng dẫn an toàn và khuyến cáo phòng chống thiên tai.

---

## 3. Các File Đã Can Thiệp
1. `src/main/java/vn/edu/cuunan/repository/DisasterAlertRepository.java` (Thêm query top 5 alert).
2. `src/main/java/vn/edu/cuunan/controller/FeedController.java` (Tiêm repo & nạp `recentAlerts` vào model).
3. `src/main/resources/templates/feed/index.html` (Sửa cấu trúc thẻ div, dọn dẹp formData, render dynamic alerts và modal chi tiết).

---

## 4. Kết Quả Kiểm Thử
- Chạy toàn bộ test `FeedControllerTest`: **5/5 tests PASS (BUILD SUCCESS)**.
- Giao diện Bảng Tin hoạt động mượt mà, phản hồi ngay lập tức khi đăng tin và tương tác cảnh báo.
