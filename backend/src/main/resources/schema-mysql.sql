CREATE TABLE IF NOT EXISTS app (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    app_id      VARCHAR(64) UNIQUE NOT NULL,
    app_name    VARCHAR(128) NOT NULL,
    app_secret  VARCHAR(128) NOT NULL,
    status      TINYINT DEFAULT 1,
    remark      VARCHAR(500),
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS compare_task (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id      VARCHAR(64) UNIQUE NOT NULL,
    session_id   VARCHAR(64) UNIQUE NOT NULL,
    app_id       VARCHAR(64) NOT NULL,
    status       VARCHAR(20) DEFAULT 'pending',
    doc_type     VARCHAR(20) DEFAULT 'word',
    file_a_name  VARCHAR(255),
    file_b_name  VARCHAR(255),
    file_a_path  VARCHAR(500),
    file_b_path  VARCHAR(500),
    result_json  LONGTEXT,
    similarity   DECIMAL(5,4),
    error_msg    VARCHAR(1000),
    callback_url VARCHAR(500),
    expires_at   DATETIME,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    started_at   DATETIME,
    completed_at DATETIME
);

CREATE TABLE IF NOT EXISTS font (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    font_name   VARCHAR(128) NOT NULL,
    font_family VARCHAR(128),
    file_name   VARCHAR(255) NOT NULL,
    file_path   VARCHAR(500) NOT NULL,
    file_size   BIGINT,
    status      TINYINT DEFAULT 1,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(64) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,
    nickname    VARCHAR(64),
    status      TINYINT DEFAULT 1,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sys_setting (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    setting_key VARCHAR(128) UNIQUE NOT NULL,
    setting_val VARCHAR(2000),
    remark      VARCHAR(500)
);

INSERT IGNORE INTO sys_user (username, password, nickname, status) VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', 1);
