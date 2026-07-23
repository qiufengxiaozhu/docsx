<template>
  <div>
    <h2>仪表盘</h2>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6"><el-card><el-statistic title="今日比对任务" :value="0" /></el-card></el-col>
      <el-col :span="6"><el-card><el-statistic title="接入应用数" :value="0" /></el-card></el-col>
      <el-col :span="6"><el-card><el-statistic title="总任务数" :value="0" /></el-card></el-col>
      <el-col :span="6"><el-card><el-statistic title="字体数量" :value="0" /></el-card></el-col>
    </el-row>
  </div>
</template>
