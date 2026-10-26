# PHIÊN LÀM VIỆC #06: PHÂN HỆ ĐIỀU PHỐI VIÊN (DASHBOARD & DISPATCH)
> **Trạng thái:** ✅ ĐÃ HOÀN THÀNH (100%)  
> **Người phụ trách:** **Sinh viên MSSV 24110329 (`24110329@student.hcmute.edu.vn`)**  
> **Thời điểm hoàn thành:** 26/10/2026  
> **Nhánh thực hiện:** `develop`

---

## 1. Mục Tiêu Phiên
- Xây dựng Trung tâm chỉ huy (Dashboard) dành riêng cho Điều phối viên (`ROLE_COORDINATOR`, `ROLE_ADMIN`).
- Tích hợp bản đồ toàn cảnh Leaflet.js, danh sách ca SOS chờ xử lý với bộ lọc nâng cao.
- Thuật toán tính khoảng cách địa lý (Haversine Formula) gợi ý đội cứu hộ gần nhất và thực hiện phân công nhiệm vụ.

---

## 2. Danh Sách Công Việc Đã Thực Hiện

### 2.1. Entities, DTO & Repositories
- [x] Sử dụng `RescueTeam.java`, `RescueAssignment.java`, `SosRequest.java`.
- [x] Tạo `RescueTeamDistanceDto.java` (thông tin đội kèm số km khoảng cách Haversine).
- [x] Tạo `AssignTaskRequestDto.java` (tiếp nhận mã ca SOS, mã đội và ghi chú chỉ đạo).
- [x] Mở rộng `RescueTeamRepository.java`, `RescueAssignmentRepository.java`, `SosRequestRepository.java`.

### 2.2. Thuật Toán Haversine & Service Điều Phối
- [x] `GeoUtils.java`:
  - Hàm tính khoảng cách giữa 2 tọa độ theo công thức Haversine:
    $$d = 2R \cdot \arcsin\left(\sqrt{\sin^2\left(\frac{\Delta \text{lat}}{2}\right) + \cos(\text{lat}_1)\cos(\text{lat}_2)\sin^2\left(\frac{\Delta \text{lng}}{2}\right)}\right)$$
- [x] `CoordinatorService.java`:
  - Thống kê Dashboard (số ca chờ, đang xử lý, đã giải cứu, số đội sẵn sàng).
  - Lấy danh sách các đội cứu hộ đang trực ban, tính khoảng cách đến ca SOS, sắp xếp tăng dần từ gần nhất đến xa nhất.
  - Phân công nhiệm vụ cứu nạn: tạo bản ghi `RescueAssignment`, chuyển trạng thái SOS sang `DISPATCHED`, cập nhật trạng thái đội sang `BUSY`, bắn thông điệp WebSocket `/topic/map-updates`.
- [x] `CoordinatorApiController.java`: REST API `/api/coordinator/**` (`stats`, `sos-list`, `nearest-teams`, `assign`, `map-data`).

### 2.3. Giao Diện Coordinator Dashboard
- [x] `templates/coordinator/dashboard.html`:
  - Bố cục Split-Screen chuyên nghiệp:
    - Cột trái (40%): Danh sách các ca SOS (Tab lọc: Tất cả, Chờ phân công, Đã điều phối, Đang tiếp cận, Hoàn thành).
    - Cột phải (60%): Bản đồ Leaflet phóng to toàn cảnh, hiển thị vị trí của tất cả ca SOS và tất cả Đội cứu hộ.
  - Modal phân công nhiệm vụ (`#dispatchModal`): Gọi API Haversine lấy danh sách đội cứu hộ kèm số km khoảng cách, hiển thị badge trực quan, nhập ghi chú chỉ đạo và bấm nút điều động.

---

## 3. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)
- [x] Trang Dashboard tải mượt mà toàn bộ điểm SOS và các trạm cứu hộ lên bản đồ Leaflet.
- [x] Bấm vào 1 ca SOS -> Modal hiển thị danh sách đội cứu hộ được sắp xếp theo thứ tự gần nhất (hiện rõ số km).
- [x] Bấm nút "Điều động" -> Bản ghi `RescueAssignment` được tạo trong CSDL, trạng thái ca SOS đổi thành `DISPATCHED`.
- [x] Toàn bộ test suite `CoordinatorServiceTest` đạt 100% BUILD SUCCESS.
