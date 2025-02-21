import request from './request'

// 获取统计数据
export function getStats() {
  return request({
    url: '/api/v1/admin/dashboard/stats',
    method: 'get'
  })
}

// 获取趋势数据
export function getUserTrends(params) {
  return request({
    url: '/api/v1/admin/dashboard/user-trends',
    method: 'get',
    params
  })
} 