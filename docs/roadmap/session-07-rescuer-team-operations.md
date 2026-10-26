# PHIÊN LÀM VIỆC #07: PHÂN HỆ ĐỘI CỨU HỘ (OPERATIONS & REPORT)
> **Trạng thái:** ✅ ĐÃ HOÀN THÀNH (100%)  
> **Người phụ trách:** **Sinh viên MSSV 24110329 (`24110329@student.hcmute.edu.vn`)**  
> **Thời điểm hoàn thành:** 26/10/2026  
> **Nhánh thực hiện:** `develop`

---

## 1. Mục Tiêu Phiên
- Xây dựng giao diện và luồng nghiệp vụ dành cho Đội cứu hộ (`ROLE_RESCUER`).
- Đội trưởng/đội viên nhận nhiệm vụ từ Coordinator (Chấp nhận hoặc Từ chối kèm lý do).
- Cập nhật tiến độ di chuyển hiện trường (`IN_PROGRESS` -> `COMPLETED`).
- Nghiệm thu kết quả giải cứu và tải ảnh minh chứng thực địa lên Cloudinary / Bộ nhớ máy chủ.

---

## 2. Danh Sách Công Việc Đã Thực Hiện

### 2.1. Nghiệp Vụ Cứu Hộ Backend
- [x] Tạo `RejectAssignmentDto.java` tiếp nhận lý do từ chối nhiệm vụ.
- [x] `RescuerService.java`:
  - `getMyTeam(User user)`: Tự động xác định đội của người dùng đang đăng nhập (Leader hoặc thành viên).
  - `getMyAssignments(User user)`: Lấy toàn bộ nhiệm vụ được phân công cho đội.
  - `acceptAssignment(Long assignmentId, User user)`: Chuyển trạng thái sang `ACCEPTED`, cập nhật trạng thái đội thành `BUSY`, ca SOS sang `IN_PROGRESS`.
  - `rejectAssignment(Long assignmentId, User user, String reason)`: Chuyển sang `REJECTED`, tự động trả ca SOS về trạng thái `PENDING` để Coordinator phân công lại cho đội khác.
  - `startMoving(Long assignmentId, User user)`: Đổi trạng thái sang `IN_PROGRESS`.
  - `completeAssignment(Long assignmentId, User user, String reportNotes, MultipartFile reportImage)`: Upload ảnh nghiệm thu lên Cloudinary (hoặc Fallback local qua `CloudinaryService`), cập nhật ca SOS thành `RESOLVED`, đưa đội cứu hộ về `AVAILABLE`.
- [x] `RescuerApiController.java`: REST API `/api/rescuer/**` (`my-team`, `assignments`, `accept`, `reject`, `start`, `complete`).

### 2.2. Giao Diện Đội Cứu Hộ (Mobile Friendly)
- [x] `templates/rescuer/missions.html`:
  - Thẻ thông tin Đội cứu hộ (Tên đội, Đội trưởng, Quân số, Trang thiết bị, Trạng thái trực ban).
  - 3 Tabs công việc rõ ràng:
    - **🟡 Mới Đến Chờ Nhận (`PENDING`):** Nút to "Tiếp nhận" và "Từ chối" (mở modal nhập lý do).
    - **🔵 Đang Thực Hiện (`ACCEPTED`, `IN_PROGRESS`):** Nút gọi điện thoại nhanh (`tel:...`), nút mở chỉ đường Google Maps, nút "Đang tiếp cận", nút "Báo Cáo Hoàn Thành".
    - **🟢 Lịch Sử Hoàn Thành (`COMPLETED`, `REJECTED`):** Xem ghi chú nghiệm thu và xem ảnh chụp hiện trường.
  - Modal nghiệm thu kết thúc ca cứu nạn: Nhập báo cáo kết quả, đính kèm ảnh chụp hiện trường.

---

## 3. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)
- [x] Khi được phân công, màn hình Đội trưởng hiện nhiệm vụ mới.
- [x] Bấm "Tiếp nhận" -> Cập nhật trạng thái tức thời.
- [x] Bấm "Từ chối" -> Ca SOS quay về PENDING trên bàn điều phối.
- [x] Bấm "Hoàn tất ca cứu hộ" kèm ảnh chụp -> Trạng thái ca SOS chuyển thành `RESOLVED`, đội quay về `AVAILABLE`.
- [x] Bộ kiểm thử tự động `RescuerServiceTest` đạt 100% BUILD SUCCESS.
