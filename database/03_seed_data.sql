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

-- 6. Nạp Bài đăng cảnh báo cộng đồng mẫu (Community Posts) kèm ảnh Cloudinary CDN
IF NOT EXISTS (SELECT 1 FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Trà Mai%')
    INSERT INTO [dbo].[community_posts] ([author_name], [hazard_type], [content], [location_name], [latitude], [longitude], [image_url], [verification_status], [upvote_count], [comment_count])
    VALUES (N'Trần Thị Thu', 'LANDSLIDE', N'Vết nứt lớn xuất hiện trên sườn đồi phía sau trường tiểu học, đất bùn đã tràn ra đường đèo. Lực lượng cứu nạn đang phong tỏa khu vực nguy hiểm, người dân tuyệt đối không di chuyển qua cung đèo này.', N'Xã Trà Mai, Nam Trà My, Quảng Nam', 15.2841, 108.0821, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793269064/cuunan247/community_feed/feed_landslide_tra_mai.jpg', 'VERIFIED', 24, 3);

IF NOT EXISTS (SELECT 1 FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Cẩm Phô%')
    INSERT INTO [dbo].[community_posts] ([author_name], [hazard_type], [content], [location_name], [latitude], [longitude], [image_url], [verification_status], [upvote_count], [comment_count])
    VALUES (N'Lê Minh Hoàng', 'FLOOD', N'Nước sông Hoài dâng nhanh vượt mức báo động 3, đã ngập sâu tầng 1 dãy nhà ven sông. Có 3 người già chưa di chuyển được, cần xuồng hỗ trợ gấp!', N'Phường Cẩm Phô, TP Hội An, Quảng Nam', 15.8782, 108.3243, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793269093/cuunan247/community_feed/feed_flood_hoi_an.jpg', 'PENDING', 18, 2);

IF NOT EXISTS (SELECT 1 FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Tam Kỳ%')
    INSERT INTO [dbo].[community_posts] ([author_name], [hazard_type], [content], [location_name], [latitude], [longitude], [image_url], [verification_status], [upvote_count], [comment_count])
    VALUES (N'Nguyễn Văn Hùng', 'TREE_FALL', N'Gió giật mạnh làm đổ cây xà cừ cổ thụ chắn ngang đường Hùng Vương, đè đứt đường dây điện hạ thế. Đã có tia lửa điện lóe lên, người dân chú ý không đi qua!', N'Đường Hùng Vương, TP Tam Kỳ, Quảng Nam', 15.5684, 108.4795, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793269099/cuunan247/community_feed/feed_storm_cay_do.jpg', 'VERIFIED', 35, 2);

IF NOT EXISTS (SELECT 1 FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Duy Vinh%')
    INSERT INTO [dbo].[community_posts] ([author_name], [hazard_type], [content], [location_name], [latitude], [longitude], [image_url], [verification_status], [upvote_count], [comment_count])
    VALUES (N'Đội Cứu Hộ Tình Nguyện Miền Trung', 'FLOOD', N'Đội đã điều động 2 xuồng máy cao su tiếp cận khu vực ngập sâu tại thôn An Lạc, đã đưa được 5 hộ dân và người già đến nhà văn hóa an toàn.', N'Thôn An Lạc, Xã Duy Vinh, Duy Xuyên, Quảng Nam', 15.8450, 108.3050, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793269096/cuunan247/community_feed/feed_flood_rescue_boat.jpg', 'VERIFIED', 52, 3);

IF NOT EXISTS (SELECT 1 FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Quế Long%')
    INSERT INTO [dbo].[community_posts] ([author_name], [hazard_type], [content], [location_name], [latitude], [longitude], [image_url], [verification_status], [upvote_count], [comment_count])
    VALUES (N'Phan Thanh Sơn', 'ISOLATED', N'Cầu tràn bị ngập 1.5m chia cắt hoàn toàn 60 hộ dân xóm Trong. Đội cứu trợ đã chuyển xong đợt 1 gồm 100 thùng mì tôm, 50 bình nước sạch và thuốc men thiết yếu.', N'Xã Quế Long, Huyện Quế Sơn, Quảng Nam', 15.6512, 108.1834, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793269102/cuunan247/community_feed/feed_relief_supplies.jpg', 'VERIFIED', 41, 2);

IF NOT EXISTS (SELECT 1 FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Nông Sơn%')
    INSERT INTO [dbo].[community_posts] ([author_name], [hazard_type], [content], [location_name], [latitude], [longitude], [image_url], [verification_status], [upvote_count], [comment_count])
    VALUES (N'Ban Chỉ Huy PCTT & TKCN Địa Phương', 'LANDSLIDE', N'Máy xúc và đội cơ giới đang khẩn trương san ủi khối đất đá sạt lở đèo Le, dự kiến thông tuyến tạm thời trong chiều nay cho xe cứu hộ qua lại.', N'Đèo Le, Nông Sơn, Quảng Nam', 15.6320, 108.0670, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793269104/cuunan247/community_feed/feed_clearing_road.jpg', 'VERIFIED', 29, 2);

-- Cập nhật bổ sung ảnh cho các bài viết hiện hữu nếu đang thiếu
UPDATE [dbo].[community_posts]
SET [image_url] = 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793269064/cuunan247/community_feed/feed_landslide_tra_mai.jpg'
WHERE ([image_url] IS NULL OR [image_url] = '') AND [hazard_type] = 'LANDSLIDE';

UPDATE [dbo].[community_posts]
SET [image_url] = 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793269093/cuunan247/community_feed/feed_flood_hoi_an.jpg'
WHERE ([image_url] IS NULL OR [image_url] = '') AND [hazard_type] = 'FLOOD';

PRINT N'>>> Đã nạp Bài đăng cảnh báo cộng đồng mẫu với ảnh Cloudinary CDN!';
GO

-- 7. Nạp Bình luận mẫu cho bài đăng cộng đồng (Post Comments) & Cảm xúc (Reactions)
DECLARE @p1Id BIGINT = (SELECT TOP 1 [id] FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Trà Mai%');
IF @p1Id IS NULL SET @p1Id = (SELECT TOP 1 [id] FROM [dbo].[community_posts] ORDER BY [id] ASC);

DECLARE @p2Id BIGINT = (SELECT TOP 1 [id] FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Cẩm Phô%');
IF @p2Id IS NULL SET @p2Id = (SELECT [id] FROM (SELECT [id], ROW_NUMBER() OVER(ORDER BY [id]) AS rn FROM [dbo].[community_posts]) t WHERE rn = 2);

DECLARE @p3Id BIGINT = (SELECT TOP 1 [id] FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Tam Kỳ%');
IF @p3Id IS NULL SET @p3Id = (SELECT [id] FROM (SELECT [id], ROW_NUMBER() OVER(ORDER BY [id]) AS rn FROM [dbo].[community_posts]) t WHERE rn = 3);

DECLARE @p4Id BIGINT = (SELECT TOP 1 [id] FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Duy Vinh%');
IF @p4Id IS NULL SET @p4Id = (SELECT [id] FROM (SELECT [id], ROW_NUMBER() OVER(ORDER BY [id]) AS rn FROM [dbo].[community_posts]) t WHERE rn = 4);

DECLARE @p5Id BIGINT = (SELECT TOP 1 [id] FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Quế Long%');
IF @p5Id IS NULL SET @p5Id = (SELECT [id] FROM (SELECT [id], ROW_NUMBER() OVER(ORDER BY [id]) AS rn FROM [dbo].[community_posts]) t WHERE rn = 5);

DECLARE @p6Id BIGINT = (SELECT TOP 1 [id] FROM [dbo].[community_posts] WHERE [location_name] LIKE N'%Nông Sơn%');
IF @p6Id IS NULL SET @p6Id = (SELECT [id] FROM (SELECT [id], ROW_NUMBER() OVER(ORDER BY [id]) AS rn FROM [dbo].[community_posts]) t WHERE rn = 6);

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

-- 7.3. Bình luận mẫu cho Post 3 (Đường Hùng Vương, Tam Kỳ)
IF @p3Id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM [dbo].[post_comments] WHERE [post_id] = @p3Id)
BEGIN
    INSERT INTO [dbo].[post_comments] ([post_id], [user_id], [author_name], [author_avatar], [content], [created_at]) VALUES
    (@p3Id, NULL, N'Phạm Văn Bình', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=120&q=80', N'Điện lực Tam Kỳ đã cắt điện trạm biến áp số 4 từ 15 phút trước rồi, bà con an tâm nhưng vẫn tránh xa dây chùng nhé.', DATEADD(MINUTE, -100, GETDATE())),
    (@p3Id, NULL, N'Nguyễn Thị Đào', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=120&q=80', N'Cảm ơn các bác thợ điện đã xử lý kịp thời, dông lốc kinh khủng quá!', DATEADD(MINUTE, -75, GETDATE()));

    UPDATE [dbo].[community_posts] SET [comment_count] = 2 WHERE [id] = @p3Id;
END

-- 7.4. Bình luận mẫu cho Post 4 (Thôn An Lạc, Duy Vinh)
IF @p4Id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM [dbo].[post_comments] WHERE [post_id] = @p4Id)
BEGIN
    INSERT INTO [dbo].[post_comments] ([post_id], [user_id], [author_name], [author_avatar], [content], [created_at]) VALUES
    (@p4Id, NULL, N'Trần Anh Tuấn', 'https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=120&q=80', N'Tuyệt vời quá các chiến sĩ ơi! Người nhà mình ở An Lạc vừa gọi điện báo đã được lên xuồng an toàn rồi.', DATEADD(MINUTE, -130, GETDATE())),
    (@p4Id, NULL, N'Lê Văn Thành', 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&w=120&q=80', N'Ấm lòng mùa bão lũ. Chúc toàn đội nhiều sức khỏe và an toàn trên từng chuyến xuồng!', DATEADD(MINUTE, -105, GETDATE())),
    (@p4Id, NULL, N'Bùi Thu Trang', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=120&q=80', N'Biết ơn các anh cứu hộ vô cùng, bà con nhớ mặc áo phao cẩn thận.', DATEADD(MINUTE, -80, GETDATE()));

    UPDATE [dbo].[community_posts] SET [comment_count] = 3 WHERE [id] = @p4Id;
END

-- 7.5. Bình luận mẫu cho Post 5 (Xã Quế Long, Quế Sơn)
IF @p5Id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM [dbo].[post_comments] WHERE [post_id] = @p5Id)
BEGIN
    INSERT INTO [dbo].[post_comments] ([post_id], [user_id], [author_name], [author_avatar], [content], [created_at]) VALUES
    (@p5Id, NULL, N'Đặng Hữu Phúc', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=120&q=80', N'Bên mình đang tập kết thêm 200 suất bánh chưng và nước suối tại UBND xã Quế Long, ai cần tiếp tế thêm liên hệ nhé.', DATEADD(MINUTE, -115, GETDATE())),
    (@p5Id, NULL, N'Hoàng Thị Tuyết', 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=120&q=80', N'Bà con khu xóm Trong đang rất thiếu nước sạch, may quá có đoàn về hỗ trợ kịp thời!', DATEADD(MINUTE, -90, GETDATE()));

    UPDATE [dbo].[community_posts] SET [comment_count] = 2 WHERE [id] = @p5Id;
END

-- 7.6. Bình luận mẫu cho Post 6 (Đèo Le, Nông Sơn)
IF @p6Id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM [dbo].[post_comments] WHERE [post_id] = @p6Id)
BEGIN
    INSERT INTO [dbo].[post_comments] ([post_id], [user_id], [author_name], [author_avatar], [content], [created_at]) VALUES
    (@p6Id, NULL, N'Hoàng Văn Nam', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=120&q=80', N'Đoạn Đèo Le đá rơi nhiều, các xe cứu trợ không có dẫn đường thì tạm thời dừng chờ ở chân đèo nhé bà con.', DATEADD(MINUTE, -125, GETDATE())),
    (@p6Id, NULL, N'Nguyễn Quang Huy', 'https://images.unsplash.com/photo-1501196354995-cbb51c65aaea?auto=format&fit=crop&w=120&q=80', N'Lực lượng chức năng đang làm việc rất khẩn trương, hy vọng đầu giờ chiều sẽ thông được một làn đường.', DATEADD(MINUTE, -100, GETDATE()));

    UPDATE [dbo].[community_posts] SET [comment_count] = 2 WHERE [id] = @p6Id;
END

-- 7.7. Nạp Phản ứng cảm xúc mẫu (Reactions / post_upvotes)
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

IF @p4Id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM [dbo].[post_upvotes] WHERE [post_id] = @p4Id)
    INSERT INTO [dbo].[post_upvotes] ([post_id], [user_id], [reaction_type], [created_at]) VALUES
    (@p4Id, 113, 'LIKE', GETDATE()),
    (@p4Id, 114, 'CARE', GETDATE()),
    (@p4Id, 115, 'PRAY', GETDATE()),
    (@p4Id, 116, 'LIKE', GETDATE());

IF @p5Id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM [dbo].[post_upvotes] WHERE [post_id] = @p5Id)
    INSERT INTO [dbo].[post_upvotes] ([post_id], [user_id], [reaction_type], [created_at]) VALUES
    (@p5Id, 117, 'CARE', GETDATE()),
    (@p5Id, 118, 'LIKE', GETDATE()),
    (@p5Id, 119, 'PRAY', GETDATE());

IF @p6Id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM [dbo].[post_upvotes] WHERE [post_id] = @p6Id)
    INSERT INTO [dbo].[post_upvotes] ([post_id], [user_id], [reaction_type], [created_at]) VALUES
    (@p6Id, 120, 'LIKE', GETDATE()),
    (@p6Id, 121, 'CARE', GETDATE()),
    (@p6Id, 122, 'WARNING', GETDATE());

-- Đồng bộ số lượng comment_count chính xác cho từng bài viết
UPDATE cp
SET cp.[comment_count] = ISNULL((SELECT COUNT(*) FROM [dbo].[post_comments] c WHERE c.[post_id] = cp.[id]), 0)
FROM [dbo].[community_posts] cp;

PRINT N'>>> Đã nạp Bình luận mẫu và Phản ứng icon cảm xúc cho các bài đăng cộng đồng!';
GO
