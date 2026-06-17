const api = require('../../utils/request')
const auth = require('../../utils/auth')

Page({
  data: {
    id: 0,
    mode: 'influencer',
    product: null,
    matches: []
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
    auth.requireLogin(expectedRole)
  },

  async loadDetail() {
    const productRes = await api.request(`/products/${this.data.id}`)
    const next = { product: productRes.item }
    if (this.data.mode === 'merchant') {
      const matchRes = await api.request(`/products/${this.data.id}/matches`)
      next.matches = matchRes.items
    }
    this.setData(next)
  }
})
