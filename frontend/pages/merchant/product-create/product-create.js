const api = require('../../../utils/request')
const auth = require('../../../utils/auth')

Page({
  data: {
    productId: 0,
    pageTitle: '发布产品',
    submitting: false,
    form: {
      name: '新品推广',
      type: '美妆',
      targetCategories: '美妆',
      goal: '曝光',
      platform: '小红书',
      contentForms: '图文,短视频',
      budgetMin: 800,
      budgetMax: 3000,
      maxQuotePerInfluencer: 1500,
      fansMin: 10000,
      fansMax: 120000,
      cooperationType: '种草',
      description: '希望达人发布真实体验内容，支持图文或短视频。'
    }
  },

  onLoad(query) {
    const productId = Number(query.id || 0)
    if (!productId) return

    this.setData({
      productId,
      pageTitle: '编辑产品'
    })
    this.loadProduct(productId)
  },

  onShow() {
    auth.requireLogin('MERCHANT')
  },

  async loadProduct(productId) {
    const res = await api.request(`/products/${productId}`, { showLoading: false })
    if (res.item) {
      this.setData({ form: res.item })
    }
  },

  async submit(event) {
    if (this.data.submitting) return

    const values = event.detail.value
    const error = validateProduct(values)
    if (error) {
      wx.showToast({ title: error, icon: 'none' })
      return
    }

    const merchantId = getApp().globalData.merchantId
    const productId = this.data.productId
    this.setData({ submitting: true })

    try {
      await api.request(productId ? `/products/${productId}` : '/products', {
        method: productId ? 'PUT' : 'POST',
        data: {
          ...values,
          merchantId,
          budgetMin: Number(values.budgetMin),
          budgetMax: Number(values.budgetMax),
          maxQuotePerInfluencer: Number(values.maxQuotePerInfluencer || 0),
          fansMin: Number(values.fansMin),
          fansMax: Number(values.fansMax),
          cooperationType: values.cooperationType
        }
      })
      wx.showToast({ title: productId ? '已保存' : '已发布' })
      setTimeout(() => wx.navigateBack(), 500)
    } finally {
      this.setData({ submitting: false })
    }
  }
})

function validateProduct(values) {
  const requiredFields = [
    ['name', '请填写产品名称'],
    ['type', '请填写行业类型'],
    ['targetCategories', '请填写适合达人领域'],
    ['goal', '请填写推广目标'],
    ['platform', '请填写投放平台'],
    ['contentForms', '请填写内容形式要求'],
    ['cooperationType', '请填写合作形式'],
    ['description', '请填写产品介绍']
  ]

  for (const [field, message] of requiredFields) {
    if (!String(values[field] || '').trim()) return message
  }

  const budgetMin = Number(values.budgetMin)
  const budgetMax = Number(values.budgetMax)
  const fansMin = Number(values.fansMin)
  const fansMax = Number(values.fansMax)
  const maxQuotePerInfluencer = Number(values.maxQuotePerInfluencer || 0)

  // 预算和粉丝范围是后续匹配排序的基础，必须在提交前保证上下限有效。
  if (!Number.isFinite(budgetMin) || budgetMin <= 0) return '最低预算需大于 0'
  if (!Number.isFinite(budgetMax) || budgetMax < budgetMin) return '最高预算不能低于最低预算'
  if (!Number.isFinite(maxQuotePerInfluencer) || maxQuotePerInfluencer < 0) return '单达人报价上限不能小于 0'
  if (!Number.isFinite(fansMin) || fansMin < 0) return '最低粉丝不能小于 0'
  if (!Number.isFinite(fansMax) || fansMax < fansMin) return '最高粉丝不能低于最低粉丝'

  return ''
}
