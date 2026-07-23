<template>
  <div style="display: flex; align-items: center; justify-content: center; height: 100vh; background: #f0f2f5">
    <el-card style="width: 400px">
      <template #header><h2 style="text-align: center; margin: 0">Docsx 管理后台</h2></template>
      <el-form @submit.prevent="handleLogin">
        <el-form-item><el-input v-model="form.username" placeholder="用户名" /></el-form-item>
        <el-form-item><el-input v-model="form.password" type="password" placeholder="密码" /></el-form-item>
        <el-form-item><el-button type="primary" style="width: 100%" @click="handleLogin">登录</el-button></el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const form = reactive({ username: '', password: '' })

async function handleLogin() {
  try {
    const res: any = await request.post('/admin/api/auth/login', form)
    if (res.code === 200) {
      userStore.setToken(res.data.token)
      router.push('/')
    } else {
      ElMessage.error(res.msg || '登录失败')
    }
  } catch (e) {
    ElMessage.error('网络错误')
  }
}
</script>
