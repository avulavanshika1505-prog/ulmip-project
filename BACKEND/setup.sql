-- ============================================================
-- ULMIP Database Setup Script
-- Run this in MySQL Workbench or MySQL CLI before starting the app
-- ============================================================

-- 1. Create database
CREATE DATABASE IF NOT EXISTS ulmip_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- 2. Use it
USE ulmip_db;

-- 3. Create a dedicated user (optional but recommended)
-- CREATE USER 'ulmip_user'@'localhost' IDENTIFIED BY 'ulmip_pass123';
-- GRANT ALL PRIVILEGES ON ulmip_db.* TO 'ulmip_user'@'localhost';
-- FLUSH PRIVILEGES;

-- ✅ Tables are created automatically by Spring Boot (ddl-auto=update)
-- This script just creates the database. Run the Spring Boot app after this.

SELECT 'Database ulmip_db ready! Now run the Spring Boot app.' AS STATUS;
