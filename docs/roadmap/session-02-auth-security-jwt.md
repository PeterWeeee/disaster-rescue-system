# PHIÊN LÀM VIỆC #02: XÁC THỰC & PHÂN QUYỀN (SPRING SECURITY 6 + JWT)
> **Trạng thái:** ⏳ Chưa bắt đầu  
> **Ước tính thời gian:** 1 phiên làm việc  
> **Nhánh thực hiện:** `develop`

---

## 1. Mục Tiêu Phiên
- Cấu hình Spring Security 6 với kiến trúc Stateless JWT kết hợp Thymeleaf SSR (lưu token an toàn trong HttpOnly Cookie).
- Hiện thực chức năng Đăng ký tài khoản (Người dân) và Đăng nhập cho 4 vai trò: `ROLE_CITIZEN`, `ROLE_RESCUER`, `ROLE_COORDINATOR`, `ROLE_ADMIN`.
- Phân quyền trang và API tương ứng.

---

## 2. Danh Sách Công Việc Cần Thực Hiện

### 2.1. Entities & Repository
- `Role.java`: `id`, `name` (`ROLE_CITIZEN`, `ROLE_RESCUER`, `ROLE_COORDINATOR`, `ROLE_ADMIN`).
- `User.java`: `id`, `username`, `password` (BCrypt), `fullName`, `phone`, `email`, `role`, `status`.
- `UserRepository.java`, `RoleRepository.java`.

### 2.2. Security & JWT Components
- `JwtUtils.java`: Tạo token, parse token, validate hạn sử dụng (expiration) và claims.
- `CustomUserDetailsService.java`: Load user từ CSDL theo username/email.
- `JwtAuthenticationFilter.java`: Đọc JWT từ Cookie `jwt_token` hoặc Header `Authorization`, nạp thông tin vào `SecurityContextHolder`.
- `SecurityConfig.java`:
  - `SecurityFilterChain` cấu hình permitAll cho `/`, `/sos/new`, `/login`, `/register`, `/css/**`, `/js/**`, `/ws/**`.
  - Phân quyền: `/coordinator/**` chỉ cho `COORDINATOR` và `ADMIN`, `/rescuer/**` cho `RESCUER`, `/admin/**` cho `ADMIN`.

### 2.3. Giao Diện Thymeleaf
- `templates/auth/login.html`: Form đăng nhập Bootstrap kèm thông báo lỗi.
- `templates/auth/register.html`: Form đăng ký thông tin người dân (Họ tên, SĐT, Địa chỉ, Mật khẩu).
- Cập nhật Navbar hiển thị tên người dùng và nút Đăng xuất khi đã đăng nhập.

---

## 3. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)
- [ ] Người dân đăng ký tài khoản mới thành công -> Mật khẩu được mã hóa BCrypt trong CSDL.
- [ ] Đăng nhập thành công -> Trả về JWT token lưu trong Cookie `jwt_token`.
- [ ] Truy cập đường dẫn `/coordinator/dashboard` khi chưa đăng nhập -> Tự động chuyển hướng về `/login`.
- [ ] Tài khoản `CITIZEN` truy cập vào trang của `COORDINATOR` -> Bị chặn lỗi `403 Forbidden`.
