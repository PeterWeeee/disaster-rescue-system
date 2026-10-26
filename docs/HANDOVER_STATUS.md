# BẢNG BÀN GIAO & TRẠNG THÁI DỰ ÁN (HANDOVER STATUS)
> **Mục đích:** File này là "bàn giao ca". Khi chuyển đổi thiết bị hoặc chuyển phiên làm việc AI, file này cho biết chính xác dự án đang ở đâu, đã làm được gì, và bước tiếp theo cần làm là gì.

---

## 1. Trạng Thái Hiện Tại (Current Snapshot)
- **Thời điểm cập nhật:** 26/10/2026
- **Nhánh Git hiện tại:** `develop`
- **Giai đoạn (Phase):** Phase 4 - Tích hợp Đám Mây Cloudinary Media Upload & Fallback Local Storage (ĐÃ HOÀN THÀNH 100% SESSION 04)
- **Trạng thái tổng quan:** Đã hoàn thành 100% Session 01, Session 02, Session 03 và Session 04. Đã triển khai hoàn chỉnh dịch vụ `CloudinaryService.java`, REST API `UploadApiController.java` (`POST /api/upload/image`), Entity `MediaFile.java`, form upload ảnh hiện trường kèm preview thumbnail trên `/sos`, xem ảnh phóng to Modal trên `/sos/track/{id}`, và xem ảnh trực tiếp trong Leaflet popup trên `/map`. Toàn bộ 44 unit & integration tests đạt **100% BUILD SUCCESS**. Sẵn sàng cho Session 05 (WebSocket STOMP Real-time cốt lõi & Còi báo động khẩn cấp).

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
  - [x] Entity & DTO: Hoàn thiện `SosRequest.java`, `SosRequestDto.java`.
  - [x] Service: `SosService.java` xử lý toàn bộ logic tiếp nhận SOS (hỗ trợ nặc danh hoặc liên kết tài khoản), chuẩn hóa mức độ khẩn cấp, cập nhật trạng thái cứu hộ, đếm ca PENDING.
  - [x] Controllers: `SosController.java`, `SosApiController.java`.
  - [x] Bản đồ Leaflet.js & GPS 1 chạm: Form `sos/request.html`, `sos/track.html` Stepper Timeline 4 bước, Bản đồ toàn cảnh Live Map `map/live-map.html`.
  - [x] Dữ liệu mẫu ban đầu: 3 ca SOS cứu nạn mẫu thực tế tại Đà Nẵng.
  - [x] Khắc phục triệt để lỗi Thymeleaf Fragment drop script và tích hợp định vị Fallback 2 lớp thông minh.
- [x] **SESSION 04 (Tri Thai / PeterWeeee - `NguyenTT162.4@gmail.com`):**
  - [x] Cấu hình SDK Cloudinary HTTP5 (`CloudinaryConfig.java`).
  - [x] Xây dựng `CloudinaryService.java`: Hỗ trợ upload ảnh lên đám mây Cloudinary hoặc tự động Fallback sang thư mục cục bộ `uploads/` nếu chưa có key/mất mạng.
  - [x] Xây dựng `UploadApiController.java` tiếp nhận tải ảnh qua REST API `POST /api/upload/image`.
  - [x] Entity `MediaFile.java` & `MediaFileRepository.java` lưu trữ lịch sử file tải lên.
  - [x] Cấu hình `MvcConfig.java` và `SecurityConfig.java` cho phép truy cập tài nguyên ảnh `/uploads/**` và `/api/upload/**`.
  - [x] Tích hợp Form SOS (`sos/request.html`): Chọn ảnh chụp từ camera hoặc máy tính, xem trước thumbnail tức thì, hủy ảnh nhanh.
  - [x] Cải tiến `sos/track.html` (Modal xem ảnh phóng to) và `map/live-map.html` (xem ảnh trực tiếp trong popup Leaflet).
  - [x] Bổ sung bộ kiểm thử `CloudinaryServiceTest` & `UploadApiControllerTest`. Toàn bộ **39/39 tests PASS 100% BUILD SUCCESS**.

---

## 3. Những Việc ĐANG LÀM DỞ DANG (In Progress)
- [ ] Không có. Đã hoàn thành 100% Session 04. Chuẩn bị bắt đầu Session 05.

---

## 4. Việc CẦN LÀM TIẾP THEO (Next Steps)
1. **Phiên AI tiếp theo (Session 05 - Phụ trách: Tri Thai / PeterWeeee - `NguyenTT162.4@gmail.com`):**
   - Triển khai hạ tầng WebSocket STOMP over SockJS (`WebSocketConfig.java`, `WebSocketEventListener.java`).
   - Kênh `/topic/sos-alerts` phát tín hiệu SOS thời gian thực đến Dashboard của Điều phối viên.
   - Âm thanh còi báo động khẩn cấp (`alert.mp3`) và hiệu ứng nhấp nháy radar trên bản đồ Live Map mà không cần F5.
   - Kênh `/topic/map-updates` đồng bộ trạng thái ca cứu nạn tức thời.
2. **Phiên AI kế tiếp (Session 06 - Phụ trách: Sinh viên MSSV 24110329):**
   - Xây dựng Trung tâm chỉ huy Điều phối viên chuyên sâu (`/coordinator/dashboard`).
   - Thuật toán Haversine đo khoảng cách tọa độ để gợi ý đội cứu hộ gần nhất.

---

## 5. Hướng Dẫn Nhanh Cho AI / Lập Trình Viên Mới Tiếp Quản
1. Đọc nhanh file `docs/HANDOVER_STATUS.md` để nắm bắt tiến độ (Session 01, 02, 03, 04 đã hoàn thành 100%).
2. Xem lộ trình chi tiết phiên 05 tại `docs/roadmap/session-05-websocket-realtime-sos.md`.
3. Sau khi hoàn thành Session 05, cập nhật lại trạng thái trong file này.
