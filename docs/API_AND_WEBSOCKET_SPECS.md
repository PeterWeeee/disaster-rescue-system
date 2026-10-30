# ĐẶC TẢ GIAO TIẾP: REST API & WEBSOCKET (SPECS)
> **Mục tiêu:** Thống nhất danh sách endpoint HTTP và kênh WebSocket giữa Spring Boot backend và frontend (Thymeleaf/JS/STOMP) để tránh sai lệch path hoặc payload.

---

## 1. WebSocket & STOMP Protocol Specifications

- **WebSocket Handshake Endpoint:** `/ws` (kèm fallback SockJS)
- **STOMP Prefix Application:** `/app` (dành cho client gửi message lên server)
- **Broker Destinations:** `/topic` (quảng bá công khai/nhóm) và `/user` (tin nhắn riêng từng người)

### 1.1. Danh Sách Các Topic / Channels

| STOMP Destination | Người Nhận (Subscribers) | Sự Kiện (Event) & Dữ Liệu Gửi Đi |
| :--- | :--- | :--- |
| `/topic/sos-alerts` | Coordinator, Rescuer, Admin | **Có SOS mới:** Gửi `SosAlertMessage` (id, contactName, phone, lat, lng, urgency, peopleCount, address). Client phát chuông & vẽ marker đỏ nhấp nháy. |
| `/topic/map-updates` | Mọi client đang xem bản đồ (Citizen, Rescuer, Coordinator) | **Cập nhật Live GPS & Tuyến đường:**<br>- `STATUS_CHANGE`: Thay đổi trạng thái ca SOS (Đỏ -> Vàng khi có đội nhận, Vàng -> Xanh khi cứu xong).<br>- `LOCATION_UPDATE`: Vị trí GPS của Đội cứu hộ (`teamId`, `latitude`, `longitude`, `distanceKm`, `durationMinutes`, `routeGeometryGeoJson`).<br>- `VICTIM_LOCATION_UPDATE`: Vị trí GPS thời gian thực của Nạn nhân (`sosId`, `latitude`, `longitude`). |
| `/topic/broadcast-alerts` | Tất cả user đang truy cập | **Cảnh báo khẩn cấp:** Admin gửi thông báo thiên tai, xả lũ, di dời đè lên toàn màn hình client. |
| `/user/queue/assignments` | Rescuer (Đội trưởng cụ thể) | **Nhiệm vụ mới được giao:** Gửi chi tiết ca cứu hộ vừa được Coordinator phân công cho đội này. |
| `/user/queue/sos-status` | Citizen (Nạn nhân gửi SOS) | **Cập nhật trạng thái phiếu cứu hộ:** Báo cho người dân biết đội nào đã nhận, đang di chuyển đến. |
| `/topic/chat/{sosId}` | Citizen & Rescuer thuộc ca SOS | **Chat trực tuyến 2 chiều thời gian thực:** Gửi `ChatMessageDto` (id, sosId, senderType, senderName, content, messageType, formattedTime, createdAt). |

### 1.2. Inbound Message Mappings (Client gửi tin lên qua STOMP)

| STOMP Destination | Tham Số / Payload | Xử Lý Phía Backend |
| :--- | :--- | :--- |
| `/app/chat/{sosId}/send` | `SendChatMessageRequest` (`content`, `senderType`, `senderName`, `senderPhone`, `messageType`) | Lưu CSDL `rescue_chat_messages` và phát sóng tới `/topic/chat/{sosId}` cho cả người dân và đội cứu hộ. |

---

## 2. Danh Sách REST APIs

### 2.1. Xác Thực & Người Dùng (`/api/auth`)
- `POST /api/auth/register` - Đăng ký tài khoản mới.
- `POST /api/auth/login` - Đăng nhập, trả về JWT Token và set Cookie `jwt_token`.
- `POST /api/auth/logout` - Đăng xuất, xóa Cookie.
- `GET /api/auth/me` - Lấy thông tin tài khoản đang đăng nhập.

### 2.2. Quản Lý Yêu Cầu Cứu Nạn & Định Vị Live (`/api/sos`)
- `POST /api/sos` - Gửi yêu cầu cứu nạn khẩn cấp (kèm tọa độ GPS, số người, mô tả, ảnh). Bắn WebSocket `/topic/sos-alerts`.
- `GET /api/sos` - Lấy danh sách SOS (hỗ trợ phân trang, lọc theo trạng thái, mức độ khẩn cấp).
- `GET /api/sos/{id}` - Chi tiết 1 yêu cầu cứu nạn.
- `PATCH /api/sos/{id}/status` - Cập nhật trạng thái ca SOS.
- `GET /api/sos/{id}/route` - Lấy lộ trình OSRM, khoảng cách đường bộ và thời gian dự kiến (đồng bộ giữa nạn nhân và đội cứu hộ).
- `POST /api/sos/{id}/location` - Nạn nhân chia sẻ vị trí trực tiếp (Live Location Tracking). Bắn WebSocket `/topic/map-updates` với type `VICTIM_LOCATION_UPDATE`.
- `PUT /api/sos/{id}/cancel` - Người dân tự hủy yêu cầu khi chưa có đội nhận.

### 2.3. Điều Phối Cứu Hộ (`/api/coordinator`)
- `GET /api/coordinator/map-data` - Lấy toàn bộ danh sách điểm SOS và đội cứu hộ đang active để vẽ lên Leaflet map.
- `GET /api/coordinator/nearest-teams?sosId={id}` - Lấy danh sách các đội cứu hộ gần nhất tính theo công thức khoảng cách Haversine.
- `POST /api/coordinator/assign` - Phân công nhiệm vụ cho đội cứu hộ (Body: `sosId`, `teamId`). Bắn WebSocket `/user/queue/assignments`.

### 2.4. Đội Cứu Hộ & Lộ Trình Tác Chiến (`/api/rescuer`)
- `GET /api/rescuer/my-tasks` - Danh sách nhiệm vụ của đội mình.
- `POST /api/rescuer/assignments/{id}/accept` - Nhận nhiệm vụ.
- `POST /api/rescuer/assignments/{id}/reject` - Từ chối nhiệm vụ (kèm lý do).
- `GET /api/rescuer/assignments/{id}/route` - Phân tích địa hình và định tuyến OSRM kèm danh mục phương tiện, trang bị đề xuất.
- `POST /api/rescuer/assignments/{id}/start-moving` - Bắt đầu xuất kích tiếp cận hiện trường.
- `POST /api/rescuer/assignments/{id}/location` - Đội cứu hộ cập nhật vị trí GPS thời gian thực (watchPosition). Bắn WebSocket `/topic/map-updates` với type `LOCATION_UPDATE` và tính lại cự ly OSRM.
- `POST /api/rescuer/assignments/{id}/complete` - Hoàn thành nhiệm vụ (kèm ảnh hiện trường báo cáo).

### 2.5. Upload Tệp Lên Cloudinary (`/api/media` & `/api/upload`)
- `POST /api/media/upload` - Multipart form upload ảnh hiện trường, trả về link ảnh an toàn HTTPS của Cloudinary.
- `POST /api/upload/guide-image` - Upload ảnh minh họa cẩm nang cứu nạn lên thư mục `cuunan_guides` của Cloudinary CDN.

### 2.6. Khung Chat Trực Tuyến Ca Cứu Hộ (`/api/chat`)
- `GET /api/chat/sos/{sosId}/messages?phone={phone}` - Lấy toàn bộ lịch sử tin nhắn của ca SOS theo thứ tự thời gian tăng dần.
- `POST /api/chat/sos/{sosId}/messages` - Gửi tin nhắn qua REST API (cơ chế fallback khi STOMP kết nối chập chờn).
- `GET /api/chat/sos/{sosId}/status` - Kiểm tra điều kiện mở phòng chat (`IN_PROGRESS`), trạng thái ca và thông tin đội tiếp cận.
- `GET /api/chat/active-session` - Kiểm tra ca cứu nạn đang hoạt động cần chat của người dùng hiện tại (Citizen hoặc Rescuer), phục vụ Khung Chat Nổi Toàn Hệ Thống phong cách Facebook Messenger.

### 2.7. Bảng Tin Cộng Đồng & Tương Tác Mạng Xã Hội (`/api/feed`)
- `GET /api/feed/posts` - Lấy danh sách các bài đăng cảnh báo thiên tai mới nhất.
- `POST /api/feed/posts` - Đăng bài cảnh báo thiên tai mới (kèm vị trí GPS, loại hiểm họa, nội dung, ảnh hiện trường tải lên Cloudinary).
- `POST /api/feed/posts/{id}/react?type={reactionType}` - Thả cảm xúc bài viết với 5 trạng thái (`LIKE`, `CARE`, `PRAY`, `WARNING`, `SAD`).
- `GET /api/feed/posts/{id}/comments` - Lấy danh sách các bình luận của bài viết theo thứ tự thời gian.
- `POST /api/feed/posts/{id}/comments` - Gửi bình luận mới cho bài viết (tham số: `content`).
- `GET /api/feed/shelters` - Lấy danh sách các điểm sơ tán tránh trú bão lũ an toàn.
