const api = require('../../../utils/request')
const auth = require('../../../utils/auth')
const storage = require('../../../utils/storage')

Page({
  data: {
    activeTab: 'library',
    type: '',
    platform: '',
    budgetMin: '',
    fansMin: '',
    cooperationType: '',
    isPublic: false,
    profileComplete: false,
    products: []
  },

  onShow() {
    if (!auth.requireLogin('INFLUENCER')) return
    this.loadProfile()
    this.loadProducts()
  },

  onFilterChange(event) {
    this.setData({ [event.detail.key]: event.detail.value })
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
    const query = buildProductQuery(this.data)
    const path = query ? `/products?${query}` : '/products'
    const res = await api.request(path)
    this.setData({ products: res.items })
  },

  goProfile() {
    wx.navigateTo({ url: '/pages/influencer/profile/profile' })
  },

  goDetail(event) {
    const id = event.detail ? event.detail.id : event.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/product-detail/product-detail?id=${id}&mode=influencer` })
  }
})

function buildProductQuery(filters) {
  const params = []
  if (filters.type) params.push(`type=${encodeURIComponent(filters.type)}`)
  if (filters.platform) params.push(`platform=${encodeURIComponent(filters.platform)}`)
  if (filters.budgetMin) params.push(`budgetMin=${Number(filters.budgetMin)}`)
  if (filters.fansMin) params.push(`fansMin=${Number(filters.fansMin)}`)
  if (filters.cooperationType) params.push(`cooperationType=${encodeURIComponent(filters.cooperationType)}`)
  return params.join('&')
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
