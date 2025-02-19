&lt;template>
  &lt;div class="users-container">
    &lt;div class="header">
      &lt;el-button type="primary" @click="handleAdd">
        新增用户
      &lt;/el-button>
      
      &lt;div class="search">
        &lt;el-input
          v-model="searchQuery"
          placeholder="搜索用户名/邮箱"
          clearable
          @keyup.enter="handleSearch"
        >
          &lt;template #append>
            &lt;el-button @click="handleSearch">
              &lt;el-icon>&lt;Search />&lt;/el-icon>
            &lt;/el-button>
          &lt;/template>
        &lt;/el-input>
      &lt;/div>
    &lt;/div>

    &lt;el-table
      v-loading="loading"
      :data="userList"
      border
      style="width: 100%"
    >
      &lt;el-table-column prop="id" label="ID" width="80" />
      &lt;el-table-column prop="username" label="用户名" />
      &lt;el-table-column prop="email" label="邮箱" />
      &lt;el-table-column prop="phone" label="手机号" />
      &lt;el-table-column label="角色" width="150">
        &lt;template #default="{ row }">
          &lt;el-tag
            v-for="role in row.roles"
            :key="role"
            class="role-tag"
          >
            {{ role }}
          &lt;/el-tag>
        &lt;/template>
      &lt;/el-table-column>
      &lt;el-table-column label="状态" width="100">
        &lt;template #default="{ row }">
          &lt;el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">
            {{ row.status === 'ACTIVE' ? '启用' : '禁用' }}
          &lt;/el-tag>
        &lt;/template>
      &lt;/el-table-column>
      &lt;el-table-column prop="createTime" label="创建时间" width="180" />
      &lt;el-table-column label="操作" width="200" fixed="right">
        &lt;template #default="{ row }">
          &lt;el-button-group>
            &lt;el-button
              type="primary"
              link
              @click="handleEdit(row)"
            >
              编辑
            &lt;/el-button>
            &lt;el-button
              type="primary"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 'ACTIVE' ? '禁用' : '启用' }}
            &lt;/el-button>
            &lt;el-button
              type="danger"
              link
              @click="handleDelete(row)"
            >
              删除
            &lt;/el-button>
          &lt;/el-button-group>
        &lt;/template>
      &lt;/el-table-column>
    &lt;/el-table>

    &lt;div class="pagination">
      &lt;el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    &lt;/div>

    &lt;el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增用户' : '编辑用户'"
      width="500px"
    >
      &lt;el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        &lt;el-form-item label="用户名" prop="username">
          &lt;el-input v-model="formData.username" :disabled="dialogType === 'edit'" />
        &lt;/el-form-item>
        &lt;el-form-item label="邮箱" prop="email">
          &lt;el-input v-model="formData.email" />
        &lt;/el-form-item>
        &lt;el-form-item label="手机号" prop="phone">
          &lt;el-input v-model="formData.phone" />
        &lt;/el-form-item>
        &lt;el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          &lt;el-input v-model="formData.password" type="password" />
        &lt;/el-form-item>
        &lt;el-form-item label="角色" prop="roles">
          &lt;el-select v-model="formData.roles" multiple style="width: 100%">
            &lt;el-option label="管理员" value="ADMIN" />
            &lt;el-option label="开发者" value="DEVELOPER" />
            &lt;el-option label="普通用户" value="USER" />
          &lt;/el-select>
        &lt;/el-form-item>
        &lt;el-form-item label="状态" prop="status">
          &lt;el-radio-group v-model="formData.status">
            &lt;el-radio label="ACTIVE">启用&lt;/el-radio>
            &lt;el-radio label="INACTIVE">禁用&lt;/el-radio>
          &lt;/el-radio-group>
        &lt;/el-form-item>
      &lt;/el-form>
      &lt;template #footer>
        &lt;span class="dialog-footer">
          &lt;el-button @click="dialogVisible = false">取消&lt;/el-button>
          &lt;el-button type="primary" @click="handleSubmit">
            确定
          &lt;/el-button>
        &lt;/span>
      &lt;/template>
    &lt;/el-dialog>
  &lt;/div>
&lt;/template>

&lt;script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import {
  getUserList,
  createUser,
  updateUser,
  deleteUser
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
  roles: [],
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
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  roles: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      query: searchQuery.value
    }
    const res = await getUserList(params)
    userList.value = res.content
    total.value = res.totalElements
  } catch (error) {
    console.error('Failed to fetch user list:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchUserList()
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchUserList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchUserList()
}

// 新增用户
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  Object.assign(formData, {
    username: '',
    email: '',
    phone: '',
    password: '',
    roles: [],
    status: 'ACTIVE'
  })
}

// 编辑用户
const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(formData, row)
}

// 提交表单
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
        console.error('Failed to submit form:', error)
      }
    }
  })
}

// 切换状态
const handleToggleStatus = async (row) => {
  try {
    const newStatus = row.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
    await updateUser(row.id, { ...row, status: newStatus })
    ElMessage.success('状态更新成功')
    fetchUserList()
  } catch (error) {
    console.error('Failed to toggle status:', error)
  }
}

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除该用户吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      fetchUserList()
    } catch (error) {
      console.error('Failed to delete user:', error)
    }
  })
}

onMounted(() => {
  fetchUserList()
})
&lt;/script>

&lt;style scoped>
.users-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.search {
  width: 300px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.role-tag {
  margin-right: 5px;
}
&lt;/style>
