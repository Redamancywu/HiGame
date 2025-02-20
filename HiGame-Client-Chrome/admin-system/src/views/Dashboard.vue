<template>
  <div class="dashboard-container">
    <h1>欢迎来到HiGame管理系统</h1>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="data-card">
          <template #header>
            <div class="card-header">
              <span>总用户数</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ stats.totalUsers }}</h2>
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
            <h2>{{ stats.sdkUsers }}</h2>
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
            <h2>{{ stats.appUsers }}</h2>
            <el-icon><Iphone /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势图表 -->
    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span>用户趋势</span>
        </div>
      </template>
      <div class="chart-container">
        <div ref="chartRef" style="width: 100%; height: 400px;"></div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, Connection, Iphone } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getStats, getUserTrends } from '../api/dashboard'

const stats = ref({
  totalUsers: 0,
  sdkUsers: 0,
  appUsers: 0,
  activeUsers: 0,
  newUsers: 0,
  onlineDevices: 0
})

const chartRef = ref(null)
let chart = null

// 获取统计数据
const fetchStats = async () => {
  try {
    const response = await getStats()
    stats.value = response.data
  } catch (error) {
    console.error('Failed to fetch stats:', error)
  }
}

// 获取趋势数据并渲染图表
const fetchTrends = async () => {
  try {
    const response = await getUserTrends()
    const { trends } = response.data
    
    // 初始化图表
    if (!chart) {
      chart = echarts.init(chartRef.value)
    }
    
    // 处理数据
    const dates = trends.map(item => item.date)
    const activeUsers = trends.map(item => item.activeUsers)
    const newUsers = trends.map(item => item.newUsers)
    
    // 设置图表配置
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['活跃用户', '新增用户']
      },
      xAxis: {
        type: 'category',
        data: dates
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '活跃用户',
          type: 'line',
          data: activeUsers
        },
        {
          name: '新增用户',
          type: 'line',
          data: newUsers
        }
      ]
    }
    
    chart.setOption(option)
  } catch (error) {
    console.error('Failed to fetch trends:', error)
  }
}

onMounted(() => {
  fetchStats()
  fetchTrends()
  
  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    chart?.resize()
  })
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

.chart-card {
  margin-top: 20px;
}

.chart-container {
  padding: 20px;
}
</style>
