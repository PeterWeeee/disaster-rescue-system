# KẾ HOẠCH CHIA NHỎ PHIÊN LÀM VIỆC CỦA AI (AI SESSIONS ROADMAP)
> **Mục tiêu:** Tránh việc AI bị "tràn ngữ cảnh" (context window loss) dẫn đến quên code cũ hoặc code chắp vá. Kế hoạch này được tách thành các file chi tiết độc lập trong thư mục [`docs/roadmap/`](roadmap/) và [`docs/sessions/`](sessions/).

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
│ Session 16: Khắc Phục Lỗi WebSocket STOMP - Đồng Bộ Chat Thời Gian Thực│
│             (Bi-directional Realtime Mission Chat Fix for Citizen)     │
│    ↓                                                                   │
│ Session 17: Tích Hợp Ảnh Hiện Trường Thiên Tai Từ Cloudinary CDN        │
│             (Community Feed Cloudinary Media Integration)              │
│    ↓                                                                   │
│ Session 18: Khung Chat Nổi Toàn Hệ Thống & Đa Cảm Xúc / Bình Luận Feed │
│             (Floating Chat Dock, Multi-Reactions, Comments & Cleanup)  │
│    ↓                                                                   │
│ Session 19: Popover Cảm Xúc Hover, Đồng Bộ Bình Luận & Tự Phục Hồi Font│
│             (Reaction Hover, Comment Sync & Unicode Self-Healing)      │
│    ↓                                                                   │
│ Session 20: Đồng Bộ Cự Ly OSRM & Tự Động Cập Nhật Vị Trí Live GPS      │
│             (Distance Synchronization & Live GPS Tracking 3 Roles)     │
│    ↓                                                                   │
│ Session 21: Tích Hợp Chuyển Khoản Ngân Hàng Thật (Dynamic VietQR)      │
│             (SePay Webhook Auto-Confirmation & Real-time Fund Update)  │
│    ↓                                                                   │
│ Session 22: Khắc Phục Phân Hệ Hồ Sơ Cá Nhân & Quản Trị Danh Tính       │
│             (Profile 403 CSRF Fix, Local Avatar Upload, Email OTP)     │
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
12. **[Session 12: Khung Chat Trực Tuyến Thời Gian Thực Khi Đang Tiếp Cận (Live Mission Chat)](roadmap/session-12-mission-live-chat.md)** *(✅ Đã hoàn thành)*
    - Tích hợp khung chat thời gian thực (WebSocket STOMP & REST fallback) xuất hiện khi đội cứu hộ chuyển sang trạng thái "Đang tiếp cận" (`IN_PROGRESS`). Hiển thị đồng bộ trên màn hình Theo dõi SOS (`sos/track.html`) của người dân và màn hình Bản đồ & Lộ trình di chuyển (`rescuer/routes.html`) của đội cứu hộ được phân công, hỗ trợ nhắn tin 2 chiều và gửi tin nhắn tình huống khẩn cấp 1-chạm (Quick SOS chips).
13. **[Session 13: Cẩm Nang & Chi Tiết Cẩm Nang Ứng Phó Thiên Tai](roadmap/session-13-disaster-guide-cloudinary.md)** *(✅ Đã hoàn thành)*
    - Xây dựng phân hệ cẩm nang ứng phó thiên tai chuyên sâu (bão lũ, sạt lở, ngập lụt), tích hợp ảnh minh họa AI lưu trữ Cloudinary CDN, giao diện hiện đại đồng bộ tông màu dự án.
14. **[Session 14: Chuẩn Hóa Unicode SQL Server, Sửa Triệt Để Lỗi Font & Schema Self-Healing](sessions/session-14-sqlserver-unicode-encoding-fix.md)** *(✅ Đã hoàn thành)*
    - Thay thế toàn bộ `columnDefinition = "TEXT"` bằng chuẩn JPA `@Lob` (tự động ánh xạ `NVARCHAR(MAX)` trên SQL Server và `LONGTEXT` trên MySQL), cấu hình JDBC driver `sendStringParametersAsUnicode=true`, gỡ bỏ triệt để `@Nationalized` tránh lỗi conversion varchar sang NCHAR. Cơ chế tự động tương thích và phục hồi schema sạch trong `DataInitializer`, làm sạch dữ liệu cũ và nạp lại Seed Data chuẩn tiếng Việt 100%.
15. **[Session 15: Sửa Lỗi Đăng Tin Cảnh Báo & Động Hóa Cảnh Báo Thiên Tai](sessions/session-15-community-feed-post-fix-and-dynamic-alerts.md)** *(✅ Đã hoàn thành)*
    - Khắc phục lỗi thẻ đóng sớm trong Thymeleaf layout của trang Bảng Tin (`feed/index.html`), chuẩn hóa mục cảnh báo khẩn thiên tai lấy trực tiếp từ database `disaster_alerts` theo chuẩn quy trình quản lý thiên tai.
16. **[Session 16: Khắc Phục Lỗi WebSocket STOMP - Đồng Bộ Chat Thời Gian Thực](sessions/session-16-realtime-chat-websocket-fix.md)** *(✅ Đã hoàn thành)*
    - Khắc phục triệt để lỗi người dân không nhận được tin nhắn thời gian thực từ đội cứu hộ do SockJS/Stomp tải trễ ở cuối layout. Tối ưu nạp sớm thư viện trong `<head>`, bao bọc `DOMContentLoaded`, bổ sung bộ đếm reconnect timer, cơ chế chống trùng lặp tin nhắn `chat-msg-{id}`, tự động bù đắp tin nhắn và polling dự phòng mỗi 10s.
17. **[Session 17: Tích Hợp Ảnh Hiện Trường Thiên Tai Từ Cloudinary CDN](roadmap/session-17-community-feed-cloudinary-media.md)** *(✅ Đã hoàn thành)*
    - Tích hợp bộ ảnh thiên tai thực địa chất lượng cao lưu trữ tại Cloudinary CDN (`cuunan247/community_feed`) cho Bảng Tin Cộng Đồng (`/feed`). Nâng cấp giao diện xem ảnh phóng to toàn màn hình, huy hiệu Cloudinary CDN và chuẩn hóa `th:data-*` theo Thymeleaf 3.1+.
18. **[Session 18: Khung Chat Nổi Toàn Hệ Thống & Đa Cảm Xúc / Bình Luận Bảng Tin](roadmap/session-18-floating-chat-dock-feed-reactions-cleanup.md)** *(✅ Đã hoàn thành - PeterWeeee)*
    - Tích hợp Khung Chat Nổi toàn hệ thống (Persistent Floating Chat Dock) phong cách Facebook Messenger, tự động phát hiện ca cứu hộ qua `/api/chat/active-session`. Loại bỏ 2 khung chat inline dư thừa trên `sos/track.html` và `rescuer/routes.html`. Gỡ bỏ triệt để Toast popup đè màn hình. Nâng cấp Bảng Tin: 5 trạng thái cảm xúc (Multi-Reactions), bình luận bài viết (`post_comments`), sửa lỗi UTF-8 BOM SQL Server và cơ chế tự phục hồi bình luận mẫu (Self-Healing Comments), sửa lỗi lệch avatar người dùng và xóa thẻ khẩn cấp 24/7.
19. **[Session 19: Khắc Phục Popover Cảm Xúc Hover, Đồng Bộ Bình Luận & Tự Phục Hồi Unicode Database (Feed Reaction Popover & Unicode Healing)](roadmap/session-19-feed-reaction-popover-comment-sync-unicode-healing.md)** *(✅ Đã hoàn thành - PeterWeeee)*
    - Khắc phục lỗi thanh chọn cảm xúc reaction-popover bị lơ lửng đè lên thanh tóm tắt phản ứng.
    - Khắc phục lỗi số lượng bình luận hiển thị nhưng ấn vào không thấy bình luận qua API fetch động `loadCommentsForPost(postId)`.
    - Cơ chế tự phục hồi lỗi font tiếng Việt (dấu hỏi ?) do seed data qua phương thức đa tầng `repairCorruptedUnicodeData()` và `ensureCleanSampleComments()`.
20. **[Session 20: Đồng Bộ Cự Ly Tuyến Đường OSRM & Định Vị Live GPS Thời Gian Thực (Distance Synchronization & Live GPS Tracking)](roadmap/session-20-distance-synchronization-and-live-gps-tracking.md)** *(✅ Đã hoàn thành - PeterWeeee)*
    - Đồng bộ triệt để cự ly lộ trình di chuyển thực tế qua OSRM Routing API (4.14 km, ~7 phút) giữa Bản đồ lộ trình của đội cứu hộ (`rescuer/routes.html`) và Màn hình theo dõi của người bị nạn (`sos/track.html`).
    - Tích hợp vẽ tuyến đường OSRM GeoJSON trực quan trên bản đồ người bị nạn.
    - Cơ chế Live GPS Tracking tự động cập nhật vị trí cho cả 3 bên: Đội cứu hộ dùng `watchPosition`, người bị nạn có công tắc "Chia sẻ vị trí trực tiếp", bản đồ Điều phối viên tự trượt marker.
21. **[Session 21: Tích Hợp Chuyển Khoản Ngân Hàng Thật Qua VietQR & SePay Webhook (Real-time Donation Auto-Confirmation)](roadmap/session-21-vietqr-sepay-webhook-realtime-donation.md)** *(✅ Đã hoàn thành - PeterWeeee)*
    - Cấu hình tài khoản ngân hàng thật BIDV (`970418` - `962471ABQD` - `NGUYEN TRI THAI`) và tạo mã Dynamic VietQR Napas 24/7 tự động điền sẵn STK, số tiền và mã giao dịch `CNxxxx`.
    - Xây dựng API Webhook `POST /api/donations/sepay-webhook` tiếp nhận biến động số dư thời gian thực từ SePay, bảo mật bằng API Key.
    - Tự động trích xuất mã giao dịch bằng Regex, tự động cập nhật số tiền thực tế nhận được vào CSDL và cộng dồn quỹ cứu trợ tức thì.
    - Giao diện `pay.html` tự động nhận diện giao dịch thành công và chuyển hướng sang trang cảm ơn/biên nhận điện tử.
22. **[Session 22: Khắc Phục Phân Hệ Hồ Sơ Cá Nhân & Quản Trị Danh Tính (Profile 403 CSRF Fix, Local Avatar Upload, Email OTP)](roadmap/session-22-profile-fixes-avatar-upload-email-otp.md)** *(✅ Đã hoàn thành - PeterWeeee)*
    - Khắc phục triệt để lỗi 403 Forbidden ("Bạn không có quyền truy cập chức năng này!") khi lưu hồ sơ và đổi mật khẩu bằng việc miễn trừ CSRF cho `/profile/**` trong `SecurityConfig.java`.
    - Sửa liên kết "Ủng hộ quỹ cứu trợ" từ `@{/donate}` thành `@{/donations}` và bổ sung redirect fallback trong `HomeController.java`.
    - Hỗ trợ chọn tệp ảnh đại diện từ máy tính khi bấm vào icon ảnh hoặc nút "Chọn ảnh", tự động upload lên `/api/upload/image?folder=avatars` và cập nhật ảnh preview tức thì.
    - Tích hợp gửi mã xác thực OTP 6 số qua Email khi thay đổi email tài khoản, xác thực chặt chẽ tại `ProfileController.java` và `EmailService.java`.
23. **[Session Chưa Làm: Tối Ưu Mobile, Kiểm Thử & Kịch Bản Demo Bảo Vệ](roadmap/session-chua-lam-mobile-testing-demo-script.md)** *(⏸️ Chưa làm trong project này)*
    - Tối ưu hóa màn hình điện thoại di động, nạp dữ liệu mẫu tự động và kịch bản 5 bước thuyết trình bảo vệ đồ án (Dời về cuối lộ trình, chưa triển khai trong phạm vi đồ án hiện tại).
