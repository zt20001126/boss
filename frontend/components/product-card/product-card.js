Component({
  options: {
    styleIsolation: "apply-shared"
  },

  properties: {
    item: {
      type: Object,
      value: {}
    },
    mode: {
      type: String,
      value: "readonly"
    }
  },

  methods: {
    onDetail() {
      this.triggerEvent("detail", { id: this.data.item.id })
    },

    onEdit() {
      this.triggerEvent("edit", { id: this.data.item.id })
    },

    onOffline() {
      this.triggerEvent("offline", { id: this.data.item.id })
    }
  }
})
