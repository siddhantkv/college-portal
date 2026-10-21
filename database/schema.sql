-- ==========================================================
-- College Communication Portal - Database Schema
-- Database: MySQL 8.0+
-- ==========================================================

-- 1. Create Database if not exists
CREATE DATABASE IF NOT EXISTS college_portal;
USE college_portal;

-- 2. Departments Table
CREATE TABLE IF NOT EXISTS departments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE
);

-- 3. Users Table (Admin, Teacher, Student)
-- Status: 'PENDING' (for new teachers), 'APPROVED' (students & verified teachers), 'REJECTED'
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,            -- BCrypt hashed password
    role ENUM('ADMIN', 'TEACHER', 'STUDENT') NOT NULL,
    department_id INT NULL,                    -- NULL for Admin, required for Teacher & Student
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'APPROVED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
);

-- 4. Notices Table (Created by Teachers)
CREATE TABLE IF NOT EXISTS notices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    teacher_id INT NOT NULL,
    department_id INT NOT NULL,
    attachment_name VARCHAR(255) NULL,         -- Optional file/image attachment name
    original_attachment_name VARCHAR(255) NULL,-- Original filename
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE
);

-- 5. Files Table (Uploaded by Teachers)
CREATE TABLE IF NOT EXISTS files (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    file_name VARCHAR(255) NOT NULL,           -- Unique stored file name on disk
    original_name VARCHAR(255) NOT NULL,       -- Original uploaded file name
    file_size BIGINT NOT NULL,                 -- File size in bytes
    file_type VARCHAR(100) NOT NULL,           -- MIME type or extension
    teacher_id INT NOT NULL,
    department_id INT NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE
);

-- 6. Queries Table (Student doubts to Teachers)
CREATE TABLE IF NOT EXISTS queries (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    teacher_id INT NOT NULL,
    department_id INT NOT NULL,
    question TEXT NOT NULL,
    answer TEXT NULL,                          -- Filled when teacher replies
    status ENUM('PENDING', 'ANSWERED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    answered_at TIMESTAMP NULL,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE
);

-- ==========================================================
-- Sample Data Insertion
-- ==========================================================

-- Insert Departments
INSERT IGNORE INTO departments (id, name, code) VALUES
(1, 'Master of Computer Applications', 'MCA'),
(2, 'Bachelor of Computer Applications', 'BCA'),
(3, 'Bachelor of Technology', 'B.Tech'),
(4, 'Master of Business Administration', 'MBA');

-- Insert Default Admin Account
-- Email: admin@college.com
-- Password: admin123 (BCrypt hash)
INSERT IGNORE INTO users (id, name, email, password, role, department_id, status) VALUES
(1, 'System Administrator', 'admin@college.com', '$2a$12$m58cpt3NvoUPeVNfYgWy7.bFpVun0E64Ceollio.3B27e7kWlmiHK', 'ADMIN', NULL, 'APPROVED');
