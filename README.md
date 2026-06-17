# Boss Monorepo

商家-达人撮合平台 monorepo，已按前后端边界拆分：

```text
boss/
├── frontend/  微信小程序前端
├── backend/   Spring Boot 后端
└── docs/      产品、技术与 TODO 文档
```

## Frontend

使用微信开发者工具打开 `frontend/` 目录。

前端接口地址统一在 `frontend/config/env.js` 中配置：

```js
API_BASE_URL: 'http://localhost:8080/api'
```

## Backend

进入 `backend/` 后运行：

```bash
mvn spring-boot:run
```

默认端口为 `8080`，接口前缀保持 `/api` 不变。
