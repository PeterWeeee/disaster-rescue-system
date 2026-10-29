# SESSION 17: TÍCH HỢP ẢNH HIỆN TRƯỜNG THIÊN TAI TỪ CLOUDINARY CDN CHO BẢNG TIN CỘNG ĐỒNG
> **Người phụ trách:** PeterWeeee (Trưởng nhóm / Backend & Real-time Core)  
> **Thời điểm thực hiện:** 29/10/2026  
> **Nhánh Git:** `develop`  
> **Trạng thái:** ✅ ĐÃ HOÀN THÀNH 100% (128/128 Tests Passed - Build Success)

---

## 1. Bối Cảnh & Yêu Cầu Nghiệp Vụ
1. **Thiếu hình ảnh trực quan trên Bảng Tin Cộng Đồng (`/feed`):**
   - Trước đây, các bài đăng cảnh báo thiên tai mẫu trên Bảng Tin mới chỉ có nội dung dạng văn bản (`imageUrl = null`), khiến giao diện mạng xã hội cứu trợ bị đơn điệu, thiếu tính chân thực và khó giúp người dân cũng như Ban Điều Phối hình dung mức độ nghiêm trọng tại thực địa.
2. **Yêu cầu lưu trữ & phục vụ ảnh qua Cloudinary CDN:**
   - Tuân thủ kiến trúc công nghệ toàn diện của đồ án: Toàn bộ ảnh hiện trường trên Bảng Tin phải được lưu trữ trên đám mây **Cloudinary** (thư mục `cuunan247/community_feed`) và phục vụ trực tiếp qua HTTPS CDN link.
   - Bảng tin phải có các hình ảnh hiện trường đa dạng theo các loại hiểm họa (`LANDSLIDE`, `FLOOD`, `TREE_FALL`, `ISOLATED`, v.v.).
3. **Trải nghiệm người dùng (UX/UI):**
   - Hiển thị huy hiệu nhận diện `Cloudinary CDN` trên mỗi ảnh hiện trường.
   - Cung cấp tính năng xem ảnh phóng to toàn màn hình qua Modal và liên kết mở ảnh gốc Cloudinary.
   - Tránh triệt để lỗi phân tích cú pháp biểu thức sự kiện của Thymeleaf 3.1+ (`th:onclick` strict expression check).

---

## 2. Chi Tiết Thực Hiện

### 2.1. Tải Lên & Chuẩn Hóa Ảnh Hiện Trường Trên Cloudinary CDN
Đã tải và ánh xạ thành công bộ ảnh thiên tai thực địa lên Cloudinary (cloud-name `odwf5uoi`, folder `cuunan247/community_feed`):
1. **Sạt lở đồi núi Trà Mai (Quảng Nam) - `LANDSLIDE`:**  
   `https://res.cloudinary.com/odwf5uoi/image/upload/v1793269064/cuunan247/community_feed/feed_landslide_tra_mai.jpg`
2. **Nước ngập sâu Cẩm Phô, Hội An - `FLOOD`:**  
   `https://res.cloudinary.com/odwf5uoi/image/upload/v1793269093/cuunan247/community_feed/feed_flood_hoi_an.jpg`
3. **Cây cổ thụ bật gốc ngã đổ, đứt cáp điện Tam Kỳ - `TREE_FALL`:**  
   `https://res.cloudinary.com/odwf5uoi/image/upload/v1793269099/cuunan247/community_feed/feed_storm_cay_do.jpg`
4. **Xuồng máy cứu hộ di dời người dân vùng lũ Duy Vinh - `FLOOD`:**  
   `https://res.cloudinary.com/odwf5uoi/image/upload/v1793269096/cuunan247/community_feed/feed_flood_rescue_boat.jpg`
5. **Tiếp tế lương thực, nước sạch cho bà con vùng cô lập Quế Long - `ISOLATED`:**  
   `https://res.cloudinary.com/odwf5uoi/image/upload/v1793269102/cuunan247/community_feed/feed_relief_supplies.jpg`
6. **Cơ giới san ủi thông tuyến đèo Le bị sạt lở - `LANDSLIDE`:**  
   `https://res.cloudinary.com/odwf5uoi/image/upload/v1793269104/cuunan247/community_feed/feed_clearing_road.jpg`

---

### 2.2. Nâng Cấp Logic Khởi Tạo & Đồng Bộ Bài Viết (`CommunityFeedService.java`)
- **Khai báo hằng số URL CDN:** Định nghĩa các hằng số tĩnh `CLOUDINARY_FEED_LANDSLIDE`, `CLOUDINARY_FEED_FLOOD`, `CLOUDINARY_FEED_BOAT`, `CLOUDINARY_FEED_STORM`, `CLOUDINARY_FEED_RELIEF`, `CLOUDINARY_FEED_ROAD`.
- **Phương thức `initSampleData()`:**
  - Khởi tạo đầy đủ 6 bản tin mẫu với nội dung phong phú, tọa độ GPS thực tế và liên kết ảnh Cloudinary CDN.
  - **Tự động vá dữ liệu (Self-healing backfill):** Quét các bản tin hiện có trong cơ sở dữ liệu nếu bị thiếu ảnh (`imageUrl == null`), tự động cập nhật URL Cloudinary tương ứng theo loại hiểm họa.
  - Tự động bổ sung các bản tin hiểm họa còn thiếu (cây đổ, xuồng cứu hộ, tiếp tế) nếu database chỉ có 2 bài mẫu cũ.

---

### 2.3. Bổ Sung Seed Data Cho Cả Hai Hệ Quản Trị CSDL
- **SQL Server (`database/03_seed_data.sql`):** Bổ sung mục 6 nạp các bài đăng cảnh báo cộng đồng mẫu kèm ảnh Cloudinary CDN và câu lệnh `UPDATE` cập nhật bài viết cũ.
- **MySQL (`database/mysql_init.sql`):** Bổ sung mục 20 `INSERT IGNORE INTO community_posts` với 6 bài viết mẫu chuẩn chỉ.

---

### 2.4. Nâng Cấp Giao Diện Bảng Tin (`feed/index.html`)
- **Huy hiệu Cloudinary CDN:** Hiển thị nhãn `Cloudinary CDN` kèm biểu tượng đám mây phát sáng trên góc ảnh bài đăng khi ảnh được nạp từ Cloudinary.
- **Nút "Xem ảnh" & Modal xem ảnh phóng to (`#imagePreviewModal`):** Cho phép người dân nhấp vào ảnh để xem chi tiết ảnh phân giải cao toàn màn hình trên nền tối sang trọng, hiển thị địa điểm/loại thiên tai và nút "Mở ảnh gốc Cloudinary".
- **Tuân thủ chuẩn bảo mật Thymeleaf 3.1+:** Thay thế hoàn toàn chuỗi nối `th:onclick` bằng thuộc tính HTML5 chuẩn `th:data-src` và `th:data-caption`, tránh lỗi TemplateProcessingException (XSS guard).
- **Hiệu ứng trực quan:** Bổ sung hiệu ứng hover mượt mà `.feed-post-image:hover { transform: scale(1.01); }`.

---

## 3. Các Tệp Tin Đã Can Thiệp
1. `src/main/java/vn/edu/cuunan/service/CommunityFeedService.java`
2. `src/main/resources/templates/feed/index.html`
3. `database/03_seed_data.sql`
4. `database/mysql_init.sql`
5. `docs/roadmap/session-17-community-feed-cloudinary-media.md`
6. `docs/sessions/session-17-community-feed-cloudinary-media.md`
7. `docs/HANDOVER_STATUS.md`
8. `docs/roadmap/README.md`
9. `docs/ROADMAP_AI_SESSIONS.md`

---

## 4. Kết Quả Kiểm Thử
- Đã chạy kiểm thử tự động toàn diện qua Maven (`mvn test`):
  - **128/128 tests PASS (0 failures, 0 errors)**
  - `FeedControllerTest`: 5/5 tests PASS.
  - `ChatApiControllerTest`, `ChatServiceTest`, `SosIntegrationTest`, `RescuerServiceTest`, `CloudinaryServiceTest`: PASS 100%.
- Toàn bộ ảnh hiện trường trên Bảng Tin được tải trực tiếp từ Cloudinary CDN với tốc độ cao và phản hồi tức thời.
