function setSession(payload) {
  wx.setStorageSync('session', payload)
}

function getSession() {
  return wx.getStorageSync('session') || {}
}

function clearSession() {
  wx.removeStorageSync('session')
}

function getRole() {
  return getSession().role || ''
}

function getProfile() {
  return getSession().profile || {}
}

module.exports = {
  setSession,
  getSession,
  clearSession,
  getRole,
  getProfile
}
