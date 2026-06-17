const api = require('../../../utils/request')
const auth = require('../../../utils/auth')

Page({
  data: {
    category: '',
    platform: '',
    fansMin: '',
    priceRange: '',
    influencers: []
  },

  onShow() {
    if (!auth.requireLogin('MERCHANT')) return
    this.loadInfluencers()
  },

  onFilterChange(event) {
    this.setData({ [event.detail.key]: event.detail.value })
  },

  async loadInfluencers() {
    const query = buildQuery(this.data)
    const path = query ? `/influencers?${query}` : '/influencers'
    const res = await api.request(path)
    this.setData({ influencers: res.items })
  },

  async unlock(event) {
    const influencerId = Number(event.detail ? event.detail.id : event.currentTarget.dataset.id)
    const merchantId = getApp().globalData.merchantId
    const productId = getApp().globalData.productId

    wx.showModal({
      title: '解锁联系方式',
      content: '确认后将解锁该达人的联系方式和社交账号。',
      confirmText: '解锁',
      success: async res => {
        if (!res.confirm) return

        await api.request('/unlock/influencer', {
          method: 'POST',
          data: { merchantId, influencerId, productId }
        })
        const detail = await api.request(`/influencers/${influencerId}?merchantId=${merchantId}&productId=${productId}`)
        this.patchInfluencer(detail.item)
        wx.showToast({ title: '解锁成功' })
      }
    })
  },

  patchInfluencer(next) {
    if (!next) return
    this.setData({
      influencers: this.data.influencers.map(item => item.id === next.id ? next : item)
    })
  }
})

function buildQuery(filters) {
  const params = []
  if (filters.category) params.push(`category=${encodeURIComponent(filters.category)}`)
  if (filters.platform) params.push(`platform=${encodeURIComponent(filters.platform)}`)
  if (filters.fansMin) params.push(`fansMin=${Number(filters.fansMin)}`)
  if (filters.priceRange) params.push(`priceRange=${encodeURIComponent(filters.priceRange)}`)
  return params.join('&')
}
