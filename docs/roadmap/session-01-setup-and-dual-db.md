# PHIÊN LÀM VIỆC #01: KHỞI TẠO SPRING BOOT & CẤU HÌNH ĐA DATABASE
> **Trạng thái:** ✅ Đã hoàn thành  
> **Thời điểm hoàn thành:** 25/10/2026  
> **Nhánh thực hiện:** `develop`

---

## 1. Mục Tiêu Phiên
- Khởi tạo khung mã nguồn dự án Spring Boot 3 (Maven, Java 17).
- Cấu hình chạy song song **MySQL** và **SQL Server** thông qua Spring Profiles (+ H2 local profile).
- Xây dựng cấu trúc thư mục package chuẩn theo mô hình MVC đa tầng.
- Tạo Base Layout Thymeleaf tích hợp Bootstrap 5 (Header, Navbar, Footer, Fragment).
- Cấu hình tích hợp SDK Cloudinary với thông tin định danh xác thực thực tế.

---

## 2. Danh Sách Công Việc Đã Thực Hiện

### 2.1. File Quản Lý Thư Viện (`pom.xml`)
- [x] Spring Boot Starter Web (3.3.5)
- [x] Spring Boot Starter Thymeleaf
- [x] Spring Boot Starter Data JPA
- [x] Spring Boot Starter Security + JWT (0.12.6)
- [x] Spring Boot Starter WebSocket (STOMP + SockJS)
- [x] Spring Boot Starter Validation
- [x] Lombok
- [x] MySQL Connector/J (`com.mysql:mysql-connector-j`)
- [x] Microsoft JDBC Driver for SQL Server (`com.microsoft.sqlserver:mssql-jdbc`)
- [x] Cloudinary Java SDK (`com.cloudinary:cloudinary-http5:2.5.0`)
- [x] H2 Database (`com.h2database:h2`) cho môi trường dev local tức thì

### 2.2. Cấu Hình Đa CSDL & Cloudinary (`src/main/resources/`)
- [x] `application.yml`: Cấu hình chung (port: 8080, upload size max 10MB, Cloudinary credentials).
- [x] `application-local.yml`: H2 in-memory DB + H2 Console phục vụ dev/test độc lập.
- [x] `application-mysql.yml`: DataSource trỏ tới MySQL `localhost:3306/cuunan_db`.
- [x] `application-sqlserver.yml`: DataSource trỏ tới SQL Server `localhost:1433;databaseName=cuunan_db`.

### 2.3. Cấu Trúc Package Java (`vn.edu.cuunan`)
- [x] `config`: `MvcConfig.java`, `SecurityConfig.java`, `WebSocketConfig.java`, `CloudinaryConfig.java`
- [x] `controller`: `HomeController.java`, `AuthController.java`
- [x] `entity`: `BaseEntity.java`
- [x] `repository`: package marker sẵn sàng cho Entity Repositories
- [x] `service`: package marker sẵn sàng cho Business Services
- [x] `dto`: `SosRequestDto.java`

### 2.4. Khung Giao Diện Thymeleaf & Bootstrap 5
- [x] `templates/fragments/head.html`: Meta viewport, Bootstrap 5.3 CDN, FontAwesome 6, Google Fonts.
- [x] `templates/fragments/navbar.html`: Emergency hotline topbar (112, 114, 115), SOS button có hiệu ứng radar pulse, liên kết động theo vai trò.
- [x] `templates/fragments/footer.html`: Thông tin liên hệ cứu hộ khẩn cấp, đồ án Lập trình Web Nhóm 04.
- [x] `templates/layout/main.html`: Template gốc layout Thymeleaf SSR.
- [x] `templates/index.html`: Hero section với nút SOS đỏ phát sóng radar, quét GPS 1 chạm, các thẻ tính năng chính và quy trình cứu hộ 4 bước.
- [x] `templates/sos/request.html`: Form gửi yêu cầu cứu nạn khẩn cấp, phân loại mức độ nguy hiểm, tự động lấy GPS và preview ảnh hiện trường.
- [x] `templates/map/live-map.html`: Giao diện bản đồ GIS trực tuyến sẵn sàng cho Leaflet.js.
- [x] `templates/auth/login.html` & `register.html`: Giao diện đăng nhập, đăng ký tài khoản công dân / cứu hộ.
- [x] `static/css/style.css` & `static/js/main.js`: Hệ thống màu cứu nạn, hiệu ứng nhịp tim/radar, tự động nạp tọa độ GPS vào form.

---

## 3. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)
- [x] Lệnh `mvn clean compile` thành công 100% không có lỗi build (11 source files compiled).
- [x] Lệnh `mvn test` thành công (1 test run, 0 failures, 0 errors) kiểm chứng Spring context, JPA, Cloudinary và WebSocket.
- [x] Chạy lệnh `mvn spring-boot:run -Dspring-boot.run.profiles=mysql` kết nối được MySQL khi daemon chạy.
- [x] Chạy lệnh `mvn spring-boot:run -Dspring-boot.run.profiles=sqlserver` kết nối được SQL Server khi daemon chạy.
- [x] Truy cập `http://localhost:8080` hiển thị giao diện trang chủ với thanh Navbar chuẩn Bootstrap 5.
