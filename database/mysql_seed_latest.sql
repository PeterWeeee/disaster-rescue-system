-- =========================================================================
-- ĐỒ ÁN LẬP TRÌNH WEB - NHÓM 04
-- DỰ ÁN: HỆ THỐNG CỨU NẠN THIÊN TAI (CỨUNẠN 24/7)
-- FILE: mysql_seed_latest.sql (ĐÃ CHUYỂN ĐỔI CHUẨN 100% CHO MYSQL & TIDB CLOUD)
-- =========================================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. Vai trò chuẩn (Roles)
INSERT INTO `roles` (`id`, `name`) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_COORDINATOR'),
(3, 'ROLE_RESCUER'),
(4, 'ROLE_CITIZEN')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 2. Người dùng mẫu (Mật khẩu mặc định: 123456)
INSERT INTO `users` (`id`, `username`, `password`, `full_name`, `phone`, `email`, `role_id`, `status`) VALUES
(1, 'admin', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Quản Trị Viên Hệ Thống', '0901112233', 'admin@cuunan247.vn', 1, 'ACTIVE'),
(2, 'coordinator', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Điều Phối Viên TT Chỉ Huy', '0902223344', 'coord@cuunan247.vn', 2, 'ACTIVE'),
(3, 'rescuer', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Nguyễn Văn Hùng (Hải Châu)', '0900000003', 'rescuer@cuunan247.vn', 3, 'ACTIVE'),
(4, 'cuuho1', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Nguyễn Văn Sơn (Sơn Trà)', '0905556677', 'cuuho1@cuunan247.vn', 3, 'ACTIVE'),
(5, 'rescuer_th01', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Lê Văn Thành (Thanh Hóa 01)', '0911001001', 'rescuer.th01@cuunan247.vn', 3, 'ACTIVE'),
(6, 'rescuer_na01', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Nguyễn Văn Nghĩa (Nghệ An 01)', '0912002001', 'rescuer.na01@cuunan247.vn', 3, 'ACTIVE'),
(7, 'rescuer_ht01', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Phạm Đình Tĩnh (Hà Tĩnh 01)', '0913003001', 'rescuer.ht01@cuunan247.vn', 3, 'ACTIVE'),
(8, 'rescuer_qt01', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Hoàng Văn Trị (Quảng Trị 01)', '0914004001', 'rescuer.qt01@cuunan247.vn', 3, 'ACTIVE'),
(9, 'rescuer_hue01', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Ngô Văn Huệ (Huế 01)', '0915005001', 'rescuer.hue01@cuunan247.vn', 3, 'ACTIVE'),
(10, 'rescuer_qn01', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Lê Văn Minh (Quảng Ngãi 01)', '0917007001', 'rescuer.qn01@cuunan247.vn', 3, 'ACTIVE'),
(11, 'rescuer_gl01', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Ksor Y Lai (Gia Lai 01)', '0918008001', 'rescuer.gl01@cuunan247.vn', 3, 'ACTIVE'),
(12, 'rescuer_dl01', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Y Krin Niê (Đắk Lắk 01)', '0919009001', 'rescuer.dl01@cuunan247.vn', 3, 'ACTIVE'),
(13, 'rescuer_kh01', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Trần Văn Hòa (Khánh Hòa 01)', '0920010001', 'rescuer.kh01@cuunan247.vn', 3, 'ACTIVE'),
(14, 'rescuer_ld01', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Trần Văn Dũng (Lâm Đồng 01)', '0921011001', 'rescuer.ld01@cuunan247.vn', 3, 'ACTIVE'),
(15, 'citizen_dn01', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Nguyễn Văn An', '0900000004', 'dan@cuunan247.vn', 4, 'ACTIVE'),
(16, 'citizen', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Nguyễn Hoàng Nam', '0900000015', 'citizen@cuunan247.vn', 4, 'ACTIVE'),
(17, 'citizen_dn02', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Trần Thị Mai', '0900000014', 'nguoidan1@cuunan247.vn', 4, 'ACTIVE'),
(18, 'citizen_th', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Nguyễn Thị Lan', '0901000101', 'citizen.th@cuunan247.vn', 4, 'ACTIVE'),
(19, 'citizen_na', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Lê Văn Hùng', '0901000102', 'citizen.na@cuunan247.vn', 4, 'ACTIVE'),
(20, 'citizen_ht', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Phạm Văn Nam', '0901000103', 'citizen.ht@cuunan247.vn', 4, 'ACTIVE'),
(21, 'citizen_qt', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Hoàng Thị Hương', '0901000104', 'citizen.qt@cuunan247.vn', 4, 'ACTIVE'),
(22, 'citizen_hue', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Trần Văn Minh', '0901000105', 'citizen.hue@cuunan247.vn', 4, 'ACTIVE'),
(23, 'citizen_qn', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Nguyễn Thị Hoa', '0901000106', 'citizen.qn@cuunan247.vn', 4, 'ACTIVE'),
(24, 'citizen_gl', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Võ Văn Bình', '0901000107', 'citizen.gl@cuunan247.vn', 4, 'ACTIVE'),
(25, 'citizen_dl', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Nguyễn Thị Hạnh', '0901000108', 'citizen.dl@cuunan247.vn', 4, 'ACTIVE'),
(26, 'citizen_kh', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Lê Thị Thảo', '0901000109', 'citizen.kh@cuunan247.vn', 4, 'ACTIVE'),
(27, 'citizen_ld', '$2a$10$yZFNmD6q21gAeJKXtGVUBO3LtnCsWfWWG4yU30/LvhT.ci1IfFrS.', 'Phạm Văn Long', '0901000110', 'citizen.ld@cuunan247.vn', 4, 'ACTIVE')
ON DUPLICATE KEY UPDATE `full_name` = VALUES(`full_name`), `phone` = VALUES(`phone`), `email` = VALUES(`email`), `role_id` = VALUES(`role_id`), `status` = VALUES(`status`);

-- 3. Đội cứu hộ mẫu (Rescue Teams)
INSERT INTO `rescue_teams` (`id`, `team_name`, `leader_id`, `contact_phone`, `current_latitude`, `current_longitude`, `member_count`, `equipment_notes`, `status`) VALUES
(1, 'Đội Cứu Hộ Hải Châu (Xuồng máy)', 3, '0900000003', 16.0678, 108.2208, 6, '2 xuồng cao su gắn máy, 15 áo phao, túi sơ cứu y tế', 'AVAILABLE'),
(2, 'Đội Cứu Hộ Thanh Hóa 01 (Sông Mã)', 5, '0911001001', 19.8074, 105.7764, 8, '2 xuồng máy, 20 áo phao, máy bơm cứu hộ', 'AVAILABLE'),
(3, 'Đội Cứu Hộ Huế 01 (Sông Hương & Tam Giang)', 9, '0915005001', 16.4637, 107.5909, 9, '3 canô cứu nạn cao tốc, bình oxy y tế', 'BUSY')
ON DUPLICATE KEY UPDATE `team_name` = VALUES(`team_name`), `leader_id` = VALUES(`leader_id`), `contact_phone` = VALUES(`contact_phone`), `current_latitude` = VALUES(`current_latitude`), `current_longitude` = VALUES(`current_longitude`), `member_count` = VALUES(`member_count`), `equipment_notes` = VALUES(`equipment_notes`), `status` = VALUES(`status`);

-- 4. Yêu cầu cứu nạn SOS mẫu (SOS Requests)
INSERT INTO `sos_requests` (`id`, `contact_name`, `contact_phone`, `latitude`, `longitude`, `address_detail`, `urgency_level`, `people_count`, `has_elderly`, `has_children`, `has_injured`, `description`, `status`) VALUES
(1, 'Trần Thị Bé', '0912345678', 16.0594, 108.1585, 'Số 45 đường Mẹ Suốt, P. Hòa Khánh Nam, Liên Chiểu, Đà Nẵng', 'CRITICAL', 4, 1, 1, 0, 'Nước ngập ngang ngực, có cụ già 80 tuổi và bé 3 tuổi, cần xuồng gấp!', 'IN_PROGRESS'),
(2, 'Lê Văn Hùng', '0988776655', 15.9982, 108.1965, 'Gần cầu Đỏ, Xã Hòa Châu, Hòa Vang, Đà Nẵng', 'HIGH', 3, 0, 0, 1, 'Bị cô lập do lũ cắt đứt đường, có 1 người bị thương ở chân.', 'DISPATCHED')
ON DUPLICATE KEY UPDATE `contact_name` = VALUES(`contact_name`), `contact_phone` = VALUES(`contact_phone`), `latitude` = VALUES(`latitude`), `longitude` = VALUES(`longitude`), `address_detail` = VALUES(`address_detail`), `urgency_level` = VALUES(`urgency_level`), `status` = VALUES(`status`);

-- 5. Cảnh báo thiên tai mẫu (Disaster Alerts)
INSERT INTO `disaster_alerts` (`id`, `title`, `content`, `severity`, `affected_region`, `is_broadcast`, `created_by`) VALUES
(1, 'Lũ khẩn cấp trên các lưu vực sông Miền Trung', 'Do ảnh hưởng của mưa lũ, mực nước các sông Hương, Thu Bồn, sông Mã dâng cao trên báo động 3.', 'DANGER_RED', 'Thanh Hóa, Nghệ An, Hà Tĩnh, Thừa Thiên Huế, Đà Nẵng, Quảng Ngãi', 1, 1)
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`), `content` = VALUES(`content`), `severity` = VALUES(`severity`), `affected_region` = VALUES(`affected_region`);

-- 6. Bài đăng cảnh báo cộng đồng mẫu (10 bài ảnh thực tế Cloudinary CDN)
INSERT INTO `community_posts` (`id`, `author_name`, `hazard_type`, `content`, `location_name`, `latitude`, `longitude`, `image_url`, `verification_status`, `upvote_count`, `comment_count`) VALUES
(1, 'Trần Thị Thu', 'LANDSLIDE', 'Vết nứt lớn xuất hiện trên sườn đồi phía sau trường tiểu học, đất bùn đã tràn ra đường đèo. Mọi người tránh khu vực này.', 'Xã Trà Mai, Quảng Nam', 15.2841, 108.0821, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328584/cuunan247/community_feed/V%E1%BA%BFt_n%E1%BB%A9t_l%E1%BB%9Bn_xu%E1%BA%A5t_hi%E1%BB%87n_tr%C3%AAn_s%C6%B0%E1%BB%9Dn_%C4%91%E1%BB%93i_ph%C3%ADa_sau_tr%C6%B0%E1%BB%9Dng_ti%E1%BB%83u_h%E1%BB%8Dc_%C4%91%E1%BA%A5t_b%C3%B9n_%C4%91%C3%A3_tr%C3%A0n_ra_%C4%91%C6%B0%E1%BB%9Dng_%C4%91%C3%A8o.webp', 'VERIFIED', 7, 3),
(2, 'Lê Minh Hoàng', 'FLOOD', 'Nước sông dâng nhanh, đã ngập tầng 1 dãy nhà ven sông. Có 3 người già chưa di chuyển được, cần xuồng gấp.', 'Phường Cẩm Phô, Hội An', 15.8782, 108.3243, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328592/cuunan247/community_feed/N%C6%B0%E1%BB%9Bc_s%C3%B4ng_d%C3%A2ng_nhanh_%C4%91%C3%A3_ng%E1%BA%ADp_t%E1%BA%A7ng_1_d%C3%A3y_nh%C3%A0_ven_s%C3%B4ng.jpg', 'PENDING', 8, 2),
(3, 'Nguyễn Văn An', 'FLOOD', 'Đường Mẹ Suốt đoạn gần cầu mương ngập sâu hơn 1m, nước chảy rất xiết, bà con tuyệt đối không nên di chuyển bằng xe máy!', 'Đường Mẹ Suốt, P. Hòa Khánh Nam, Liên Chiểu, Đà Nẵng', 16.0590, 108.1580, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328442/cuunan247/community_feed/%C4%90%C6%B0%E1%BB%9Dng_M%E1%BA%B9_Su%E1%BB%91t_%C4%91o%E1%BA%A1n_g%E1%BA%A7n_c%E1%BA%A7u_m%C6%B0%C6%A1ng_ng%E1%BA%ADp_s%C3%A2u_h%C6%A1n_1m_n%C6%B0%E1%BB%9Bc_ch%E1%BA%A3y_r%E1%BA%A5t_xi%E1%BA%BFt.jpg', 'VERIFIED', 28, 6),
(4, 'Trần Thị Mai', 'ISOLATED', 'Cầu tràn thôn Thạch Nham Tây xã Hòa Nhơn đang bị ngập 0.8m, cô lập 120 hộ dân, cần xuồng tiếp tế mì và nước uống.', 'Thôn Thạch Nham Tây, Xã Hòa Nhơn, Hòa Vang, Đà Nẵng', 16.0120, 108.1350, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328564/cuunan247/community_feed/C%E1%BA%A7u_tr%C3%A0n_th%C3%B4n_Th%E1%BA%A1ch_Nham_T%C3%A2y_x%C3%A3_H%C3%B2a_Nh%C6%A1n_%C4%91ang_b%E1%BB%8B_ng%E1%BA%ADp_0.8m.jpg', 'VERIFIED', 19, 4),
(5, 'Trần Văn Minh', 'FLOOD', 'Đường Bạch Đằng ven sông Hương nước tràn lên bờ đi vào nhà dân sâu 0.5m, đề nghị bà con dọn đồ lên gác lửng.', 'Đường Bạch Đằng, Phường Phú Cát, TP. Huế', 16.4720, 107.5920, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328566/cuunan247/community_feed/%C4%90%C6%B0%E1%BB%9Dng_B%E1%BA%A1ch_%C4%90%E1%BA%B1ng_ven_s%C3%B4ng_H%C6%B0%C6%A1ng_n%C6%B0%E1%BB%9Bc_tr%C3%A0n_l%C3%AAn_b%E1%BB%9D_%C4%91i_v%C3%A0o_nh%C3%A0_d%C3%A2n_s%C3%A2u_0.5m.jpg', 'VERIFIED', 35, 12),
(6, 'Phạm Văn Long', 'LANDSLIDE', 'Đèo Prenn đoạn Km225 xuất hiện vết nứt sườn taluy dương, có đất đá nhỏ rơi xuống mặt đường, các bác tài chú ý giảm tốc độ.', 'Đèo Prenn, Phường 3, TP. Đà Lạt, Lâm Đồng', 11.9150, 108.4450, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328569/cuunan247/community_feed/%C4%90%C3%A8o_Prenn_%C4%91o%E1%BA%A1n_Km225_xu%E1%BA%A5t_hi%E1%BB%87n_v%E1%BA%BFt_n%E1%BB%A9t_s%C6%B0%E1%BB%9Dn_taluy_d%C6%B0%C6%A1ng_c%C3%B3_%C4%91%E1%BA%A5t_%C4%91%C3%A1_nh%E1%BB%8F_r%C6%A1i_xu%E1%BB%91ng_m%E1%BA%B7t_%C4%91%C6%B0%E1%BB%9Dng.jpg', 'PENDING', 14, 2),
(7, 'Lê Văn Hùng', 'TREE_FALL', 'Cây phượng cổ thụ bị lốc xoáy quật đổ đè trúng cột điện hạ thế tại đường Nguyễn Du, gây mất điện toàn khu phố.', 'Đường Nguyễn Du, Phường Bến Thủy, TP. Vinh, Nghệ An', 18.6650, 105.6950, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328571/cuunan247/community_feed/C%C3%A2y_ph%C6%B0%E1%BB%A3ng_c%E1%BB%95_th%E1%BB%A5_b%E1%BB%8B_l%E1%BB%91c_xo%C3%A1y_qu%E1%BA%ADt_%C4%91%E1%BB%95_%C4%91%C3%A8_tr%C3%BAng_c%E1%BB%99t_%C4%91i%E1%BB%87n_h%E1%BA%A1_th%E1%BA%BF_t%E1%BA%A1i_%C4%91%C6%B0%E1%BB%9Dng_Nguy%E1%BB%85n_Du.jpg', 'VERIFIED', 22, 5),
(8, 'Lê Thị Thảo', 'STORM', 'Gió rít mạnh từng cơn giật cấp 9 tại ven biển Vĩnh Hải, sóng cao đánh tràn qua kè công viên, người dân không nên ra bờ biển.', 'Bờ kè Phạm Văn Đồng, Phường Vĩnh Hải, TP. Nha Trang, Khánh Hòa', 12.2850, 109.2050, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328573/cuunan247/community_feed/Gi%C3%B3_r%C3%ADt_m%E1%BA%A1nh_t%E1%BB%ABng_c%C6%A1n_gi%E1%BA%ADt_c%E1%BA%A5p_9_t%E1%BA%A1i_ven_bi%E1%BB%83n_V%C4%A9nh_H%E1%BA%A3i.jpg', 'PENDING', 11, 1),
(9, 'Võ Văn Bình', 'OTHER', 'Tin đồn vỡ đập thủy điện trên sông Ba là tin giả mạo, chính quyền địa phương thông báo đập vẫn vận hành xả lũ an toàn.', 'Huyện Krông Pa, Tỉnh Gia Lai', 13.2100, 108.6800, NULL, 'REJECTED', 3, 8),
(10, 'Đội Cứu Hộ Tình Nguyện Miền Trung', 'FLOOD', 'Đội đã điều động 2 xuồng máy cao su tiếp cận khu vực ngập sâu tại thôn An Lạc, đã đưa được 5 hộ dân và người già đến nhà văn hóa an toàn.', 'Thôn An Lạc, Xã Duy Vinh, Duy Xuyên, Quảng Nam', 15.8450, 108.3050, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328594/cuunan247/community_feed/Xu%E1%BB%93ng_cao_su_c%E1%BB%A9u_h%E1%BB%99_ti%E1%BA%BFp_c%E1%BA%ADn_v%C3%B9ng_ng%E1%BA%ADp_s%C3%A2u.jpg', 'VERIFIED', 52, 3)
ON DUPLICATE KEY UPDATE `author_name` = VALUES(`author_name`), `hazard_type` = VALUES(`hazard_type`), `content` = VALUES(`content`), `location_name` = VALUES(`location_name`), `image_url` = VALUES(`image_url`), `verification_status` = VALUES(`verification_status`);

-- 7. Bình luận mẫu cho bài đăng (Post Comments)
INSERT INTO `post_comments` (`id`, `post_id`, `user_id`, `author_name`, `author_avatar`, `content`, `created_at`) VALUES
(1, 1, NULL, 'Nguyễn Văn Hùng', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=120&q=80', 'Bà con xóm 2 chú ý tuyệt đối không đi đường tắt qua khe suối nhé, đất đá trên đồi vẫn đang có dấu hiệu trượt tiếp!', DATE_SUB(NOW(), INTERVAL 120 MINUTE)),
(2, 1, NULL, 'Trần Đình Điều Phối', 'https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&w=120&q=80', 'Đội dân quân xã và lực lượng công an đã chốt chặn hai đầu ngõ rồi ạ. Đề nghị bà con bình tĩnh chờ lệnh sơ tán.', DATE_SUB(NOW(), INTERVAL 95 MINUTE)),
(3, 1, NULL, 'Lê Thị Mai', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=120&q=80', 'Cầu mong không có ai bị thương, nhìn vết nứt mà thót cả tim! Mọi người cố gắng giữ an toàn nhé.', DATE_SUB(NOW(), INTERVAL 70 MINUTE)),
(4, 2, NULL, 'Võ Thị Mỹ Linh', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=120&q=80', 'Có gia đình bà Bảy ở số 28 Nguyễn Thị Minh Khai đang bị kẹt trên gác lửng, nước lên sát sàn rồi, đội ca nô ghé đón với ạ!', DATE_SUB(NOW(), INTERVAL 110 MINUTE)),
(5, 2, NULL, 'Đội Cứu Hộ Đà Nẵng', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=120&q=80', 'Đội 02 đã tiếp nhận thông tin nhà bà Bảy, xuồng máy đang rẽ vào hẻm tiếp cận ngay đây!', DATE_SUB(NOW(), INTERVAL 85 MINUTE)),
(6, 3, NULL, 'Phạm Văn Bình', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=120&q=80', 'Điện lực Liên Chiểu đã cắt điện trạm biến áp từ 15 phút trước rồi, bà con an tâm nhưng vẫn tránh xa dây chùng nhé.', DATE_SUB(NOW(), INTERVAL 100 MINUTE)),
(7, 3, NULL, 'Nguyễn Thị Đào', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=120&q=80', 'Cảm ơn các bác thợ điện đã xử lý kịp thời, nước ngập sâu quá!', DATE_SUB(NOW(), INTERVAL 75 MINUTE)),
(8, 4, NULL, 'Trần Anh Tuấn', 'https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=120&q=80', 'Cầu tràn Thạch Nham Tây nước chảy xiết lắm, xe máy đừng cố vượt!', DATE_SUB(NOW(), INTERVAL 130 MINUTE)),
(9, 4, NULL, 'Lê Văn Thành', 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&w=120&q=80', 'Đội cứu trợ đã chuyển xong 100 thùng mì tôm cho bà con xóm Trong rồi.', DATE_SUB(NOW(), INTERVAL 105 MINUTE)),
(10, 10, NULL, 'Trần Anh Tuấn', 'https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=120&q=80', 'Tuyệt vời quá các chiến sĩ ơi! Người nhà mình ở An Lạc vừa gọi điện báo đã được lên xuồng an toàn rồi.', DATE_SUB(NOW(), INTERVAL 130 MINUTE)),
(11, 10, NULL, 'Lê Văn Thành', 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&w=120&q=80', 'Ấm lòng mùa bão lũ. Chúc toàn đội nhiều sức khỏe và an toàn trên từng chuyến xuồng!', DATE_SUB(NOW(), INTERVAL 105 MINUTE)),
(12, 10, NULL, 'Bùi Thu Trang', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=120&q=80', 'Biết ơn các anh cứu hộ vô cùng, bà con nhớ mặc áo phao cẩn thận.', DATE_SUB(NOW(), INTERVAL 80 MINUTE))
ON DUPLICATE KEY UPDATE `content` = VALUES(`content`), `author_name` = VALUES(`author_name`);

-- 8. Phản ứng icon cảm xúc mẫu (Reactions / post_upvotes)
INSERT IGNORE INTO `post_upvotes` (`post_id`, `user_id`, `reaction_type`, `created_at`) VALUES
(1, 101, 'WARNING', NOW()),
(1, 102, 'PRAY', NOW()),
(1, 103, 'LIKE', NOW()),
(1, 104, 'CARE', NOW()),
(1, 105, 'SAD', NOW()),
(2, 106, 'PRAY', NOW()),
(2, 107, 'CARE', NOW()),
(2, 108, 'LIKE', NOW()),
(2, 109, 'WARNING', NOW()),
(3, 110, 'WARNING', NOW()),
(3, 111, 'LIKE', NOW()),
(3, 112, 'CARE', NOW()),
(4, 113, 'LIKE', NOW()),
(4, 114, 'CARE', NOW()),
(4, 115, 'PRAY', NOW()),
(4, 116, 'LIKE', NOW()),
(5, 117, 'CARE', NOW()),
(5, 118, 'LIKE', NOW()),
(5, 119, 'PRAY', NOW()),
(6, 120, 'LIKE', NOW()),
(6, 121, 'CARE', NOW()),
(6, 122, 'WARNING', NOW());

SET FOREIGN_KEY_CHECKS = 1;
SELECT '>>> ĐÃ NẠP THÀNH CÔNG 100% SEED DATA MỚI NHẤT VÀO TIDB CLOUD!' AS `status`;