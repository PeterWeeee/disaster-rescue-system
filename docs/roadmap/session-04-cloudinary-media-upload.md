# PHIÊN LÀM VIỆC #04: TÍCH HỢP UPLOAD ẢNH HIỆN TRƯỜNG LÊN CLOUDINARY
> **Trạng thái:** ✅ ĐÃ HOÀN THÀNH (100%)  
> **Người thực hiện:** Tri Thai / PeterWeeee (`NguyenTT162.4@gmail.com`)  
> **Thời điểm hoàn thành:** 25/10/2026  
> **Nhánh thực hiện:** `develop`

---

## 1. Mục Tiêu Phiên
- Tích hợp dịch vụ lưu trữ đám mây Cloudinary để xử lý tải ảnh hiện trường khi người dân gửi SOS và ảnh nghiệm thu khi đội cứu hộ hoàn thành nhiệm vụ.
- Hỗ trợ fallback thông minh lưu ảnh cục bộ (Local Storage qua `/uploads/**`) nếu chưa cấu hình Cloudinary credentials để không gián đoạn việc code/test.

---

## 2. Danh Sách Công Việc Đã Thực Hiện

### 2.1. Cấu Hình Cloudinary SDK & Web Mvc
- [x] `CloudinaryConfig.java`: Khởi tạo Bean `Cloudinary` an toàn, đọc cấu hình từ `application.yml` (`cloud-name`, `api-key`, `api-secret`).
- [x] Cấu hình kích thước upload trong `application.yml`: `spring.servlet.multipart.max-file-size=10MB`, `max-request-size=10MB`.
- [x] `MvcConfig.java`: Đăng ký ResourceHandler `/uploads/**` ánh xạ tới thư mục lưu trữ cục bộ `uploads/` trên đĩa cứng.
- [x] `SecurityConfig.java`: Cấu hình mở quyền truy cập `permitAll()` cho `/uploads/**` và `/api/upload/**`.

### 2.2. Service & File Upload Logic
- [x] `CloudinaryService.java`:
  - Phương thức `uploadImage(MultipartFile file, String folderName)`: Kiểm tra định dạng ảnh (JPG, PNG, WEBP, GIF), giới hạn kích thước 10MB.
  - Tải lên Cloudinary trong thư mục `cuunan247/{folder}` và trả về HTTPS secure URL.
  - Cơ chế tự động Fallback sang Local Storage (`/uploads/{folder}/...`) nếu chưa cấu hình Cloudinary hoặc khi mất kết nối mạng.
- [x] `UploadApiController.java`: REST API `POST /api/upload/image` tiếp nhận upload từ client/mobile trả về JSON URL.
- [x] `MediaFile.java` & `MediaFileRepository.java`: Thực thể JPA ghi nhận lịch sử các file ảnh tải lên kèm metadata (`fileSize`, `contentType`, `storageType`, `publicId`).

### 2.3. Cải Tiến Form Gửi SOS & Giao Diện Theo Dõi
- [x] `sos/request.html`:
  - Thêm thẻ `enctype="multipart/form-data"` cho form.
  - Mục 6: Khung chọn ảnh hiện trường kéo thả / bấm chọn từ camera, hiển thị xem trước thumbnail tức thì và nút hủy chọn ảnh.
- [x] `sos/track.html`:
  - Hiển thị ảnh hiện trường thu nhỏ (nếu có đính kèm).
  - Modal phóng to xem ảnh kích thước lớn (`#imageZoomModal`) và nút mở trong tab mới.
- [x] `map/live-map.html`:
  - Nhúng thumbnail ảnh hiện trường trực tiếp trong popup marker Leaflet để Điều phối viên xem nhanh tình hình ngập lụt.

---

## 3. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)
- [x] Chọn file ảnh (JPG/PNG/WEBP) từ điện thoại/máy tính gửi kèm Form SOS `/sos`.
- [x] Ảnh được tải lên Cloudinary hoặc Local Storage và lưu đường dẫn vào `sos_requests.image_url`.
- [x] Link ảnh hiển thị mượt mà trên trang theo dõi tiến độ `/sos/track/{id}` và bản đồ Live Map `/map`.
- [x] Bộ kiểm thử tự động toàn diện: Thêm `CloudinaryServiceTest` và `UploadApiControllerTest`. Toàn bộ **39/39 tests PASS 100% BUILD SUCCESS**.
