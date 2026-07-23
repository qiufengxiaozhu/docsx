package com.docsx.engine.diff;

import com.docsx.engine.model.*;

import java.util.*;

/**
 * 比对引擎编排层
 * 移植自 luoshu compareDocuments.ts 的 AICompareDocuments.compareDocument
 *
 * 职责：
 * 1. 过滤空段落/图片段落
 * 2. 表格分流（有表格时先 TableAligner 处理）
 * 3. NW 段落对齐
 * 4. 构建 totalList（含 key 冲突处理）
 * 5. 产出 CompareResult
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
public class CompareEngine {

    private static final int PRE_COMPARE_LIMIT = 1000;

    /**
     * 执行 Word 比对（纯段落模式，无表格分流）
     *
     * @param doc1Paragraphs 旧文档段落列表
     * @param doc2Paragraphs 新文档段落列表
     * @return 预比对结果
     */
    public static CompareResult compareWord(List<DocContents> doc1Paragraphs,
                                            List<DocContents> doc2Paragraphs) {
        return compareWordOnly(doc1Paragraphs, doc2Paragraphs);
    }

    /**
     * 执行 Word 比对（含表格分流）
     *
     * @param doc1Paragraphs 旧文档段落列表
     * @param doc2Paragraphs 新文档段落列表
     * @param doc1Blocks     旧文档块列表（含表格）
     * @param doc2Blocks     新文档块列表（含表格）
     * @return 预比对结果
     */
    public static CompareResult compareWordWithBlocks(List<DocContents> doc1Paragraphs,
                                                      List<DocContents> doc2Paragraphs,
                                                      List<DocBlock> doc1Blocks,
                                                      List<DocBlock> doc2Blocks) {
        boolean hasTables = doc1Blocks.stream().anyMatch(b -> b.getType() == BlockType.TABLE)
                || doc2Blocks.stream().anyMatch(b -> b.getType() == BlockType.TABLE);

        if (hasTables) {
            // TODO: Phase 2+ 实现表格分流 TableAligner
            // 暂时直接走纯段落比对
            return compareWordOnly(doc1Paragraphs, doc2Paragraphs);
        }

        return compareWordOnly(doc1Paragraphs, doc2Paragraphs);
    }

    /**
     * 纯段落比对（compareWordOnly）
     * 对应 luoshu compareDocuments.ts:compareWordOnly
     */
    private static CompareResult compareWordOnly(List<DocContents> doc1Array,
                                                  List<DocContents> doc2Array) {
        // 过滤空段落和图片段落
        List<DocContents> filtered1 = filterParagraphs(doc1Array);
        List<DocContents> filtered2 = filterParagraphs(doc2Array);

        // 截断到预比对限制
        if (filtered1.size() > PRE_COMPARE_LIMIT) {
            filtered1 = filtered1.subList(0, PRE_COMPARE_LIMIT);
        }
        if (filtered2.size() > PRE_COMPARE_LIMIT) {
            filtered2 = filtered2.subList(0, PRE_COMPARE_LIMIT);
        }

        // NW 段落对齐
        NeedlemanWunsch.ParagraphDiffResult diffResult = NeedlemanWunsch.align(filtered1, filtered2);

        // 构建 totalList 和 editList
        List<DocContents> doc1EditList = new ArrayList<>();
        List<DocContents> doc2EditList = new ArrayList<>();
        Map<String, Object> totalList = new LinkedHashMap<>();

        for (ParagraphDiffItem item : diffResult.getItems()) {
            String key = item.getId1() + "_" + item.getId2();

            switch (item.getType()) {
                case MODIFY:
                    // MODIFY 占位（前端 segmentDiff 后填充）
                    totalList.put(key, Map.of());
                    doc1EditList.add(new DocContents(item.getId1(), item.getText1()));
                    doc2EditList.add(new DocContents(item.getId2(), item.getText2()));
                    break;

                case ADD:
                case DELETE:
                    // 处理 key 冲突（DELETE+ADD 共享 key 时）
                    key = resolveKeyConflict(totalList, key);

                    Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put("type", item.getType().name().toLowerCase());
                    entry.put("pId1", item.getId1());
                    entry.put("pId2", item.getId2());
                    entry.put("text1", item.getText1());
                    entry.put("text2", item.getText2());
                    if (item.getIdx1() != null) entry.put("idx1", item.getIdx1());
                    if (item.getIdx2() != null) entry.put("idx2", item.getIdx2());
                    totalList.put(key, entry);
                    break;

                default:
                    break;
            }
        }

        boolean allCompared = filtered1.size() <= PRE_COMPARE_LIMIT
                && filtered2.size() <= PRE_COMPARE_LIMIT;

        CompareResult result = new CompareResult();
        result.setDoc1EditList(doc1EditList);
        result.setDoc2EditList(doc2EditList);
        result.setTotalList(totalList);
        result.setSimilarityScore(diffResult.getSimilarityScore());
        result.setEditDistance(diffResult.getEditDistance());
        result.setAllContentsCompared(allCompared);
        result.setDoc1ParagraphCount(doc1Array.size());
        result.setDoc2ParagraphCount(doc2Array.size());
        return result;
    }

    /**
     * 过滤空段落和纯图片段落
     * 对应 luoshu compareDocuments.ts:filterCompareDocArray
     */
    private static List<DocContents> filterParagraphs(List<DocContents> paragraphs) {
        List<DocContents> result = new ArrayList<>();
        for (DocContents p : paragraphs) {
            if (!TextUtils.isEmptyOrImage(p.getContent())) {
                String cleaned = TextUtils.removeImageChars(p.getContent());
                result.add(new DocContents(p.getId(), cleaned));
            }
        }
        return result;
    }

    /**
     * 处理 totalList key 冲突
     * 对应 luoshu compareDocuments.ts 中的 _dup{N} 后缀处理
     */
    private static String resolveKeyConflict(Map<String, Object> totalList, String key) {
        if (!totalList.containsKey(key)) {
            return key;
        }
        int suffix = 1;
        while (totalList.containsKey(key + "_dup" + suffix)) {
            suffix++;
        }
        return key + "_dup" + suffix;
    }
}
