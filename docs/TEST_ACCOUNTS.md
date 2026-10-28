# 🔑 DANH SÁCH TÀI KHOẢN VÀ HƯỚNG DẪN KIỂM THỬ — CỨUNẠN 24/7 MIỀN TRUNG

> **Mật khẩu dùng chung cho tất cả tài khoản thử nghiệm (Development / Test):**  
> `123456`  
> *(Tất cả mật khẩu đều được băm bằng thuật toán BCrypt chuẩn Spring Security: `$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.`)*

---

## 1. Bảng Tổng Hợp Tài Khoản Thử Nghiệm

### 1.1. Quản Trị Viên (ROLE_ADMIN)
| Username | Mật khẩu | Họ và Tên | Điện thoại | Phạm vi quản lý | URL truy cập |
| :--- | :---: | :--- | :--- | :--- | :--- |
| `admin` | `123456` | Quản Trị Viên Hệ Thống | 0901112233 | Toàn quốc / Miền Trung | `/admin/dashboard` |

---

### 1.2. Điều Phối Viên (ROLE_COORDINATOR)
| Username | Mật khẩu | Họ và Tên | Điện thoại | Địa bàn phụ trách | URL giao diện |
| :--- | :---: | :--- | :--- | :--- | :--- |
| `coordinator` | `123456` | Điều Phối Viên TT Chỉ Huy | 0902223344 | Trung tâm điều hành Đà Nẵng | `/coordinator/dispatch` |
| `dieupoi1` | `123456` | Điều Phối Viên Khu Vực 2 | 0902223355 | Duyên hải Miền Trung | `/coordinator/dispatch` |
| `coord_thanhhoa` | `123456` | Nguyễn Văn Điều Phối Thanh Hóa | 0902010001 | Tỉnh Thanh Hóa | `/coordinator/dispatch` |
| `coord_nghean` | `123456` | Trần Đình Điều Phối Nghệ An | 0902020002 | Tỉnh Nghệ An | `/coordinator/dispatch` |
| `coord_hatinh` | `123456` | Lê Văn Điều Phối Hà Tĩnh | 0902030003 | Tỉnh Hà Tĩnh | `/coordinator/dispatch` |
| `coord_quangtri` | `123456` | Phạm Văn Điều Phối Quảng Trị | 0902040004 | Tỉnh Quảng Trị | `/coordinator/dispatch` |
| `coord_hue` | `123456` | Hoàng Văn Điều Phối Huế | 0902050005 | Thành phố Huế | `/coordinator/dispatch` |
| `coord_danang` | `123456` | Ngô Quốc Điều Phối Đà Nẵng | 0902060006 | Thành phố Đà Nẵng | `/coordinator/dispatch` |
| `coord_quangngai` | `123456` | Võ Văn Điều Phối Quảng Ngãi | 0902070007 | Tỉnh Quảng Ngãi | `/coordinator/dispatch` |
| `coord_gialai` | `123456` | Đỗ Văn Điều Phối Gia Lai | 0902080008 | Tỉnh Gia Lai | `/coordinator/dispatch` |
| `coord_daklak` | `123456` | Bùi Văn Điều Phối Đắk Lắk | 0902090009 | Tỉnh Đắk Lắk | `/coordinator/dispatch` |
| `coord_khanhhoa` | `123456` | Đinh Văn Điều Phối Khánh Hòa | 0902100010 | Tỉnh Khánh Hòa | `/coordinator/dispatch` |
| `coord_lamdong` | `123456` | Chu Văn Điều Phối Lâm Đồng | 0902110011 | Tỉnh Lâm Đồng | `/coordinator/dispatch` |

---

### 1.3. Đội Trưởng Cứu Hộ (ROLE_RESCUER) — 23 Đội Tại 11 Tỉnh/Thành
*Mỗi tài khoản đội trưởng đều được liên kết trực tiếp với Đội cứu hộ tương ứng trong cơ sở dữ liệu.*

| Tỉnh / Thành | Username | Họ và tên Đội trưởng | Tên Đội cứu hộ | Tọa độ GPS | Trạng thái đội |
| :--- | :--- | :--- | :--- | :---: | :---: |
| **Thanh Hóa** | `rescuer_th01` | Lê Văn Thành | Đội Cứu Hộ Thanh Hóa 01 (Sông Mã) | 19.8074, 105.7764 | AVAILABLE |
| **Thanh Hóa** | `rescuer_th02` | Trần Quốc Hùng | Đội Cứu Hộ Thanh Hóa 02 (Nghi Sơn) | 19.3400, 105.8000 | AVAILABLE |
| **Nghệ An** | `rescuer_na01` | Nguyễn Văn Nghĩa | Đội Cứu Hộ Nghệ An 01 (Vinh - Sông Lam) | 18.6799, 105.6813 | AVAILABLE |
| **Nghệ An** | `rescuer_na02` | Võ Thị An | Đội Cứu Hộ Nghệ An 02 (Kỳ Sơn - Miền Núi) | 19.4100, 104.1500 | BUSY |
| **Hà Tĩnh** | `rescuer_ht01` | Phạm Đình Tĩnh | Đội Cứu Hộ Hà Tĩnh 01 (Cẩm Xuyên) | 18.2500, 105.9500 | BUSY |
| **Hà Tĩnh** | `rescuer_ht02` | Lê Hồng Sơn | Đội Cứu Hộ Hà Tĩnh 02 (Hương Khê) | 18.1700, 105.7000 | AVAILABLE |
| **Quảng Trị** | `rescuer_qt01` | Hoàng Văn Trị | Đội Cứu Hộ Quảng Trị 01 (Đông Hà - Thạch Hãn) | 16.8150, 107.1000 | AVAILABLE |
| **Quảng Trị** | `rescuer_qt02` | Đặng Thị Quảng | Đội Cứu Hộ Quảng Trị 02 (Hướng Hóa) | 16.6800, 106.6600 | AVAILABLE |
| **Thành phố Huế**| `rescuer_hue01`| Ngô Văn Huệ | Đội Cứu Hộ Huế 01 (Sông Hương & Tam Giang) | 16.4637, 107.5909 | BUSY |
| **Thành phố Huế**| `rescuer_hue02`| Lý Thị Hương | Đội Cứu Hộ Huế 02 (Hương Trà) | 16.4300, 107.5000 | AVAILABLE |
| **Đà Nẵng** | `rescuer` | Đặng Văn Cứu Hộ | Đội Cứu Hộ Hải Châu (Xuồng máy) | 16.0678, 108.2208 | BUSY |
| **Đà Nẵng** | `cuuho1` | Nguyễn Văn Sơn Trà | Đội Tình Nguyện Cứu Trợ Sơn Trà | 16.1022, 108.2450 | AVAILABLE |
| **Đà Nẵng** | `rescuer_dn03` | Dương Văn Nẵng | Đội Cứu Hộ Hòa Vang (Cơ động) | 15.9800, 108.1800 | AVAILABLE |
| **Quảng Ngãi** | `rescuer_qn01` | Đinh Công Ngãi | Đội Cứu Hộ Quảng Ngãi 01 (Sông Trà Khúc) | 15.1200, 108.7922 | AVAILABLE |
| **Quảng Ngãi** | `rescuer_qn02` | Bùi Văn Trà | Đội Cứu Hộ Quảng Ngãi 02 (Ba Tơ) | 14.7700, 108.7200 | AVAILABLE |
| **Gia Lai** | `rescuer_gl01` | Ksor Y Lai | Đội Cứu Hộ Gia Lai 01 (Pleiku) | 13.9833, 108.0000 | AVAILABLE |
| **Gia Lai** | `rescuer_gl02` | Rmah Đinh | Đội Cứu Hộ Gia Lai 02 (Ia Grai) | 14.0500, 107.7500 | OFFLINE |
| **Đắk Lắk** | `rescuer_dl01` | Y Krin Niê | Đội Cứu Hộ Đắk Lắk 01 (Buôn Ma Thuột) | 12.6667, 108.0500 | AVAILABLE |
| **Đắk Lắk** | `rescuer_dl02` | H'Lan Byă | Đội Cứu Hộ Đắk Lắk 02 (Ea Súp) | 13.1500, 107.8800 | AVAILABLE |
| **Khánh Hòa** | `rescuer_kh01` | Trần Văn Hòa | Đội Cứu Hộ Khánh Hòa 01 (Nha Trang) | 12.2388, 109.1967 | AVAILABLE |
| **Khánh Hòa** | `rescuer_kh02` | Nguyễn Thị Khánh | Đội Cứu Hộ Khánh Hòa 02 (Cam Ranh) | 11.9200, 109.1500 | BUSY |
| **Lâm Đồng** | `rescuer_ld01` | Cil Ha Đồng | Đội Cứu Hộ Lâm Đồng 01 (Đà Lạt - Đèo Dốc) | 11.9404, 108.4583 | AVAILABLE |
| **Lâm Đồng** | `rescuer_ld02` | K'Brooke | Đội Cứu Hộ Lâm Đồng 02 (Bảo Lộc) | 11.5400, 107.8000 | AVAILABLE |

---

### 1.4. Người Dân (ROLE_CITIZEN)
| Username | Mật khẩu | Họ và Tên | Điện thoại | Địa phương |
| :--- | :---: | :--- | :--- | :--- |
| `citizen` | `123456` | Nguyễn Văn Dân | 0900000004 | Đà Nẵng |
| `nguoidan1` | `123456` | Lê Thị Cứu Trợ | 0900000014 | Đà Nẵng |
| `citizen_th` | `123456` | Nguyễn Thị Thanh Hóa | 0901000101 | Thanh Hóa |
| `citizen_na` | `123456` | Lê Văn Nghệ An | 0901000102 | Nghệ An |
| `citizen_ht` | `123456` | Phạm Văn Hà Tĩnh | 0901000103 | Hà Tĩnh |
| `citizen_qt` | `123456` | Hoàng Văn Quảng Trị | 0901000104 | Quảng Trị |
| `citizen_hue` | `123456` | Trần Thị Huế | 0901000105 | Thừa Thiên Huế |
| `citizen_qn` | `123456` | Đinh Văn Quảng Ngãi | 0901000106 | Quảng Ngãi |
| `citizen_gl` | `123456` | Ksor H'Lâm | 0901000107 | Gia Lai |
| `citizen_dl` | `123456` | Y Krin Dân | 0901000108 | Đắk Lắk |
| `citizen_kh` | `123456` | Hoàng Thị Khánh Hòa | 0901000109 | Khánh Hòa |
| `citizen_ld` | `123456` | Cil Ha Kôi | 0901000110 | Lâm Đồng |

---

## 2. Kịch Bản Kiểm Thử Các Chức Năng Chính

### Kịch bản 1: Đăng nhập Đội cứu hộ Hải Châu và xử lý nhiệm vụ
1. Truy cập: `http://localhost:8080/login`
2. Đăng nhập tài khoản: `rescuer` / `123456`
3. Hệ thống chuyển hướng vào: `http://localhost:8080/rescuer/missions`
4. **Kết quả:** Hiển thị thông tin **"Đội Cứu Hộ Hải Châu (Xuồng máy)"** và 3 nhiệm vụ:
   - **Tab Chờ xác nhận (1):** Ca SOS #2 (Lê Văn Hùng)
   - **Tab Đang thực hiện (1):** Ca SOS #1 (Trần Thị Bé)
   - **Tab Đã hoàn thành (1):** Ca SOS #3 (Phạm Minh Tâm)
5. Nhấn **"Chấp nhận nhiệm vụ"** hoặc **"Báo cáo hoàn thành"** để kiểm tra tương tác thời gian thực.

### Kịch bản 2: Đăng nhập Điều phối viên và phân công ca SOS
1. Đăng nhập tài khoản: `coordinator` / `123456` (hoặc `coord_thanhhoa` cho Thanh Hóa)
2. Truy cập: `http://localhost:8080/coordinator/dispatch`
3. Xem danh sách các ca SOS chờ ứng cứu (ví dụ: ca SOS Thanh Hóa, Quảng Trị, Khánh Hòa...)
4. Bấm vào một ca SOS trên bản đồ hoặc danh sách, chọn **"Tìm đội cứu hộ gần nhất"**
5. Hệ thống tính khoảng cách GPS (Haversine) và gợi ý đội gần nhất.
6. Bấm **"Phân công đội"** -> Nhiệm vụ được giao lập tức qua WebSocket đến đội trưởng.

### Kịch bản 3: Người dân phát tín hiệu SOS
1. Đăng nhập tài khoản `citizen` (hoặc gửi ẩn danh tại trang chủ `/sos`)
2. Điền thông tin: Họ tên, SĐT, định vị GPS, tình trạng ngập nước, người già/trẻ em
3. Bấm **"GỬI YÊU CẦU CỨU NẠN KHẨN CẤP"**
4. Màn hình tự động chuyển sang trang theo dõi trực tiếp: `/sos/track/{id}`
5. Xem vị trí của mình và vị trí đội cứu hộ di chuyển thời gian thực trên bản đồ Leaflet.
