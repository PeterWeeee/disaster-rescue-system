# PHIÊN LÀM VIỆC #12: TÍCH HỢP KHUNG CHAT TRỰC TUYẾN THỜI GIAN THỰC GIỮA NẠN NHÂN SOS VÀ ĐỘI CỨU HỘ KHI ĐANG TIẾP CẬN (REAL-TIME RESCUE MISSION CHAT)
> **Trạng thái:** ✅ ĐÃ HOÀN THÀNH 100% (IMPLEMENTATION COMPLETED & VERIFIED 100%)  
> **Người phụ trách:** **Tri Thai (PeterWeeee - `NguyenTT162.4@gmail.com`)**  
> **Thời điểm lập:** 28/10/2026  
> **Nhánh thực hiện dự kiến:** `ui-fixes` / `feature/mission-chat`

---

## 1. Bối Cảnh Thực Tế & Mục Tiêu Nghiệp Vụ

### 1.1. Bối Cảnh Thực Địa Cứu Nạn
- Trong tình huống thiên tai khẩn cấp (lũ quét, ngập lụt dâng cao ban đêm, sạt lở chia cắt giao thông), sau khi Đội cứu hộ bắt đầu xuất kích và chuyển trạng thái sang **"Đang tiếp cận" (`IN_PROGRESS`)**, việc liên lạc thoại qua mạng viễn thông truyền thống thường gặp nhiều hạn chế:
  - Nạn nhân phải tiết kiệm pin điện thoại ở mức tối đa do mất điện diện rộng, không thể duy trì cuộc gọi thoại kéo dài.
  - Tiếng mưa bão gầm rú, nước chảy xiết và tiếng động cơ xuồng máy làm cho cuộc gọi thoại rất khó nghe.
  - Nạn nhân có thể đang ẩn nấp trên trần nhà, ngọn cây, gác xép cao cần gửi thông tin chi tiết bằng chữ hoặc tin nhắn 1-chạm (ví dụ: *"Nước đã ngập tới mái tôn", "Nhà có bé 6 tháng tuổi bị sốt", "Đang dùng đèn pin nhấp nháy áo đỏ"*).
- **Yêu cầu cấp bách:** Cần một kênh trao đổi thông tin 2 chiều tức thời (Real-time Mission Chat) được kích hoạt tự động ngay khi Đội cứu hộ đang trên đường tiếp cận, kết nối trực tiếp nạn nhân và đội cứu hộ phụ trách.

### 1.2. Mục Tiêu Cụ Thể Của Phiên
1. **Kích hoạt Khung Chat tại Màn hình Theo dõi Ca Cứu Nạn (`sos/track.html`):**
   - Khi ca SOS chuyển sang trạng thái `IN_PROGRESS` (Đội cứu hộ đang tiếp cận hiện trường), giao diện màn hình theo dõi sẽ tự động hiển thị khung chat trực tuyến kết nối với Đội cứu hộ được chỉ định.
   - Cho phép người dân gửi tin nhắn văn bản và các tin nhắn khẩn cấp 1-chạm (Quick SOS chips).
2. **Kích hoạt Khung Chat tại Màn hình Bản đồ & Lộ trình Cứu Hộ (`rescuer/routes.html`):**
   - Khi Đội cứu hộ xem lộ trình di chuyển tới ca cứu nạn đang tiếp cận, khung chat tương ứng của ca đó sẽ xuất hiện ngay cạnh bản đồ số GIS.
   - Đội viên/Đội trưởng có thể nhắn tin dặn dò, hướng dẫn nạn nhân giữ an toàn, thông báo vị trí xuồng máy và nhận phản hồi trực tiếp từ hiện trường.
3. **Nhắn Tin 2 Chiều Thời Gian Thực (Bi-directional Real-time Messaging):**
   - Ứng dụng hạ tầng **WebSocket STOMP over SockJS** (kênh `/topic/chat/{sosId}`) kết hợp với cơ chế **REST API Fallback** đảm bảo tin nhắn không bị thất lạc kể cả khi đường truyền mạng 3G/4G chập chờn.
4. **Lưu Trữ Bền Vững (Persistent Storage):**
   - Toàn bộ nội dung trò chuyện được lưu trữ trong cơ sở dữ liệu (`rescue_chat_messages`), cho phép tải lại lịch sử trao đổi khi người dùng tải lại trang (F5) hoặc đổi thiết bị.
5. **Đóng Kênh Khi Hoàn Tất:**
   - Khi ca cứu hộ hoàn thành (`RESOLVED`), khung chat tự động chuyển sang trạng thái "Chỉ đọc / Lưu trữ hồ sơ" (Read-only Archive) để làm bằng chứng nghiệm thu cứu trợ.

---

## 2. Thiết Kế Kiến Trúc & Đặc Tả Kỹ Thuật (Architecture & Tech Specs)

### 2.1. Sơ Đồ Luồng Hoạt Động (Message Flow Diagram)

```
┌─────────────────────────────────┐                    ┌─────────────────────────────────┐
│     NẠN NHÂN (Citizen SOS)      │                    │     ĐỘI CỨU HỘ (Rescuer Team)   │
│       Màn hình sos/track        │                    │    Màn hình rescuer/routes      │
└────────────────┬────────────────┘                    └────────────────┬────────────────┘
                 │                                                      │
                 │ 1. Đội bấm "Bắt đầu xuất kích tiếp cận"              │
                 │    (Trạng thái SOS -> IN_PROGRESS)                   │
                 │ <────────────────────────────────────────────────────┤
                 │                                                      │
                 │ 2. Khung chat tự động kích hoạt trên cả 2 màn hình   │
                 │ ◄──────────────────────────────────────────────────► │
                 │                                                      │
                 │ 3. Gửi tin: "Nước ngập ngang ngực rồi!"              │
                 │ ───► [STOMP: /app/chat/{sosId}/send] ────┐           │
                 │                                          │           │
                 │                                 [Spring WebSocket]   │
                 │                                          │           │
                 │                                 Lưu DB + Broadcast   │
                 │                                          │           │
                 │ 4. Nhận tin tức thì (< 500ms)            ▼           │
                 │ ◄─── [Topic: /topic/chat/{sosId}] ─────────────────► │
                 │                                                      │
                 │ 5. Đội nhắn lại: "Đội cách bạn 300m, hãy vẫy đèn!"  │
                 │ ◄─────────────────────────────────────────────────── │
```

### 2.2. Thiết Kế Cơ Sở Dữ Liệu (Database Schema)

#### Bảng `rescue_chat_messages`
```sql
CREATE TABLE [dbo].[rescue_chat_messages] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [sos_request_id] BIGINT NOT NULL,
    [assignment_id] BIGINT NULL,
    [sender_type] VARCHAR(20) NOT NULL,       -- 'CITIZEN', 'RESCUER', 'COORDINATOR', 'SYSTEM'
    [sender_name] NVARCHAR(100) NOT NULL,      -- Tên người gửi (Vd: Nguyễn Văn A hoặc Đội Hải Châu 1)
    [sender_phone] VARCHAR(20) NULL,           -- SĐT người gửi
    [content] NVARCHAR(1000) NOT NULL,         -- Nội dung tin nhắn
    [message_type] VARCHAR(30) DEFAULT 'TEXT', -- 'TEXT', 'QUICK_ALERT', 'LOCATION_PING', 'SYSTEM_INFO'
    [is_read] BIT DEFAULT 0,
    [created_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT [PK_rescue_chat_messages] PRIMARY KEY CLUSTERED ([id] ASC),
    CONSTRAINT [FK_chat_sos] FOREIGN KEY ([sos_request_id]) REFERENCES [dbo].[sos_requests] ([id]) ON DELETE CASCADE,
    CONSTRAINT [FK_chat_assignment] FOREIGN KEY ([assignment_id]) REFERENCES [dbo].[rescue_assignments] ([id])
);

CREATE INDEX [IX_rescue_chat_sos_time] ON [dbo].[rescue_chat_messages] ([sos_request_id], [created_at] ASC);
```

#### Phiên bản tương thích MySQL (`database/mysql_init.sql`):
```sql
CREATE TABLE IF NOT EXISTS rescue_chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sos_request_id BIGINT NOT NULL,
    assignment_id BIGINT NULL,
    sender_type VARCHAR(20) NOT NULL,
    sender_name VARCHAR(100) NOT NULL,
    sender_phone VARCHAR(20) NULL,
    content TEXT NOT NULL,
    message_type VARCHAR(30) DEFAULT 'TEXT',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_chat_sos (sos_request_id, created_at),
    CONSTRAINT fk_chat_sos FOREIGN KEY (sos_request_id) REFERENCES sos_requests (id) ON DELETE CASCADE,
    CONSTRAINT fk_chat_assignment FOREIGN KEY (assignment_id) REFERENCES rescue_assignments (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 2.3. Cấu Trúc Backend & API Endpoints

1. **JPA Entity:** `vn.edu.cuunan.entity.RescueChatMessage.java`
   - Quan hệ `@ManyToOne` với `SosRequest` và `RescueAssignment`.
2. **Repository:** `vn.edu.cuunan.repository.RescueChatMessageRepository.java`
   - `List<RescueChatMessage> findBySosRequestIdOrderByCreatedAtAsc(Long sosRequestId);`
   - `long countBySosRequestId(Long sosRequestId);`
3. **DTOs:**
   - `ChatMessageDto.java`: Truyền tải dữ liệu tin nhắn qua WebSocket và REST API (id, sosId, senderType, senderName, senderPhone, content, messageType, formattedTime, isMine).
   - `SendChatMessageRequest.java`: DTO gửi tin từ client.
4. **WebSocket STOMP Channel:**
   - Điểm nhận tin từ Client: `@MessageMapping("/chat/{sosId}/send")`
   - Kênh phát tin đến các Subscribers: `@SendTo("/topic/chat/{sosId}")`
5. **REST API Controller (`ChatApiController.java`):**
   - `GET /api/chat/sos/{sosId}/messages`: Lấy danh sách toàn bộ lịch sử tin nhắn của ca SOS.
   - `POST /api/chat/sos/{sosId}/messages`: Gửi tin nhắn qua HTTP (phục vụ fallback khi kết nối WebSocket bị gián đoạn).
   - `GET /api/chat/sos/{sosId}/status`: Kiểm tra điều kiện mở phòng chat (`canChat = true` khi trạng thái là `IN_PROGRESS`).

---

## 3. Thiết Kế Giao Diện Khung Chat (UI/UX Mockup & Implementation)

### 3.1. Giao Diện Trên Màn Hình Theo Dõi SOS Của Người Dân (`templates/sos/track.html`)
- **Điều kiện hiển thị:** Khối chat hiển thị ngay dưới thẻ `liveTrackingCard` khi ca cứu nạn ở trạng thái `IN_PROGRESS`.
- **Cấu trúc Khung Chat:**
  - **Header:**
    - Tiêu đề: `💬 Kênh Liên Lạc Trực Tiếp Với Đội Cứu Hộ`
    - Tên đội: `[Tên đội được gán]` - Hotline: `[SĐT đội]`
    - Trạng thái Real-time: Chấm tròn xanh nhấp nháy `<span class="badge bg-success"><i class="fa-solid fa-signal"></i> Trực Tuyến</span>`
    - Nút thu gọn / mở rộng (Collapse toggle).
  - **Khu vực Tin nhắn (Message Scroll Area):**
    - Chiều cao cố định ~280px - 320px, tự động cuộn xuống dưới cùng (`scrollTop = scrollHeight`).
    - Tin nhắn người dân (Của bạn): Nằm bên phải, nền xanh dương `#0d6efd`, chữ trắng.
    - Tin nhắn Đội cứu hộ: Nằm bên trái, nền xám nhạt `#f1f5f9` viền xanh `#0d6efd`, chữ tối kèm huy hiệu `🚑 Đội Cứu Hộ`.
    - Mốc thời gian nhỏ bên dưới mỗi tin nhắn (Vd: `14:25`).
  - **Bộ Gợi Ý Tin Nhắn Nhanh 1-Chạm (Quick SOS Chips):**
    - `🌊 Nước đang dâng cao thêm!`
    - `🔋 Pin điện thoại của tôi sắp cạn!`
    - `🏠 Chúng tôi đang ở trên mái nhà / tầng thượng`
    - `👶 Trong nhà có trẻ sơ sinh / người già yếu`
    - `🔦 Đang vẫy áo đỏ / chiếu đèn pin báo hiệu`
  - **Thanh Nhập Liệu (Input Box):**
    - Input text placeholder: *"Nhập tin nhắn gửi đến đội cứu hộ..."*
    - Nút Gửi màu xanh / đỏ khẩn cấp có icon máy bay giấy `fa-paper-plane`.
    - Hỗ trợ nhấn phím `Enter` để gửi ngay.

### 3.2. Giao Diện Trên Màn Hình Lộ Trình Cứu Hộ Của Đội (`templates/rescuer/routes.html`)
- **Vị trí tích hợp:**
  - Bổ sung Card khung chat chuyên dụng đặt ngay dưới khối "Chi Tiết Tuyến Đường & Khí Tượng" (Cột trái) hoặc dưới bản đồ Leaflet.
- **Tính năng chuyên biệt cho Đội cứu hộ:**
  - Nhận diện tự động ca SOS đang được chọn trong danh sách nhiệm vụ.
  - Hiển thị thông tin người liên hệ: Tên nạn nhân, số điện thoại, số lượng người gặp nạn.
  - **Bộ Gợi Ý Tin Nhắn Nhanh Dành Cho Đội Cứu Hộ:**
    - `🚤 Đội đã xuất phát và đang di chuyển hết tốc lực`
    - `📍 Đội đang cách bạn khoảng 300m - 500m`
    - `🔦 Hãy vẫy vải sáng màu hoặc bật đèn pin phát tín hiệu`
    - `⚠️ Ở nguyên vị trí an toàn, tuyệt đối không bơi ra dòng nước xiết`
    - `📞 Hãy giữ máy, đội chuẩn bị tiếp cận nhà bạn`

---

## 4. Kế Hoạch Triển Khai Chi Tiết (Implementation Checklist)

### 4.1. Cơ Sở Dữ Liệu & Entity Model
- [x] Bổ sung bảng `rescue_chat_messages` vào các script `database/02_create_tables.sql` và `database/mysql_init.sql`.
- [x] Tạo JPA Entity `vn.edu.cuunan.entity.RescueChatMessage.java`.
- [x] Tạo Spring Data JPA Repository `vn.edu.cuunan.repository.RescueChatMessageRepository.java`.

### 4.2. DTO & Backend Service
- [x] Tạo DTO `ChatMessageDto.java` và `SendChatMessageRequest.java`.
- [x] Xây dựng `ChatService.java`:
  - `List<ChatMessageDto> getMessagesBySosId(Long sosId, User currentUser, String citizenPhone);`
  - `ChatMessageDto saveAndBroadcastMessage(Long sosId, SendChatMessageRequest request, User currentUser);`
  - `boolean isChatAllowed(Long sosId);` (Kiểm tra `status == 'IN_PROGRESS'`).

### 4.3. WebSocket Handler & REST Controller
- [x] Tạo `ChatApiController.java` tiếp nhận REST API (`/api/chat/sos/{sosId}/messages`).
- [x] Bổ sung WebSocket Message Handler `@MessageMapping("/chat/{sosId}/send")` bắn dữ liệu qua `SimpMessagingTemplate` tới `/topic/chat/{sosId}`.

### 4.4. Tích Hợp Frontend
- [x] **Màn hình `sos/track.html`:**
  - Bổ sung khối giao diện HTML Chat Box vào vị trí hiển thị ca `IN_PROGRESS`.
  - Viết module Javascript kết nối SockJS + STOMP lắng nghe kênh `/topic/chat/{sosId}`.
  - Kích hoạt gửi tin qua WebSocket hoặc fetch POST fallback.
  - Xử lý các nút Quick Action Chips.
- [x] **Màn hình `rescuer/routes.html`:**
  - Bổ sung khối HTML Chat Box cho Đội cứu hộ.
  - Lập trình JS tự động đổi kênh chat khi đội viên bấm chọn ca nhiệm vụ khác trong danh sách.
  - Xử lý gửi tin nhắn hướng dẫn và cập nhật trạng thái thời gian thực.

### 4.5. Kiểm Thử Hệ Thống (Automated Testing)
- [x] Viết `ChatServiceTest.java`: Kiểm thử lưu tin nhắn, kiểm tra quyền truy cập ca cứu hộ, kiểm tra điều kiện trạng thái `IN_PROGRESS`.
- [x] Viết `ChatApiControllerTest.java`: Kiểm thử API lấy lịch sử tin nhắn và gửi tin nhắn HTTP 200 OK.
- [x] Kiểm thử tích hợp WebSocket STOMP messaging.

---

## 5. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)

- [x] **Kích hoạt đúng thời điểm:**
  - Khi ca SOS ở trạng thái `PENDING` hoặc `DISPATCHED`: Khung chat chưa mở, hiển thị thông báo thân thiện *"Kênh chat trực tuyến sẽ tự động mở khi đội cứu hộ bắt đầu di chuyển tiếp cận"*.
  - Khi ca chuyển sang `IN_PROGRESS` (Đang tiếp cận): Cả màn hình `sos/track.html` và `rescuer/routes.html` đều kích hoạt khung chat.
- [x] **Trao đổi 2 chiều tức thời:**
  - Tin nhắn gửi từ màn hình theo dõi của người dân hiển thị trên màn hình lộ trình cứu hộ của đội trong vòng dưới **1 giây** không cần F5.
  - Tin nhắn gửi từ đội cứu hộ hiển thị trên màn hình của người dân tức thời.
- [x] **Bền vững dữ liệu:** Khi F5 tải lại trang, toàn bộ lịch sử tin nhắn cũ hiển thị đầy đủ, đúng thứ tự thời gian.
- [x] **Bộ phím tắt nhanh 1-chạm:** Các phím tin nhắn tình huống khẩn cấp hoạt động mượt mà, bấm là gửi ngay.
- [x] **Đóng ca nghiệm thu:** Khi ca chuyển sang `COMPLETED` / `RESOLVED`, khung chat hiển thị huy hiệu *"Ca cứu nạn đã hoàn tất - Kênh chat đã lưu trữ"* và vô hiệu hóa ô nhập liệu.
- [x] **Kiểm thử tự động:** Toàn bộ test case mới và cũ đều đạt **BUILD SUCCESS 100%**.
