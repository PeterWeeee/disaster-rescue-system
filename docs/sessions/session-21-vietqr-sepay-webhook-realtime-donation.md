# PHIÊN LÀM VIỆC #21: TÍCH HỢP CHUYỂN KHOẢN NGÂN HÀNG THẬT QUA VIETQR VÀ SEPAY WEBHOOK (REAL-TIME DONATION AUTO-CONFIRMATION)
> **Trạng thái:** ✅ ĐÃ HOÀN THÀNH (100%)  
> **Người phụ trách:** **Tri Thai (PeterWeeee - `NguyenTT162.4@gmail.com`)**  
> **Thời điểm hoàn thành:** 31/10/2026  
> **Nhánh thực hiện:** `main`  
> **Kết quả kiểm thử:** 133/133 bài test unit/integration passed (BUILD SUCCESS 100%)

---

## 1. Bối Cảnh Yêu Cầu & Vấn Đề Cần Giải Quyết

Trong hệ thống Cứu Nạn 24/7, Cổng quyên góp ủng hộ cứu trợ thiên tai (`/donations`) đóng vai trò huyết mạch để huy động nguồn lực tài chính từ cộng đồng cho đồng bào bị bão lũ và trang cấp trang thiết bị cho các đội cứu hộ.

Trước phiên làm việc này:
1. **Mã VietQR mang thông tin ngân hàng mẫu (Demo / Placeholder):**
   - Thông tin tài khoản ngân hàng thụ hưởng đang là dữ liệu giả định MB Bank (`0909123456`, `QUY CUU TRO KHAN CAP CUUNAN 247`), chưa gắn vào tài khoản ngân hàng thực tế để tiếp nhận giao dịch tiền thật.
2. **Thiếu cơ chế xác nhận thanh toán ngân hàng tự động (Auto-reconciliation / Webhook):**
   - Khi người hảo tâm quét mã QR chuyển khoản tiền thật qua ứng dụng ngân hàng, tiền vào tài khoản người nhận nhưng hệ thống máy chủ web không hề hay biết.
   - Người dùng bắt buộc phải bấm nút thủ công *"Tôi đã chuyển khoản thành công"* để đổi trạng thái, dễ dẫn đến tình trạng gian lận hoặc bấm nhầm.
3. **Chưa tự động điều chỉnh số tiền thực tế nhận được:**
   - Trong từ thiện thực tế, người dân có thể quét mã nhưng trên ứng dụng ngân hàng họ tùy ý sửa tăng số tiền (ví dụ trên web chọn 50.000đ nhưng chuyển 100.000đ hoặc ngược lại). Hệ thống chưa có cơ chế đối soát để ghi nhận chính xác từng đồng thực tế vào tổng quỹ cứu trợ.

---

## 2. Các Giải Pháp Kỹ Thuật Đã Triển Khai

### 2.1. Cấu hình Tài khoản Ngân hàng Thật (BIDV) & Chuẩn VietQR Động Napas 24/7
- **Tài khoản thụ hưởng thực tế:**
  - Ngân hàng: **BIDV** (Ngân hàng TMCP Đầu tư và Phát triển Việt Nam)
  - Mã định danh Napas BIN: `970418`
  - Số tài khoản: `6351078034`
  - Chủ tài khoản: `NGUYEN TRI THAI`
- **Động hóa cấu hình trong `application.yml`:**
  - Hỗ trợ ghi đè cấu hình linh hoạt qua biến môi trường (`DONATION_BANK_BIN`, `DONATION_BANK_ACCOUNT_NO`, `DONATION_BANK_ACCOUNT_NAME`, `SEPAY_API_KEY`).
- **Dynamic VietQR Generator (`vietqr.io`):**
  - Trong `DonationController.java` (`/donations/pay/{code}`): Tạo URL VietQR chuẩn Napas 24/7 chứa sẵn: Mã BIN ngân hàng, Số tài khoản, Số tiền cần chuyển, và Mã giao dịch duy nhất dạng `CNxxxx` trong nội dung chuyển khoản (`addInfo`).
  - Khi người dùng quét mã trên bất kỳ App ngân hàng nào (BIDV, Vietcombank, MB, Techcombank, VPBank,...), các trường thông tin (Số tài khoản, Tên chủ thẻ, Số tiền, Nội dung) đều được **tự động điền 100%**, người chuyển chỉ cần xác nhận bảo mật là tiền đi ngay.

### 2.2. Tích hợp SePay Webhook Biến Động Số Dư Thời Gian Thực
- **Kiến trúc DTO tiếp nhận dữ liệu SePay:**
  - Tạo `SepayWebhookDto.java` hứng đầy đủ các trường từ webhook của SePay: `gateway`, `transactionDate`, `accountNumber`, `content`, `transferType`, `transferAmount`, `accumulated`, `referenceCode`, `description`.
- **API Endpoint Webhook Bảo Mật (`DonationApiController.java`):**
  - Khai báo endpoint công khai: `POST /api/donations/sepay-webhook`.
  - **Bảo mật:** Kiểm tra tiêu đề `Authorization: Apikey <TOKEN>` hoặc `Bearer <TOKEN>` khớp với API Key được cấp trên SePay (`BBE3HIZC...`).
  - **Lọc giao dịch:** Chỉ xử lý khi `transferType == "in"` (tiền vào tài khoản).
  - **Trích xuất mã giao dịch thông minh:** Dùng biểu thức chính quy Regex `Pattern.compile("CN\\d+", Pattern.CASE_INSENSITIVE)` để bắt chính xác mã ủng hộ dạng `CN123456...` nằm trong chuỗi nội dung chuyển khoản (bất chấp ngân hàng có thêm tiền tố/hậu tố SMS).
- **Tự động đối soát và cập nhật số tiền thực tế (`DonationService.java`):**
  - Xây dựng phương thức `@Transactional confirmSepayPayment(String transactionCode, BigDecimal actualAmount)`.
  - Nếu người dùng chuyển đúng hoặc sửa số tiền khác đi (ví dụ chuyển nhiều hơn), hệ thống tự động gán `donation.setAmount(actualAmount)`, chuyển trạng thái đơn sang `SUCCESS`, lưu mốc thời gian `paidAt = LocalDateTime.now()` và kích hoạt gửi email tri ân.
  - Tổng ngân sách quỹ cứu trợ được tính toán trực tiếp qua `donationRepository.sumAmountByStatus("SUCCESS")` nên ngay lập tức cộng dồn chính xác số tiền vừa nhận.

### 2.3. Tự động Nhận Diện và Chuyển Trang Thành Công trên Giao Diện
- Trong `pay.html`: Đoạn script polling định kỳ mỗi 3 giây gọi API `GET /api/donations/status/{code}`.
- Ngay khi SePay Webhook cập nhật đơn sang `SUCCESS`, giao diện của người hảo tâm lập tức nhận diện thành công, tự động chuyển hướng sang trang Biên nhận điện tử `/donations/success?code=...` mà người dùng không cần phải bấm bất kỳ nút nào.

---

## 3. Danh Sách Tệp Đã Thay Đổi & Tạo Mới

| Tệp | Trạng Thái | Mô Tả Thay Đổi |
| :--- | :--- | :--- |
| `src/main/resources/application.yml` | Chỉnh sửa | Bổ sung cấu hình `sepay.api-key` và `donation.bank` (BIN 970418, STK 6351078034, NGUYEN TRI THAI). |
| `src/main/java/vn/edu/cuunan/dto/SepayWebhookDto.java` | Tạo mới | DTO ánh xạ gói tin JSON Webhook từ SePay khi có biến động số dư ngân hàng. |
| `src/main/java/vn/edu/cuunan/service/DonationService.java` | Chỉnh sửa | Bổ sung phương thức `confirmSepayPayment` tự động cập nhật số tiền thực tế và trạng thái SUCCESS. |
| `src/main/java/vn/edu/cuunan/controller/DonationApiController.java` | Chỉnh sửa | Thêm endpoint `POST /api/donations/sepay-webhook` với cơ chế xác thực API Key và regex trích xuất mã `CNxxxx`. |
| `src/main/java/vn/edu/cuunan/controller/DonationController.java` | Chỉnh sửa | Động hóa thông tin tài khoản BIDV từ `@Value`, tạo mã Dynamic VietQR chuẩn Napas 24/7. |
| `src/main/resources/templates/donation/pay.html` | Chỉnh sửa | Cập nhật thông tin ngân hàng thụ hưởng BIDV, đảm bảo hiển thị đúng số tài khoản và polling 3s. |
| `src/test/java/vn/edu/cuunan/service/DonationServiceTest.java` | Chỉnh sửa | Bổ sung unit test `testConfirmSepayPayment` kiểm tra tự động cập nhật trạng thái và số tiền. |
| `src/test/java/vn/edu/cuunan/controller/DonationApiControllerTest.java` | Tạo mới | Kiểm thử bảo mật API Key Webhook SePay (chấp nhận đúng key, từ chối 401 khi sai key). |

---

## 4. Hướng Dẫn Vận Hành & Kiểm Thử Thực Địa (ngrok / SePay)

1. **Khởi chạy ứng dụng:** Chạy server Spring Boot trên cổng `8080`.
2. **Tạo Tunnel công khai ra Internet (Localhost testing):**
   ```powershell
   ngrok http 8080
   ```
3. **Cấu hình Webhook trên SePay Dashboard (my.sepay.vn):**
   - URL Webhook: `https://<ngrok-domain>/api/donations/sepay-webhook`
   - Tài khoản nhận: BIDV `6351078034`
   - Kiểu xác thực: `API Key` (`BBE3HIZC69VHCFUVNWDZC0K8ISMAGPXMJXJIFOAQ3BP0JRCUEGW8TSRZOYGLEYF2`)
4. **Quy trình kiểm thử chuyển khoản thật:**
   - Vào `http://localhost:8080/donations`, chọn ủng hộ `2,000 đ` hoặc `5,000 đ`.
   - Mở App ngân hàng quét mã QR hiển thị.
   - Bấm chuyển tiền ➔ Tiền vào tài khoản BIDV ➔ SePay gửi Webhook về máy chủ ➔ Màn hình web tự động nhảy sang trang Thành công và cộng tiền vào Quỹ!
