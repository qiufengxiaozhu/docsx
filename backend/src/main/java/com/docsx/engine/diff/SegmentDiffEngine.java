package com.docsx.engine.diff;

import com.docsx.engine.model.ContentOperation;
import com.docsx.engine.model.SegmentDiffItem;
import com.docsx.engine.model.SegmentDiffItem.Position;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 词级/字符级 Diff 引擎
 * 移植自 luoshu diffEngine.ts 的 segmentDiff 实现
 *
 * 算法流程：
 * 1. jieba 分词 → token 列表
 * 2. linesToChars 技巧：token → Unicode 字符映射
 * 3. DMP diff_main + cleanupSemantic
 * 4. 还原为 SegmentDiffItem（含字符偏移）
 * 5. 无 EQUAL 时回退纯字符级 DMP
 * 6. 相邻 DELETE+ADD 归并为 MODIFY
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
public class SegmentDiffEngine {

    private static final JiebaSegmenter SEGMENTER = new JiebaSegmenter();
    private static final DiffMatchPatch DMP = new DiffMatchPatch();

    /**
     * 词级 segmentDiff
     *
     * @param oldText 旧文本
     * @param newText 新文本
     * @return 差异项列表
     */
    public static List<SegmentDiffItem> segmentDiff(String oldText, String newText) {
        if (oldText == null) oldText = "";
        if (newText == null) newText = "";

        if (oldText.equals(newText)) {
            return List.of();
        }

        // 1. 分词
        List<String> oldTokens = cut(oldText);
        List<String> newTokens = cut(newText);

        // 2. linesToChars 编码
        LinesToCharsResult encoded = linesToChars(oldTokens, newTokens);

        // 3. DMP diff
        LinkedList<DiffMatchPatch.Diff> diffs = DMP.diffMain(encoded.chars1, encoded.chars2, false);
        DMP.diffCleanupSemantic(diffs);

        // 4. 还原为 token 级 diff
        List<TokenDiff> tokenDiffs = decodeTokenDiffs(diffs, encoded.lineArray);

        // 5. 检查是否有 EQUAL，无则回退字符级
        boolean hasEqual = tokenDiffs.stream().anyMatch(td -> td.op == DiffMatchPatch.Operation.EQUAL);
        if (!hasEqual && !oldText.isEmpty() && !newText.isEmpty()) {
            return charDiff(oldText, newText);
        }

        // 6. 转为 SegmentDiffItem 并归并 DELETE+ADD 为 MODIFY
        return buildSegmentDiffItems(tokenDiffs, oldText, newText);
    }

    /**
     * 纯字符级 diff（回退方案）
     */
    public static List<SegmentDiffItem> charDiff(String oldText, String newText) {
        LinkedList<DiffMatchPatch.Diff> diffs = DMP.diffMain(oldText, newText, false);
        DMP.diffCleanupSemantic(diffs);

        List<SegmentDiffItem> result = new ArrayList<>();
        int pos1 = 0, pos2 = 0;

        List<RawOp> ops = new ArrayList<>();
        for (DiffMatchPatch.Diff diff : diffs) {
            int len = diff.text.length();
            switch (diff.operation) {
                case EQUAL:
                    pos1 += len;
                    pos2 += len;
                    break;
                case DELETE:
                    ops.add(new RawOp(ContentOperation.DELETE, diff.text, "", pos1, pos1 + len, pos2, pos2));
                    pos1 += len;
                    break;
                case INSERT:
                    ops.add(new RawOp(ContentOperation.ADD, "", diff.text, pos1, pos1, pos2, pos2 + len));
                    pos2 += len;
                    break;
            }
        }

        // 归并相邻 DELETE+ADD 为 MODIFY
        mergeOps(ops, result);
        return result;
    }

    /**
     * jieba 中文分词
     */
    private static List<String> cut(String text) {
        if (text == null || text.isEmpty()) return List.of();
        List<String> tokens = new ArrayList<>();
        List<SegToken> segTokens = SEGMENTER.process(text, JiebaSegmenter.SegMode.SEARCH);
        for (SegToken st : segTokens) {
            tokens.add(st.word);
        }
        return tokens;
    }

    /**
     * linesToChars 编码：将 token 序列映射为 Unicode 字符串
     */
    private static LinesToCharsResult linesToChars(List<String> tokens1, List<String> tokens2) {
        Map<String, Character> tokenToChar = new HashMap<>();
        List<String> charToToken = new ArrayList<>();
        charToToken.add("");  // index 0 unused
        char nextChar = 0x0100;

        StringBuilder sb1 = new StringBuilder();
        for (String token : tokens1) {
            Character c = tokenToChar.get(token);
            if (c == null) {
                c = nextChar++;
                tokenToChar.put(token, c);
                charToToken.add(token);
            }
            sb1.append(c);
        }

        StringBuilder sb2 = new StringBuilder();
        for (String token : tokens2) {
            Character c = tokenToChar.get(token);
            if (c == null) {
                c = nextChar++;
                tokenToChar.put(token, c);
                charToToken.add(token);
            }
            sb2.append(c);
        }

        return new LinesToCharsResult(sb1.toString(), sb2.toString(), charToToken);
    }

    /**
     * 将 DMP diff 结果还原为 token 级别
     */
    private static List<TokenDiff> decodeTokenDiffs(LinkedList<DiffMatchPatch.Diff> diffs, List<String> lineArray) {
        List<TokenDiff> result = new ArrayList<>();
        for (DiffMatchPatch.Diff diff : diffs) {
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < diff.text.length(); i++) {
                int idx = diff.text.charAt(i);
                if (idx < lineArray.size()) {
                    text.append(lineArray.get(idx));
                }
            }
            result.add(new TokenDiff(diff.operation, text.toString()));
        }
        return result;
    }

    /**
     * 从 token diff 构建 SegmentDiffItem 列表
     */
    private static List<SegmentDiffItem> buildSegmentDiffItems(List<TokenDiff> tokenDiffs, String oldText, String newText) {
        List<RawOp> ops = new ArrayList<>();
        int pos1 = 0, pos2 = 0;

        for (TokenDiff td : tokenDiffs) {
            int len = td.text.length();
            switch (td.op) {
                case EQUAL:
                    pos1 += len;
                    pos2 += len;
                    break;
                case DELETE:
                    ops.add(new RawOp(ContentOperation.DELETE, td.text, "", pos1, pos1 + len, pos2, pos2));
                    pos1 += len;
                    break;
                case INSERT:
                    ops.add(new RawOp(ContentOperation.ADD, "", td.text, pos1, pos1, pos2, pos2 + len));
                    pos2 += len;
                    break;
            }
        }

        List<SegmentDiffItem> result = new ArrayList<>();
        mergeOps(ops, result);
        return result;
    }

    /**
     * 归并相邻 DELETE+ADD 为 MODIFY
     */
    private static void mergeOps(List<RawOp> ops, List<SegmentDiffItem> result) {
        int i = 0;
        while (i < ops.size()) {
            RawOp curr = ops.get(i);
            if (curr.type == ContentOperation.DELETE && i + 1 < ops.size()
                    && ops.get(i + 1).type == ContentOperation.ADD) {
                RawOp next = ops.get(i + 1);
                SegmentDiffItem item = new SegmentDiffItem();
                item.setType(ContentOperation.MODIFY);
                item.setText1(curr.text1);
                item.setText2(next.text2);
                item.setPos1(new Position(curr.pos1Start, curr.pos1End));
                item.setPos2(new Position(next.pos2Start, next.pos2End));
                result.add(item);
                i += 2;
            } else {
                SegmentDiffItem item = new SegmentDiffItem();
                item.setType(curr.type);
                item.setText1(curr.text1);
                item.setText2(curr.text2);
                item.setPos1(new Position(curr.pos1Start, curr.pos1End));
                item.setPos2(new Position(curr.pos2Start, curr.pos2End));
                result.add(item);
                i++;
            }
        }
    }

    // ===== 内部数据类 =====

    private static class LinesToCharsResult {
        String chars1;
        String chars2;
        List<String> lineArray;

        LinesToCharsResult(String chars1, String chars2, List<String> lineArray) {
            this.chars1 = chars1;
            this.chars2 = chars2;
            this.lineArray = lineArray;
        }
    }

    private static class TokenDiff {
        DiffMatchPatch.Operation op;
        String text;

        TokenDiff(DiffMatchPatch.Operation op, String text) {
            this.op = op;
            this.text = text;
        }
    }

    private static class RawOp {
        ContentOperation type;
        String text1;
        String text2;
        int pos1Start, pos1End;
        int pos2Start, pos2End;

        RawOp(ContentOperation type, String text1, String text2,
               int pos1Start, int pos1End, int pos2Start, int pos2End) {
            this.type = type;
            this.text1 = text1;
            this.text2 = text2;
            this.pos1Start = pos1Start;
            this.pos1End = pos1End;
            this.pos2Start = pos2Start;
            this.pos2End = pos2End;
        }
    }
}
