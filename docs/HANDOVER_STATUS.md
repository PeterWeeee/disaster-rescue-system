# BẢNG BÀN GIAO & TRẠNG THÁI DỰ ÁN (HANDOVER STATUS)
> **Mục đích:** File này là "bàn giao ca". Khi chuyển đổi thiết bị hoặc chuyển phiên làm việc AI, file này cho biết chính xác dự án đang ở đâu, đã làm được gì, và bước tiếp theo cần làm là gì.

---

## 1. Trạng Thái Hiện Tại (Current Snapshot)
- **Thời điểm cập nhật:** 25/10/2026
- **Nhánh Git hiện tại:** `develop`
- **Giai đoạn (Phase):** Phase 3 - Phân hệ Gửi SOS & Bản đồ tương tác Leaflet.js + GPS 1 chạm (ĐÃ HOÀN THÀNH 100% SESSION 03)
- **Trạng thái tổng quan:** Đã hoàn thành 100% Session 01, Session 02 và Session 03. Đã triển khai hoàn chỉnh dịch vụ cứu nạn SOS, form gửi cứu nạn tương tác GPS 1 chạm, bản đồ Leaflet.js OpenStreetMap kéo thả ghim vị trí, trang tra cứu và theo dõi tiến độ ca cứu hộ (`/sos/track/{id}` & `/sos/track`), REST API `/api/sos`, bản đồ toàn cảnh Live Map (`/map`) đồng bộ dữ liệu thời gian thực. Toàn bộ 33 unit & integration tests đạt **100% BUILD SUCCESS**. Sẵn sàng cho Session 04 (Tích hợp Cloudinary Media Upload ảnh hiện trường).

---

## 2. Những Việc ĐÃ HOÀN THÀNH (Completed)
- [x] Phân tích toàn bộ nghiệp vụ từ tài liệu `Nhom04_LTWEB.docx`.
- [x] Tạo file `.gitignore` tiêu chuẩn cho Java Spring Boot, Maven/Gradle, IDEs và OS.
- [x] Tạo branch `main` và `develop` trên GitHub (`https://github.com/PeterWeeee/disaster-rescue-system.git`).
- [x] Tách lộ trình thành 9 phiên làm việc trong `docs/roadmap/`.
- [x] Thiết lập bảng phân công nhiệm vụ 3 thành viên (`docs/TEAM_WORK_DIVISION.md`) và script chuyển đổi commit tự động (`git-member.ps1`, `commit-as.ps1`).
- [x] **SESSION 01 (PeterWeeee):**
  - [x] Tạo `pom.xml` chuẩn Spring Boot 3.3.5 (Java 17, Web, Thymeleaf, Security, JPA, WebSocket STOMP, Validation, Cloudinary HTTP5, MySQL, SQL Server, H2, JJWT).
  - [x] Cấu hình đa profile (`application.yml`, `application-local.yml`, `application-mysql.yml`, `application-sqlserver.yml`).
  - [x] Cấu hình Cloudinary (sử dụng biến môi trường / `application-secret.yml`) trong `application.yml` và bean `CloudinaryConfig.java`.
  - [x] Base Layout Thymeleaf (`main.html`, `navbar.html`, `footer.html`, `head.html`).
- [x] **SESSION 02 (Võ Văn Thịnh - `vovanthinh2006@gmail.com`):**
  - [x] Xây dựng trọn bộ 7 Entities JPA tương thích đa CSDL: `Role`, `User`, `RescueTeam`, `TeamMember`, `SosRequest`, `RescueAssignment`, `DisasterAlert`.
  - [x] Xây dựng 7 JPA Repositories tương ứng trong `vn.edu.cuunan.repository`.
  - [x] Triển khai bảo mật Spring Security 6 + Stateless JWT qua HttpOnly Cookie (`JwtTokenProvider`, `CustomUserDetails`, `CustomUserDetailsService`, `JwtAuthenticationFilter`, `SecurityConfig`).
  - [x] `DataInitializer.java`: Nạp 4 vai trò và 4 tài khoản mặc định (`admin`, `coordinator`, `rescuer`, `citizen` với pass `123456`).
  - [x] Controllers & DTOs: `AuthController`, `AuthApiController`, `CoordinatorController`, `RescuerController`, `AdminController`, `ProfileController`.
  - [x] Giao diện Thymeleaf hoàn chỉnh: `auth/login.html`, `auth/register.html`, `auth/forbidden.html`.
- [x] **SESSION 03 (Võ Văn Thịnh - `vovanthinh2006@gmail.com`):**
  - [x] Entity & DTO: Hoàn thiện `SosRequest.java` (thêm Builder, JsonIgnoreProperties tránh lỗi ByteBuddy lazy serialization), `SosRequestDto.java` (đầy đủ Jakarta Validation, vĩ độ/kinh độ, số người, người già/trẻ em/bị thương).
  - [x] Repository: Mở rộng `SosRequestRepository.java` thêm tìm kiếm theo SĐT, đếm theo trạng thái.
  - [x] Service: Xây dựng `SosService.java` xử lý toàn bộ logic tiếp nhận SOS (hỗ trợ nặc danh hoặc liên kết tài khoản đã đăng nhập), chuẩn hóa mức độ khẩn cấp, cập nhật trạng thái cứu hộ, đếm ca PENDING.
  - [x] Controllers:
    - `SosController.java`: Render form gửi SOS (`/sos`), tự động điền thông tin tài khoản nếu đã đăng nhập, redirect sang trang theo dõi, trang theo dõi chi tiết (`/sos/track/{id}`), trang tra cứu (`/sos/track`).
    - `SosApiController.java`: REST API `POST /api/sos` nhận JSON, `GET /api/sos/{id}`, `GET /api/sos` lọc hoặc lấy tất cả cho bản đồ Leaflet, `PATCH /api/sos/{id}/status` cập nhật trạng thái.
  - [x] Bản đồ Leaflet.js & GPS 1 chạm:
    - Nhúng Leaflet 1.9.4 CSS/JS vào `head.html`.
    - Form `sos/request.html` & `sos/create.html`: Nút to màu đỏ "📍 LẤY VỊ TRÍ GPS HIỆN TẠI CỦA TÔI" (1 Chạm), bản đồ OpenStreetMap kéo thả ghim vị trí hoặc click trực tiếp tự nhảy tọa độ và cập nhật input.
    - Trang `sos/track.html`: Stepper Timeline trực quan 4 bước (Đã tiếp nhận -> Đã điều phối -> Đang tiếp cận -> Hoàn tất), mini map Leaflet hiển thị marker radar đỏ nhấp nháy, cẩm nang an toàn sinh tồn.
    - Trang `map/live-map.html`: Bản đồ toàn cảnh Leaflet kết nối trực tiếp `/api/sos`, hiển thị tất cả các ca cứu nạn với marker phân màu theo mức độ khẩn cấp, popup chi tiết và danh sách tóm tắt ở sidebar.
  - [x] Dữ liệu mẫu ban đầu: `DataInitializer.java` nạp 3 ca SOS mẫu thực tế tại Đà Nẵng / Miền Trung (Liên Chiểu, Hòa Vang, Hải Châu).
  - [x] Bộ kiểm thử toàn diện: Thêm `SosServiceTest.java` (9 unit tests) và `SosIntegrationTest.java` (11 integration tests). Toàn bộ **33/33 tests** chạy thành công **100% BUILD SUCCESS**.

---

## 3. Những Việc ĐANG LÀM DỞ DANG (In Progress)
- [ ] Không có. Đã hoàn thành 100% Session 03. Chuẩn bị bắt đầu Session 04.

---

## 4. Việc CẦN LÀM TIẾP THEO (Next Steps)
1. **Phiên AI tiếp theo (Session 04 - Phụ trách: Tri Thai / PeterWeeee - `NguyenTT162.4@gmail.com`):**
   - Xây dựng `CloudinaryService.java` tích hợp SDK Cloudinary HTTP5 (`com.cloudinary:cloudinary-http5`).
   - Xây dựng `UploadApiController.java` tiếp nhận tải ảnh hiện trường từ trình duyệt hoặc camera di động.
   - Nén ảnh client-side bằng HTML5 Canvas trước khi tải lên Cloudinary để tối ưu tốc độ trong điều kiện mạng bão lũ chập chờn.
   - Nhúng Cloudinary upload vào form SOS (`/sos`) và trang báo cáo hoàn thành nhiệm vụ của Rescuer.
2. **Phiên AI kế tiếp (Session 05 - Phụ trách: Tri Thai / PeterWeeee):**
   - Triển khai hạ tầng WebSocket STOMP over SockJS (`WebSocketConfig.java`).
   - Kênh `/topic/sos-alerts` phát tín hiệu SOS thời gian thực đến Dashboard của Điều phối viên.
   - Âm thanh còi báo động khẩn cấp và hiệu ứng nhấp nháy radar trên bản đồ Live Map.

---

## 5. Vấn Đề Tồn Đọng & Lưu Ý Kỹ Thuật (Known Issues / Blockers)
- **Cơ sở dữ liệu:** Mặc định profile được đặt là `local` (sử dụng H2 in-memory) để ứng dụng có thể chạy và test ngay lập tức mà không cần bật MySQL hay SQL Server. Khi chạy môi trường thực tế, truyền `-Dspring-boot.run.profiles=mysql` hoặc `-Dspring-boot.run.profiles=sqlserver`.
- **Bảo mật CSRF:** Tuyến đường `/sos/**` và `/api/sos/**` đã được cấu hình bỏ qua kiểm tra CSRF trong `SecurityConfig.java` để đảm bảo người dân vùng lũ trong tình trạng khẩn cấp có thể phát tín hiệu SOS mà không bị rào cản token phiên.
- **Thư mục dự án:** Dự án được phát triển và quản lý duy nhất tại `C:\Users\Will\Projects\disaster-rescue-system`.

---

## 6. Hướng Dẫn Nhanh Cho AI / Lập Trình Viên Mới Tiếp Quản
1. Đọc nhanh file `docs/HANDOVER_STATUS.md` để nắm bắt tiến độ (Session 01, 02, 03 đã hoàn thành 100%).
2. Xem lộ trình chi tiết phiên 04 tại `docs/roadmap/session-04-cloudinary-media.md`.
3. Kiểm tra mã nguồn Cloudinary bean đã khởi tạo tại `src/main/java/vn/edu/cuunan/config/CloudinaryConfig.java`.
4. Sau khi hoàn thành Session 04, cập nhật lại trạng thái trong file này.
