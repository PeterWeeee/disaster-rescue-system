-- =========================================================================
-- ĐỒ ÁN LẬP TRÌNH WEB - NHÓM 04
-- DỰ ÁN: HỆ THỐNG CỨU NẠN THIÊN TAI (CỨUNẠN 24/7)
-- FILE: 01_create_database.sql
-- MỤC ĐÍCH: TẠO CƠ SỞ DỮ LIỆU TRÊN MICROSOFT SQL SERVER
-- =========================================================================

-- 1. Kiểm tra và tạo Database nếu chưa tồn tại
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = N'cuunan_db')
BEGIN
    CREATE DATABASE [cuunan_db]
    COLLATE Vietnamese_CI_AS; -- Hỗ trợ tiếng Việt đầy đủ
    PRINT N'>>> Đã tạo thành công Database cuunan_db!';
END
ELSE
BEGIN
    PRINT N'>>> Database cuunan_db đã tồn tại sẵn.';
END
GO

-- 2. Chuyển ngữ cảnh làm việc sang Database cuunan_db
USE [cuunan_db];
GO
