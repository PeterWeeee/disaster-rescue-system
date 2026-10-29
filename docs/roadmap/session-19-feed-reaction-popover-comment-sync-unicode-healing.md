# PHIÊN LÀM VIỆC #19: SỬA LỖI THANH CẢM XÚC POPOVER, ĐỒNG BỘ HIỂN THỊ BÌNH LUẬN & TỰ PHỤC HỒI UNICODE TIẾNG VIỆT
> **Trạng thái:** ✅ ĐÃ HOÀN THÀNH (100%)  
> **Người phụ trách:** **Tri Thai (PeterWeeee - `NguyenTT162.4@gmail.com`)**  
> **Thời điểm hoàn thành:** 29/10/2026  
> **Nhánh thực hiện:** `develop`  
> **Kết quả kiểm thử:** 131/131 bài test passed (BUILD SUCCESS 100%)

---

## 1. Bối Cảnh Yêu Cầu & Triệu Chứng Lỗi Phát Sinh

Sau phiên làm việc #18, hệ thống đã tích hợp khung chat nổi Facebook Messenger và đa cảm xúc/bình luận cho Bảng tin cộng đồng (`/feed`). Tuy nhiên, trong quá trình kiểm thử thực tế trên giao diện và cơ sở dữ liệu, nhóm phát hiện 3 vấn đề cần xử lý triệt để:

### 1.1. Thanh chọn cảm xúc bị lơ lửng vĩnh viễn trên giao diện bài viết
* **Triệu chứng:** Khi mở trang Bảng tin `/feed`, thanh emoji nổi (`👍 ❤️ 🙏 ⚠️ 😢`) luôn luôn hiển thị lơ lửng bên trên nút bấm, che mất dòng tóm tắt tương tác ("tương tác", "2 bình luận").
* **Nguyên nhân kỹ thuật gốc rễ:**
  - Thẻ `reaction-popover` trong [`feed/index.html`](../../src/main/resources/templates/feed/index.html) mang class Bootstrap `d-flex` (`display: flex !important;`).
  - Thuộc tính `!important` của Bootstrap 5 đã đè hoàn toàn lên khai báo CSS `display: none;` của `.reaction-popover`, khiến thanh chọn cảm xúc không bao giờ ẩn đi được khi không có hover.

### 1.2. Nút bình luận hiển thị số lượng nhưng bấm vào không thấy bình luận nào
* **Triệu chứng:** Nút bấm hiển thị "Bình luận (2)" hoặc "Bình luận (6)", nhưng khi người dùng bấm vào thì khung hiển thị rỗng: *"Chưa có bình luận nào. Hãy là người đầu tiên bình luận!"*.
* **Nguyên nhân kỹ thuật gốc rễ:**
  1. Trong [`DataInitializer.java`](../../src/main/java/vn/edu/cuunan/config/DataInitializer.java), các bài viết (ví dụ bài *Cil Ha Kôi* - Đèo Prenn, *Nguyễn Văn Dân* - Mẹ Suốt,...) được gán cứng giá trị `commentCount = 2, 6, 4, 12,...` vào cột `comment_count` của bảng `community_posts`, nhưng mã nguồn chưa từng tạo bản ghi tương ứng nào vào bảng `post_comments`.
  2. Phương thức `initSampleComments()` trong `CommunityFeedService` trước đây chỉ kích hoạt nếu `commentRepository.count() == 0`. Nếu CSDL đã có bình luận của các bài viết khác thì các bài viết còn lại bị bỏ qua hoàn toàn.
  3. Cú pháp SpEL `${commentsMap[post.id]}` có thể gặp rủi ro khi ép kiểu khóa `Long` trong Thymeleaf, và hàm JavaScript `loadCommentsForPost` khi nhận phản hồi mảng rỗng `[]` chưa hiển thị thông báo trạng thái đồng bộ.

### 1.3. Lỗi font tiếng Việt (dấu hỏi `?`) trong bình luận, thông báo, nhật ký admin và bài viết
* **Triệu chứng:** Các bình luận nạp lên hiển thị người bình luận nhưng nội dung dính ký tự `?` (ví dụ: *"B? con x?m 2 ch? ? tuy?t ??i kh?ng ?i...*). Hiện tượng tương tự từng xuất hiện ở thông báo (`notifications`) và nhật ký kiểm toán (`activity_logs`).
* **Nguyên nhân kỹ thuật gốc rễ:**
  1. File seed data SQL đưa lên GitHub trước đó từng được lưu dưới dạng `UTF-8 without BOM`. Khi nạp bằng SSMS (SQL Server Management Studio) hoặc dòng lệnh `sqlcmd` trên Windows, SQL Server mặc định đọc theo ANSI (Windows-1252), làm hỏng ký tự tiếng Việt thành dấu `?` trước khi ghi vào đĩa.
  2. Bảng `post_comments` trước đây chưa được quét và sửa tự động trong phương thức tự phục hồi của `DataInitializer`, khiến dữ liệu cũ đã bị lưu dấu `?` trong CSDL SQL Server vẫn hiển thị lỗi font lên giao diện.

---

## 2. Các Giải Pháp Kỹ Thuật Đã Triển Khai

### 2.1. Khắc phục thanh chọn cảm xúc Popover hiển thị chuẩn Hover
* **Trong [`feed/index.html`](../../src/main/resources/templates/feed/index.html):**
  - Gỡ bỏ class `d-flex` khỏi `<div class="reaction-popover shadow-lg rounded-pill p-1 bg-white border align-items-center gap-1">`.
  - Cấu hình CSS kiểm soát trạng thái popover:
    ```css
    .reaction-popover {
        position: absolute;
        bottom: calc(100% + 8px);
        left: 0;
        z-index: 1050;
        background: #ffffff;
        padding: 4px 10px;
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15) !important;
        display: none !important;
        pointer-events: none;
        align-items: center;
        gap: 6px;
        border: 1px solid rgba(0, 0, 0, 0.08);
        animation: popoverBounce 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    }

    /* Cầu đệm vô hình nối nút bấm và popover để tránh giật/chớp tắt chuột khi di chuyển */
    .reaction-popover::after {
        content: '';
        position: absolute;
        bottom: -12px;
        left: 0;
        width: 100%;
        height: 12px;
        background: transparent;
    }

    .reaction-container:hover .reaction-popover,
    .reaction-container:focus-within .reaction-popover {
        display: flex !important;
        pointer-events: auto;
    }
    ```

### 2.2. Đồng bộ hiển thị & Nạp bình luận thực tế cho toàn bộ bài viết
* **Trong [`CommunityFeedService.java`](../../src/main/java/vn/edu/cuunan/service/CommunityFeedService.java):**
  - Nâng cấp `initSampleComments()`: Duyệt qua toàn bộ danh sách bài viết trong `postRepository.findAll()`. Với mỗi bài viết có `commentRepository.countByPostId(post.getId()) == 0`, hệ thống tự động sinh bình luận mẫu với ngữ cảnh cứu nạn thực tế, ảnh đại diện Unsplash và thời gian chân thực.
  - Tự động đồng bộ số lượng `post.setCommentCount((int) countInDb)` đảm bảo số hiển thị trên nút bấm luôn khớp 100% với số lượng bản ghi trong CSDL.
  - Thêm phương thức `ensureCleanSampleComments()`: Tự động kiểm tra trước mỗi lần gọi `getRecentPosts()`, `getCommentsByPostId()` hoặc `getAllCommentsGroupedByPostId()`. Nếu phát hiện bình luận dính dấu `?`, hệ thống tự động xóa sạch và tái tạo bình luận tiếng Việt chuẩn.
* **Trong [`feed/index.html`](../../src/main/resources/templates/feed/index.html):**
  - Đổi cú pháp truy xuất Map sang `commentsMap.get(post.id)` an toàn tuyệt đối kiểu dữ liệu `Long`.
  - Nâng cấp JavaScript `loadCommentsForPost(postId)`: Khi nhận dữ liệu từ REST API, xử lý cả 2 trường hợp `comments.length === 0` (hiển thị thông báo chưa có bình luận và đồng bộ counter về 0) và `comments.length > 0` (render đầy đủ bubble bình luận, avatar và cập nhật đồng bộ các nhãn đếm).

### 2.3. Tự phục hồi Unicode tiếng Việt (Self-Healing) & Nâng cấp Script SQL Server
* **Trong [`DataInitializer.java`](../../src/main/java/vn/edu/cuunan/config/DataInitializer.java):**
  - Tích hợp `@Autowired(required = false) private PostCommentRepository postCommentRepository;`.
  - Triển khai phương thức `repairCorruptedUnicodeData()` tự động thực thi ngay khi khởi động:
    1. **Thông báo (`notifications`):** Quét và phục hồi toàn bộ thông báo `SOS_NEW`, `MISSION_NEW` có dấu `?` thành tiếng Việt chuẩn.
    2. **Nhật ký hệ thống Admin (`activity_logs`):** Quét và phục hồi toàn bộ các nhật ký điều phối, xuất kho trang bị, xuất kích cứu hộ, duyệt ngân sách (`SYSTEM_INIT`, `DISPATCH_MISSION`, `ACCEPT_MISSION`, `DISPATCH_EQUIPMENT`, `START_MISSION`, `APPROVE_BUDGET_REQUEST`).
    3. **Bài đăng cộng đồng (`community_posts`):** Quét và phục hồi nội dung, tên tác giả và địa bàn cho 13 khu vực (Đèo Prenn, Mẹ Suốt, Trà Mai, Cẩm Phô, Tam Kỳ, Bạch Đằng, Duy Vinh, Quế Long, Đèo Le, Nha Trang, Krông Pa,...).
    4. **Cảnh báo thiên tai (`disaster_alerts`):** Phục hồi đầy đủ bản tin lũ sông Mã, sông Hương, sạt lở Tây Nguyên, sóng biển Nam Trung Bộ.
    5. **Bình luận bài viết (`post_comments`):** Quét toàn bộ bảng `post_comments`, nếu phát hiện bình luận lỗi font dấu `?`, hệ thống tự động làm sạch và nạp lại bình luận chuẩn UTF-8.
* **Nâng cấp [`database/fix_sqlserver_encoding.sql`](../../database/fix_sqlserver_encoding.sql):**
  - Lưu chuẩn định dạng **UTF-8 with BOM** (`0xEF 0xBB 0xBF`).
  - Bổ sung đầy đủ lệnh `ALTER TABLE ... ALTER COLUMN ... NVARCHAR(...)` cho 15 bảng.
  - Bổ sung toàn bộ lệnh `UPDATE` phục hồi tiếng Việt sạch cho `notifications`, `activity_logs`, `community_posts`, `disaster_alerts`, `post_comments` và lệnh xóa bỏ dứt điểm mọi bản ghi rác dính dấu `?`.

---

## 3. Kết Quả Kiểm Thử & Nghiệm Thu

1. **Biên dịch & Kiểm thử đơn vị (Unit & Integration Tests):**
   - Lệnh `mvn test-compile`: **BUILD SUCCESS**.
   - Lệnh `mvn test`: Chạy toàn bộ **131/131 bài test**, kết quả **0 failures, 0 errors** (BUILD SUCCESS 100%).
2. **Kiểm thử trực quan giao diện:**
   - Thanh chọn cảm xúc ẩn hoàn toàn khi tải trang, chỉ xuất hiện mượt mà khi di chuột vào nút *"Cảm xúc"* và biến mất khi rời chuột.
   - Bấm vào nút *"Bình luận"* hiển thị ngay danh sách bình luận thực tế với avatar, tên người gửi, thời gian và nội dung cứu trợ rõ ràng, không còn hiện tượng rỗng danh sách hay lệch số đếm.
   - Toàn bộ bình luận, bài viết, thông báo và nhật ký kiểm toán admin hiển thị tiếng Việt có dấu chuẩn đẹp mắt, không còn bất kỳ dấu chấm hỏi (`?`) nào.
