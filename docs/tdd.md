# 🧱 商家-达人撮合小程序 技术设计文档（TDD）

# 1\. 系统架构设计（MVP）

## 1.1 架构模式

采用：

> 单体后端 + 模块化分层架构（MVP阶段）

---

## 1.2 技术栈选型

### 后端

* Java 17
* Spring Boot 3.x
* Spring Web
* Spring Validation
* Spring Security（简化JWT）
* MyBatis-Plus（ORM）
* Lombok

---

### 前端（小程序）

* 微信小程序原生框架
* 或 Taro（可选）

---

### 数据库

* MySQL 8.x

---

## 1.3 系统模块划分

```
├── auth模块（登录注册）├── user模块（用户与角色）├── merchant模块（商家）├── influencer模块（达人）├── product模块（推广产品）├── match模块（筛选/匹配）├── payment模块（解锁记录）
```

---

# 2\. 数据库设计（核心）

---

# 2.1 用户表（user）

```
CREATE TABLE user (    id BIGINT PRIMARY KEY AUTO_INCREMENT,    phone VARCHAR(20) UNIQUE,    role VARCHAR(20), -- MERCHANT / INFLUENCER    password VARCHAR(100),    status TINYINT DEFAULT 1,    created_at DATETIME,    updated_at DATETIME);
```

---

# 2.2 商家信息表（merchant\_profile）

```
CREATE TABLE merchant_profile (    id BIGINT PRIMARY KEY AUTO_INCREMENT,    user_id BIGINT UNIQUE,    merchant_name VARCHAR(100),    industry VARCHAR(50),    description TEXT,    contact VARCHAR(50),    created_at DATETIME,    updated_at DATETIME);
```

---

# 2.3 达人信息表（influencer\_profile）

```
CREATE TABLE influencer_profile (    id BIGINT PRIMARY KEY AUTO_INCREMENT,    user_id BIGINT UNIQUE,    nickname VARCHAR(100),    platform VARCHAR(100), -- 抖音/小红书/B站    follower_range VARCHAR(50),    category VARCHAR(50),    price_range VARCHAR(50),    is_public TINYINT DEFAULT 0, -- 信息公开开关    created_at DATETIME,    updated_at DATETIME);
```

---

# 2.4 推广产品表（product）

```
CREATE TABLE product (    id BIGINT PRIMARY KEY AUTO_INCREMENT,    merchant_id BIGINT,    title VARCHAR(100),    type VARCHAR(50),    description TEXT,    goal VARCHAR(50), -- 曝光/转化/引流    budget_range VARCHAR(50),    platforms VARCHAR(100),    follower_requirement VARCHAR(50),    category_requirement VARCHAR(50),    cooperation_type VARCHAR(50),    status TINYINT DEFAULT 1,    created_at DATETIME,    updated_at DATETIME);
```

---

# 2.5 达人浏览记录/匹配表（可选MVP）

```
CREATE TABLE product_view (    id BIGINT PRIMARY KEY AUTO_INCREMENT,    user_id BIGINT,    product_id BIGINT,    created_at DATETIME);
```

---

# 2.6 付费解锁记录表（核心商业表）

```
CREATE TABLE unlock_record (    id BIGINT PRIMARY KEY AUTO_INCREMENT,    user_id BIGINT, -- 商家    influencer_id BIGINT,    price DECIMAL(10,2),    type VARCHAR(50), -- CONTACT_UNLOCK    created_at DATETIME);
```

---

# 3\. 核心接口设计

---

# 3.1 登录注册模块

### 注册

```
POST /api/auth/register
```

**参数：**

```
{  "phone": "",  "code": "",  "role": "MERCHANT | INFLUENCER"}
```

---

### 登录

```
POST /api/auth/login
```

返回 JWT Token

---

# 3.2 商家模块

### 完善信息

```
POST /api/merchant/profile
```

---

### 发布产品

```
POST /api/product/create
```

---

### 产品列表

```
GET /api/product/list
```

---

### 查看达人列表（筛选）

```
GET /api/influencer/list
```

---

# 3.3 达人模块

### 完善信息

```
POST /api/influencer/profile
```

---

### 设置公开状态

```
POST /api/influencer/public-switch
```

---

### 查看产品列表

```
GET /api/product/list
```

---

# 3.4 解锁模块（核心）

### 解锁达人联系方式

```
POST /api/unlock/influencer
```

逻辑：

* 判断是否已支付
* 写入 unlock\_record
* 返回完整信息

---

# 4\. 核心业务逻辑设计

---

# 4.1 信息分级逻辑（关键）

## 达人信息返回规则

### 未解锁（免费）

```
{  "nickname": "",  "platform": "",  "follower_range": "",  "category": ""}
```

---

### 已解锁（付费）

```
{  "contact": "",  "price_range": "",  "exact_follower": ""}
```

---

# 4.2 达人可见性规则

```
if is_public == 0:    不进入查询结果
```

---

# 4.3 商家筛选逻辑

查询条件：

* category
* follower\_range
* platform
* price\_range
* is\_public = 1

---

# 4.4 匹配逻辑（MVP简化）

```
score =   行业匹配 +   粉丝匹配 +   平台匹配 +   预算匹配
```

不做复杂算法，仅排序即可

---

# 5\. 权限系统设计

---

## 5.1 JWT结构

```
{  "userId": "",  "role": "MERCHANT / INFLUENCER"}
```

---

## 5.2 权限控制

| 接口     | 商家 | 达人 |
| :------- | :--- | :--- |
| 发布产品 | ✔    | ❌    |
| 查看达人 | ✔    | ❌    |
| 查看产品 | ✔    | ✔    |
| 解锁     | ✔    | ❌    |

---

# 6\. 系统关键设计点总结

---

## 6.1 3个核心对象

* user（账号）
* influencer（达人）
* product（商家需求）

---

## 6.2 2个核心商业机制

### ① 信息分级

* 免费信息 → 引流
* 付费信息 → 变现

### ② 达人可见性控制

* is\_public = 0 → 完全隐藏
* is\_public = 1 → 可被匹配

---

## 6.3 MVP核心路径

### 商家路径

注册 → 完善信息 → 发布产品 → 找达人 → 付费解锁

---

### 达人路径

注册 → 完善信息 → 设置公开 → 浏览产品

---

# 7\. MVP非目标（刻意不做）

* 不做推荐算法
* 不做复杂IM聊天
* 不做支付分账
* 不做AI匹配
* 不做缓存架构
* 不做对象存储