-- docsx MySQL Schema
-- @Author Cursor
-- @Date 2026-07-23
-- @Version 1.0

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    nick_name VARCHAR(64),
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS app (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    app_name VARCHAR(128) NOT NULL,
    app_key VARCHAR(64) NOT NULL UNIQUE,
    app_secret VARCHAR(128) NOT NULL,
    status TINYINT DEFAULT 1,
    description VARCHAR(512),
    callback_url VARCHAR(512),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS compare_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(64) NOT NULL UNIQUE,
    app_key VARCHAR(64),
    status VARCHAR(20) DEFAULT 'PENDING',
    doc_type VARCHAR(20) DEFAULT 'WORD',
    file_name1 VARCHAR(256),
    file_name2 VARCHAR(256),
    file1_path VARCHAR(512),
    file2_path VARCHAR(512),
    result_json LONGTEXT,
    similarity DECIMAL(10,6),
    error_message VARCHAR(2048),
    callback_url VARCHAR(512),
    expires_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    started_at DATETIME,
    completed_at DATETIME,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_task_session(session_id),
    INDEX idx_task_status(status),
    INDEX idx_task_app_key(app_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS font (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    font_name VARCHAR(128) NOT NULL,
    font_family VARCHAR(128),
    file_path VARCHAR(512) NOT NULL,
    file_size BIGINT,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始管理员由 DataInitializer 在代码中初始化
