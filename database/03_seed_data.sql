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
UPDATE [dbo].[users]
SET [role_id] = @coordRoleId, [status] = 'ACTIVE'
WHERE [username] = 'coordinator';
-- Chỉ giữ một tài khoản điều phối viên; khóa tài khoản mẫu cũ nhưng bảo toàn dữ liệu lịch sử.
UPDATE [dbo].[users]
SET [role_id] = @citizenRoleId, [status] = 'BLOCKED'
WHERE [username] <> 'coordinator' AND [role_id] = @coordRoleId;
-- 2.2. Đội trưởng cứu hộ (ROLE_RESCUER)
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'rescuer')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('rescuer', @hash, N'Nguyễn Văn Hùng (Hải Châu)', '0900000003', 'rescuer@cuunan247.vn', @rescuerRoleId, 'ACTIVE');

IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'cuuho1')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('cuuho1', @hash, N'Nguyễn Văn Sơn (Sơn Trà)', '0905556677', 'cuuho1@cuunan247.vn', @rescuerRoleId, 'ACTIVE');

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
    VALUES ('rescuer_qn01', @hash, N'Lê Văn Minh (Quảng Ngãi 01)', '0917007001', 'rescuer.qn01@cuunan247.vn', @rescuerRoleId, 'ACTIVE');

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
    VALUES ('rescuer_ld01', @hash, N'Trần Văn Dũng (Lâm Đồng 01)', '0921011001', 'rescuer.ld01@cuunan247.vn', @rescuerRoleId, 'ACTIVE');

-- Chuẩn hóa username người dân theo dạng citizen_<mã tỉnh>, giữ nguyên ID và dữ liệu liên quan.
IF EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen' AND [phone] = '0900000004') AND NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_dn01') UPDATE [dbo].[users] SET [username] = 'citizen_dn01' WHERE [username] = 'citizen';
IF EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'nguoidan01') AND NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_dn01') UPDATE [dbo].[users] SET [username] = 'citizen_dn01' WHERE [username] = 'nguoidan01';
IF EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'nguoidan1') AND NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_dn02') UPDATE [dbo].[users] SET [username] = 'citizen_dn02' WHERE [username] = 'nguoidan1';
IF EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'nguoidan02') AND NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_dn02') UPDATE [dbo].[users] SET [username] = 'citizen_dn02' WHERE [username] = 'nguoidan02';
IF EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'nguoidan03') AND NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_th') UPDATE [dbo].[users] SET [username] = 'citizen_th' WHERE [username] = 'nguoidan03';
IF EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'nguoidan04') AND NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_na') UPDATE [dbo].[users] SET [username] = 'citizen_na' WHERE [username] = 'nguoidan04';
IF EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'nguoidan05') AND NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_ht') UPDATE [dbo].[users] SET [username] = 'citizen_ht' WHERE [username] = 'nguoidan05';
IF EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'nguoidan06') AND NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_qt') UPDATE [dbo].[users] SET [username] = 'citizen_qt' WHERE [username] = 'nguoidan06';
IF EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'nguoidan07') AND NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_hue') UPDATE [dbo].[users] SET [username] = 'citizen_hue' WHERE [username] = 'nguoidan07';
IF EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'nguoidan08') AND NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_qn') UPDATE [dbo].[users] SET [username] = 'citizen_qn' WHERE [username] = 'nguoidan08';
IF EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'nguoidan09') AND NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_gl') UPDATE [dbo].[users] SET [username] = 'citizen_gl' WHERE [username] = 'nguoidan09';
IF EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'nguoidan10') AND NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_dl') UPDATE [dbo].[users] SET [username] = 'citizen_dl' WHERE [username] = 'nguoidan10';
IF EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'nguoidan11') AND NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_kh') UPDATE [dbo].[users] SET [username] = 'citizen_kh' WHERE [username] = 'nguoidan11';
IF EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'nguoidan12') AND NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_ld') UPDATE [dbo].[users] SET [username] = 'citizen_ld' WHERE [username] = 'nguoidan12';
-- 2.3. Người dân (ROLE_CITIZEN)
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_dn01')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('citizen_dn01', @hash, N'Nguyễn Văn An', '0900000004', 'dan@cuunan247.vn', @citizenRoleId, 'ACTIVE');
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('citizen', @hash, N'Nguyễn Hoàng Nam', '0900000015', 'citizen@cuunan247.vn', @citizenRoleId, 'ACTIVE');
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_dn02')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('citizen_dn02', @hash, N'Trần Thị Mai', '0900000014', 'nguoidan1@cuunan247.vn', @citizenRoleId, 'ACTIVE');
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_th')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('citizen_th', @hash, N'Nguyễn Thị Lan', '0901000101', 'citizen.th@cuunan247.vn', @citizenRoleId, 'ACTIVE');
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_na')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('citizen_na', @hash, N'Lê Văn Hùng', '0901000102', 'citizen.na@cuunan247.vn', @citizenRoleId, 'ACTIVE');
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_ht')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('citizen_ht', @hash, N'Phạm Văn Nam', '0901000103', 'citizen.ht@cuunan247.vn', @citizenRoleId, 'ACTIVE');
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_qt')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('citizen_qt', @hash, N'Hoàng Thị Hương', '0901000104', 'citizen.qt@cuunan247.vn', @citizenRoleId, 'ACTIVE');
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_hue')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('citizen_hue', @hash, N'Trần Văn Minh', '0901000105', 'citizen.hue@cuunan247.vn', @citizenRoleId, 'ACTIVE');
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_qn')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('citizen_qn', @hash, N'Nguyễn Thị Hoa', '0901000106', 'citizen.qn@cuunan247.vn', @citizenRoleId, 'ACTIVE');
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_gl')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('citizen_gl', @hash, N'Võ Văn Bình', '0901000107', 'citizen.gl@cuunan247.vn', @citizenRoleId, 'ACTIVE');
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_dl')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('citizen_dl', @hash, N'Nguyễn Thị Hạnh', '0901000108', 'citizen.dl@cuunan247.vn', @citizenRoleId, 'ACTIVE');
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_kh')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('citizen_kh', @hash, N'Lê Thị Thảo', '0901000109', 'citizen.kh@cuunan247.vn', @citizenRoleId, 'ACTIVE');
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'citizen_ld')
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('citizen_ld', @hash, N'Phạm Văn Long', '0901000110', 'citizen.ld@cuunan247.vn', @citizenRoleId, 'ACTIVE');

UPDATE [dbo].[users] SET [full_name] = N'Nguyễn Văn Hùng (Hải Châu)' WHERE [username] = 'rescuer';
UPDATE [dbo].[users] SET [full_name] = N'Trần Thị Hương (Quảng Trị 02)' WHERE [username] = 'rescuer_qt02';
UPDATE [dbo].[users] SET [full_name] = N'Nguyễn Văn Sơn (Sơn Trà)' WHERE [username] = 'cuuho1';
UPDATE [dbo].[users] SET [full_name] = N'Lê Văn Minh (Quảng Ngãi 01)' WHERE [username] = 'rescuer_qn01';
UPDATE [dbo].[users] SET [full_name] = N'Trần Văn Dũng (Lâm Đồng 01)' WHERE [username] = 'rescuer_ld01';
UPDATE [dbo].[users] SET [full_name] = N'Nguyễn Văn An' WHERE [username] = 'citizen_dn01';
UPDATE [dbo].[users] SET [full_name] = N'Nguyễn Hoàng Nam', [phone] = '0900000015', [email] = 'citizen@cuunan247.vn', [role_id] = @citizenRoleId, [status] = 'ACTIVE' WHERE [username] = 'citizen';
UPDATE [dbo].[users] SET [full_name] = N'Trần Thị Mai' WHERE [username] = 'citizen_dn02';
UPDATE [dbo].[users] SET [full_name] = N'Nguyễn Thị Lan' WHERE [username] = 'citizen_th';
UPDATE [dbo].[users] SET [full_name] = N'Lê Văn Hùng' WHERE [username] = 'citizen_na';
UPDATE [dbo].[users] SET [full_name] = N'Phạm Văn Nam' WHERE [username] = 'citizen_ht';
UPDATE [dbo].[users] SET [full_name] = N'Hoàng Thị Hương' WHERE [username] = 'citizen_qt';
UPDATE [dbo].[users] SET [full_name] = N'Trần Văn Minh' WHERE [username] = 'citizen_hue';
UPDATE [dbo].[users] SET [full_name] = N'Nguyễn Thị Hoa' WHERE [username] = 'citizen_qn';
UPDATE [dbo].[users] SET [full_name] = N'Võ Văn Bình' WHERE [username] = 'citizen_gl';
UPDATE [dbo].[users] SET [full_name] = N'Nguyễn Thị Hạnh' WHERE [username] = 'citizen_dl';
UPDATE [dbo].[users] SET [full_name] = N'Lê Thị Thảo' WHERE [username] = 'citizen_kh';
UPDATE [dbo].[users] SET [full_name] = N'Phạm Văn Long' WHERE [username] = 'citizen_ld';
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

-- 6. Nạp Bài đăng cảnh báo cộng đồng mẫu (Community Posts) kèm ảnh Cloudinary CDN (10 bài ảnh thực tế miền Trung)
IF NOT EXISTS (SELECT 1 FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Trà Mai%')
    INSERT INTO [dbo].[community_posts] ([author_name], [hazard_type], [content], [location_name], [latitude], [longitude], [image_url], [verification_status], [upvote_count], [comment_count])
    VALUES (N'Trần Thị Thu', 'LANDSLIDE', N'Vết nứt lớn xuất hiện trên sườn đồi phía sau trường tiểu học, đất bùn đã tràn ra đường đèo. Mọi người tránh khu vực này.', N'Xã Trà Mai, Quảng Nam', 15.2841, 108.0821, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328584/cuunan247/community_feed/V%E1%BA%BFt_n%E1%BB%A9t_l%E1%BB%9Bn_xu%E1%BA%A5t_hi%E1%BB%87n_tr%C3%AAn_s%C6%B0%E1%BB%9Dn_%C4%91%E1%BB%93i_ph%C3%ADa_sau_tr%C6%B0%E1%BB%9Dng_ti%E1%BB%83u_h%E1%BB%8Dc_%C4%91%E1%BA%A5t_b%C3%B9n_%C4%91%C3%A3_tr%C3%A0n_ra_%C4%91%C6%B0%E1%BB%9Dng_%C4%91%C3%A8o.webp', 'VERIFIED', 7, 3);

IF NOT EXISTS (SELECT 1 FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Cẩm Phô%')
    INSERT INTO [dbo].[community_posts] ([author_name], [hazard_type], [content], [location_name], [latitude], [longitude], [image_url], [verification_status], [upvote_count], [comment_count])
    VALUES (N'Lê Minh Hoàng', 'FLOOD', N'Nước sông dâng nhanh, đã ngập tầng 1 dãy nhà ven sông. Có 3 người già chưa di chuyển được, cần xuồng gấp.', N'Phường Cẩm Phô, Hội An', 15.8782, 108.3243, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328592/cuunan247/community_feed/N%C6%B0%E1%BB%9Bc_s%C3%B4ng_d%C3%A2ng_nhanh_%C4%91%C3%A3_ng%E1%BA%ADp_t%E1%BA%A7ng_1_d%C3%A3y_nh%C3%A0_ven_s%C3%B4ng.jpg', 'PENDING', 8, 2);

IF NOT EXISTS (SELECT 1 FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Mẹ Suốt%')
    INSERT INTO [dbo].[community_posts] ([author_name], [hazard_type], [content], [location_name], [latitude], [longitude], [image_url], [verification_status], [upvote_count], [comment_count])
    VALUES (N'Nguyễn Văn An', 'FLOOD', N'Đường Mẹ Suốt đoạn gần cầu mương ngập sâu hơn 1m, nước chảy rất xiết, bà con tuyệt đối không nên di chuyển bằng xe máy!', N'Đường Mẹ Suốt, P. Hòa Khánh Nam, Liên Chiểu, Đà Nẵng', 16.0590, 108.1580, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328442/cuunan247/community_feed/%C4%90%C6%B0%E1%BB%9Dng_M%E1%BA%B9_Su%E1%BB%91t_%C4%91o%E1%BA%A1n_g%E1%BA%A7n_c%E1%BA%A7u_m%C6%B0%C6%A1ng_ng%E1%BA%ADp_s%C3%A2u_h%C6%A1n_1m_n%C6%B0%E1%BB%9Bc_ch%E1%BA%A3y_r%E1%BA%A5t_xi%E1%BA%BFt.jpg', 'VERIFIED', 28, 6);

IF NOT EXISTS (SELECT 1 FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Thạch Nham%')
    INSERT INTO [dbo].[community_posts] ([author_name], [hazard_type], [content], [location_name], [latitude], [longitude], [image_url], [verification_status], [upvote_count], [comment_count])
    VALUES (N'Trần Thị Mai', 'ISOLATED', N'Cầu tràn thôn Thạch Nham Tây xã Hòa Nhơn đang bị ngập 0.8m, cô lập 120 hộ dân, cần xuồng tiếp tế mì và nước uống.', N'Thôn Thạch Nham Tây, Xã Hòa Nhơn, Hòa Vang, Đà Nẵng', 16.0120, 108.1350, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328564/cuunan247/community_feed/C%E1%BA%A7u_tr%C3%A0n_th%C3%B4n_Th%E1%BA%A1ch_Nham_T%C3%A2y_x%C3%A3_H%C3%B2a_Nh%C6%A1n_%C4%91ang_b%E1%BB%8B_ng%E1%BA%ADp_0.8m.jpg', 'VERIFIED', 19, 4);

IF NOT EXISTS (SELECT 1 FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Bạch Đằng%')
    INSERT INTO [dbo].[community_posts] ([author_name], [hazard_type], [content], [location_name], [latitude], [longitude], [image_url], [verification_status], [upvote_count], [comment_count])
    VALUES (N'Trần Văn Minh', 'FLOOD', N'Đường Bạch Đằng ven sông Hương nước tràn lên bờ đi vào nhà dân sâu 0.5m, đề nghị bà con dọn đồ lên gác lửng.', N'Đường Bạch Đằng, Phường Phú Cát, TP. Huế', 16.4720, 107.5920, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328566/cuunan247/community_feed/%C4%90%C6%B0%E1%BB%9Dng_B%E1%BA%A1ch_%C4%90%E1%BA%B1ng_ven_s%C3%B4ng_H%C6%B0%C6%A1ng_n%C6%B0%E1%BB%9Bc_tr%C3%A0n_l%C3%AAn_b%E1%BB%9D_%C4%91i_v%C3%A0o_nh%C3%A0_d%C3%A2n_s%C3%A2u_0.5m.jpg', 'VERIFIED', 35, 12);

IF NOT EXISTS (SELECT 1 FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Prenn%')
    INSERT INTO [dbo].[community_posts] ([author_name], [hazard_type], [content], [location_name], [latitude], [longitude], [image_url], [verification_status], [upvote_count], [comment_count])
    VALUES (N'Phạm Văn Long', 'LANDSLIDE', N'Đèo Prenn đoạn Km225 xuất hiện vết nứt sườn taluy dương, có đất đá nhỏ rơi xuống mặt đường, các bác tài chú ý giảm tốc độ.', N'Đèo Prenn, Phường 3, TP. Đà Lạt, Lâm Đồng', 11.9150, 108.4450, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328569/cuunan247/community_feed/%C4%90%C3%A8o_Prenn_%C4%91o%E1%BA%A1n_Km225_xu%E1%BA%A5t_hi%E1%BB%87n_v%E1%BA%BFt_n%E1%BB%A9t_s%C6%B0%E1%BB%9Dn_taluy_d%C6%B0%C6%A1ng_c%C3%B3_%C4%91%E1%BA%A5t_%C4%91%C3%A1_nh%E1%BB%8F_r%C6%A1i_xu%E1%BB%91ng_m%E1%BA%B7t_%C4%91%C6%B0%E1%BB%9Dng.jpg', 'PENDING', 14, 2);

IF NOT EXISTS (SELECT 1 FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Nguyễn Du%')
    INSERT INTO [dbo].[community_posts] ([author_name], [hazard_type], [content], [location_name], [latitude], [longitude], [image_url], [verification_status], [upvote_count], [comment_count])
    VALUES (N'Lê Văn Hùng', 'TREE_FALL', N'Cây phượng cổ thụ bị lốc xoáy quật đổ đè trúng cột điện hạ thế tại đường Nguyễn Du, gây mất điện toàn khu phố.', N'Đường Nguyễn Du, Phường Bến Thủy, TP. Vinh, Nghệ An', 18.6650, 105.6950, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328571/cuunan247/community_feed/C%C3%A2y_ph%C6%B0%E1%BB%A3ng_c%E1%BB%95_th%E1%BB%A5_b%E1%BB%8B_l%E1%BB%91c_xo%C3%A1y_qu%E1%BA%ADt_%C4%91%E1%BB%95_%C4%91%C3%A8_tr%C3%BAng_c%E1%BB%99t_%C4%91i%E1%BB%87n_h%E1%BA%A1_th%E1%BA%BF_t%E1%BA%A1i_%C4%91%C6%B0%E1%BB%9Dng_Nguy%E1%BB%85n_Du.jpg', 'VERIFIED', 22, 5);

IF NOT EXISTS (SELECT 1 FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Vĩnh Hải%' OR [location_name] LIKE N'%Nha Trang%')
    INSERT INTO [dbo].[community_posts] ([author_name], [hazard_type], [content], [location_name], [latitude], [longitude], [image_url], [verification_status], [upvote_count], [comment_count])
    VALUES (N'Lê Thị Thảo', 'STORM', N'Gió rít mạnh từng cơn giật cấp 9 tại ven biển Vĩnh Hải, sóng cao đánh tràn qua kè công viên, người dân không nên ra bờ biển.', N'Bờ kè Phạm Văn Đồng, Phường Vĩnh Hải, TP. Nha Trang, Khánh Hòa', 12.2850, 109.2050, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328573/cuunan247/community_feed/Gi%C3%B3_r%C3%ADt_m%E1%BA%A1nh_t%E1%BB%ABng_c%C6%A1n_gi%E1%BA%ADt_c%E1%BA%A5p_9_t%E1%BA%A1i_ven_bi%E1%BB%83n_V%C4%A9nh_H%E1%BA%A3i.jpg', 'PENDING', 11, 1);

IF NOT EXISTS (SELECT 1 FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Krông Pa%')
    INSERT INTO [dbo].[community_posts] ([author_name], [hazard_type], [content], [location_name], [latitude], [longitude], [image_url], [verification_status], [upvote_count], [comment_count])
    VALUES (N'Võ Văn Bình', 'OTHER', N'Tin đồn vỡ đập thủy điện trên sông Ba là tin giả mạo, chính quyền địa phương thông báo đập vẫn vận hành xả lũ an toàn.', N'Huyện Krông Pa, Tỉnh Gia Lai', 13.2100, 108.6800, NULL, 'REJECTED', 3, 8);

IF NOT EXISTS (SELECT 1 FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Duy Vinh%' OR [location_name] LIKE N'%An Lạc%')
    INSERT INTO [dbo].[community_posts] ([author_name], [hazard_type], [content], [location_name], [latitude], [longitude], [image_url], [verification_status], [upvote_count], [comment_count])
    VALUES (N'Đội Cứu Hộ Tình Nguyện Miền Trung', 'FLOOD', N'Đội đã điều động 2 xuồng máy cao su tiếp cận khu vực ngập sâu tại thôn An Lạc, đã đưa được 5 hộ dân và người già đến nhà văn hóa an toàn.', N'Thôn An Lạc, Xã Duy Vinh, Duy Xuyên, Quảng Nam', 15.8450, 108.3050, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328594/cuunan247/community_feed/Xu%E1%BB%93ng_cao_su_c%E1%BB%A9u_h%E1%BB%99_ti%E1%BA%BFp_c%E1%BA%ADn_v%C3%B9ng_ng%E1%BA%ADp_s%C3%A2u.jpg', 'VERIFIED', 52, 3);

-- Xóa bỏ các bài đăng cũ thừa nếu còn sót lại từ bản seed cũ
DELETE FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Đèo Le%' OR [location_name] LIKE N'%Nông Sơn%' OR [location_name] LIKE N'%Quế Long%';

-- Cập nhật bổ sung ảnh chuẩn thực tế cho các bài viết hiện hữu
UPDATE [dbo].[community_posts]
SET [image_url] = 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328594/cuunan247/community_feed/Xu%E1%BB%93ng_cao_su_c%E1%BB%A9u_h%E1%BB%99_ti%E1%BA%BFp_c%E1%BA%ADn_v%C3%B9ng_ng%E1%BA%ADp_s%C3%A2u.jpg'
WHERE [location_name] LIKE N'%An Lạc%' OR [location_name] LIKE N'%Duy Vinh%';

-- Cập nhật bổ sung ảnh cho các bài viết hiện hữu nếu đang thiếu
UPDATE [dbo].[community_posts]
SET [image_url] = 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328584/cuunan247/community_feed/V%E1%BA%BFt_n%E1%BB%A9t_l%E1%BB%9Bn_xu%E1%BA%A5t_hi%E1%BB%87n_tr%C3%AAn_s%C6%B0%E1%BB%9Dn_%C4%91%E1%BB%93i_ph%C3%ADa_sau_tr%C6%B0%E1%BB%9Dng_ti%E1%BB%83u_h%E1%BB%8Dc_%C4%91%E1%BA%A5t_b%C3%B9n_%C4%91%C3%A3_tr%C3%A0n_ra_%C4%91%C6%B0%E1%BB%9Dng_%C4%91%C3%A8o.webp'
WHERE ([image_url] IS NULL OR [image_url] = '') AND [hazard_type] = 'LANDSLIDE'
  AND [location_name] LIKE N'%Trà Mai%';

UPDATE [dbo].[community_posts]
SET [image_url] = 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328592/cuunan247/community_feed/N%C6%B0%E1%BB%9Bc_s%C3%B4ng_d%C3%A2ng_nhanh_%C4%91%C3%A3_ng%E1%BA%ADp_t%E1%BA%A7ng_1_d%C3%A3y_nh%C3%A0_ven_s%C3%B4ng.jpg'
WHERE ([image_url] IS NULL OR [image_url] = '') AND [hazard_type] = 'FLOOD'
  AND [location_name] LIKE N'%Cẩm Phô%';

PRINT N'>>> Đã nạp Bài đăng cảnh báo cộng đồng mẫu với ảnh Cloudinary CDN!';
GO

-- 7. Nạp Bình luận mẫu cho bài đăng cộng đồng (Post Comments) & Cảm xúc (Reactions)
DECLARE @p1Id BIGINT = (SELECT TOP 1 [id] FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Trà Mai%');
IF @p1Id IS NULL SET @p1Id = (SELECT TOP 1 [id] FROM [dbo].[community_posts] ORDER BY [id] ASC);

DECLARE @p2Id BIGINT = (SELECT TOP 1 [id] FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Cẩm Phô%');
IF @p2Id IS NULL SET @p2Id = (SELECT [id] FROM (SELECT [id], ROW_NUMBER() OVER(ORDER BY [id]) AS rn FROM [dbo].[community_posts]) t WHERE rn = 2);

DECLARE @p3Id BIGINT = (SELECT TOP 1 [id] FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Mẹ Suốt%');
IF @p3Id IS NULL SET @p3Id = (SELECT [id] FROM (SELECT [id], ROW_NUMBER() OVER(ORDER BY [id]) AS rn FROM [dbo].[community_posts]) t WHERE rn = 3);

DECLARE @p4Id BIGINT = (SELECT TOP 1 [id] FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Thạch Nham%');
IF @p4Id IS NULL SET @p4Id = (SELECT [id] FROM (SELECT [id], ROW_NUMBER() OVER(ORDER BY [id]) AS rn FROM [dbo].[community_posts]) t WHERE rn = 4);

DECLARE @p10Id BIGINT = (SELECT TOP 1 [id] FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%An Lạc%' OR [location_name] LIKE N'%Duy Vinh%');
IF @p10Id IS NULL SET @p10Id = (SELECT TOP 1 [id] FROM [dbo].[community_posts] ORDER BY [id] DESC);

-- 7.1. Bình luận mẫu cho Post 1 (Trà Mai)
IF @p1Id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM [dbo].[post_comments] WHERE [post_id] = @p1Id)
BEGIN
    INSERT INTO [dbo].[post_comments] ([post_id], [user_id], [author_name], [author_avatar], [content], [created_at]) VALUES
    (@p1Id, NULL, N'Nguyễn Văn Hùng', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=120&q=80', N'Bà con xóm 2 chú ý tuyệt đối không đi đường tắt qua khe suối nhé, đất đá trên đồi vẫn đang có dấu hiệu trượt tiếp!', DATEADD(MINUTE, -120, GETDATE())),
    (@p1Id, NULL, N'Trần Đình Điều Phối', 'https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&w=120&q=80', N'Đội dân quân xã và lực lượng công an đã chốt chặn hai đầu ngõ rồi ạ. Đề nghị bà con bình tĩnh chờ lệnh sơ tán.', DATEADD(MINUTE, -95, GETDATE())),
    (@p1Id, NULL, N'Lê Thị Mai', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=120&q=80', N'Cầu mong không có ai bị thương, nhìn vết nứt mà thót cả tim! Mọi người cố gắng giữ an toàn nhé.', DATEADD(MINUTE, -70, GETDATE()));

    UPDATE [dbo].[community_posts] SET [comment_count] = 3 WHERE [id] = @p1Id;
END

-- 7.2. Bình luận mẫu cho Post 2 (Cẩm Phô, Hội An)
IF @p2Id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM [dbo].[post_comments] WHERE [post_id] = @p2Id)
BEGIN
    INSERT INTO [dbo].[post_comments] ([post_id], [user_id], [author_name], [author_avatar], [content], [created_at]) VALUES
    (@p2Id, NULL, N'Võ Thị Mỹ Linh', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=120&q=80', N'Có gia đình bà Bảy ở số 28 Nguyễn Thị Minh Khai đang bị kẹt trên gác lửng, nước lên sát sàn rồi, đội ca nô ghé đón với ạ!', DATEADD(MINUTE, -110, GETDATE())),
    (@p2Id, NULL, N'Đội Cứu Hộ Đà Nẵng', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=120&q=80', N'Đội 02 đã tiếp nhận thông tin nhà bà Bảy, xuồng máy đang rẽ vào hẻm tiếp cận ngay đây!', DATEADD(MINUTE, -85, GETDATE()));

    UPDATE [dbo].[community_posts] SET [comment_count] = 2 WHERE [id] = @p2Id;
END

-- 7.3. Bình luận mẫu cho Post 3 (Đường Mẹ Suốt, Đà Nẵng)
IF @p3Id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM [dbo].[post_comments] WHERE [post_id] = @p3Id)
BEGIN
    INSERT INTO [dbo].[post_comments] ([post_id], [user_id], [author_name], [author_avatar], [content], [created_at]) VALUES
    (@p3Id, NULL, N'Phạm Văn Bình', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=120&q=80', N'Điện lực Liên Chiểu đã cắt điện trạm biến áp từ 15 phút trước rồi, bà con an tâm nhưng vẫn tránh xa dây chùng nhé.', DATEADD(MINUTE, -100, GETDATE())),
    (@p3Id, NULL, N'Nguyễn Thị Đào', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=120&q=80', N'Cảm ơn các bác thợ điện đã xử lý kịp thời, nước ngập sâu quá!', DATEADD(MINUTE, -75, GETDATE()));

    UPDATE [dbo].[community_posts] SET [comment_count] = 2 WHERE [id] = @p3Id;
END

-- 7.4. Bình luận mẫu cho Post 10 (Thôn An Lạc, Duy Vinh - Xuồng cao su)
IF @p10Id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM [dbo].[post_comments] WHERE [post_id] = @p10Id)
BEGIN
    INSERT INTO [dbo].[post_comments] ([post_id], [user_id], [author_name], [author_avatar], [content], [created_at]) VALUES
    (@p10Id, NULL, N'Trần Anh Tuấn', 'https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=120&q=80', N'Tuyệt vời quá các chiến sĩ ơi! Người nhà mình ở An Lạc vừa gọi điện báo đã được lên xuồng an toàn rồi.', DATEADD(MINUTE, -130, GETDATE())),
    (@p10Id, NULL, N'Lê Văn Thành', 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&w=120&q=80', N'Ấm lòng mùa bão lũ. Chúc toàn đội nhiều sức khỏe và an toàn trên từng chuyến xuồng!', DATEADD(MINUTE, -105, GETDATE())),
    (@p10Id, NULL, N'Bùi Thu Trang', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=120&q=80', N'Biết ơn các anh cứu hộ vô cùng, bà con nhớ mặc áo phao cẩn thận.', DATEADD(MINUTE, -80, GETDATE()));

    UPDATE [dbo].[community_posts] SET [comment_count] = 3 WHERE [id] = @p10Id;
END

-- 7.5. Nạp Phản ứng cảm xúc mẫu (Reactions / post_upvotes)
IF @p1Id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM [dbo].[post_upvotes] WHERE [post_id] = @p1Id)
    INSERT INTO [dbo].[post_upvotes] ([post_id], [user_id], [reaction_type], [created_at]) VALUES
    (@p1Id, 101, 'WARNING', GETDATE()),
    (@p1Id, 102, 'PRAY', GETDATE()),
    (@p1Id, 103, 'LIKE', GETDATE()),
    (@p1Id, 104, 'CARE', GETDATE()),
    (@p1Id, 105, 'SAD', GETDATE());

IF @p2Id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM [dbo].[post_upvotes] WHERE [post_id] = @p2Id)
    INSERT INTO [dbo].[post_upvotes] ([post_id], [user_id], [reaction_type], [created_at]) VALUES
    (@p2Id, 106, 'PRAY', GETDATE()),
    (@p2Id, 107, 'CARE', GETDATE()),
    (@p2Id, 108, 'LIKE', GETDATE()),
    (@p2Id, 109, 'WARNING', GETDATE());

IF @p3Id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM [dbo].[post_upvotes] WHERE [post_id] = @p3Id)
    INSERT INTO [dbo].[post_upvotes] ([post_id], [user_id], [reaction_type], [created_at]) VALUES
    (@p3Id, 110, 'WARNING', GETDATE()),
    (@p3Id, 111, 'LIKE', GETDATE()),
    (@p3Id, 112, 'CARE', GETDATE());

IF @p10Id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM [dbo].[post_upvotes] WHERE [post_id] = @p10Id)
    INSERT INTO [dbo].[post_upvotes] ([post_id], [user_id], [reaction_type], [created_at]) VALUES
    (@p10Id, 113, 'LIKE', GETDATE()),
    (@p10Id, 114, 'CARE', GETDATE()),
    (@p10Id, 115, 'PRAY', GETDATE()),
    (@p10Id, 116, 'LIKE', GETDATE());
    (@p6Id, 121, 'CARE', GETDATE()),
    (@p6Id, 122, 'WARNING', GETDATE());

-- Đồng bộ số lượng comment_count chính xác cho từng bài viết
UPDATE cp
SET cp.[comment_count] = ISNULL((SELECT COUNT(*) FROM [dbo].[post_comments] c WHERE c.[post_id] = cp.[id]), 0)
FROM [dbo].[community_posts] cp;

PRINT N'>>> Đã nạp Bình luận mẫu và Phản ứng icon cảm xúc cho các bài đăng cộng đồng!';
GO
