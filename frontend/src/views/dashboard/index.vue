<template>
  <div class="dashboard">
    <h2>仪表盘</h2>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="总任务数" :value="stats.taskTotal" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="进行中" :value="stats.taskRunning" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="接入应用数" :value="stats.appCount" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="字体数量" :value="stats.fontCount" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>任务状态分布</template>
          <div class="stats-detail">
            <div class="stat-item">
              <span class="stat-label">等待中</span>
              <el-tag type="info">{{ stats.taskPending }}</el-tag>
            </div>
            <div class="stat-item">
              <span class="stat-label">执行中</span>
              <el-tag type="warning">{{ stats.taskRunning }}</el-tag>
            </div>
            <div class="stat-item">
              <span class="stat-label">已完成</span>
              <el-tag type="success">{{ stats.taskCompleted }}</el-tag>
            </div>
            <div class="stat-item">
              <span class="stat-label">失败</span>
              <el-tag type="danger">{{ stats.taskFailed }}</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>系统信息</template>
          <div class="stats-detail">
            <div class="stat-item">
              <span class="stat-label">版本</span>
              <span>1.0.0</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">支持格式</span>
              <span>Word (DOCX)</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">比对算法</span>
              <span>NW + SegmentDiff</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const stats = ref({
  taskTotal: 0,
  taskPending: 0,
  taskRunning: 0,
  taskCompleted: 0,
  taskFailed: 0,
  appCount: 0,
  fontCount: 0
})

onMounted(async () => {
  try {
    const res = await request.get('/admin/api/dashboard')
    if (res.data.code === 200) {
      stats.value = res.data.data
    }
  } catch (e) {
    console.error('Failed to load dashboard data', e)
  }
})
</script>

<style scoped>
.dashboard h2 { margin-bottom: 0; }
.stats-detail { display: flex; flex-direction: column; gap: 12px; }
.stat-item { display: flex; justify-content: space-between; align-items: center; }
.stat-label { color: #606266; }
</style>
