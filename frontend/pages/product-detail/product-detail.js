const api = require('../../utils/request')
const auth = require('../../utils/auth')

Page({
  data: {
    id: 0,
    mode: 'influencer',
    product: null,
    matches: [],
    isFavorite: false,
    favoriteSaving: false
  },

  onLoad(query) {
    this.setData({
      id: Number(query.id),
      mode: query.mode || 'influencer'
    })
    this.loadDetail()
  },

  onShow() {
    const expectedRole = this.data.mode === 'merchant' ? 'MERCHANT' : 'INFLUENCER'
    if (!auth.requireLogin(expectedRole)) return
    if (this.data.mode === 'influencer' && this.data.id) this.loadFavoriteStatus()
  },

  async loadDetail() {
    const productRes = await api.request(`/products/${this.data.id}`)
    const next = { product: productRes.item }
    if (this.data.mode === 'merchant') {
      const matchRes = await api.request(`/products/${this.data.id}/matches`)
      next.matches = matchRes.items
    }
    this.setData(next)
  },

  // 收藏状态属于达人账号私有数据，仅在达人模式下加载。
  async loadFavoriteStatus() {
    try {
      const res = await api.request(`/influencer/favorites/${this.data.id}`, { showLoading: false })
      this.setData({ isFavorite: Boolean(res.favorited) })
    } catch (err) {
      this.setData({ isFavorite: false })
    }
  },

  // 先更新按钮反馈；接口失败时恢复原状态，避免页面与服务端不一致。
  async toggleFavorite() {
    if (this.data.favoriteSaving) return

    const previousValue = this.data.isFavorite
    const nextValue = !previousValue
    this.setData({ isFavorite: nextValue, favoriteSaving: true })

    try {
      const res = await api.request(`/influencer/favorites/${this.data.id}`, {
        method: nextValue ? 'POST' : 'DELETE',
        showLoading: false
      })
      this.setData({ isFavorite: Boolean(res.favorited) })
      wx.showToast({ title: nextValue ? '收藏成功' : '已取消收藏' })
    } catch (err) {
      this.setData({ isFavorite: previousValue })
    } finally {
      this.setData({ favoriteSaving: false })
    }
  }
})
