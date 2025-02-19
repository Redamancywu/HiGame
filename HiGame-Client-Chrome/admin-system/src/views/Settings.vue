&lt;template&gt;
  &lt;div class="settings-container"&gt;
    &lt;el-card&gt;
      &lt;template #header&gt;
        &lt;div class="card-header"&gt;
          &lt;span&gt;个人设置&lt;/span&gt;
        &lt;/div&gt;
      &lt;/template&gt;
      
      &lt;el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      &gt;
        &lt;el-form-item label="用户名" prop="username"&gt;
          &lt;el-input v-model="formData.username" disabled /&gt;
        &lt;/el-form-item&gt;
        
        &lt;el-form-item label="邮箱" prop="email"&gt;
          &lt;el-input v-model="formData.email" /&gt;
        &lt;/el-form-item&gt;
        
        &lt;el-form-item label="手机号" prop="phone"&gt;
          &lt;el-input v-model="formData.phone" /&gt;
        &lt;/el-form-item&gt;
        
        &lt;el-form-item label="旧密码" prop="oldPassword"&gt;
          &lt;el-input v-model="formData.oldPassword" type="password" show-password /&gt;
        &lt;/el-form-item&gt;
        
        &lt;el-form-item label="新密码" prop="newPassword"&gt;
          &lt;el-input v-model="formData.newPassword" type="password" show-password /&gt;
        &lt;/el-form-item&gt;
        
        &lt;el-form-item label="确认密码" prop="confirmPassword"&gt;
          &lt;el-input v-model="formData.confirmPassword" type="password" show-password /&gt;
        &lt;/el-form-item&gt;
        
        &lt;el-form-item&gt;
          &lt;el-button type="primary" @click="handleSubmit"&gt;保存修改&lt;/el-button&gt;
        &lt;/el-form-item&gt;
      &lt;/el-form&gt;
    &lt;/el-card&gt;

    &lt;el-card class="mt-4"&gt;
      &lt;template #header&gt;
        &lt;div class="card-header"&gt;
          &lt;span&gt;系统设置&lt;/span&gt;
        &lt;/div&gt;
      &lt;/template&gt;
      
      &lt;el-form
        :model="systemSettings"
        label-width="100px"
      &gt;
        &lt;el-form-item label="语言"&gt;
          &lt;el-select v-model="systemSettings.language" style="width: 200px"&gt;
            &lt;el-option label="简体中文" value="zh-CN" /&gt;
            &lt;el-option label="English" value="en-US" /&gt;
          &lt;/el-select&gt;
        &lt;/el-form-item&gt;
        
        &lt;el-form-item label="主题"&gt;
          &lt;el-select v-model="systemSettings.theme" style="width: 200px"&gt;
            &lt;el-option label="浅色" value="light" /&gt;
            &lt;el-option label="深色" value="dark" /&gt;
          &lt;/el-select&gt;
        &lt;/el-form-item&gt;
        
        &lt;el-form-item label="通知"&gt;
          &lt;el-switch v-model="systemSettings.notifications" /&gt;
        &lt;/el-form-item&gt;
        
        &lt;el-form-item&gt;
          &lt;el-button type="primary" @click="handleSaveSystemSettings"&gt;保存设置&lt;/el-button&gt;
        &lt;/el-form-item&gt;
      &lt;/el-form&gt;
    &lt;/el-card&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup&gt;
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { updateUserProfile, updatePassword } from '../api/user'

const formRef = ref(null)
const formData = reactive({
  username: 'admin',
  email: 'admin@higame.com',
  phone: '',
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const formRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== formData.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const systemSettings = reactive({
  language: 'zh-CN',
  theme: 'light',
  notifications: true
})

// 保存个人信息
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 更新个人信息
        if (formData.email || formData.phone) {
          await updateUserProfile({
            email: formData.email,
            phone: formData.phone
          })
        }
        
        // 修改密码
        if (formData.oldPassword && formData.newPassword) {
          await updatePassword({
            oldPassword: formData.oldPassword,
            newPassword: formData.newPassword
          })
          
          // 清空密码字段
          formData.oldPassword = ''
          formData.newPassword = ''
          formData.confirmPassword = ''
        }
        
        ElMessage.success('保存成功')
      } catch (error) {
        console.error('Failed to save settings:', error)
      }
    }
  })
}

// 保存系统设置
const handleSaveSystemSettings = () => {
  localStorage.setItem('systemSettings', JSON.stringify(systemSettings))
  ElMessage.success('系统设置保存成功')
}

// 初始化系统设置
const initSystemSettings = () => {
  const savedSettings = localStorage.getItem('systemSettings')
  if (savedSettings) {
    Object.assign(systemSettings, JSON.parse(savedSettings))
  }
}

// 初始化
initSystemSettings()
&lt;/script&gt;

&lt;style scoped&gt;
.settings-container {
  padding: 20px;
}

.mt-4 {
  margin-top: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
&lt;/style&gt;
