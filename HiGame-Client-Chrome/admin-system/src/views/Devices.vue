<template>
  <div class="devices-container">
    <div class="header">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="设备ID">
          <el-input v-model="searchForm.deviceId" placeholder="搜索设备ID" clearable />
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input v-model="searchForm.userId" placeholder="搜索用户ID" clearable />
        </el-form-item>
        <el-form-item label="设备类型">
          <el-select v-model="searchForm.osType" placeholder="选择设备类型" clearable>
            <el-option label="iOS" value="iOS" />
            <el-option label="Android" value="Android" />
            <el-option label="Web" value="Web" />
          </el-select>
        </el-form-item>
        <el-form-item label="在线状态">
          <el-select v-model="searchForm.online" placeholder="选择在线状态" clearable>
            <el-option label="在线" :value="true" />
            <el-option label="离线" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table
      v-loading="loading"
      :data="deviceList"
      border
      style="width: 100%"
    >
      <el-table-column prop="deviceId" label="设备ID" width="180" />
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="deviceName" label="设备名称" />
      <el-table-column prop="deviceModel" label="设备型号" />
      <el-table-column prop="osType" label="系统类型" width="100">
        <template #default="{ row }">
          <el-tag :type="getOsTypeTagType(row.osType)">
            {{ row.osType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="osVersion" label="系统版本" width="100" />
      <el-table-column prop="appVersion" label="APP版本" width="100" />
      <el-table-column label="在线状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.online ? 'success' : 'info'">
            {{ row.online ? '在线' : '离线' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="lastLoginIp" label="最后登录IP" width="140" />
      <el-table-column prop="lastActiveTime" label="最后活跃时间" width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button-group>
            <el-button
              type="primary"
              link
              @click="handleViewDetails(row)"
            >
              详情
            </el-button>
            <el-button
              type="danger"
              link
              @click="handleForceOffline(row)"
              :disabled="!row.online"
            >
              强制下线
            </el-button>
          </el-button-group>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog
      v-model="detailsDialogVisible"
      title="设备详情"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="设备ID">{{ selectedDevice.deviceId }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ selectedDevice.userId }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ selectedDevice.deviceName }}</el-descriptions-item>
        <el-descriptions-item label="设备型号">{{ selectedDevice.deviceModel }}</el-descriptions-item>
        <el-descriptions-item label="系统类型">{{ selectedDevice.osType }}</el-descriptions-item>
        <el-descriptions-item label="系统版本">{{ selectedDevice.osVersion }}</el-descriptions-item>
        <el-descriptions-item label="APP版本">{{ selectedDevice.appVersion }}</el-descriptions-item>
        <el-descriptions-item label="在线状态">
          <el-tag :type="selectedDevice.online ? 'success' : 'info'">
            {{ selectedDevice.online ? '在线' : '离线' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="最后登录IP">{{ selectedDevice.lastLoginIp }}</el-descriptions-item>
        <el-descriptions-item label="最后活跃时间">{{ selectedDevice.lastActiveTime }}</el-descriptions-item>
        <el-descriptions-item label="推送Token" :span="2">{{ selectedDevice.pushToken || '无' }}</el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailsDialogVisible = false">关闭</el-button>
          <el-button
            type="danger"
            @click="handleForceOffline(selectedDevice)"
            :disabled="!selectedDevice.online"
          >
            强制下线
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getDeviceList, forceOffline } from '../api/device'

// 列表相关
const loading = ref(false)
const deviceList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索表单
const searchForm = reactive({
  deviceId: '',
  userId: '',
  osType: '',
  online: ''
})

// 详情弹窗
const detailsDialogVisible = ref(false)
const selectedDevice = ref({})

// 获取设备列表
const fetchDeviceList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      ...searchForm
    }
    const res = await getDeviceList(params)
    deviceList.value = res.content
    total.value = res.totalElements
  } catch (error) {
    console.error('Failed to fetch device list:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchDeviceList()
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    deviceId: '',
    userId: '',
    osType: '',
    online: ''
  })
  handleSearch()
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchDeviceList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchDeviceList()
}

// 查看详情
const handleViewDetails = (row) => {
  selectedDevice.value = { ...row }
  detailsDialogVisible.value = true
}

// 强制下线
const handleForceOffline = (row) => {
  ElMessageBox.confirm(
    '确定要强制该设备下线吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await forceOffline(row.deviceId)
      ElMessage.success('强制下线成功')
      fetchDeviceList()
      if (detailsDialogVisible.value) {
        detailsDialogVisible.value = false
      }
    } catch (error) {
      console.error('Failed to force offline:', error)
    }
  })
}

// 获取系统类型标签样式
const getOsTypeTagType = (osType) => {
  const typeMap = {
    'iOS': 'success',
    'Android': 'warning',
    'Web': 'info'
  }
  return typeMap[osType] || 'info'
}

// 初始化
fetchDeviceList()
</script>

<style scoped>
.devices-container {
  padding: 20px;
}

.header {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
