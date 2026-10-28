# Bản xem trước giao diện Cứu Nạn 24/7

Đây là prototype HTML tĩnh, mở trực tiếp bằng trình duyệt và không gọi backend. Các vị trí trên bản đồ là minh họa; chỉ số/tình trạng trong trang không đại diện dữ liệu cứu hộ thực.

## Hướng thiết kế

- Tham khảo cách website Trạm Cứu Trợ chia nội dung thành giới thiệu, bản đồ, tính năng và quy trình. Prototype dùng cấu trúc nội dung tương tự ở mức ý tưởng, không sao chép logo, hình ảnh, câu chữ hoặc mã nguồn của website đó.
- Nhận diện riêng cho Cứu Nạn 24/7: navy làm nền tin cậy, xanh dương cho hành động và điều hướng, đỏ chỉ dành cho tình huống SOS, xanh lá cho điểm an toàn.
- Nền sáng, thẻ có đường viền mảnh, bán kính vừa phải, bóng nhẹ; tiêu đề dùng Manrope, nội dung dùng DM Sans.
- Bản đồ được vẽ bằng SVG/CSS nội tuyến để mở offline. Chú giải giải thích rõ điểm yêu cầu, đội cứu hộ, điểm an toàn và vùng cần lưu ý.
- Các số liệu/vị trí chỉ là demo và được đánh dấu trong phần thông tin bản xem trước. Không kết nối API, GPS hoặc cơ sở dữ liệu.

## Các phần đã dựng

1. Header với logo chữ/biểu tượng riêng, điều hướng và CTA.
2. Hero với thông điệp chính, hai CTA và bản đồ minh họa.
3. Dải thông tin demo được ghi chú rõ.
4. Sáu thẻ tính năng: SOS, yêu cầu cứu trợ, bản đồ, điểm an toàn, điều phối và trạng thái.
5. Bản đồ lớn với đường, vùng lưu ý và chú giải.
6. Quy trình bốn bước: gửi yêu cầu, tiếp nhận/xác minh, phân công và cập nhật.
7. CTA cuối trang và footer gọn.

## Responsive và tương tác

- Desktop: hero hai cột, lưới tính năng ba cột, bản đồ kèm chú giải.
- Tablet: hero một cột, bản đồ xếp dọc, lưới thông tin gọn.
- Mobile: menu thu gọn, lưới thẻ một hoặc hai cột, CTA dễ chạm, nội dung xếp dọc.
- `app.js` chỉ xử lý đóng/mở menu mobile và phím Escape; các liên kết cuộn tới phần tương ứng trong trang.

## Mở xem

Mở `ui-preview/index.html` trực tiếp trong trình duyệt. Không cần chạy Spring Boot. Google Fonts có thể dùng font dự phòng nếu máy không có kết nối mạng.
