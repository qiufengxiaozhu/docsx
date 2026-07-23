package com.docsx.engine.model;

import java.util.List;
import java.util.Map;

/**
 * 比对结果（对应 luoshu compareDocuments.ts 的 CompareResult）
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
public class CompareResult {

    private List<DocContents> doc1EditList;
    private List<DocContents> doc2EditList;
    private Map<String, Object> totalList;
    private double similarityScore;
    private int editDistance;
    private boolean allContentsCompared;
    private int doc1ParagraphCount;
    private int doc2ParagraphCount;

    public List<DocContents> getDoc1EditList() { return doc1EditList; }
    public void setDoc1EditList(List<DocContents> doc1EditList) { this.doc1EditList = doc1EditList; }

    public List<DocContents> getDoc2EditList() { return doc2EditList; }
    public void setDoc2EditList(List<DocContents> doc2EditList) { this.doc2EditList = doc2EditList; }

    public Map<String, Object> getTotalList() { return totalList; }
    public void setTotalList(Map<String, Object> totalList) { this.totalList = totalList; }

    public double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(double similarityScore) { this.similarityScore = similarityScore; }

    public int getEditDistance() { return editDistance; }
    public void setEditDistance(int editDistance) { this.editDistance = editDistance; }

    public boolean isAllContentsCompared() { return allContentsCompared; }
    public void setAllContentsCompared(boolean allContentsCompared) { this.allContentsCompared = allContentsCompared; }

    public int getDoc1ParagraphCount() { return doc1ParagraphCount; }
    public void setDoc1ParagraphCount(int doc1ParagraphCount) { this.doc1ParagraphCount = doc1ParagraphCount; }

    public int getDoc2ParagraphCount() { return doc2ParagraphCount; }
    public void setDoc2ParagraphCount(int doc2ParagraphCount) { this.doc2ParagraphCount = doc2ParagraphCount; }
}
