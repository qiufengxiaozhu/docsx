package com.docsx.engine.model;

/**
 * 文档段落内容（id + content）
 * 对应 luoshu 的 DocContents 接口
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
public class DocContents {

    private String id;
    private String content;

    public DocContents() {}

    public DocContents(String id, String content) {
        this.id = id;
        this.content = content;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
