# KẾ HOẠCH CHIA NHỎ PHIÊN LÀM VIỆC CỦA AI (AI SESSIONS ROADMAP)
> **Mục tiêu:** Tránh việc AI bị "tràn ngữ cảnh" (context window loss) dẫn đến quên code cũ hoặc code chắp vá. Kế hoạch này được tách thành các file chi tiết độc lập trong thư mục [`docs/roadmap/`](roadmap/).

---

## 🗺️ Bản Đồ 9 Phiên Làm Việc

```
                       LỘ TRÌNH 9 PHIÊN LÀM VIỆC CỦA AI
┌────────────────────────────────────────────────────────────────────────┐
│ Session 01: Khởi tạo Spring Boot Project & Cấu hình Đa Database        │
│    ↓                                                                   │
│ Session 02: Xác thực & Phân quyền (Spring Security 6 + JWT + Thymeleaf)│
│    ↓                                                                   │
│ Session 03: Phân hệ SOS & Tích hợp Bản đồ Leaflet.js (GPS 1 chạm)      │
│    ↓                                                                   │
│ Session 04: Tích hợp Upload ảnh hiện trường cứu hộ lên Cloudinary      │
│    ↓                                                                   │
│ Session 05: WebSocket Real-time Cốt lõi (Bắn thông báo SOS & Live Map) │
│    ↓                                                                   │
│ Session 06: Phân hệ Điều phối viên (Coordinator Dashboard & Dispatch)   │
│    ↓                                                                   │
│ Session 07: Phân hệ Đội cứu hộ (Rescue Team Operations & Status Update)│
│    ↓                                                                   │
│ Session 08: Phân hệ Quản trị (Admin: Thống kê, User, Broadcast Cảnh báo│
│    ↓                                                                   │
│ Session 09: Tối ưu UI/UX Mobile, Kiểm thử toàn diện & Chuẩn bị Demo    │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 📂 Danh Sách File Chi Tiết Từng Phiên

1. **[Session 01: Khởi tạo Spring Boot & Cấu hình Đa Database](roadmap/session-01-setup-and-dual-db.md)**
   - Setup `pom.xml`, cấu hình song song MySQL + SQL Server, Base Layout Thymeleaf + Bootstrap 5.
2. **[Session 02: Xác thực & Phân quyền (Security + JWT)](roadmap/session-02-auth-security-jwt.md)**
   - User, Role, Spring Security 6, JWT lưu HttpOnly Cookie, Đăng ký, Đăng nhập cho 4 roles.
3. **[Session 03: Phân hệ Gửi SOS & Bản đồ Leaflet.js](roadmap/session-03-sos-geolocation-leaflet.md)**
   - Entity SosRequest, lấy GPS 1 chạm, ghim bản đồ Leaflet, theo dõi tiến độ.
4. **[Session 04: Tích hợp Upload Ảnh Lên Cloudinary](roadmap/session-04-cloudinary-media-upload.md)**
   - Cấu hình Cloudinary SDK, upload ảnh hiện trường kèm form SOS, xem trước ảnh.
5. **[Session 05: WebSocket Real-time Cốt Lõi](roadmap/session-05-websocket-realtime-sos.md)**
   - STOMP over SockJS, kênh `/topic/sos-alerts`, âm thanh chuông báo, marker đỏ động.
6. **[Session 06: Phân hệ Điều Phối Viên (Dashboard & Dispatch)](roadmap/session-06-coordinator-dashboard-dispatch.md)**
   - Trung tâm chỉ huy, công thức Haversine gợi ý đội gần nhất, gán nhiệm vụ.
7. **[Session 07: Phân hệ Đội Cứu Hộ](roadmap/session-07-rescuer-team-operations.md)**
   - Nhận nhiệm vụ, cập nhật tiến độ di chuyển, hoàn thành ca cứu nạn kèm ảnh.
8. **[Session 08: Phân hệ Admin (Quản Trị & Cảnh Báo Khẩn)](roadmap/session-08-admin-broadcast-statistics.md)**
   - Broadcast cảnh báo khẩn cấp toàn màn hình, quản trị tài khoản, biểu đồ Chart.js.
9. **[Session 09: Tối Ưu Mobile, Seed Data & Kịch Bản Demo](roadmap/session-09-mobile-testing-demo-script.md)**
   - Tối ưu màn hình điện thoại, nạp dữ liệu mẫu tự động, kịch bản 5 bước thuyết trình điểm tối đa.
