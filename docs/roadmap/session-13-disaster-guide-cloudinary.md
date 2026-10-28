# PHIÊN LÀM VIỆC #13: XÂY DỰNG CẨM NANG & CHI TIẾT CẨM NANG ỨNG PHÓ THIÊN TAI (TÍCH HỢP ẢNH MINH HỌA AI & LƯU TRỮ CLOUDINARY)
> **Trạng thái:** ✅ ĐÃ HOÀN THÀNH (100%)  
> **Người phụ trách:** **Tri Thai (PeterWeeee - `NguyenTT162.4@gmail.com`)**  
> **Thời điểm hoàn thành:** 29/10/2026  
> **Nhánh thực hiện:** `ui-fixes` / `feature/disaster-guide-cloudinary`

---

## 1. Bối Cảnh Thực Tế & Mục Tiêu Nghiệp Vụ

### 1.1. Bối Cảnh Thực Địa Cứu Nạn
- Trong tình huống thiên tai dồn dập (bão lớn, lũ lụt, lũ quét, sạt lở đồi núi, dông sét...), người dân thường rơi vào tâm lý hoảng loạn, thiếu kỹ năng sinh tồn chuẩn mực và không biết cách phòng ngừa rủi ro điện giật, ngạt nước hay sập đổ công trình.
- Trước đây, trang Cẩm nang chỉ dừng lại ở các đoạn tóm tắt văn bản ngắn gọn, chưa có quy trình hành động trực quan theo từng bước (Step-by-step) và thiếu các hình ảnh minh họa tình huống thực tế để người dân dễ ghi nhớ, thực hành ngay trong đêm tối hoặc khi mất kết nối diện rộng.
- **Yêu cầu cấp thiết từ Người dùng & Đồ án:**
  1. Mỗi phương thức đối phó thiên tai cần có nút **"Xem chi tiết các bước"** dẫn tới trang chuyên sâu.
  2. Trang chi tiết chia nhỏ thành từng bước hành động thực tế, có **hình ảnh mô tả trực quan được tạo bởi Trí tuệ Nhân tạo (AI Generated Illustrations)**.
  3. **Lưu trữ & Lấy ảnh từ Cloudinary:** Toàn bộ hình ảnh hướng dẫn được cấu hình lấy trực tiếp từ **Cloudinary CDN** (`https://res.cloudinary.com/odwf5uoi/image/upload/cuunan247/guide_images/`), tuân thủ chuẩn công nghệ của đồ án, đồng thời trang bị cơ chế **Fallback an toàn về Local Storage** qua `data-fallback` và sự kiện `onerror` để bảo đảm hiển thị mượt mà ngay cả khi chạy offline hoặc ngắt mạng.
  4. **Bố cục Điều hướng Cột Trái Cố Định (Non-Floating Left Sidebar):** Các nút chọn tình huống đối phó (`01. Chuẩn bị sẵn sàng cho gia đình`, `02. Ở trong nơi trú ẩn kiên cố`, ...) được đưa sang **cột bên trái màn hình** (`col-lg-3 col-md-4`) trong luồng tĩnh tiêu chuẩn, **không di chuyển/trôi nổi đè lên nội dung ảnh và bài viết** khi cuộn chuột xuống dưới.
  5. **Loại bỏ nút "Phát tín hiệu SOS" trên toàn bộ trang Cẩm nang:** Xóa bỏ hoàn toàn nút SOS khỏi trang tổng quan (`/guide/disaster`) và chi tiết (`/guide/disaster/{slug}`) để trang cẩm nang tập trung thuần túy vào kiến thức phòng ngừa, kỹ năng tự cứu và danh bạ các số điện thoại khẩn cấp quốc gia (112, 114, 115).

### 1.2. Mục Tiêu Cụ Thể Của Phiên
1. **Nâng cấp Giao diện Cẩm nang Tổng quan (`/guide/disaster`):**
   - Bổ sung nút **"Xem chi tiết các bước"** và huy hiệu số lượng bước kèm ảnh AI trên từng thẻ thiên tai.
   - Xóa nút "Mở chức năng SOS" ở khối cứu hộ khẩn cấp cuối trang.
2. **Xây dựng Màn hình Chi tiết Từng bước Đối phó Thiên tai (`/guide/disaster/{slug}`):**
   - Thiết kế layout 2 cột: Cột trái (Sidebar chọn 7 loại thiên tai, không floating) và Cột phải (Quy trình chi tiết từng bước, ảnh AI 16:9, bảng NÊN/KHÔNG NÊN LÀM, Mẹo an toàn sống còn, Checklist sẵn sàng).
   - Xóa bỏ hoàn toàn nút "Phát tín hiệu SOS" trên Hero banner và khối hỗ trợ khẩn cấp cuối trang.
3. **Tích hợp Lưu trữ & Phục vụ Ảnh từ Cloudinary:**
   - Cấu hình `CLOUDINARY_GUIDE_BASE_URL` trong `DisasterGuideService`.
   - Mở rộng `CloudinaryService` với phương thức `uploadGuideImage()` và `uploadLocalFile()`.
   - Bổ sung REST API `POST /api/upload/guide-image` tiếp nhận ảnh từ client/admin tải lên Cloudinary folder `cuunan247/guide_images`.
   - Huy hiệu `Cloudinary CDN` tự động hiển thị trên khung ảnh khi tải từ CDN đám mây.
4. **Bộ Ảnh Minh Họa Sinh Tồn AI Đầy Đủ (18 Bước):**
   - 14 bức ảnh sinh tồn thực tế chất lượng cao tỉ lệ 16:9 và 4 biểu đồ đồ họa cứu nạn SVG.

---

## 2. Thiết Kế Kiến Trúc & Đặc Tả Kỹ Thuật (Architecture & Tech Specs)

### 2.1. Sơ Đồ Luồng Tải Lên & Phục Vụ Ảnh Cloudinary

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           QUY TRÌNH PHỤC VỤ & QUẢN TRỊ ẢNH                      │
└─────────────────────────────────────────────────────────────────────────────────┘

[Admin / Quản trị viên]               [Trình duyệt Người dân (Citizen)]
            │                                         │
            ▼                                         ▼
┌───────────────────────────────────────┐ ┌───────────────────────────────────────┐
│ POST /api/upload/guide-image         │ │ GET /guide/disaster/{slug}            │
│ (MultipartFile, guideSlug, stepNumber)│ │                                       │
└───────────────────┬───────────────────┘ └───────────────────┬───────────────────┘
                    │                                         │
                    ▼                                         ▼
┌───────────────────────────────────────┐ ┌───────────────────────────────────────┐
│         CloudinaryService             │ │        DisasterGuideService           │
│  Tải lên folder cuunan247/guide_images│ │  imageUrl: Cloudinary CDN URL         │
└───────────────────┬───────────────────┘ │  fallbackUrl: Local /images/guide/... │
                    │                     └───────────────────┬───────────────────┘
                    ▼                                         │
┌───────────────────────────────────────┐                     ▼
│  Cập nhật URL vào DisasterGuideService│ ┌───────────────────────────────────────┐
│  updateStepImage(slug, step, url)     │ │ <img th:src="${step.imageUrl}"        │
└───────────────────────────────────────┘ │      th:data-fallback="..."           │
                                          │      onerror="swapToFallback(this)" />│
                                          └───────────────────────────────────────┘
```

### 2.2. Bố Cục Giao Diện 2 Cột (2-Column Responsive Layout)

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│ [HERO BANNER] Breadcrumb · Tiêu đề thiên tai · Nguyên tắc sống còn · In tài liệu│
│ (KHÔNG CÓ NÚT SOS)                                                              │
└─────────────────────────────────────────────────────────────────────────────────┘
┌───────────────────────────┬─────────────────────────────────────────────────────┐
│ CỘT TRÁI (col-lg-3):      │ CỘT PHẢI (col-lg-9):                                │
│ DANH MỤC THIÊN TAI        │ CÁC BƯỚC ĐỐI PHÓ CHI TIẾT                           │
│ (Vị trí tĩnh, không trôi) │                                                     │
│ ┌───────────────────────┐ │ ┌─────────────────────────────────────────────────┐ │
│ │ 01. Chuẩn bị trước    │ │ │ BƯỚC 01: [Khung ảnh AI 16:9]   [Hành động]       │ │
│ │ 02. Bão, gió mạnh     │ │ │          (Badge Cloudinary CDN) [Nên / Không nên]│ │
│ │ 03. Lũ lụt ngập sâu   │ │ └─────────────────────────────────────────────────┘ │
│ │ 04. Lũ quét sạt lở    │ │ ┌─────────────────────────────────────────────────┐ │
│ │ 05. Dông, sét         │ │ │ BƯỚC 02: [Khung ảnh AI 16:9]   [Hành động]       │ │
│ │ 06. Động đất          │ │ └─────────────────────────────────────────────────┘ │
│ │ 07. Sau thiên tai     │ │ ┌─────────────────────────────────────────────────┐ │
│ └───────────────────────┘ │ │ CHECKLIST SẴN SÀNG CHO GIA ĐÌNH                  │ │
│                           │ └─────────────────────────────────────────────────┘ │
│                           │ ┌─────────────────────────────────────────────────┐ │
│                           │ │ TỔNG ĐÀI KHẨN CẤP QUỐC GIA (112, 114, 115)       │ │
│                           │ │ (KHÔNG CÓ NÚT KÍCH HOẠT SOS)                     │ │
│                           │ └─────────────────────────────────────────────────┘ │
│                           │ [Điều hướng Bài Trước / Bài Sau] · [Disclaimer]     │
└───────────────────────────┴─────────────────────────────────────────────────────┘
```

### 2.3. Cấu Trúc Dữ Liệu Cẩm Nang (DTO Models)

#### 1. `DisasterStepDto`
- `int stepNumber`: Thứ tự bước (1, 2, 3...).
- `String stepTitle`: Tiêu đề hành động chính.
- `String headline`: Khái quát ngắn gọn.
- `String description`: Nội dung phân tích tình huống chi tiết.
- `String imageUrl`: URL ảnh Cloudinary CDN (`https://res.cloudinary.com/odwf5uoi/image/upload/cuunan247/guide_images/...`).
- `String fallbackUrl`: URL ảnh dự phòng nội bộ (`/images/guide/...`).
- `String imageCaption`: Chú thích chi tiết nội dung ảnh AI mô tả.
- `String aiBadge`: Nhãn nhận diện ảnh AI ("Ảnh do AI tạo").
- `List<String> actions`: Danh sách hành động thiết thực.
- `List<String> dos`: Danh sách việc NÊN LÀM.
- `List<String> donts`: Danh sách việc TUYỆT ĐỐI TRÁNH.
- `String safetyTip`: Mẹo sống còn khẩn cấp.

#### 2. `DisasterGuideDto`
- `String id`: Mã định danh (slug) thiên tai (`preparation`, `storm`, `flood`, `landslide`, `lightning`, `earthquake`, `after`).
- `String stepCode`: Mã bước hiển thị (`01` đến `07`).
- `String category`: Phân loại thiên tai.
- `String title`: Tên cẩm nang.
- `String summary`: Tóm tắt tổng quan.
- `String principle`: Nguyên tắc an toàn cốt lõi.
- `String iconClass`: Icon FontAwesome.
- `String colorClass`: Màu sắc nhận diện (`topic-blue`, `topic-red`, `topic-teal`, `topic-amber`, `topic-purple`, `topic-green`).
- `List<DisasterStepDto> steps`: Danh sách các bước đối phó chi tiết.
- `List<String> checklist`: Danh sách kiểm tra mức độ sẵn sàng.
- `String emergencyNote`: Cảnh báo đặc biệt từ ban chỉ đạo.

---

## 3. Danh Sách Công Việc Đã Triển Khai & Hoàn Thành

### 3.1. Dịch Vụ Nghiệp Vụ Cẩm Nang (`DisasterGuideService.java`)
- [x] Tạo mới package `vn.edu.cuunan.dto` chứa `DisasterGuideDto.java` và `DisasterStepDto.java`.
- [x] Tích hợp hằng số `CLOUDINARY_GUIDE_BASE_URL` trỏ tới `https://res.cloudinary.com/odwf5uoi/image/upload/cuunan247/guide_images/`.
- [x] Hàm `buildStep()` thiết lập `imageUrl` = Cloudinary CDN và `fallbackUrl` = `/images/guide/{imageName}`.
- [x] Khởi tạo dữ liệu thực nghiệm cho toàn bộ 7 cẩm nang thiên tai (18 bước hành động chi tiết).
- [x] Hỗ trợ tra cứu theo slug (`getGuideBySlug`), lấy bài trước/bài sau (`getPreviousGuide`, `getNextGuide`).
- [x] Hỗ trợ cập nhật ảnh động từ Cloudinary: `updateStepImage(guideSlug, stepNumber, imageUrl)`.
- [x] Hỗ trợ đồng bộ hóa ảnh cẩm nang cục bộ lên Cloudinary: `syncImagesToCloudinary()`.

### 3.2. Điều Hướng Web Controller & Phân Quyền Bảo Mật
- [x] **Cập nhật `vn.edu.cuunan.controller.DisasterGuideController.java`:**
  - `GET /guide/disaster`: Nạp danh mục cẩm nang và hiển thị các nút xem chi tiết.
  - `GET /guide/disaster/{slug}` & `GET /guide/disaster/detail/{slug}`: Nạp cẩm nang chi tiết theo slug, danh sách bài chuyển đổi nhanh, bài trước/sau và render giao diện `guide/disaster-detail`. Tự động chuyển hướng về `/guide/disaster` nếu slug không tồn tại.
- [x] **Cập nhật `vn.edu.cuunan.config.SecurityConfig.java`:**
  - Quyền `.requestMatchers("/guide/**").hasRole("CITIZEN")` bảo vệ đồng bộ các trang chi tiết cẩm nang cho người dân đã đăng nhập.

### 3.3. Tích Hợp Upload & Quản Lý Ảnh Cloudinary
- [x] **Nâng cấp `vn.edu.cuunan.service.CloudinaryService.java`:**
  - Bổ sung `uploadGuideImage(MultipartFile file, String guideSlug, int stepNumber)`: Tải ảnh chuyên dụng lên Cloudinary thư mục `cuunan247/guide_images` với publicId được định danh chuẩn xác.
  - Bổ sung `uploadLocalFile(File file, String folderName, String publicId)`: Đọc file ảnh từ đĩa cứng và đồng bộ trực tiếp lên Cloudinary CDN.
  - Cơ chế tự động Fallback về Local Storage (`/uploads/guide_images/...`) nếu mất mạng hoặc chưa cấu hình credentials.
- [x] **Nâng cấp `vn.edu.cuunan.controller.UploadApiController.java`:**
  - REST API `POST /api/upload/guide-image` tiếp nhận upload ảnh cẩm nang, tự động gọi CloudinaryService và cập nhật URL vào `DisasterGuideService`.

### 3.4. Giao Diện Cẩm Nang Tổng Quan (`templates/guide/disaster-guide.html`)
- [x] Bổ sung khối hành động chân thẻ (`.guide-card-action`) cho toàn bộ 7 thẻ thiên tai:
  - Hiển thị số lượng bước kèm icon AI: `<span class="guide-step-count-pill"><i class="fa-solid fa-wand-magic-sparkles"></i>... bước kèm ảnh AI mô tả</span>`.
  - Nút bấm trực quan chuyển trang: `<a th:href="@{/guide/disaster/...}" class="guide-btn-detail">Xem chi tiết các bước <i class="fa-solid fa-arrow-right"></i></a>`.
- [x] **Xóa nút SOS:** Loại bỏ thẻ `<a th:href="@{/sos}" class="guide-sos-button">Mở chức năng SOS</a>` tại khối khẩn cấp cuối trang.
- [x] Cập nhật phụ đề thẻ topic #08 thành "Số điện thoại khẩn cấp".

### 3.5. Giao Diện Chi Tiết Cẩm Nang Từng Bước (`templates/guide/disaster-detail.html`)
- [x] **Bố cục 2 Cột Tiêu Chuẩn:**
  - **Cột Trái (`aside.col-lg-3`):** Khung card `.guide-sidebar-card` chứa danh mục 7 tình huống thiên tai (`01. Chuẩn bị sẵn cho gia đình`, `02. Ở trong nơi trú ẩn kiên cố`, ...). **Được đặt ở vị trí tĩnh (`position: static`), hoàn toàn không di chuyển/trôi nổi khi lướt xuống dưới trang**, giúp trải nghiệm đọc bài không bị che khuất.
  - **Cột Phải (`div.col-lg-9`):** Toàn bộ nội dung chi tiết các bước, ảnh AI minh họa, bảng Nên/Không nên, Mẹo sống còn và Checklist sẵn sàng.
- [x] **Khung Ảnh Minh Họa AI & Nguồn Cloudinary CDN:**
  - `th:src="${step.imageUrl}"`: Ưu tiên nạp từ Cloudinary CDN.
  - `th:data-fallback="@{${step.fallbackUrl}}"`: Thiết lập URL ảnh nội bộ dự phòng.
  - `onerror="if (this.dataset.fallback && this.src !== this.dataset.fallback) { this.src = this.dataset.fallback; }"`: Tự động đổi sang ảnh nội bộ nếu mạng lỗi hoặc offline.
  - Huy hiệu đôi: `Ảnh do AI tạo` (đen mờ) + `Cloudinary CDN` (xanh dương).
- [x] **Xóa Nút "Phát Tín Hiệu SOS":**
  - Xóa nút SOS màu đỏ trên Hero banner.
  - Xóa nút SOS tại khối tổng đài cứu nạn cuối trang chi tiết.

### 3.6. Định Kiểu CSS Tối Ưu (`src/main/resources/static/css/style.css`)
- [x] Xóa bỏ thuộc tính `position: sticky; top: 60px;` cũ gây ra hiện tượng thanh chọn trôi nổi cắt ngang ảnh.
- [x] Bổ sung các class `.guide-sidebar-card`, `.guide-sidebar-item`, `.guide-sidebar-item.active`, `.guide-sidebar-badge`, `.guide-sidebar-arrow`.
- [x] Thiết lập `max-width: 1320px` cho `.disaster-detail-page .guide-main` để layout 2 cột hiển thị thoáng đãng trên màn hình rộng.

### 3.7. Bộ Ảnh Sinh Tồn Trực Quan AI (18 Bước)
18 ảnh và sơ đồ đồ họa được tạo và tích hợp đầy đủ tại `src/main/resources/static/images/guide/`:
1. `prep-step1.jpg`: Theo dõi radar bão và cảnh báo khẩn cấp trên máy tính bảng & đài radio.
2. `prep-step2.jpg`: Ba lô sinh tồn Go-Bag khẩn cấp với đèn pin, nước sạch, lương khô, thuốc men.
3. `prep-step3.jpg`: Chằng chống mái tôn, dán băng keo cửa kính chữ X và đắp đê bao cát ngăn nước.
4. `prep-step4.jpg`: Gia đình họp bàn sơ tán theo bản đồ đến điểm tránh trú an toàn vùng cao.
5. `storm-step1.jpg`: Trú ẩn trong phòng kiên cố không có cửa sổ khi tâm bão quét qua.
6. `storm-step2.jpg`: Thao tác ngắt cầu dao tổng aptomat và khóa van bình gas an toàn.
7. `storm-step3.svg`: Đồ họa an toàn cảnh báo nguy cơ tôn bay, cây đổ và bẫy tâm bão lặng gió.
8. `flood-step1.jpg`: Di chuyển người và tài sản lên tầng 2 cao ráo khi nước lũ tràn vào nhà.
9. `flood-step2.jpg`: Mặc áo phao phản quang, dùng còi và đèn pin phát tín hiệu cho thuyền cứu trợ.
10. `landslide-step1.jpg`: Nhận biết vết nứt đất sườn đồi, cây nghiêng và nước suối đục ngầu.
11. `landslide-step2.jpg`: Chạy thoát hiểm theo phương vuông góc với hướng trượt lở của sườn dốc.
12. `landslide-step3.svg`: Đồ họa cảnh báo vùng nguy cơ cao: khe tụ thủy và chân vách taluy đứng.
13. `lightning-step1.jpg`: Tư thế ngồi xổm kiễng gót chân chụm sát, cúi gập đầu bịt tai giữa đồng trống.
14. `lightning-step2.svg`: Đồ họa phân biệt nơi trú ẩn an toàn và nơi nguy hiểm dông sét.
15. `earthquake-step1.svg`: Đồ họa chuẩn quốc tế Drop (Cúi thấp) – Cover (Che chắn dưới bàn) – Hold On (Bám chặt).
16. `earthquake-step2.jpg`: Sơ tán bằng cầu thang bộ và tập kết ngoài quảng trường/công viên rộng.
17. `after-step1.jpg`: Chủ nhà mang ủng bảo hộ kiểm tra kỹ rò rỉ điện và van gas trước khi vào nhà.
18. `after-step2.jpg`: Đun sôi nước uống tiệt trùng, dùng viên Cloramin B và vệ sinh khử trùng phòng dịch.

### 3.8. Bộ Kiểm Thử Tự Động (Automated Tests)
- [x] `DisasterGuideControllerTest.java` (7 test cases):
  - Xác thực chuyển hướng khi chưa đăng nhập.
  - Xác thực nút xem chi tiết và **không có nút SOS** trên trang tổng quan.
  - Xác thực trang chi tiết chuẩn bị: có các bước, ảnh AI, nhãn `Cloudinary CDN`, sidebar cột trái `guide-sidebar-card` và **hoàn toàn không có nút SOS**.
  - Xác thực cẩm nang bão, lũ lụt, điều hướng slug không hợp lệ, phân quyền bảo mật.
- [x] `UploadApiControllerTest.java`: Xác thực REST API upload ảnh cẩm nang lên Cloudinary.
- [x] `CloudinaryServiceTest.java`: Xác thực cơ chế upload ảnh và fallback an toàn.
- [x] **Kết quả kiểm thử:** Toàn bộ test suite vượt qua với **BUILD SUCCESS**.

---

## 4. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)

- [x] Người dân sau khi đăng nhập truy cập `/guide/disaster` thấy đầy đủ 7 cẩm nang, mỗi cẩm nang có nút **"Xem chi tiết các bước"** rõ ràng.
- [x] Nhấp vào nút "Xem chi tiết" chuyển hướng tức thì sang trang chi tiết tương ứng (ví dụ `/guide/disaster/preparation`), hiển thị toàn bộ các bước hành động.
- [x] Mỗi bước đều có hình ảnh AI mô tả sinh động, nhãn nhận diện ảnh AI và chú thích chi tiết.
- [x] Hình ảnh được cấu hình lấy trực tiếp từ Cloudinary CDN, có nhãn `Cloudinary CDN` trên giao diện, và có cơ chế tự động fallback về ảnh nội bộ nếu mạng ngắt kết nối.
- [x] **Thanh chọn các tình huống (01. Chuẩn bị..., 02. Ở trong nơi trú ẩn...) nằm ở bên trái màn hình, không trôi nổi hay di chuyển khi người dùng cuộn chuột xuống dưới trang.**
- [x] **Xóa bỏ hoàn toàn nút "Phát tín hiệu SOS" trên tất cả các trang cẩm nang (`/guide/disaster` và `/guide/disaster/{slug}`).**
- [x] Toàn bộ test suite unit & integration tests vượt qua 100% không có lỗi.

---

## 5. Hướng Dẫn Kiểm Thử & Chạy Thử (Testing & Demo Script)

1. **Khởi động ứng dụng:**
   ```bash
   mvn spring-boot:run
   ```
2. **Đăng nhập bằng tài khoản người dân:**
   - URL: `http://localhost:8080/auth/login`
   - Tài khoản: `citizen_dan` / Mật khẩu: `citizen123` (hoặc đăng ký tài khoản Citizen mới).
3. **Truy cập Cẩm nang ứng phó:**
   - Bấm vào mục **"Cẩm nang"** trên thanh điều hướng hoặc truy cập `http://localhost:8080/guide/disaster`.
   - Quan sát các thẻ thiên tai đều có nút **"Xem chi tiết các bước"** và thông tin số bước kèm ảnh AI.
   - Kiểm tra khối cứu nạn cuối trang: Không còn nút "Mở chức năng SOS", chỉ còn 3 đường dây nóng (112, 114, 115).
4. **Trải nghiệm trang Chi tiết Cẩm nang:**
   - Bấm **"Xem chi tiết các bước"** tại thẻ *01 · Chuẩn bị trước* -> Chuyển đến `/guide/disaster/preparation`.
   - Kiểm tra **cột bên trái**: Hiển thị card danh mục 7 tình huống. Khi lướt cuộn trang xuống dưới, thanh chọn nằm yên ở cột trái, không bị trôi đè lên nội dung bài viết hay che khuất ảnh.
   - Kiểm tra các bước sinh tồn: Có ảnh minh họa AI, badge `Cloudinary CDN`, bảng Nên làm / Không nên làm, Mẹo sống còn và Checklist.
   - Kiểm tra phần Hero banner và khối cứu nạn cuối trang: Hoàn toàn không có nút "Phát tín hiệu SOS".
5. **Kiểm thử Upload ảnh cẩm nang lên Cloudinary:**
   ```bash
   curl -X POST http://localhost:8080/api/upload/guide-image \
     -F "file=@duong_dan_anh.jpg" \
     -F "guideSlug=preparation" \
     -F "stepNumber=1"
   ```
   - API trả về JSON chứa `url` Cloudinary và cập nhật ngay vào cẩm nang.
