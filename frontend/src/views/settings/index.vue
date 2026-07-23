<template>
  <div class="settings-page">
    <h2>系统设置</h2>

    <el-card style="margin-top: 20px">
      <template #header>接入文档</template>
      <div class="doc-section">
        <h4>三方系统接入说明</h4>
        <el-descriptions border :column="1" style="margin-top: 12px">
          <el-descriptions-item label="比对接口">POST /api/compare/create</el-descriptions-item>
          <el-descriptions-item label="认证方式">HMAC-MD5 签名</el-descriptions-item>
          <el-descriptions-item label="签名算法">MD5(app_secret + "@@" + timestamp + "@@" + nonce)</el-descriptions-item>
          <el-descriptions-item label="请求头">
            X-App-Key, X-Timestamp, X-Nonce, X-Sign
          </el-descriptions-item>
          <el-descriptions-item label="请求参数">
            file1 (文档A), file2 (文档B), callbackUrl (可选)
          </el-descriptions-item>
          <el-descriptions-item label="返回">
            {"sessionId": "xxx", "viewUrl": "/view/xxx"}
          </el-descriptions-item>
        </el-descriptions>

        <h4 style="margin-top: 24px">查询状态</h4>
        <el-descriptions border :column="1" style="margin-top: 12px">
          <el-descriptions-item label="接口">GET /api/compare/status/{sessionId}</el-descriptions-item>
          <el-descriptions-item label="返回状态">PENDING / RUNNING / COMPLETED / FAILED</el-descriptions-item>
        </el-descriptions>

        <h4 style="margin-top: 24px">获取结果</h4>
        <el-descriptions border :column="1" style="margin-top: 12px">
          <el-descriptions-item label="接口">GET /api/compare/result/{sessionId}</el-descriptions-item>
          <el-descriptions-item label="嵌入页面">iframe src="/view/{sessionId}"</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.settings-page h2 { margin-bottom: 0; }
.doc-section h4 { color: #1f2937; margin-top: 16px; }
</style>
