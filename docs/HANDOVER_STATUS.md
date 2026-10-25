# BẢNG BÀN GIAO & TRẠNG THÁI DỰ ÁN (HANDOVER STATUS)
> **Mục đích:** File này là "bàn giao ca". Khi chuyển đổi thiết bị hoặc chuyển phiên làm việc AI, file này cho biết chính xác dự án đang ở đâu, đã làm được gì, và bước tiếp theo cần làm là gì.

---

## 1. Trạng Thái Hiện Tại (Current Snapshot)
- **Thời điểm cập nhật:** 25/10/2026
- **Nhánh Git hiện tại:** `develop`
- **Giai đoạn (Phase):** Phase 0 - Khởi tạo tài liệu kiến trúc & cấu hình dự án
- **Trạng thái tổng quan:** Đã hoàn thành cấu trúc nhánh Git (`main` chỉ chứa `.gitignore`, `develop` chứa toàn bộ tài liệu), tách lộ trình 9 phiên làm việc trong `docs/roadmap/`. Sẵn sàng bắt đầu Session 01.

---

## 2. Những Việc ĐÃ HOÀN THÀNH (Completed)
- [x] Phân tích toàn bộ nghiệp vụ từ tài liệu `Nhom04_LTWEB.docx`.
- [x] Tạo file `.gitignore` tiêu chuẩn cho Java Spring Boot, Maven/Gradle, IDEs và OS.
- [x] Tạo branch `main` trên GitHub chỉ chứa file `.gitignore`.
- [x] Tạo branch `develop` trên GitHub chứa toàn bộ tài liệu và lộ trình chi tiết.
- [x] Tách lộ trình thành thư mục `docs/roadmap/` gồm 9 phiên làm việc độc lập.
- [x] Xây dựng bộ tài liệu điều phối AI trong thư mục `docs/`:
  - `docs/PROJECT_CONTEXT.md` (Ngữ cảnh dự án & Kiến trúc)
  - `docs/HANDOVER_STATUS.md` (File này - Trạng thái & Bàn giao)
  - `docs/ROADMAP_AI_SESSIONS.md` & `docs/roadmap/` (Lộ trình chi tiết 9 phiên)
  - `docs/DATABASE_SCHEMA.md` (Đặc tả Entity & Cấu hình song song MySQL + SQL Server)
  - `docs/API_AND_WEBSOCKET_SPECS.md` (Đặc tả REST & WebSocket)

---

## 3. Những Việc ĐANG LÀM DỞ DANG (In Progress)
- [ ] Xác nhận thông tin cấu hình SQL Server & Cloudinary từ người dùng.
- [ ] Bắt đầu Session 01: Khởi tạo mã nguồn Spring Boot 3 & layout Thymeleaf.

---

## 4. Việc CẦN LÀM TIẾP THEO (Next Steps)
1. **Phiên AI tiếp theo (Session 01):**
   - Khởi tạo khung mã nguồn Spring Boot 3 (Maven, Java 17) với các dependency: Web, Thymeleaf, Data JPA, Security, WebSocket, Validation, Lombok, MySQL Driver, SQL Server Driver.
   - Viết file `pom.xml`, cấu hình `application.yml` kèm 2 profile: `application-mysql.yml` và `application-sqlserver.yml`.
   - Tạo cấu trúc package chuẩn (`config`, `controller`, `entity`, `repository`, `service`, `security`).
   - Tạo Base Layout Thymeleaf + Bootstrap 5 (Navbar, Footer, Sidebar).
2. **Phiên AI kế tiếp (Session 02):**
   - Tạo các Entity nền tảng (`User`, `Role`).
   - Cấu hình Spring Security 6 + JWT Authentication (Đăng ký, Đăng nhập, lưu Cookie JWT).

---

## 5. Vấn Đề Tồn Đọng & Lưu Ý Kỹ Thuật (Known Issues / Blockers)
- **Lưu ý CSDL:** Đội ngũ phát triển dùng đồng thời cả MySQL và SQL Server -> Tuyệt đối không hardcode SQL native trong Repository, luôn dùng JPQL/HQL hoặc Spring Data JPA methods.
- **Tài khoản Cloudinary:** Cần chuẩn bị `CLOUDINARY_URL` hoặc credentials (`cloud_name`, `api_key`, `api_secret`) để cấu hình vào profile môi trường.

---

## 6. Hướng Dẫn Nhanh Cho AI / Lập Trình Viên Mới Tiếp Quản
1. Đọc nhanh file `docs/PROJECT_CONTEXT.md` để nắm kiến trúc và quy ước.
2. Mở file phiên làm việc cụ thể trong `docs/roadmap/` (ví dụ `docs/roadmap/session-01-setup-and-dual-db.md`).
3. Khi hoàn thành công việc của phiên, cập nhật lại mục **"Những việc ĐÃ HOÀN THÀNH"** và **"Việc CẦN LÀM TIẾP THEO"** trong file này.
