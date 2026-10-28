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
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uq_post_user` (`post_id`, `user_id`)
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
