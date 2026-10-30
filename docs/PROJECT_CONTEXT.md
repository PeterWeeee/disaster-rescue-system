# TÀI LIỆU NGỮ CẢNH DỰ ÁN (PROJECT CONTEXT)
> **Mục đích:** File này cung cấp toàn bộ ngữ cảnh kỹ thuật cốt lõi cho AI và lập trình viên khi bắt đầu một phiên làm việc mới (giúp AI nắm bắt dự án chỉ trong 10 giây mà không cần giải thích lại từ đầu).

---

## 1. Thông Tin Chung
- **Tên đề tài:** Hệ thống website hỗ trợ tìm kiếm cứu nạn thiên tai (CứuNạn 24/7).
- **Môn học:** Lập trình Web (Đồ án Nhóm 04).
- **Mục tiêu:** Xây dựng nền tảng web kết nối người dân vùng thiên tai (bão lũ, sạt lở) với đội cứu hộ và điều phối viên theo thời gian thực (Real-time).
- **Repository GitHub:** `https://github.com/PeterWeeee/disaster-rescue-system.git`
- **Nhánh phát triển chính:** `main` (hoặc `develop`).

---

## 2. Công Nghệ & Kiến Trúc Kỹ Thuật

| Thành phần | Công nghệ lựa chọn | Mục đích / Ghi chú |
| :--- | :--- | :--- |
| **Backend** | Spring Boot 3.x (Java 17+) | Nền tảng chính, Spring MVC, Spring Data JPA |
| **Frontend** | Thymeleaf + Bootstrap 5 | Server-Side Rendering (SSR), Responsive mobile |
| **Bản đồ (GIS)** | Leaflet.js + OpenStreetMap | Hiển thị vị trí cứu nạn, trạm an toàn, tuyến đường |
| **Real-time** | WebSocket (STOMP + SockJS) | Bắn tín hiệu SOS, live dispatching, cập nhật bản đồ |
| **Bảo mật** | Spring Security + JWT | Xác thực Stateless, lưu JWT trong Cookie/Header |
| **Lưu trữ ảnh** | Cloudinary | Upload ảnh hiện trường cứu hộ, avatar người dùng |
| **Cổng Quyên Góp** | VietQR Napas 24/7 + SePay Webhook + VNPay | Tiếp nhận ủng hộ thật (BIDV), tự động cập nhật ngân sách cứu trợ qua Webhook |
| **Cơ sở dữ liệu** | MySQL & SQL Server (Đa CSDL) | Hỗ trợ song song cả 2 hệ quản trị trong nhóm |

---

## 3. Chiến Lược Hỗ Trợ Đa CSDL (MySQL & SQL Server)
Nhóm có thành viên dùng MySQL, có thành viên dùng SQL Server. Hệ thống được cấu hình đa profile:
- `application.yml`: Cấu hình chung (JPA, WebSocket, Cloudinary, JWT, SePay & BIDV Bank info).
- `application-mysql.yml`: `spring.datasource.url=jdbc:mysql://localhost:3306/cuunan_db`
- `application-sqlserver.yml`: `spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=cuunan_db`
- Khi chạy: chỉ cần đổi tham số `spring.profiles.active=mysql` hoặc `sqlserver`.
- **Nguyên tắc JPA:** Sử dụng chuẩn JPA annotations (`GenerationType.IDENTITY`, `@Lob`, `@Enumerated(EnumType.STRING)`), tránh dùng các kiểu dữ liệu đặc thù của riêng một DB để cả 2 hệ quản trị đều tự sinh bảng chuẩn.

---

## 4. Vai Trò Người Dùng (Roles & Permissions)

1. **`ROLE_CITIZEN` (Người dân):**
   - Đăng ký, đăng nhập, quản lý thông tin cá nhân.
   - Gửi yêu cầu cứu nạn khẩn cấp (GPS 1 chạm + số người + mức độ + ảnh).
   - Theo dõi tiến độ cứu nạn của bản thân theo thời gian thực (Live GPS & tuyến OSRM).
   - Quyên góp ủng hộ cứu trợ bão lũ bằng quét mã VietQR Napas 24/7 ngân hàng thật.

2. **`ROLE_RESCUER` (Đội cứu hộ / Tình nguyện viên):**
   - Đội trưởng: Xem nhiệm vụ được điều phối, nhận/từ chối, phân công cho đội viên, quản lý thành viên đội.
   - Đội viên: Nhận việc, cập nhật tiến độ, chia sẻ vị trí GPS thực tế (`watchPosition`), tải ảnh nghiệm thu.

3. **`ROLE_COORDINATOR` (Điều phối viên):**
   - Xem Dashboard toàn cảnh các ca SOS trên bản đồ thời gian thực.
   - Lọc SOS theo mức độ khẩn cấp, khu vực, trạng thái.
   - Phân công ca cứu hộ cho đội gần nhất dựa trên tọa độ.
   - Gửi thông báo/chỉ đạo cho đội cứu hộ.

4. **`ROLE_ADMIN` (Quản trị viên hệ thống):**
   - Toàn quyền của Coordinator.
   - Quản lý tài khoản, phân quyền, quản lý các đội cứu hộ.
   - Quản lý các đợt thiên tai, phát sóng cảnh báo khẩn cấp diện rộng (Broadcast).
   - Xem báo cáo thống kê tổng hợp và quản lý tài chính thu - chi Quỹ cứu trợ.

---

## 5. Quy Ước Code & Làm Việc Với AI
1. **Luôn kiểm tra `docs/HANDOVER_STATUS.md`** trước khi code để biết đang ở bước nào.
2. **Tuân thủ cấu trúc thư mục chuẩn Spring Boot:**
   ```
   src/main/java/vn/edu/cuunan/
   ├── config/          # Cấu hình Security, WebSocket, Cloudinary, Mvc
   ├── controller/      # @Controller (Thymeleaf) & @RestController (API)
   ├── dto/             # Data Transfer Objects (Request/Response)
   ├── entity/          # JPA Entities
   ├── repository/      # Spring Data JPA Repositories
   ├── security/        # JWT Filter, Token Provider, UserDetails
   ├── service/         # Business Logic Interface & Implementation
   └── websocket/       # WebSocket Event Handlers & Message Models
   ```
3. Sau mỗi phiên làm việc, **luôn cập nhật lại `docs/HANDOVER_STATUS.md`** để phiên tiếp theo tiếp tục mượt mà.
