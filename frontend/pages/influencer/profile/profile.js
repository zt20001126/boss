const api = require('../../../utils/request')
const auth = require('../../../utils/auth')
const storage = require('../../../utils/storage')

const LOCKED_TEXT = '付费解锁后可见'
const EMPTY_TEXT = '待完善'
const NEW_INFLUENCER_TEXT = '新达人'

Page({
  data: {
    saving: false,
    form: {
      id: 0,
      userId: 0,
      nickname: '',
      city: '',
      platform: '',
      fansCount: '',
      category: '',
      priceRange: '',
      contact: '',
      isPublic: false
    },
    // 首填阶段默认保留一条代表作品，用户可按需继续添加。
    portfolioList: [createEmptyPortfolio()]
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
      this.applyProfile(fallback)
      return
    }

    try {
      const res = await api.request(`/influencers/profile?userId=${userId}`, { showLoading: false })
      this.applyProfile(res.item || fallback)
    } catch (err) {
      this.applyProfile(fallback)
    }
  },

  applyProfile(profile) {
    const normalized = normalizeProfile(profile)
    this.setData({
      form: normalized.form,
      portfolioList: normalized.portfolioList
    })
  },

  onPortfolioInput(event) {
    const index = Number(event.currentTarget.dataset.index)
    const field = event.currentTarget.dataset.field
    const nextList = this.data.portfolioList.map((item, itemIndex) => (
      itemIndex === index ? { ...item, [field]: event.detail.value } : item
    ))

    this.setData({ portfolioList: nextList })
  },

  addPortfolio() {
    this.setData({
      portfolioList: this.data.portfolioList.concat(createEmptyPortfolio())
    })
  },

  removePortfolio(event) {
    if (this.data.portfolioList.length <= 1) {
      wx.showToast({ title: '至少保留一条代表作品', icon: 'none' })
      return
    }

    const index = Number(event.currentTarget.dataset.index)
    this.setData({
      portfolioList: this.data.portfolioList.filter((item, itemIndex) => itemIndex !== index)
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

    const portfolioError = validatePortfolio(this.data.portfolioList)
    if (portfolioError) {
      wx.showToast({ title: portfolioError, icon: 'none' })
      return
    }

    const session = storage.getSession()
    this.setData({ saving: true })

    try {
      const fansCount = Number(values.fansCount)
      const res = await api.request('/influencers/profile', {
        method: 'POST',
        data: {
          ...this.data.form,
          ...values,
          id: getApp().globalData.influencerId,
          userId: session.user && session.user.id,
          fansCount,
          fansRange: buildFansRange(fansCount),
          categories: values.category,
          styleTags: '',
          contentForms: '',
          priceImageText: 0,
          priceVideo: 0,
          priceDetail: '',
          contactWechat: values.contact,
          contactPhone: '',
          socialAccount: '',
          // 代表作品内容暂复用后端 contentUrl 字段，后续可独立扩展正文/链接字段。
          portfolio: buildPortfolio(this.data.portfolioList, values.platform),
          isPublic: this.data.form.isPublic
        }
      })
      const profile = res.item
      storage.setSession({ ...session, profile })
      getApp().globalData.influencerId = profile.id
      this.applyProfile(profile)
      wx.showToast({ title: '已保存' })
      setTimeout(() => wx.redirectTo({ url: '/pages/influencer/home/home' }), 450)
    } catch (err) {
      wx.showToast({ title: err.message || '保存失败', icon: 'none' })
    } finally {
      this.setData({ saving: false })
    }
  }
})

function normalizeProfile(profile = {}) {
  const portfolio = Array.isArray(profile.portfolio) ? profile.portfolio : []
  return {
    form: {
      id: profile.id || 0,
      userId: profile.userId || 0,
      nickname: placeholderValue(profile.nickname, NEW_INFLUENCER_TEXT),
      city: profile.city || '',
      platform: placeholderValue(profile.platform, EMPTY_TEXT),
      fansCount: visibleFansCount(profile.fansCount),
      category: placeholderValue(profile.category, EMPTY_TEXT),
      priceRange: placeholderValue(profile.priceRange, EMPTY_TEXT),
      contact: visibleValue(profile.contact),
      isPublic: Boolean(profile.isPublic)
    },
    portfolioList: portfolio.length
      ? portfolio.map(item => ({
        id: item.id || 0,
        title: item.title || '',
        content: item.contentUrl || item.coverUrl || ''
      }))
      : [createEmptyPortfolio()]
  }
}

function createEmptyPortfolio() {
  return {
    id: 0,
    title: '',
    content: ''
  }
}

function placeholderValue(value, placeholder) {
  return value && value !== placeholder ? value : ''
}

function visibleValue(value) {
  return value && value !== LOCKED_TEXT ? value : ''
}

function visibleFansCount(value) {
  return value === null || value === undefined ? '' : value
}

function buildFansRange(fansCount) {
  if (fansCount < 10000) return '1万以下'
  if (fansCount < 50000) return '1万-5万'
  if (fansCount < 100000) return '5万-10万'
  if (fansCount < 500000) return '10万-50万'
  return '50万以上'
}

function buildPortfolio(portfolioList, platform) {
  return portfolioList
    .map((item, index) => {
      const title = String(item.title || '').trim()
      const content = String(item.content || '').trim()
      return {
        id: item.id || null,
        title,
        contentUrl: content,
        coverUrl: '',
        platform: platform || '',
        sortOrder: index + 1
      }
    })
    .filter(item => item.title || item.contentUrl)
}

function validateProfile(values) {
  const requiredFields = [
    ['nickname', '请填写达人昵称'],
    ['city', '请填写所在城市'],
    ['platform', '请填写主要平台'],
    ['fansCount', '请填写粉丝数'],
    ['category', '请填写内容领域'],
    ['priceRange', '请填写报价'],
    ['contact', '请填写联系方式']
  ]

  for (const [field, message] of requiredFields) {
    if (!String(values[field] || '').trim()) return message
  }

  const fansCount = Number(values.fansCount)
  if (!Number.isFinite(fansCount) || fansCount < 0) {
    return '粉丝数不能小于 0'
  }

  return ''
}

function validatePortfolio(portfolioList) {
  const hasCompleteItem = portfolioList.some(item => (
    String(item.title || '').trim() && String(item.content || '').trim()
  ))

  if (!hasCompleteItem) return '请至少填写一条完整的代表作品'
  return ''
}
