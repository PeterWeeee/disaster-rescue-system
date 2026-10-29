# PHIÊN LÀM VIỆC #14: CHUẨN HÓA UNICODE SQL SERVER, KHẮC PHỤC LỖI FONT TIẾNG VIỆT & NÂNG CẤP SCHEMA TỰ ĐỘNG
> **Trạng thái:** ✅ ĐÃ HOÀN THÀNH (100%)  
> **Người phụ trách:** **Tri Thai (PeterWeeee - `NguyenTT162.4@gmail.com`)**  
> **Thời điểm hoàn thành:** 29/10/2026  
> **Nhánh thực hiện:** `develop`

---

## 1. Bối Cảnh Vấn Đề & Triệu Chứng Lỗi

### 1.1. Triệu chứng quan sát
* Khi ứng dụng chạy với profile `sqlserver` (kết nối Microsoft SQL Server):
  1. Hộp thoại/dropdown Thông báo (`notifications`) trên trang Điều phối viên (`/coordinator/**`) và Đội cứu hộ (`/rescuer/**`) bị biến đổi toàn bộ ký tự tiếng Việt có dấu thành dấu chấm hỏi (`?`).
  2. Bảng Lịch sử hoạt động (`activity_logs`) tại trang Điều phối viên (`/coordinator/history`) cũng xuất hiện hàng loạt ký tự `?`.
  3. Trước đây, dự án từng gặp lỗi ánh xạ khi so sánh nội dung text trong `CommunityPostRepository.findFirstByContent` do kiểu dữ liệu `TEXT` trong SQL Server không hỗ trợ toán tử so sánh bằng (`=`).

### 1.2. Nguyên nhân kỹ thuật gốc rễ (Root Cause)
1. **Kiểu dữ liệu `TEXT` và `VARCHAR` non-Unicode trong SQL Server**:
   - Khác với MySQL (chuỗi mặc định hỗ trợ UTF-8 qua `utf8mb4`), SQL Server phân tách nghiêm ngặt giữa kiểu dữ liệu ANSI 1-byte (`VARCHAR`, `TEXT`) và kiểu Unicode 2-byte UTF-16 (`NVARCHAR`, `NVARCHAR(MAX)`).
   - Kiểu `TEXT` đã bị Microsoft deprecate từ SQL Server 2005, không hỗ trợ tốt tiếng Việt Unicode và cấm dùng toán tử so sánh `=`.
   - Một số Entity trong hệ thống trước đây sử dụng `@Column(columnDefinition = "TEXT")`, khiến Hibernate sinh kiểu `TEXT` hoặc không map đúng `NVARCHAR(MAX)`.
2. **Thiếu tham số Unicode trên JDBC Connection String**:
   - Khi ứng dụng gửi câu lệnh `INSERT` / `UPDATE` qua `PreparedStatement`, nếu thiếu tham số `sendStringParametersAsUnicode=true`, trình điều khiển JDBC của Microsoft mặc định gửi chuỗi dưới dạng non-Unicode, khiến SQL Server tự động chuyển đổi ký tự có dấu sang `?` trước khi ghi vào đĩa.
3. **Dữ liệu động cũ đã bị biến thành `?` vĩnh viễn**:
   - Các bản ghi thông báo và nhật ký được tạo ra trong quá trình kiểm thử trước khi cấu hình Unicode đã bị lưu dấu `?` vĩnh viễn vào database.

---

## 2. Các Bước Kỹ Thuật Đã Triển Khai

### 2.1. Chuẩn hóa JPA Entities với `@Lob` tiêu chuẩn (Cross-Database)
Thay thế toàn bộ `@Column(columnDefinition = "TEXT")` bằng JPA chuẩn `@Lob` trên 10 Entities:
- [`Notification.java`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/java/vn/edu/cuunan/entity/Notification.java): trường `message`
- [`SosRequest.java`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/java/vn/edu/cuunan/entity/SosRequest.java): trường `description`
- [`CommunityPost.java`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/java/vn/edu/cuunan/entity/CommunityPost.java): trường `content`
- [`DisasterAlert.java`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/java/vn/edu/cuunan/entity/DisasterAlert.java): trường `content`
- [`ActivityLog.java`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/java/vn/edu/cuunan/entity/ActivityLog.java): trường `details`
- [`RescueTeam.java`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/java/vn/edu/cuunan/entity/RescueTeam.java): trường `equipmentNotes`
- [`RescueAssignment.java`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/java/vn/edu/cuunan/entity/RescueAssignment.java): trường `reportNotes` và `selectedEquipment`
- [`RescueEquipment.java`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/java/vn/edu/cuunan/entity/RescueEquipment.java): trường `notes`
- [`EquipmentAssignment.java`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/java/vn/edu/cuunan/entity/EquipmentAssignment.java): trường `notes`
- [`RescueChatMessage.java`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/java/vn/edu/cuunan/entity/RescueChatMessage.java): trường `content`

> **Lợi ích**: `@Lob` được Hibernate 6 tự động ánh xạ thông minh theo từng hệ CSDL:
> - **SQL Server**: `NVARCHAR(MAX)` (hỗ trợ Unicode 100%, hỗ trợ toán tử `=`, `LIKE`, indexing).
> - **MySQL / MariaDB**: `LONGTEXT` / `TEXT` (kết hợp `utf8mb4` hiển thị tiếng Việt hoàn chỉnh).
> - **PostgreSQL**: `TEXT`.
> - **H2**: `CLOB`.

### 2.2. Bổ sung cấu hình JDBC URL
Trong [`application-sqlserver.yml`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/resources/application-sqlserver.yml):
```yaml
spring:
  datasource:
    url: jdbc:sqlserver://${MSSQL_HOST:localhost}:${MSSQL_PORT:1433};databaseName=${MSSQL_DB:cuunan_db};encrypt=true;trustServerCertificate=true;sendStringParametersAsUnicode=true
```
Cờ `sendStringParametersAsUnicode=true` đảm bảo mọi chuỗi truyền từ ứng dụng vào SQL Server luôn được gửi dưới dạng Unicode `N'...'` (UTF-16).

---

## 3. Phân Tích Sai Lầm Phát Sinh & Cách Khắc Phục Triệt Để

### 3.1. Sai lầm thử nghiệm ban đầu
- Trong nỗ lực đầu tiên, cấu hình `hibernate.use_nationalized_character_data: true` và annotation `@Nationalized` được thử nghiệm để ép mọi chuỗi sang Unicode.

### 3.2. Hậu quả
- Khi ứng dụng khởi động và thực hiện truy vấn `RoleRepository.findByName("ROLE_ADMIN")`, hệ thống gặp ngoại lệ:
  ```
  com.microsoft.sqlserver.jdbc.SQLServerException: The conversion from varchar to NCHAR is unsupported.
  ```
- **Nguyên nhân**: Cấu hình `use_nationalized_character_data` ép Hibernate sử dụng `NVarcharJdbcType`. Khi đọc kết quả SELECT, driver gọi hàm `ResultSet.getNString()`. Tuy nhiên, trong CSDL SQL Server, cột `roles.name` (và `users.username`,...) được khai báo là kiểu `VARCHAR(50)`. Trình điều khiển `mssql-jdbc` của Microsoft cấm tuyệt đối việc gọi `getNString()` lên các cột kiểu `VARCHAR`, dẫn đến crash ứng dụng.

### 3.3. Giải pháp khắc phục triệt để
1. **Loại bỏ hoàn toàn**: Gỡ bỏ `use_nationalized_character_data: true` và annotation `@Nationalized`.
2. **Dùng phương thức đọc tiêu chuẩn**: Hibernate quay về sử dụng `rs.getString()`. Trong driver Microsoft SQL Server, `rs.getString()` đọc được an toàn **cả `VARCHAR` lẫn `NVARCHAR`/`NVARCHAR(MAX)`** mà không ném lỗi chuyển đổi kiểu, đồng thời trả về chuỗi Unicode Java UTF-16 đầy đủ.

---

## 4. Tự Động Hóa Nâng Cấp Schema & Phục Hồi Dữ Liệu Sạch (Self-Healing)

### 4.1. Tự động tương thích trong [`DataInitializer.java`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/java/vn/edu/cuunan/config/DataInitializer.java)
- Thêm phương thức `ensureSqlServerUnicodeCompatibility()`:
  - Chỉ kích hoạt khi CSDL là Microsoft SQL Server (`dbProductName.contains("microsoft")`), tự động bỏ qua khi chạy MySQL hoặc H2.
  - Tự động chạy các lệnh `ALTER TABLE ... ALTER COLUMN ... NVARCHAR(MAX)` để nâng cấp database đang chạy mà không yêu cầu người dùng phải gõ lệnh SQL thủ công.
  - Tự động phát hiện các bản ghi mẫu tĩnh dính dấu `?` để sửa lại tiếng Việt chuẩn.

### 4.2. Xóa dữ liệu lỗi cũ & Tái tạo Seed Data sạch 100%
Đã kết nối trực tiếp vào SQL Server cục bộ (`cuunan_db`) và thực thi quy trình làm sạch:
1. Tắt Foreign Keys và xóa toàn bộ dữ liệu ở 19 bảng.
2. Reset toàn bộ `IDENTITY` counters về 0.
3. Nạp lại [`database/03_seed_data.sql`](file:///c:/Users/Will/Projects/disaster-rescue-system/database/03_seed_data.sql) (chứa sẵn tiền tố `N'...'` và lưu UTF-8 with BOM).
4. Nạp dữ liệu mẫu mới cho `notifications` và `activity_logs` với tiếng Việt có dấu chuẩn đẹp mắt.

---

## 5. Kết Quả Kiểm Thử & Nghiệm Thu

1. **Unit & Integration Tests**:
   - `mvn test-compile`: **BUILD SUCCESS**.
   - `mvn test`: Chạy toàn bộ **128/128 bài test**, kết quả **0 failures, 0 errors** (BUILD SUCCESS 100%).
2. **Kiểm tra trực tiếp CSDL SQL Server**:
   - Bảng `notifications`: Hiển thị trơn tru các thông báo *"Tiếp nhận ca SOS #1 mới"*, *"Lệnh phân công nhiệm vụ mới"*.
   - Bảng `activity_logs`: Hiển thị trơn tru các nhật ký điều phối *"Điều phối Đội cứu hộ Đà Nẵng 01 tiếp cận ca cứu nạn #1"*, v.v.
   - Bảng `users`, `rescue_teams`, `sos_requests`: Toàn bộ họ tên và địa chỉ 11 tỉnh thành miền Trung không còn bất kỳ ký tự `?` nào.
