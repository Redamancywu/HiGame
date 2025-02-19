&lt;template>
  &lt;div class="dashboard-container">
    &lt;el-row :gutter="20">
      &lt;el-col :span="8">
        &lt;el-card class="data-card">
          &lt;template #header>
            &lt;div class="card-header">
              &lt;span>总用户数&lt;/span>
            &lt;/div>
          &lt;/template>
          &lt;div class="card-content">
            &lt;h2>{{ statistics.totalUsers || 0 }}&lt;/h2>
            &lt;el-icon>&lt;User />&lt;/el-icon>
          &lt;/div>
        &lt;/el-card>
      &lt;/el-col>
      
      &lt;el-col :span="8">
        &lt;el-card class="data-card">
          &lt;template #header>
            &lt;div class="card-header">
              &lt;span>SDK用户数&lt;/span>
            &lt;/div>
          &lt;/template>
          &lt;div class="card-content">
            &lt;h2>{{ statistics.sdkUsers || 0 }}&lt;/h2>
            &lt;el-icon>&lt;Connection />&lt;/el-icon>
          &lt;/div>
        &lt;/el-card>
      &lt;/el-col>
      
      &lt;el-col :span="8">
        &lt;el-card class="data-card">
          &lt;template #header>
            &lt;div class="card-header">
              &lt;span>APP用户数&lt;/span>
            &lt;/div>
          &lt;/template>
          &lt;div class="card-content">
            &lt;h2>{{ statistics.appUsers || 0 }}&lt;/h2>
            &lt;el-icon>&lt;Iphone />&lt;/el-icon>
          &lt;/div>
        &lt;/el-card>
      &lt;/el-col>
    &lt;/el-row>

    &lt;el-row :gutter="20" class="chart-row">
      &lt;el-col :span="12">
        &lt;el-card>
          &lt;template #header>
            &lt;div class="card-header">
              &lt;span>用户增长趋势&lt;/span>
              &lt;el-radio-group v-model="userTrendType" size="small">
                &lt;el-radio-button label="ALL">全部&lt;/el-radio-button>
                &lt;el-radio-button label="SDK">SDK&lt;/el-radio-button>
                &lt;el-radio-button label="APP">APP&lt;/el-radio-button>
              &lt;/el-radio-group>
            &lt;/div>
          &lt;/template>
          &lt;div class="chart-container">
            &lt;div ref="userGrowthChart" class="chart">&lt;/div>
          &lt;/div>
        &lt;/el-card>
      &lt;/el-col>
      
      &lt;el-col :span="12">
        &lt;el-card>
          &lt;template #header>
            &lt;div class="card-header">
              &lt;span>用户分布&lt;/span>
            &lt;/div>
          &lt;/template>
          &lt;div class="chart-container">
            &lt;div ref="userDistributionChart" class="chart">&lt;/div>
          &lt;/div>
        &lt;/el-card>
      &lt;/el-col>
    &lt;/el-row>

    &lt;el-row :gutter="20" class="table-row">
      &lt;el-col :span="24">
        &lt;el-card>
          &lt;template #header>
            &lt;div class="card-header">
              &lt;span>最近活动&lt;/span>
              &lt;el-select v-model="activityType" size="small" style="width: 120px">
                &lt;el-option label="全部" value="ALL" />
                &lt;el-option label="SDK" value="SDK" />
                &lt;el-option label="APP" value="APP" />
              &lt;/el-select>
            &lt;/div>
          &lt;/template>
          &lt;el-table :data="recentActivities" style="width: 100%">
            &lt;el-table-column prop="time" label="时间" width="180" />
            &lt;el-table-column prop="type" label="类型" width="120">
              &lt;template #default="{ row }">
                &lt;el-tag :type="getActivityTagType(row.type)">
                  {{ row.type }}
                &lt;/el-tag>
              &lt;/template>
            &lt;/el-table-column>
            &lt;el-table-column prop="content" label="内容" />
            &lt;el-table-column prop="operator" label="用户" width="120" />
          &lt;/el-table>
        &lt;/el-card>
      &lt;/el-col>
    &lt;/el-row>
  &lt;/div>
&lt;/template>

&lt;script setup>
import { ref, onMounted, watch } from 'vue'
import { User, Connection, Iphone } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const userGrowthChart = ref(null)
const userDistributionChart = ref(null)
const userTrendType = ref('ALL')
const activityType = ref('ALL')

const statistics = ref({
  totalUsers: 0,
  sdkUsers: 0,
  appUsers: 0
})

const recentActivities = ref([
  {
    time: '2025-02-19 14:30:00',
    type: 'SDK',
    content: '用户 John Doe 登录了SDK',
    operator: 'John Doe'
  },
  {
    time: '2025-02-19 14:25:00',
    type: 'APP',
    content: '用户 Jane Smith 在APP中进行了操作',
    operator: 'Jane Smith'
  }
])

const getActivityTagType = (type) => {
  const typeMap = {
    'SDK': 'success',
    'APP': 'warning'
  }
  return typeMap[type] || 'info'
}

const initUserGrowthChart = () => {
  const chart = echarts.init(userGrowthChart.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['SDK用户', 'APP用户', '总用户数']
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: 'SDK用户',
        type: 'line',
        smooth: true,
        data: [100, 150, 200, 250, 300, 350]
      },
      {
        name: 'APP用户',
        type: 'line',
        smooth: true,
        data: [50, 100, 150, 200, 250, 300]
      },
      {
        name: '总用户数',
        type: 'line',
        smooth: true,
        data: [150, 250, 350, 450, 550, 650]
      }
    ]
  }
  chart.setOption(option)
}

const initUserDistributionChart = () => {
  const chart = echarts.init(userDistributionChart.value)
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '用户分布',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 350, name: 'SDK用户' },
          { value: 300, name: 'APP用户' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  chart.setOption(option)
}

onMounted(() => {
  // 模拟获取统计数据
  statistics.value = {
    totalUsers: 650,
    sdkUsers: 350,
    appUsers: 300
  }

  // 初始化图表
  initUserGrowthChart()
  initUserDistributionChart()

  // 监听窗口大小变化，重绘图表
  window.addEventListener('resize', () => {
    const userGrowth = echarts.getInstanceByDom(userGrowthChart.value)
    const userDistribution = echarts.getInstanceByDom(userDistributionChart.value)
    userGrowth?.resize()
    userDistribution?.resize()
  })
})

// 监听类型变化，更新图表
watch([userTrendType, activityType], () => {
  // 这里可以根据类型重新获取数据并更新图表
})
&lt;/script>

&lt;style scoped>
.dashboard-container {
  padding: 20px;
}

.data-card {
  .card-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    h2 {
      margin: 0;
      font-size: 24px;
    }
    
    .el-icon {
      font-size: 48px;
      color: #409EFF;
      opacity: 0.6;
    }
  }
}

.chart-row {
  margin-top: 20px;
}

.table-row {
  margin-top: 20px;
}

.chart-container {
  height: 300px;
  
  .chart {
    height: 100%;
    width: 100%;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
&lt;/style>
