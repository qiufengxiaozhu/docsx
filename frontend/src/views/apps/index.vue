<template>
  <div class="apps-page">
    <div class="page-header">
      <h2>应用管理</h2>
      <el-button type="primary" @click="showCreateDialog">新增应用</el-button>
    </div>

    <el-table :data="apps" border style="margin-top: 16px" v-loading="loading">
      <el-table-column prop="appName" label="应用名称" min-width="140" />
      <el-table-column prop="appKey" label="App Key" min-width="200">
        <template #default="{ row }">
          <el-text type="info" size="small">{{ row.appKey }}</el-text>
        </template>
      </el-table-column>
      <el-table-column prop="appSecret" label="App Secret" min-width="220">
        <template #default="{ row }">
          <el-text type="info" size="small">{{ maskSecret(row.appSecret) }}</el-text>
          <el-button size="small" link @click="showSecret(row)">查看</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="editApp(row)">编辑</el-button>
          <el-button size="small" type="warning" @click="regenerate(row)">重置密钥</el-button>
          <el-button size="small" type="danger" @click="deleteApp(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="editingApp ? '编辑应用' : '新增应用'" width="500">
      <el-form :model="form" label-width="100px">
        <el-form-item label="应用名称" required>
          <el-input v-model="form.appName" placeholder="请输入应用名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="应用描述" />
        </el-form-item>
        <el-form-item label="回调地址">
          <el-input v-model="form.callbackUrl" placeholder="https://..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApp">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const apps = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const editingApp = ref<any>(null)
const form = ref({ appName: '', description: '', callbackUrl: '' })

const loadApps = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/api/apps')
    if (res.data.code === 200) {
      apps.value = res.data.data.records || []
    }
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  editingApp.value = null
  form.value = { appName: '', description: '', callbackUrl: '' }
  dialogVisible.value = true
}

const editApp = (row: any) => {
  editingApp.value = row
  form.value = { appName: row.appName, description: row.description || '', callbackUrl: row.callbackUrl || '' }
  dialogVisible.value = true
}

const submitApp = async () => {
  if (!form.value.appName) {
    ElMessage.warning('请输入应用名称')
    return
  }
  try {
    if (editingApp.value) {
      await request.put(`/admin/api/apps/${editingApp.value.id}`, form.value)
      ElMessage.success('更新成功')
    } else {
      await request.post('/admin/api/apps', form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadApps()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const deleteApp = async (row: any) => {
  await ElMessageBox.confirm(`确定删除应用 "${row.appName}" 吗？`, '确认删除', { type: 'warning' })
  await request.delete(`/admin/api/apps/${row.id}`)
  ElMessage.success('已删除')
  loadApps()
}

const regenerate = async (row: any) => {
  await ElMessageBox.confirm('重置密钥后原密钥将失效，确定继续？', '重置密钥', { type: 'warning' })
  const res = await request.post(`/admin/api/apps/${row.id}/regenerate-secret`)
  if (res.data.code === 200) {
    ElMessage.success('密钥已重置')
    loadApps()
  }
}

const showSecret = (row: any) => {
  ElMessageBox.alert(row.appSecret, 'App Secret', { confirmButtonText: '关闭' })
}

const maskSecret = (secret: string) => {
  if (!secret) return ''
  return secret.substring(0, 8) + '****' + secret.substring(secret.length - 4)
}

onMounted(loadApps)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; }
</style>
