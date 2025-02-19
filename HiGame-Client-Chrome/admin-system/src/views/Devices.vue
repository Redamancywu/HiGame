&lt;template&gt;
  &lt;div class="devices-container"&gt;
    &lt;div class="header"&gt;
      &lt;el-form :inline="true" :model="searchForm" class="search-form"&gt;
        &lt;el-form-item label="设备ID"&gt;
          &lt;el-input v-model="searchForm.deviceId" placeholder="搜索设备ID" clearable /&gt;
        &lt;/el-form-item&gt;
        &lt;el-form-item label="用户ID"&gt;
          &lt;el-input v-model="searchForm.userId" placeholder="搜索用户ID" clearable /&gt;
        &lt;/el-form-item&gt;
        &lt;el-form-item label="设备类型"&gt;
          &lt;el-select v-model="searchForm.osType" placeholder="选择设备类型" clearable&gt;
            &lt;el-option label="iOS" value="iOS" /&gt;
            &lt;el-option label="Android" value="Android" /&gt;
            &lt;el-option label="Web" value="Web" /&gt;
          &lt;/el-select&gt;
        &lt;/el-form-item&gt;
        &lt;el-form-item label="在线状态"&gt;
          &lt;el-select v-model="searchForm.online" placeholder="选择在线状态" clearable&gt;
            &lt;el-option label="在线" :value="true" /&gt;
            &lt;el-option label="离线" :value="false" /&gt;
          &lt;/el-select&gt;
        &lt;/el-form-item&gt;
        &lt;el-form-item&gt;
          &lt;el-button type="primary" @click="handleSearch"&gt;
            &lt;el-icon&gt;&lt;Search /&gt;&lt;/el-icon&gt;
            搜索
          &lt;/el-button&gt;
          &lt;el-button @click="resetSearch"&gt;重置&lt;/el-button&gt;
        &lt;/el-form-item&gt;
      &lt;/el-form&gt;
    &lt;/div&gt;

    &lt;el-table
      v-loading="loading"
      :data="deviceList"
      border
      style="width: 100%"
    &gt;
      &lt;el-table-column prop="deviceId" label="设备ID" width="180" /&gt;
      &lt;el-table-column prop="userId" label="用户ID" width="100" /&gt;
      &lt;el-table-column prop="deviceName" label="设备名称" /&gt;
      &lt;el-table-column prop="deviceModel" label="设备型号" /&gt;
      &lt;el-table-column prop="osType" label="系统类型" width="100"&gt;
        &lt;template #default="{ row }"&gt;
          &lt;el-tag :type="getOsTypeTagType(row.osType)"&gt;
            {{ row.osType }}
          &lt;/el-tag&gt;
        &lt;/template&gt;
      &lt;/el-table-column&gt;
      &lt;el-table-column prop="osVersion" label="系统版本" width="100" /&gt;
      &lt;el-table-column prop="appVersion" label="APP版本" width="100" /&gt;
      &lt;el-table-column label="在线状态" width="100"&gt;
        &lt;template #default="{ row }"&gt;
          &lt;el-tag :type="row.online ? 'success' : 'info'"&gt;
            {{ row.online ? '在线' : '离线' }}
          &lt;/el-tag&gt;
        &lt;/template&gt;
      &lt;/el-table-column&gt;
      &lt;el-table-column prop="lastLoginIp" label="最后登录IP" width="140" /&gt;
      &lt;el-table-column prop="lastActiveTime" label="最后活跃时间" width="180" /&gt;
      &lt;el-table-column label="操作" width="150" fixed="right"&gt;
        &lt;template #default="{ row }"&gt;
          &lt;el-button-group&gt;
            &lt;el-button
              type="primary"
              link
              @click="handleViewDetails(row)"
            &gt;
              详情
            &lt;/el-button&gt;
            &lt;el-button
              type="danger"
              link
              @click="handleForceOffline(row)"
              :disabled="!row.online"
            &gt;
              强制下线
            &lt;/el-button&gt;
          &lt;/el-button-group&gt;
        &lt;/template&gt;
      &lt;/el-table-column&gt;
    &lt;/el-table&gt;

    &lt;div class="pagination"&gt;
      &lt;el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      /&gt;
    &lt;/div&gt;

    &lt;el-dialog
      v-model="detailsDialogVisible"
      title="设备详情"
      width="600px"
    &gt;
      &lt;el-descriptions :column="2" border&gt;
        &lt;el-descriptions-item label="设备ID"&gt;{{ selectedDevice.deviceId }}&lt;/el-descriptions-item&gt;
        &lt;el-descriptions-item label="用户ID"&gt;{{ selectedDevice.userId }}&lt;/el-descriptions-item&gt;
        &lt;el-descriptions-item label="设备名称"&gt;{{ selectedDevice.deviceName }}&lt;/el-descriptions-item&gt;
        &lt;el-descriptions-item label="设备型号"&gt;{{ selectedDevice.deviceModel }}&lt;/el-descriptions-item&gt;
        &lt;el-descriptions-item label="系统类型"&gt;{{ selectedDevice.osType }}&lt;/el-descriptions-item&gt;
        &lt;el-descriptions-item label="系统版本"&gt;{{ selectedDevice.osVersion }}&lt;/el-descriptions-item&gt;
        &lt;el-descriptions-item label="APP版本"&gt;{{ selectedDevice.appVersion }}&lt;/el-descriptions-item&gt;
        &lt;el-descriptions-item label="在线状态"&gt;
          &lt;el-tag :type="selectedDevice.online ? 'success' : 'info'"&gt;
            {{ selectedDevice.online ? '在线' : '离线' }}
          &lt;/el-tag&gt;
        &lt;/el-descriptions-item&gt;
        &lt;el-descriptions-item label="最后登录IP"&gt;{{ selectedDevice.lastLoginIp }}&lt;/el-descriptions-item&gt;
        &lt;el-descriptions-item label="最后活跃时间"&gt;{{ selectedDevice.lastActiveTime }}&lt;/el-descriptions-item&gt;
        &lt;el-descriptions-item label="推送Token" :span="2"&gt;{{ selectedDevice.pushToken || '无' }}&lt;/el-descriptions-item&gt;
      &lt;/el-descriptions&gt;

      &lt;template #footer&gt;
        &lt;span class="dialog-footer"&gt;
          &lt;el-button @click="detailsDialogVisible = false"&gt;关闭&lt;/el-button&gt;
          &lt;el-button
            type="danger"
            @click="handleForceOffline(selectedDevice)"
            :disabled="!selectedDevice.online"
          &gt;
            强制下线
          &lt;/el-button&gt;
        &lt;/span&gt;
      &lt;/template&gt;
    &lt;/el-dialog&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup&gt;
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
&lt;/script&gt;

&lt;style scoped&gt;
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
&lt;/style&gt;
