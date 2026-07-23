-- docsx SQLite Schema
-- @Author Cursor
-- @Date 2026-07-23
-- @Version 1.0

CREATE TABLE IF NOT EXISTS sys_user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    nick_name TEXT,
    status INTEGER DEFAULT 1,
    created_at TEXT DEFAULT (datetime('now')),
    updated_at TEXT DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS app (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    app_name TEXT NOT NULL,
    app_key TEXT NOT NULL UNIQUE,
    app_secret TEXT NOT NULL,
    status INTEGER DEFAULT 1,
    description TEXT,
    callback_url TEXT,
    created_at TEXT DEFAULT (datetime('now')),
    updated_at TEXT DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS compare_task (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    session_id TEXT NOT NULL UNIQUE,
    app_key TEXT,
    status TEXT DEFAULT 'PENDING',
    doc_type TEXT DEFAULT 'WORD',
    file_name1 TEXT,
    file_name2 TEXT,
    file1_path TEXT,
    file2_path TEXT,
    result_json TEXT,
    similarity REAL,
    error_message TEXT,
    callback_url TEXT,
    expires_at TEXT,
    created_at TEXT DEFAULT (datetime('now')),
    started_at TEXT,
    completed_at TEXT,
    updated_at TEXT DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS font (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    font_name TEXT NOT NULL,
    font_family TEXT,
    file_path TEXT NOT NULL,
    file_size INTEGER,
    status INTEGER DEFAULT 1,
    created_at TEXT DEFAULT (datetime('now')),
    updated_at TEXT DEFAULT (datetime('now'))
);

-- 初始管理员由 DataInitializer 在代码中初始化

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_task_session ON compare_task(session_id);
CREATE INDEX IF NOT EXISTS idx_task_status ON compare_task(status);
CREATE INDEX IF NOT EXISTS idx_task_app_key ON compare_task(app_key);
CREATE INDEX IF NOT EXISTS idx_app_key ON app(app_key);
