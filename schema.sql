-- Paperless World Database Schema
-- MySQL/MariaDB Database

-- Create database
CREATE DATABASE IF NOT EXISTS paperless_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE paperless_db;

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS annotations;
DROP TABLE IF EXISTS documents;
DROP TABLE IF EXISTS users;

-- Users table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'pending',
    access_level VARCHAR(50) DEFAULT 'public',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Documents table
CREATE TABLE documents (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    file_name VARCHAR(255),
    file_path VARCHAR(500),
    file_type VARCHAR(100),
    file_size BIGINT,
    uploader_id INT NOT NULL,
    category VARCHAR(100),
    tags VARCHAR(500),
    access_level VARCHAR(50) DEFAULT 'public',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (uploader_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_uploader (uploader_id),
    INDEX idx_category (category),
    INDEX idx_access_level (access_level),
    FULLTEXT INDEX idx_fulltext (title, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Annotations table
CREATE TABLE annotations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    document_id INT NOT NULL,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    page_number INT,
    position_x DOUBLE,
    position_y DOUBLE,
    annotation_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_document (document_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Feedback table
CREATE TABLE feedback (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    document_id INT,
    subject VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'pending',
    priority VARCHAR(50) DEFAULT 'normal',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE SET NULL,
    INDEX idx_user (user_id),
    INDEX idx_document (document_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert demo users (passwords are BCrypt hashed 'admin123', 'research123', 'public123')
INSERT INTO users (name, email, password, role, status, access_level) VALUES
('Admin User', 'admin@paperless.com', '$2a$10$b.dZT85qaJi3FXs11UesvuFy.D4De4I77YTK2Ebajj8J4oOnCwVxW', 'archivist', 'approved', 'full'),
('Research User', 'researcher@paperless.com', '$2a$10$pNOsoAoxblB7zSF3XutkguLf5sfdfyW3VQot3lfbn7EN7gv5ILabS', 'researcher', 'approved', 'restricted'),
('Public User', 'public@paperless.com', '$2a$10$3zUx4h1j493/o7oWZr/squ0ioJzfSNbcnASxr7xy5YICw4qfAjsLe', 'public', 'approved', 'public');

-- Insert sample document
INSERT INTO documents (title, description, file_name, file_type, uploader_id, category, access_level) VALUES
('Welcome Document', 'Welcome to Paperless World Digital Archive System', 'welcome.pdf', 'application/pdf', 1, 'general', 'public');

-- Insert sample annotation
INSERT INTO annotations (document_id, user_id, content, page_number, annotation_type) VALUES
(1, 2, 'This is a sample annotation by researcher', 1, 'note');

-- Insert sample feedback
INSERT INTO feedback (user_id, document_id, subject, message, status) VALUES
(3, 1, 'Great system!', 'This digital archive system is very user-friendly.', 'pending');

-- View to check user statistics
CREATE VIEW user_stats AS
SELECT 
    u.id,
    u.name,
    u.email,
    u.role,
    COUNT(DISTINCT d.id) as documents_uploaded,
    COUNT(DISTINCT a.id) as annotations_created,
    COUNT(DISTINCT f.id) as feedback_submitted
FROM users u
LEFT JOIN documents d ON u.id = d.uploader_id
LEFT JOIN annotations a ON u.id = a.user_id
LEFT JOIN feedback f ON u.id = f.user_id
GROUP BY u.id, u.name, u.email, u.role;

-- Procedure to get document with annotations
DELIMITER //
CREATE PROCEDURE GetDocumentWithAnnotations(IN doc_id INT)
BEGIN
    SELECT d.*, u.name as uploader_name
    FROM documents d
    JOIN users u ON d.uploader_id = u.id
    WHERE d.id = doc_id;
    
    SELECT a.*, u.name as annotator_name
    FROM annotations a
    JOIN users u ON a.user_id = u.id
    WHERE a.document_id = doc_id
    ORDER BY a.created_at DESC;
END //
DELIMITER ;

-- Show tables
SHOW TABLES;

-- Display initial data
SELECT 'Users:' as Info;
SELECT id, name, email, role, status FROM users;

SELECT 'Documents:' as Info;
SELECT id, title, category, access_level FROM documents;

SELECT 'Schema created successfully!' as Status;
