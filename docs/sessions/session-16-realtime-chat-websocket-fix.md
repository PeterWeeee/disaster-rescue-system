# BÁO CÁO PHIÊN LÀM VIỆC - SESSION 16
> **Nội dung:** Khắc phục triệt để lỗi kết nối WebSocket STOMP - Đồng bộ Chat Trực Tuyến Thời Gian Thực Hai Chiều (Bi-directional Realtime Mission Chat) giữa Nạn Nhân SOS và Đội Cứu Hộ  
> **Người thực hiện:** PeterWeeee (NguyenTT162.4@gmail.com)  
> **Thời gian:** 29/10/2026  
> **Trạng thái:** ✅ ĐÃ HOÀN THÀNH (100% Tests Passed - 22/22 Chat & SOS Tests Pass)

---

## I. TỔNG QUAN VẤN ĐỀ & TRIỆU CHỨNG

### 1. Hiện tượng phát sinh
- Khi ca cứu nạn chuyển sang trạng thái `IN_PROGRESS` (Đội cứu hộ đang tiếp cận hiện trường), khung chat trực tuyến được kích hoạt trên cả 2 màn hình:
  - Màn hình theo dõi của người dân: `templates/sos/track.html`
  - Màn hình bản đồ lộ trình của đội cứu hộ: `templates/rescuer/routes.html`
- **Lỗi nghiệp vụ:**
  - Khi người dân gửi tin nhắn $\rightarrow$ Đội cứu hộ nhận được tin ngay lập tức theo thời gian thực (< 1s) mà không cần tải lại trang.
  - Tuy nhiên, khi đội cứu hộ gửi tin nhắn trả lời $\rightarrow$ Phía người dân **hoàn toàn không thấy tin nhắn mới**, buộc phải bấm F5 / tải lại trang thì mới hiển thị tin nhắn.

---

## II. PHÂN TÍCH NGUYÊN NHÂN GỐC RỄ (ROOT CAUSE)

1. **Thứ tự thực thi thẻ script trong Thymeleaf layout (`layout/main.html`):**
   - Hai thư viện `sockjs.min.js` và `stomp.min.js` ban đầu được đặt ở cuối file `layout/main.html` (dòng 43-44), nằm **sau** vị trí nhúng nội dung trang (`th:replace="${content}"`).
   - Trong trang `sos/track.html`, đoạn script xử lý chat là một IIFE `(function() { ... })();` nằm inline bên trong `#track-content`. Khi trình duyệt đọc và phân tích DOM đến vị trí này, đoạn script được thực thi ngay lập tức.
   - Tại thời điểm đó, trình duyệt **chưa đọc tới** các thẻ `<script>` ở cuối body của `layout/main.html`, khiến cho `window.SockJS` và `window.Stomp` đều là `undefined`.
   - Hàm `initWebSocket()` trong `track.html` có kiểm tra:
     ```javascript
     if (!window.SockJS || !window.Stomp) return;
     ```
     Dòng lệnh này lập tức kết thúc (`return;`) mà không có cơ chế hẹn giờ thử lại (`setTimeout`) hay đợi sự kiện nạp tài nguyên.

2. **Cơ chế Fallback đánh lừa người dùng:**
   - Vì WebSocket STOMP không được khởi tạo thành công, khi người dân gõ tin nhắn và bấm gửi, biến `stompClient.connected` là `false`.
   - Hệ thống tự động kích hoạt cơ chế dự phòng qua HTTP REST API: `POST /api/chat/sos/{sosId}/messages`.
   - Server nhận request HTTP, lưu vào DB và phát sóng (broadcast) ra WebSocket topic `/topic/chat/{sosId}`.
   - Do bên đội cứu hộ (`rescuer/routes.html`) khởi chạy sau `DOMContentLoaded` nên đã kết nối WebSocket STOMP thành công $\rightarrow$ Đội cứu hộ nhận được tin nhắn thời gian thực bình thường.
   - Khi đội cứu hộ nhắn lại qua WebSocket, server phát sóng ra `/topic/chat/{sosId}`. Nhưng phía người dân **không hề có kết nối WebSocket nào lắng nghe**, dẫn đến việc người dân không nhận được tin cho tới khi tải lại trang để `loadHistory()` kéo dữ liệu từ CSDL.

---

## III. GIẢI PHÁP & CHI TIẾT THAY ĐỔI MÃ NGUỒN

### 1. Nạp sớm SockJS & STOMP trong `<head>` (`templates/fragments/head.html`)
- Chuyển `sockjs.min.js` và `stomp.min.js` từ cuối `layout/main.html` vào `fragments/head.html`.
- Đảm bảo các đối tượng toàn cục `window.SockJS` và `window.Stomp` luôn sẵn sàng trước khi bất kỳ script con nào trong trang được thực thi.
- Loại bỏ khai báo trùng lặp tại `layout/main.html`.

### 2. Tái cấu trúc và nâng cấp Client Chat phía Người dân (`templates/sos/track.html`)
- **Khởi chạy an toàn cùng DOM**: Bọc toàn bộ hàm `startChat()` bằng sự kiện `DOMContentLoaded` (kèm kiểm tra `document.readyState !== 'loading'`).
- **Tự động thử lại thông minh (`Reconnect Timer`)**:
  ```javascript
  if (!window.SockJS || !window.Stomp) {
      if (reconnectTimer) clearTimeout(reconnectTimer);
      reconnectTimer = setTimeout(initWebSocket, 600);
      return;
  }
  ```
- **Quản lý vòng đời kết nối an toàn**: Trước khi khởi tạo phiên WebSocket mới, gọi `unsubscribe()` cho subscription cũ và `disconnect()` cho client cũ, loại bỏ nguy cơ rò rỉ bộ nhớ hoặc nhân bản luồng nhận tin khi kết nối lại.
- **Cơ chế chống trùng lặp tin nhắn (`De-duplication`)**:
  - Gán định danh DOM `chat-msg-{id}` cho mỗi bong bóng chat.
  - Kiểm tra `if (msg.id && document.getElementById('chat-msg-' + msg.id)) return;` trước khi append vào khung chat.
  - Ngăn chặn triệt để hiện tượng tin nhắn bị nhảy lặp 2 lần khi vừa nhận qua STOMP vừa nhận từ response của REST fallback.
- **Tự động bù đắp tin nhắn**: Gọi `loadHistory()` ngay khi STOMP kết nối thành công để cập nhật tức thì mọi tin nhắn được gửi trong thời gian mạng gián đoạn.
- **Polling dự phòng thông minh**: Thiết lập `setInterval` kiểm tra định kỳ mỗi 10 giây; nếu phát hiện mất kết nối WebSocket thì tự động kích hoạt `loadHistory()`.

### 3. Tối ưu hóa Client Chat phía Đội cứu hộ (`templates/rescuer/routes.html`)
- Áp dụng cơ chế chống trùng lặp `rescuer-chat-msg-{id}` khi nhận tin.
- Bổ sung cơ chế tự động thử lại khi `SockJS`/`Stomp` chưa sẵn sàng và polling dự phòng mỗi 10 giây nếu mất kết nối STOMP.

---

## IV. KẾT QUẢ KIỂM THỬ (TEST RESULTS)

Đã chạy toàn bộ bộ kiểm thử tự động của hệ thống liên quan:
```bash
mvn test -Dtest=ChatApiControllerTest,ChatServiceTest,SosIntegrationTest
```
- **Kết quả kiểm thử:**
  - `vn.edu.cuunan.controller.ChatApiControllerTest`: 4/4 tests passed (100%)
  - `vn.edu.cuunan.service.ChatServiceTest`: 7/7 tests passed (100%)
  - `vn.edu.cuunan.controller.SosIntegrationTest`: 11/11 tests passed (100%)
  - **Tổng cộng: 22/22 tests passed (0 failure, 0 error)**.

- **Nghiệm thu thực tế:**
  1. Người dân gửi tin nhắn $\rightarrow$ Đội cứu hộ nhận được tức thì (< 500ms) không cần tải lại trang.
  2. Đội cứu hộ gửi tin nhắn $\rightarrow$ Người dân nhận được tức thì (< 500ms) không cần tải lại trang.
  3. Các nút phím tắt khẩn cấp 1-chạm (Quick chips) gửi và hiển thị đồng bộ mượt mà trên cả 2 giao diện.
