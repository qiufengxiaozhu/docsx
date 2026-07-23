package com.docsx.service;

import com.docsx.engine.diff.CompareEngine;
import com.docsx.engine.diff.SegmentDiffEngine;
import com.docsx.engine.extractor.AsposeLicenseLoader;
import com.docsx.engine.extractor.AsposeWordExtractor;
import com.docsx.engine.model.*;
import com.docsx.model.entity.CompareTask;
import com.docsx.mapper.CompareTaskMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 比对服务：编排 Aspose 提取 → NW 段落对齐 → segmentDiff 词级差异
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@Service
public class CompareService {

    private static final Logger log = LoggerFactory.getLogger(CompareService.class);

    private final CompareTaskMapper compareTaskMapper;
    private final ObjectMapper objectMapper;

    public CompareService(CompareTaskMapper compareTaskMapper, ObjectMapper objectMapper) {
        this.compareTaskMapper = compareTaskMapper;
        this.objectMapper = objectMapper;
    }

    /**
     * 创建比对任务，返回 taskId
     *
     * @param appKey   应用 key
     * @param file1    文档A路径
     * @param file2    文档B路径
     * @param fileName1 文件名A
     * @param fileName2 文件名B
     * @return 任务 sessionId
     */
    public String createTask(String appKey, String file1, String file2,
                             String fileName1, String fileName2) {
        String sessionId = UUID.randomUUID().toString().replace("-", "");

        CompareTask task = new CompareTask();
        task.setSessionId(sessionId);
        task.setAppKey(appKey);
        task.setFile1Path(file1);
        task.setFile2Path(file2);
        task.setFileName1(fileName1);
        task.setFileName2(fileName2);
        task.setStatus("PENDING");
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        compareTaskMapper.insert(task);

        return sessionId;
    }

    /**
     * 异步执行比对
     *
     * @param sessionId 任务ID
     */
    @Async("docsxTaskExecutor")
    public void executeCompare(String sessionId) {
        CompareTask task = getTaskBySessionId(sessionId);
        if (task == null) {
            log.error("Task not found: {}", sessionId);
            return;
        }

        try {
            // 更新状态为执行中
            updateTaskStatus(task, "RUNNING");

            // 加载 Aspose 许可证
            AsposeLicenseLoader.loadWordsLicense();

            // 提取文档内容
            File file1 = new File(task.getFile1Path());
            File file2 = new File(task.getFile2Path());

            if (!file1.exists() || !file2.exists()) {
                throw new RuntimeException("File not found: " + task.getFile1Path() + " or " + task.getFile2Path());
            }

            log.info("[{}] Extracting document content...", sessionId);
            AsposeWordExtractor.ExtractResult extract1 = AsposeWordExtractor.extract(file1);
            AsposeWordExtractor.ExtractResult extract2 = AsposeWordExtractor.extract(file2);

            log.info("[{}] doc1 paragraphs: {}, doc2 paragraphs: {}",
                    sessionId, extract1.getParagraphs().size(), extract2.getParagraphs().size());

            // NW 段落对齐
            log.info("[{}] Running NW paragraph alignment...", sessionId);
            CompareResult preResult = CompareEngine.compareWordWithBlocks(
                    extract1.getParagraphs(),
                    extract2.getParagraphs(),
                    extract1.getBlocks(),
                    extract2.getBlocks()
            );

            // segmentDiff：对 MODIFY 项做词级 diff
            log.info("[{}] Running segment diff on {} edit pairs...", sessionId,
                    preResult.getDoc1EditList().size());
            Map<String, Object> enrichedTotalList = enrichWithSegmentDiff(preResult);

            preResult.setTotalList(enrichedTotalList);

            // 序列化并保存结果
            String resultJson = objectMapper.writeValueAsString(preResult);
            task.setResultJson(resultJson);
            updateTaskStatus(task, "COMPLETED");
            log.info("[{}] Compare completed, similarity: {}", sessionId,
                    String.format("%.4f", preResult.getSimilarityScore()));

        } catch (Exception e) {
            log.error("[{}] Compare failed", sessionId, e);
            task.setErrorMessage(e.getMessage());
            updateTaskStatus(task, "FAILED");
        }
    }

    /**
     * 对 MODIFY 项做词级 segmentDiff，填充 totalList
     * 对应 luoshu harness.ts:segmentDiffOnly
     */
    private Map<String, Object> enrichWithSegmentDiff(CompareResult preResult) {
        Map<String, Object> enriched = new LinkedHashMap<>(preResult.getTotalList());
        List<DocContents> editList1 = preResult.getDoc1EditList();
        List<DocContents> editList2 = preResult.getDoc2EditList();

        int editIdx = 0;
        for (Map.Entry<String, Object> entry : enriched.entrySet()) {
            Object val = entry.getValue();
            if (val instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> m = (Map<String, Object>) val;
                if (m.isEmpty() && editIdx < editList1.size()) {
                    // MODIFY 占位 → 执行 segmentDiff
                    DocContents d1 = editList1.get(editIdx);
                    DocContents d2 = editList2.get(editIdx);

                    List<SegmentDiffItem> segDiffs = SegmentDiffEngine.segmentDiff(
                            d1.getContent(), d2.getContent());

                    Map<String, Object> modified = new LinkedHashMap<>();
                    modified.put("type", "modify");
                    modified.put("pId1", d1.getId());
                    modified.put("pId2", d2.getId());
                    modified.put("text1", d1.getContent());
                    modified.put("text2", d2.getContent());
                    modified.put("segments", segDiffs);
                    entry.setValue(modified);

                    editIdx++;
                }
            }
        }
        return enriched;
    }

    /**
     * 获取任务状态
     */
    public CompareTask getTaskBySessionId(String sessionId) {
        return compareTaskMapper.selectBySessionId(sessionId);
    }

    /**
     * 获取比对结果
     */
    public CompareResult getResult(String sessionId) throws Exception {
        CompareTask task = getTaskBySessionId(sessionId);
        if (task == null || !"COMPLETED".equals(task.getStatus())) {
            return null;
        }
        return objectMapper.readValue(task.getResultJson(), CompareResult.class);
    }

    private void updateTaskStatus(CompareTask task, String status) {
        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        compareTaskMapper.updateById(task);
    }
}
