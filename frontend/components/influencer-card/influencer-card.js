Component({
  options: {
    styleIsolation: "apply-shared"
  },

  properties: {
    item: {
      type: Object,
      value: {}
    }
  },

  methods: {
    onUnlock() {
      this.triggerEvent("unlock", { id: this.data.item.id })
    }
  }
})
