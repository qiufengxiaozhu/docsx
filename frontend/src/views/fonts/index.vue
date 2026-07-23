<template>
  <div class="fonts-page">
    <div class="page-header">
      <h2>字体管理</h2>
      <el-upload
        :action="uploadUrl"
        :headers="uploadHeaders"
        :before-upload="beforeUpload"
        :on-success="onUploadSuccess"
        :on-error="onUploadError"
        :show-file-list="false"
        accept=".ttf,.otf,.woff,.woff2,.ttc"
      >
        <el-button type="primary">上传字体</el-button>
      </el-upload>
    </div>

    <el-table :data="fonts" border style="margin-top: 16px" v-loading="loading">
      <el-table-column prop="fontName" label="字体名称" min-width="160" />
      <el-table-column prop="fontFamily" label="字体族" min-width="140" />
      <el-table-column prop="fileSize" label="文件大小" width="120">
        <template #default="{ row }">
          {{ formatSize(row.fileSize) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="上传时间" width="180" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="deleteFont(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const fonts = ref<any[]>([])
const loading = ref(false)

const uploadUrl = '/admin/api/fonts/upload'
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${userStore.token}`
}))

const loadFonts = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/api/fonts')
    if (res.data.code === 200) {
      fonts.value = res.data.data.records || []
    }
  } finally {
    loading.value = false
  }
}

const beforeUpload = (file: File) => {
  const validExts = ['.ttf', '.otf', '.woff', '.woff2', '.ttc']
  const ext = file.name.substring(file.name.lastIndexOf('.')).toLowerCase()
  if (!validExts.includes(ext)) {
    ElMessage.error('仅支持 ttf/otf/woff/woff2/ttc 格式')
    return false
  }
  if (file.size > 50 * 1024 * 1024) {
    ElMessage.error('字体文件不能超过 50MB')
    return false
  }
  return true
}

const onUploadSuccess = () => {
  ElMessage.success('上传成功')
  loadFonts()
}

const onUploadError = () => {
  ElMessage.error('上传失败')
}

const deleteFont = async (row: any) => {
  await ElMessageBox.confirm(`确定删除字体 "${row.fontName}" 吗？`, '确认删除', { type: 'warning' })
  await request.delete(`/admin/api/fonts/${row.id}`)
  ElMessage.success('已删除')
  loadFonts()
}

const formatSize = (bytes: number) => {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / 1024 / 1024).toFixed(1) + ' MB'
}

onMounted(loadFonts)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; }
</style>
