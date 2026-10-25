# BẢNG BÀN GIAO & TRẠNG THÁI DỰ ÁN (HANDOVER STATUS)
> **Mục đích:** File này là "bàn giao ca". Khi chuyển đổi thiết bị hoặc chuyển phiên làm việc AI, file này cho biết chính xác dự án đang ở đâu, đã làm được gì, và bước tiếp theo cần làm là gì.

---

## 1. Trạng Thái Hiện Tại (Current Snapshot)
- **Thời điểm cập nhật:** 25/10/2026
- **Nhánh Git hiện tại:** `develop`
- **Giai đoạn (Phase):** Phase 0 - Khởi tạo tài liệu kiến trúc & cấu hình dự án
- **Trạng thái tổng quan:** Đã hoàn thành phân tích yêu cầu từ tài liệu nhóm, thiết lập Git, branch `develop`, hệ thống tài liệu AI Context & Handover. Chưa khởi tạo mã nguồn Spring Boot.

---

## 2. Những Việc ĐÃ HOÀN THÀNH (Completed)
- [x] Phân tích toàn bộ nghiệp vụ từ tài liệu `Nhom04_LTWEB.docx`.
- [x] Tạo file `.gitignore` tiêu chuẩn cho Java Spring Boot, Maven/Gradle, IDEs và OS.
- [x] Khởi tạo Git repository cục bộ và trỏ remote về `https://github.com/PeterWeeee/disaster-rescue-system.git`.
- [x] Tạo branch `develop` (không làm việc trực tiếp trên `main`).
- [x] Xây dựng bộ tài liệu điều phối AI trong thư mục `docs/`:
  - `docs/PROJECT_CONTEXT.md` (Ngữ cảnh dự án & Kiến trúc)
  - `docs/HANDOVER_STATUS.md` (File này - Trạng thái & Bàn giao)
  - `docs/ROADMAP_AI_SESSIONS.md` (Lộ trình chia nhỏ các phiên làm việc cho AI)
  - `docs/DATABASE_SCHEMA.md` (Đặc tả Entity & Cấu hình song song MySQL + SQL Server)
  - `docs/API_AND_WEBSOCKET_SPECS.md` (Đặc tả REST & WebSocket)

---

## 3. Những Việc ĐANG LÀM DỞ DANG (In Progress)
- [ ] Chờ User xem qua kế hoạch nội dung các file Markdown và xác nhận.
- [ ] Push branch `develop` đầu tiên lên GitHub repo.

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
2. Đọc file `docs/ROADMAP_AI_SESSIONS.md` để xác định nhiệm vụ của phiên làm việc.
3. Khi hoàn thành công việc của phiên, cập nhật lại mục **"Những việc ĐÃ HOÀN THÀNH"** và **"Việc CẦN LÀM TIẾP THEO"** trong file này.
