CREATE TABLE IF NOT EXISTS app (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    app_id      TEXT UNIQUE NOT NULL,
    app_name    TEXT NOT NULL,
    app_secret  TEXT NOT NULL,
    status      INTEGER DEFAULT 1,
    remark      TEXT,
    created_at  TEXT DEFAULT (datetime('now', 'localtime')),
    updated_at  TEXT DEFAULT (datetime('now', 'localtime'))
);

CREATE TABLE IF NOT EXISTS compare_task (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    task_id      TEXT UNIQUE NOT NULL,
    session_id   TEXT UNIQUE NOT NULL,
    app_id       TEXT NOT NULL,
    status       TEXT DEFAULT 'pending',
    doc_type     TEXT DEFAULT 'word',
    file_a_name  TEXT,
    file_b_name  TEXT,
    file_a_path  TEXT,
    file_b_path  TEXT,
    result_json  TEXT,
    similarity   REAL,
    error_msg    TEXT,
    callback_url TEXT,
    expires_at   TEXT,
    created_at   TEXT DEFAULT (datetime('now', 'localtime')),
    started_at   TEXT,
    completed_at TEXT
);

CREATE TABLE IF NOT EXISTS font (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    font_name   TEXT NOT NULL,
    font_family TEXT,
    file_name   TEXT NOT NULL,
    file_path   TEXT NOT NULL,
    file_size   INTEGER,
    status      INTEGER DEFAULT 1,
    created_at  TEXT DEFAULT (datetime('now', 'localtime'))
);

CREATE TABLE IF NOT EXISTS sys_user (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    username    TEXT UNIQUE NOT NULL,
    password    TEXT NOT NULL,
    nickname    TEXT,
    status      INTEGER DEFAULT 1,
    created_at  TEXT DEFAULT (datetime('now', 'localtime'))
);

CREATE TABLE IF NOT EXISTS sys_setting (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    setting_key TEXT UNIQUE NOT NULL,
    setting_val TEXT,
    remark      TEXT
);

-- 初始管理员（密码: admin123 的 BCrypt）
INSERT OR IGNORE INTO sys_user (username, password, nickname, status) VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', 1);
