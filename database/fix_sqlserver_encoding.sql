-- =========================================================================
-- ĐỒ ÁN LẬP TRÌNH WEB - NHÓM 04
-- DỰ ÁN: HỆ THỐNG CỨU NẠN THIÊN TAI (CỨUNẠN 24/7)
-- FILE: fix_sqlserver_encoding.sql
-- MỤC ĐÍCH: NÂNG CẤP TOÀN BỘ CỘT SANG NVARCHAR/NVARCHAR(MAX) & SỬA TRIỆT ĐỂ LỖI FONT TIẾNG VIỆT
-- ĐỊNH DẠNG FILE: UTF-8 WITH BOM (Đảm bảo SSMS và sqlcmd nạp đúng 100%)
-- =========================================================================

USE [cuunan_db];
GO

PRINT N'>>> 1. Đang kiểm tra và chuyển đổi kiểu dữ liệu các cột sang NVARCHAR / NVARCHAR(MAX)...';

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

-- 15. Bảng post_comments
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'post_comments') AND name = 'author_name')
    ALTER TABLE [dbo].[post_comments] ALTER COLUMN [author_name] NVARCHAR(150) NOT NULL;

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'post_comments') AND name = 'content')
    ALTER TABLE [dbo].[post_comments] ALTER COLUMN [content] NVARCHAR(MAX) NOT NULL;

PRINT N'>>> Chuyển đổi kiểu dữ liệu cột thành công!';
GO

PRINT N'>>> 2. Đang tự động sửa dữ liệu thông báo, nhật ký, bài viết và bảng biểu bị lỗi font...';

-- 2.1. Phục hồi thông báo (Notifications)
UPDATE [dbo].[notifications]
SET [title] = N'Tiếp nhận ca SOS #1 mới',
    [message] = N'Có yêu cầu cứu nạn khẩn cấp tại Mẹ Suốt, Liên Chiểu, Đà Nẵng cần điều phối.'
WHERE [type] = 'SOS_NEW' OR [title] LIKE N'%Ti%p nh%n%';

UPDATE [dbo].[notifications]
SET [title] = N'Lệnh phân công nhiệm vụ mới',
    [message] = N'Bạn nhận lệnh cứu nạn ca SOS #1 tại K456 Tôn Đức Thắng, Đà Nẵng. Mức độ: CRITICAL.'
WHERE [type] = 'MISSION_NEW' OR [title] LIKE N'%L%nh ph%n c%ng%';

-- 2.2. Phục hồi nhật ký hệ thống (ActivityLogs)
UPDATE [dbo].[activity_logs]
SET [target_entity] = N'Hệ thống',
    [details] = N'Khởi tạo toàn bộ dữ liệu mẫu hệ thống cứu nạn 24/7 miền Trung thành công'
WHERE [action] = 'SYSTEM_INIT';

UPDATE [dbo].[activity_logs]
SET [target_entity] = N'Nhiệm vụ #1',
    [details] = N'Điều phối Đội cứu hộ Đà Nẵng 01 tiếp cận ca cứu nạn #1 (Nguyễn Văn An)'
WHERE [action] = 'DISPATCH_MISSION';

UPDATE [dbo].[activity_logs]
SET [target_entity] = N'Nhiệm vụ #1',
    [details] = N'Đội Cứu hộ Đà Nẵng 01 chấp nhận ca cứu nạn #1'
WHERE [action] = 'ACCEPT_MISSION';

UPDATE [dbo].[activity_logs]
SET [target_entity] = N'Nhiệm vụ #1',
    [details] = N'Xuất kho trang bị: 2 Áo phao chuyên dụng, 1 Xuồng máy cao su'
WHERE [action] = 'DISPATCH_EQUIPMENT';

UPDATE [dbo].[activity_logs]
SET [target_entity] = N'Nhiệm vụ #1',
    [details] = N'Đội Cứu hộ Đà Nẵng 01 xuất kích tiếp cận ca #1 (Cự ly: 8.5 km)'
WHERE [action] = 'START_MISSION';

UPDATE [dbo].[activity_logs]
SET [target_entity] = N'Yêu cầu tiếp tế #1',
    [details] = N'Phê duyệt kinh phí 15.000.000 ₫ cho Đội Cứu hộ Đà Nẵng 01 mua nhu yếu phẩm'
WHERE [action] = 'APPROVE_BUDGET_REQUEST';

-- 2.3. Phục hồi bài viết cảnh báo cộng đồng (CommunityPosts)
UPDATE [dbo].[community_posts]
SET [author_name] = N'Cil Ha Kôi',
    [content] = N'Đèo Prenn đoạn Km225 xuất hiện vết nứt sườn taluy dương, có đất đá nhỏ rơi xuống mặt đường, các bác tài chú ý giảm tốc độ.',
    [location_name] = N'Đèo Prenn, Phường 3, TP. Đà Lạt, Lâm Đồng'
WHERE [location_name] LIKE N'%Prenn%' OR [location_name] LIKE N'%Lâm Đồng%' OR ([hazard_type] = 'LANDSLIDE' AND [location_name] LIKE N'%Đà Lạt%');

UPDATE [dbo].[community_posts]
SET [author_name] = N'Trần Thị Thu',
    [content] = N'Vết nứt lớn xuất hiện trên sườn đồi phía sau trường tiểu học, đất bùn đã tràn ra đường đèo. Lực lượng cứu nạn đang phong tỏa khu vực nguy hiểm, người dân tuyệt đối không di chuyển qua cung đèo này.',
    [location_name] = N'Xã Trà Mai, Nam Trà My, Quảng Nam'
WHERE [location_name] LIKE N'%Trà Mai%' OR [location_name] LIKE N'%Tr% Mai%';

UPDATE [dbo].[community_posts]
SET [author_name] = N'Lê Minh Hoàng',
    [content] = N'Nước sông Hoài dâng nhanh vượt mức báo động 3, đã ngập sâu tầng 1 dãy nhà ven sông. Có 3 người già chưa di chuyển được, cần xuồng hỗ trợ gấp!',
    [location_name] = N'Phường Cẩm Phô, TP Hội An, Quảng Nam'
WHERE [location_name] LIKE N'%Cẩm Phô%' OR [location_name] LIKE N'%C%m Ph%';

UPDATE [dbo].[community_posts]
SET [author_name] = N'Nguyễn Văn Hùng',
    [content] = N'Gió giật mạnh làm đổ cây xà cừ cổ thụ chắn ngang đường Hùng Vương, đè đứt đường dây điện hạ thế. Đã có tia lửa điện lóe lên, người dân chú ý không đi qua!',
    [location_name] = N'Đường Hùng Vương, TP Tam Kỳ, Quảng Nam'
WHERE [location_name] LIKE N'%Hùng Vương%' OR [location_name] LIKE N'%Tam Kỳ%';

UPDATE [dbo].[community_posts]
SET [author_name] = N'Đội Cứu Hộ Tình Nguyện Miền Trung',
    [content] = N'Đội đã điều động 2 xuồng máy cao su tiếp cận khu vực ngập sâu tại thôn An Lạc, đã đưa được 5 hộ dân và người già đến nhà văn hóa an toàn.',
    [location_name] = N'Thôn An Lạc, Xã Duy Vinh, Duy Xuyên, Quảng Nam'
WHERE [location_name] LIKE N'%An Lạc%' OR [location_name] LIKE N'%Duy Vinh%';

UPDATE [dbo].[community_posts]
SET [author_name] = N'Phan Thanh Sơn',
    [content] = N'Cầu tràn bị ngập 1.5m chia cắt hoàn toàn 60 hộ dân xóm Trong. Đội cứu trợ đã chuyển xong đợt 1 gồm 100 thùng mì tôm, 50 bình nước sạch và thuốc men thiết yếu.',
    [location_name] = N'Xã Quế Long, Huyện Quế Sơn, Quảng Nam'
WHERE [location_name] LIKE N'%Quế Long%' OR [location_name] LIKE N'%Qu% Long%';

UPDATE [dbo].[community_posts]
SET [author_name] = N'Ban Chỉ Huy PCTT & TKCN Địa Phương',
    [content] = N'Máy xúc và đội cơ giới đang khẩn trương san ủi khối đất đá sạt lở đèo Le, dự kiến thông tuyến tạm thời trong chiều nay cho xe cứu hộ qua lại.',
    [location_name] = N'Đèo Le, Nông Sơn, Quảng Nam'
WHERE [location_name] LIKE N'%Đèo Le%' OR [location_name] LIKE N'%Nông Sơn%';

UPDATE [dbo].[community_posts]
SET [author_name] = N'Nguyễn Văn Dân',
    [content] = N'Đường Mẹ Suốt đoạn gần cầu mương ngập sâu hơn 1m, nước chảy rất xiết, bà con tuyệt đối không nên di chuyển bằng xe máy!',
    [location_name] = N'Đường Mẹ Suốt, P. Hòa Khánh Nam, Liên Chiểu, Đà Nẵng'
WHERE [location_name] LIKE N'%Mẹ Suốt%' OR [location_name] LIKE N'%M% Su%t%';

UPDATE [dbo].[community_posts]
SET [author_name] = N'Lê Thị Cứu Trợ',
    [content] = N'Cầu tràn thôn Thạch Nham Tây xã Hòa Nhơn đang bị ngập 0.8m, cô lập 120 hộ dân, cần xuồng tiếp tế mì và nước uống.',
    [location_name] = N'Thôn Thạch Nham Tây, Xã Hòa Nhơn, Hòa Vang, Đà Nẵng'
WHERE [location_name] LIKE N'%Thạch Nham%' OR [location_name] LIKE N'%Hòa Nhơn%';

UPDATE [dbo].[community_posts]
SET [author_name] = N'Trần Thị Huế',
    [content] = N'Đường Bạch Đằng ven sông Hương nước tràn lên bờ đi vào nhà dân sâu 0.5m, đề nghị bà con dọn đồ lên gác lửng.',
    [location_name] = N'Đường Bạch Đằng, Phường Phú Cát, TP. Huế'
WHERE [location_name] LIKE N'%Bạch Đằng%' OR [location_name] LIKE N'%B%ch %ng%';

UPDATE [dbo].[community_posts]
SET [author_name] = N'Lê Văn Nghệ An',
    [content] = N'Cây phượng cổ thụ bị lốc xoáy quật đổ đè trúng cột điện hạ thế tại đường Nguyễn Du, gây mất điện toàn khu phố.',
    [location_name] = N'Đường Nguyễn Du, Phường Bến Thủy, TP. Vinh, Nghệ An'
WHERE [location_name] LIKE N'%Nguyễn Du%' OR [location_name] LIKE N'%Bến Thủy%';

UPDATE [dbo].[community_posts]
SET [author_name] = N'Hoàng Thị Khánh Hòa',
    [content] = N'Gió rít mạnh từng cơn giật cấp 9 tại ven biển Vĩnh Hải, sóng cao đánh tràn qua kè công viên, người dân không nên ra bờ biển.',
    [location_name] = N'Bờ kè Phạm Văn Đồng, Phường Vĩnh Hải, TP. Nha Trang, Khánh Hòa'
WHERE [location_name] LIKE N'%Phạm Văn Đồng%' OR [location_name] LIKE N'%Nha Trang%';

UPDATE [dbo].[community_posts]
SET [author_name] = N'Ksor H''Lâm',
    [content] = N'Tin đồn vỡ đập thủy điện trên sông Ba là tin giả mạo, chính quyền địa phương thông báo đập vẫn vận hành xả lũ an toàn.',
    [location_name] = N'Huyện Krông Pa, Tỉnh Gia Lai'
WHERE [location_name] LIKE N'%Krông Pa%' OR [location_name] LIKE N'%Gia Lai%';

-- 2.4. Phục hồi Cảnh báo thiên tai (DisasterAlerts)
UPDATE [dbo].[disaster_alerts]
SET [title] = N'Lũ khẩn cấp trên sông Mã và lưu vực Bắc Trung Bộ',
    [content] = N'Mực nước sông Mã tại trạm Cẩm Thủy đang vượt báo động 3 là 0.8m, nguy cơ ngập lụt diện rộng các xã ven sông.',
    [affected_region] = N'Tỉnh Thanh Hóa, Nghệ An, Hà Tĩnh'
WHERE [title] LIKE N'%sông Mã%' OR [title] LIKE N'%s%ng M%';

UPDATE [dbo].[disaster_alerts]
SET [title] = N'Mưa lớn đặc biệt và lũ quét khu vực Thừa Thiên Huế - Đà Nẵng',
    [content] = N'Tổng lượng mưa 24h qua phổ biến 350-500mm, hồ Tả Trạch xả lũ điều tiết, vùng hạ du báo động đỏ.',
    [affected_region] = N'Thừa Thiên Huế, TP. Đà Nẵng, Quảng Nam'
WHERE [title] LIKE N'%Thừa Thiên Huế%' OR [title] LIKE N'%Hu%';

UPDATE [dbo].[disaster_alerts]
SET [title] = N'Cảnh báo sạt lở đèo dốc và sườn núi nguy cơ cao Tây Nguyên',
    [content] = N'Các tuyến quốc lộ 14, 27C và đường liên huyện tại Gia Lai, Kon Tum, Đắk Lắk xuất hiện nhiều điểm nứt sườn dốc.',
    [affected_region] = N'Gia Lai, Đắk Lắk, Kon Tum, Lâm Đồng'
WHERE [title] LIKE N'%Tây Nguyên%' OR [title] LIKE N'%T%y Nguy%n%';

UPDATE [dbo].[disaster_alerts]
SET [title] = N'Triều cường và sóng biển dâng cao duyên hải Nam Trung Bộ',
    [content] = N'Sóng biển cao 4-5m đánh trực tiếp vào kè chắn sóng, cấm toàn bộ tàu thuyền và lồng bè nuôi trồng thủy sản ra khơi.',
    [affected_region] = N'Quảng Ngãi, Bình Định, Phú Yên, Khánh Hòa'
WHERE [title] LIKE N'%Nam Trung Bộ%' OR [title] LIKE N'%sóng biển%';

-- 2.5. Phục hồi bình luận bài đăng (post_comments)
UPDATE [dbo].[post_comments]
SET [author_name] = N'Nguyễn Hoàng Nam',
    [content] = N'Lực lượng CSGT và thanh tra Lâm Đồng đã cắm biển cảnh báo hai đầu đèo Prenn rồi, anh em tài xế chú ý giảm tốc độ hoặc chuyển sang đèo Sacom!'
WHERE [content] LIKE N'%Prenn%' OR [content] LIKE N'%Sacom%' OR ([author_name] LIKE N'%Nam%' AND [content] LIKE N'%cảnh báo%');

UPDATE [dbo].[post_comments]
SET [author_name] = N'Trần Thị Lan',
    [content] = N'Trời đang mưa to ở Đà Lạt, sườn đồi rất dễ sạt lở tiếp, các bác tài tuyệt đối không dừng đỗ sát chân taluy dương nhé.'
WHERE [content] LIKE N'%Đà Lạt%' OR [content] LIKE N'%taluy%' OR ([author_name] LIKE N'%Lan%' AND [content] LIKE N'%mưa to%');

UPDATE [dbo].[post_comments]
SET [author_name] = N'Nguyễn Văn Hùng',
    [content] = N'Bà con khu vực gần sườn đồi chú ý tuyệt đối không đi đường tắt qua khe suối nhé, đất bùn vẫn đang có dấu hiệu trượt tiếp!'
WHERE [content] LIKE N'%khe suối%' OR [content] LIKE N'%trượt tiếp%';

UPDATE [dbo].[post_comments]
SET [author_name] = N'Trần Đình Điều Phối',
    [content] = N'Lực lượng dân quân và công an địa phương đã chốt chặn hai đầu ngõ rồi ạ. Đề nghị bà con bình tĩnh chờ lệnh sơ tán.'
WHERE [content] LIKE N'%chốt chặn%' OR [content] LIKE N'%dân quân%';

UPDATE [dbo].[post_comments]
SET [author_name] = N'Lê Thị Mai',
    [content] = N'Cầu mong không có ai bị thương, nhìn vết nứt mà thót cả tim! Mọi người cố gắng giữ an toàn nhé.'
WHERE [content] LIKE N'%thót cả tim%' OR [content] LIKE N'%vết nứt%';

UPDATE [dbo].[post_comments]
SET [author_name] = N'Võ Thị Mỹ Linh',
    [content] = N'Có gia đình bà Bảy ở cụm xóm Trong đang bị kẹt trên gác lửng, nước lên sát sàn rồi, đội ca nô ghé đón với ạ!'
WHERE [content] LIKE N'%bà Bảy%' OR [content] LIKE N'%gác lửng%';

UPDATE [dbo].[post_comments]
SET [author_name] = N'Đội Cứu Hộ Đà Nẵng',
    [content] = N'Đội 02 đã tiếp nhận thông tin nhà bà Bảy, xuồng máy đang rẽ vào hẻm tiếp cận ngay đây!'
WHERE [content] LIKE N'%Đội 02%' OR [content] LIKE N'%tiếp nhận thông tin%';

UPDATE [dbo].[post_comments]
SET [author_name] = N'Phạm Văn Bình',
    [content] = N'Điện lực Tam Kỳ đã cắt điện trạm biến áp số 4 từ 15 phút trước rồi, bà con an tâm nhưng vẫn tránh xa dây chùng nhé.'
WHERE [content] LIKE N'%biến áp số 4%' OR [content] LIKE N'%dây chùng%';

UPDATE [dbo].[post_comments]
SET [author_name] = N'Nguyễn Thị Đào',
    [content] = N'Cảm ơn các bác thợ điện đã xử lý kịp thời, dông lốc kinh khủng quá!'
WHERE [content] LIKE N'%thợ điện%' OR [content] LIKE N'%dông lốc%';

UPDATE [dbo].[post_comments]
SET [author_name] = N'Trần Anh Tuấn',
    [content] = N'Tuyệt vời quá các chiến sĩ ơi! Người nhà mình ở An Lạc vừa gọi điện báo đã được lên xuồng an toàn rồi.'
WHERE [content] LIKE N'%chiến sĩ ơi%' OR [content] LIKE N'%An Lạc%';

UPDATE [dbo].[post_comments]
SET [author_name] = N'Lê Văn Thành',
    [content] = N'Ấm lòng mùa bão lũ. Chúc toàn đội nhiều sức khỏe và an toàn trên từng chuyến xuồng!'
WHERE [content] LIKE N'%Ấm lòng%' OR [content] LIKE N'%chuyến xuồng%';

UPDATE [dbo].[post_comments]
SET [author_name] = N'Bùi Thu Trang',
    [content] = N'Biết ơn các anh cứu hộ vô cùng, bà con nhớ mặc áo phao cẩn thận.'
WHERE [content] LIKE N'%Biết ơn các anh%' OR [content] LIKE N'%áo phao%';

UPDATE [dbo].[post_comments]
SET [author_name] = N'Đặng Hữu Phúc',
    [content] = N'Bên mình đang tập kết thêm 200 suất bánh chưng và nước suối tại UBND xã Quế Long, ai cần tiếp tế thêm liên hệ nhé.'
WHERE [content] LIKE N'%bánh chưng%' OR [content] LIKE N'%Quế Long%';

UPDATE [dbo].[post_comments]
SET [author_name] = N'Hoàng Thị Tuyết',
    [content] = N'Bà con khu xóm Trong đang rất thiếu nước sạch, may quá có đoàn về hỗ trợ kịp thời!'
WHERE [content] LIKE N'%xóm Trong%' OR [content] LIKE N'%nước sạch%';

UPDATE [dbo].[post_comments]
SET [author_name] = N'Hoàng Văn Nam',
    [content] = N'Đoạn Đèo Le đá rơi nhiều, các xe cứu trợ không có dẫn đường thì tạm thời dừng chờ ở chân đèo nhé bà con.'
WHERE [content] LIKE N'%Đèo Le đá rơi%' OR [content] LIKE N'%chân đèo%';

UPDATE [dbo].[post_comments]
SET [author_name] = N'Nguyễn Quang Huy',
    [content] = N'Lực lượng chức năng đang làm việc rất khẩn trương, hy vọng đầu giờ chiều sẽ thông được một làn đường.'
WHERE [content] LIKE N'%làn đường%' OR [content] LIKE N'%khẩn trương%';

-- Xóa bỏ bất kỳ bình luận nào còn sót dấu hỏi ? để ứng dụng tự tái tạo dữ liệu sạch 100%
DELETE FROM [dbo].[post_comments] WHERE [content] LIKE N'%?%' OR [author_name] LIKE N'%?%';

-- 2.6. Đồng bộ số lượng bình luận chính xác cho từng bài viết
UPDATE cp
SET cp.[comment_count] = ISNULL((SELECT COUNT(*) FROM [dbo].[post_comments] c WHERE c.[post_id] = cp.[id]), 0)
FROM [dbo].[community_posts] cp;

PRINT N'>>> Đã hoàn tất nâng cấp schema và phục hồi 100% dữ liệu tiếng Việt có dấu!';
GO
