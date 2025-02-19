<template>
  <div class="dashboard-container">
    <h1>欢迎来到HiGame管理系统</h1>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="data-card">
          <template #header>
            <div class="card-header">
              <span>总用户数</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ statistics.totalUsers || 0 }}</h2>
            <el-icon><User /></el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="data-card">
          <template #header>
            <div class="card-header">
              <span>SDK用户数</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ statistics.sdkUsers || 0 }}</h2>
            <el-icon><Connection /></el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="data-card">
          <template #header>
            <div class="card-header">
              <span>APP用户数</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ statistics.appUsers || 0 }}</h2>
            <el-icon><Iphone /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, Connection, Iphone } from '@element-plus/icons-vue'

const statistics = ref({
  totalUsers: 0,
  sdkUsers: 0,
  appUsers: 0
})

onMounted(() => {
  // 模拟获取统计数据
  statistics.value = {
    totalUsers: 650,
    sdkUsers: 350,
    appUsers: 300
  }
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.data-card {
  margin-bottom: 20px;
}

.card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content h2 {
  margin: 0;
  font-size: 24px;
  color: #409EFF;
}

.card-content .el-icon {
  font-size: 48px;
  color: #409EFF;
  opacity: 0.6;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
