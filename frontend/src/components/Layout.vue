<template>
  <el-container style="height: 100vh">
    <el-aside width="200px" style="background: #304156">
      <div style="padding: 16px; color: #fff; font-size: 18px; font-weight: bold; text-align: center">
        Docsx
      </div>
      <el-menu :default-active="$route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409eff">
        <el-menu-item index="/dashboard">
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/apps">
          <span>应用管理</span>
        </el-menu-item>
        <el-menu-item index="/tasks">
          <span>任务监控</span>
        </el-menu-item>
        <el-menu-item index="/fonts">
          <span>字体管理</span>
        </el-menu-item>
        <el-menu-item index="/settings">
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-main style="padding: 20px; background: #f0f2f5">
      <router-view />
    </el-main>
  </el-container>
</template>
