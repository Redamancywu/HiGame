<template>
  <div class="settings-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统设置</span>
        </div>
      </template>
      
      <el-form 
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-form-item label="站点名称" prop="siteName">
          <el-input v-model="formData.siteName" />
        </el-form-item>
        
        <el-form-item label="站点描述" prop="siteDescription">
          <el-input type="textarea" v-model="formData.siteDescription" />
        </el-form-item>
        
        <el-form-item label="管理员邮箱" prop="adminEmail">
          <el-input v-model="formData.adminEmail" />
        </el-form-item>
        
        <el-form-item label="开放注册" prop="enableRegistration">
          <el-switch v-model="formData.enableRegistration" />
        </el-form-item>
        
        <el-form-item label="邮箱验证" prop="enableEmailVerification">
          <el-switch v-model="formData.enableEmailVerification" />
        </el-form-item>
        
        <el-form-item label="会话超时(分钟)" prop="sessionTimeout">
          <el-input-number v-model="formData.sessionTimeout" :min="1" :max="1440" />
        </el-form-item>
        
        <el-form-item label="主题" prop="theme">
          <el-select v-model="formData.theme">
            <el-option label="默认主题" value="default" />
            <el-option label="暗色主题" value="dark" />
            <el-option label="浅色主题" value="light" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSettings, updateSettings } from '../api/settings'

const formRef = ref(null)
const formData = ref({
  siteName: '',
  siteDescription: '',
  adminEmail: '',
  enableRegistration: true,
  enableEmailVerification: false,
  sessionTimeout: 30,
  theme: 'default'
})

const formRules = {
  siteName: [
    { required: true, message: '请输入站点名称', trigger: 'blur' }
  ],
  adminEmail: [
    { required: true, message: '请输入管理员邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  sessionTimeout: [
    { required: true, message: '请输入会话超时时间', trigger: 'blur' }
  ]
}

// 获取设置
const fetchSettings = async () => {
  try {
    const response = await getSettings()
    formData.value = response.data
  } catch (error) {
    console.error('获取设置失败:', error)
    ElMessage.error('获取设置失败')
  }
}

// 提交设置
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await updateSettings(formData.value)
        ElMessage.success('设置更新成功')
      } catch (error) {
        console.error('更新设置失败:', error)
        ElMessage.error('更新设置失败')
      }
    }
  })
}

onMounted(() => {
  fetchSettings()
})
</script>

<style scoped>
.settings-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
