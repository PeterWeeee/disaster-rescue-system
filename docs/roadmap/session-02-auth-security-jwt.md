# PHIÊN LÀM VIỆC #02: XÁC THỰC & PHÂN QUYỀN (SPRING SECURITY 6 + JWT)
> **Trạng thái:** ✅ ĐÃ HOÀN THÀNH  
> **Người phụ trách:** **Võ Văn Thịnh (`vovanthinh2006@gmail.com`)**  
> **Thời điểm hoàn thành:** 25/10/2026  
> **Nhánh thực hiện:** `develop`

---

## 1. Mục Tiêu Phiên
- Cấu hình Spring Security 6 với kiến trúc Stateless JWT kết hợp Thymeleaf SSR (lưu token an toàn trong HttpOnly Cookie).
- Hiện thực chức năng Đăng ký tài khoản (Người dân) và Đăng nhập cho 4 vai trò: `ROLE_CITIZEN`, `ROLE_RESCUER`, `ROLE_COORDINATOR`, `ROLE_ADMIN`.
- Phân quyền trang và API tương ứng.

---

## 2. Danh Sách Công Việc Đã Thực Hiện

### 2.1. Entities & Repository
- [x] Tạo trọn bộ 7 Entities JPA chuẩn đa CSDL:
  - `Role.java`: `id`, `name` (`ROLE_CITIZEN`, `ROLE_RESCUER`, `ROLE_COORDINATOR`, `ROLE_ADMIN`).
  - `User.java`: `id`, `username`, `password` (BCrypt), `fullName`, `phone`, `email`, `address`, `avatarUrl`, `role`, `status`.
  - `RescueTeam.java`, `TeamMember.java`, `SosRequest.java`, `RescueAssignment.java`, `DisasterAlert.java`.
- [x] Tạo 7 JPA Repositories: `UserRepository`, `RoleRepository`, `RescueTeamRepository`, `TeamMemberRepository`, `SosRequestRepository`, `RescueAssignmentRepository`, `DisasterAlertRepository`.

### 2.2. Security & JWT Components
- [x] `JwtTokenProvider.java`: Tạo token, parse token, validate hạn sử dụng (expiration) và trích xuất claims chuẩn JJWT 0.12.x.
- [x] `CustomUserDetails.java` & `CustomUserDetailsService.java`: Load user từ CSDL theo username/SĐT.
- [x] `JwtAuthenticationFilter.java`: Đọc JWT từ HttpOnly Cookie `jwt_token` hoặc Header `Authorization: Bearer <token>`, nạp thông tin vào `SecurityContextHolder`.
- [x] `JwtAuthenticationEntryPoint.java`: Tự động chuyển hướng về `/auth/login?error=unauthorized` khi chưa đăng nhập.
- [x] `CustomAccessDeniedHandler.java`: Trả về `403 Forbidden` khi người dùng không đủ quyền hạn.
- [x] `SecurityConfig.java`:
  - `SecurityFilterChain` Stateless session, cấu hình permitAll cho `/`, `/sos/**`, `/auth/**`, `/map/**`, `/css/**`, `/js/**`, `/ws/**`.
  - Phân quyền: `/coordinator/**` (`COORDINATOR`, `ADMIN`), `/rescuer/**` (`RESCUER`, `ADMIN`), `/admin/**` (`ADMIN`).
- [x] `DataInitializer.java`: Tự động nạp 4 vai trò chuẩn và 4 tài khoản mặc định (`admin`, `coordinator`, `rescuer`, `citizen` với mật khẩu `123456`) khi khởi động ứng dụng.

### 2.3. Giao Diện Thymeleaf & Controller
- [x] `AuthController.java` & `AuthApiController.java`: Xử lý đăng ký, đăng nhập (cấp cookie JWT), đăng xuất (xóa cookie), và điều hướng theo vai trò.
- [x] `templates/auth/login.html`: Form đăng nhập Bootstrap kèm nút điền nhanh tài khoản demo.
- [x] `templates/auth/register.html`: Form đăng ký thông tin người dân/cứu hộ.
- [x] `templates/auth/forbidden.html`: Trang lỗi 403 thân thiện.
- [x] Dashboard placeholder cho từng role: `coordinator/dashboard.html`, `rescuer/missions.html`, `admin/dashboard.html`, `profile/index.html`.

---

## 3. Tiêu Chí Nghiệm Thu (Definition of Done - DoD)
- [x] Người dân đăng ký tài khoản mới thành công -> Mật khẩu được mã hóa BCrypt trong CSDL.
- [x] Đăng nhập thành công -> Trả về JWT token lưu trong Cookie `jwt_token` và Header.
- [x] Truy cập đường dẫn `/coordinator/dashboard` khi chưa đăng nhập -> Tự động chuyển hướng về `/auth/login?error=unauthorized`.
- [x] Tài khoản `CITIZEN` truy cập vào trang của `COORDINATOR` -> Bị chặn lỗi `403 Forbidden`.
- [x] Bộ kiểm thử tự động 13 unit & integration tests (`SecurityIntegrationTest`, `AuthServiceTest`, `JwtTokenProviderTest`, `DisasterRescueApplicationTests`) đạt **100% BUILD SUCCESS**.
