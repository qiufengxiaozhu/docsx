<template>
  <div>
    <h2>应用管理</h2>
    <el-button type="primary" style="margin: 16px 0">新增应用</el-button>
    <el-table :data="[]" border>
      <el-table-column prop="appId" label="App ID" />
      <el-table-column prop="appName" label="应用名称" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作" width="200">
        <template #default>
          <el-button size="small">编辑</el-button>
          <el-button size="small" type="danger">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
