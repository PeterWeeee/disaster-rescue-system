# 🛠️ HƯỚNG DẪN THIẾT LẬP VÀ VẬN HÀNH CƠ SỞ DỮ LIỆU — CỨUNẠN 24/7

> **Hệ thống hỗ trợ 3 chế độ cơ sở dữ liệu:**
> 1. **Mặc định (Development / Demo):** H2 In-Memory Database (Chạy ngay không cần cài đặt DB server).
> 2. **Hệ quản trị Microsoft SQL Server:** Chạy qua các script trong thư mục `database/`.
> 3. **Hệ quản trị MySQL / MariaDB:** Chạy qua script `database/mysql_init.sql`.

---

## 1. Cấu Trúc Bảng Dữ Liệu Thực Tế (11 Bảng)

Hệ thống được thiết kế chuẩn mực với 11 bảng dữ liệu phục vụ đầy đủ tất cả các phân hệ:

| STT | Tên bảng | Entity Java tương ứng | Ý nghĩa nghiệp vụ |
| :---: | :--- | :--- | :--- |
| 1 | `roles` | `Role.java` | 4 vai trò chuẩn: ADMIN, COORDINATOR, RESCUER, CITIZEN |
| 2 | `users` | `User.java` | Tài khoản, mật khẩu BCrypt, thông tin liên lạc, trạng thái ACTIVE/BLOCKED |
| 3 | `rescue_teams` | `RescueTeam.java` | 23 Đội cứu hộ phân bổ tại 11 tỉnh/thành Miền Trung, tọa độ GPS, trang bị |
| 4 | `team_members` | `TeamMember.java` | Thành viên đội cứu hộ (LEADER, RESCUER, MEDIC, DRIVER), tọa độ cá nhân |
| 5 | `sos_requests` | `SosRequest.java` | Yêu cầu SOS khẩn cấp, GPS, mức độ, người già/trẻ em/người bị thương |
| 6 | `rescue_assignments`| `RescueAssignment.java` | Phân công nhiệm vụ tác nghiệp, trạng thái, biên bản nghiệm thu, ảnh cứu nạn |
| 7 | `disaster_alerts` | `DisasterAlert.java` | Cảnh báo bão lũ, sạt lở đèo dốc, ngập lụt, phát sóng WebSocket diện rộng |
| 8 | `evacuation_shelters`| `EvacuationShelter.java`| Điểm sơ tán tạm lánh an toàn, sức chứa, tỷ lệ lấp đầy |
| 9 | `community_posts` | `CommunityPost.java` | Bảng tin cộng đồng, 6 loại hiểm họa, trạng thái xác minh, chuyển đổi SOS |
| 10 | `post_upvotes` | `PostUpvote.java` | Lượt bình chọn bài đăng (ràng buộc chống trùng UNIQUE(post_id, user_id)) |
| 11 | `media_files` | `MediaFile.java` | Tệp ảnh/video đính kèm chứng minh hiện trường cứu hộ (Local / Cloudinary) |

---

## 2. Số Lượng Dữ Liệu Thực Tế Được Khởi Tạo (Seed Data)

*Dữ liệu thực tế được ghi nhận chính xác sau khi hệ thống khởi động hoàn tất:*

| Hạng mục dữ liệu | Số lượng thực tế | Ghi chú chi tiết |
| :--- | :---: | :--- |
| **Số tỉnh/thành được seed** | **11** | Thanh Hóa, Nghệ An, Hà Tĩnh, Quảng Trị, TP. Huế, TP. Đà Nẵng, Quảng Ngãi, Gia Lai, Đắk Lắk, Khánh Hòa, Lâm Đồng |
| **Số Đội cứu hộ** | **23** | Mỗi tỉnh/thành có từ 1 đến 2 đội, gắn với địa bàn và tọa độ GPS thực tế |
| **Số Tài khoản người dùng** | **49** | Bao gồm: 1 Admin, 13 Coordinators, 23 Rescuers (Đội trưởng), 12 Citizens |
| **Số Thành viên đội cứu hộ** | **34** | Gồm Đội trưởng (LEADER) và thành viên phối hợp (RESCUER, MEDIC, DRIVER) |
| **Số Ca SOS cứu nạn** | **15** | Đủ 4 mức độ (CRITICAL, HIGH, MEDIUM, LOW) & 5 trạng thái (PENDING, DISPATCHED, IN_PROGRESS, RESOLVED, CANCELLED) |
| **Số Nhiệm vụ phân công** | **9** | Đủ các trạng thái PENDING, IN_PROGRESS, COMPLETED (kèm ảnh nghiệm thu), REJECTED |
| **Số Cảnh báo thiên tai** | **4** | Lũ sông Mã/sông Hương, sạt lở Tây Nguyên, sóng biển Nam Trung Bộ, áp thấp Bắc Trung Bộ |
| **Số Điểm sơ tán an toàn** | **15** | Sức chứa từ 300 - 1.200 người/điểm, có trạng thái OPEN và FULL |
| **Số Bài đăng cộng đồng** | **7** | Bao phủ 6 nhóm hiểm họa, có bài VERIFIED, PENDING, REJECTED |
| **Số Bản ghi tệp đính kèm** | **2** | Ảnh hiện trường ngập lụt thực tế |

---

## 3. Hướng Dẫn Vận Hành Theo Từng Môi Trường

### 3.1. Chạy với H2 Database (Mặc định - Khuyến nghị cho demo)
Ứng dụng được cấu hình tự động nạp `DataInitializer.java` khi khởi động profile `local`.

```powershell
# Di chuyển vào thư mục dự án
cd d:\LT_WEB\disaster-rescue-system

# Biên dịch gói ứng dụng
mvn clean package -DskipTests

# Khởi chạy ứng dụng
java -jar target\disaster-rescue-system-1.0.0-SNAPSHOT.jar
```

- **Giao diện web:** `http://localhost:8080`
- **H2 Console:** `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:cuunan_db`
  - Username: `sa`
  - Password: *(để trống)*

---

### 3.2. Chạy với Microsoft SQL Server
Nếu muốn chạy ứng dụng kết nối trực tiếp đến SQL Server nội bộ:

1. **Khởi tạo database và bảng:**
   Mở **SQL Server Management Studio (SSMS)** và thực thi lần lượt:
   - `database/01_create_database.sql` (Tạo database `cuunan_db` với collation `Vietnamese_CI_AS`)
   - `database/02_create_tables.sql` (Tạo đủ 11 bảng, khóa chính, khóa ngoại, index)
   - `database/03_seed_data.sql` (Nạp tài khoản và dữ liệu mẫu miền Trung)

   *Hoặc chạy nhanh qua PowerShell bằng `sqlcmd`:*
   ```powershell
   sqlcmd -S localhost -U sa -P 123456 -i database\01_create_database.sql
   sqlcmd -S localhost -U sa -P 123456 -i database\02_create_tables.sql
   sqlcmd -S localhost -U sa -P 123456 -i database\03_seed_data.sql
   ```

2. **Kích hoạt profile SQL Server khi chạy Spring Boot:**
   ```powershell
   java -jar -Dspring.profiles.active=sqlserver target\disaster-rescue-system-1.0.0-SNAPSHOT.jar
   # Hoặc chạy bằng Maven:
   mvn spring-boot:run -Dspring-boot.run.profiles=sqlserver
   ```

   > [!NOTE]
   > - **Chuẩn mã hóa UTF-8 BOM:** Các file `01_create_database.sql`, `02_create_tables.sql`, `03_seed_data.sql` và `fix_sqlserver_encoding.sql` đều được lưu ở định dạng UTF-8 with BOM để SSMS và `sqlcmd` đọc chuẩn tiếng Việt có dấu, không bị chuyển thành dấu `?`.
   > - **Tự động tương thích (Self-healing):** Khi khởi chạy với profile `sqlserver`, lớp `DataInitializer` sẽ tự động kiểm tra và nâng cấp các cột văn bản cũ sang `NVARCHAR(MAX)` và tự động phục hồi các bản ghi mẫu bị lỗi font.
   > - **File vá nhanh độc lập:** Bạn cũng có thể mở `database/fix_sqlserver_encoding.sql` trong SSMS để chạy vá nhanh toàn bộ bảng và dữ liệu bất cứ lúc nào.

---

### 3.3. Chạy với MySQL / MariaDB

1. **Khởi tạo database và nạp dữ liệu:**
   ```powershell
   mysql -u root -p < database\mysql_init.sql
   ```

2. **Kích hoạt profile MySQL khi chạy Spring Boot:**
   ```powershell
   java -jar -Dspring.profiles.active=mysql target\disaster-rescue-system-1.0.0-SNAPSHOT.jar
   ```

---

## 4. Cơ Chế Chống Trùng Dữ Liệu (Idempotent Seed)

Trong `DataInitializer.java`:
- Mọi bảng đều sử dụng cơ chế kiểm tra sự tồn tại trước khi thêm mới:
  - Tài khoản kiểm tra qua `userRepository.findByUsername(username)`
  - Đội cứu hộ kiểm tra qua `rescueTeamRepository.findByTeamName(teamName)`
  - Thành viên kiểm tra qua `teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)`
  - Ca SOS kiểm tra qua `sosRequestRepository.findFirstByContactPhone(contactPhone)`
  - Phân công kiểm tra qua `rescueAssignmentRepository.findBySosRequestId(sosId)`
  - Cảnh báo kiểm tra qua `disasterAlertRepository.existsByTitle(title)`
  - Điểm sơ tán kiểm tra qua `evacuationShelterRepository.findByName(name)`
  - Lượt upvote kiểm tra qua `postUpvoteRepository.existsByPostIdAndUserId(postId, userId)`

-> **Đảm bảo ứng dụng có thể khởi động lại nhiều lần liên tiếp mà không bị trùng lặp dữ liệu, không làm phát sinh lỗi ràng buộc khóa.**
