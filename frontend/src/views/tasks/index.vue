<template>
  <div class="tasks-page">
    <div class="page-header">
      <h2>任务监控</h2>
      <div class="filters">
        <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 120px" @change="loadTasks">
          <el-option label="等待中" value="PENDING" />
          <el-option label="执行中" value="RUNNING" />
          <el-option label="已完成" value="COMPLETED" />
          <el-option label="失败" value="FAILED" />
        </el-select>
        <el-button @click="loadTasks">刷新</el-button>
      </div>
    </div>

    <el-table :data="tasks" border style="margin-top: 16px" v-loading="loading">
      <el-table-column prop="sessionId" label="会话ID" width="200">
        <template #default="{ row }">
          <el-text type="info" size="small" truncated>{{ row.sessionId }}</el-text>
        </template>
      </el-table-column>
      <el-table-column prop="fileName1" label="文档A" min-width="140" show-overflow-tooltip />
      <el-table-column prop="fileName2" label="文档B" min-width="140" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="similarity" label="相似度" width="90">
        <template #default="{ row }">
          {{ row.similarity != null ? (row.similarity * 100).toFixed(1) + '%' : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" v-if="row.status === 'COMPLETED'"
                     @click="viewResult(row)">查看结果</el-button>
          <el-button size="small" type="danger" @click="deleteTask(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      style="margin-top: 16px; justify-content: flex-end;"
      background
      layout="total, prev, pager, next"
      :total="total"
      :page-size="pageSize"
      v-model:current-page="currentPage"
      @current-change="loadTasks"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const tasks = ref<any[]>([])
const loading = ref(false)
const filterStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const loadTasks = async () => {
  loading.value = true
  try {
    const params: any = { page: currentPage.value, size: pageSize.value }
    if (filterStatus.value) params.status = filterStatus.value
    const res = await request.get('/admin/api/tasks', { params })
    if (res.data.code === 200) {
      tasks.value = res.data.data.records || []
      total.value = res.data.data.total || 0
    }
  } finally {
    loading.value = false
  }
}

const deleteTask = async (row: any) => {
  await ElMessageBox.confirm('确定删除该任务？', '确认删除', { type: 'warning' })
  await request.delete(`/admin/api/tasks/${row.id}`)
  ElMessage.success('已删除')
  loadTasks()
}

const viewResult = (row: any) => {
  window.open(`/view/${row.sessionId}`, '_blank')
}

const statusType = (status: string) => {
  const map: Record<string, string> = {
    PENDING: 'info', RUNNING: 'warning', COMPLETED: 'success', FAILED: 'danger'
  }
  return map[status] || 'info'
}

const statusText = (status: string) => {
  const map: Record<string, string> = {
    PENDING: '等待中', RUNNING: '执行中', COMPLETED: '已完成', FAILED: '失败'
  }
  return map[status] || status
}

onMounted(loadTasks)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; }
.filters { display: flex; gap: 8px; }
</style>
