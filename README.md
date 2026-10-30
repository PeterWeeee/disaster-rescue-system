# 🚨 CứuNạn 24/7 - Hệ Thống Phản Ứng Khẩn Cấp & Cứu Hộ Thiên Tai
### Disaster Rescue & Relief Management System

[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)](https://github.com/PeterWeeee/disaster-rescue-system)
[![Tests](https://img.shields.io/badge/Tests-97%2F97%20Passed-success.svg)](https://github.com/PeterWeeee/disaster-rescue-system)
[![Java](https://img.shields.io/badge/Java-17%20%7C%2021%20%7C%2025-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-blue.svg)](https://spring.io/projects/spring-boot)
[![Security](https://img.shields.io/badge/Security-Spring%20Security%206%20%2B%20JWT-red.svg)](https://spring.io/projects/spring-security)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

---

## 📖 Tổng Quan Dự Án

**CứuNạn 24/7** là nền tảng công nghệ phản ứng khẩn cấp và điều phối cứu hộ thiên tai, được xây dựng nhằm hỗ trợ công tác cứu nạn, cứu trợ đồng bào bị ảnh hưởng bởi bão lũ, sạt lở và ngập lụt tại các địa bàn trọng điểm (đặc biệt là khu vực miền Trung Việt Nam).

Hệ thống kết nối trực tiếp **3 đối tượng nòng cốt** trong chuỗi ứng phó khẩn cấp:
- **Người dân:** Gửi tín hiệu SOS định vị GPS chính xác chỉ với 1 chạm, theo dõi tiến trình ứng cứu theo thời gian thực, cập nhật bản đồ trú ẩn và đóng góp nguồn lực qua cổng quyên góp minh bạch.
- **Trung tâm điều phối:** Giám sát toàn cảnh bản đồ tình huống (Incident Heatmap), tính toán khoảng cách tự động qua thuật toán Haversine và định tuyến OSRM để phân bổ đội cứu hộ gần nhất, kiểm duyệt thông tin cứu trợ và cấp phát ngân sách.
- **Đội cứu hộ hiện trường:** Tiếp nhận điều động tức thì qua kênh WebSocket hai chiều, di chuyển theo lộ trình tối ưu tránh vùng chia cắt, nghiệm thu ca cứu nạn bằng hình ảnh hiện trường và báo cáo tiêu hao trang thiết bị.

---

## ✨ Tính Năng Nổi Bật

- **🚨 SOS Khẩn Cấp & Định Vị Đa Kênh:** Gửi yêu cầu cứu nạn tức thì với toạ độ GPS, phân loại mức độ khẩn cấp (người già, trẻ nhỏ, người bị thương), hỗ trợ cả người dùng đăng nhập lẫn khách vãng lai.
- **🗺️ Bản Đồ Cứu Hộ GIS Thời Gian Thực:** Tích hợp Leaflet 1.9.4 & OpenStreetMap hiển thị vùng ngập lụt, trạm cứu hộ, điểm sơ tán an toàn và vị trí di chuyển thực của các đội cứu hộ.
- **⚡ Điều Phối Thông Minh & Định Tuyến Tự Động:**
  - Ứng dụng công thức **Haversine** để tìm kiếm và xếp hạng các đội cứu hộ có bán kính gần nhất.
  - Tích hợp **OSRM Routing Engine** để vẽ đường đi ngắn nhất từ vị trí đội cứu hộ đến vị trí người bị nạn.
- **📡 Truyền Tin Thời Gian Thực (WebSocket STOMP):** Đẩy thông báo khẩn, cập nhật trạng thái nhiệm vụ cứu hộ và vị trí GPS tức thời mà không cần reload trang.
- **📰 Bảng Tin Cộng Đồng & Xác Thực Tin Tức:** Kênh chia sẻ thông tin cứu trợ, cảnh báo mưa lũ, tích hợp cơ chế kiểm duyệt (Verified/Rejected) nhằm ngăn chặn tin giả và thông tin sai lệch trong thiên tai.
- **💳 Cổng Quyên Góp & Quản Lý Quỹ Minh Bạch:** Tích hợp VNPay Sandbox (HMAC-SHA512), MoMo QR và VietQR chuyển khoản 24/7, tự động cập nhật số dư quỹ cứu trợ và công khai sao kê chi viện.
- **🛡️ Bảo Mật & Xác Thực An Toàn:** Kiến trúc bảo mật Spring Security 6, JWT Token bảo vệ bằng HttpOnly Cookie chống XSS/CSRF, mã hóa BCrypt và xác thực OTP qua Email.

---

## 🛠️ Công Nghệ Sử Dụng

### Backend & Kiến Trúc Hệ Thống
- **Ngôn ngữ & Framework:** Java (hỗ trợ JDK 17, 21, 25), Spring Boot 3.3.5.
- **Persistence & ORM:** Spring Data JPA, Hibernate ORM 6.5.
- **Bảo Mật:** Spring Security 6, Stateless JWT (JSON Web Token), Password Hashing (BCrypt), Email OTP Service.
- **Thời Gian Thực (Real-time):** Spring WebSocket với giao thức STOMP qua SockJS fallback.
- **Tính Toán & Giải Thuật Địa Lý:** Haversine Great-Circle Distance, GeoJSON coordinates.

### Cơ Sở Dữ Liệu & Lưu Trữ Đám Mây
- **Cơ sở dữ liệu:** Hỗ trợ kiến trúc đa hệ quản trị cơ sở dữ liệu:
  - **MySQL 8.0:** Môi trường Production & Local Dev chính.
  - **Microsoft SQL Server:** Hỗ trợ tương thích môi trường doanh nghiệp.
  - **H2 In-Memory Database:** Chạy tức thì cho môi trường phát triển nhanh và chạy test tự động.
- **Lưu trữ đa phương tiện:** Cloudinary Media Storage API (upload & quản lý ảnh hiện trường cứu nạn).

### Frontend & Giao Diện Người Dùng
- **Template Engine:** Thymeleaf 3 với layout component tái sử dụng.
- **UI Framework & Styling:** Bootstrap 5.3, Bootstrap Icons, Font Awesome 6.
- **Bản Đồ Số:** Leaflet.js 1.9.4, OpenStreetMap Tile Layer, Leaflet Routing Machine.
- **Biểu Đồ & Thống Kê:** Chart.js phục vụ dashboard phân tích dữ liệu cứu trợ.

### Cổng Thanh Toán & Tích Hợp Thứ Ba
- **VNPay Sandbox:** Thanh toán quyên góp với chữ ký số bảo mật SHA512.
- **MoMo Payment Gateway:** Cổng thanh toán ví điện tử MoMo QR.
- **VietQR Napas 24/7:** Tạo mã QR ngân hàng động theo chuẩn VietQR.

---

## 🚀 Hướng Dẫn Cài Đặt & Khởi Chạy

### 1. Yêu Cầu Môi Trường
- **JDK:** Java 17 trở lên (khuyến nghị Java 17, 21 hoặc 25).
- **Maven:** Apache Maven 3.8+ (hoặc dùng Maven Wrapper đi kèm).
- **Trình duyệt:** Chrome, Edge, Firefox phiên bản hiện đại.

### 2. Khởi Chạy Nhanh (Với H2 In-Memory)
Dự án được cấu hình sẵn profile `local` sử dụng H2 Database, tự động tạo cấu trúc bảng và nạp đầy đủ dữ liệu mẫu để thử nghiệm ngay lập tức mà không cần cài đặt CSDL bên ngoài:

```bash
# 1. Clone mã nguồn
git clone https://github.com/PeterWeeee/disaster-rescue-system.git
cd disaster-rescue-system

# 2. Chuyển sang nhánh develop
git checkout develop

# 3. Chạy ứng dụng với profile local
mvn clean spring-boot:run -Dspring-boot.run.profiles=local
```

Sau khi ứng dụng khởi động thành công, truy cập hệ thống tại:  
👉 **`http://localhost:8080`**

### 3. Chạy Với Cơ Sở Dữ Liệu MySQL (Production/Staging)
1. Tạo database MySQL:
   ```sql
   CREATE DATABASE cuunan_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Cấu hình thông tin kết nối trong `src/main/resources/application-mysql.yml` hoặc truyền biến môi trường:
   ```bash
   mvn clean spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

### 4. Kiểm Thử Tự Động (Unit & Integration Tests)
Chạy toàn bộ 97 bài kiểm thử tích hợp (Bảo mật, Điều phối, SOS, Feed, Quyên góp):
```bash
mvn test
```

---

## 🔑 Tài Khoản Trải Nghiệm Mẫu

Hệ thống được cấu hình sẵn các vai trò mẫu với mật khẩu mặc định là **`123456`**:

| Tên Đăng Nhập | Mật Khẩu | Vai Trò (Role) | Chức Năng Trải Nghiệm Chính |
| :--- | :---: | :--- | :--- |
| `admin` | `123456` | `ROLE_ADMIN` | Quản trị toàn hệ thống, Thống kê biểu đồ, Phát sóng cảnh báo thiên tai diện rộng |
| `coordinator` | `123456` | `ROLE_COORDINATOR` | Trung tâm điều phối, Tìm đội cứu hộ gần nhất (Haversine), Điều phối xe & xuồng, Duyệt cấp quỹ |
| `rescuer` | `123456` | `ROLE_RESCUER` | Đội trưởng cứu hộ: Tiếp nhận lệnh điều động, Xem lộ trình OSRM, Nghiệm thu cứu nạn bằng ảnh |
| `citizen` | `123456` | `ROLE_CITIZEN` | Người dân: Gửi SOS khẩn cấp, Theo dõi đội cứu hộ, Đăng bài bảng tin, Xem cẩm nang, Quyên góp cứu trợ |

---

## 📁 Tài Liệu Kỹ Thuật

- [Tài Liệu Đặc Tả API & WebSocket](docs/API_AND_WEBSOCKET_SPECS.md)
- [Thiết Kế Cơ Sở Dữ Liệu (Schema)](docs/DATABASE_SCHEMA.md)
- [Hướng Dẫn Cài Đặt Cơ Sở Dữ Liệu](docs/DATABASE_SETUP.md)
- [Danh Sách Tài Khoản Kiểm Thử Chi Tiết](docs/TEST_ACCOUNTS.md)
- [Báo Cáo Hoàn Thiện Tính Năng Hệ Thống](docs/BAO_CAO_DANH_GIA_MUC_DO_HOAN_THANH.md)

---

*© 2026 CứuNạn 24/7 - Disaster Rescue & Relief Management System.*
