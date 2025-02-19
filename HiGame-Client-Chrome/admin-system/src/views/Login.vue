<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="login-header">
          <h2>HiGame 后台管理系统</h2>
        </div>
      </template>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-width="0"
        size="large"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="用户名"
            :prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            :prefix-icon="Lock"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="login-button"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>

        <div class="login-tips">
          默认账号：admin<br>
          默认密码：sx991026@.
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: 'admin',
  password: 'sx991026@.'
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = () => {
  if (!loginFormRef.value) return
  
  loginFormRef.value.validate(async valid => {
    if (valid) {
      loading.value = true
      try {
        const requestData = {
          username: loginForm.username,
          password: loginForm.password
        }
        console.log('Request URL:', '/api/v1/admin/login')
        console.log('Request data:', requestData)
        
        // 调用管理员登录 API
        const response = await fetch('/api/v1/admin/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          body: JSON.stringify(requestData)
        })

        console.log('Response status:', response.status)
        console.log('Response headers:', Object.fromEntries(response.headers))
        
        const responseText = await response.text()
        console.log('Raw response text:', responseText)
        
        if (!responseText) {
          throw new Error('服务器没有返回数据')
        }
        
        let data
        try {
          data = JSON.parse(responseText)
          console.log('Parsed response data:', data)
        } catch (e) {
          console.error('Failed to parse response:', e)
          console.error('Response text was:', responseText)
          throw new Error(`服务器返回数据格式错误: ${responseText}`)
        }

        if (!response.ok) {
          throw new Error(data?.error || '用户名或密码错误')
        }

        // 保存认证信息
        localStorage.setItem('token', data.token)
        localStorage.setItem('username', data.username)
        localStorage.setItem('role', data.role)
        
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } catch (error) {
        console.error('Login failed:', error)
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #2d3a4b;
  overflow: hidden;
}

.login-card {
  width: 100%;
  max-width: 420px;
  margin: 20px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  padding: 20px 0;
}

.login-card h2 {
  margin: 0;
  font-size: 28px;
  color: #303133;
  font-weight: 500;
}

.login-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
}

.el-form-item {
  margin-bottom: 24px;
}

.login-tips {
  text-align: center;
  font-size: 14px;
  color: #909399;
  line-height: 1.6;
  margin-top: 20px;
}

:deep(.el-input__wrapper) {
  height: 44px;
}

:deep(.el-card__body) {
  padding: 30px;
}

:deep(.el-card__header) {
  padding: 0;
  border-bottom: none;
}
</style>
