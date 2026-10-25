# LỘ TRÌNH 9 PHIÊN LÀM VIỆC CỦA AI (AI SESSIONS ROADMAP OVERVIEW)
> **Mục tiêu:** Chia nhỏ toàn bộ đồ án thành các phiên độc lập, tập trung và có tiêu chí nghiệm thu rõ ràng. Khi làm việc với AI, bạn chỉ cần chỉ định tên file của phiên đó (ví dụ: *"Hôm nay chúng ta làm `session-02-auth-security-jwt.md`"*).

---

## 🗺️ Bản Đồ Tổng Quan & Phân Chia Thành Viên

| Phiên | Tên Phiên & Nội Dung Trọng Tâm | Phụ Trách | File Chi Tiết | Trạng Thái |
| :---: | :--- | :--- | :--- | :---: |
| **01** | Khởi tạo Spring Boot 3 & Cấu hình Đa Database (MySQL + SQL Server) | **PeterWeeee** | [`session-01-setup-and-dual-db.md`](session-01-setup-and-dual-db.md) | ✅ Đã hoàn thành |
| **02** | Xác thực & Phân quyền (Spring Security 6 + JWT + HttpOnly Cookie) | **Vo Van Thinh** | [`session-02-auth-security-jwt.md`](session-02-auth-security-jwt.md) | ✅ Đã hoàn thành |
| **03** | Phân hệ Gửi SOS & Tích hợp Bản đồ Leaflet.js (GPS 1 chạm) | **Vo Van Thinh** | [`session-03-sos-geolocation-leaflet.md`](session-03-sos-geolocation-leaflet.md) | ⏳ Sẵn sàng thực hiện |
| **04** | Tích hợp Upload ảnh hiện trường cứu hộ lên Cloudinary | **PeterWeeee** | [`session-04-cloudinary-media-upload.md`](session-04-cloudinary-media-upload.md) | ⏳ Chưa bắt đầu |
| **05** | WebSocket Real-time Cốt lõi (Bắn thông báo SOS & Live Map Marker) | **PeterWeeee** | [`session-05-websocket-realtime-sos.md`](session-05-websocket-realtime-sos.md) | ⏳ Chưa bắt đầu |
| **06** | Phân hệ Điều phối viên (Coordinator Dashboard, Tìm đội gần nhất & Gán việc) | **SV 24110329** | [`session-06-coordinator-dashboard-dispatch.md`](session-06-coordinator-dashboard-dispatch.md) | ⏳ Chưa bắt đầu |
| **07** | Phân hệ Đội cứu hộ (Rescue Team Operations & Báo cáo kết quả) | **SV 24110329** | [`session-07-rescuer-team-operations.md`](session-07-rescuer-team-operations.md) | ⏳ Chưa bắt đầu |
| **08** | Phân hệ Admin (Quản trị, Cảnh báo Broadcast khẩn cấp, Biểu đồ thống kê) | **SV 24110329** | [`session-08-admin-broadcast-statistics.md`](session-08-admin-broadcast-statistics.md) | ⏳ Chưa bắt đầu |
| **09** | Tối ưu UI/UX Mobile, Kiểm thử Toàn diện & Kịch bản Demo bảo vệ | **Cả nhóm** | [`session-09-mobile-testing-demo-script.md`](session-09-mobile-testing-demo-script.md) | ⏳ Chưa bắt đầu |

---

## 👥 Tài Liệu Phân Công & Script Đổi Author Commit
Xem chi tiết cách phân bổ và kịch bản commit/push cho từng bạn tại: [`docs/TEAM_WORK_DIVISION.md`](../TEAM_WORK_DIVISION.md).

---

## 💡 Hướng Dẫn Sử Dụng
1. Trước mỗi phiên làm việc, mở file tương ứng trong thư mục `docs/roadmap/`.
2. Gửi yêu cầu cho AI kèm theo đường dẫn file đó để AI nắm trọn vẹn mục tiêu và không bị lan man sang các tính năng khác.
3. Khi hoàn thành, commit theo đúng tác giả bằng script `.\commit-as.ps1 <1|2|3> "..."` và cập nhật lại [`docs/HANDOVER_STATUS.md`](../HANDOVER_STATUS.md).
