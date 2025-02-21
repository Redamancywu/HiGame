import request from './request'

// 用户登录
export function login(data) {
  return request({
    url: '/api/v1/admin/login',
    method: 'post',
    data
  })
}

// 获取用户列表
export function getUserList(params) {
  return request({
    url: '/v1/admin/users',
    method: 'get',
    params
  })
}

// 创建用户
export function createUser(data) {
  return request({
    url: '/api/v1/admin/users',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(id, data) {
  return request({
    url: `/api/v1/admin/users/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/api/v1/admin/users/${id}`,
    method: 'delete'
  })
}

// 获取用户详情
export function getUserInfo(id) {
  return request({
    url: `/api/v1/admin/users/${id}`,
    method: 'get'
  })
}

// 修改用户状态
export function updateUserStatus(id, status) {
  return request({
    url: `/api/v1/admin/users/${id}/status`,
    method: 'put',
    data: { status }
  })
}
