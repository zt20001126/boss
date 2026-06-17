const auth = require('../../utils/auth')

Page({
  onShow() {
    const session = auth.restoreSession()
    if (!session.token) return

    wx.redirectTo({
      url: session.role === 'MERCHANT' ? '/pages/merchant/home/home' : '/pages/influencer/home/home'
    })
  },

  goLogin() {
    wx.navigateTo({ url: '/pages/login/login' })
  }
})
