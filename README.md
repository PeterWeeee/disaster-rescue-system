# 🚨 CứuNạn 24/7 - Hệ Thống Phản Ứng Khẩn Cấp & Cứu Hộ Thiên Tai
### Disaster Rescue & Relief Management System

[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)](https://github.com/PeterWeeee/disaster-rescue-system)
[![Tests](https://img.shields.io/badge/Tests-97%2F97%20Passed-success.svg)](https://github.com/PeterWeeee/disaster-rescue-system)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-blue.svg)](https://spring.io/projects/spring-boot)
[![Security](https://img.shields.io/badge/Security-Spring%20Security%206%20%2B%20JWT-red.svg)](https://spring.io/projects/spring-security)
[![HCMUTE](https://img.shields.io/badge/HCMUTE-Web%20Programming-orange.svg)](https://hcmute.edu.vn)

---

## 📖 Giới Thiệu Dự Án

**CứuNạn 24/7** là đồ án môn học **Lập trình Web (HCMUTE - Nhóm 04)**, xây dựng hệ thống nền tảng phục vụ công tác cứu hộ, cứu nạn và điều phối hỗ trợ đồng bào bị ảnh hưởng bởi thiên tai, bão lũ lụt tại miền Trung Việt Nam.

Hệ thống được thiết kế dựa trên sự ánh xạ tương đương từ Barem chuẩn sàn Thương mại Điện tử đa người bán (**Multi-Vendor E-Commerce**), ứng dụng các công nghệ hiện đại bao gồm Bản đồ số **GIS Leaflet 1.9.4**, thuật toán định tuyến **OSRM**, giải thuật khoảng cách **Haversine Formula**, hạ tầng truyền thông thời gian thực **WebSocket STOMP**, xác thực **Email OTP**, và cổng thanh toán quyên góp **VNPay / MoMo / VietQR**.

> 📄 **Xem Báo Cáo Đánh Giá Chi Tiết Theo Barem HCMUTE:**  
> 👉 [**docs/BAO_CAO_DANH_GIA_MUC_DO_HOAN_THANH.md**](docs/BAO_CAO_DANH_GIA_MUC_DO_HOAN_THANH.md)

---

## 👥 Danh Sách Nhóm Thực Hiện (Nhóm 04)

| STT | Họ và tên | Mã số SV | Vai trò | Email GitHub | Mức độ đóng góp |
| :---: | :--- | :---: | :--- | :--- | :---: |
| **1** | **Tri Thai (PeterWeeee)** | *Trưởng nhóm* | Backend Core & Real-time Lead | `NguyenTT162.4@gmail.com` | **100%** |
| **2** | **Võ Văn Thịnh** | **24110329** | Fullstack & Security Specialist | `vovanthinh2006@gmail.com` | **100%** |
| **3** | **Sinh viên phụ trách Điều phối & Admin** | *Thành viên* | GIS & Operations Specialist | `24110329@student.hcmute.edu.vn` | **100%** |

---

## 🛠️ Công Nghệ Nền Tảng

- **Backend:** Java 17/25, Spring Boot 3.3.5, Spring Data JPA, Hibernate ORM 6.5.
- **Security:** Spring Security 6, Stateless JWT Token lưu trong HttpOnly Cookie, BCrypt Password Encoder, Email OTP verification.
- **Frontend & UI:** Thymeleaf 3, Bootstrap 5.3, FontAwesome 6, Bootstrap Icons.
- **Bản Đồ & Định Vị GIS:** Leaflet.js 1.9.4, OpenStreetMap, OSRM Routing Engine, Haversine Distance Formula.
- **Thời Gian Thực (Real-time):** Spring WebSocket, STOMP Protocol over SockJS.
- **Đa Phương Tiện & Đám Mây:** Cloudinary Media Storage (Upload ảnh hiện trường & nghiệm thu cứu nạn).
- **Cơ Sở Dữ Liệu:** Kiến trúc Dual Database hỗ trợ đồng thời MySQL 8.0, Microsoft SQL Server và H2 In-Memory (profile `local` phục vụ dev & test tự động).
- **Cổng Quyên Góp:** VNPay Sandbox (HMAC-SHA512), MoMo QR, VietQR Napas 24/7 tự động.

---

## 🚀 Hướng Dẫn Chạy Nhanh

### 1. Yêu cầu môi trường
- Java JDK 17 trở lên (hoặc Java 21/25).
- Apache Maven 3.8+.
- (Tùy chọn) MySQL 8.0 hoặc SQL Server nếu không dùng H2 in-memory.

### 2. Chạy ứng dụng với H2 in-memory (Mặc định cho kiểm thử)
```bash
git clone https://github.com/PeterWeeee/disaster-rescue-system.git
cd disaster-rescue-system
git checkout develop
mvn clean spring-boot:run -Dspring-boot.run.profiles=local
```
Ứng dụng sẽ tự động seed sẵn dữ liệu mẫu và chạy tại: `http://localhost:8080`

### 3. Chạy toàn bộ 97 Unit & Integration Tests
```bash
mvn test
```

---

## 🔑 Tài Khoản Trải Nghiệm Mẫu

| Tài khoản | Mật khẩu | Quyền hạn (Role) | Chức năng kiểm thử chính |
| :--- | :---: | :--- | :--- |
| `admin` | `123456` | `ROLE_ADMIN` | Quản trị toàn hệ thống, Thống kê Chart.js, Phát sóng cảnh báo khẩn cấp |
| `coordinator` | `123456` | `ROLE_COORDINATOR` | TT Điều phối, Haversine gợi ý trạm, Quản lý đội, Lộ trình, Duyệt cấp Quỹ 500tr |
| `rescuer` | `123456` | `ROLE_RESCUER` | Đội trưởng Hải Châu: Nhận lệnh, Lộ trình OSRM, Nghiệm thu ảnh, Xin tiếp tế ngân sách |
| `citizen` | `123456` | `ROLE_CITIZEN` | Người dân: Gửi SOS 1 chạm, Theo dõi vị trí đội cứu hộ, Quyên góp VNPay/MoMo/VietQR |

---

## 📁 Cấu Trúc Tài Liệu Chi Tiết

- [Báo Cáo Đánh Giá Hoàn Thành Theo Barem HCMUTE](docs/BAO_CAO_DANH_GIA_MUC_DO_HOAN_THANH.md)
- [Bảng Phân Chia Công Việc Nhóm 04](docs/TEAM_WORK_DIVISION.md)
- [Tài Liệu Đặc Tả API & WebSocket](docs/API_AND_WEBSOCKET_SPECS.md)
- [Thiết Kế Cơ Sở Dữ Liệu (Schema)](docs/DATABASE_SCHEMA.md)
- [Hướng Dẫn Cài Đặt Cơ Sở Dữ Liệu](docs/DATABASE_SETUP.md)
- [Danh Sách Tài Khoản Kiểm Thử](docs/TEST_ACCOUNTS.md)

---
*© 2026 Nhóm 04 - Đồ án Lập trình Web - Trường Đại học Sư phạm Kỹ thuật TP.HCM (HCMUTE).*
