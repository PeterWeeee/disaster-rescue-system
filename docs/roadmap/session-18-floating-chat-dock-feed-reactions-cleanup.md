# BÁO CÁO PHIÊN LÀM VIỆC - SESSION 18
> **Nội dung:** Tích hợp Khung Chat Nổi Toàn Hệ Thống (Floating Chat Dock) Phong Cách Facebook Messenger & Đa Cảm Xúc (Multi-Reactions) / Bình Luận Bảng Tin Cộng Đồng  
> **Người thực hiện:** Võ Văn Thịnh ([vovanthinh2006@gmail.com](mailto:vovanthinh2006@gmail.com) / `ThinhVo206`) & Nhóm 04  
> **Thời gian:** 29/10/2026  
> **Trạng thái:** ✅ ĐÃ HOÀN THÀNH (131/131 Tests Passed - 100% Build Success)

---

## I. TỔNG QUAN YÊU CẦU & BỐI CẢNH PHÁT TRIỂN

Trong giai đoạn hoàn thiện hệ thống cứu nạn thiên tai CứuNạn 24/7, nhóm tập trung nâng cao trải nghiệm người dùng (UX) và tối ưu hóa luồng tương tác thực tế giữa người dân bị nạn, đội cứu hộ cơ động và cộng đồng mạng xã hội cứu trợ:

1. **Khung Chat Nổi Toàn Hệ Thống (Persistent Floating Chat Dock):**
   - **Bối cảnh:** Trước đây, khung chat chỉ hiển thị cục bộ dưới dạng thẻ tĩnh (inline card) bên trong 2 trang riêng biệt: [`sos/track.html`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/resources/templates/sos/track.html) (người dân) và [`rescuer/routes.html`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/resources/templates/rescuer/routes.html) (đội cứu hộ). Khi người dùng chuyển sang các trang khác (trang chủ, bản đồ số, cẩm nang thiên tai, bảng tin cộng đồng), kênh liên lạc cứu hộ bị gián đoạn.
   - **Giải pháp:** Triển khai khung chat nổi toàn hệ thống phong cách Facebook Messenger (thu gọn góc phải dưới màn hình), tự động phát hiện ca cứu nạn đang tiếp cận (`IN_PROGRESS`) qua API `/api/chat/active-session`.
   - **Tinh gọn giao diện:** Loại bỏ hoàn toàn các khung chat inline cũ tại [`sos/track.html`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/resources/templates/sos/track.html) và [`rescuer/routes.html`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/resources/templates/rescuer/routes.html) để tránh hiện tượng nhân đôi giao diện chat trên cùng một màn hình.
   - **Xóa bỏ Toast đè giao diện:** Gỡ bỏ hoàn toàn popup Toast nhỏ (`#floatingChatToast`) từng gây hiện tượng hiển thị đè lên nút chip thao tác nhanh và ô nhập tin nhắn của cửa sổ chat. Thông báo tin nhắn mới khi chat đóng được quản lý tinh tế qua huy hiệu số tin chưa đọc (Unread Badge) và âm thanh chuông báo lịch sự.

2. **Hệ Thống Đa Cảm Xúc (Multi-Reactions) & Bình Luận Thời Gian Thực Bảng Tin Cộng Đồng:**
   - **Bối cảnh:** Bảng tin cộng đồng trước đây chỉ hỗ trợ upvote đơn thuần (`1-0`), chưa phản ánh được sự cảm thông, lo lắng hay hỗ trợ trong bối cảnh thiên tai. Ngoài ra chưa có tính năng bình luận tương tác để người dân cập nhật tình hình tức thời.
   - **Giải pháp:** Nâng cấp hệ thống cảm xúc thành 5 loại cảm xúc phong phú: `LIKE` (👍 Thích), `CARE` (❤️ Thương thương), `PRAY` (🙏 Cầu nguyện), `WARNING` (⚠️ Cảnh giác), `SAD` (😢 Chia buồn), đi kèm thanh popover thả cảm xúc hoạt họa mượt mà.
   - **Hệ thống bình luận:** Bổ sung bảng CSDL [`post_comments`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/java/vn/edu/cuunan/entity/PostComment.java), Entity, Repository, Service và REST APIs cho phép gửi và xem bình luận theo từng bài viết.
   - **Đồng bộ cơ sở dữ liệu:** Bổ sung Seed Data mẫu bình luận và cảm xúc đa dạng trên cả SQL Server ([`03_seed_data.sql`](file:///c:/Users/Will/Projects/disaster-rescue-system/database/03_seed_data.sql), [`fix_sqlserver_encoding.sql`](file:///c:/Users/Will/Projects/disaster-rescue-system/database/fix_sqlserver_encoding.sql)), MySQL ([`mysql_init.sql`](file:///c:/Users/Will/Projects/disaster-rescue-system/database/mysql_init.sql)) và [`DataInitializer.java`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/java/vn/edu/cuunan/config/DataInitializer.java).

3. **Tinh Gọn & Chuẩn Hóa Giao Diện Bảng Tin (`feed/index.html`):**
   - Xóa bỏ card "Khẩn Cấp 24/7 - Gặp Nguy Hiểm Tính Mạng" ở sidebar bên trái để tránh chiếm dụng không gian và gây phân tâm.
   - Sửa triệt để lỗi méo/lệch avatar người dùng: Bổ sung `flex-shrink-0`, `min-width: 44px; min-height: 44px;`, căn giữa nội dung `justify-content-center`, đồng thời thêm `min-width: 0;` và `text-truncate` cho chuỗi địa chỉ để chống co ép avatar khi địa chỉ quá dài.
   - Loại bỏ nút "Chuyển thành đơn SOS" không cần thiết trên thanh tác vụ của bài viết.

---

## II. CHI TIẾT KIẾN TRÚC & TRIỂN KHAI KỸ THUẬT

### 1. Phân Hệ Khung Chat Nổi Toàn Hệ Thống (Floating Chat Dock)

```
                            KIẾN TRÚC CHAT NỔI TOÀN HỆ THỐNG
┌────────────────────────────────────────────────────────────────────────┐
│                          layout/main.html                              │
│                                ↓                                       │
│                fragments/floating-chat.html                            │
│                                ↓                                       │
│                     static/js/floating-chat.js                         │
└───────────────────┬────────────────────────────────┬───────────────────┘
                    │                                │
    Kiểm tra phiên hoạt động              Truyền thông điệp 2 chiều
    GET /api/chat/active-session          WebSocket STOMP /topic/chat/{id}
                    │                     (Fallback: POST /api/chat/...)
                    ▼                                ▼
┌──────────────────────────────────────┐ ┌───────────────────────────────┐
│        ChatApiController.java        │ │       ChatService.java        │
│        - getActiveChatSession()      │ │       - getActiveChatSession()│
│        - handleWebSocketMessage()    │ │       - saveAndBroadcast()    │
└──────────────────────────────────────┘ └───────────────────────────────┘
```

#### A. Cấu trúc Fragment & Giao diện (`fragments/floating-chat.html`):
- **Bong bóng kích hoạt (`#floatingChatBubble`):**
  - Vị trí góc dưới cùng bên phải (`bottom: 24px; right: 24px; z-index: 1060;`).
  - Nút tròn gradient xanh cứu trợ với biểu tượng bình luận phát sáng.
  - Chấm tròn trạng thái xanh lá nhấp nháy (`.status-pulse-dot`).
  - Huy hiệu số tin nhắn chưa đọc (`#floatingChatBadge`).
- **Cửa sổ chat mở rộng (`#floatingChatWindow`):**
  - Kích thước $380 \times 540\text{ px}$, bo tròn góc 16px, hiệu ứng đổ bóng đa tầng.
  - Header tích hợp thông tin đối phương (Tên đội cứu hộ / Nạn nhân, số điện thoại, nút gọi khẩn `tel:`, nút mở lộ trình bản đồ GPS).
  - Vùng tin nhắn cuộn mượt `#floatingChatMessages` với cơ chế chống trùng lặp `chat-msg-{id}`.
  - Thanh phím tắt khẩn cấp 1-chạm (Quick chips): "Nước dâng cao!", "Pin điện thoại sắp cạn!", "Ở trên mái nhà", "Đang vẫy áo đỏ / chiếu đèn pin".
  - Ô nhập liệu và nút gửi tin nhắn SVG máy bay giấy.
- **Loại bỏ popup Toast đè giao diện:**
  - Trước đây khi có tin nhắn mới, `#floatingChatToast` nổi lên ở góc dưới màn hình. Tuy nhiên khi cửa sổ chat đang mở hoặc ở các kích thước màn hình hẹp, khung nhỏ này bị nổi đè lên các nút thao tác nhanh (chips) và khung nhập liệu.
  - Đã loại bỏ hoàn toàn mã HTML `#floatingChatToast`, các class CSS `.floating-chat-toast` và hàm `showToastNotification()` trong JavaScript.
  - Khi người dùng đang thu nhỏ khung chat, tin nhắn mới đến sẽ tự động tăng số đếm trên huy hiệu đỏ (`#floatingChatBadge`) và phát âm thanh thông báo `notification.mp3` mà không hề gây cản trở tầm nhìn.

#### B. Xử lý Logic Client (`static/js/floating-chat.js`):
- **Khởi tạo thông minh:** Kiểm tra `GET /api/chat/active-session` khi tải trang. Nếu có ca SOS đang tiếp cận liên quan đến tài khoản người dùng hoặc phiên hiện tại, tự động nạp cấu hình và kết nối WebSocket STOMP.
- **Phân tách tin nhắn ban đầu vs thời gian thực:**
  ```javascript
  function appendMessage(msg, isInitial = false) {
      // ... thêm tin nhắn vào DOM ...
      if (!isInitial && (!isOpen || document.hidden)) {
          showToast(msg);
          playNotificationSound();
      }
  }
  ```
  Nhờ cờ `isInitial = true`, khi người dùng mở trang hoặc chuyển trang, toàn bộ lịch sử trò chuyện được kéo về mà không gây khó chịu bởi chuông báo và popup liên tục.

#### C. Backend API Cung Cấp Thông Tin Phiên (`ChatApiController.java` & `ChatService.java`):
- Endpoint: `GET /api/chat/active-session?sosId={optional}`
- Logic xử lý:
  - Nếu là `ROLE_CITIZEN`: Tìm ca SOS gần nhất của người dùng hoặc khớp `sosId` có trạng thái `IN_PROGRESS` (kèm thông tin Đội cứu hộ đang tiếp cận).
  - Nếu là `ROLE_RESCUER`: Tìm nhiệm vụ cứu nạn của đội có trạng thái `IN_PROGRESS` (kèm thông tin Nạn nhân cần cứu).
  - Trả về JSON chứa `hasActiveChat: true`, `sosId`, `partnerName`, `partnerPhone`, `partnerRole`, `myRole`.

#### D. Loại Bỏ Khung Chat Inline Dư Thừa:
- Đã gỡ bỏ toàn bộ `#missionChatCard` và các script inline khỏi [`sos/track.html`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/resources/templates/sos/track.html).
- Đã gỡ bỏ toàn bộ `#rescuerChatCard` và các hàm switch phòng chat inline khỏi [`rescuer/routes.html`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/resources/templates/rescuer/routes.html).
- Giao diện hai trang trên trở nên cực kỳ thoáng đãng, tập trung trọn vẹn vào lộ trình GPS và trạng thái cứu nạn, trong khi chức năng trò chuyện được đảm nhiệm xuyên suốt bởi Floating Chat Dock.

---

### 2. Phân Hệ Đa Cảm Xúc & Bình Luận Bảng Tin Cộng Đồng

#### A. Mô hình Thực Thể JPA:
- **`PostComment.java`:**
  - `@Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;`
  - `@ManyToOne @JoinColumn(name = "post_id") CommunityPost post;`
  - `@ManyToOne @JoinColumn(name = "user_id") User user;`
  - `@Lob @Column(nullable = false) String content;`
  - `@Column(nullable = false) LocalDateTime createdAt;`
- **`PostUpvote.java`:**
  - Bổ sung trường `reactionType`: Lưu chuỗi enum (`LIKE`, `CARE`, `PRAY`, `WARNING`, `SAD`). Mặc định là `LIKE`.
  - Khóa phức hợp tự nhiên `(post_id, user_id)` đảm bảo mỗi người dùng chỉ có 1 trạng thái cảm xúc trên 1 bài viết.

#### B. Dịch Vụ Nghiệp Vụ (`CommunityFeedService.java`):
- `reactToPost(Long postId, User user, String reactionType)`: Nếu đã thả cùng cảm xúc $\rightarrow$ Hủy tương tác; nếu thả cảm xúc khác $\rightarrow$ Cập nhật loại cảm xúc mới; nếu chưa thả $\rightarrow$ Tạo bản ghi mới. Tự động tính toán lại tổng số lượt tương tác `upvoteCount`.
- `addComment(Long postId, User user, String content)`: Lưu bình luận mới, tự động tăng `commentCount` của bài viết và trả về thông tin bình luận kèm họ tên, avatar và thời gian định dạng.
- `getCommentsByPostId(Long postId)`: Lấy danh sách bình luận theo thứ tự thời gian tăng dần.

#### C. Giao Diện & Tương Tác Người Dùng (`feed/index.html`):
- **Thanh Reaction Popover:** Hiển thị 5 nút emoji nổi bật khi rê chuột (hover trên Desktop) hoặc giữ chạm (trên Mobile).
- **Hộp thoại bình luận gập/mở (Collapsible Comments):** Cho phép xem danh sách bình luận, nạp bình luận bằng AJAX và gửi bình luận mới mà không cần tải lại trang.
- **Bộ dữ liệu mẫu đa dạng (Seed Data):** Nạp sẵn hàng chục lượt tương tác cảm xúc và các bình luận động viên, thông báo tình hình cứu nạn từ các tài khoản thực tế.

---

### 3. Tinh Gọn Giao Diện Bảng Tin & Chuẩn Hóa Avatar Người Dùng

1. **Xóa Khung Khẩn Cấp 24/7:** Gỡ bỏ thẻ `.emergency-sos-card` khỏi cột bên trái của [`feed/index.html`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/resources/templates/feed/index.html).
2. **Sửa Lỗi Avatar Bị Co Dúm / Lệch:**
   - Trước đây: Khi người đăng bài có địa chỉ dài, khối Flexbox bị co ép làm phần hiển thị ảnh đại diện tròn bị ép dẹp thành hình bầu dục méo mó.
   - Sửa chữa:
     - Thêm `flex-shrink: 0; min-width: 44px; min-height: 44px; width: 44px; height: 44px;` vào container avatar.
     - Thêm `d-inline-flex align-items-center justify-content-center` để chữ cái đại diện luôn nằm chính tâm hoàn hảo.
     - Bổ sung `style="min-width: 0;"` và class `text-truncate` cho container tên và địa chỉ để văn bản tự ngắt dấu `...` khi vượt quá độ rộng, tuyệt đối không chèn ép avatar.
3. **Xóa Nút "Chuyển thành đơn SOS":** Loại bỏ nút hành động thừa trên các bài đăng nhằm phân định rõ giữa Bảng Tin chia sẻ hiện trường cộng đồng và Luồng gửi đơn SOS chính thức.

---

## III. DANH MỤC TỆP TIN THAY ĐỔI

| STT | Tệp tin | Loại thay đổi | Mô tả |
| :---: | :--- | :---: | :--- |
| **1** | [`src/main/resources/templates/fragments/floating-chat.html`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/resources/templates/fragments/floating-chat.html) | Tạo mới & Tối ưu | Giao diện Khung chat nổi (Launcher bubble, Window, Toast thông báo, nút đóng X chuẩn xác) |
| **2** | [`src/main/resources/static/js/floating-chat.js`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/resources/static/js/floating-chat.js) | Tạo mới & Tối ưu | Logic JavaScript chat nổi STOMP over SockJS, cờ `isInitial` chống spam chuông, xử lý dismiss toast |
| **3** | [`src/main/resources/templates/layout/main.html`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/resources/templates/layout/main.html) | Chỉnh sửa | Nhúng fragment floating-chat vào layout dùng chung toàn hệ thống |
| **4** | [`src/main/resources/templates/sos/track.html`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/resources/templates/sos/track.html) | Tinh gọn | Gỡ bỏ khung chat inline `#missionChatCard` và script inline dư thừa |
| **5** | [`src/main/resources/templates/rescuer/routes.html`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/resources/templates/rescuer/routes.html) | Tinh gọn | Gỡ bỏ khung chat inline `#rescuerChatCard` và các script inline chuyển phòng chat |
| **6** | [`src/main/java/vn/edu/cuunan/entity/PostComment.java`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/java/vn/edu/cuunan/entity/PostComment.java) | Tạo mới | JPA Entity lưu trữ bình luận bài đăng cộng đồng |
| **7** | [`src/main/java/vn/edu/cuunan/repository/PostCommentRepository.java`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/java/vn/edu/cuunan/repository/PostCommentRepository.java) | Tạo mới | Spring Data JPA Repository cho bình luận bài viết |
| **8** | [`src/main/java/vn/edu/cuunan/entity/PostUpvote.java`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/java/vn/edu/cuunan/entity/PostUpvote.java) | Nâng cấp | Bổ sung cột `reaction_type` cho 5 loại cảm xúc |
| **9** | [`src/main/java/vn/edu/cuunan/service/CommunityFeedService.java`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/java/vn/edu/cuunan/service/CommunityFeedService.java) | Nâng cấp | Xử lý nghiệp vụ reactions, bình luận, khởi tạo bình luận mẫu |
| **10** | [`src/main/java/vn/edu/cuunan/controller/FeedApiController.java`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/java/vn/edu/cuunan/controller/FeedApiController.java) | Nâng cấp | Bổ sung REST APIs: `/posts/{id}/react`, `/posts/{id}/comments` (GET & POST) |
| **11** | [`src/main/java/vn/edu/cuunan/controller/ChatApiController.java`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/java/vn/edu/cuunan/controller/ChatApiController.java) | Nâng cấp | Bổ sung REST API `/api/chat/active-session` |
| **12** | [`src/main/java/vn/edu/cuunan/service/ChatService.java`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/java/vn/edu/cuunan/service/ChatService.java) | Nâng cấp | Logic xác định phiên chat hoạt động cho Citizen và Rescuer |
| **13** | [`src/main/resources/templates/feed/index.html`](file:///c:/Users/Will/Projects/disaster-rescue-system/src/main/resources/templates/feed/index.html) | Nâng cấp | Giao diện popover reactions, khung bình luận, sửa avatar, xóa card khẩn cấp 24/7 và nút chuyển SOS |
| **14** | [`database/02_create_tables.sql`](file:///c:/Users/Will/Projects/disaster-rescue-system/database/02_create_tables.sql) & [`database/mysql_init.sql`](file:///c:/Users/Will/Projects/disaster-rescue-system/database/mysql_init.sql) | Nâng cấp | DDL tạo bảng `post_comments` và cập nhật cột `reaction_type` |
| **15** | [`database/03_seed_data.sql`](file:///c:/Users/Will/Projects/disaster-rescue-system/database/03_seed_data.sql) & [`database/fix_sqlserver_encoding.sql`](file:///c:/Users/Will/Projects/disaster-rescue-system/database/fix_sqlserver_encoding.sql) | Nâng cấp | Dữ liệu mẫu bình luận và cảm xúc tiếng Việt chuẩn Unicode |

---

## IV. KẾT QUẢ KIỂM THỬ (TEST RESULTS)

Toàn bộ bộ kiểm thử tự động của dự án đã được thực thi và xác nhận:
```bash
mvn test
```

- **Kết quả tổng kết:**
  - **Tests run: 131**
  - **Failures: 0**
  - **Errors: 0**
  - **Skipped: 0**
  - **Trạng thái: BUILD SUCCESS (100% Passed)**
- **Kiểm thử tính năng thực tế:**
  1. Khi ca cứu nạn chuyển sang `IN_PROGRESS`, bong bóng chat nổi lập tức xuất hiện ở góc phải trên tất cả các trang của hệ thống.
  2. Nạn nhân và Đội cứu hộ trao đổi tin nhắn hai chiều tức thời qua WebSocket STOMP.
  3. Tin nhắn mới được nhận diện theo thời gian thực; khi khung chat đóng, huy hiệu đỏ cập nhật chính xác số tin nhắn chưa đọc kèm âm thanh báo hiệu nhẹ nhàng, loại bỏ hoàn toàn khung toast đè tầm nhìn.
  4. Tải lại trang: Lịch sử tin nhắn được nạp đầy đủ vào khung chat mà không gây phiền toái bởi chuông hay thông báo ảo.
  5. Bảng Tin Cộng Đồng: Người dùng có thể thả 5 loại cảm xúc (`LIKE`, `CARE`, `PRAY`, `WARNING`, `SAD`) mượt mà. Hệ thống tự động nạp bình luận mẫu (Self-Healing Comments) và tự động kéo bình luận mới nhất qua AJAX khi mở rộng danh sách bình luận.
  6. Avatar người dùng hiển thị tròn đều hoàn hảo, không bị méo hay vỡ giao diện trên mọi độ phân giải màn hình.
