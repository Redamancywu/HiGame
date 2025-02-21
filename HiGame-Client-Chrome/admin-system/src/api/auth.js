import request from './request'

export function login(data) {
  return request({
    url: '/api/v1/admin/login',
    method: 'post',
    data
  })
} 