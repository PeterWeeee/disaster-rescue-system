-- =========================================================================
-- ĐỒ ÁN LẬP TRÌNH WEB - NHÓM 04
-- DỰ ÁN: HỆ THỐNG CỨU NẠN THIÊN TAI (CỨUNẠN 24/7)
-- FILE: 03_seed_data.sql
-- MỤC ĐÍCH: NẠP DỮ LIỆU MẪU BAN ĐẦU (ROLES, USERS, TEAMS, SOS, ALERTS)
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

-- 2. Nạp Người dùng mẫu (Mật khẩu mặc định: 123456 -> BCrypt hash: $2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa)
DECLARE @adminRoleId INT = (SELECT [id] FROM [dbo].[roles] WHERE [name] = 'ROLE_ADMIN');
DECLARE @coordRoleId INT = (SELECT [id] FROM [dbo].[roles] WHERE [name] = 'ROLE_COORDINATOR');
DECLARE @rescuerRoleId INT = (SELECT [id] FROM [dbo].[roles] WHERE [name] = 'ROLE_RESCUER');
DECLARE @citizenRoleId INT = (SELECT [id] FROM [dbo].[roles] WHERE [name] = 'ROLE_CITIZEN');

-- 2.1. Admin
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'admin')
BEGIN
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('admin', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', N'Quản Trị Viên Hệ Thống', '0901112233', 'admin@cuunan247.vn', @adminRoleId, 'ACTIVE');
END

-- 2.2. Điều phối viên (Coordinator)
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'dieuphoi01')
BEGIN
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('dieuphoi01', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', N'Trần Điều Phối', '0902223344', 'coordinator01@cuunan247.vn', @coordRoleId, 'ACTIVE');
END

-- 2.3. Đội trưởng cứu hộ (Rescuer Leader)
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'doitruong01')
BEGIN
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('doitruong01', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', N'Lê Văn Cứu Hộ', '0903334455', 'rescuer01@cuunan247.vn', @rescuerRoleId, 'ACTIVE');
END

-- 2.4. Người dân (Citizen)
IF NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE [username] = 'nguoidan01')
BEGIN
    INSERT INTO [dbo].[users] ([username], [password], [full_name], [phone], [email], [role_id], [status])
    VALUES ('nguoidan01', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', N'Nguyễn Văn An', '0904445566', 'dan01@gmail.com', @citizenRoleId, 'ACTIVE');
END
PRINT N'>>> Đã nạp tài khoản mẫu cho 4 vai trò!';
GO

-- 3. Nạp Đội cứu hộ mẫu (Rescue Teams)
DECLARE @leaderId BIGINT = (SELECT [id] FROM [dbo].[users] WHERE [username] = 'doitruong01');

IF NOT EXISTS (SELECT 1 FROM [dbo].[rescue_teams] WHERE [team_name] = N'Đội Cứu Nạn Xuồng Cao Su Số 1')
BEGIN
    INSERT INTO [dbo].[rescue_teams] ([team_name], [leader_id], [contact_phone], [current_latitude], [current_longitude], [member_count], [equipment_notes], [status])
    VALUES (N'Đội Cứu Nạn Xuồng Cao Su Số 1', @leaderId, '0903334455', 16.0748, 108.2240, 6, N'2 xuồng máy cao su, 15 áo phao cứu sinh, 2 phao tròn, túi cứu thương chuyên sâu', 'AVAILABLE');
END
PRINT N'>>> Đã nạp Đội cứu hộ mẫu!';
GO

-- 4. Nạp Yêu cầu cứu nạn SOS mẫu (SOS Requests)
IF NOT EXISTS (SELECT 1 FROM [dbo].[sos_requests] WHERE [contact_phone] = '0988776655')
BEGIN
    INSERT INTO [dbo].[sos_requests] (
        [contact_name], [contact_phone], [latitude], [longitude], [address_detail],
        [urgency_level], [people_count], [has_elderly], [has_children], [has_injured],
        [description], [status]
    )
    VALUES (
        N'Bà Trần Thị Bích', '0988776655', 16.0678, 108.2140, N'Số 45 Đường Trưng Nữ Vương, ngập nước ngang ngực',
        'CRITICAL', 4, 1, 1, 0,
        N'Nước lũ đang dâng cao gần tới gác lửng, nhà có 1 cụ già 82 tuổi liệt giường và bé sơ sinh 6 tháng tuổi. Cần xuồng tiếp cận gấp!',
        'PENDING'
    );
END

IF NOT EXISTS (SELECT 1 FROM [dbo].[sos_requests] WHERE [contact_phone] = '0977665544')
BEGIN
    INSERT INTO [dbo].[sos_requests] (
        [contact_name], [contact_phone], [latitude], [longitude], [address_detail],
        [urgency_level], [people_count], [has_elderly], [has_children], [has_injured],
        [description], [status]
    )
    VALUES (
        N'Nguyễn Hoàng Nam', '0977665544', 16.0544, 108.2022, N'Khu dân cư ven sông Cẩm Lệ',
        'HIGH', 3, 0, 0, 1,
        N'Đang bị cô lập do ngập sâu 1.5m, một người bị ngã trẹo chân khi dọn đồ cần băng nẹp y tế và lương thực.',
        'PENDING'
    );
END
PRINT N'>>> Đã nạp dữ liệu SOS mẫu!';
GO

-- 5. Nạp Cảnh báo thiên tai mẫu (Disaster Alerts)
DECLARE @adminId BIGINT = (SELECT [id] FROM [dbo].[users] WHERE [username] = 'admin');

IF NOT EXISTS (SELECT 1 FROM [dbo].[disaster_alerts] WHERE [title] LIKE N'%Lũ khẩn cấp%')
BEGIN
    INSERT INTO [dbo].[disaster_alerts] ([title], [content], [severity], [affected_region], [is_broadcast], [created_by])
    VALUES (
        N'Lũ khẩn cấp trên các lưu vực sông',
        N'Do ảnh hưởng của bão số 6, mực nước sông lên nhanh đạt mức báo động 3. Người dân các khu vực thấp trũng khẩn trương di dời tài sản và liên hệ lực lượng ứng trực qua hotline 112.',
        'DANGER_RED',
        N'Thành phố Đà Nẵng, Tỉnh Quảng Nam, Tỉnh Thừa Thiên Huế',
        1,
        @adminId
    );
END
PRINT N'>>> Đã nạp Cảnh báo thiên tai mẫu!';
GO
