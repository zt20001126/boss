const api = require('../../../utils/request')
const auth = require('../../../utils/auth')
const storage = require('../../../utils/storage')

const LOCKED_TEXT = '付费解锁后可见'

Page({
  data: {
    saving: false,
    form: {
      id: 0,
      userId: 0,
      nickname: '',
      avatarUrl: '',
      city: '',
      platform: '',
      fansRange: '',
      fansCount: '',
      category: '',
      categories: '',
      styleTags: '',
      contentForms: '',
      priceRange: '',
      priceImageText: '',
      priceVideo: '',
      priceDetail: '',
      contactWechat: '',
      contactPhone: '',
      contact: '',
      socialAccount: '',
      portfolioTitle1: '',
      portfolioUrl1: '',
      portfolioTitle2: '',
      portfolioUrl2: '',
      portfolioTitle3: '',
      portfolioUrl3: '',
      isPublic: false
    }
  },

  onShow() {
    if (!auth.requireLogin('INFLUENCER')) return
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
      const res = await api.request(`/influencers/profile?userId=${userId}`, { showLoading: false })
      this.setData({ form: normalizeProfile(res.item || fallback) })
    } catch (err) {
      this.setData({ form: normalizeProfile(fallback) })
    }
  },

  togglePublic(event) {
    const influencerId = getApp().globalData.influencerId
    this.setData({ 'form.isPublic': event.detail.value })
    api.request(`/influencers/${influencerId}/public`, {
      method: 'PATCH',
      data: { isPublic: event.detail.value }
    })
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
    this.setData({ saving: true })

    try {
      const res = await api.request('/influencers/profile', {
        method: 'POST',
        data: {
          ...this.data.form,
          ...values,
          id: getApp().globalData.influencerId,
          userId: session.user && session.user.id,
          fansCount: Number(values.fansCount),
          priceImageText: Number(values.priceImageText || 0),
          priceVideo: Number(values.priceVideo || 0),
          portfolio: buildPortfolio(values),
          isPublic: this.data.form.isPublic
        }
      })
      const profile = res.item
      storage.setSession({ ...session, profile })
      getApp().globalData.influencerId = profile.id
      this.setData({ form: normalizeProfile(profile) })
      wx.showToast({ title: '已保存' })
    } finally {
      this.setData({ saving: false })
    }
  }
})

function normalizeProfile(profile = {}) {
  const portfolio = Array.isArray(profile.portfolio) ? profile.portfolio : []
  return {
    id: profile.id || 0,
    userId: profile.userId || 0,
    nickname: profile.nickname === '新达人' ? '' : profile.nickname || '',
    platform: profile.platform === '待完善' ? '' : profile.platform || '',
    avatarUrl: profile.avatarUrl || '',
    city: profile.city || '',
    fansRange: profile.fansRange === '待完善' ? '' : profile.fansRange || '',
    fansCount: profile.fansCount || '',
    category: profile.category === '待完善' ? '' : profile.category || '',
    categories: profile.categories || profile.category || '',
    styleTags: profile.styleTags || '',
    contentForms: profile.contentForms || '',
    priceRange: profile.priceRange === '待完善' ? '' : profile.priceRange || '',
    priceImageText: profile.priceImageText || '',
    priceVideo: profile.priceVideo || '',
    priceDetail: visibleValue(profile.priceDetail),
    contactWechat: visibleValue(profile.contactWechat),
    contactPhone: visibleValue(profile.contactPhone),
    contact: visibleValue(profile.contact),
    socialAccount: visibleValue(profile.socialAccount),
    portfolioTitle1: portfolio[0] && portfolio[0].title || '',
    portfolioUrl1: portfolio[0] && (portfolio[0].contentUrl || portfolio[0].coverUrl) || '',
    portfolioTitle2: portfolio[1] && portfolio[1].title || '',
    portfolioUrl2: portfolio[1] && (portfolio[1].contentUrl || portfolio[1].coverUrl) || '',
    portfolioTitle3: portfolio[2] && portfolio[2].title || '',
    portfolioUrl3: portfolio[2] && (portfolio[2].contentUrl || portfolio[2].coverUrl) || '',
    isPublic: Boolean(profile.isPublic)
  }
}

function visibleValue(value) {
  return value && value !== LOCKED_TEXT ? value : ''
}

function buildPortfolio(values) {
  return [1, 2, 3]
    .map((index) => {
      const title = String(values[`portfolioTitle${index}`] || '').trim()
      const contentUrl = String(values[`portfolioUrl${index}`] || '').trim()
      return {
        title,
        contentUrl,
        coverUrl: contentUrl,
        platform: values.platform || '',
        sortOrder: index
      }
    })
    .filter((item) => item.title || item.contentUrl)
}

function validateProfile(values) {
  const requiredFields = [
    ['nickname', '请填写达人昵称'],
    ['platform', '请填写平台'],
    ['city', '请填写所在城市'],
    ['fansRange', '请填写粉丝区间'],
    ['category', '请填写擅长领域'],
    ['contentForms', '请填写内容形式'],
    ['priceRange', '请填写报价区间'],
    ['contact', '请填写联系方式'],
    ['socialAccount', '请填写社交账号']
  ]

  for (const [field, message] of requiredFields) {
    if (!String(values[field] || '').trim()) return message
  }

  const fansCount = Number(values.fansCount)
  if (!Number.isFinite(fansCount) || fansCount < 0) {
    return '精确粉丝数不能小于 0'
  }

  return ''
}
