-- =========================================================================
-- ĐỒ ÁN LẬP TRÌNH WEB - NHÓM 04
-- DỰ ÁN: HỆ THỐNG CỨU NẠN THIÊN TAI (CỨUNẠN 24/7)
-- FILE: 02_create_tables.sql
-- MỤC ĐÍCH: TẠO CÁC BẢNG (TABLES), KHÓA CHÍNH, KHÓA NGOẠI VÀ CHỈ MỤC TRÊN SQL SERVER
-- =========================================================================

USE [cuunan_db];
GO

-- 1. Bảng Vai trò (roles)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = N'roles')
BEGIN
    CREATE TABLE [dbo].[roles] (
        [id] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [name] VARCHAR(50) NOT NULL UNIQUE
    );
    PRINT N'>>> Đã tạo bảng roles';
END
GO

-- 2. Bảng Người dùng (users)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = N'users')
BEGIN
    CREATE TABLE [dbo].[users] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [username] VARCHAR(100) NOT NULL UNIQUE,
        [password] VARCHAR(255) NOT NULL,
        [full_name] NVARCHAR(150) NOT NULL,
        [phone] VARCHAR(20) NOT NULL,
        [email] VARCHAR(100) NULL,
        [address] NVARCHAR(255) NULL,
        [avatar_url] VARCHAR(500) NULL,
        [role_id] INT NULL,
        [status] VARCHAR(30) DEFAULT 'ACTIVE',
        [created_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        [updated_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT [FK_users_roles] FOREIGN KEY ([role_id]) REFERENCES [dbo].[roles]([id]) ON DELETE SET NULL
    );
    PRINT N'>>> Đã tạo bảng users';
END
GO

-- 3. Bảng Đội cứu hộ (rescue_teams)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = N'rescue_teams')
BEGIN
    CREATE TABLE [dbo].[rescue_teams] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [team_name] NVARCHAR(150) NOT NULL,
        [leader_id] BIGINT NULL,
        [contact_phone] VARCHAR(20) NOT NULL,
        [current_latitude] FLOAT NULL,
        [current_longitude] FLOAT NULL,
        [member_count] INT DEFAULT 0,
        [equipment_notes] NVARCHAR(MAX) NULL,
        [status] VARCHAR(30) DEFAULT 'AVAILABLE',
        [created_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        [updated_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT [FK_rescue_teams_leader] FOREIGN KEY ([leader_id]) REFERENCES [dbo].[users]([id])
    );
    PRINT N'>>> Đã tạo bảng rescue_teams';
END
GO

-- 4. Bảng Thành viên đội cứu hộ (team_members)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = N'team_members')
BEGIN
    CREATE TABLE [dbo].[team_members] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [team_id] BIGINT NOT NULL,
        [user_id] BIGINT NOT NULL,
        [member_role] VARCHAR(50) DEFAULT 'RESCUER', -- LEADER, MEDIC, DRIVER, RESCUER
        [current_lat] FLOAT NULL,
        [current_lng] FLOAT NULL,
        [joined_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT [FK_team_members_team] FOREIGN KEY ([team_id]) REFERENCES [dbo].[rescue_teams]([id]) ON DELETE CASCADE,
        CONSTRAINT [FK_team_members_user] FOREIGN KEY ([user_id]) REFERENCES [dbo].[users]([id]) ON DELETE CASCADE
    );
    PRINT N'>>> Đã tạo bảng team_members';
END
GO

-- 5. Bảng Yêu cầu cứu nạn khẩn cấp (sos_requests)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = N'sos_requests')
BEGIN
    CREATE TABLE [dbo].[sos_requests] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [sender_id] BIGINT NULL,
        [contact_name] NVARCHAR(150) NOT NULL,
        [contact_phone] VARCHAR(20) NOT NULL,
        [secondary_phone] VARCHAR(20) NULL,
        [latitude] FLOAT NOT NULL,
        [longitude] FLOAT NOT NULL,
        [address_detail] NVARCHAR(255) NULL,
        [urgency_level] VARCHAR(30) NOT NULL, -- CRITICAL, HIGH, MEDIUM, LOW
        [people_count] INT NOT NULL DEFAULT 1,
        [has_elderly] BIT DEFAULT 0,
        [has_children] BIT DEFAULT 0,
        [has_injured] BIT DEFAULT 0,
        [description] NVARCHAR(MAX) NULL,
        [image_url] VARCHAR(500) NULL,
        [status] VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, DISPATCHED, IN_PROGRESS, RESOLVED, CANCELLED
        [created_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        [updated_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        [resolved_at] DATETIME2 NULL,
        CONSTRAINT [FK_sos_sender] FOREIGN KEY ([sender_id]) REFERENCES [dbo].[users]([id]) ON DELETE SET NULL
    );
    CREATE INDEX [IX_sos_status] ON [dbo].[sos_requests]([status]);
    CREATE INDEX [IX_sos_urgency] ON [dbo].[sos_requests]([urgency_level]);
    PRINT N'>>> Đã tạo bảng sos_requests';
END
GO

-- 6. Bảng Phân công nhiệm vụ cứu hộ (rescue_assignments)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = N'rescue_assignments')
BEGIN
    CREATE TABLE [dbo].[rescue_assignments] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [sos_request_id] BIGINT NOT NULL,
        [team_id] BIGINT NOT NULL,
        [assigned_by] BIGINT NULL,
        [status] VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, ACCEPTED, REJECTED, IN_PROGRESS, COMPLETED
        [rejection_reason] NVARCHAR(255) NULL,
        [assigned_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        [accepted_at] DATETIME2 NULL,
        [completed_at] DATETIME2 NULL,
        [report_notes] NVARCHAR(MAX) NULL,
        [report_image_url] VARCHAR(500) NULL,
        [vehicle_type] VARCHAR(50) NULL,
        [selected_equipment] NVARCHAR(MAX) NULL,
        [terrain_type] VARCHAR(50) NULL,
        [route_distance_km] FLOAT NULL,
        [route_duration_minutes] INT NULL,
        CONSTRAINT [FK_assign_sos] FOREIGN KEY ([sos_request_id]) REFERENCES [dbo].[sos_requests]([id]),
        CONSTRAINT [FK_assign_team] FOREIGN KEY ([team_id]) REFERENCES [dbo].[rescue_teams]([id]),
        CONSTRAINT [FK_assign_by] FOREIGN KEY ([assigned_by]) REFERENCES [dbo].[users]([id])
    );
    PRINT N'>>> Đã tạo bảng rescue_assignments';
END
GO

-- 7. Bảng Cảnh báo thiên tai & Vùng nguy hiểm (disaster_alerts)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = N'disaster_alerts')
BEGIN
    CREATE TABLE [dbo].[disaster_alerts] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [title] NVARCHAR(200) NOT NULL,
        [content] NVARCHAR(MAX) NOT NULL,
        [severity] VARCHAR(30) NOT NULL, -- DANGER_RED, WARNING_ORANGE, NOTICE_YELLOW
        [affected_region] NVARCHAR(255) NULL,
        [is_broadcast] BIT DEFAULT 0,
        [created_by] BIGINT NULL,
        [created_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        [updated_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        [expires_at] DATETIME2 NULL,
        CONSTRAINT [FK_alerts_created_by] FOREIGN KEY ([created_by]) REFERENCES [dbo].[users]([id])
    );
    PRINT N'>>> Đã tạo bảng disaster_alerts';
END
GO

-- 8. Bảng Điểm sơ tán, tạm lánh (evacuation_shelters)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = N'evacuation_shelters')
BEGIN
    CREATE TABLE [dbo].[evacuation_shelters] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [name] NVARCHAR(200) NOT NULL,
        [address] NVARCHAR(255) NULL,
        [latitude] FLOAT NULL,
        [longitude] FLOAT NULL,
        [total_capacity] INT DEFAULT 300,
        [current_occupancy] INT DEFAULT 0,
        [contact_phone] VARCHAR(30) NULL,
        [status] VARCHAR(30) DEFAULT 'OPEN', -- OPEN, FULL, CLOSED
        [created_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        [updated_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP
    );
    PRINT N'>>> Đã tạo bảng evacuation_shelters';
END
GO

-- 9. Bảng Bài đăng cộng đồng (community_posts)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = N'community_posts')
BEGIN
    CREATE TABLE [dbo].[community_posts] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [user_id] BIGINT NULL,
        [author_name] NVARCHAR(150) NOT NULL,
        [hazard_type] VARCHAR(50) NOT NULL DEFAULT 'OTHER', -- LANDSLIDE, FLOOD, ISOLATED, TREE_FALL, STORM, OTHER
        [content] NVARCHAR(MAX) NOT NULL,
        [location_name] NVARCHAR(255) NULL,
        [latitude] FLOAT NULL,
        [longitude] FLOAT NULL,
        [image_url] VARCHAR(500) NULL,
        [verification_status] VARCHAR(30) DEFAULT 'PENDING', -- PENDING, VERIFIED, REJECTED
        [upvote_count] INT DEFAULT 0,
        [comment_count] INT DEFAULT 0,
        [sos_request_id] BIGINT NULL,
        [created_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        [updated_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT [FK_posts_user] FOREIGN KEY ([user_id]) REFERENCES [dbo].[users]([id]) ON DELETE SET NULL,
        CONSTRAINT [FK_posts_sos] FOREIGN KEY ([sos_request_id]) REFERENCES [dbo].[sos_requests]([id]) ON DELETE SET NULL
    );
    PRINT N'>>> Đã tạo bảng community_posts';
END
GO

-- 10. Bảng Bình chọn bài đăng cộng đồng (post_upvotes)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = N'post_upvotes')
BEGIN
    CREATE TABLE [dbo].[post_upvotes] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [post_id] BIGINT NOT NULL,
        [user_id] BIGINT NOT NULL,
        [created_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT [UQ_post_user] UNIQUE ([post_id], [user_id])
    );
    PRINT N'>>> Đã tạo bảng post_upvotes';
END
GO

-- 11. Bảng Tệp đa phương tiện đính kèm (media_files)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = N'media_files')
BEGIN
    CREATE TABLE [dbo].[media_files] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [file_name] VARCHAR(255) NOT NULL,
        [original_name] VARCHAR(255) NULL,
        [file_url] VARCHAR(1000) NOT NULL,
        [file_size] BIGINT NULL,
        [content_type] VARCHAR(100) NULL,
        [storage_type] VARCHAR(30) DEFAULT 'LOCAL', -- CLOUDINARY, LOCAL
        [public_id] VARCHAR(255) NULL,
        [sos_id] BIGINT NULL,
        [uploaded_by] BIGINT NULL,
        [created_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        [updated_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT [FK_media_sos] FOREIGN KEY ([sos_id]) REFERENCES [dbo].[sos_requests]([id]) ON DELETE CASCADE,
        CONSTRAINT [FK_media_user] FOREIGN KEY ([uploaded_by]) REFERENCES [dbo].[users]([id]) ON DELETE SET NULL
    );
    PRINT N'>>> Đã tạo bảng media_files';
END
GO

-- 12. Bảng Thông báo người dùng (notifications)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = N'notifications')
BEGIN
    CREATE TABLE [dbo].[notifications] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [recipient_id] BIGINT NOT NULL,
        [title] NVARCHAR(200) NOT NULL,
        [message] NVARCHAR(MAX) NOT NULL,
        [type] VARCHAR(50) DEFAULT 'SYSTEM', -- MISSION_NEW, MISSION_STATUS, SOS_NEW, EQUIPMENT_ALERT, SYSTEM
        [target_url] VARCHAR(255) NULL,
        [is_read] BIT DEFAULT 0,
        [created_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT [FK_notifications_recipient] FOREIGN KEY ([recipient_id]) REFERENCES [dbo].[users]([id]) ON DELETE CASCADE
    );
    PRINT N'>>> Đã tạo bảng notifications';
END
GO

-- 13. Bảng Nhật ký hoạt động kiểm toán (activity_logs)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = N'activity_logs')
BEGIN
    CREATE TABLE [dbo].[activity_logs] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [user_id] BIGINT NULL,
        [action] VARCHAR(80) NOT NULL,
        [target_entity] NVARCHAR(150) NULL,
        [details] NVARCHAR(MAX) NULL,
        [ip_address] VARCHAR(50) NULL,
        [created_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT [FK_activity_logs_user] FOREIGN KEY ([user_id]) REFERENCES [dbo].[users]([id]) ON DELETE SET NULL
    );
    PRINT N'>>> Đã tạo bảng activity_logs';
END
GO

-- 14. Bảng Kho trang thiết bị cứu hộ (rescue_equipment)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = N'rescue_equipment')
BEGIN
    CREATE TABLE [dbo].[rescue_equipment] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [team_id] BIGINT NOT NULL,
        [equipment_code] VARCHAR(50) NOT NULL,
        [equipment_name] NVARCHAR(150) NOT NULL,
        [category] VARCHAR(50) DEFAULT 'SAFETY', -- SAFETY, MEDICAL, GEAR, SUPPLY
        [unit] NVARCHAR(30) DEFAULT N'chiếc',
        [total_quantity] INT DEFAULT 0,
        [available_quantity] INT DEFAULT 0,
        [dispatched_quantity] INT DEFAULT 0,
        [damaged_quantity] INT DEFAULT 0,
        [notes] NVARCHAR(MAX) NULL,
        [updated_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT [FK_rescue_equipment_team] FOREIGN KEY ([team_id]) REFERENCES [dbo].[rescue_teams]([id]) ON DELETE CASCADE
    );
    PRINT N'>>> Đã tạo bảng rescue_equipment';
END
GO

-- 15. Bảng Cấp phát trang thiết bị cho nhiệm vụ (equipment_assignments)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = N'equipment_assignments')
BEGIN
    CREATE TABLE [dbo].[equipment_assignments] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [rescue_assignment_id] BIGINT NOT NULL,
        [equipment_id] BIGINT NOT NULL,
        [quantity_dispatched] INT DEFAULT 0,
        [quantity_returned] INT DEFAULT 0,
        [quantity_damaged] INT DEFAULT 0,
        [quantity_lost] INT DEFAULT 0,
        [quantity_consumed] INT DEFAULT 0,
        [status] VARCHAR(30) DEFAULT 'DISPATCHED', -- DISPATCHED, RETURNED
        [returned_at] DATETIME2 NULL,
        [notes] NVARCHAR(MAX) NULL,
        [created_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT [FK_eq_assign_assignment] FOREIGN KEY ([rescue_assignment_id]) REFERENCES [dbo].[rescue_assignments]([id]) ON DELETE CASCADE,
        CONSTRAINT [FK_eq_assign_equipment] FOREIGN KEY ([equipment_id]) REFERENCES [dbo].[rescue_equipment]([id]) ON DELETE NO ACTION
    );
    PRINT N'>>> Đã tạo bảng equipment_assignments';
END
GO

-- 16. Bảng Mã OTP xác thực email (otp_verifications)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = N'otp_verifications')
BEGIN
    CREATE TABLE [dbo].[otp_verifications] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [email] VARCHAR(120) NOT NULL,
        [otp_code] VARCHAR(6) NOT NULL,
        [type] VARCHAR(30) NOT NULL, -- REGISTRATION, FORGOT_PASSWORD
        [expiry_time] DATETIME2 NOT NULL,
        [is_used] BIT NOT NULL DEFAULT 0,
        [created_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP
    );
    CREATE INDEX [IX_otp_email_code] ON [dbo].[otp_verifications]([email], [otp_code]);
    PRINT N'>>> Đã tạo bảng otp_verifications';
END
GO

-- 17. Bảng Quyên góp cứu trợ bão lũ (donations)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = N'donations')
BEGIN
    CREATE TABLE [dbo].[donations] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [donor_name] NVARCHAR(100) DEFAULT N'Nhà hảo tâm ẩn danh',
        [donor_phone] VARCHAR(20) NULL,
        [donor_email] VARCHAR(120) NULL,
        [amount] DECIMAL(15,2) NOT NULL,
        [message] NVARCHAR(500) NULL,
        [target_province] NVARCHAR(80) DEFAULT N'TOAN_MIEN_TRUNG',
        [payment_method] VARCHAR(30) NOT NULL, -- VNPAY, MOMO, VIETQR
        [transaction_code] VARCHAR(100) NOT NULL UNIQUE,
        [status] VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, SUCCESS, FAILED
        [created_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        [paid_at] DATETIME2 NULL
    );
    CREATE INDEX [IX_donations_status] ON [dbo].[donations]([status]);
    CREATE INDEX [IX_donations_tx_code] ON [dbo].[donations]([transaction_code]);
    PRINT N'>>> Đã tạo bảng donations';
END
GO

-- Thêm cột budget vào rescue_teams nếu chưa có
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'rescue_teams') AND name = 'budget')
BEGIN
    ALTER TABLE [dbo].[rescue_teams] ADD [budget] DECIMAL(15,2) DEFAULT 0;
    PRINT N'>>> Đã thêm cột budget vào rescue_teams';
END
GO

-- 18. Bảng Yêu cầu tiếp tế ngân sách nhu yếu phẩm cho đội cứu hộ (team_fund_requests)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = N'team_fund_requests')
BEGIN
    CREATE TABLE [dbo].[team_fund_requests] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [team_id] BIGINT NOT NULL,
        [requester_id] BIGINT NOT NULL,
        [amount] DECIMAL(15,2) NOT NULL,
        [purpose] NVARCHAR(500) NOT NULL,
        [category] VARCHAR(50) DEFAULT 'NHU_YEU_PHAM',
        [urgency_level] VARCHAR(30) DEFAULT 'HIGH',
        [status] VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
        [reviewed_by] BIGINT NULL,
        [reviewed_at] DATETIME2 NULL,
        [reject_reason] NVARCHAR(500) NULL,
        [created_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        [updated_at] DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT [FK_team_fund_team] FOREIGN KEY ([team_id]) REFERENCES [dbo].[rescue_teams]([id]) ON DELETE CASCADE,
        CONSTRAINT [FK_team_fund_requester] FOREIGN KEY ([requester_id]) REFERENCES [dbo].[users]([id]) ON DELETE NO ACTION,
        CONSTRAINT [FK_team_fund_reviewer] FOREIGN KEY ([reviewed_by]) REFERENCES [dbo].[users]([id]) ON DELETE SET NULL
    );
    CREATE INDEX [IX_team_fund_status] ON [dbo].[team_fund_requests]([status]);
    CREATE INDEX [IX_team_fund_team_id] ON [dbo].[team_fund_requests]([team_id]);
    PRINT N'>>> Đã tạo bảng team_fund_requests';
END
GO
