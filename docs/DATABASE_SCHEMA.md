# ĐẶC TẢ CƠ SỞ DỮ LIỆU & CHIẾN LƯỢC ĐA CSDL (DATABASE SCHEMA)
> **Mục tiêu:** Đảm bảo tất cả các thành viên trong nhóm (dù dùng MySQL hay SQL Server) đều sở hữu cấu trúc dữ liệu đồng nhất 100%, không bị lỗi cú pháp hay xung đột kiểu dữ liệu.

---

## 1. Cấu Hình Tương Thích Song Song MySQL & SQL Server

### Quy tắc JPA bắt buộc:
1. **Khóa chính:** Luôn dùng `@GeneratedValue(strategy = GenerationType.IDENTITY)` (được cả MySQL `AUTO_INCREMENT` và SQL Server `IDENTITY(1,1)` hỗ trợ mượt mà).
2. **Kiểu chuỗi dài (Cross-Database):** Luôn dùng `@Lob` chuẩn Jakarta Persistence cho các trường mô tả dài. Tuyệt đối không dùng cứng `@Column(columnDefinition = "TEXT")` vì kiểu `TEXT` của SQL Server đã bị deprecated, không hỗ trợ Unicode tiếng Việt đầy đủ và cấm toán tử so sánh bằng (`=`). Khi dùng `@Lob`, Hibernate 6 tự động ánh xạ thành `NVARCHAR(MAX)` trên SQL Server (hỗ trợ Unicode và so sánh `=`) và `LONGTEXT` trên MySQL.
3. **Enum:** Luôn dùng `@Enumerated(EnumType.STRING)` để lưu tên dạng chuỗi (Vd: `'PENDING'`, `'IN_PROGRESS'`), tránh lưu số int gây nhầm lẫn khi inspect DB.
4. **Thời gian:** Dùng `LocalDateTime` của Java 8+, map tự động sang `DATETIME` (MySQL) và `DATETIME2` (SQL Server).
5. **Tọa độ GPS:** Dùng kiểu `Double` trong Java, map sang `DOUBLE` (MySQL) hoặc `FLOAT` (SQL Server).
6. **Chuỗi Unicode Tiếng Việt:** Tất cả các cột chứa tiếng Việt (họ tên, địa chỉ, tiêu đề, tin nhắn) trên SQL Server bắt buộc là `NVARCHAR` / `NVARCHAR(MAX)` (trong DDL SQL) và connection string JDBC phải có `;sendStringParametersAsUnicode=true` để driver gửi Unicode `N'...'` (UTF-16). Không dùng `use_nationalized_character_data: true` vì gây lỗi conversion varchar sang NCHAR trên các cột `VARCHAR(50)`.

---

## 2. Danh Sách Các Bảng (Tables) & Quan Hệ (Entities)

### 2.1. Bảng `roles` (Vai trò)
- `id` (INT, PK, Identity)
- `name` (VARCHAR(50), Unique, Not Null) - Ví dụ: `ROLE_CITIZEN`, `ROLE_RESCUER`, `ROLE_COORDINATOR`, `ROLE_ADMIN`.

### 2.2. Bảng `users` (Người dùng)
- `id` (BIGINT, PK, Identity)
- `username` (VARCHAR(100), Unique, Not Null)
- `password` (VARCHAR(255), Not Null) - Băm bằng BCrypt
- `full_name` (VARCHAR(150), Not Null)
- `phone` (VARCHAR(20), Not Null)
- `email` (VARCHAR(100))
- `address` (VARCHAR(255))
- `avatar_url` (VARCHAR(500))
- `role_id` (INT, FK -> `roles.id`)
- `status` (VARCHAR(30)) - `ACTIVE`, `BLOCKED`
- `created_at` (DATETIME)

### 2.3. Bảng `rescue_teams` (Đội cứu hộ)
- `id` (BIGINT, PK, Identity)
- `team_name` (VARCHAR(150), Not Null)
- `leader_id` (BIGINT, FK -> `users.id`) - Đội trưởng
- `contact_phone` (VARCHAR(20), Not Null)
- `current_latitude` (DOUBLE) - Tọa độ trạm đóng quân / vị trí hiện tại
- `current_longitude` (DOUBLE)
- `member_count` (INT, Default 0)
- `equipment_notes` (TEXT) - Trang thiết bị: Vd "2 xuồng cao su, 10 áo phao, túi y tế"
- `status` (VARCHAR(30)) - `AVAILABLE`, `BUSY`, `OFFLINE`
- `created_at` (DATETIME)

### 2.4. Bảng `team_members` (Thành viên đội cứu hộ)
- `id` (BIGINT, PK, Identity)
- `team_id` (BIGINT, FK -> `rescue_teams.id`)
- `user_id` (BIGINT, FK -> `users.id`)
- `member_role` (VARCHAR(50)) - `LEADER`, `MEDIC`, `DRIVER`, `RESCUER`
- `current_lat` (DOUBLE)
- `current_lng` (DOUBLE)
- `joined_at` (DATETIME)

### 2.5. Bảng `sos_requests` (Yêu cầu cứu nạn)
- `id` (BIGINT, PK, Identity)
- `sender_id` (BIGINT, Nullable, FK -> `users.id`) - Cho phép gửi ẩn danh nếu chưa đăng nhập
- `contact_name` (VARCHAR(150), Not Null)
- `contact_phone` (VARCHAR(20), Not Null)
- `secondary_phone` (VARCHAR(20))
- `latitude` (DOUBLE, Not Null) - Tọa độ GPS
- `longitude` (DOUBLE, Not Null)
- `address_detail` (VARCHAR(255)) - Địa chỉ ghi nhớ (Vd: "Cạnh cây xăng số 3")
- `urgency_level` (VARCHAR(30), Not Null) - `CRITICAL`, `HIGH`, `MEDIUM`, `LOW`
- `people_count` (INT, Not Null, Default 1)
- `has_elderly` (BOOLEAN, Default false)
- `has_children` (BOOLEAN, Default false)
- `has_injured` (BOOLEAN, Default false)
- `description` (TEXT)
- `image_url` (VARCHAR(500)) - Link ảnh trên Cloudinary
- `status` (VARCHAR(30), Not Null) - `PENDING`, `DISPATCHED`, `IN_PROGRESS`, `RESOLVED`, `CANCELLED`
- `created_at` (DATETIME)
- `resolved_at` (DATETIME)

### 2.6. Bảng `rescue_assignments` (Phân công nhiệm vụ cứu hộ)
- `id` (BIGINT, PK, Identity)
- `sos_request_id` (BIGINT, FK -> `sos_requests.id`)
- `team_id` (BIGINT, FK -> `rescue_teams.id`)
- `assigned_by` (BIGINT, FK -> `users.id`) - Coordinator đã phân công
- `status` (VARCHAR(30)) - `PENDING`, `ACCEPTED`, `REJECTED`, `IN_PROGRESS`, `COMPLETED`
- `rejection_reason` (VARCHAR(255))
- `assigned_at` (DATETIME)
- `accepted_at` (DATETIME)
- `completed_at` (DATETIME)
- `report_notes` (TEXT) - Ghi chú kết quả hoàn thành
- `report_image_url` (VARCHAR(500)) - Ảnh nghiệm thu

### 2.7. Bảng `disaster_alerts` (Cảnh báo thiên tai & Vùng nguy hiểm)
- `id` (BIGINT, PK, Identity)
- `title` (VARCHAR(200), Not Null)
- `content` (TEXT, Not Null)
- `severity` (VARCHAR(30)) - `DANGER_RED`, `WARNING_ORANGE`, `NOTICE_YELLOW`
- `affected_region` (VARCHAR(255))
- `is_broadcast` (BOOLEAN, Default false) - Có bắn pop-up khẩn cấp toàn màn hình không
- `created_by` (BIGINT, FK -> `users.id`)
- `created_at` (DATETIME)
- `expires_at` (DATETIME)

---

## 3. Mẫu Cấu Hình Đa CSDL (`application.yml`)

### File `application-mysql.yml`
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cuunan_db?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true
    username: root
    password: your_mysql_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
```

### File `application-sqlserver.yml`
```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=cuunan_db;encrypt=true;trustServerCertificate=true
    username: sa
    password: your_sqlserver_password
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.SQLServerDialect
```
