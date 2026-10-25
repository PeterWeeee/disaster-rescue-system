# PHIÊN LÀM VIỆC #07: PHÂN HỆ ĐỘI CỨU HỘ (OPERATIONS & REPORT)
> **Trạng thái:** ⏳ Chưa bắt đầu  
> **Ước tính thời gian:** 1 phiên làm việc  
> **Nhánh thực hiện:** `develop`

---

## 1. Mục Tiêu Phiên
- Xây dựng giao diện và luồng nghiệp vụ dành cho Đội cứu hộ (`ROLE_RESCUER`).
- Đội trưởng nhận nhiệm vụ từ Coordinator (Chấp nhận hoặc Từ chối kèm lý do).
- Cập nhật tiến độ di chuyển và xử lý hiện trường (`IN_PROGRESS` -> `COMPLETED`).
- Tải ảnh bằng chứng nghiệm thu và báo cáo kết quả cứu nạn.

---

## 2. Danh Sách Công Việc Cần Thực Hiện

### 2.1. Nghiệp Vụ Cứu Hộ Backend
- `RescuerService.java`:
  - `acceptAssignment(Long assignmentId)`: Chuyển trạng thái sang `ACCEPTED`, cập nhật trạng thái đội thành `BUSY`.
  - `rejectAssignment(Long assignmentId, String reason)`: Chuyển sang `REJECTED`, trả ca SOS về trạng thái `PENDING` để Coordinator phân công lại cho đội khác.
  - `startMoving(Long assignmentId)`: Đổi trạng thái sang `IN_PROGRESS`.
  - `completeAssignment(Long assignmentId, String reportNotes, MultipartFile reportImage)`: Upload ảnh hoàn thành lên Cloudinary, cập nhật ca SOS thành `RESOLVED`, đưa đội cứu hộ về `AVAILABLE`.

### 2.2. Giao Diện Đội Cứu Hộ (Mobile Friendly)
- `templates/rescuer/tasks.html`:
  - Danh sách nhiệm vụ mới đến: Thẻ Card nổi bật với 2 nút to "✅ Chấp Nhận" / "❌ Từ Chối".
  - Nhiệm vụ đang thực hiện: Hiển thị tên người dân, SĐT (bấm gọi ngay), địa chỉ, chỉ dẫn đường đi trên bản đồ.
- `templates/rescuer/complete-modal.html`:
  - Form kết thúc ca cứu nạn: Nhập ghi chú số người đã cứu an toàn, chụp ảnh nghiệm thu tải lên.

---

## 3. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)
- [ ] Khi được phân công, màn hình Đội trưởng hiện nhiệm vụ mới.
- [ ] Bấm "Chấp nhận" -> Cập nhật trạng thái tức thời.
- [ ] Bấm "Hoàn thành nhiệm vụ" kèm ảnh chụp -> Trạng thái ca SOS chuyển thành `RESOLVED`, marker trên bản đồ chuyển sang màu xanh lá cây, người dân nhận được thông báo cứu nạn thành công.
