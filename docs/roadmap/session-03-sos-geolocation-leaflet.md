# PHIÊN LÀM VIỆC #03: PHÂN HỆ GỬI SOS & BẢN ĐỒ LEAFLET.JS
> **Trạng thái:** ✅ Đã hoàn thành (100%)  
> **Người thực hiện:** Võ Văn Thịnh (`vovanthinh2006@gmail.com`)  
> **Thời điểm hoàn thành:** 25/10/2026  
> **Nhánh thực hiện:** `develop`

---

## 1. Mục Tiêu Phiên
- Xây dựng chức năng gửi yêu cầu cứu nạn khẩn cấp (cho phép cả người dân đăng nhập hoặc gửi nặc danh nhanh).
- Tích hợp HTML5 Geolocation API để tự động lấy tọa độ GPS chính xác (1 chạm).
- Nhúng bản đồ tương tác Leaflet.js + OpenStreetMap để người dùng kéo thả ghim vị trí hoặc click trực tiếp trên bản đồ.
- Trang theo dõi tiến độ ca cứu hộ dành cho người dân (`/sos/track/{id}` và `/sos/track`).
- Cung cấp REST API đầy đủ (`POST /api/sos`, `GET /api/sos/{id}`, `GET /api/sos`, `PATCH /api/sos/{id}/status`).
- Tích hợp bản đồ trực tuyến Live Map hiển thị toàn bộ điểm kêu cứu thời gian thực.

---

## 2. Danh Sách Công Việc Cần Thực Hiện

### 2.1. Entity & Repository
- [x] `SosRequest.java`: `id`, `contactName`, `contactPhone`, `secondaryPhone`, `latitude`, `longitude`, `addressDetail`, `urgencyLevel` (`CRITICAL`, `HIGH`, `MEDIUM`, `LOW`), `peopleCount`, `hasElderly`, `hasChildren`, `hasInjured`, `description`, `status` (`PENDING`, `DISPATCHED`, `IN_PROGRESS`, `RESOLVED`, `CANCELLED`), `resolvedAt`, `createdAt`, `updatedAt`, Builder pattern.
- [x] `SosRequestRepository.java`: Tìm kiếm theo trạng thái, theo người gửi, theo SĐT, đếm số lượng ca cứu nạn theo trạng thái.

### 2.2. Service & Controller
- [x] `SosService.java`: Logic tiếp nhận & lưu yêu cầu cứu nạn (hỗ trợ nặc danh hoặc liên kết tài khoản), kiểm tra hợp lệ tọa độ [-90..90, -180..180], cập nhật trạng thái cứu hộ, đếm ca PENDING.
- [x] `SosController.java`: Render form gửi SOS (`/sos`), tự động điền thông tin nếu đã đăng nhập, xử lý gửi cứu nạn, redirect sang trang theo dõi, trang theo dõi chi tiết (`/sos/track/{id}`), trang tra cứu theo SĐT hoặc mã số (`/sos/track`).
- [x] `SosApiController.java`: REST API `POST /api/sos` nhận JSON DTO, `GET /api/sos/{id}`, `GET /api/sos` lọc theo status hoặc lấy tất cả cho Leaflet map, `PATCH /api/sos/{id}/status` cập nhật trạng thái.

### 2.3. Frontend & Bản đồ Leaflet
- [x] `templates/sos/request.html` & `templates/sos/create.html`:
  - Nút to màu đỏ: "📍 LẤY VỊ TRÍ GPS HIỆN TẠI CỦA TÔI" (1 Chạm).
  - Thẻ `div#sosMap` hiển thị bản đồ Leaflet + OpenStreetMap tiles. Marker kéo thả `draggable: true` và click trực tiếp trên bản đồ tự động nhảy marker và cập nhật 2 ô input `latitude`, `longitude` (làm tròn 6 chữ số thập phân).
  - Nhập tọa độ tay tự động di chuyển marker trên bản đồ.
  - Form chọn mức độ khẩn cấp (CRITICAL, HIGH, MEDIUM, LOW), số lượng người, tích chọn ưu tiên: Người già / Trẻ nhỏ / Bị thương.
- [x] `templates/sos/track.html`:
  - Thanh tra cứu mã ca SOS (`#SOS-XXX`) hoặc SĐT người liên hệ.
  - Stepper Timeline trực quan 4 bước: 1. Đã tiếp nhận -> 2. Đã điều phối -> 3. Đang tiếp cận -> 4. Hoàn tất cứu nạn.
  - Bản đồ Leaflet mini hiển thị vị trí vệ tinh hiện trường ca SOS với marker radar đỏ nhấp nháy và vòng tròn cảnh báo.
  - Cẩm nang an toàn sinh tồn trong khi chờ đội cứu hộ tiếp cận.
- [x] `templates/map/live-map.html`:
  - Bản đồ toàn cảnh Leaflet tải động toàn bộ marker các ca SOS từ `/api/sos`.
  - Phân loại màu marker theo mức độ nguy hiểm (Đỏ, Cam, Xanh dương, Xanh lá).
  - Popup xem nhanh thông tin ca cứu hộ và nút bấm sang trang theo dõi chi tiết.

---

## 3. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)
- [x] Bấm nút "Lấy vị trí" -> Trình duyệt hỏi quyền GPS và tự điền tọa độ chính xác vào form.
- [x] Kéo thả marker trên bản đồ Leaflet -> Tọa độ trong ô input tự động cập nhật theo.
- [x] Bấm gửi SOS -> Lưu thành công vào CSDL, chuyển hướng đến trang theo dõi tiến độ.
- [x] Toàn bộ 33/33 unit & integration tests đạt BUILD SUCCESS 100%.
