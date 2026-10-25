# PHIÊN LÀM VIỆC #01: KHỞI TẠO SPRING BOOT & CẤU HÌNH ĐA DATABASE
> **Trạng thái:** ⏳ Chưa bắt đầu  
> **Ước tính thời gian:** 1 phiên làm việc  
> **Nhánh thực hiện:** `develop`

---

## 1. Mục Tiêu Phiên
- Khởi tạo khung mã nguồn dự án Spring Boot 3 (Maven, Java 17).
- Cấu hình chạy song song **MySQL** và **SQL Server** thông qua Spring Profiles.
- Xây dựng cấu trúc thư mục package chuẩn theo mô hình MVC đa tầng.
- Tạo Base Layout Thymeleaf tích hợp Bootstrap 5 (Header, Navbar, Footer, Fragment).

---

## 2. Danh Sách Công Việc Cần Thực Hiện

### 2.1. File Quản Lý Thư Viện (`pom.xml`)
- Spring Boot Starter Web
- Spring Boot Starter Thymeleaf
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter WebSocket
- Spring Boot Starter Validation
- Lombok
- MySQL Connector/J (`com.mysql:mysql-connector-j`)
- Microsoft JDBC Driver for SQL Server (`com.microsoft.sqlserver:mssql-jdbc`)
- Cloudinary Java SDK (`com.cloudinary:cloudinary-http5`)

### 2.2. Cấu Hình Đa CSDL (`src/main/resources/`)
- `application.yml`: Cấu hình chung (port: 8080, JPA naming, dialect fallback, upload size max 10MB).
- `application-mysql.yml`: DataSource trỏ tới MySQL `localhost:3306/cuunan_db`.
- `application-sqlserver.yml`: DataSource trỏ tới SQL Server `localhost:1433;databaseName=cuunan_db`.

### 2.3. Cấu Trúc Package Java (`vn.edu.cuunan`)
- `config`: `MvcConfig.java`
- `controller`: `HomeController.java`
- `entity`: `BaseEntity.java`
- `repository`
- `service`
- `dto`

### 2.4. Khung Giao Diện Thymeleaf & Bootstrap 5
- `templates/layout/main.html`: Template gốc chứa thẻ `<head>`, link Bootstrap 5 CDN, FontAwesome, Footer.
- `templates/fragments/navbar.html`: Thanh điều hướng phản ứng theo vai trò (Người dân, Điều phối, Đội cứu hộ).
- `templates/index.html`: Trang chủ giới thiệu hệ thống, có nút "GỬI CỨU TRỢ SOS KHẨN CẤP" nổi bật.

---

## 3. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)
- [ ] Lệnh `mvn clean compile` thành công không có lỗi build.
- [ ] Chạy lệnh `mvn spring-boot:run -Dspring-boot.run.profiles=mysql` kết nối được MySQL.
- [ ] Chạy lệnh `mvn spring-boot:run -Dspring-boot.run.profiles=sqlserver` kết nối được SQL Server.
- [ ] Truy cập `http://localhost:8080` hiển thị giao diện trang chủ với thanh Navbar chuẩn Bootstrap 5.
