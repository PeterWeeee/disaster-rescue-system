# BẢNG BÀN GIAO & TRẠNG THÁI DỰ ÁN (HANDOVER STATUS)
> **Mục đích:** File này là "bàn giao ca". Khi chuyển đổi thiết bị hoặc chuyển phiên làm việc AI, file này cho biết chính xác dự án đang ở đâu, đã làm được gì, và bước tiếp theo cần làm là gì.

---

## 1. Trạng Thái Hiện Tại (Current Snapshot)
- **Thời điểm cập nhật:** 25/10/2026
- **Nhánh Git hiện tại:** `develop`
- **Giai đoạn (Phase):** Phase 1 - Khởi tạo mã nguồn Spring Boot 3, Đa CSDL & Base Layout (ĐÃ HOÀN THÀNH SESSION 01)
- **Trạng thái tổng quan:** Đã hoàn thành toàn bộ mã nguồn nền tảng Spring Boot 3.3.5, cấu hình đa profile (MySQL, SQL Server, H2 local), tích hợp Cloudinary SDK với tài khoản thực tế của người dùng, hoàn thiện Base Layout Thymeleaf + Bootstrap 5. Biên dịch `mvn clean compile` và chạy kiểm thử `mvn test` thành công 100%. Đã đồng bộ mã nguồn sang cả 2 thư mục làm việc trên máy (`c:\ute\WEB programming\Group` và `C:\Users\Will\Projects\disaster-rescue-system`). Sẵn sàng cho Session 02.

---

## 2. Những Việc ĐÃ HOÀN THÀNH (Completed)
- [x] Phân tích toàn bộ nghiệp vụ từ tài liệu `Nhom04_LTWEB.docx`.
- [x] Tạo file `.gitignore` tiêu chuẩn cho Java Spring Boot, Maven/Gradle, IDEs và OS.
- [x] Tạo branch `main` và `develop` trên GitHub (`https://github.com/PeterWeeee/disaster-rescue-system.git`).
- [x] Tách lộ trình thành 9 phiên làm việc trong `docs/roadmap/`.
- [x] **SESSION 01:**
  - [x] Tạo `pom.xml` chuẩn Spring Boot 3.3.5 (Java 17, Web, Thymeleaf, Security, JPA, WebSocket STOMP, Validation, Lombok, Cloudinary HTTP5, MySQL, SQL Server, H2, JJWT).
  - [x] Cấu hình đa profile (`application.yml`, `application-local.yml`, `application-mysql.yml`, `application-sqlserver.yml`).
  - [x] Cấu hình Cloudinary (`odwf5uoi`, `329355988118811`, `w6nY13mFiftdZCSg6Sqd0GdU4j0`) trong `application.yml` và bean `CloudinaryConfig.java`.
  - [x] Cấu hình `SecurityConfig.java`, `WebSocketConfig.java`, `MvcConfig.java`.
  - [x] Tạo `BaseEntity.java`, `HomeController.java`, `AuthController.java`, `SosRequestDto.java`.
  - [x] Xây dựng trọn bộ Thymeleaf layout (`main.html`, `navbar.html`, `footer.html`, `head.html`).
  - [x] Thiết kế giao diện chuyên nghiệp: `index.html` (Hero SOS, GPS 1 chạm, quy trình cứu nạn), `sos/request.html` (Form SOS khẩn cấp + GPS auto-fill + photo preview), `map/live-map.html`, `auth/login.html`, `auth/register.html`.
  - [x] Custom CSS/JS (`style.css`, `main.js`) cho hiệu ứng nhịp tim radar khẩn cấp và định vị GPS độ chính xác cao.
  - [x] Biên dịch `mvn clean compile` thành công (11 files java) và chạy `mvn test` thành công.

---

## 3. Những Việc ĐANG LÀM DỞ DANG (In Progress)
- [ ] Chuẩn bị bắt đầu Session 02: Xây dựng Entity toàn diện và hệ thống Xác thực Spring Security 6 + JWT.

---

## 4. Việc CẦN LÀM TIẾP THEO (Next Steps)
1. **Phiên AI tiếp theo (Session 02):**
   - Tạo toàn bộ Entity JPA tương thích đa CSDL (`Role`, `User`, `RescueTeam`, `TeamMember`, `SosRequest`, `RescueAssignment`, `DisasterAlert`).
   - Xây dựng hệ thống bảo mật Spring Security 6 + JWT:
     - `UserRepository`, `RoleRepository`.
     - `CustomUserDetailsService`, `JwtTokenProvider`, `JwtAuthenticationFilter`.
     - API và Controller đăng ký tài khoản (mã hóa BCrypt), đăng nhập (cấp phát JWT lưu Cookie & Header).
     - Gắn phân quyền các vai trò: `ROLE_CITIZEN`, `ROLE_RESCUER`, `ROLE_COORDINATOR`, `ROLE_ADMIN`.
2. **Phiên AI kế tiếp (Session 03):**
   - Xây dựng chức năng gửi SOS lưu vào CSDL, upload ảnh lên Cloudinary và hiển thị Marker trên bản đồ Leaflet.js.

---

## 5. Vấn Đề Tồn Đọng & Lưu Ý Kỹ Thuật (Known Issues / Blockers)
- **Cơ sở dữ liệu:** Mặc định profile được đặt là `local` (sử dụng H2 in-memory) để ứng dụng có thể chạy và test ngay lập tức mà không cần bật MySQL hay SQL Server. Khi chạy môi trường thực tế, truyền `-Dspring-boot.run.profiles=mysql` hoặc `-Dspring-boot.run.profiles=sqlserver`.
- **Thư mục dự án:** Hai thư mục `c:\ute\WEB programming\Group` và `C:\Users\Will\Projects\disaster-rescue-system` đã được đồng bộ hóa mã nguồn đầy đủ.

---

## 6. Hướng Dẫn Nhanh Cho AI / Lập Trình Viên Mới Tiếp Quản
1. Đọc nhanh file `docs/HANDOVER_STATUS.md` để nắm bắt tiến độ (Session 01 đã xong, bắt đầu Session 02).
2. Xem lộ trình chi tiết phiên 02 tại `docs/roadmap/session-02-auth-security-jwt.md`.
3. Kiểm tra schema CSDL tại `docs/DATABASE_SCHEMA.md`.
4. Sau khi hoàn thành Session 02, cập nhật lại trạng thái trong file này.
