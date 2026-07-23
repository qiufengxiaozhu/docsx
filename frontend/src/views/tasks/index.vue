<template>
  <div>
    <h2>任务监控</h2>
    <el-table :data="[]" border style="margin-top: 16px">
      <el-table-column prop="taskId" label="任务ID" />
      <el-table-column prop="appId" label="应用" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="docType" label="文档类型" />
      <el-table-column prop="similarity" label="相似度" />
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作" width="120">
        <template #default>
          <el-button size="small" type="danger">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
