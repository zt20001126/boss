Component({
  properties: {
    mode: {
      type: String,
      value: 'influencer'
    },
    actionText: {
      type: String,
      value: '筛选'
    },
    category: String,
    type: String,
    platform: String,
    fansMin: String,
    budgetMin: String,
    priceRange: String,
    cooperationType: String
  },

  methods: {
    onInput(event) {
      this.triggerEvent('change', {
        key: event.currentTarget.dataset.key,
        value: event.detail.value
      })
    },

    onSubmit() {
      this.triggerEvent('submit')
    }
  }
})
