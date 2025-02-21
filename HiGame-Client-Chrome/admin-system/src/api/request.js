import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

let isRefreshing = false
let requests = []

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // 添加时间戳防止缓存
    if (config.method === 'get') {
      config.params = {
        ...config.params,
        _t: Date.now()
      }
    }
    
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    ElMessage.error(error.response?.data?.message || '请求失败')
    return Promise.reject(error)
  }
)

// 刷新token
const refreshToken = () => {
  return request.post('/auth/refresh', {
    refreshToken: localStorage.getItem('refreshToken')
  })
}

// 处理登出
const handleLogout = () => {
  ElMessageBox.confirm(
    '登录已过期，请重新登录',
    '提示',
    {
      confirmButtonText: '确定',
      type: 'warning',
      showCancelButton: false,
      showClose: false,
      closeOnClickModal: false,
      closeOnPressEscape: false
    }
  ).then(() => {
    localStorage.clear()
    router.push('/login')
  })
}

export default request
