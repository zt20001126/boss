const api = require('../../../utils/request')
const auth = require('../../../utils/auth')

Page({
  data: {
    favorites: [],
    loaded: false
  },

  onShow() {
    if (!auth.requireLogin('INFLUENCER')) return
    this.loadFavorites()
  },

  // 每次返回页面都重新加载，确保详情页取消收藏后列表立即同步。
  async loadFavorites() {
    try {
      const res = await api.request('/influencer/favorites')
      this.setData({ favorites: res.items || [], loaded: true })
    } catch (err) {
      this.setData({ favorites: [], loaded: true })
    }
  },

  goDetail(event) {
    const id = event.detail ? event.detail.id : event.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/product-detail/product-detail?id=${id}&mode=influencer` })
  }
})
