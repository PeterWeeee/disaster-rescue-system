# KẾ HOẠCH CHIA NHỎ PHIÊN LÀM VIỆC CỦA AI (AI SESSIONS ROADMAP)
> **Mục tiêu:** Tránh việc AI bị "tràn ngữ cảnh" (context window loss) dẫn đến quên code cũ hoặc code chắp vá. Kế hoạch này được tách thành các file chi tiết độc lập trong thư mục [`docs/roadmap/`](roadmap/).

---

## 🗺️ Bản Đồ Lộ Trình Các Phiên Làm Việc

```
                       LỘ TRÌNH CÁC PHIÊN LÀM VIỆC CỦA AI
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
│ Session 09: Tân trang Giao diện & Tinh gọn Nội dung thừa (ui-redesign) │
│    ↓                                                                   │
│ Session 10: Nâng cấp Giao diện, Sửa lỗi Bản đồ, Quyên góp & Phân trang │
│             (ui-fixes: Bản nâng cấp hoàn thiện của ui-redesign)        │
│    ↓                                                                   │
│ Session 11: Bổ sung Chức năng Thống kê Thu - Chi cho Admin             │
│    ↓                                                                   │
│ Session 12: Khung Chat Trực Tuyến Thời Gian Thực Khi Đang Tiếp Cận     │
│             (Live Chat Giữa Nạn Nhân SOS và Đội Cứu Hộ Phân Công)      │
│    ↓                                                                   │
│ Session 13: Cẩm Nang & Chi Tiết Cẩm Nang Ứng Phó Thiên Tai             │
│             (Tích Hợp Ảnh Minh Họa AI & Lưu Trữ Cloudinary CDN)        │
│    ↓                                                                   │
│ Session 14: Chuẩn Hóa Unicode SQL Server & Sửa Triệt Để Lỗi Font       │
│             (NVARCHAR, @Lob Cross-DB, Schema Self-Healing & Re-seed)   │
│    ↓                                                                   │
│ Session 15: Sửa Lỗi Đăng Tin Cảnh Báo & Động Hóa Cảnh Báo Thiên Tai     │
│             (Thymeleaf Layout Fix & Dynamic Disaster Alerts)           │
│    ↓                                                                   │
│ [SESSION CHƯA LÀM]: Tối ưu Mobile, Kiểm thử & Kịch bản Demo bảo vệ     │
│             (Dời về cuối lộ trình - Chưa thực hiện trong project này)  │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 📂 Danh Sách File Chi Tiết Từng Phiên

1. **[Session 01: Khởi tạo Spring Boot & Cấu hình Đa Database](roadmap/session-01-setup-and-dual-db.md)** *(✅ Đã hoàn thành)*
   - Setup `pom.xml`, cấu hình song song MySQL + SQL Server, Base Layout Thymeleaf + Bootstrap 5.
2. **[Session 02: Xác thực & Phân quyền (Security + JWT)](roadmap/session-02-auth-security-jwt.md)** *(✅ Đã hoàn thành)*
   - User, Role, Spring Security 6, JWT lưu HttpOnly Cookie, Đăng ký, Đăng nhập cho 4 roles.
3. **[Session 03: Phân hệ Gửi SOS & Bản đồ Leaflet.js](roadmap/session-03-sos-geolocation-leaflet.md)** *(✅ Đã hoàn thành)*
   - Entity SosRequest, lấy GPS 1 chạm, ghim bản đồ Leaflet, theo dõi tiến độ.
4. **[Session 04: Tích hợp Upload Ảnh Lên Cloudinary](roadmap/session-04-cloudinary-media-upload.md)** *(✅ Đã hoàn thành)*
   - Cấu hình Cloudinary SDK, upload ảnh hiện trường kèm form SOS, xem trước ảnh.
5. **[Session 05: WebSocket Real-time Cốt Lõi](roadmap/session-05-websocket-realtime-sos.md)** *(✅ Đã hoàn thành)*
   - STOMP over SockJS, kênh `/topic/sos-alerts`, âm thanh chuông báo, marker đỏ động.
6. **[Session 06: Phân hệ Điều Phối Viên (Dashboard & Dispatch)](roadmap/session-06-coordinator-dashboard-dispatch.md)** *(✅ Đã hoàn thành)*
   - Trung tâm chỉ huy, công thức Haversine gợi ý đội gần nhất, gán nhiệm vụ.
7. **[Session 07: Phân hệ Đội Cứu Hộ](roadmap/session-07-rescuer-team-operations.md)** *(✅ Đã hoàn thành)*
   - Nhận nhiệm vụ, cập nhật tiến độ di chuyển, hoàn thành ca cứu nạn kèm ảnh.
8. **[Session 08: Phân hệ Admin (Quản Trị & Cảnh Báo Khẩn)](roadmap/session-08-admin-broadcast-statistics.md)** *(✅ Đã hoàn thành)*
   - Broadcast cảnh báo khẩn cấp toàn màn hình, quản trị tài khoản, biểu đồ Chart.js cơ bản.
9. **[Session 09: Tân Trang Giao Diện & Tinh Gọn Nội Dung Thừa (UI Redesign)](roadmap/session-09-ui-redesign.md)** *(✅ Đã hoàn thành)*
   - Tân trang toàn bộ website, xóa sạch các nội dung thừa và component rườm rà (bloatware), tách Navbar gọn gàng và xây dựng Sidebar chuyên dụng theo từng vai trò, thiết kế bộ UI Preview độc lập (`ui-preview/`).
10. **[Session 10: Nâng Cấp Hoàn Thiện Giao Diện, Sửa Lỗi Bản Đồ, Quyên Góp & Phân Trang (UI Fixes)](roadmap/session-10-ui-fixes.md)** *(✅ Đã hoàn thành)*
    - Bản nâng cấp sâu trực tiếp của `ui-redesign`: Sửa lỗi hiển thị bản đồ Leaflet (z-index & resize), sửa triệt để lỗi encoding UTF-8 tiếng Việt trong SQL/Seeder, nâng cấp Cổng Quyên Góp Fullwidth Seamless Dark Theme sang trọng, triển khai Phân trang (Pagination) trên toàn bộ bảng dữ liệu hệ thống, tách riêng trang Lịch sử điều phối (`coordinator/history.html`).
11. **[Session 11: Bổ Sung Chức Năng Thống Kê Thu - Chi Dành Cho Quản Trị Viên (Admin Finance)](roadmap/session-11-admin-revenue-expense-statistics.md)** *(✅ Đã hoàn thành)*
    - Phân hệ Thống kê Thu - Chi minh bạch cho Admin: Thống kê nguồn thu từ quyên góp (VNPay, MoMo, VietQR) và ngân sách chi giải ngân cấp phát cho các đội cứu hộ, 4 card KPI tài chính, 2 biểu đồ so sánh Thu - Chi và phân bổ nguồn thu, 2 bảng kê dòng tiền Thu và Chi chi tiết có phân trang.
12. **[Session 12: Khung Chat Trực Tuyến Thời Gian Thực Khi Đang Tiếp Cận (Live Mission Chat)](roadmap/session-12-mission-live-chat.md)** *(🆕 Mới tạo / Sẵn sàng triển khai)*
    - Tích hợp khung chat thời gian thực (WebSocket STOMP & REST fallback) xuất hiện khi đội cứu hộ chuyển sang trạng thái "Đang tiếp cận" (`IN_PROGRESS`). Hiển thị đồng bộ trên màn hình Theo dõi SOS (`sos/track.html`) của người dân và màn hình Bản đồ & Lộ trình di chuyển (`rescuer/routes.html`) của đội cứu hộ được phân công, hỗ trợ nhắn tin 2 chiều và gửi tin nhắn tình huống khẩn cấp 1-chạm (Quick SOS chips).
13. **[Session 13: Cẩm Nang & Chi Tiết Cẩm Nang Ứng Phó Thiên Tai](roadmap/session-13-disaster-guide-cloudinary.md)** *(✅ Đã hoàn thành)*
    - Xây dựng phân hệ cẩm nang ứng phó thiên tai chuyên sâu (bão lũ, sạt lở, ngập lụt), tích hợp ảnh minh họa AI lưu trữ Cloudinary CDN, giao diện hiện đại đồng bộ tông màu dự án.
14. **[Session 14: Chuẩn Hóa Unicode SQL Server, Sửa Triệt Để Lỗi Font & Schema Self-Healing](roadmap/session-14-sqlserver-unicode-encoding-fix.md)** *(✅ Đã hoàn thành)*
    - Thay thế toàn bộ `columnDefinition = "TEXT"` bằng chuẩn JPA `@Lob` (tự động ánh xạ `NVARCHAR(MAX)` trên SQL Server và `LONGTEXT` trên MySQL), cấu hình JDBC driver `sendStringParametersAsUnicode=true`, gỡ bỏ triệt để `@Nationalized` tránh lỗi conversion varchar sang NCHAR. Cơ chế tự động tương thích và phục hồi schema sạch trong `DataInitializer`, làm sạch dữ liệu cũ và nạp lại Seed Data chuẩn tiếng Việt 100%.
15. **[Session Chưa Làm: Tối Ưu Mobile, Kiểm Thử & Kịch Bản Demo Bảo Vệ](roadmap/session-chua-lam-mobile-testing-demo-script.md)** *(⏸️ Chưa làm trong project này)*
    - Tối ưu hóa màn hình điện thoại di động, nạp dữ liệu mẫu tự động và kịch bản 5 bước thuyết trình bảo vệ đồ án (Dời về cuối lộ trình, chưa triển khai trong phạm vi đồ án hiện tại).
