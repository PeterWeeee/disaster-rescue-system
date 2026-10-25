# NHẬT KÝ PHIÊN LÀM VIỆC #01: KHỞI TẠO SPRING BOOT & GIAO DIỆN NỀN TẢNG
- **Thời gian:** 25/10/2026
- **Người thực hiện:** AI Assistant & Nhóm 04 (Lập trình Web)
- **Mục tiêu phiên:** Thiết lập mã nguồn Spring Boot 3, cấu hình đa profile (MySQL, SQL Server, H2), tích hợp Cloudinary SDK, xây dựng Base Layout Thymeleaf + Bootstrap 5.

---

### 1. Kết Quả Đạt Được:
1. **Quản lý thư viện (`pom.xml`):**
   - Đã cấu hình Spring Boot Starter Parent 3.3.5 với Java 17.
   - Bổ sung đầy đủ: Web, Thymeleaf, Security, Data JPA, WebSocket (STOMP), Validation, Lombok, JWT 0.12.6, Cloudinary HTTP5 2.5.0, MySQL Connector, SQL Server JDBC, H2 Runtime.
2. **Cấu hình đa CSDL & Cloudinary (`application.yml`):**
   - Cấu hình chung `application.yml` tích hợp định danh Cloudinary:
     - Cloud Name: `odwf5uoi`
     - API Key: `329355988118811`
     - API Secret: `w6nY13mFiftdZCSg6Sqd0GdU4j0`
   - Cấu hình `application-mysql.yml` cho thành viên dùng MySQL.
   - Cấu hình `application-sqlserver.yml` cho thành viên dùng SQL Server.
   - Cấu hình `application-local.yml` (H2 In-Memory) giúp dev/test ngay không cần cài đặt DB daemon.
3. **Mã nguồn Java (`vn.edu.cuunan`):**
   - `DisasterRescueApplication.java`: Main Application.
   - `config/CloudinaryConfig.java`: Khởi tạo Bean Cloudinary dùng credentials của dự án.
   - `config/SecurityConfig.java`: Spring Security 6 cho phép truy cập các trang công cộng (`/`, `/sos`, `/map`, `/auth/**`, `/css/**`, `/js/**`).
   - `config/WebSocketConfig.java`: Cấu hình STOMP broker `/topic` và `/queue`, endpoint `/ws` hỗ trợ SockJS.
   - `config/MvcConfig.java`: WebMvc cấu hình static resources.
   - `entity/BaseEntity.java`: `@MappedSuperclass` với `createdAt`, `updatedAt` tự động.
   - `controller/HomeController.java`: Điều hướng `/`, `/sos`, `/map`.
   - `controller/AuthController.java`: Điều hướng `/auth/login`, `/auth/register`.
   - `dto/SosRequestDto.java`: DTO tiếp nhận dữ liệu SOS từ người dân.
4. **Giao diện người dùng (Thymeleaf + Bootstrap 5.3 + FontAwesome 6):**
   - `templates/layout/main.html`: Template gốc layout SSR.
   - `templates/fragments/navbar.html`: Header cứu nạn với Hotline khẩn cấp (112, 114, 115) & nút SOS phát xung radar.
   - `templates/fragments/footer.html`: Chân trang thông tin khẩn cấp và nhóm đồ án.
   - `templates/index.html`: Trang chủ với Hero banner cứu nạn khẩn cấp, quét GPS 1 chạm, 4 thẻ tính năng và quy trình điều phối 4 bước.
   - `templates/sos/request.html`: Form gửi cứu nạn khẩn cấp kèm định vị GPS tự động và xem trước ảnh tải lên Cloudinary.
   - `templates/map/live-map.html`: Khung hiển thị bản đồ trực tuyến Leaflet.js.
   - `templates/auth/login.html` & `register.html`: Mẫu đăng nhập / đăng ký tài khoản.
   - `static/css/style.css` & `static/js/main.js`: Style hiệu ứng cứu hộ, animation radar, JS bắt GPS chuẩn xác cao (`navigator.geolocation`).
5. **Kiểm thử & Biên dịch:**
   - `mvn clean compile` thành công (11 files biên dịch không lỗi).
   - `mvn test` thành công 100% (1/1 test passed).

---

### 2. Kế Hoạch Cho Phiên Tiếp Theo (Session 02):
- Thiết kế các JPA Entity hoàn chỉnh (`Role`, `User`, `RescueTeam`, `TeamMember`, `SosRequest`, `RescueAssignment`, `DisasterAlert`) dựa theo `docs/DATABASE_SCHEMA.md`.
- Xây dựng hệ thống Spring Security 6 kết hợp JWT Token:
  - Repository & Service cho `User` và `Role`.
  - Đăng ký tài khoản người dân / cứu hộ (mã hóa mật khẩu BCrypt).
  - Đăng nhập, sinh mã JWT và lưu trữ trong Cookie / Authorization Header.
  - Phân quyền theo vai trò (`ROLE_CITIZEN`, `ROLE_RESCUER`, `ROLE_COORDINATOR`, `ROLE_ADMIN`).
