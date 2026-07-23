<template>
  <div>
    <h2>系统设置</h2>
    <el-form label-width="140px" style="max-width: 600px; margin-top: 20px">
      <el-form-item label="任务过期时间(h)"><el-input-number :min="1" :max="720" :model-value="24" /></el-form-item>
      <el-form-item label="最大文件大小(MB)"><el-input-number :min="1" :max="500" :model-value="100" /></el-form-item>
      <el-form-item label="线程池大小"><el-input-number :min="1" :max="16" :model-value="4" /></el-form-item>
      <el-form-item><el-button type="primary">保存设置</el-button></el-form-item>
    </el-form>
  </div>
</template>
