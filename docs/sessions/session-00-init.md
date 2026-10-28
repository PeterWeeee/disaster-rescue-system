# NHẬT KÝ PHIÊN LÀM VIỆC #00: KHỞI TẠO HỆ THỐNG QUẢN LÝ DỰ ÁN
- **Thời gian:** 25/10/2026
- **Người thực hiện:** AI Assistant & Nhóm 04 (Lập trình Web)
- **Mục tiêu phiên:** Thiết lập Git repository, cấu trúc phân nhánh `develop`, file `.gitignore` tiêu chuẩn và hệ thống Markdown chia nhỏ phiên làm việc cho AI + bàn giao máy.

---

### Kết Quả Đạt Được:
1. Phân tích tài liệu đặc tả đồ án Nhóm 04 (`Nhom04_LTWEB.docx`).
2. Khởi tạo Git cục bộ, thêm remote `https://github.com/PeterWeeee/disaster-rescue-system.git`.
3. Tạo nhánh làm việc `develop`.
4. Tạo file `.gitignore` đầy đủ cho Spring Boot 3, Maven/Gradle, IDEs, và bỏ qua các file binary nặng.
5. Xây dựng hoàn chỉnh thư mục `docs/` gồm 5 tài liệu chủ chốt:
   - `docs/PROJECT_CONTEXT.md`: Ngữ cảnh dự án, kiến trúc, đa CSDL (MySQL & SQL Server).
   - `docs/HANDOVER_STATUS.md`: Bảng theo dõi trạng thái hiện tại & bàn giao ca.
   - `docs/ROADMAP_AI_SESSIONS.md`: Lộ trình 9 phiên làm việc độc lập của AI.
   - `docs/DATABASE_SCHEMA.md`: Thiết kế CSDL và quy tắc JPA tương thích đa CSDL.
   - `docs/API_AND_WEBSOCKET_SPECS.md`: Đặc tả REST API & WebSocket Topics.

---

### Kế Hoạch Cho Phiên Tiếp Theo (Session 01):
- Khởi tạo project Spring Boot với `pom.xml`.
- Tạo `application.yml` kèm 2 profile `application-mysql.yml` và `application-sqlserver.yml`.
- Xây dựng giao diện cơ bản với Thymeleaf + Bootstrap 5.
