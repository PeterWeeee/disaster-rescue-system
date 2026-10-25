-- =========================================================================
-- ĐỒ ÁN LẬP TRÌNH WEB - NHÓM 04
-- DỰ ÁN: HỆ THỐNG CỨU NẠN THIÊN TAI (CỨUNẠN 24/7)
-- FILE: mysql_init.sql
-- MỤC ĐÍCH: TẠO DATABASE, TABLES VÀ SEED DATA DÀNH CHO MYSQL
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

-- 9. Nạp Dữ Liệu Mẫu (Seed Data)
INSERT IGNORE INTO `roles` (`name`) VALUES 
('ROLE_ADMIN'), ('ROLE_COORDINATOR'), ('ROLE_RESCUER'), ('ROLE_CITIZEN');

-- Nạp Users mẫu (Mật khẩu: 123456)
INSERT IGNORE INTO `users` (`username`, `password`, `full_name`, `phone`, `email`, `role_id`, `status`)
SELECT 'admin', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', 'Quản Trị Viên Hệ Thống', '0901112233', 'admin@cuunan247.vn', id, 'ACTIVE'
FROM `roles` WHERE `name` = 'ROLE_ADMIN' LIMIT 1;

INSERT IGNORE INTO `users` (`username`, `password`, `full_name`, `phone`, `email`, `role_id`, `status`)
SELECT 'dieuphoi01', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', 'Trần Điều Phối', '0902223344', 'coordinator01@cuunan247.vn', id, 'ACTIVE'
FROM `roles` WHERE `name` = 'ROLE_COORDINATOR' LIMIT 1;

INSERT IGNORE INTO `users` (`username`, `password`, `full_name`, `phone`, `email`, `role_id`, `status`)
SELECT 'doitruong01', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', 'Lê Văn Cứu Hộ', '0903334455', 'rescuer01@cuunan247.vn', id, 'ACTIVE'
FROM `roles` WHERE `name` = 'ROLE_RESCUER' LIMIT 1;

INSERT IGNORE INTO `users` (`username`, `password`, `full_name`, `phone`, `email`, `role_id`, `status`)
SELECT 'nguoidan01', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', 'Nguyễn Văn An', '0904445566', 'dan01@gmail.com', id, 'ACTIVE'
FROM `roles` WHERE `name` = 'ROLE_CITIZEN' LIMIT 1;

-- Nạp Đội cứu hộ mẫu
INSERT IGNORE INTO `rescue_teams` (`team_name`, `leader_id`, `contact_phone`, `current_latitude`, `current_longitude`, `member_count`, `equipment_notes`, `status`)
SELECT 'Đội Cứu Nạn Xuồng Cao Su Số 1', id, '0903334455', 16.0748, 108.2240, 6, '2 xuồng máy cao su, 15 áo phao cứu sinh, 2 phao tròn, túi cứu thương chuyên sâu', 'AVAILABLE'
FROM `users` WHERE `username` = 'doitruong01' LIMIT 1;

-- Nạp Yêu cầu cứu nạn SOS mẫu
INSERT IGNORE INTO `sos_requests` (`contact_name`, `contact_phone`, `latitude`, `longitude`, `address_detail`, `urgency_level`, `people_count`, `has_elderly`, `has_children`, `has_injured`, `description`, `status`)
VALUES 
('Bà Trần Thị Bích', '0988776655', 16.0678, 108.2140, 'Số 45 Đường Trưng Nữ Vương, ngập nước ngang ngực', 'CRITICAL', 4, 1, 1, 0, 'Nước lũ đang dâng cao gần tới gác lửng, nhà có 1 cụ già 82 tuổi liệt giường và bé sơ sinh 6 tháng tuổi. Cần xuồng tiếp cận gấp!', 'PENDING'),
('Nguyễn Hoàng Nam', '0977665544', 16.0544, 108.2022, 'Khu dân cư ven sông Cẩm Lệ', 'HIGH', 3, 0, 0, 1, 'Đang bị cô lập do ngập sâu 1.5m, một người bị ngã trẹo chân khi dọn đồ cần băng nẹp y tế và lương thực.', 'PENDING');

-- Nạp Cảnh báo thiên tai mẫu
INSERT IGNORE INTO `disaster_alerts` (`title`, `content`, `severity`, `affected_region`, `is_broadcast`, `created_by`)
SELECT 'Lũ khẩn cấp trên các lưu vực sông', 'Do ảnh hưởng của bão số 6, mực nước sông lên nhanh đạt mức báo động 3. Người dân các khu vực thấp trũng khẩn trương di dời tài sản và liên hệ lực lượng ứng trực qua hotline 112.', 'DANGER_RED', 'Thành phố Đà Nẵng, Tỉnh Quảng Nam, Tỉnh Thừa Thiên Huế', 1, id
FROM `users` WHERE `username` = 'admin' LIMIT 1;
