package com.docsx.engine.model;

/**
 * 词级/字符级 diff 结果项
 * 对应 luoshu diffEngine.ts 的 SegmentDiffItem
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
public class SegmentDiffItem {

    private ContentOperation type;
    private String text1;
    private String text2;
    private Position pos1;
    private Position pos2;

    public ContentOperation getType() { return type; }
    public void setType(ContentOperation type) { this.type = type; }

    public String getText1() { return text1; }
    public void setText1(String text1) { this.text1 = text1; }

    public String getText2() { return text2; }
    public void setText2(String text2) { this.text2 = text2; }

    public Position getPos1() { return pos1; }
    public void setPos1(Position pos1) { this.pos1 = pos1; }

    public Position getPos2() { return pos2; }
    public void setPos2(Position pos2) { this.pos2 = pos2; }

    /**
     * 字符位置区间
     */
    public static class Position {
        private int start;
        private int end;

        public Position() {}

        public Position(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() { return start; }
        public void setStart(int start) { this.start = start; }
        public int getEnd() { return end; }
        public void setEnd(int end) { this.end = end; }
    }
}
