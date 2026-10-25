# PHIÊN LÀM VIỆC #05: WEBSOCKET REAL-TIME CỐT LÕI (SOS & LIVE MAP)
> **Trạng thái:** ⏳ Chưa bắt đầu  
> **Ước tính thời gian:** 1 phiên làm việc  
> **Nhánh thực hiện:** `develop`

---

## 1. Mục Tiêu Phiên
- Cấu hình Spring WebSocket STOMP Broker qua SockJS.
- Hiện thực tính năng thời gian thực cốt lõi của đồ án:
  1. Khi người dân gửi SOS, server bắn sự kiện real-time đến tất cả Coordinator/Rescuer đang online.
  2. Trình duyệt client tự động nổ chuông cảnh báo + toast notification + marker đỏ nhấp nháy trên bản đồ mà **không cần F5**.
  3. Cập nhật trạng thái đổi màu marker trên bản đồ của mọi người theo thời gian thực (`/topic/map-updates`).

---

## 2. Danh Sách Công Việc Cần Thực Hiện

### 2.1. Cấu Hình Backend WebSocket
- `WebSocketConfig.java`:
  - `registerStompEndpoints`: Đăng ký endpoint `/ws` với SockJS fallback.
  - `configureMessageBroker`: Bật Simple Broker cho `/topic` và `/user`, đặt application destination prefix `/app`.
- `WebSocketEventListener.java`: Bắt sự kiện Connect / Disconnect của các client.
- DTOs gửi qua STOMP:
  - `SosAlertMessage.java` (id, contactName, phone, latitude, longitude, urgencyLevel, address, createdAt).
  - `MapUpdateMessage.java` (sosId, newStatus, teamName).

### 2.2. Tích Hợp Vào Nghiệp Vụ SOS
- Tiêm (Inject) `SimpMessagingTemplate` vào `SosService`.
- Sau khi `sosRequestRepository.save(sos)` thành công -> Gửi thông báo đến `/topic/sos-alerts`.
- Khi cập nhật trạng thái phiếu -> Gửi đến `/topic/map-updates`.

### 2.3. Client JavaScript (STOMP.js & SockJS)
- Tải thư viện `sockjs.min.js` và `stomp.min.js` vào thư mục `static/js/libs/`.
- `static/js/websocket-listener.js`:
  - Khởi tạo kết nối STOMP đến `/ws`.
  - Subscribe vào `/topic/sos-alerts`: Khi có message mới -> Phát file âm thanh `alert.mp3`, hiển thị Toast góc phải màn hình, thêm marker đỏ động lên bản đồ Leaflet.
  - Subscribe vào `/topic/map-updates`: Tự đổi màu icon marker tương ứng (Đỏ -> Vàng -> Xanh).

---

## 3. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)
- [ ] Mở 2 trình duyệt:
  - Trình duyệt A: Đóng vai Người dân bấm gửi SOS.
  - Trình duyệt B: Màn hình của Coordinator đang mở bản đồ.
- [ ] Ngay khi bấm gửi ở A, màn hình B ngay lập tức:
  - [ ] Phát âm thanh chuông cảnh báo.
  - [ ] Hiện thông báo Toast màu đỏ: *"Có yêu cầu cứu hộ khẩn cấp mới từ Nguyễn Văn A!"*.
  - [ ] Bản đồ tự động ghim thêm 1 điểm màu đỏ nhấp nháy tại đúng vị trí mà **không hề tải lại trang**.
