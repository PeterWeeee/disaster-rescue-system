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
