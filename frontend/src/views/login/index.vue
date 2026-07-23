<template>
  <div class="login-page">
    <div class="login-left">
      <div class="brand-area">
        <div class="brand-icon">
          <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="4" y="4" width="18" height="40" rx="3" fill="rgba(255,255,255,0.9)" />
            <rect x="26" y="4" width="18" height="40" rx="3" fill="rgba(255,255,255,0.6)" />
            <path d="M8 14h10M8 20h10M8 26h8" stroke="#4F46E5" stroke-width="2" stroke-linecap="round" />
            <path d="M30 14h10M30 20h10M30 26h8" stroke="#4F46E5" stroke-width="2" stroke-linecap="round" opacity="0.6" />
            <circle cx="24" cy="24" r="6" fill="#4F46E5" opacity="0.8" />
            <path d="M22 24l1.5 1.5L26 22" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
          </svg>
        </div>
        <h1>Docsx</h1>
        <p class="tagline">智能文档比对平台</p>
        <div class="features">
          <div class="feature-item">
            <span class="feature-dot"></span>
            <span>Word / PDF / Excel 多格式支持</span>
          </div>
          <div class="feature-item">
            <span class="feature-dot"></span>
            <span>NW 段落对齐 + 字符级精细差异</span>
          </div>
          <div class="feature-item">
            <span class="feature-dot"></span>
            <span>三方系统 iframe 无缝集成</span>
          </div>
          <div class="feature-item">
            <span class="feature-dot"></span>
            <span>OnlyOffice 在线预览</span>
          </div>
        </div>
      </div>
    </div>

    <div class="login-right">
      <div class="login-form-wrapper">
        <h2>欢迎登录</h2>
        <p class="form-subtitle">管理后台控制面板</p>

        <el-form :model="form" @submit.prevent="handleLogin" class="login-form">
          <el-form-item>
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>
          <el-form-item>
            <el-input
              v-model="form.password"
              placeholder="请输入密码"
              type="password"
              size="large"
              show-password
              :prefix-icon="Lock"
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <span>Docsx v1.0.0</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const form = ref({ username: '', password: '' })

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await request.post('/admin/api/auth/login', form.value)
    if (res.data.code === 200) {
      userStore.setToken(res.data.data.token)
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } else {
      ElMessage.error(res.data.msg || '登录失败')
    }
  } catch (e: any) {
    ElMessage.error('登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #4F46E5 0%, #7C3AED 50%, #9333EA 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-left::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.05) 0%, transparent 70%);
  animation: float 20s infinite linear;
}

@keyframes float {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.brand-area {
  position: relative;
  z-index: 1;
  text-align: center;
  color: #fff;
  padding: 40px;
}

.brand-icon svg {
  width: 72px;
  height: 72px;
  margin-bottom: 20px;
}

.brand-area h1 {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 8px;
  letter-spacing: -0.5px;
}

.tagline {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 40px;
}

.features {
  text-align: left;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  opacity: 0.9;
}

.feature-dot {
  width: 6px;
  height: 6px;
  background: #a5f3fc;
  border-radius: 50%;
  flex-shrink: 0;
}

.login-right {
  flex: 0 0 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
}

.login-form-wrapper {
  width: 360px;
}

.login-form-wrapper h2 {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 6px;
}

.form-subtitle {
  font-size: 14px;
  color: #9ca3af;
  margin-bottom: 36px;
}

.login-form :deep(.el-input__wrapper) {
  padding: 8px 16px;
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e5e7eb inset;
}

.login-form :deep(.el-input__wrapper):hover {
  box-shadow: 0 0 0 1px #a5b4fc inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #4F46E5 inset;
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(135deg, #4F46E5, #7C3AED);
  border: none;
}

.login-btn:hover {
  background: linear-gradient(135deg, #4338CA, #6D28D9);
}

.login-footer {
  margin-top: 40px;
  text-align: center;
  font-size: 12px;
  color: #d1d5db;
}

@media (max-width: 900px) {
  .login-left { display: none; }
  .login-right { flex: 1; }
}
</style>
