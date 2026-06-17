const storage = require('./storage')

function restoreSession() {
  const session = storage.getSession()
  const app = getApp()

  app.globalData.role = session.role || ''
  app.globalData.token = session.token || ''

  if (session.profile && session.role === 'MERCHANT') {
    app.globalData.merchantId = session.profile.id
  }

  if (session.profile && session.role === 'INFLUENCER') {
    app.globalData.influencerId = session.profile.id
  }

  return session
}

function requireLogin(expectedRole) {
  const session = restoreSession()

  if (!session.token) {
    wx.redirectTo({ url: '/pages/login/login' })
    return false
  }

  if (expectedRole && session.role !== expectedRole) {
    wx.redirectTo({
      url: session.role === 'MERCHANT' ? '/pages/merchant/home/home' : '/pages/influencer/home/home'
    })
    return false
  }

  return true
}

module.exports = {
  restoreSession,
  requireLogin
}
