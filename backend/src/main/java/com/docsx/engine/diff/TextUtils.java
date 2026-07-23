package com.docsx.engine.diff;

import java.util.HashMap;
import java.util.Map;

/**
 * 文本工具类
 * 对应 luoshu util.ts 中的 normalizeText、compareTwoStrings 等
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
public final class TextUtils {

    private TextUtils() {}

    private static final String IMAGE_UNICODE = "\uFFFC";

    /**
     * 文本规范化：去除所有空白字符
     * 对应 luoshu Util.normalizeText
     */
    public static String normalizeText(String text) {
        if (text == null) return "";
        return text.replaceAll("[\\s\\r\\n\\t\\u00a0]+", "");
    }

    /**
     * 判断段落是否为空或纯图片
     */
    public static boolean isEmptyOrImage(String text) {
        if (text == null) return true;
        String cleaned = text.replaceAll("[\\s\\r\\n\\t\\u00a0]+", "").trim();
        return cleaned.isEmpty() || cleaned.equals(IMAGE_UNICODE);
    }

    /**
     * 移除图片 Unicode 字符
     */
    public static String removeImageChars(String text) {
        if (text == null) return "";
        return text.replace(IMAGE_UNICODE, "");
    }

    /**
     * Dice 系数相似度（基于 bigram）
     * 对应 luoshu Util.compareTwoStrings
     */
    public static double diceSimilarity(String a, String b) {
        if (a == null || b == null) return 0;
        if (a.equals(b)) return 1.0;
        if (a.length() < 2 || b.length() < 2) return a.equals(b) ? 1.0 : 0.0;

        Map<String, Integer> bigramsA = new HashMap<>();
        for (int i = 0; i < a.length() - 1; i++) {
            String bg = a.substring(i, i + 2);
            bigramsA.merge(bg, 1, Integer::sum);
        }

        int intersection = 0;
        for (int i = 0; i < b.length() - 1; i++) {
            String bg = b.substring(i, i + 2);
            if (bigramsA.getOrDefault(bg, 0) > 0) {
                intersection++;
                bigramsA.merge(bg, -1, Integer::sum);
            }
        }

        return (2.0 * intersection) / ((a.length() - 1) + (b.length() - 1));
    }
}
