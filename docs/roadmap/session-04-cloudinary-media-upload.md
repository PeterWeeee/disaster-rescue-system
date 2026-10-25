# PHIÊN LÀM VIỆC #04: TÍCH HỢP UPLOAD ẢNH HIỆN TRƯỜNG LÊN CLOUDINARY
> **Trạng thái:** ⏳ Chưa bắt đầu  
> **Ước tính thời gian:** 1 phiên làm việc  
> **Nhánh thực hiện:** `develop`

---

## 1. Mục Tiêu Phiên
- Tích hợp dịch vụ lưu trữ đám mây Cloudinary để xử lý tải ảnh hiện trường khi người dân gửi SOS và ảnh nghiệm thu khi đội cứu hộ hoàn thành nhiệm vụ.
- Hỗ trợ fallback lưu ảnh cục bộ (Local Storage) nếu chưa cấu hình Cloudinary credentials để không gián đoạn việc code.

---

## 2. Danh Sách Công Việc Cần Thực Hiện

### 2.1. Cấu Hình Cloudinary SDK
- `CloudinaryConfig.java`: Khởi tạo Bean `Cloudinary` đọc cấu hình từ file môi trường hoặc `application.yml` (`cloud_name`, `api_key`, `api_secret`).
- Bổ sung cấu hình kích thước file upload trong `application.yml`: `spring.servlet.multipart.max-file-size=10MB`.

### 2.2. Service & File Upload Logic
- `CloudinaryService.java`:
  - Phương thức `uploadImage(MultipartFile file, String folderName)` -> Nén ảnh, tải lên Cloudinary, trả về secure URL (HTTPS).
  - Bắt lỗi khi ảnh sai định dạng hoặc mất kết nối mạng.
- Bổ sung trường `imageUrl` vào `SosRequest.java`.
- Tạo Entity `MediaFile.java` (lưu vết nhiều ảnh hiện trường nếu cần).

### 2.3. Cải Tiến Form Gửi SOS
- Bổ sung input file chọn ảnh kèm preview thumbnail bằng JavaScript trước khi upload.
- Modal phóng to xem ảnh kích thước lớn trong trang chi tiết yêu cầu cứu hộ.

---

## 3. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)
- [ ] Chọn 1 file ảnh (JPG/PNG) từ điện thoại/máy tính và gửi kèm form SOS.
- [ ] Ảnh được upload thành công lên Cloudinary (có link dạng `https://res.cloudinary.com/...`).
- [ ] Link ảnh lưu đúng vào bảng `sos_requests` trong CSDL và hiển thị mượt mà trên web.
