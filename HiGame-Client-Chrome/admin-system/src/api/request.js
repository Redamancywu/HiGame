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
    const { code, message, data } = response.data
    
    if (code === 200) {
      return data
    } else if (code === 401) {
      // Token过期处理
      if (!isRefreshing) {
        isRefreshing = true
        // 尝试刷新token
        return refreshToken().then(newToken => {
          if (newToken) {
            localStorage.setItem('token', newToken)
            // 重试队列中的请求
            requests.forEach(cb => cb(newToken))
            requests = []
            // 重试当前请求
            response.config.headers.Authorization = `Bearer ${newToken}`
            return request(response.config)
          } else {
            // 刷新token失败，退出登录
            handleLogout()
          }
        }).catch(() => {
          handleLogout()
        }).finally(() => {
          isRefreshing = false
        })
      } else {
        // 将请求加入队列
        return new Promise(resolve => {
          requests.push(token => {
            response.config.headers.Authorization = `Bearer ${token}`
            resolve(request(response.config))
          })
        })
      }
    } else {
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message || '请求失败'))
    }
  },
  error => {
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 400:
          ElMessage.error(data.message || '请求参数错误')
          break
        case 401:
          handleLogout()
          break
        case 403:
          ElMessage.error('没有权限访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error('网络错误')
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请重试')
    } else {
      ElMessage.error('网络连接失败，请检查网络')
    }
    
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
