const api = require('../../../utils/request')
const auth = require('../../../utils/auth')
const storage = require('../../../utils/storage')

Page({
  data: {
    activeTab: 'library',
    category: '',
    fansMin: '',
    priceRange: '',
    productCount: 0,
    influencerCount: 0,
    profileComplete: true,
    influencers: [],
    showUnlockPrompt: false,
    selectedInfluencer: null
  },

  onShow() {
    if (!auth.requireLogin('MERCHANT')) return
    this.loadData()
  },

  async loadData() {
    const merchantId = getApp().globalData.merchantId
    const session = storage.getSession()
    const products = await api.request(`/merchant/${merchantId}/products`)
    const query = buildInfluencerQuery(this.data)
    const influencers = await api.request(query ? `/influencers?${query}` : '/influencers', { showLoading: false })
    this.setData({
      productCount: products.items.length,
      influencerCount: influencers.items.length,
      profileComplete: isMerchantProfileComplete(session.profile),
      influencers: influencers.items || []
    })
  },

  switchTab(event) {
    this.setData({ activeTab: event.currentTarget.dataset.tab })
  },

  onFilterInput(event) {
    this.setData({ [event.currentTarget.dataset.key]: event.detail.value }, () => this.loadData())
  },

  showPayPrompt(event) {
    const id = Number(event.currentTarget.dataset.id)
    const selectedInfluencer = this.data.influencers.find(item => item.id === id) || null
    this.setData({
      selectedInfluencer,
      showUnlockPrompt: true
    })
  },

  hidePayPrompt() {
    this.setData({
      showUnlockPrompt: false,
      selectedInfluencer: null
    })
  },

  noop() {
  },

  goUnlockPlaceholder() {
    this.hidePayPrompt()
    wx.showToast({ title: '功能暂未开放', icon: 'none' })
  },

  goProfile() {
    wx.navigateTo({ url: '/pages/merchant/profile/profile' })
  },

  goCreate() {
    wx.navigateTo({ url: '/pages/merchant/product-create/product-create' })
  },

  goProducts() {
    wx.navigateTo({ url: '/pages/merchant/product-list/product-list' })
  },

  goInfluencers() {
    wx.navigateTo({ url: '/pages/merchant/influencer-list/influencer-list' })
  }
})

function isMerchantProfileComplete(profile = {}) {
  return Boolean(profile.name && profile.industry && profile.industry !== '待完善' && profile.description && profile.contact)
}

function buildInfluencerQuery(filters) {
  const params = []
  if (filters.category) params.push(`category=${encodeURIComponent(filters.category)}`)
  if (filters.fansMin) params.push(`fansMin=${Number(filters.fansMin)}`)
  if (filters.priceRange) params.push(`priceRange=${encodeURIComponent(filters.priceRange)}`)
  return params.join('&')
}
