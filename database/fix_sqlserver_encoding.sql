-- =========================================================================
-- ĐỒ ÁN LẬP TRÌNH WEB - NHÓM 04
-- DỰ ÁN: HỆ THỐNG CỨU NẠN THIÊN TAI (CỨUNẠN 24/7)
-- FILE: fix_sqlserver_encoding.sql
-- MỤC ĐÍCH: NÂNG CẤP KIỂU DỮ LIỆU SANG NVARCHAR/NVARCHAR(MAX) & SỬA LỖI FONT TIẾNG VIỆT
-- =========================================================================

USE [cuunan_db];
GO

PRINT N'>>> 1. Đang kiểm tra và chuyển đổi kiểu dữ liệu cột sang NVARCHAR / NVARCHAR(MAX)...';

-- 1. Bảng notifications
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'notifications') AND name = 'message')
    ALTER TABLE [dbo].[notifications] ALTER COLUMN [message] NVARCHAR(MAX) NOT NULL;

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'notifications') AND name = 'title')
    ALTER TABLE [dbo].[notifications] ALTER COLUMN [title] NVARCHAR(200) NOT NULL;

-- 2. Bảng users
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'users') AND name = 'full_name')
    ALTER TABLE [dbo].[users] ALTER COLUMN [full_name] NVARCHAR(150) NOT NULL;

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'users') AND name = 'address')
    ALTER TABLE [dbo].[users] ALTER COLUMN [address] NVARCHAR(255) NULL;

-- 3. Bảng rescue_teams
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'rescue_teams') AND name = 'team_name')
    ALTER TABLE [dbo].[rescue_teams] ALTER COLUMN [team_name] NVARCHAR(150) NOT NULL;

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'rescue_teams') AND name = 'equipment_notes')
    ALTER TABLE [dbo].[rescue_teams] ALTER COLUMN [equipment_notes] NVARCHAR(MAX) NULL;

-- 4. Bảng sos_requests
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'sos_requests') AND name = 'contact_name')
    ALTER TABLE [dbo].[sos_requests] ALTER COLUMN [contact_name] NVARCHAR(150) NOT NULL;

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'sos_requests') AND name = 'address_detail')
    ALTER TABLE [dbo].[sos_requests] ALTER COLUMN [address_detail] NVARCHAR(255) NULL;

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'sos_requests') AND name = 'description')
    ALTER TABLE [dbo].[sos_requests] ALTER COLUMN [description] NVARCHAR(MAX) NULL;

-- 5. Bảng rescue_assignments
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'rescue_assignments') AND name = 'report_notes')
    ALTER TABLE [dbo].[rescue_assignments] ALTER COLUMN [report_notes] NVARCHAR(MAX) NULL;

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'rescue_assignments') AND name = 'selected_equipment')
    ALTER TABLE [dbo].[rescue_assignments] ALTER COLUMN [selected_equipment] NVARCHAR(MAX) NULL;

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'rescue_assignments') AND name = 'rejection_reason')
    ALTER TABLE [dbo].[rescue_assignments] ALTER COLUMN [rejection_reason] NVARCHAR(255) NULL;

-- 6. Bảng disaster_alerts
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'disaster_alerts') AND name = 'title')
    ALTER TABLE [dbo].[disaster_alerts] ALTER COLUMN [title] NVARCHAR(200) NOT NULL;

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'disaster_alerts') AND name = 'content')
    ALTER TABLE [dbo].[disaster_alerts] ALTER COLUMN [content] NVARCHAR(MAX) NOT NULL;

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'disaster_alerts') AND name = 'affected_region')
    ALTER TABLE [dbo].[disaster_alerts] ALTER COLUMN [affected_region] NVARCHAR(255) NULL;

-- 7. Bảng evacuation_shelters
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'evacuation_shelters') AND name = 'name')
    ALTER TABLE [dbo].[evacuation_shelters] ALTER COLUMN [name] NVARCHAR(200) NOT NULL;

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'evacuation_shelters') AND name = 'address')
    ALTER TABLE [dbo].[evacuation_shelters] ALTER COLUMN [address] NVARCHAR(255) NULL;

-- 8. Bảng community_posts
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'community_posts') AND name = 'author_name')
    ALTER TABLE [dbo].[community_posts] ALTER COLUMN [author_name] NVARCHAR(150) NOT NULL;

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'community_posts') AND name = 'content')
    ALTER TABLE [dbo].[community_posts] ALTER COLUMN [content] NVARCHAR(MAX) NOT NULL;

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'community_posts') AND name = 'location_name')
    ALTER TABLE [dbo].[community_posts] ALTER COLUMN [location_name] NVARCHAR(255) NULL;

-- 9. Bảng rescue_equipment
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'rescue_equipment') AND name = 'equipment_name')
    ALTER TABLE [dbo].[rescue_equipment] ALTER COLUMN [equipment_name] NVARCHAR(150) NOT NULL;

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'rescue_equipment') AND name = 'notes')
    ALTER TABLE [dbo].[rescue_equipment] ALTER COLUMN [notes] NVARCHAR(MAX) NULL;

-- 10. Bảng equipment_assignments
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'equipment_assignments') AND name = 'notes')
    ALTER TABLE [dbo].[equipment_assignments] ALTER COLUMN [notes] NVARCHAR(MAX) NULL;

-- 11. Bảng activity_logs
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'activity_logs') AND name = 'target_entity')
    ALTER TABLE [dbo].[activity_logs] ALTER COLUMN [target_entity] NVARCHAR(150) NULL;

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'activity_logs') AND name = 'details')
    ALTER TABLE [dbo].[activity_logs] ALTER COLUMN [details] NVARCHAR(MAX) NULL;

-- 12. Bảng donations
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'donations') AND name = 'donor_name')
    ALTER TABLE [dbo].[donations] ALTER COLUMN [donor_name] NVARCHAR(100) NULL;

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'donations') AND name = 'message')
    ALTER TABLE [dbo].[donations] ALTER COLUMN [message] NVARCHAR(500) NULL;

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'donations') AND name = 'target_province')
    ALTER TABLE [dbo].[donations] ALTER COLUMN [target_province] NVARCHAR(80) NULL;

-- 13. Bảng team_fund_requests
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'team_fund_requests') AND name = 'purpose')
    ALTER TABLE [dbo].[team_fund_requests] ALTER COLUMN [purpose] NVARCHAR(500) NOT NULL;

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'team_fund_requests') AND name = 'reject_reason')
    ALTER TABLE [dbo].[team_fund_requests] ALTER COLUMN [reject_reason] NVARCHAR(500) NULL;

-- 14. Bảng rescue_chat_messages
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'rescue_chat_messages') AND name = 'sender_name')
    ALTER TABLE [dbo].[rescue_chat_messages] ALTER COLUMN [sender_name] NVARCHAR(100) NOT NULL;

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'rescue_chat_messages') AND name = 'content')
    ALTER TABLE [dbo].[rescue_chat_messages] ALTER COLUMN [content] NVARCHAR(MAX) NOT NULL;

PRINT N'>>> Chuyển đổi kiểu dữ liệu cột thành công!';
GO

PRINT N'>>> 2. Đang tự động sửa dữ liệu thông báo và bảng biểu bị lỗi font...';

-- Phục hồi thông báo mẫu (Notifications)
UPDATE [dbo].[notifications]
SET [title] = N'Tiếp nhận ca SOS #1 mới',
    [message] = N'Có yêu cầu cứu nạn khẩn cấp tại Mẹ Suốt, Liên Chiểu, Đà Nẵng cần điều phối.'
WHERE [type] = 'SOS_NEW';

UPDATE [dbo].[notifications]
SET [title] = N'Lệnh phân công nhiệm vụ mới',
    [message] = N'Bạn nhận lệnh cứu nạn ca SOS #1 tại K456 Tôn Đức Thắng, Đà Nẵng. Mức độ: CRITICAL.'
WHERE [type] = 'MISSION_NEW';

-- Phục hồi nhật ký hệ thống (ActivityLogs)
UPDATE [dbo].[activity_logs]
SET [target_entity] = N'Hệ thống',
    [details] = N'Khởi tạo toàn bộ dữ liệu mẫu hệ thống cứu nạn 24/7 miền Trung thành công'
WHERE [action] = 'SYSTEM_INIT';

PRINT N'>>> Đã hoàn tất sửa lỗi font SQL Server!';
GO
