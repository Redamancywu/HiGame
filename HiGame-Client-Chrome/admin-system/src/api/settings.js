import request from './request'

// 获取系统设置
export function getSettings() {
  return request({
    url: '/api/v1/admin/settings',
    method: 'get'
  })
}

// 更新系统设置
export function updateSettings(data) {
  return request({
    url: '/api/v1/admin/settings',
    method: 'put',
    data
  })
} 