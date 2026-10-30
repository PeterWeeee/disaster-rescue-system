-- =========================================================================
-- ĐỒ ÁN LẬP TRÌNH WEB - NHÓM 04
-- DỰ ÁN: HỆ THỐNG CỨU NẠN THIÊN TAI (CỨUNẠN 24/7)
-- FILE: mysql_init.sql
-- MỤC ĐÍCH: TẠO DATABASE, 11 BẢNG CHUẨN VÀ SEED DATA DÀNH CHO MYSQL
-- =========================================================================

-- 1. Tạo Database
CREATE DATABASE IF NOT EXISTS `cuunan_db` 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `cuunan_db`;

-- 2. Bảng Vai trò (roles)
CREATE TABLE IF NOT EXISTS `roles` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Bảng Người dùng (users)
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(150) NOT NULL,
    `phone` VARCHAR(20) NOT NULL,
    `email` VARCHAR(100) NULL,
    `address` VARCHAR(255) NULL,
    `avatar_url` VARCHAR(500) NULL,
    `role_id` INT NULL,
    `status` VARCHAR(30) DEFAULT 'ACTIVE',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_users_roles` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Bảng Đội cứu hộ (rescue_teams)
CREATE TABLE IF NOT EXISTS `rescue_teams` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `team_name` VARCHAR(150) NOT NULL,
    `leader_id` BIGINT NULL,
    `contact_phone` VARCHAR(20) NOT NULL,
    `current_latitude` DOUBLE NULL,
    `current_longitude` DOUBLE NULL,
    `member_count` INT DEFAULT 0,
    `equipment_notes` TEXT NULL,
    `status` VARCHAR(30) DEFAULT 'AVAILABLE',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_rescue_teams_leader` FOREIGN KEY (`leader_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Bảng Thành viên đội cứu hộ (team_members)
CREATE TABLE IF NOT EXISTS `team_members` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `team_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `member_role` VARCHAR(50) DEFAULT 'RESCUER',
    `current_lat` DOUBLE NULL,
    `current_lng` DOUBLE NULL,
    `joined_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_team_members_team` FOREIGN KEY (`team_id`) REFERENCES `rescue_teams` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_team_members_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Bảng Yêu cầu cứu nạn khẩn cấp (sos_requests)
CREATE TABLE IF NOT EXISTS `sos_requests` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `sender_id` BIGINT NULL,
    `contact_name` VARCHAR(150) NOT NULL,
    `contact_phone` VARCHAR(20) NOT NULL,
    `secondary_phone` VARCHAR(20) NULL,
    `latitude` DOUBLE NOT NULL,
    `longitude` DOUBLE NOT NULL,
    `address_detail` VARCHAR(255) NULL,
    `urgency_level` VARCHAR(30) NOT NULL,
    `people_count` INT NOT NULL DEFAULT 1,
    `has_elderly` BOOLEAN DEFAULT FALSE,
    `has_children` BOOLEAN DEFAULT FALSE,
    `has_injured` BOOLEAN DEFAULT FALSE,
    `description` TEXT NULL,
    `image_url` VARCHAR(500) NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `resolved_at` DATETIME NULL,
    CONSTRAINT `fk_sos_sender` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
    INDEX `idx_sos_status` (`status`),
    INDEX `idx_sos_urgency` (`urgency_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Bảng Phân công nhiệm vụ cứu hộ (rescue_assignments)
CREATE TABLE IF NOT EXISTS `rescue_assignments` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `sos_request_id` BIGINT NOT NULL,
    `team_id` BIGINT NOT NULL,
    `assigned_by` BIGINT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    `rejection_reason` VARCHAR(255) NULL,
    `assigned_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `accepted_at` DATETIME NULL,
    `completed_at` DATETIME NULL,
    `report_notes` TEXT NULL,
    `report_image_url` VARCHAR(500) NULL,
  `vehicle_type` VARCHAR(50) NULL,
  `selected_equipment` TEXT NULL,
  `terrain_type` VARCHAR(50) NULL,
  `route_distance_km` DOUBLE NULL,
  `route_duration_minutes` INT NULL,
    CONSTRAINT `fk_assign_sos` FOREIGN KEY (`sos_request_id`) REFERENCES `sos_requests` (`id`),
    CONSTRAINT `fk_assign_team` FOREIGN KEY (`team_id`) REFERENCES `rescue_teams` (`id`),
    CONSTRAINT `fk_assign_by` FOREIGN KEY (`assigned_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Bảng Cảnh báo thiên tai (disaster_alerts)
CREATE TABLE IF NOT EXISTS `disaster_alerts` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(200) NOT NULL,
    `content` TEXT NOT NULL,
    `severity` VARCHAR(30) NOT NULL,
    `affected_region` VARCHAR(255) NULL,
    `is_broadcast` BOOLEAN DEFAULT FALSE,
    `created_by` BIGINT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `expires_at` DATETIME NULL,
    CONSTRAINT `fk_alerts_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Bảng Điểm sơ tán, tạm lánh (evacuation_shelters)
CREATE TABLE IF NOT EXISTS `evacuation_shelters` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(200) NOT NULL,
    `address` VARCHAR(255) NULL,
    `latitude` DOUBLE NULL,
    `longitude` DOUBLE NULL,
    `total_capacity` INT DEFAULT 300,
    `current_occupancy` INT DEFAULT 0,
    `contact_phone` VARCHAR(30) NULL,
    `status` VARCHAR(30) DEFAULT 'OPEN',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. Bảng Bài đăng cộng đồng (community_posts)
CREATE TABLE IF NOT EXISTS `community_posts` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NULL,
    `author_name` VARCHAR(150) NOT NULL,
    `hazard_type` VARCHAR(50) NOT NULL DEFAULT 'OTHER',
    `content` TEXT NOT NULL,
    `location_name` VARCHAR(255) NULL,
    `latitude` DOUBLE NULL,
    `longitude` DOUBLE NULL,
    `image_url` VARCHAR(500) NULL,
    `verification_status` VARCHAR(30) DEFAULT 'PENDING',
    `upvote_count` INT DEFAULT 0,
    `comment_count` INT DEFAULT 0,
    `sos_request_id` BIGINT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_posts_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_posts_sos` FOREIGN KEY (`sos_request_id`) REFERENCES `sos_requests` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. Bảng Bình chọn bài đăng (post_upvotes)
CREATE TABLE IF NOT EXISTS `post_upvotes` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `post_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `reaction_type` VARCHAR(30) DEFAULT 'LIKE',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uq_post_user` (`post_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11b. Bàng Bình luận bài đăng (post_comments)
CREATE TABLE IF NOT EXISTS `post_comments` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `post_id` BIGINT NOT NULL,
    `user_id` BIGINT NULL,
    `author_name` VARCHAR(150) NOT NULL,
    `author_avatar` VARCHAR(255) NULL,
    `content` TEXT NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY `idx_comments_post` (`post_id`),
    CONSTRAINT `fk_comments_post` FOREIGN KEY (`post_id`) REFERENCES `community_posts` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. Bảng Tệp đính kèm (media_files)
CREATE TABLE IF NOT EXISTS `media_files` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `file_name` VARCHAR(255) NOT NULL,
    `original_name` VARCHAR(255) NULL,
    `file_url` VARCHAR(1000) NOT NULL,
    `file_size` BIGINT NULL,
    `content_type` VARCHAR(100) NULL,
    `storage_type` VARCHAR(30) DEFAULT 'LOCAL',
    `public_id` VARCHAR(255) NULL,
    `sos_id` BIGINT NULL,
    `uploaded_by` BIGINT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_media_sos` FOREIGN KEY (`sos_id`) REFERENCES `sos_requests` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_media_user` FOREIGN KEY (`uploaded_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. Bảng Thông báo người dùng (notifications)
CREATE TABLE IF NOT EXISTS `notifications` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `recipient_id` BIGINT NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `message` TEXT NOT NULL,
    `type` VARCHAR(50) DEFAULT 'SYSTEM',
    `target_url` VARCHAR(255) NULL,
    `is_read` TINYINT(1) DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_notifications_recipient` FOREIGN KEY (`recipient_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. Bảng Nhật ký hoạt động kiểm toán (activity_logs)
CREATE TABLE IF NOT EXISTS `activity_logs` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NULL,
    `action` VARCHAR(80) NOT NULL,
    `target_entity` VARCHAR(150) NULL,
    `details` TEXT NULL,
    `ip_address` VARCHAR(50) NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_activity_logs_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 14. Bảng Kho trang thiết bị cứu hộ (rescue_equipment)
CREATE TABLE IF NOT EXISTS `rescue_equipment` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `team_id` BIGINT NOT NULL,
    `equipment_code` VARCHAR(50) NOT NULL,
    `equipment_name` VARCHAR(150) NOT NULL,
    `category` VARCHAR(50) DEFAULT 'SAFETY',
    `unit` VARCHAR(30) DEFAULT 'chiếc',
    `total_quantity` INT DEFAULT 0,
    `available_quantity` INT DEFAULT 0,
    `dispatched_quantity` INT DEFAULT 0,
    `damaged_quantity` INT DEFAULT 0,
    `notes` TEXT NULL,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_rescue_equipment_team` FOREIGN KEY (`team_id`) REFERENCES `rescue_teams` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 15. Bảng Cấp phát trang thiết bị cho nhiệm vụ (equipment_assignments)
CREATE TABLE IF NOT EXISTS `equipment_assignments` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `rescue_assignment_id` BIGINT NOT NULL,
    `equipment_id` BIGINT NOT NULL,
    `quantity_dispatched` INT DEFAULT 0,
    `quantity_returned` INT DEFAULT 0,
    `quantity_damaged` INT DEFAULT 0,
    `quantity_lost` INT DEFAULT 0,
    `quantity_consumed` INT DEFAULT 0,
    `status` VARCHAR(30) DEFAULT 'DISPATCHED',
    `returned_at` DATETIME NULL,
    `notes` TEXT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_eq_assign_assignment` FOREIGN KEY (`rescue_assignment_id`) REFERENCES `rescue_assignments` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_eq_assign_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `rescue_equipment` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 16. Bảng Mã OTP xác thực email (otp_verifications)
CREATE TABLE IF NOT EXISTS `otp_verifications` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `email` VARCHAR(120) NOT NULL,
    `otp_code` VARCHAR(6) NOT NULL,
    `type` VARCHAR(30) NOT NULL, -- REGISTRATION, FORGOT_PASSWORD
    `expiry_time` DATETIME NOT NULL,
    `is_used` TINYINT(1) DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_otp_email_code` (`email`, `otp_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 17. Bảng Quyên góp cứu trợ bão lũ (donations)
CREATE TABLE IF NOT EXISTS `donations` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `donor_name` VARCHAR(100) DEFAULT 'Nhà hảo tâm ẩn danh',
    `donor_phone` VARCHAR(20) NULL,
    `donor_email` VARCHAR(120) NULL,
    `amount` DECIMAL(15,2) NOT NULL,
    `message` TEXT NULL,
    `target_province` VARCHAR(80) DEFAULT 'TOAN_MIEN_TRUNG',
    `payment_method` VARCHAR(30) NOT NULL, -- VNPAY, MOMO, VIETQR
    `transaction_code` VARCHAR(100) NOT NULL UNIQUE,
    `status` VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, SUCCESS, FAILED
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `paid_at` DATETIME NULL,
    INDEX `idx_donations_status` (`status`),
    INDEX `idx_donations_tx_code` (`transaction_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 18. Bảng Yêu cầu tiếp tế ngân sách nhu yếu phẩm (team_fund_requests)
CREATE TABLE IF NOT EXISTS `team_fund_requests` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `team_id` BIGINT NOT NULL,
    `requester_id` BIGINT NOT NULL,
    `amount` DECIMAL(15,2) NOT NULL,
    `purpose` VARCHAR(500) NOT NULL,
    `category` VARCHAR(50) DEFAULT 'NHU_YEU_PHAM',
    `urgency_level` VARCHAR(30) DEFAULT 'HIGH',
    `status` VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    `reviewed_by` BIGINT NULL,
    `reviewed_at` DATETIME NULL,
    `reject_reason` VARCHAR(500) NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_team_fund_team` FOREIGN KEY (`team_id`) REFERENCES `rescue_teams` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_team_fund_requester` FOREIGN KEY (`requester_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_team_fund_reviewer` FOREIGN KEY (`reviewed_by`) REFERENCES `users` (`id`) ON DELETE SET NULL,
    INDEX `idx_team_fund_status` (`status`),
    INDEX `idx_team_fund_team_id` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 19. Bảng Khung chat trực tuyến thời gian thực giữa nạn nhân SOS và đội cứu hộ (rescue_chat_messages)
CREATE TABLE IF NOT EXISTS rescue_chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sos_request_id BIGINT NOT NULL,
    assignment_id BIGINT NULL,
    sender_type VARCHAR(20) NOT NULL,
    sender_name VARCHAR(100) NOT NULL,
    sender_phone VARCHAR(20) NULL,
    content TEXT NOT NULL,
    message_type VARCHAR(30) DEFAULT 'TEXT',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_chat_sos (sos_request_id, created_at),
    CONSTRAINT fk_chat_sos FOREIGN KEY (sos_request_id) REFERENCES sos_requests (id) ON DELETE CASCADE,
    CONSTRAINT fk_chat_assignment FOREIGN KEY (assignment_id) REFERENCES rescue_assignments (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 20. Dữ liệu mẫu Bài đăng cộng đồng (community_posts) kèm ảnh Cloudinary CDN (10 bài ảnh thực tế miền Trung)
INSERT IGNORE INTO `community_posts` (`id`, `author_name`, `hazard_type`, `content`, `location_name`, `latitude`, `longitude`, `image_url`, `verification_status`, `upvote_count`, `comment_count`) VALUES
(1, 'Trần Thị Thu', 'LANDSLIDE', 'Vết nứt lớn xuất hiện trên sườn đồi phía sau trường tiểu học, đất bùn đã tràn ra đường đèo. Mọi người tránh khu vực này.', 'Xã Trà Mai, Quảng Nam', 15.2841, 108.0821, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328584/cuunan247/community_feed/V%E1%BA%BFt_n%E1%BB%A9t_l%E1%BB%9Bn_xu%E1%BA%A5t_hi%E1%BB%87n_tr%C3%AAn_s%C6%B0%E1%BB%9Dn_%C4%91%E1%BB%93i_ph%C3%ADa_sau_tr%C6%B0%E1%BB%9Dng_ti%E1%BB%83u_h%E1%BB%8Dc_%C4%91%E1%BA%A5t_b%C3%B9n_%C4%91%C3%A3_tr%C3%A0n_ra_%C4%91%C6%B0%E1%BB%9Dng_%C4%91%C3%A8o.webp', 'VERIFIED', 7, 3),
(2, 'Lê Minh Hoàng', 'FLOOD', 'Nước sông dâng nhanh, đã ngập tầng 1 dãy nhà ven sông. Có 3 người già chưa di chuyển được, cần xuồng gấp.', 'Phường Cẩm Phô, Hội An', 15.8782, 108.3243, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328592/cuunan247/community_feed/N%C6%B0%E1%BB%9Bc_s%C3%B4ng_d%C3%A2ng_nhanh_%C4%91%C3%A3_ng%E1%BA%ADp_t%E1%BA%A7ng_1_d%C3%A3y_nh%C3%A0_ven_s%C3%B4ng.jpg', 'PENDING', 8, 2),
(3, 'Nguyễn Văn An', 'FLOOD', 'Đường Mẹ Suốt đoạn gần cầu mương ngập sâu hơn 1m, nước chảy rất xiết, bà con tuyệt đối không nên di chuyển bằng xe máy!', 'Đường Mẹ Suốt, P. Hòa Khánh Nam, Liên Chiểu, Đà Nẵng', 16.0590, 108.1580, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328442/cuunan247/community_feed/%C4%90%C6%B0%E1%BB%9Dng_M%E1%BA%B9_Su%E1%BB%91t_%C4%91o%E1%BA%A1n_g%E1%BA%A7n_c%E1%BA%A7u_m%C6%B0%C6%A1ng_ng%E1%BA%ADp_s%C3%A2u_h%C6%A1n_1m_n%C6%B0%E1%BB%9Bc_ch%E1%BA%A3y_r%E1%BA%A5t_xi%E1%BA%BFt.jpg', 'VERIFIED', 28, 6),
(4, 'Trần Thị Mai', 'ISOLATED', 'Cầu tràn thôn Thạch Nham Tây xã Hòa Nhơn đang bị ngập 0.8m, cô lập 120 hộ dân, cần xuồng tiếp tế mì và nước uống.', 'Thôn Thạch Nham Tây, Xã Hòa Nhơn, Hòa Vang, Đà Nẵng', 16.0120, 108.1350, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328564/cuunan247/community_feed/C%E1%BA%A7u_tr%C3%A0n_th%C3%B4n_Th%E1%BA%A1ch_Nham_T%C3%A2y_x%C3%A3_H%C3%B2a_Nh%C6%A1n_%C4%91ang_b%E1%BB%8B_ng%E1%BA%ADp_0.8m.jpg', 'VERIFIED', 19, 4),
(5, 'Trần Văn Minh', 'FLOOD', 'Đường Bạch Đằng ven sông Hương nước tràn lên bờ đi vào nhà dân sâu 0.5m, đề nghị bà con dọn đồ lên gác lửng.', 'Đường Bạch Đằng, Phường Phú Cát, TP. Huế', 16.4720, 107.5920, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328566/cuunan247/community_feed/%C4%90%C6%B0%E1%BB%9Dng_B%E1%BA%A1ch_%C4%90%E1%BA%B1ng_ven_s%C3%B4ng_H%C6%B0%C6%A1ng_n%C6%B0%E1%BB%9Bc_tr%C3%A0n_l%C3%AAn_b%E1%BB%9D_%C4%91i_v%C3%A0o_nh%C3%A0_d%C3%A2n_s%C3%A2u_0.5m.jpg', 'VERIFIED', 35, 12),
(6, 'Phạm Văn Long', 'LANDSLIDE', 'Đèo Prenn đoạn Km225 xuất hiện vết nứt sườn taluy dương, có đất đá nhỏ rơi xuống mặt đường, các bác tài chú ý giảm tốc độ.', 'Đèo Prenn, Phường 3, TP. Đà Lạt, Lâm Đồng', 11.9150, 108.4450, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328569/cuunan247/community_feed/%C4%90%C3%A8o_Prenn_%C4%91o%E1%BA%A1n_Km225_xu%E1%BA%A5t_hi%E1%BB%87n_v%E1%BA%BFt_n%E1%BB%A9t_s%C6%B0%E1%BB%9Dn_taluy_d%C6%B0%C6%A1ng_c%C3%B3_%C4%91%E1%BA%A5t_%C4%91%C3%A1_nh%E1%BB%8F_r%C6%A1i_xu%E1%BB%91ng_m%E1%BA%B7t_%C4%91%C6%B0%E1%BB%9Dng.jpg', 'PENDING', 14, 2),
(7, 'Lê Văn Hùng', 'TREE_FALL', 'Cây phượng cổ thụ bị lốc xoáy quật đổ đè trúng cột điện hạ thế tại đường Nguyễn Du, gây mất điện toàn khu phố.', 'Đường Nguyễn Du, Phường Bến Thủy, TP. Vinh, Nghệ An', 18.6650, 105.6950, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328571/cuunan247/community_feed/C%C3%A2y_ph%C6%B0%E1%BB%A3ng_c%E1%BB%95_th%E1%BB%A5_b%E1%BB%8B_l%E1%BB%91c_xo%C3%A1y_qu%E1%BA%ADt_%C4%91%E1%BB%95_%C4%91%C3%A8_tr%C3%BAng_c%E1%BB%99t_%C4%91i%E1%BB%87n_h%E1%BA%A1_th%E1%BA%BF_t%E1%BA%A1i_%C4%91%C6%B0%E1%BB%9Dng_Nguy%E1%BB%85n_Du.jpg', 'VERIFIED', 22, 5),
(8, 'Lê Thị Thảo', 'STORM', 'Gió rít mạnh từng cơn giật cấp 9 tại ven biển Vĩnh Hải, sóng cao đánh tràn qua kè công viên, người dân không nên ra bờ biển.', 'Bờ kè Phạm Văn Đồng, Phường Vĩnh Hải, TP. Nha Trang, Khánh Hòa', 12.2850, 109.2050, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328573/cuunan247/community_feed/Gi%C3%B3_r%C3%ADt_m%E1%BA%A1nh_t%E1%BB%ABng_c%C6%A1n_gi%E1%BA%ADt_c%E1%BA%A5p_9_t%E1%BA%A1i_ven_bi%E1%BB%83n_V%C4%A9nh_H%E1%BA%A3i.jpg', 'PENDING', 11, 1),
(9, 'Võ Văn Bình', 'OTHER', 'Tin đồn vỡ đập thủy điện trên sông Ba là tin giả mạo, chính quyền địa phương thông báo đập vẫn vận hành xả lũ an toàn.', 'Huyện Krông Pa, Tỉnh Gia Lai', 13.2100, 108.6800, NULL, 'REJECTED', 3, 8),
(10, 'Đội Cứu Hộ Tình Nguyện Miền Trung', 'FLOOD', 'Đội đã điều động 2 xuồng máy cao su tiếp cận khu vực ngập sâu tại thôn An Lạc, đã đưa được 5 hộ dân và người già đến nhà văn hóa an toàn.', 'Thôn An Lạc, Xã Duy Vinh, Duy Xuyên, Quảng Nam', 15.8450, 108.3050, 'https://res.cloudinary.com/odwf5uoi/image/upload/v1793328594/cuunan247/community_feed/Xu%E1%BB%93ng_cao_su_c%E1%BB%A9u_h%E1%BB%99_ti%E1%BA%BFp_c%E1%BA%ADn_v%C3%B9ng_ng%E1%BA%ADp_s%C3%A2u.jpg', 'VERIFIED', 52, 3);

-- 21. Dữ liệu mẫu Bình luận bài đăng cộng đồng (post_comments)
INSERT IGNORE INTO `post_comments` (`id`, `post_id`, `user_id`, `author_name`, `author_avatar`, `content`, `created_at`) VALUES
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
(12, 10, NULL, 'Bùi Thu Trang', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=120&q=80', 'Biết ơn các anh cứu hộ vô cùng, bà con nhớ mặc áo phao cẩn thận.', DATE_SUB(NOW(), INTERVAL 80 MINUTE));

-- 22. Dữ liệu mẫu Phản ứng icon cảm xúc bài đăng (post_upvotes)
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
