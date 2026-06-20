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
    },
    // 收藏页开启状态展示，使已下架产品仍可识别和查看。
    showStatus: {
      type: Boolean,
      value: false
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
