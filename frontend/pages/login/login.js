const api = require('../../utils/request')
const auth = require('../../utils/auth')
const ENV = require('../../config/env')
const storage = require('../../utils/storage')

Page({
  data: {
    role: 'MERCHANT',
    submitMode: 'login',
    loading: false,
    wxLoading: false,
    needBindRole: false,
    mockCode: '123456'
  },

  onShow() {
    const session = auth.restoreSession()
    if (!session.token || session.role === 'UNBOUND' || this.data.needBindRole) return

    wx.redirectTo({
      url: session.role === 'MERCHANT' ? '/pages/merchant/home/home' : '/pages/influencer/home/home'
    })
  },

  selectRole(event) {
    this.setData({ role: event.currentTarget.dataset.role })
  },

  setSubmitMode(event) {
    this.setData({ submitMode: event.currentTarget.dataset.mode })
  },

  getCode() {
    wx.showToast({ title: `验证码 ${this.data.mockCode}`, icon: 'none' })
  },

  wxLogin() {
    if (this.data.wxLoading) return

    this.setData({ wxLoading: true })
    wx.login({
      success: async ({ code }) => {
        await this.loginWithCode(code)
      },
      fail: async err => {
        if (ENV.DEV_WX_LOGIN_FALLBACK) {
          await this.loginWithCode('local-dev-login')
          return
        }

        this.setData({ wxLoading: false })
        wx.showToast({ title: err.errMsg || '微信登录失败', icon: 'none' })
      }
    })
  },

  async loginWithCode(code) {
    try {
      const res = await api.request('/auth/wx-login', {
        method: 'POST',
        data: { code },
        showLoading: false
      })
      this.handleWxSession(res)
    } catch (err) {
      wx.showToast({ title: err.message || '微信登录失败', icon: 'none' })
    } finally {
      this.setData({ wxLoading: false })
    }
  },

  handleWxSession(res) {
    if (res.needBindRole) {
      const app = getApp()
      app.globalData.token = res.token
      app.globalData.role = res.user.role
      storage.setSession({ role: res.user.role, token: res.token, user: res.user, profile: null })
      this.setData({ needBindRole: true })
      wx.showToast({ title: '请选择身份', icon: 'none' })
      return
    }

    this.applySession(res)
  },

  async bindRole() {
    if (this.data.loading) return

    this.setData({ loading: true })
    try {
      const res = await api.request('/auth/bind-role', {
        method: 'POST',
        data: { role: this.data.role },
        showLoading: false
      })
      this.applySession(res)
    } catch (err) {
      wx.showToast({ title: err.message || '绑定失败', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  },

  async submit(event) {
    if (this.data.loading) return

    const values = event.detail.value
    const error = validateLogin(values)
    if (error) {
      wx.showToast({ title: error, icon: 'none' })
      return
    }

    const path = this.data.submitMode === 'register' ? '/auth/register' : '/auth/login'
    this.setData({ loading: true })

    try {
      const res = await api.request(path, {
        method: 'POST',
        data: {
          phone: values.phone,
          code: values.code,
          role: this.data.role
        }
      })
      this.applySession(res)
    } catch (err) {
      if (this.data.submitMode === 'login') {
        await this.fallbackRegister(values)
      }
    } finally {
      this.setData({ loading: false })
    }
  },

  async fallbackRegister(values) {
    const res = await api.request('/auth/register', {
      method: 'POST',
      data: {
        phone: values.phone,
        code: values.code,
        role: this.data.role
      }
    })
    this.applySession(res)
  },

  applySession(res) {
    const role = res.user.role
    const app = getApp()
    app.globalData.role = role
    app.globalData.token = res.token
    if (role === 'MERCHANT') app.globalData.merchantId = res.profile.id
    if (role === 'INFLUENCER') app.globalData.influencerId = res.profile.id
    storage.setSession({ role, token: res.token, user: res.user, profile: res.profile })
    if (role === 'MERCHANT' && !isMerchantProfileComplete(res.profile)) {
      wx.redirectTo({ url: '/pages/merchant/profile/profile' })
      return
    }

    if (role === 'INFLUENCER' && !isInfluencerProfileComplete(res.profile)) {
      wx.redirectTo({ url: '/pages/influencer/profile/profile' })
      return
    }

    wx.redirectTo({
      url: role === 'MERCHANT' ? '/pages/merchant/home/home' : '/pages/influencer/home/home'
    })
  }
})

function validateLogin(values) {
  const phone = String(values.phone || '').trim()
  const code = String(values.code || '').trim()

  if (!/^1\d{10}$/.test(phone)) return '请输入 11 位手机号'
  if (!code) return '请输入验证码'

  return ''
}

function isMerchantProfileComplete(profile = {}) {
  return Boolean(profile.name && profile.industry && profile.industry !== '待完善' && profile.description && profile.contact)
}

function isInfluencerProfileComplete(profile = {}) {
  return Boolean(
    profile.nickname &&
    profile.platform &&
    profile.platform !== '待完善' &&
    profile.fansRange &&
    profile.category &&
    profile.category !== '待完善' &&
    profile.priceRange &&
    profile.contact &&
    profile.socialAccount
  )
}
