<template>
  <el-container style="height: 100vh">
    <el-aside width="200px" style="background: #304156">
      <div class="logo">Docsx</div>
      <el-menu :default-active="$route.path" router background-color="#304156"
               text-color="#bfcbd9" active-text-color="#409eff">
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/apps">
          <el-icon><Grid /></el-icon>
          <span>应用管理</span>
        </el-menu-item>
        <el-menu-item index="/tasks">
          <el-icon><List /></el-icon>
          <span>任务监控</span>
        </el-menu-item>
        <el-menu-item index="/fonts">
          <el-icon><Edit /></el-icon>
          <span>字体管理</span>
        </el-menu-item>
        <el-menu-item index="/settings">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background:#fff;border-bottom:1px solid #e4e7ed;display:flex;align-items:center;justify-content:flex-end;padding:0 20px;">
        <el-dropdown @command="handleCommand">
          <span style="cursor:pointer;color:#606266;">
            管理员 <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main style="padding: 20px; background: #f0f2f5; overflow: auto;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Odometer, Grid, List, Edit, Setting, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = (cmd: string) => {
  if (cmd === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.logo {
  padding: 16px;
  color: #fff;
  font-size: 20px;
  font-weight: bold;
  text-align: center;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
</style>
