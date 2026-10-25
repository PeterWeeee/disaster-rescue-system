# PHIÊN LÀM VIỆC #06: PHÂN HỆ ĐIỀU PHỐI VIÊN (DASHBOARD & DISPATCH)
> **Trạng thái:** ⏳ Chưa bắt đầu  
> **Ước tính thời gian:** 1 phiên làm việc  
> **Nhánh thực hiện:** `develop`

---

## 1. Mục Tiêu Phiên
- Xây dựng Trung tâm chỉ huy (Dashboard) dành riêng cho Điều phối viên (`ROLE_COORDINATOR`).
- Tích hợp bản đồ toàn cảnh, danh sách ca SOS chờ xử lý với bộ lọc nâng cao.
- Thuật toán tính khoảng cách (Haversine Formula) gợi ý đội cứu hộ gần nhất và thực hiện phân công nhiệm vụ.

---

## 2. Danh Sách Công Việc Cần Thực Hiện

### 2.1. Entities & Repository
- `RescueTeam.java`: `id`, `teamName`, `leader` (`User`), `contactPhone`, `currentLatitude`, `currentLongitude`, `memberCount`, `equipmentNotes`, `status` (`AVAILABLE`, `BUSY`, `OFFLINE`).
- `RescueAssignment.java`: `id`, `sosRequest`, `rescueTeam`, `assignedBy`, `status` (`PENDING`, `ACCEPTED`, `REJECTED`, `IN_PROGRESS`, `COMPLETED`), `assignedAt`.
- `RescueTeamRepository.java`, `RescueAssignmentRepository.java`.

### 2.2. Thuật Toán Gợi Ý Đội Cứu Hộ Gần Nhất
- `GeoUtils.java`:
  - Hàm tính khoảng cách giữa 2 tọa độ theo công thức Haversine:
    $$d = 2R \cdot \arcsin\left(\sqrt{\sin^2\left(\frac{\Delta \text{lat}}{2}\right) + \cos(\text{lat}_1)\cos(\text{lat}_2)\sin^2\left(\frac{\Delta \text{lng}}{2}\right)}\right)$$
- `CoordinatorService.java`:
  - Lấy danh sách các đội cứu hộ đang `AVAILABLE`, tính khoảng cách đến ca SOS, sắp xếp tăng dần từ gần nhất đến xa nhất.

### 2.3. Giao Diện Coordinator Dashboard
- `templates/coordinator/dashboard.html`:
  - Bố cục 2 cột (Split Screen):
    - Cột trái (35%): Danh sách các ca SOS (Tab: Chưa phân công, Đang xử lý, Đã hoàn thành).
    - Cột phải (65%): Bản đồ Leaflet phóng to toàn cảnh, hiển thị vị trí của tất cả ca SOS và tất cả Đội cứu hộ.
  - Modal phân công nhiệm vụ: Khi chọn 1 ca SOS, mở danh sách các đội cứu hộ kèm số km khoảng cách, bấm nút "Phân công đội này".
  - Bắn thông báo STOMP đến `/user/queue/assignments` của đội trưởng đội được giao.

---

## 3. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)
- [ ] Trang Dashboard load mượt mà toàn bộ điểm SOS và các trạm cứu hộ lên bản đồ.
- [ ] Bấm vào 1 ca SOS -> Modal hiển thị danh sách đội cứu hộ được sắp xếp theo thứ tự gần nhất (hiện rõ số km).
- [ ] Bấm nút "Phân công" -> Bản ghi `RescueAssignment` được tạo trong CSDL, trạng thái ca SOS đổi thành `DISPATCHED`.
