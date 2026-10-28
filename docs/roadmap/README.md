# LỘ TRÌNH CÁC PHIÊN LÀM VIỆC CỦA AI (AI SESSIONS ROADMAP OVERVIEW)
> **Mục tiêu:** Chia nhỏ toàn bộ đồ án thành các phiên độc lập, tập trung và có tiêu chí nghiệm thu rõ ràng. Khi làm việc với AI, bạn chỉ cần chỉ định tên file của phiên đó (ví dụ: *"Hôm nay chúng ta làm `session-11-admin-revenue-expense-statistics.md`"*).

---

## 🗺️ Bản Đồ Tổng Quan & Phân Chia Thành Viên

| Phiên | Tên Phiên & Nội Dung Trọng Tâm | Phụ Trách | File Chi Tiết | Trạng Thái |
| :---: | :--- | :--- | :--- | :---: |
| **01** | Khởi tạo Spring Boot 3 & Cấu hình Đa Database (MySQL + SQL Server) | **PeterWeeee** | [`session-01-setup-and-dual-db.md`](session-01-setup-and-dual-db.md) | ✅ Đã hoàn thành |
| **02** | Xác thực & Phân quyền (Spring Security 6 + JWT + HttpOnly Cookie) | **Vo Van Thinh** | [`session-02-auth-security-jwt.md`](session-02-auth-security-jwt.md) | ✅ Đã hoàn thành |
| **03** | Phân hệ Gửi SOS & Tích hợp Bản đồ Leaflet.js (GPS 1 chạm) | **Vo Van Thinh** | [`session-03-sos-geolocation-leaflet.md`](session-03-sos-geolocation-leaflet.md) | ✅ Đã hoàn thành |
| **04** | Tích hợp Upload ảnh hiện trường cứu hộ lên Cloudinary | **PeterWeeee** | [`session-04-cloudinary-media-upload.md`](session-04-cloudinary-media-upload.md) | ✅ Đã hoàn thành |
| **05** | WebSocket Real-time Cốt lõi (Bắn thông báo SOS & Còi báo động) | **PeterWeeee** | [`session-05-websocket-realtime-sos.md`](session-05-websocket-realtime-sos.md) | ✅ Đã hoàn thành |
| **06** | Phân hệ Điều phối viên (Coordinator Dashboard, Haversine & Gán việc) | **SV 24110329** | [`session-06-coordinator-dashboard-dispatch.md`](session-06-coordinator-dashboard-dispatch.md) | ✅ Đã hoàn thành |
| **07** | Phân hệ Đội cứu hộ (Rescue Team Operations & Nghiệm thu kết quả) | **SV 24110329** | [`session-07-rescuer-team-operations.md`](session-07-rescuer-team-operations.md) | ✅ Đã hoàn thành |
| **08** | Phân hệ Admin (Quản trị, Cảnh báo Broadcast khẩn cấp, Biểu đồ Chart.js) | **SV 24110329** | [`session-08-admin-broadcast-statistics.md`](session-08-admin-broadcast-statistics.md) | ✅ Đã hoàn thành |
| **09** | Tân trang Giao diện Hệ thống & Tinh gọn Nội dung Thừa (UI Redesign) | **SV 24110329** | [`session-09-ui-redesign.md`](session-09-ui-redesign.md) | ✅ Đã hoàn thành |
| **10** | Nâng cấp Giao diện, Sửa lỗi Bản đồ, Quyên góp & Phân trang (UI Fixes) | **PeterWeeee** | [`session-10-ui-fixes.md`](session-10-ui-fixes.md) | ✅ Đã hoàn thành |
| **11** | Bổ sung Chức năng Thống kê Thu - Chi dành cho Admin (Admin Finance) | **PeterWeeee** | [`session-11-admin-revenue-expense-statistics.md`](session-11-admin-revenue-expense-statistics.md) | ✅ Đã hoàn thành |
| **Cuối** | Tối ưu UI/UX Mobile, Kiểm thử Toàn diện & Kịch bản Demo bảo vệ | **Cả nhóm** | [`session-chua-lam-mobile-testing-demo-script.md`](session-chua-lam-mobile-testing-demo-script.md) | ⏸️ Chưa làm trong project này |

---

## 👥 Tài Liệu Phân Công & Script Đổi Author Commit
Xem chi tiết cách phân bổ và kịch bản commit/push cho từng bạn tại: [`docs/TEAM_WORK_DIVISION.md`](../TEAM_WORK_DIVISION.md).

---

## 💡 Hướng Dẫn Sử Dụng
1. Trước mỗi phiên làm việc, mở file tương ứng trong thư mục `docs/roadmap/`.
2. Gửi yêu cầu cho AI kèm theo đường dẫn file đó để AI nắm trọn vẹn mục tiêu và không bị lan man sang các tính năng khác.
3. Khi hoàn thành, commit theo đúng tác giả bằng script `.\commit-as.ps1 <1|2|3> "..."` và cập nhật lại [`docs/HANDOVER_STATUS.md`](../HANDOVER_STATUS.md).
