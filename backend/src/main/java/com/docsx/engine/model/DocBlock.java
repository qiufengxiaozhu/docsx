package com.docsx.engine.model;

import java.util.List;

/**
 * 文档块：段落或表格的结构化表示
 * 对应 luoshu 的 DocBlock 结构
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
public class DocBlock {

    private BlockType type;
    private String id;
    private String content;
    private int docOrder;
    private String styleName;
    private String listLabel;
    private int listLevel = -1;
    private TableSnapshot snapshot;

    public static DocBlock paragraph(int docOrder, String id, String content) {
        DocBlock block = new DocBlock();
        block.type = BlockType.PARAGRAPH;
        block.docOrder = docOrder;
        block.id = id;
        block.content = content;
        return block;
    }

    public static DocBlock table(int docOrder, String id, TableSnapshot snapshot) {
        DocBlock block = new DocBlock();
        block.type = BlockType.TABLE;
        block.docOrder = docOrder;
        block.id = id;
        block.snapshot = snapshot;
        return block;
    }

    // Getters and Setters

    public BlockType getType() { return type; }
    public void setType(BlockType type) { this.type = type; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getDocOrder() { return docOrder; }
    public void setDocOrder(int docOrder) { this.docOrder = docOrder; }

    public String getStyleName() { return styleName; }
    public void setStyleName(String styleName) { this.styleName = styleName; }

    public String getListLabel() { return listLabel; }
    public void setListLabel(String listLabel) { this.listLabel = listLabel; }

    public int getListLevel() { return listLevel; }
    public void setListLevel(int listLevel) { this.listLevel = listLevel; }

    public TableSnapshot getSnapshot() { return snapshot; }
    public void setSnapshot(TableSnapshot snapshot) { this.snapshot = snapshot; }

    /**
     * 表格快照
     */
    public static class TableSnapshot {
        private int rows;
        private int cols;
        private List<TableCellSnapshot> cells;

        public int getRows() { return rows; }
        public void setRows(int rows) { this.rows = rows; }
        public int getCols() { return cols; }
        public void setCols(int cols) { this.cols = cols; }
        public List<TableCellSnapshot> getCells() { return cells; }
        public void setCells(List<TableCellSnapshot> cells) { this.cells = cells; }
    }

    /**
     * 表格单元格快照
     */
    public static class TableCellSnapshot {
        private int row;
        private int col;
        private int rowSpan;
        private int colSpan;
        private List<String> content;

        public int getRow() { return row; }
        public void setRow(int row) { this.row = row; }
        public int getCol() { return col; }
        public void setCol(int col) { this.col = col; }
        public int getRowSpan() { return rowSpan; }
        public void setRowSpan(int rowSpan) { this.rowSpan = rowSpan; }
        public int getColSpan() { return colSpan; }
        public void setColSpan(int colSpan) { this.colSpan = colSpan; }
        public List<String> getContent() { return content; }
        public void setContent(List<String> content) { this.content = content; }
    }
}
