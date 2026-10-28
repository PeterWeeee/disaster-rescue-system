-- =========================================================================
-- ĐỒ ÁN LẬP TRÌNH WEB - NHÓM 04
-- DỰ ÁN: HỆ THỐNG CỨU NẠN THIÊN TAI (CỨUNẠN 24/7)
-- FILE: 03_seed_data.sql
-- MỤC ĐÍCH: NẠP DỮ LIỆU MẪU BAN ĐẦU CHO 11 TỈNH/THÀNH MIỀN TRUNG
-- =========================================================================

USE [cuunan_db];
GO

-- 1. Nạp Vai trò mặc định (Roles)
IF NOT EXISTS (SELECT 1 FROM [dbo].[roles] WHERE [name] = 'ROLE_ADMIN')
    INSERT INTO [dbo].[roles] ([name]) VALUES ('ROLE_ADMIN');

IF NOT EXISTS (SELECT 1 FROM [dbo].[roles] WHERE [name] = 'ROLE_COORDINATOR')
    INSERT INTO [dbo].[roles] ([name]) VALUES ('ROLE_COORDINATOR');

IF NOT EXISTS (SELECT 1 FROM [dbo].[roles] WHERE [name] = 'ROLE_RESCUER')
    INSERT INTO [dbo].[roles] ([name]) VALUES ('ROLE_RESCUER');

IF NOT EXISTS (SELECT 1 FROM [dbo].[roles] WHERE [name] = 'ROLE_CITIZEN')
    INSERT INTO [dbo].[roles] ([name]) VALUES ('ROLE_CITIZEN');

PRINT N'>>> Đã nạp 4 Roles chuẩn hệ thống!';
GO

-- 2. Nạp Người dùng mẫu (Mật khẩu mặc định: 123456 -> BCrypt hash: $2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.)
DECLARE @adminRoleId INT = (SELECT [id] FROM [dbo].[roles] WHERE [name] = 'ROLE_ADMIN');
DECLARE @coordRoleId INT = (SELECT [id] FROM [dbo].[roles] WHERE [name] = 'ROLE_COORDINATOR');
DECLARE @rescuerRoleId INT = (SELECT [id] FROM [dbo].[roles] WHERE [name] = 'ROLE_RESCUER');
DECLARE @citizenRoleId INT = (SELECT [id] FROM [dbo].[roles] WHERE [name] = 'ROLE_CITIZEN');
DECLARE @hash VARCHAR(255) = '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.';

-- 2.1. Admin & Điều phối viên
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'admin')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('admin', @hash, N'Quản Trị Viên Hệ Thống', '0901112233', 'admin@cuunan247.vn', @adminRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'coordinator')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('coordinator', @hash, N'Điều Phối Viên TT Chỉ Huy', '0902223344', 'coord@cuunan247.vn', @coordRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'dieupoi1')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('dieupoi1', @hash, N'Điều Phối Viên Khu Vực 2', '0902223355', 'dieupoi1@cuunan247.vn', @coordRoleId, 'ACTIVE');

-- Điều phối viên 11 tỉnh/thành
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'coord_thanhhoa')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('coord_thanhhoa', @hash, N'Nguyễn Văn Điều Phối Thanh Hóa', '0902010001', 'coord.th@cuunan247.vn', @coordRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'coord_nghean')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('coord_nghean', @hash, N'Trần Đình Điều Phối Nghệ An', '0902020002', 'coord.na@cuunan247.vn', @coordRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'coord_hatinh')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('coord_hatinh', @hash, N'Lê Văn Điều Phối Hà Tĩnh', '0902030003', 'coord.ht@cuunan247.vn', @coordRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'coord_quangtri')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('coord_quangtri', @hash, N'Phạm Văn Điều Phối Quảng Trị', '0902040004', 'coord.qt@cuunan247.vn', @coordRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'coord_hue')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('coord_hue', @hash, N'Hoàng Văn Điều Phối Huế', '0902050005', 'coord.hue@cuunan247.vn', @coordRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'coord_danang')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('coord_danang', @hash, N'Ngô Quốc Điều Phối Đà Nẵng', '0902060006', 'coord.dn@cuunan247.vn', @coordRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'coord_quangngai')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('coord_quangngai', @hash, N'Võ Văn Điều Phối Quảng Ngãi', '0902070007', 'coord.qn@cuunan247.vn', @coordRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'coord_gialai')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('coord_gialai', @hash, N'Đỗ Văn Điều Phối Gia Lai', '0902080008', 'coord.gl@cuunan247.vn', @coordRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'coord_daklak')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('coord_daklak', @hash, N'Bùi Văn Điều Phối Đắk Lắk', '0902090009', 'coord.dl@cuunan247.vn', @coordRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'coord_khanhhoa')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('coord_khanhhoa', @hash, N'Đinh Văn Điều Phối Khánh Hòa', '0902100010', 'coord.kh@cuunan247.vn', @coordRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'coord_lamdong')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('coord_lamdong', @hash, N'Chu Văn Điều Phối Lâm Đồng', '0902110011', 'coord.ld@cuunan247.vn', @coordRoleId, 'ACTIVE');

-- 2.2. Đội trưởng cứu hộ (ROLE_RESCUER)
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'rescuer')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('rescuer', @hash, N'Đặng Văn Cứu Hộ (Hải Châu)', '0900000003', 'rescuer@cuunan247.vn', @rescuerRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'cuuho1')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('cuuho1', @hash, N'Nguyễn Văn Sơn Trà (Sơn Trà)', '0905556677', 'cuuho1@cuunan247.vn', @rescuerRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'rescuer_th01')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('rescuer_th01', @hash, N'Lê Văn Thành (Thanh Hóa 01)', '0911001001', 'rescuer.th01@cuunan247.vn', @rescuerRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'rescuer_na01')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('rescuer_na01', @hash, N'Nguyễn Văn Nghĩa (Nghệ An 01)', '0912002001', 'rescuer.na01@cuunan247.vn', @rescuerRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'rescuer_ht01')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('rescuer_ht01', @hash, N'Phạm Đình Tĩnh (Hà Tĩnh 01)', '0913003001', 'rescuer.ht01@cuunan247.vn', @rescuerRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'rescuer_qt01')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('rescuer_qt01', @hash, N'Hoàng Văn Trị (Quảng Trị 01)', '0914004001', 'rescuer.qt01@cuunan247.vn', @rescuerRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'rescuer_hue01')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('rescuer_hue01', @hash, N'Ngô Văn Huệ (Huế 01)', '0915005001', 'rescuer.hue01@cuunan247.vn', @rescuerRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'rescuer_qn01')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('rescuer_qn01', @hash, N'Đinh Công Ngãi (Quảng Ngãi 01)', '0917007001', 'rescuer.qn01@cuunan247.vn', @rescuerRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'rescuer_gl01')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('rescuer_gl01', @hash, N'Ksor Y Lai (Gia Lai 01)', '0918008001', 'rescuer.gl01@cuunan247.vn', @rescuerRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'rescuer_dl01')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('rescuer_dl01', @hash, N'Y Krin Niê (Đắk Lắk 01)', '0919009001', 'rescuer.dl01@cuunan247.vn', @rescuerRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'rescuer_kh01')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('rescuer_kh01', @hash, N'Trần Văn Hòa (Khánh Hòa 01)', '0920010001', 'rescuer.kh01@cuunan247.vn', @rescuerRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'rescuer_ld01')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('rescuer_ld01', @hash, N'Cil Ha Đồng (Lâm Đồng 01)', '0921011001', 'rescuer.ld01@cuunan247.vn', @rescuerRoleId, 'ACTIVE');

-- 2.3. Người dân (ROLE_CITIZEN)
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('citizen', @hash, N'Nguyễn Văn Dân', '0900000004', 'dan@cuunan247.vn', @citizenRoleId, 'ACTIVE');

PRINT N'>>> Đã nạp tài khoản mẫu cho các vai trò!';
GO

-- 3. Nạp Đội cứu hộ mẫu (Rescue Teams)
DECLARE @leaderDN1 BIGINT = (SELECT [id] FROM [dbo].[users] WHERE [username] = 'rescuer');
DECLARE @leaderTH1 BIGINT = (SELECT [id] FROM [dbo].[users] WHERE [username] = 'rescuer_th01');
DECLARE @leaderNA1 BIGINT = (SELECT [id] FROM [dbo].[users] WHERE [username] = 'rescuer_na01');
DECLARE @leaderHT1 BIGINT = (SELECT [id] FROM [dbo].[users] WHERE [username] = 'rescuer_ht01');
DECLARE @leaderHue1 BIGINT = (SELECT [id] FROM [dbo].[users] WHERE [username] = 'rescuer_hue01');

IF NOT EXISTS (SELECT 1 FROM [dbo].[rescue_teams] WHERE [team_name] LIKE N'%Hải Châu%')
    INSERT INTO [dbo].[rescue_teams] ([team_name], [leader_id], [contact_phone], [current_latitude], [current_longitude], [member_count], [equipment_notes], [status])
    VALUES (N'Đội Cứu Hộ Hải Châu (Xuồng máy)', @leaderDN1, '0900000003', 16.0678, 108.2208, 6, N'2 xuồng cao su gắn máy, 15 áo phao, túi sơ cứu y tế', 'AVAILABLE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[rescue_teams] WHERE [team_name] LIKE N'%Thanh Hóa 01%')
    INSERT INTO [dbo].[rescue_teams] ([team_name], [leader_id], [contact_phone], [current_latitude], [current_longitude], [member_count], [equipment_notes], [status])
    VALUES (N'Đội Cứu Hộ Thanh Hóa 01 (Sông Mã)', @leaderTH1, '0911001001', 19.8074, 105.7764, 8, N'2 xuồng máy, 20 áo phao, máy bơm cứu hộ', 'AVAILABLE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[rescue_teams] WHERE [team_name] LIKE N'%Huế 01%')
    INSERT INTO [dbo].[rescue_teams] ([team_name], [leader_id], [contact_phone], [current_latitude], [current_longitude], [member_count], [equipment_notes], [status])
    VALUES (N'Đội Cứu Hộ Huế 01 (Sông Hương & Tam Giang)', @leaderHue1, '0915005001', 16.4637, 107.5909, 9, N'3 canô cứu nạn cao tốc, bình oxy y tế', 'BUSY');

PRINT N'>>> Đã nạp Đội cứu hộ mẫu!';
GO

-- 4. Nạp Yêu cầu cứu nạn SOS mẫu (SOS Requests)
IF NOT EXISTS (SELECT 1 FROM [dbo].[sos_requests] WHERE [contact_phone] = '0912345678')
    INSERT INTO [dbo].[sos_requests] ([contact_name], [contact_phone], [latitude], [longitude], [address_detail], [urgency_level], [people_count], [has_elderly], [has_children], [has_injured], [description], [status])
    VALUES (N'Trần Thị Bé', '0912345678', 16.0594, 108.1585, N'Số 45 đường Mẹ Suốt, P. Hòa Khánh Nam, Liên Chiểu, Đà Nẵng', 'CRITICAL', 4, 1, 1, 0, N'Nước ngập ngang ngực, có cụ già 80 tuổi và bé 3 tuổi, cần xuồng gấp!', 'IN_PROGRESS');

IF NOT EXISTS (SELECT 1 FROM [dbo].[sos_requests] WHERE [contact_phone] = '0988776655')
    INSERT INTO [dbo].[sos_requests] ([contact_name], [contact_phone], [latitude], [longitude], [address_detail], [urgency_level], [people_count], [has_elderly], [has_children], [has_injured], [description], [status])
    VALUES (N'Lê Văn Hùng', '0988776655', 15.9982, 108.1965, N'Gần cầu Đỏ, Xã Hòa Châu, Hòa Vang, Đà Nẵng', 'HIGH', 3, 0, 0, 1, N'Bị cô lập do lũ cắt đứt đường, có 1 người bị thương ở chân.', 'DISPATCHED');

PRINT N'>>> Đã nạp dữ liệu SOS mẫu!';
GO

-- 5. Nạp Cảnh báo thiên tai mẫu (Disaster Alerts)
DECLARE @adminId BIGINT = (SELECT [id] FROM [dbo].[users] WHERE [username] = 'admin');

IF NOT EXISTS (SELECT 1 FROM [dbo].[disaster_alerts] WHERE [title] LIKE N'%Lũ khẩn cấp%')
    INSERT INTO [dbo].[disaster_alerts] ([title], [content], [severity], [affected_region], [is_broadcast], [created_by])
    VALUES (N'Lũ khẩn cấp trên các lưu vực sông Miền Trung', N'Do ảnh hưởng của mưa lũ, mực nước các sông Hương, Thu Bồn, sông Mã dâng cao trên báo động 3.', 'DANGER_RED', N'Thanh Hóa, Nghệ An, Hà Tĩnh, Thừa Thiên Huế, Đà Nẵng, Quảng Ngãi', 1, @adminId);

PRINT N'>>> Đã nạp Cảnh báo thiên tai mẫu!';
GO
