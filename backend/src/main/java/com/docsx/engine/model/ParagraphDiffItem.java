package com.docsx.engine.model;

/**
 * NW 段落对齐结果项
 * 对应 luoshu diffEngine.ts 的 ParagraphDiffItem
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
public class ParagraphDiffItem {

    private ContentOperation type;
    private String text1;
    private String text2;
    private String id1;
    private String id2;
    private Integer idx1;
    private Integer idx2;

    public ContentOperation getType() { return type; }
    public void setType(ContentOperation type) { this.type = type; }

    public String getText1() { return text1; }
    public void setText1(String text1) { this.text1 = text1; }

    public String getText2() { return text2; }
    public void setText2(String text2) { this.text2 = text2; }

    public String getId1() { return id1; }
    public void setId1(String id1) { this.id1 = id1; }

    public String getId2() { return id2; }
    public void setId2(String id2) { this.id2 = id2; }

    public Integer getIdx1() { return idx1; }
    public void setIdx1(Integer idx1) { this.idx1 = idx1; }

    public Integer getIdx2() { return idx2; }
    public void setIdx2(Integer idx2) { this.idx2 = idx2; }
}
