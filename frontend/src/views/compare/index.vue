<template>
  <div class="compare-page">
    <div class="page-header">
      <h2>文档比对</h2>
      <el-tag type="info" size="small">Word (DOCX)</el-tag>
    </div>

    <div class="compare-container">
      <!-- 文档 A -->
      <div class="compare-panel">
        <div class="panel-title">
          <span class="panel-label">A</span>
          <span>原始文档</span>
        </div>
        <div
          class="upload-area"
          :class="{ 'dragover': dragA, 'has-file': fileA }"
          @dragover.prevent="dragA = true"
          @dragleave="dragA = false"
          @drop.prevent="handleDrop($event, 'A')"
          @click="triggerUpload('A')"
        >
          <template v-if="!fileA">
            <div class="upload-icon">
              <el-icon :size="40"><UploadFilled /></el-icon>
            </div>
            <p class="upload-text">拖拽文件到此处，或 <em>点击上传</em></p>
            <p class="upload-hint">支持 .docx 格式，最大 100MB</p>
          </template>
          <template v-else>
            <div class="file-preview">
              <el-icon :size="32" color="#4F46E5"><Document /></el-icon>
              <div class="file-detail">
                <span class="file-name">{{ fileA.name }}</span>
                <span class="file-size">{{ formatSize(fileA.size) }}</span>
              </div>
              <el-button type="danger" size="small" circle :icon="Close" @click.stop="removeFile('A')" />
            </div>
          </template>
        </div>
        <input ref="inputA" type="file" accept=".docx" style="display:none" @change="handleSelect($event, 'A')" />
      </div>

      <!-- VS 分隔 -->
      <div class="vs-divider">
        <div class="vs-icon">VS</div>
      </div>

      <!-- 文档 B -->
      <div class="compare-panel">
        <div class="panel-title">
          <span class="panel-label panel-label-b">B</span>
          <span>比对文档</span>
        </div>
        <div
          class="upload-area"
          :class="{ 'dragover': dragB, 'has-file': fileB }"
          @dragover.prevent="dragB = true"
          @dragleave="dragB = false"
          @drop.prevent="handleDrop($event, 'B')"
          @click="triggerUpload('B')"
        >
          <template v-if="!fileB">
            <div class="upload-icon">
              <el-icon :size="40"><UploadFilled /></el-icon>
            </div>
            <p class="upload-text">拖拽文件到此处，或 <em>点击上传</em></p>
            <p class="upload-hint">支持 .docx 格式，最大 100MB</p>
          </template>
          <template v-else>
            <div class="file-preview">
              <el-icon :size="32" color="#7C3AED"><Document /></el-icon>
              <div class="file-detail">
                <span class="file-name">{{ fileB.name }}</span>
                <span class="file-size">{{ formatSize(fileB.size) }}</span>
              </div>
              <el-button type="danger" size="small" circle :icon="Close" @click.stop="removeFile('B')" />
            </div>
          </template>
        </div>
        <input ref="inputB" type="file" accept=".docx" style="display:none" @change="handleSelect($event, 'B')" />
      </div>
    </div>

    <!-- 比对按钮 -->
    <div class="compare-action">
      <el-button
        type="primary"
        size="large"
        :disabled="!fileA || !fileB"
        :loading="comparing"
        @click="startCompare"
        class="compare-btn"
      >
        <el-icon v-if="!comparing"><VideoPlay /></el-icon>
        {{ comparing ? '比对中...' : '开始比对' }}
      </el-button>
    </div>

    <!-- 比对历史 -->
    <div class="history-section" v-if="history.length > 0">
      <h3>最近比对</h3>
      <div class="history-list">
        <div class="history-item" v-for="item in history" :key="item.sessionId" @click="viewResult(item)">
          <div class="history-files">
            <span>{{ item.fileName1 }}</span>
            <el-icon><Right /></el-icon>
            <span>{{ item.fileName2 }}</span>
          </div>
          <div class="history-meta">
            <el-tag :type="statusType(item.status)" size="small">{{ statusText(item.status) }}</el-tag>
            <span class="history-time">{{ item.createdAt }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled, Document, Close, VideoPlay, Right } from '@element-plus/icons-vue'
import request from '@/utils/request'

const fileA = ref<File | null>(null)
const fileB = ref<File | null>(null)
const dragA = ref(false)
const dragB = ref(false)
const comparing = ref(false)
const history = ref<any[]>([])

const inputA = ref<HTMLInputElement>()
const inputB = ref<HTMLInputElement>()

const triggerUpload = (side: string) => {
  if (side === 'A' && !fileA.value) inputA.value?.click()
  if (side === 'B' && !fileB.value) inputB.value?.click()
}

const handleDrop = (e: DragEvent, side: string) => {
  dragA.value = false
  dragB.value = false
  const files = e.dataTransfer?.files
  if (files && files.length > 0) {
    setFile(files[0], side)
  }
}

const handleSelect = (e: Event, side: string) => {
  const input = e.target as HTMLInputElement
  if (input.files && input.files.length > 0) {
    setFile(input.files[0], side)
    input.value = ''
  }
}

const setFile = (file: File, side: string) => {
  if (!file.name.toLowerCase().endsWith('.docx')) {
    ElMessage.warning('请选择 .docx 格式文件')
    return
  }
  if (file.size > 100 * 1024 * 1024) {
    ElMessage.warning('文件大小不能超过 100MB')
    return
  }
  if (side === 'A') fileA.value = file
  else fileB.value = file
}

const removeFile = (side: string) => {
  if (side === 'A') fileA.value = null
  else fileB.value = null
}

const startCompare = async () => {
  if (!fileA.value || !fileB.value) return
  comparing.value = true

  try {
    const formData = new FormData()
    formData.append('file1', fileA.value)
    formData.append('file2', fileB.value)

    const res = await request.post('/api/compare/create', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    if (res.data.code === 200) {
      const { sessionId, viewUrl } = res.data.data
      ElMessage.success('比对任务已创建')
      window.open(viewUrl || `/view/${sessionId}`, '_blank')
      loadHistory()
    } else {
      ElMessage.error(res.data.msg || '创建比对任务失败')
    }
  } catch (e: any) {
    ElMessage.error('比对请求失败')
  } finally {
    comparing.value = false
  }
}

const loadHistory = async () => {
  try {
    const res = await request.get('/admin/api/tasks', { params: { page: 1, size: 5 } })
    if (res.data.code === 200) {
      history.value = res.data.data.records || []
    }
  } catch (e) {}
}

const viewResult = (item: any) => {
  if (item.status === 'COMPLETED') {
    window.open(`/view/${item.sessionId}`, '_blank')
  } else {
    ElMessage.info('任务' + statusText(item.status))
  }
}

const formatSize = (bytes: number) => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / 1024 / 1024).toFixed(1) + ' MB'
}

const statusType = (s: string) => ({ PENDING: 'info', RUNNING: 'warning', COMPLETED: 'success', FAILED: 'danger' }[s] || 'info')
const statusText = (s: string) => ({ PENDING: '等待中', RUNNING: '执行中', COMPLETED: '已完成', FAILED: '失败' }[s] || s)

onMounted(loadHistory)
</script>

<style scoped>
.compare-page { max-width: 1000px; margin: 0 auto; }

.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.compare-container {
  display: flex;
  gap: 0;
  align-items: stretch;
}

.compare-panel {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 16px;
}

.panel-label {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #4F46E5;
  color: #fff;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 700;
}

.panel-label-b { background: #7C3AED; }

.upload-area {
  border: 2px dashed #d1d5db;
  border-radius: 12px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  min-height: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.upload-area:hover { border-color: #a5b4fc; background: #f5f3ff; }
.upload-area.dragover { border-color: #4F46E5; background: #eef2ff; }
.upload-area.has-file { border-style: solid; border-color: #c7d2fe; background: #fafafe; cursor: default; }

.upload-icon { color: #9ca3af; margin-bottom: 12px; }
.upload-text { color: #6b7280; font-size: 14px; margin: 0; }
.upload-text em { color: #4F46E5; font-style: normal; font-weight: 500; }
.upload-hint { color: #9ca3af; font-size: 12px; margin-top: 6px; }

.file-preview {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 8px;
}

.file-detail {
  flex: 1;
  text-align: left;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.file-name { font-size: 14px; font-weight: 500; color: #1f2937; word-break: break-all; }
.file-size { font-size: 12px; color: #9ca3af; }

.vs-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  flex-shrink: 0;
}

.vs-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4F46E5, #7C3AED);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(79,70,229,0.3);
}

.compare-action {
  text-align: center;
  margin-top: 28px;
}

.compare-btn {
  min-width: 200px;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #4F46E5, #7C3AED);
  border: none;
}

.compare-btn:hover:not(:disabled) { background: linear-gradient(135deg, #4338CA, #6D28D9); }

.history-section {
  margin-top: 40px;
}

.history-section h3 {
  font-size: 15px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 12px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  transition: box-shadow 0.15s;
}

.history-item:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.06); }

.history-files {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #374151;
}

.history-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.history-time { font-size: 12px; color: #9ca3af; }
</style>
