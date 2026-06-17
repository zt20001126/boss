const ENV = require('../config/env')
const mockApi = require('./mockApi')
const storage = require('./storage')

function request(path, options = {}) {
  if (ENV.USE_MOCK) {
    return mockApi.mockRequest(path, options)
  }

  return realRequest(path, options)
}

function realRequest(path, options = {}) {
  const session = storage.getSession()
  const headers = {
    ...(options.header || {})
  }

  if (session.token) {
    headers.Authorization = `Bearer ${session.token}`
  }

  if (options.showLoading !== false) {
    wx.showLoading({ title: options.loadingText || '加载中' })
  }

  return new Promise((resolve, reject) => {
    wx.request({
      url: `${ENV.API_BASE_URL}${path}`,
      method: options.method || 'GET',
      data: options.data || {},
      header: headers,
      success: res => {
        if (res.statusCode >= 200 && res.statusCode < 300) {
          resolve(res.data)
          return
        }

        const message = getErrorMessage(res)
        wx.showToast({ title: message, icon: 'none' })
        reject(new Error(message))
      },
      fail: err => {
        wx.showToast({ title: '网络请求失败', icon: 'none' })
        reject(err)
      },
      complete: () => {
        if (options.showLoading !== false) {
          wx.hideLoading()
        }
      }
    })
  })
}

function getErrorMessage(res) {
  if (res.data && res.data.message) return res.data.message
  if (res.statusCode === 401) return '登录已过期，请重新登录'
  if (res.statusCode === 403) return '暂无操作权限'
  return '服务暂时不可用'
}

module.exports = {
  request
}
