const state = {
  merchantId: 1,
  influencerId: 1,
  currentWxUserId: null,
  users: [],
  merchants: [
    {
      id: 1,
      userId: 1,
      name: '星芒美妆',
      industry: '美妆',
      description: '专注新品种草和内容投放',
      contact: 'brand@example.com'
    }
  ],
  products: [
    {
      id: 1,
      merchantId: 1,
      name: '修护面膜推广',
      type: '美妆',
      description: '新品修护面膜，适合小红书和抖音种草。',
      goal: '曝光',
      budgetMin: 800,
      budgetMax: 3000,
      platform: '小红书',
      fansMin: 10000,
      fansMax: 120000,
      cooperationType: '种草',
      status: 'ACTIVE',
      createdAt: '2026-06-16'
    },
    {
      id: 2,
      merchantId: 1,
      name: '咖啡店探店合作',
      type: '本地生活',
      description: '周末探店短视频合作，强调到店转化。',
      goal: '引流',
      budgetMin: 300,
      budgetMax: 1500,
      platform: '抖音',
      fansMin: 5000,
      fansMax: 80000,
      cooperationType: '探店',
      status: 'ACTIVE',
      createdAt: '2026-06-16'
    }
  ],
  influencers: [
    {
      id: 1,
      userId: 2,
      nickname: '小鹿测评',
      platform: '小红书',
      fansRange: '1w-5w',
      fansCount: 35000,
      category: '美妆',
      priceRange: '800-1500',
      contact: '付费解锁后可见',
      rawContact: 'lulu@example.com',
      socialAccount: '付费解锁后可见',
      rawSocialAccount: '@lulu',
      isPublic: true,
      unlocked: false
    },
    {
      id: 2,
      userId: 3,
      nickname: '阿辰探店',
      platform: '抖音',
      fansRange: '5w-10w',
      fansCount: 76000,
      category: '本地生活',
      priceRange: '1000-2500',
      contact: '付费解锁后可见',
      rawContact: 'achen@example.com',
      socialAccount: '付费解锁后可见',
      rawSocialAccount: '@achen',
      isPublic: true,
      unlocked: false
    },
    {
      id: 3,
      userId: 4,
      nickname: '暂不公开达人',
      platform: 'B站',
      fansRange: '1w-5w',
      fansCount: 28000,
      category: '数码',
      priceRange: '600-1200',
      contact: 'hidden@example.com',
      rawContact: 'hidden@example.com',
      socialAccount: '@hidden',
      rawSocialAccount: '@hidden',
      isPublic: false,
      unlocked: false
    }
  ],
  unlocks: {}
}

function mockRequest(path, options) {
  const method = options.method || 'GET'
  const data = options.data || {}
  const route = parseRoute(path)

  return new Promise(resolve => {
    setTimeout(() => {
      if (route.pathname === '/auth/mock-login' && method === 'POST') {
        resolve(mockLogin(data))
        return
      }
      if (route.pathname === '/auth/wx-login' && method === 'POST') {
        resolve(wxLogin(data))
        return
      }
      if (route.pathname === '/auth/bind-role' && method === 'POST') {
        resolve(bindRole(data))
        return
      }
      if (route.pathname === '/auth/register' && method === 'POST') {
        resolve(register(data))
        return
      }
      if (route.pathname === '/auth/login' && method === 'POST') {
        resolve(login(data))
        return
      }
      if (route.pathname === '/products' && method === 'GET') {
        resolve({ items: filterProducts(route.query) })
        return
      }
      if (route.pathname === '/products' && method === 'POST') {
        resolve({ item: createProduct(data) })
        return
      }
      if (route.pathname.startsWith('/products/') && method === 'PUT') {
        const id = Number(route.pathname.split('/')[2])
        resolve({ item: updateProduct(id, data) })
        return
      }
      if (route.pathname.startsWith('/products/') && route.pathname.endsWith('/status') && method === 'PATCH') {
        const id = Number(route.pathname.split('/')[2])
        resolve({ item: updateProductStatus(id, data) })
        return
      }
      if (route.pathname === '/merchant/profile' && method === 'POST') {
        resolve({ item: saveMerchant(data) })
        return
      }
      if (route.pathname === '/merchant/profile' && method === 'GET') {
        const userId = Number(route.query.userId)
        resolve({ item: state.merchants.find(item => item.userId === userId) })
        return
      }
      if (route.pathname.startsWith('/products/') && route.pathname.endsWith('/matches')) {
        const id = Number(route.pathname.split('/')[2])
        resolve({ items: matchInfluencers(id) })
        return
      }
      if (route.pathname.startsWith('/products/')) {
        const id = Number(route.pathname.split('/')[2])
        resolve({ item: state.products.find(item => item.id === id) })
        return
      }
      if (route.pathname.startsWith('/merchant/') && route.pathname.endsWith('/products')) {
        const merchantId = Number(route.pathname.split('/')[2])
        resolve({ items: state.products.filter(item => item.merchantId === merchantId) })
        return
      }
      if (route.pathname === '/influencers') {
        resolve({ items: filterInfluencers(route.query) })
        return
      }
      if (route.pathname.startsWith('/influencers/') && route.pathname.endsWith('/public')) {
        const id = Number(route.pathname.split('/')[2])
        const item = state.influencers.find(target => target.id === id)
        if (item) item.isPublic = data.isPublic
        resolve({ item })
        return
      }
      if (route.pathname.startsWith('/influencers/')) {
        const id = Number(route.pathname.split('/')[2])
        const merchantId = Number(route.query.merchantId)
        const productId = Number(route.query.productId)
        resolve({ item: findInfluencerDetail(id, merchantId, productId) })
        return
      }
      if (route.pathname === '/influencers/profile' && method === 'GET') {
        const userId = Number(route.query.userId)
        resolve({ item: state.influencers.find(item => item.userId === userId) })
        return
      }
      if (route.pathname === '/influencers/profile' && method === 'POST') {
        resolve({ item: saveInfluencer(data) })
        return
      }
      if ((route.pathname === '/payments/unlock' || route.pathname === '/unlock/influencer') && method === 'POST') {
        resolve({ item: unlock(data) })
        return
      }
      resolve({})
    }, 180)
  })
}

function parseRoute(path) {
  const [pathname, queryString = ''] = path.split('?')
  const query = {}

  queryString.split('&').filter(Boolean).forEach(part => {
    const [key, value = ''] = part.split('=')
    query[decodeURIComponent(key)] = decodeURIComponent(value)
  })

  return { pathname, query }
}

function filterProducts(query) {
  return state.products
    .filter(item => item.status === 'ACTIVE')
    .filter(item => query.type ? item.type.includes(query.type) : true)
    .filter(item => query.platform ? item.platform.includes(query.platform) : true)
    .filter(item => query.budgetMin ? item.budgetMax >= Number(query.budgetMin) : true)
    .filter(item => query.fansMin ? item.fansMax >= Number(query.fansMin) : true)
    .filter(item => query.cooperationType ? item.cooperationType.includes(query.cooperationType) : true)
}

function filterInfluencers(query) {
  return state.influencers
    .filter(item => item.isPublic)
    .filter(item => query.category ? item.category.includes(query.category) : true)
    .filter(item => query.platform ? item.platform.includes(query.platform) : true)
    .filter(item => query.fansMin ? item.fansCount >= Number(query.fansMin) : true)
    .filter(item => query.priceRange ? item.priceRange.includes(query.priceRange) : true)
    .map(maskInfluencer)
}

function mockLogin(data) {
  const isMerchant = data.role === 'MERCHANT'
  return {
    token: `mock-token-${Date.now()}`,
    user: { id: Date.now(), role: data.role },
    profile: isMerchant
      ? { id: state.merchantId, name: data.nickname || '星芒美妆' }
      : maskInfluencer(state.influencers[0])
  }
}

function register(data) {
  const existing = state.users.find(item => item.phone === data.phone)
  if (existing) return createSession(existing)

  const user = {
    id: Date.now(),
    phone: data.phone,
    role: data.role
  }
  state.users.push(user)
  return createSession(user)
}

function login(data) {
  const user = state.users.find(item => item.phone === data.phone)
  return createSession(user || {
    id: Date.now(),
    phone: data.phone,
    role: data.role || 'MERCHANT'
  })
}

function wxLogin(data) {
  const openid = `mock-openid-${data.code || 'local'}`
  let user = state.users.find(item => item.openid === openid)
  if (!user) {
    user = {
      id: Date.now(),
      role: 'UNBOUND',
      phone: '',
      openid,
      loginType: 'WECHAT'
    }
    state.users.push(user)
  }
  state.currentWxUserId = user.id
  return createSession(user)
}

function bindRole(data) {
  const user = state.users.find(item => item.id === state.currentWxUserId)
  if (!user) return {}
  if (user.role !== 'UNBOUND' && user.role !== data.role) {
    return createSession(user)
  }
  user.role = data.role
  return createSession(user)
}

function createSession(user) {
  if (user.role === 'UNBOUND') {
    return {
      token: `mock-token-${user.id}`,
      needBindRole: true,
      user,
      profile: null
    }
  }

  const isMerchant = user.role === 'MERCHANT'
  const merchant = isMerchant ? ensureMerchant(user) : null
  return {
    token: `mock-token-${user.id}`,
    needBindRole: false,
    user,
    profile: isMerchant
      ? merchant
      : ownInfluencerProfile(ensureInfluencer(user))
  }
}

function ensureMerchant(user) {
  const current = state.merchants.find(item => item.userId === user.id)
  if (current) return current

  const merchant = {
    id: ++state.merchantId,
    userId: user.id,
    name: '新商家',
    industry: '待完善',
    description: '',
    contact: ''
  }
  state.merchants.push(merchant)
  return merchant
}

function ensureInfluencer(user) {
  const current = state.influencers.find(item => item.userId === user.id)
  if (current) return current

  const influencer = {
    id: ++state.influencerId,
    userId: user.id,
    nickname: '新达人',
    platform: '待完善',
    fansRange: '待完善',
    fansCount: 0,
    category: '待完善',
    priceRange: '待完善',
    contact: '',
    rawContact: '',
    socialAccount: '',
    rawSocialAccount: '',
    isPublic: false,
    unlocked: false,
    portfolio: []
  }
  state.influencers.push(influencer)
  return influencer
}

function createProduct(data) {
  const item = {
    ...data,
    id: Date.now(),
    merchantId: Number(data.merchantId || state.merchantId),
    budgetMin: Number(data.budgetMin || 0),
    budgetMax: Number(data.budgetMax || 0),
    fansMin: Number(data.fansMin || 0),
    fansMax: Number(data.fansMax || 0),
    cooperationType: data.cooperationType || '种草',
    status: 'ACTIVE',
    createdAt: '2026-06-16'
  }
  state.products.unshift(item)
  return item
}

function updateProduct(id, data) {
  const current = state.products.find(item => item.id === id && item.merchantId === Number(data.merchantId))
  if (!current) return null

  Object.assign(current, {
    ...data,
    id,
    merchantId: current.merchantId,
    budgetMin: Number(data.budgetMin || 0),
    budgetMax: Number(data.budgetMax || 0),
    fansMin: Number(data.fansMin || 0),
    fansMax: Number(data.fansMax || 0),
    cooperationType: data.cooperationType || current.cooperationType,
    status: current.status
  })
  return current
}

function updateProductStatus(id, data) {
  const current = state.products.find(item => item.id === id && item.merchantId === Number(data.merchantId))
  if (!current) return null

  current.status = data.status
  return current
}

function saveMerchant(data) {
  const userId = Number(data.userId)
  const current = state.merchants.find(item => item.userId === userId)
  const next = {
    id: current ? current.id : Date.now(),
    userId,
    name: data.name,
    industry: data.industry,
    description: data.description || '',
    contact: data.contact || ''
  }

  if (current) {
    Object.assign(current, next)
    return current
  }

  state.merchants.push(next)
  return next
}

function saveInfluencer(data) {
  const current = state.influencers.find(item => item.id === Number(data.id)) || state.influencers.find(item => item.userId === Number(data.userId))
  const next = current || {}
  Object.assign(next, data, {
    id: current ? current.id : Date.now(),
    userId: Number(data.userId || (current && current.userId) || 0),
    fansCount: Number(data.fansCount || (current && current.fansCount) || 0),
    rawContact: data.contact,
    rawSocialAccount: data.socialAccount,
    portfolio: Array.isArray(data.portfolio) ? data.portfolio : []
  })
  if (!current) state.influencers.push(next)
  return ownInfluencerProfile(next)
}

function unlock(data) {
  const key = `${data.merchantId}:${data.influencerId}:${data.productId}`
  if (state.unlocks[key]) return state.unlocks[key]

  const product = state.products.find(target => target.id === data.productId)
  const item = state.influencers.find(target => target.id === data.influencerId)
  if (!product || product.merchantId !== data.merchantId || !item || !item.isPublic) {
    return { id: 0, amountCent: 0, status: 'FAILED', ...data }
  }

  state.unlocks[key] = true
  if (item) {
    item.unlocked = true
    item.contact = item.rawContact
    item.socialAccount = item.rawSocialAccount
  }
  const record = { id: Date.now(), amountCent: 990, status: 'PAID', ...data }
  state.unlocks[key] = record
  return record
}

function findInfluencerDetail(id, merchantId, productId) {
  const item = state.influencers.find(target => target.id === id)
  if (!item || !item.isPublic) return null

  const key = `${merchantId}:${id}:${productId}`
  return maskInfluencer({
    ...item,
    unlocked: Boolean(state.unlocks[key])
  })
}

function matchInfluencers(productId) {
  const product = state.products.find(item => item.id === productId)
  if (!product) return []
  return state.influencers
    .filter(item => item.isPublic)
    .map(item => ({
      score: score(product, item),
      influencer: maskInfluencer(item)
    }))
    .sort((a, b) => b.score - a.score)
}

function score(product, influencer) {
  let value = 0
  if (product.type === influencer.category) value += 0.4
  if (influencer.fansCount >= product.fansMin && influencer.fansCount <= product.fansMax) value += 0.3
  if (influencer.priceRange) value += 0.2
  if (product.platform === influencer.platform) value += 0.1
  return value
}

function maskInfluencer(item) {
  return {
    ...item,
    contact: item.unlocked ? item.rawContact : '付费解锁后可见',
    socialAccount: item.unlocked ? item.rawSocialAccount : '付费解锁后可见',
    fansCount: item.unlocked ? item.fansCount : null
  }
}

function ownInfluencerProfile(item) {
  return {
    ...item,
    contact: item.rawContact || item.contact || '',
    socialAccount: item.rawSocialAccount || item.socialAccount || '',
    fansCount: Number(item.fansCount || 0),
    portfolio: Array.isArray(item.portfolio) ? item.portfolio : []
  }
}

module.exports = {
  request: mockRequest,
  mockRequest,
  state
}
