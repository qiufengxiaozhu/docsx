package com.docsx.engine.diff;

import com.docsx.engine.model.ContentOperation;
import com.docsx.engine.model.DocContents;
import com.docsx.engine.model.ParagraphDiffItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Needleman-Wunsch 全局序列对齐算法
 * 用于段落级 diff，移植自 luoshu diffEngine.ts:paragraphDiff
 *
 * 算法参数与 luoshu 完全一致：
 * - GAP_PENALTY = -1.0
 * - CHANGE_THRESHOLD = 0.2
 * - matchScore(sim) = sim - 0.5
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
public class NeedlemanWunsch {

    private static final double GAP_PENALTY = -1.0;
    private static final double CHANGE_THRESHOLD = 0.2;

    /**
     * 段落级全局对齐
     *
     * @param oldArr 旧文档段落列表
     * @param newArr 新文档段落列表
     * @return 对齐结果
     */
    public static ParagraphDiffResult align(List<DocContents> oldArr, List<DocContents> newArr) {
        int m = oldArr.size();
        int n = newArr.size();

        // 相似度缓存矩阵
        double[][] simCache = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                simCache[i][j] = -1;
            }
        }

        // ---- 阶段 1：正向填表 ----
        double[][] score = new double[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            score[i][0] = score[i - 1][0] + GAP_PENALTY;
        }
        for (int j = 1; j <= n; j++) {
            score[0][j] = score[0][j - 1] + GAP_PENALTY;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                double sim = getSimilarity(oldArr.get(i - 1).getContent(),
                        newArr.get(j - 1).getContent(), simCache, i - 1, j - 1);
                double matchScore = sim - 0.5;

                double diag = score[i - 1][j - 1] + matchScore;
                double up = score[i - 1][j] + GAP_PENALTY;
                double left = score[i][j - 1] + GAP_PENALTY;

                score[i][j] = Math.max(diag, Math.max(up, left));
            }
        }

        // ---- 阶段 2：回溯 ----
        List<ParagraphDiffItem> items = new ArrayList<>();
        int editDistance = 0;
        int i = m, j = n;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0) {
                double sim = getSimilarity(oldArr.get(i - 1).getContent(),
                        newArr.get(j - 1).getContent(), simCache, i - 1, j - 1);
                double matchScore = sim - 0.5;

                if (Math.abs(score[i][j] - (score[i - 1][j - 1] + matchScore)) < 1e-9) {
                    if (sim >= 1.0) {
                        // SAME - 不输出
                    } else if (sim >= CHANGE_THRESHOLD) {
                        // MODIFY
                        ParagraphDiffItem item = new ParagraphDiffItem();
                        item.setType(ContentOperation.MODIFY);
                        item.setText1(oldArr.get(i - 1).getContent());
                        item.setText2(newArr.get(j - 1).getContent());
                        item.setId1(oldArr.get(i - 1).getId());
                        item.setId2(newArr.get(j - 1).getId());
                        item.setIdx1(i - 1);
                        item.setIdx2(j - 1);
                        items.add(item);
                        editDistance += (int) Math.ceil(1.0 - sim);
                    } else {
                        // 低相似度：拆分为 DELETE + ADD
                        ParagraphDiffItem delItem = new ParagraphDiffItem();
                        delItem.setType(ContentOperation.DELETE);
                        delItem.setText1(oldArr.get(i - 1).getContent());
                        delItem.setText2("");
                        delItem.setId1(oldArr.get(i - 1).getId());
                        delItem.setId2(newArr.get(j - 1).getId());
                        delItem.setIdx1(i - 1);
                        items.add(delItem);

                        ParagraphDiffItem addItem = new ParagraphDiffItem();
                        addItem.setType(ContentOperation.ADD);
                        addItem.setText1("");
                        addItem.setText2(newArr.get(j - 1).getContent());
                        addItem.setId1(oldArr.get(i - 1).getId());
                        addItem.setId2(newArr.get(j - 1).getId());
                        addItem.setIdx2(j - 1);
                        items.add(addItem);

                        editDistance += 2;
                    }
                    i--;
                    j--;
                    continue;
                }
            }

            if (i > 0 && (j == 0 || score[i - 1][j] + GAP_PENALTY >= score[i][j] - 1e-9
                    && !(j > 0 && Math.abs(score[i][j] - (score[i][j - 1] + GAP_PENALTY)) < 1e-9))) {
                // DELETE
                ParagraphDiffItem item = new ParagraphDiffItem();
                item.setType(ContentOperation.DELETE);
                item.setText1(oldArr.get(i - 1).getContent());
                item.setText2("");
                item.setId1(oldArr.get(i - 1).getId());
                item.setId2("");
                item.setIdx1(i - 1);
                items.add(item);
                editDistance += 1;
                i--;
            } else {
                // ADD
                ParagraphDiffItem item = new ParagraphDiffItem();
                item.setType(ContentOperation.ADD);
                item.setText1("");
                item.setText2(newArr.get(j - 1).getContent());
                item.setId1("");
                item.setId2(newArr.get(j - 1).getId());
                item.setIdx2(j - 1);
                items.add(item);
                editDistance += 1;
                j--;
            }
        }

        Collections.reverse(items);

        double similarityScore = (m + n) > 0 ? 1.0 - (double) editDistance / (m + n) : 1.0;

        ParagraphDiffResult result = new ParagraphDiffResult();
        result.setItems(items);
        result.setEditDistance(editDistance);
        result.setSimilarityScore(similarityScore);
        return result;
    }

    /**
     * 计算两段文本的 LCS 字符相似度（带缓存）
     */
    private static double getSimilarity(String t1, String t2, double[][] cache, int ci, int cj) {
        if (cache[ci][cj] >= 0) {
            return cache[ci][cj];
        }
        String s1 = TextUtils.normalizeText(t1);
        String s2 = TextUtils.normalizeText(t2);

        if (s1.isEmpty() && s2.isEmpty()) {
            cache[ci][cj] = 1.0;
            return 1.0;
        }
        if (s1.isEmpty() || s2.isEmpty()) {
            cache[ci][cj] = 0.0;
            return 0.0;
        }

        int lcs = lcsLength(s1, s2);
        double sim = (double) lcs / Math.max(s1.length(), s2.length());
        cache[ci][cj] = sim;
        return sim;
    }

    /**
     * 最长公共子序列长度（O(m*n)空间优化为两行）
     */
    private static int lcsLength(String a, String b) {
        int m = a.length();
        int n = b.length();
        if (m > 2000 || n > 2000) {
            return lcsLengthApprox(a, b);
        }
        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    curr[j] = prev[j - 1] + 1;
                } else {
                    curr[j] = Math.max(prev[j], curr[j - 1]);
                }
            }
            int[] tmp = prev;
            prev = curr;
            curr = tmp;
            java.util.Arrays.fill(curr, 0);
        }
        return prev[n];
    }

    /**
     * 长文本近似相似度（基于 bigram Dice 系数）
     */
    private static int lcsLengthApprox(String a, String b) {
        int maxLen = Math.max(a.length(), b.length());
        double dice = TextUtils.diceSimilarity(a, b);
        return (int) (dice * maxLen);
    }

    /**
     * NW 段落对齐结果
     */
    public static class ParagraphDiffResult {
        private List<ParagraphDiffItem> items;
        private int editDistance;
        private double similarityScore;

        public List<ParagraphDiffItem> getItems() { return items; }
        public void setItems(List<ParagraphDiffItem> items) { this.items = items; }
        public int getEditDistance() { return editDistance; }
        public void setEditDistance(int editDistance) { this.editDistance = editDistance; }
        public double getSimilarityScore() { return similarityScore; }
        public void setSimilarityScore(double similarityScore) { this.similarityScore = similarityScore; }
    }
}
