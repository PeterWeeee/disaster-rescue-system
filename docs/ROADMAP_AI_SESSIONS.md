# KẾ HOẠCH CHIA NHỎ PHIÊN LÀM VIỆC CỦA AI (AI WORK SESSIONS ROADMAP)
> **Mục tiêu:** Tránh việc AI bị "tràn ngữ cảnh" (context window loss) dẫn đến quên code cũ hoặc code chắp vá. Mỗi phiên (Session) chỉ giải quyết trọn vẹn 1 tính năng độc lập, có tiêu chí hoàn thành (Checklist) rõ ràng.

---

```
                       LỘ TRÌNH 9 PHIÊN LÀM VIỆC CỦA AI
┌────────────────────────────────────────────────────────────────────────┐
│ Session 01: Khởi tạo Spring Boot Project & Cấu hình Đa Database        │
│    ↓                                                                   │
│ Session 02: Xác thực & Phân quyền (Spring Security 6 + JWT + Thymeleaf)│
│    ↓                                                                   │
│ Session 03: Phân hệ SOS & Tích hợp Bản đồ Leaflet.js (GPS 1 chạm)      │
│    ↓                                                                   │
│ Session 04: Tích hợp Upload ảnh hiện trường cứu hộ lên Cloudinary      │
│    ↓                                                                   │
│ Session 05: WebSocket Real-time Cốt lõi (Bắn thông báo SOS & Live Map) │
│    ↓                                                                   │
│ Session 06: Phân hệ Điều phối viên (Coordinator Dashboard & Dispatch)   │
│    ↓                                                                   │
│ Session 07: Phân hệ Đội cứu hộ (Rescue Team Operations & Status Update)│
│    ↓                                                                   │
│ Session 08: Phân hệ Quản trị (Admin: Thống kê, User, Broadcast Cảnh báo│
│    ↓                                                                   │
│ Session 09: Tối ưu UI/UX Mobile, Kiểm thử toàn diện & Chuẩn bị Demo    │
└────────────────────────────────────────────────────────────────────────┘
```

---

###  Session 01: Khởi tạo Project & Cấu hình Đa Database
- **Mục tiêu:** Dựng xong khung dự án Spring Boot 3 chạy được trên cả MySQL lẫn SQL Server; giao diện Base Layout Thymeleaf.
- **Nội dung công việc:**
  - Viết `pom.xml` với đầy đủ dependencies (Web, Thymeleaf, Security, Data JPA, WebSocket, Validation, Lombok, MySQL Connector, MSSQL JDBC).
  - Cấu hình đa profile trong `application.yml`, `application-mysql.yml`, `application-sqlserver.yml`.
  - Tạo cấu trúc package chuẩn.
  - Xây dựng Layout Thymeleaf chuẩn với Bootstrap 5 (Header, Navbar, Footer, Flash Messages).
- **Tiêu chí nghiệm thu (Checklist):**
  - [ ] Chạy lệnh `mvn clean spring-boot:run` thành công với profile `mysql`.
  - [ ] Chạy lệnh `mvn clean spring-boot:run` thành công với profile `sqlserver`.
  - [ ] Truy cập `http://localhost:8080/` hiển thị trang chủ Bootstrap giao diện chuẩn.

---

### 🔑 Session 02: Xác thực & Phân quyền (Spring Security 6 + JWT + Cookie)
- **Mục tiêu:** Đăng ký, đăng nhập và phân quyền 4 Roles: `CITIZEN`, `RESCUER`, `COORDINATOR`, `ADMIN`.
- **Nội dung công việc:**
  - Tạo các Entity `User`, `Role`.
  - Cấu hình `SecurityFilterChain`, `JwtUtils`, `JwtAuthenticationFilter`.
  - Lưu JWT token an toàn trong HttpOnly Cookie để dùng mượt mà với cả Thymeleaf SSR và REST API.
  - Trang Đăng ký, Đăng nhập đẹp mắt với Bootstrap 5.
  - Điều hướng sau đăng nhập: Citizen về trang gửi SOS; Coordinator về Dashboard điều phối; Rescuer về trang nhiệm vụ; Admin về trang quản trị.
- **Tiêu chí nghiệm thu (Checklist):**
  - [ ] Đăng ký tài khoản người dân mới thành công.
  - [ ] Đăng nhập thành công, sinh JWT và lưu cookie.
  - [ ] Chặn truy cập trái phép vào các URL quản trị nếu chưa có đúng Role.

---

### 📍 Session 03: Phân hệ Gửi SOS & Bản đồ Leaflet.js
- **Mục tiêu:** Người dân gửi yêu cầu cứu trợ khẩn cấp có tọa độ GPS tự động và xem vị trí trên bản đồ.
- **Nội dung công việc:**
  - Tạo Entity `SosRequest` (người gửi, sđt, địa chỉ, latitude, longitude, mức độ khẩn cấp, số người, mô tả, trạng thái).
  - Giao diện gửi SOS: Nút "Lấy vị trí hiện tại" tự động đọc toạ độ trình duyệt qua HTML5 Geolocation API.
  - Tích hợp thư viện Leaflet.js + OpenStreetMap: Kéo thả ghim marker chọn vị trí chính xác.
  - Trang cá nhân của người dân: Theo dõi lịch sử và trạng thái yêu cầu của mình.
- **Tiêu chí nghiệm thu (Checklist):**
  - [ ] Bấm nút "Lấy vị trí", form tự điền latitude/longitude.
  - [ ] Lưu yêu cầu SOS thành công vào cơ sở dữ liệu.
  - [ ] Bản đồ hiển thị marker chính xác tại vị trí đã gửi.

---

### ☁️ Session 04: Upload Ảnh Hiện Trường Lên Cloudinary
- **Mục tiêu:** Cho phép đính kèm ảnh khi gửi SOS và ảnh báo cáo cứu nạn qua dịch vụ lưu trữ đám mây Cloudinary.
- **Nội dung công việc:**
  - Tích hợp Cloudinary SDK vào Spring Boot (`CloudinaryConfig`).
  - Viết `CloudinaryService` xử lý upload multipart file, nén ảnh và trả về secure URL.
  - Bổ sung trường `imageUrl` vào `SosRequest` và `media_files`.
  - Xem trước ảnh (preview) trực tiếp trên giao diện trước khi bấm gửi.
- **Tiêu chí nghiệm thu (Checklist):**
  - [ ] Chọn ảnh chụp từ điện thoại/máy tính -> Upload trực tiếp lên Cloudinary.
  - [ ] URL ảnh được lưu trong CSDL và hiển thị ảnh phóng to trong modal chi tiết.

---

### ⚡ Session 05: WebSocket Real-time (Bắn SOS & Live Dispatch)
- **Mục tiêu:** Đẩy dữ liệu thời gian thực không cần tải lại trang (Trọng tâm kỹ thuật đồ án).
- **Nội dung công việc:**
  - Cấu hình Spring WebSocket STOMP Broker (`WebSocketConfig` với endpoint `/ws`).
  - Kênh `/topic/sos-alerts`: Khi có người gửi SOS mới, bắn tín hiệu ngay lập tức đến Coordinator và Rescuer.
  - Giao diện nhận WebSocket: Chuông báo âm thanh cảnh báo + Pop-up Toast góc màn hình + Tự động thêm điểm đỏ nhấp nháy trên bản đồ Leaflet.
  - Kênh `/topic/map-updates`: Đồng bộ hóa trạng thái marker (ví dụ: chuyển từ Đỏ sang Vàng khi có đội nhận).
- **Tiêu chí nghiệm thu (Checklist):**
  - [ ] Mở 2 tab trình duyệt: Tab 1 (Người dân gửi SOS) -> Tab 2 (Coordinator) ngay lập tức hiện thông báo và nổ chuông mà không cần F5.

---

### 📋 Session 06: Phân hệ Điều phối viên (Coordinator Dashboard)
- **Mục tiêu:** Ban chỉ huy theo dõi toàn cảnh, phân công đội cứu hộ gần nhất.
- **Nội dung công việc:**
  - Dashboard điều phối: Bản đồ trung tâm toàn màn hình + Danh sách SOS chờ xử lý bên thanh sidebar.
  - Bộ lọc SOS theo: Mức độ khẩn cấp, quận/huyện, trạng thái.
  - Thuật toán tính khoảng cách (Haversine Formula) giữa vị trí SOS và các Đội cứu hộ để gợi ý đội gần nhất.
  - Chức năng Gán đội cứu hộ (`RescueAssignment`).
- **Tiêu chí nghiệm thu (Checklist):**
  - [ ] Điều phối viên click vào 1 ca SOS -> Hệ thống tự động liệt kê các đội cứu hộ sắp xếp từ gần đến xa.
  - [ ] Bấm nút "Phân công" -> WebSocket gửi thông báo nhiệm vụ riêng (`/user/queue/assignments`) đến Đội trưởng đội đó.

---

### 🚑 Session 07: Phân hệ Đội cứu hộ (Rescue Team)
- **Mục tiêu:** Đội cứu hộ nhận việc, cập nhật tiến độ, chia sẻ vị trí thực tế.
- **Nội dung công việc:**
  - Màn hình dành cho Đội trưởng: Xem nhiệm vụ đến, bấm "Chấp nhận" hoặc "Từ chối".
  - Quản lý thành viên trong đội (Bác sĩ, Lái xuồng, Cứu hộ viên).
  - Cập nhật tiến độ: *Đang di chuyển -> Đã tiếp cận hiện trường -> Đã cứu nạn an toàn*.
  - Upload ảnh hiện trường làm bằng chứng hoàn thành nhiệm vụ.
- **Tiêu chí nghiệm thu (Checklist):**
  - [ ] Đội cứu hộ cập nhật trạng thái "Đã an toàn" -> Trạng thái của người dân tự chuyển xanh theo thời gian thực.

---

### 🛡️ Session 08: Phân hệ Admin (Quản trị & Cảnh báo thiên tai)
- **Mục tiêu:** Quản trị toàn diện hệ thống và phát cảnh báo thiên tai diện rộng.
- **Nội dung công việc:**
  - Quản lý người dùng: Xem, khóa/mở khóa tài khoản, đổi vai trò.
  - Quản lý thiên tai & vùng nguy hiểm: Khai báo bão, vùng sạt lở (vẽ polygon vùng nguy hiểm lên bản đồ).
  - Chức năng Cảnh báo khẩn cấp diện rộng (Broadcast Alert): Admin phát tin khẩn -> Đè thông báo màu đỏ lên toàn bộ màn hình người dùng đang truy cập web qua WebSocket.
  - Báo cáo thống kê: Biểu đồ số ca cứu hộ thành công, số người được hỗ trợ theo từng ngày/khu vực (dùng Chart.js).
- **Tiêu chí nghiệm thu (Checklist):**
  - [ ] Phát thử cảnh báo khẩn cấp -> Mọi trình duyệt đang mở đều nhận được popup cảnh báo.
  - [ ] Thống kê hiển thị số liệu trực quan dạng biểu đồ cột và tròn.

---

### 🎯 Session 09: Tối ưu UI/UX Mobile, Kiểm thử & Kịch bản Bảo vệ
- **Mục tiêu:** Đảm bảo hệ thống mượt mà trên điện thoại di động và sẵn sàng kịch bản demo thuyết trình đạt điểm tối đa.
- **Nội dung công việc:**
  - Kiểm tra và tinh chỉnh Responsive trên các kích thước màn hình điện thoại (Bootstrap).
  - Tạo dữ liệu mẫu (Seed Data) chuẩn: 5 đội cứu hộ, 10 ca SOS mẫu ở các quận huyện khác nhau.
  - Chuẩn bị sẵn kịch bản demo 2-3 thiết bị (Điện thoại gửi SOS - Máy tính Admin điều phối - Máy tính Rescuer nhận việc).
  - Rà soát tài liệu báo cáo và hoàn thiện README.
- **Tiêu chí nghiệm thu (Checklist):**
  - [ ] Test toàn bộ quy trình từ lúc gửi SOS đến khi hoàn thành cứu nạn không có lỗi Console/Exception.
  - [ ] Giao diện di động dễ bấm, trực quan.
