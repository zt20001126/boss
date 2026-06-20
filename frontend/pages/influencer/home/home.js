const api = require('../../../utils/request')
const auth = require('../../../utils/auth')
const storage = require('../../../utils/storage')

const INCOMPLETE_TEXT = '待完善'
const LOCKED_TEXT = '付费解锁后可见'

Page({
  data: {
    activeTab: 'library',
    isPublic: false,
    profileComplete: false,
    products: []
  },

  onShow() {
    if (!auth.requireLogin('INFLUENCER')) return
    this.loadProfile()
    this.loadProducts()
  },

  switchTab(event) {
    this.setData({ activeTab: event.currentTarget.dataset.tab })
  },

  async loadProfile() {
    const session = storage.getSession()
    const userId = session.user && session.user.id
    const fallback = session.profile || {}

    try {
      const res = userId ? await api.request(`/influencers/profile?userId=${userId}`, { showLoading: false }) : { item: fallback }
      const profile = res.item || fallback
      this.setData({
        isPublic: Boolean(profile.isPublic),
        profileComplete: isInfluencerProfileComplete(profile)
      })
    } catch (err) {
      this.setData({
        isPublic: Boolean(fallback.isPublic),
        profileComplete: isInfluencerProfileComplete(fallback)
      })
    }
  },

  async loadProducts() {
    const res = await api.request('/products')
    this.setData({ products: res.items })
  },

  goProfile() {
    wx.navigateTo({ url: '/pages/influencer/profile/profile' })
  },

  // 从“我的”进入独立收藏页，收藏列表由目标页面按账号加载。
  goFavorites() {
    wx.navigateTo({ url: '/pages/influencer/favorites/favorites' })
  },

  goDetail(event) {
    const id = event.detail ? event.detail.id : event.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/product-detail/product-detail?id=${id}&mode=influencer` })
  }
})

function isInfluencerProfileComplete(profile = {}) {
  // 达人工作台只按首填阶段的核心字段判断资料是否完成。
  return Boolean(
    profile.nickname &&
    profile.city &&
    profile.platform &&
    profile.platform !== INCOMPLETE_TEXT &&
    Number(profile.fansCount) > 0 &&
    profile.category &&
    profile.category !== INCOMPLETE_TEXT &&
    profile.priceRange &&
    profile.priceRange !== INCOMPLETE_TEXT &&
    profile.contact &&
    profile.contact !== LOCKED_TEXT
  )
}
