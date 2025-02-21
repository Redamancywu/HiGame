<template>
  <div class="users-container">
    <div class="header">
      <div class="left">
        <el-input
          v-model="searchQuery"
          placeholder="搜索用户名/邮箱/手机号"
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增用户
      </el-button>
    </div>

    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="userList"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机号" min-width="120" />
        <el-table-column prop="userType" label="用户类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.userType === 'SDK' ? 'success' : 'primary'" size="small">
              {{ row.userType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'danger'" size="small">
              {{ row.status === 'ACTIVE' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button type="primary" size="small" @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>编辑
              </el-button>
              <el-button
                :type="row.status === 'ACTIVE' ? 'warning' : 'success'"
                size="small"
                @click="handleToggleStatus(row)"
              >
                <el-icon>
                  <component :is="row.status === 'ACTIVE' ? 'Lock' : 'Unlock'" />
                </el-icon>
                {{ row.status === 'ACTIVE' ? '禁用' : '启用' }}
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)">
                <el-icon><Delete /></el-icon>删除
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增用户' : '编辑用户'"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" :disabled="dialogType === 'edit'" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" />
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="formData.userType" placeholder="请选择用户类型">
            <el-option label="SDK用户" value="SDK" />
            <el-option label="APP用户" value="APP" />
          </el-select>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input v-model="formData.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="formData.status"
            :active-value="'ACTIVE'"
            :inactive-value="'INACTIVE'"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import {
  getUserList,
  createUser,
  updateUser,
  deleteUser,
  updateUserStatus
} from '../api/user'

// 列表相关
const loading = ref(false)
const userList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchQuery = ref('')

// 表单相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const formData = reactive({
  username: '',
  email: '',
  phone: '',
  password: '',
  userType: 'SDK',
  status: 'ACTIVE'
})

const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ]
}

// 获取用户列表
const fetchUserList = async () => {
  try {
    loading.value = true;
    const response = await getUserList({
      page: currentPage.value - 1,
      size: pageSize.value,
      query: searchQuery.value
    });
    
    userList.value = response.data.content;
    total.value = response.data.totalElements;
  } catch (error) {
    console.error('获取用户列表失败:', error);
    ElMessage.error('获取用户列表失败');
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  fetchUserList();
};

// 分页变化
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchUserList();
};

// 每页条数变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  fetchUserList();
};

// 新增/编辑
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  formData.username = ''
  formData.email = ''
  formData.phone = ''
  formData.password = ''
  formData.userType = 'SDK'
  formData.status = 'ACTIVE'
}

const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  formData.username = row.username
  formData.email = row.email
  formData.phone = row.phone
  formData.userType = row.userType
  formData.status = row.status
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await createUser(formData)
          ElMessage.success('创建成功')
        } else {
          await updateUser(formData.id, formData)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        fetchUserList()
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error(error.response?.data?.message || '操作失败')
      }
    }
  })
}

// 启用/禁用
const handleToggleStatus = async (row) => {
  try {
    await updateUserStatus(row.id, row.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE')
    ElMessage.success('操作成功')
    fetchUserList()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该用户吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      fetchUserList()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

onMounted(() => {
  fetchUserList()
})
</script>

<style scoped>
.users-container {
  padding: 20px;
  min-height: calc(100vh - 60px);
  background-color: #f5f7fa;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.left {
  display: flex;
  gap: 10px;
}

.search-input {
  width: 300px;
}

.table-card {
  background-color: #fff;
  border-radius: 4px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 按钮组样式优化 */
.el-button-group {
  display: flex;
  gap: 8px;
}

/* 表格内容垂直居中 */
:deep(.el-table .cell) {
  display: flex;
  align-items: center;
}

/* 标签样式优化 */
.el-tag {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 60px;
}

/* 按钮图标和文字间距 */
.el-button .el-icon {
  margin-right: 4px;
}

/* 表格hover效果 */
:deep(.el-table__row:hover) {
  background-color: #f5f7fa !important;
}
</style>
