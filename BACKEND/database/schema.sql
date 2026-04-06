-- ============================================================
-- ULMIP - Unified Life Management Intelligence Platform
-- MySQL Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS ulmip_db;
USE ulmip_db;

-- ============================================================
-- USERS TABLE
-- ============================================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(500),
    timezone VARCHAR(50) DEFAULT 'UTC',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================
-- TASKS TABLE
-- ============================================================
CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status ENUM('TODO', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'TODO',
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') DEFAULT 'MEDIUM',
    auto_priority_score DECIMAL(5,2) DEFAULT 0.0,
    category VARCHAR(100),
    due_date DATETIME,
    completed_at TIMESTAMP NULL,
    estimated_minutes INT DEFAULT 30,
    actual_minutes INT,
    tags JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_status (user_id, status),
    INDEX idx_due_date (due_date)
);

-- ============================================================
-- EVENTS / SCHEDULE TABLE
-- ============================================================
CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_type ENUM('MEETING', 'STUDY', 'EXERCISE', 'PERSONAL', 'REMINDER', 'OTHER') DEFAULT 'OTHER',
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    is_recurring BOOLEAN DEFAULT FALSE,
    recurrence_rule VARCHAR(200),
    location VARCHAR(300),
    color VARCHAR(20) DEFAULT '#6366f1',
    reminder_minutes INT DEFAULT 15,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_time (user_id, start_time)
);

-- ============================================================
-- HEALTH TRACKING TABLE
-- ============================================================
CREATE TABLE health_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    log_date DATE NOT NULL,
    sleep_hours DECIMAL(4,2),
    sleep_quality TINYINT CHECK (sleep_quality BETWEEN 1 AND 10),
    water_ml INT DEFAULT 0,
    calories_intake INT,
    calories_burned INT,
    steps INT DEFAULT 0,
    exercise_minutes INT DEFAULT 0,
    exercise_type VARCHAR(100),
    heart_rate_avg INT,
    mood TINYINT CHECK (mood BETWEEN 1 AND 10),
    energy_level TINYINT CHECK (energy_level BETWEEN 1 AND 10),
    stress_level TINYINT CHECK (stress_level BETWEEN 1 AND 10),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_date (user_id, log_date),
    INDEX idx_user_date (user_id, log_date)
);

-- ============================================================
-- FINANCE TRACKING TABLE
-- ============================================================
CREATE TABLE finance_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type ENUM('INCOME', 'EXPENSE') NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    category VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    transaction_date DATE NOT NULL,
    payment_method VARCHAR(100),
    is_recurring BOOLEAN DEFAULT FALSE,
    tags JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_type_date (user_id, type, transaction_date)
);

-- ============================================================
-- FINANCE BUDGETS TABLE
-- ============================================================
CREATE TABLE finance_budgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category VARCHAR(100) NOT NULL,
    monthly_limit DECIMAL(12,2) NOT NULL,
    month_year DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_budget (user_id, category, month_year)
);

-- ============================================================
-- GOALS TABLE
-- ============================================================
CREATE TABLE goals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category ENUM('ACADEMIC', 'HEALTH', 'FINANCE', 'CAREER', 'PERSONAL', 'SKILL') DEFAULT 'PERSONAL',
    target_value DECIMAL(12,2),
    current_value DECIMAL(12,2) DEFAULT 0,
    unit VARCHAR(50),
    target_date DATE,
    status ENUM('ACTIVE', 'COMPLETED', 'PAUSED', 'CANCELLED') DEFAULT 'ACTIVE',
    milestones JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================
-- LEARNING / COURSES TABLE
-- ============================================================
CREATE TABLE learning_courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    platform VARCHAR(100),
    category VARCHAR(100),
    total_modules INT DEFAULT 1,
    completed_modules INT DEFAULT 0,
    total_hours DECIMAL(6,2),
    spent_hours DECIMAL(6,2) DEFAULT 0,
    status ENUM('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED', 'PAUSED') DEFAULT 'NOT_STARTED',
    start_date DATE,
    target_completion_date DATE,
    actual_completion_date DATE,
    certificate_url VARCHAR(500),
    rating TINYINT CHECK (rating BETWEEN 1 AND 5),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================
-- AI SUGGESTIONS TABLE
-- ============================================================
CREATE TABLE ai_suggestions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    suggestion_type ENUM('PRODUCTIVITY', 'HEALTH', 'FINANCE', 'LEARNING', 'STRESS', 'SCHEDULE') NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    priority TINYINT DEFAULT 5,
    is_read BOOLEAN DEFAULT FALSE,
    is_acted_upon BOOLEAN DEFAULT FALSE,
    valid_until TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_unread (user_id, is_read)
);

-- ============================================================
-- FOCUS SESSIONS TABLE
-- ============================================================
CREATE TABLE focus_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    task_id BIGINT,
    session_type ENUM('POMODORO', 'DEEP_WORK', 'QUICK', 'CUSTOM') DEFAULT 'POMODORO',
    planned_minutes INT NOT NULL,
    actual_minutes INT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    productivity_score TINYINT CHECK (productivity_score BETWEEN 1 AND 10),
    distractions INT DEFAULT 0,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE SET NULL
);

-- ============================================================
-- USER SETTINGS TABLE
-- ============================================================
CREATE TABLE user_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    daily_work_hours INT DEFAULT 8,
    work_start_time TIME DEFAULT '09:00:00',
    work_end_time TIME DEFAULT '17:00:00',
    pomodoro_duration INT DEFAULT 25,
    short_break_duration INT DEFAULT 5,
    long_break_duration INT DEFAULT 15,
    water_goal_ml INT DEFAULT 2000,
    sleep_goal_hours DECIMAL(4,2) DEFAULT 8.0,
    step_goal INT DEFAULT 10000,
    calorie_goal INT DEFAULT 2000,
    monthly_budget DECIMAL(12,2) DEFAULT 50000.00,
    notification_enabled BOOLEAN DEFAULT TRUE,
    ai_suggestions_enabled BOOLEAN DEFAULT TRUE,
    theme VARCHAR(20) DEFAULT 'dark',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================
-- SAMPLE DATA
-- ============================================================
INSERT INTO users (name, email, password, timezone) VALUES
('Arjun Sharma', 'arjun@ulmip.ai', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lMIu', 'Asia/Kolkata'),
('Priya Patel', 'priya@ulmip.ai', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lMIu', 'Asia/Kolkata');
-- Default password: Password@123

INSERT INTO user_settings (user_id) VALUES (1), (2);

INSERT INTO tasks (user_id, title, description, status, priority, category, due_date, estimated_minutes) VALUES
(1, 'Complete ULMIP Project Proposal', 'Write IEEE-style abstract and submit form', 'IN_PROGRESS', 'CRITICAL', 'Academic', DATE_ADD(NOW(), INTERVAL 2 DAY), 120),
(1, 'Study Data Structures - Trees', 'Complete chapter 8 exercises', 'TODO', 'HIGH', 'Academic', DATE_ADD(NOW(), INTERVAL 1 DAY), 90),
(1, 'Morning Workout', '30 min cardio + stretching', 'COMPLETED', 'MEDIUM', 'Health', NOW(), 40),
(1, 'Review Monthly Budget', 'Track expenses for February', 'TODO', 'MEDIUM', 'Finance', DATE_ADD(NOW(), INTERVAL 5 DAY), 30),
(1, 'Read Clean Code Book', 'Chapters 5-7', 'IN_PROGRESS', 'LOW', 'Learning', DATE_ADD(NOW(), INTERVAL 10 DAY), 60);

INSERT INTO events (user_id, title, event_type, start_time, end_time, color) VALUES
(1, 'Team Standup', 'MEETING', DATE_ADD(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 2 HOUR), '#6366f1'),
(1, 'Deep Work: ULMIP Coding', 'STUDY', DATE_ADD(NOW(), INTERVAL 3 HOUR), DATE_ADD(NOW(), INTERVAL 6 HOUR), '#10b981'),
(1, 'Gym Session', 'EXERCISE', DATE_ADD(NOW(), INTERVAL 8 HOUR), DATE_ADD(NOW(), INTERVAL 9 HOUR), '#f59e0b');

INSERT INTO health_logs (user_id, log_date, sleep_hours, sleep_quality, water_ml, steps, exercise_minutes, mood, energy_level, stress_level) VALUES
(1, CURDATE(), 7.5, 7, 1500, 6500, 35, 7, 6, 4),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 6.0, 5, 1200, 4200, 0, 5, 5, 7),
(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 8.0, 8, 2100, 9800, 45, 8, 8, 3);

INSERT INTO finance_transactions (user_id, type, amount, category, description, transaction_date) VALUES
(1, 'INCOME', 25000.00, 'Salary', 'Monthly stipend', CURDATE()),
(1, 'EXPENSE', 8500.00, 'Rent', 'Monthly rent', CURDATE()),
(1, 'EXPENSE', 2300.00, 'Food', 'Groceries and dining', CURDATE()),
(1, 'EXPENSE', 999.00, 'Learning', 'Udemy course subscription', CURDATE()),
(1, 'EXPENSE', 500.00, 'Health', 'Gym membership', CURDATE());

INSERT INTO goals (user_id, title, category, target_value, current_value, unit, target_date) VALUES
(1, 'Complete 10 Projects This Year', 'ACADEMIC', 10, 3, 'projects', DATE_ADD(CURDATE(), INTERVAL 9 MONTH)),
(1, 'Lose 5kg', 'HEALTH', 5, 1.5, 'kg', DATE_ADD(CURDATE(), INTERVAL 3 MONTH)),
(1, 'Save ₹1,00,000', 'FINANCE', 100000, 28000, 'INR', DATE_ADD(CURDATE(), INTERVAL 6 MONTH));

INSERT INTO learning_courses (user_id, title, platform, category, total_modules, completed_modules, total_hours, spent_hours, status) VALUES
(1, 'Spring Boot Microservices', 'Udemy', 'Backend', 24, 14, 18.5, 11.0, 'IN_PROGRESS'),
(1, 'React Advanced Patterns', 'Coursera', 'Frontend', 16, 16, 12.0, 12.0, 'COMPLETED'),
(1, 'Machine Learning A-Z', 'edX', 'AI/ML', 32, 8, 28.0, 7.5, 'IN_PROGRESS');

INSERT INTO ai_suggestions (user_id, suggestion_type, title, content, priority) VALUES
(1, 'STRESS', 'High Stress Detected', 'Your workload has increased 40% this week. Consider taking a 15-min break every 2 hours and delegating 2 low-priority tasks.', 9),
(1, 'HEALTH', 'Hydration Alert', 'You have only consumed 1500ml of water today. Drink 2 glasses before your next focus session.', 7),
(1, 'PRODUCTIVITY', 'Peak Focus Window', 'Based on your patterns, 9 AM - 12 PM is your most productive window. Schedule your ULMIP coding session now.', 8),
(1, 'FINANCE', 'Budget on Track', 'You have spent 47% of your monthly budget with 16 days remaining. Great financial discipline!', 5);
