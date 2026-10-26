# PHIÊN LÀM VIỆC #05: WEBSOCKET REAL-TIME CỐT LÕI (SOS & LIVE MAP)
> **Trạng thái:** ✅ ĐÃ HOÀN THÀNH  
> **Người thực hiện:** PeterWeeee (Kiến trúc) & SV 24110329 (Tích hợp thực địa)  
> **Thời điểm hoàn thành:** 26/10/2026  
> **Nhánh thực hiện:** `develop`

---

## 1. Mục Tiêu Phiên
- Cấu hình Spring WebSocket STOMP Broker qua SockJS.
- Hiện thực tính năng thời gian thực cốt lõi của đồ án:
  1. Khi người dân gửi SOS, server bắn sự kiện real-time đến tất cả Coordinator/Rescuer đang online.
  2. Trình duyệt client tự động nổ chuông cảnh báo + toast notification + marker đỏ nhấp nháy trên bản đồ mà **không cần F5**.
  3. Cập nhật trạng thái đổi màu marker trên bản đồ của mọi người theo thời gian thực (`/topic/map-updates`).
  4. Bắn sự kiện điều động nhiệm vụ (`/topic/assignments`).

---

## 2. Danh Sách Công Việc Đã Thực Hiện

### 2.1. Cấu Hình Backend WebSocket
- [x] `WebSocketConfig.java`:
  - `registerStompEndpoints`: Đăng ký endpoint `/ws` với SockJS fallback và CORS pattern.
  - `configureMessageBroker`: Bật Simple Broker cho `/topic` và `/queue`, đặt application destination prefix `/app`.
- [x] Tiêm `SimpMessagingTemplate` vào `SosService`, `CoordinatorService`, `RescuerService`, `AdminService`.

### 2.2. Tích Hợp Vào Nghiệp Vụ SOS & Điều Phối
- [x] Sau khi `sosRequestRepository.save(sos)` thành công -> Gửi thông báo đến `/topic/sos-alerts`.
- [x] Khi cập nhật trạng thái phiếu hoặc phân công ca cứu hộ -> Gửi đến `/topic/map-updates`.
- [x] Khi gán nhiệm vụ cho đội -> Gửi đến `/topic/assignments`.

### 2.3. Client JavaScript (STOMP.js & SockJS) & Âm Thanh Cảnh Báo
- [x] Tích hợp thư viện `sockjs.min.js` và `stomp.min.js` vào `layout/main.html`.
- [x] `static/js/alert-client.js`:
  - Khởi tạo kết nối STOMP đến `/ws`.
  - Subscribe vào `/topic/sos-alerts`: Khi có message mới -> Phát còi báo động HTML5 Web Audio API, hiển thị Toast góc phải màn hình, phát sự kiện `rescue:new-sos`.
  - Subscribe vào `/topic/map-updates`: Tự động cập nhật trạng thái các điểm trên bản đồ `rescue:map-update`.
  - Subscribe vào `/topic/broadcast-alerts`: Kích hoạt popup cảnh báo khẩn cấp màu đỏ đè toàn màn hình.

---

## 3. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)
- [x] Mở 2 cửa sổ: Người dân gửi SOS -> màn hình Coordinator nhận ngay thông báo Toast và điểm marker đỏ mới mà không cần tải lại trang.
- [x] Phát âm thanh chuông cảnh báo qua HTML5 Web Audio API độc lập, không bị phụ thuộc tài nguyên ngoài.
- [x] Toàn bộ kiểm thử tự động WebSocket và Service tích hợp đạt 100% BUILD SUCCESS.
