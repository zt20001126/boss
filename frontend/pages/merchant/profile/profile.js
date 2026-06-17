const api = require('../../../utils/request')
const auth = require('../../../utils/auth')
const storage = require('../../../utils/storage')

Page({
  data: {
    saving: false,
    form: {
      name: '',
      industry: '',
      description: '',
      contact: ''
    }
  },

  onShow() {
    if (!auth.requireLogin('MERCHANT')) return
    this.loadProfile()
  },

  async loadProfile() {
    const session = storage.getSession()
    const userId = session.user && session.user.id
    const fallback = session.profile || {}

    if (!userId) {
      this.setData({ form: normalizeProfile(fallback) })
      return
    }

    try {
      const res = await api.request(`/merchant/profile?userId=${userId}`, { showLoading: false })
      this.setData({ form: normalizeProfile(res.item || fallback) })
    } catch (err) {
      this.setData({ form: normalizeProfile(fallback) })
    }
  },

  async submit(event) {
    if (this.data.saving) return

    const values = event.detail.value
    const error = validateProfile(values)
    if (error) {
      wx.showToast({ title: error, icon: 'none' })
      return
    }

    const session = storage.getSession()
    const userId = session.user && session.user.id
    this.setData({ saving: true })

    try {
      const res = await api.request('/merchant/profile', {
        method: 'POST',
        data: {
          userId,
          name: values.name.trim(),
          industry: values.industry.trim(),
          description: values.description.trim(),
          contact: values.contact.trim()
        }
      })
      const profile = res.item
      storage.setSession({ ...session, profile })
      getApp().globalData.merchantId = profile.id
      wx.showToast({ title: '已保存' })
      setTimeout(() => wx.redirectTo({ url: '/pages/merchant/home/home' }), 500)
    } finally {
      this.setData({ saving: false })
    }
  }
})

function normalizeProfile(profile = {}) {
  return {
    name: profile.name === '新商家' ? '' : profile.name || '',
    industry: profile.industry === '待完善' ? '' : profile.industry || '',
    description: profile.description || '',
    contact: profile.contact || ''
  }
}

function validateProfile(values) {
  if (!String(values.name || '').trim()) return '请填写商家名称'
  if (!String(values.industry || '').trim()) return '请填写行业领域'
  if (!String(values.description || '').trim()) return '请填写品牌简介'
  if (!String(values.contact || '').trim()) return '请填写联系方式'
  return ''
}
