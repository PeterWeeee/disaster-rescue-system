# PHIÊN LÀM VIỆC #03: PHÂN HỆ GỬI SOS & BẢN ĐỒ LEAFLET.JS
> **Trạng thái:** ⏳ Chưa bắt đầu  
> **Ước tính thời gian:** 1 phiên làm việc  
> **Nhánh thực hiện:** `develop`

---

## 1. Mục Tiêu Phiên
- Xây dựng chức năng gửi yêu cầu cứu nạn khẩn cấp (cho phép cả người dân đăng nhập hoặc gửi nặc danh nhanh).
- Tích hợp HTML5 Geolocation API để tự động lấy tọa độ GPS chính xác.
- Nhúng bản đồ tương tác Leaflet.js + OpenStreetMap để người dùng kéo thả ghim vị trí hoặc xem vị trí của mình.
- Trang theo dõi tiến độ ca cứu hộ dành cho người dân.

---

## 2. Danh Sách Công Việc Cần Thực Hiện

### 2.1. Entity & Repository
- `SosRequest.java`: `id`, `contactName`, `contactPhone`, `secondaryPhone`, `latitude`, `longitude`, `addressDetail`, `urgencyLevel` (`CRITICAL`, `HIGH`, `MEDIUM`, `LOW`), `peopleCount`, `hasElderly`, `hasChildren`, `hasInjured`, `description`, `status` (`PENDING`, `DISPATCHED`, `IN_PROGRESS`, `RESOLVED`, `CANCELLED`), `createdAt`.
- `SosRequestRepository.java`: Tìm kiếm theo trạng thái, theo người gửi, sắp xếp theo mức độ ưu tiên.

### 2.2. Service & Controller
- `SosService.java`: Logic lưu yêu cầu cứu nạn, kiểm tra hợp lệ tọa độ, cập nhật trạng thái.
- `SosController.java`: Render form gửi SOS và trang chi tiết theo dõi.
- `SosApiController.java`: Endpoint `POST /api/sos` nhận dữ liệu form.

### 2.3. Frontend & Bản đồ Leaflet
- `templates/sos/create.html`:
  - Nút to màu đỏ: "📍 LẤY VỊ TRÍ GPS HIỆN TẠI CỦA TÔI".
  - Thẻ `div#map` hiển thị bản đồ Leaflet. Khi click vào bản đồ, marker tự nhảy và cập nhật input `latitude`, `longitude`.
  - Form nhập số lượng người, tích chọn "Có người già / trẻ em / người bị thương".
- `templates/sos/track.html`:
  - Timeline hiển thị trạng thái xử lý: Đã tiếp nhận -> Đang điều phối -> Đội cứu hộ đang tới -> Hoàn tất.

---

## 3. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)
- [ ] Bấm nút "Lấy vị trí" -> Trình duyệt hỏi quyền GPS và tự điền tọa độ chính xác vào form.
- [ ] Kéo thả marker trên bản đồ Leaflet -> Tọa độ trong ô input tự động cập nhật theo.
- [ ] Bấm gửi SOS -> Lưu thành công vào CSDL, chuyển hướng đến trang theo dõi tiến độ.
