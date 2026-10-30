# HƯỚNG DẪN KHỞI TẠO CƠ SỞ DỮ LIỆU SQL SERVER
> Thư mục này lưu trữ toàn bộ kịch bản SQL khởi tạo Database, Tables và Seed Data cho dự án CứuNạn 24/7.

---

### Thứ tự thực thi các file SQL:

Mở **SQL Server Management Studio (SSMS)** và chạy lần lượt:

1. **`01_create_database.sql`:**
   * Tạo cơ sở dữ liệu `cuunan_db` với bảng mã tiếng Việt `Vietnamese_CI_AS`.
2. **`02_create_tables.sql`:**
   * Tạo 7 bảng chính: `roles`, `users`, `rescue_teams`, `team_members`, `sos_requests`, `rescue_assignments`, `disaster_alerts`.
   * Gắn các ràng buộc khóa ngoại (Foreign Keys), Unique và Index tìm kiếm nhanh theo trạng thái SOS.
   *(Lưu ý: Nếu bạn chạy Spring Boot với `spring.jpa.hibernate.ddl-auto=update`, Hibernate cũng có thể tự sinh các bảng này. Tuy nhiên việc chạy file SQL này giúp đảm bảo đúng 100% kiểu dữ liệu, index và collation).*
3. **`03_seed_data.sql`:**
   * Nạp sẵn 4 vai trò chuẩn (`ROLE_ADMIN`, `ROLE_COORDINATOR`, `ROLE_RESCUER`, `ROLE_CITIZEN`).
   * Nạp tài khoản mẫu (Mật khẩu mặc định đều là `123456`):
     * `admin` / `123456` (Quản trị viên)
     * `coordinator` / `123456` (Điều phối viên duy nhất)
     * `doitruong01` / `123456` (Đội trưởng cứu hộ)
     * `nguoidan01` / `123456` (Người dân)
   * Nạp Đội cứu hộ mẫu, 2 ca cứu nạn SOS khẩn cấp mẫu và 1 cảnh báo bão lũ diện rộng.

---

### Chạy bằng lệnh dòng lệnh (sqlcmd):
Nếu không dùng giao diện SSMS, bạn có thể chạy nhanh qua PowerShell:
```powershell
sqlcmd -S localhost -U sa -P 123456 -i database\01_create_database.sql
sqlcmd -S localhost -U sa -P 123456 -i database\02_create_tables.sql
sqlcmd -S localhost -U sa -P 123456 -i database\03_seed_data.sql
```
