# PHIÊN LÀM VIỆC #20: ĐỒNG BỘ KHOẢNG CÁCH TUYẾN ĐƯỜNG OSRM & TỰ ĐỘNG CẬP NHẬT VỊ TRÍ GPS THỜI GIAN THỰC (LIVE GPS TRACKING)
> **Trạng thái:** ✅ ĐÃ HOÀN THÀNH (100%)  
> **Người phụ trách:** **Tri Thai (PeterWeeee - `NguyenTT162.4@gmail.com`)**  
> **Thời điểm hoàn thành:** 31/10/2026  
> **Nhánh thực hiện:** `main`  
> **Kết quả kiểm thử:** 17/17 bài test unit/integration passed (BUILD SUCCESS 100%)

---

## 1. Bối Cảnh Yêu Cầu & Vấn Đề Cần Giải Quyết

Trong quá trình vận hành mô phỏng thực tế cứu hộ cứu nạn:
1. **Lệch khoảng cách giữa hai màn hình bản đồ:**
   - Trên **Bản đồ Lộ trình của Đội cứu hộ** (`/rescuer/routes`): Hệ thống phân tích tuyến đường giao thông bộ/thủy qua **OSRM Routing API** (`TerrainAnalysisService`), tính ra cự ly đường đi thực tế là **`4.14 km`** (thời gian dự kiến `~7 phút`).
   - Trên **Màn hình Theo dõi SOS của Người bị nạn** (`/sos/track/{id}`) và **Thẻ nhiệm vụ cứu hộ** (`/rescuer/missions`): Hệ thống lại tính theo công thức đường chim bay **Haversine** (`GeoUtils.calculateDistance`), hiển thị cự ly thẳng là **`3.6 km`**.
   - Sự bất nhất này khiến người bị nạn và đội cứu hộ khi trao đổi qua hotline/chat nhìn thấy hai số km khác nhau, gây hoang mang và hiểu lầm về tiến độ tiếp cận.
2. **Thiếu cơ chế Live GPS tự động trượt vị trí trên bản đồ:**
   - Trên trang Bản đồ lộ trình của đội cứu hộ (`rescuer/routes.html`), vị trí đội chỉ được vẽ một lần lúc tải trang, chưa có `watchPosition` nên marker xe cứu hộ đứng yên dù đội đang di chuyển.
   - Vị trí của người bị nạn trước đây là tọa độ tĩnh (chỉ lấy lúc gửi đơn), chưa hỗ trợ cơ chế nạn nhân trôi dạt trên nước lũ hoặc di chuyển đến điểm cao hơn.
   - Trên màn hình của Điều phối viên (`coordinator/missions.html`), bản đồ chưa bắt sự kiện WebSocket `LOCATION_UPDATE` để cập nhật marker theo thời gian thực.

---

## 2. Các Giải Pháp Kỹ Thuật Đã Triển Khai

### 2.1. Đồng bộ cự ly lộ trình OSRM & ETA cho phía Người bị nạn
- **Trong `SosController.java` (`/sos/track/{id}`):**
  - Tiêm `TerrainAnalysisService`. Khi mở chi tiết ca cứu nạn đã có đội phân công, hệ thống tính toán tuyến đường OSRM đồng bộ với bản đồ đội cứu hộ.
  - Đưa `distanceKm` (`4.14 km`), `durationMinutes` (`~7 phút`) và `routeGeometryGeoJson` vào `Model`.
  - Lưu cự ly và thời gian dự kiến vào thực thể `RescueAssignment`.
- **Trong `SosApiController.java`:**
  - Bổ sung REST endpoint `GET /api/sos/{id}/route` trả về đầy đủ dữ liệu định tuyến OSRM cho nạn nhân.
- **Trong `sos/track.html`:**
  - Hiển thị cự ly thực tế đồng bộ (`4.14 km`) kèm thời gian dự kiến (`~7 phút`).
  - Bản đồ Leaflet (`#trackMap`) vẽ trực tiếp tuyến đường OSRM GeoJSON giống hệt bản đồ đội cứu hộ, kèm chú giải trực quan (*Đội cứu hộ*, *Điểm SOS*, *Tuyến OSRM*).
  - Khởi tạo giá trị `rescuerDistText` trong `rescuer/missions.html` từ cự ly tuyến đường OSRM đã lưu thay vì giá trị mặc định.

### 2.2. Tự động cập nhật vị trí GPS thời gian thực (Live GPS Tracking) cho cả 3 vai trò
1. **Phía Đội cứu hộ (`rescuer/routes.html`):**
   - Tích hợp `navigator.geolocation.watchPosition` khi nhiệm vụ ở trạng thái `IN_PROGRESS`.
   - Gửi định kỳ tọa độ mới lên `POST /api/rescuer/assignments/{id}/location`.
   - Marker đội cứu hộ tự động trượt trên bản đồ, bảng thông tin cự ly và thời gian dự kiến tự động giảm dần khi tiến gần nạn nhân. Tuyến OSRM tự động vẽ lại.
2. **Phía Người bị nạn (`sos/track.html` & `SosApiController.java`):**
   - Bổ sung công tắc bật/tắt **"Chia sẻ vị trí trực tiếp (Live GPS)"**.
   - Khi bật, trình duyệt của nạn nhân dùng `watchPosition` và gửi định kỳ tọa độ mới lên `POST /api/sos/{id}/location`.
   - Backend cập nhật tọa độ vào `SosRequest` và phát WebSocket `/topic/map-updates` với payload `VICTIM_LOCATION_UPDATE`.
   - Marker đỏ của nạn nhân tự động trượt trên bản đồ của chính nạn nhân, đồng thời di chuyển trên bản đồ của Đội cứu hộ và Điều phối viên.
3. **Phía Điều phối viên (`coordinator/dashboard.html` & `coordinator/missions.html`):**
   - Lắng nghe sự kiện WebSocket `LOCATION_UPDATE` (vị trí đội cứu hộ) và `VICTIM_LOCATION_UPDATE` (vị trí nạn nhân).
   - Marker Đội cứu hộ và marker Ca SOS trên cả 2 trang tự động di chuyển, dây nối tiếp cận tự động co ngắn và cập nhật số km cự ly trực tiếp.

---

## 3. Danh Sách Tệp Đã Thay Đổi

| Tệp | Thay Đổi |
| :--- | :--- |
| `src/main/java/vn/edu/cuunan/controller/SosController.java` | Tiêm `TerrainAnalysisService`, đồng bộ cự ly OSRM và ETA vào model `sos/track`. |
| `src/main/java/vn/edu/cuunan/controller/SosApiController.java` | Thêm `GET /api/sos/{id}/route` và `POST /api/sos/{id}/location` (Live Location Tracking). |
| `src/main/java/vn/edu/cuunan/service/RescuerService.java` | Cập nhật `getRouteAndTerrainAnalysis` và `updateTeamLocation` đồng bộ cự ly OSRM và phát WebSocket đầy đủ. |
| `src/main/resources/templates/sos/track.html` | Vẽ tuyến đường OSRM, hiển thị ETA, bổ sung Live GPS switch và `startVictimWatch`. |
| `src/main/resources/templates/rescuer/routes.html` | Bổ sung `watchPosition` và WebSocket listener tự động di chuyển marker và cập nhật số km. |
| `src/main/resources/templates/rescuer/missions.html` | Hiển thị cự ly OSRM khởi tạo cho `rescuerDistText`. |
| `src/main/resources/templates/coordinator/dashboard.html` | Bổ sung xử lý `VICTIM_LOCATION_UPDATE` cập nhật marker SOS live. |
| `src/main/resources/templates/coordinator/missions.html` | Gắn `data-sos-id`, `data-team-id` và listener `rescue:map-update` cập nhật marker/polyline live. |
| `docs/API_AND_WEBSOCKET_SPECS.md` | Cập nhật đặc tả REST API và WebSocket channels mới nhất. |

---

## 4. Kết Quả Kiểm Thử
- **Unit & Integration Tests:** 17/17 tests passed (`mvn test "-Dtest=SosServiceTest,RescuerServiceTest,TerrainAnalysisServiceTest"`).
- **Maven Build:** `BUILD SUCCESS` không lỗi biên dịch.
- **Kiểm thử trực quan:** Giả lập tọa độ qua Chrome DevTools Sensors, cả 3 màn hình (Nạn nhân, Đội cứu hộ, Điều phối viên) đều tự động nhận diện và di chuyển marker đồng bộ.
