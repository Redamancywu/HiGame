import request from './request'

export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

export function getUserList(params) {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}

export function updateUser(data) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}
