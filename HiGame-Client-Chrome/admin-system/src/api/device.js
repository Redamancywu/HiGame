import request from './request'

// 获取设备列表
export const getDeviceList = (params) => {
  return request({
    url: '/api/v1/admin/devices',
    method: 'GET',
    params
  })
}

// 强制设备下线
export const forceOffline = (deviceId) => {
  return request({
    url: `/api/v1/admin/devices/${deviceId}/force-offline`,
    method: 'POST'
  })
}