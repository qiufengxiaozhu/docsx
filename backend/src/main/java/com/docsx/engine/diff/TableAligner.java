package com.docsx.engine.diff;

import com.docsx.engine.model.ContentOperation;
import com.docsx.engine.model.DocBlock;
import com.docsx.engine.model.DocBlock.TableCellSnapshot;
import com.docsx.engine.model.DocBlock.TableSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * 表格对齐比对器
 * 移植自 luoshu tableAlignPipeline.ts
 *
 * 算法流程：
 * 1. 按行匹配（Dice 相似度）
 * 2. 行内按列匹配
 * 3. 对每个匹配的单元格做 segmentDiff
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
public class TableAligner {

    private static final double ROW_MATCH_THRESHOLD = 0.3;

    /**
     * 表格比对
     *
     * @param table1 旧表格快照
     * @param table2 新表格快照
     * @return 表格差异列表
     */
    public static List<TableCellDiff> alignTables(TableSnapshot table1, TableSnapshot table2) {
        List<TableCellDiff> diffs = new ArrayList<>();

        if (table1 == null || table2 == null) {
            return diffs;
        }

        int rows1 = table1.getRows();
        int rows2 = table2.getRows();

        // 按行做 NW 对齐
        String[] rowStrings1 = getRowStrings(table1);
        String[] rowStrings2 = getRowStrings(table2);

        int[] rowMapping = alignRows(rowStrings1, rowStrings2);

        // 逐行对比
        int j = 0;
        for (int i = 0; i < rows1 && j < rows2; i++) {
            if (rowMapping[i] >= 0) {
                int mappedJ = rowMapping[i];
                List<TableCellSnapshot> row1Cells = getCellsForRow(table1, i);
                List<TableCellSnapshot> row2Cells = getCellsForRow(table2, mappedJ);
                compareCellRow(row1Cells, row2Cells, i, mappedJ, diffs);
            } else {
                // 行被删除
                List<TableCellSnapshot> row1Cells = getCellsForRow(table1, i);
                for (TableCellSnapshot cell : row1Cells) {
                    TableCellDiff diff = new TableCellDiff();
                    diff.type = ContentOperation.DELETE;
                    diff.row1 = i;
                    diff.col1 = cell.getCol();
                    diff.text1 = String.join("", cell.getContent());
                    diffs.add(diff);
                }
            }
        }

        return diffs;
    }

    /**
     * 行对齐：简化版贪心匹配
     */
    private static int[] alignRows(String[] rows1, String[] rows2) {
        int[] mapping = new int[rows1.length];
        boolean[] used = new boolean[rows2.length];

        for (int i = 0; i < rows1.length; i++) {
            double bestSim = -1;
            int bestJ = -1;
            for (int j = 0; j < rows2.length; j++) {
                if (used[j]) continue;
                double sim = TextUtils.diceSimilarity(rows1[i], rows2[j]);
                if (sim > bestSim && sim >= ROW_MATCH_THRESHOLD) {
                    bestSim = sim;
                    bestJ = j;
                }
            }
            mapping[i] = bestJ;
            if (bestJ >= 0) {
                used[bestJ] = true;
            }
        }
        return mapping;
    }

    /**
     * 比对同一行的单元格
     */
    private static void compareCellRow(List<TableCellSnapshot> row1Cells,
                                        List<TableCellSnapshot> row2Cells,
                                        int rowIdx1, int rowIdx2,
                                        List<TableCellDiff> diffs) {
        int cols = Math.max(row1Cells.size(), row2Cells.size());
        for (int c = 0; c < cols; c++) {
            String text1 = c < row1Cells.size() ? String.join("", row1Cells.get(c).getContent()) : "";
            String text2 = c < row2Cells.size() ? String.join("", row2Cells.get(c).getContent()) : "";

            if (text1.equals(text2)) continue;

            TableCellDiff diff = new TableCellDiff();
            diff.row1 = rowIdx1;
            diff.col1 = c;
            diff.row2 = rowIdx2;
            diff.col2 = c;
            diff.text1 = text1;
            diff.text2 = text2;

            if (text1.isEmpty()) {
                diff.type = ContentOperation.ADD;
            } else if (text2.isEmpty()) {
                diff.type = ContentOperation.DELETE;
            } else {
                diff.type = ContentOperation.MODIFY;
            }
            diffs.add(diff);
        }
    }

    private static String[] getRowStrings(TableSnapshot table) {
        String[] result = new String[table.getRows()];
        for (int r = 0; r < table.getRows(); r++) {
            StringBuilder sb = new StringBuilder();
            for (TableCellSnapshot cell : table.getCells()) {
                if (cell.getRow() == r) {
                    sb.append(String.join("", cell.getContent()));
                }
            }
            result[r] = sb.toString();
        }
        return result;
    }

    private static List<TableCellSnapshot> getCellsForRow(TableSnapshot table, int row) {
        List<TableCellSnapshot> result = new ArrayList<>();
        for (TableCellSnapshot cell : table.getCells()) {
            if (cell.getRow() == row) {
                result.add(cell);
            }
        }
        return result;
    }

    /**
     * 表格单元格差异项
     */
    public static class TableCellDiff {
        public ContentOperation type;
        public int row1, col1;
        public int row2, col2;
        public String text1;
        public String text2;
    }
}
