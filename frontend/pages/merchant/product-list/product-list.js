const api = require('../../../utils/request')
const auth = require('../../../utils/auth')

Page({
  data: {
    products: []
  },

  onShow() {
    if (!auth.requireLogin('MERCHANT')) return
    this.loadProducts()
  },

  async loadProducts() {
    const merchantId = getApp().globalData.merchantId
    const res = await api.request(`/merchant/${merchantId}/products`)
    this.setData({ products: res.items })
  },

  goCreate() {
    wx.navigateTo({ url: '/pages/merchant/product-create/product-create' })
  },

  goEdit(event) {
    const id = event.detail ? event.detail.id : event.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/merchant/product-create/product-create?id=${id}` })
  },

  goDetail(event) {
    const id = event.detail ? event.detail.id : event.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/product-detail/product-detail?id=${id}&mode=merchant` })
  },

  offline(event) {
    const productId = Number(event.detail ? event.detail.id : event.currentTarget.dataset.id)
    wx.showModal({
      title: '下架产品',
      content: '下架后达人将无法在产品列表中看到该推广需求。',
      confirmText: '下架',
      success: async res => {
        if (!res.confirm) return

        const merchantId = getApp().globalData.merchantId
        await api.request(`/products/${productId}/status`, {
          method: 'PATCH',
          data: { merchantId, status: 'OFFLINE' }
        })
        wx.showToast({ title: '已下架' })
        this.loadProducts()
      }
    })
  }
})
