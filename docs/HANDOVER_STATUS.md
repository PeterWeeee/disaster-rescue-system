# BẢNG BÀN GIAO & TRẠNG THÁI DỰ ÁN (HANDOVER STATUS)
> **Mục đích:** File này là "bàn giao ca". Khi chuyển đổi thiết bị hoặc chuyển phiên làm việc AI, file này cho biết chính xác dự án đang ở đâu, đã làm được gì, và bước tiếp theo cần làm là gì.

---

## 1. Trạng Thái Hiện Tại (Current Snapshot)
- **Thời điểm cập nhật:** 25/10/2026
- **Nhánh Git hiện tại:** `develop`
- **Giai đoạn (Phase):** Phase 2 - Xác thực & Phân quyền Spring Security 6 + JWT + Entity Toàn Diện (ĐÃ HOÀN THÀNH SESSION 02)
- **Trạng thái tổng quan:** Đã hoàn thành 100% Session 01 và Session 02. Toàn bộ 7 Entities JPA, 7 Repositories, hệ thống bảo mật Spring Security 6 + Stateless JWT qua HttpOnly Cookie, DataInitializer nạp 4 roles & 4 accounts mặc định, Controllers & Views đăng ký/đăng nhập/403 forbidden. Toàn bộ 13 unit & integration tests đạt **100% BUILD SUCCESS**. Sẵn sàng cho Session 03 (Phân hệ Gửi SOS & Leaflet.js).

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
  - [x] Cấu hình Cloudinary (`odwf5uoi`, `329355988118811`, `w6nY13mFiftdZCSg6Sqd0GdU4j0`) trong `application.yml` và bean `CloudinaryConfig.java`.
  - [x] Base Layout Thymeleaf (`main.html`, `navbar.html`, `footer.html`, `head.html`).
- [x] **SESSION 02 (Võ Văn Thịnh - `vovanthinh2006@gmail.com`):**
  - [x] Xây dựng trọn bộ 7 Entities JPA tương thích đa CSDL: `Role`, `User`, `RescueTeam`, `TeamMember`, `SosRequest`, `RescueAssignment`, `DisasterAlert`.
  - [x] Xây dựng 7 JPA Repositories tương ứng trong `vn.edu.cuunan.repository`.
  - [x] Triển khai bảo mật Spring Security 6 + Stateless JWT qua HttpOnly Cookie:
    - `JwtTokenProvider.java` (chuẩn JJWT 0.12.x).
    - `CustomUserDetails.java` & `CustomUserDetailsService.java`.
    - `JwtAuthenticationFilter.java` (hỗ trợ đọc cả HttpOnly Cookie `jwt_token` và Bearer header).
    - `JwtAuthenticationEntryPoint.java` & `CustomAccessDeniedHandler.java` (403 Forbidden).
    - `SecurityConfig.java` phân quyền chặt chẽ các route `/coordinator/**`, `/rescuer/**`, `/admin/**`.
  - [x] `DataInitializer.java`: Tự động nạp 4 vai trò và 4 tài khoản mặc định (`admin`, `coordinator`, `rescuer`, `citizen` với pass `123456`) khi khởi động.
  - [x] Controllers & DTOs: `AuthController`, `AuthApiController`, `CoordinatorController`, `RescuerController`, `AdminController`, `ProfileController`.
  - [x] Giao diện Thymeleaf hoàn chỉnh: `auth/login.html` (có nút điền nhanh tài khoản demo), `auth/register.html`, `auth/forbidden.html`.
  - [x] Bộ kiểm thử 13 unit & integration tests (`SecurityIntegrationTest`, `AuthServiceTest`, `JwtTokenProviderTest`, `DisasterRescueApplicationTests`) chạy thành công 100%.

---

## 3. Những Việc ĐANG LÀM DỞ DANG (In Progress)
- [ ] Chuẩn bị bắt đầu Session 03: Phân hệ Gửi SOS & Bản đồ tương tác Leaflet.js (GPS 1 chạm).

---

## 4. Việc CẦN LÀM TIẾP THEO (Next Steps)
1. **Phiên AI tiếp theo (Session 03 - Phụ trách: Võ Văn Thịnh - `vovanthinh2006@gmail.com`):**
   - Xây dựng chức năng gửi yêu cầu cứu nạn khẩn cấp SOS (lưu CSDL, hỗ trợ gửi nặc danh hoặc đã đăng nhập).
   - Tích hợp HTML5 Geolocation API lấy tọa độ GPS 1 chạm.
   - Nhúng bản đồ Leaflet.js + OpenStreetMap (kéo thả marker, ghim vị trí, tự điền vĩ độ/kinh độ).
   - Trang theo dõi tiến độ ca cứu hộ cho người dân (`templates/sos/track.html`).
2. **Phiên AI kế tiếp (Session 04 - Phụ trách: PeterWeeee):**
   - Tích hợp Cloudinary Media Upload ảnh hiện trường cứu hộ.

---

## 5. Vấn Đề Tồn Đọng & Lưu Ý Kỹ Thuật (Known Issues / Blockers)
- **Cơ sở dữ liệu:** Mặc định profile được đặt là `local` (sử dụng H2 in-memory) để ứng dụng có thể chạy và test ngay lập tức mà không cần bật MySQL hay SQL Server. Khi chạy môi trường thực tế, truyền `-Dspring-boot.run.profiles=mysql` hoặc `-Dspring-boot.run.profiles=sqlserver`.
- **Thư mục dự án:** Dự án được phát triển và quản lý duy nhất tại `C:\Users\Will\Projects\disaster-rescue-system`. Toàn bộ tài liệu nghiệp vụ gốc (`.docx`) đã được lưu trữ an toàn trong `docs/`.

---

## 6. Hướng Dẫn Nhanh Cho AI / Lập Trình Viên Mới Tiếp Quản
1. Đọc nhanh file `docs/HANDOVER_STATUS.md` để nắm bắt tiến độ (Session 01 và 02 đã xong, bắt đầu Session 03).
2. Xem lộ trình chi tiết phiên 03 tại `docs/roadmap/session-03-sos-geolocation-leaflet.md`.
3. Kiểm tra schema CSDL tại `docs/DATABASE_SCHEMA.md`.
4. Sau khi hoàn thành Session 03, cập nhật lại trạng thái trong file này.
