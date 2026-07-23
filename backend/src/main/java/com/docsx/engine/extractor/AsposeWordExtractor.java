package com.docsx.engine.extractor;

import com.aspose.words.*;
import com.docsx.engine.model.BlockType;
import com.docsx.engine.model.DocBlock;
import com.docsx.engine.model.DocContents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aspose.Words 文档内容提取器
 * 移植自 filez-demo 的 DocExtractUtils，适配 luoshu 的 blocks+paragraphs 输出结构
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
public class AsposeWordExtractor {

    private static final Logger log = LoggerFactory.getLogger(AsposeWordExtractor.class);

    /**
     * 提取结果：同时包含 blocks（段落+表格）和 paragraphs（纯段落 id+content）
     */
    public static class ExtractResult {
        private List<DocBlock> blocks;
        private List<DocContents> paragraphs;

        public List<DocBlock> getBlocks() { return blocks; }
        public void setBlocks(List<DocBlock> blocks) { this.blocks = blocks; }
        public List<DocContents> getParagraphs() { return paragraphs; }
        public void setParagraphs(List<DocContents> paragraphs) { this.paragraphs = paragraphs; }
    }

    /**
     * 从文件提取文档内容
     */
    public static ExtractResult extract(File file) throws Exception {
        Document doc = new Document(file.getAbsolutePath());
        return extractFromDocument(doc);
    }

    /**
     * 从输入流提取文档内容
     */
    public static ExtractResult extract(InputStream inputStream) throws Exception {
        Document doc = new Document(inputStream);
        return extractFromDocument(doc);
    }

    /**
     * 从 Aspose Document 对象提取
     */
    private static ExtractResult extractFromDocument(Document doc) throws Exception {
        doc.updateListLabels();

        List<DocBlock> blocks = new ArrayList<>();
        List<DocContents> paragraphs = new ArrayList<>();
        int blockOrder = 0;
        int tableSeq = 0;

        for (Section section : doc.getSections()) {
            Body body = section.getBody();
            if (body == null) continue;

            for (Node child = body.getFirstChild(); child != null; child = child.getNextSibling()) {
                if (child instanceof Paragraph) {
                    Paragraph para = (Paragraph) child;
                    List<ParagraphInfo> paraInfos = extractParagraph(para, blockOrder);
                    for (ParagraphInfo info : paraInfos) {
                        String id = generateId(blockOrder);
                        DocBlock block = DocBlock.paragraph(blockOrder, id, info.text);
                        block.setStyleName(info.styleName);
                        block.setListLabel(info.listLabel);
                        block.setListLevel(info.listLevel);
                        blocks.add(block);
                        paragraphs.add(new DocContents(id, info.text));
                        blockOrder++;
                    }
                } else if (child instanceof Table) {
                    Table table = (Table) child;
                    String id = generateId(blockOrder);
                    DocBlock block = extractTable(table, tableSeq, blockOrder, id);
                    blocks.add(block);
                    // 表格内容也作为一个段落加入 paragraphs（整体拼接）
                    paragraphs.add(new DocContents(id, block.getContent()));
                    blockOrder++;
                    tableSeq++;
                }
            }
        }

        ExtractResult result = new ExtractResult();
        result.setBlocks(blocks);
        result.setParagraphs(paragraphs);
        return result;
    }

    /**
     * 提取段落（支持软回车拆分）
     */
    private static List<ParagraphInfo> extractParagraph(Paragraph para, int startIndex) throws Exception {
        List<ParagraphInfo> result = new ArrayList<>();
        String fullText = para.toString(SaveFormat.TEXT).trim();
        if (fullText.isEmpty()) return result;

        String styleName = para.getParagraphFormat().getStyle().getName();
        int listLevel = -1;
        String listLabel = null;

        if (para.getListFormat().isListItem()) {
            listLevel = para.getListFormat().getListLevelNumber();
            listLabel = para.getListLabel().getLabelString();
        }

        // 按软回车拆分
        String[] lines = fullText.split("[\\r\\n]+");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            ParagraphInfo info = new ParagraphInfo();
            info.text = trimmed;
            info.styleName = styleName;
            info.listLevel = listLevel;
            info.listLabel = listLabel;
            result.add(info);
        }

        return result;
    }

    /**
     * 将表格整体提取为一个 DocBlock（按行拼接全部单元格文本）
     */
    private static DocBlock extractTable(Table table, int tableSeq, int blockOrder, String id) throws Exception {
        StringBuilder sb = new StringBuilder();
        RowCollection rows = table.getRows();

        List<DocBlock.TableCellSnapshot> cellSnapshots = new ArrayList<>();
        int maxCols = 0;

        for (int r = 0; r < rows.getCount(); r++) {
            if (r > 0) sb.append("\n");
            Row row = rows.get(r);
            CellCollection cells = row.getCells();
            maxCols = Math.max(maxCols, cells.getCount());

            for (int c = 0; c < cells.getCount(); c++) {
                if (c > 0) sb.append("\t");
                Cell cell = cells.get(c);
                String cellText = cell.toString(SaveFormat.TEXT).trim();
                sb.append(cellText);

                DocBlock.TableCellSnapshot cs = new DocBlock.TableCellSnapshot();
                cs.setRow(r);
                cs.setCol(c);
                cs.setRowSpan(1);
                cs.setColSpan(1);
                cs.setContent(List.of(cellText.split("[\\r\\n]+")));
                cellSnapshots.add(cs);
            }
        }

        DocBlock.TableSnapshot snapshot = new DocBlock.TableSnapshot();
        snapshot.setRows(rows.getCount());
        snapshot.setCols(maxCols);
        snapshot.setCells(cellSnapshots);

        DocBlock block = DocBlock.table(blockOrder, id, snapshot);
        block.setContent(sb.toString());
        return block;
    }

    /**
     * 生成段落 ID
     */
    private static String generateId(int order) {
        return "p_" + order + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private static class ParagraphInfo {
        String text;
        String styleName;
        String listLabel;
        int listLevel = -1;
    }
}
