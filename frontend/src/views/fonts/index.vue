<template>
  <div>
    <h2>字体管理</h2>
    <el-upload action="/admin/api/fonts/upload" :show-file-list="false" style="margin: 16px 0">
      <el-button type="primary">上传字体</el-button>
    </el-upload>
    <el-table :data="[]" border>
      <el-table-column prop="fontName" label="字体名称" />
      <el-table-column prop="fontFamily" label="字体族" />
      <el-table-column prop="fileName" label="文件名" />
      <el-table-column prop="fileSize" label="文件大小" />
      <el-table-column prop="createdAt" label="上传时间" />
      <el-table-column label="操作" width="120">
        <template #default>
          <el-button size="small" type="danger">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
