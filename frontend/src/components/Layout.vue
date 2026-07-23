<template>
  <el-container style="height: 100vh">
    <el-aside width="220px" style="background: #1e1e2d">
      <div class="logo">Docsx</div>
      <el-menu :default-active="$route.path" router background-color="#1e1e2d"
               text-color="#9ca3af" active-text-color="#fff">
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/compare" class="menu-highlight">
          <el-icon><DocumentCopy /></el-icon>
          <span>文档比对</span>
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
import { Odometer, Grid, List, Edit, Setting, ArrowDown, DocumentCopy } from '@element-plus/icons-vue'

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
  padding: 20px 16px;
  color: #fff;
  font-size: 22px;
  font-weight: 700;
  text-align: center;
  letter-spacing: -0.5px;
  background: rgba(79,70,229,0.15);
}

:deep(.el-menu) {
  border-right: none !important;
}

:deep(.el-menu-item) {
  margin: 4px 8px;
  border-radius: 8px;
  height: 44px;
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #4F46E5, #7C3AED) !important;
}

:deep(.el-menu-item:hover:not(.is-active)) {
  background: rgba(255,255,255,0.05) !important;
}
</style>
