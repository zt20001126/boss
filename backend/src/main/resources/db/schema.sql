CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY,
    role VARCHAR(32) NOT NULL,
    phone VARCHAR(32),
    password VARCHAR(128),
    openid VARCHAR(128),
    unionid VARCHAR(128),
    login_type VARCHAR(32) NOT NULL DEFAULT 'PHONE',
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_openid (openid),
    INDEX idx_user_phone (phone),
    INDEX idx_user_unionid (unionid)
);

CREATE TABLE IF NOT EXISTS merchant (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(128) NOT NULL,
    industry VARCHAR(64) NOT NULL,
    description VARCHAR(512),
    contact VARCHAR(128),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_merchant_user (user_id)
);

CREATE TABLE IF NOT EXISTS influencer (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    nickname VARCHAR(128) NOT NULL,
    avatar_url VARCHAR(512),
    city VARCHAR(64),
    platform VARCHAR(64) NOT NULL,
    fans_range VARCHAR(64) NOT NULL,
    fans_count INT NOT NULL DEFAULT 0,
    category VARCHAR(64) NOT NULL,
    categories VARCHAR(512),
    style_tags VARCHAR(512),
    content_forms VARCHAR(512),
    price_range VARCHAR(64) NOT NULL,
    price_image_text INT NOT NULL DEFAULT 0,
    price_video INT NOT NULL DEFAULT 0,
    price_detail VARCHAR(1024),
    contact_wechat VARCHAR(128),
    contact_phone VARCHAR(128),
    contact VARCHAR(128),
    social_account VARCHAR(128),
    is_public TINYINT(1) NOT NULL DEFAULT 0,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_influencer_public (is_public),
    INDEX idx_influencer_fans (fans_count),
    INDEX idx_influencer_category (category),
    INDEX idx_influencer_platform (platform),
    INDEX idx_influencer_city (city)
);

CREATE TABLE IF NOT EXISTS influencer_portfolio (
    id BIGINT PRIMARY KEY,
    influencer_id BIGINT NOT NULL,
    title VARCHAR(128) NOT NULL,
    cover_url VARCHAR(512),
    content_url VARCHAR(512),
    platform VARCHAR(64),
    sort_order INT NOT NULL DEFAULT 0,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_portfolio_influencer (influencer_id)
);

CREATE TABLE IF NOT EXISTS product (
    id BIGINT PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(128) NOT NULL,
    type VARCHAR(64) NOT NULL,
    target_categories VARCHAR(512),
    description VARCHAR(1024) NOT NULL,
    goal VARCHAR(64) NOT NULL,
    budget_min INT NOT NULL,
    budget_max INT NOT NULL,
    max_quote_per_influencer INT NOT NULL DEFAULT 0,
    platform VARCHAR(64) NOT NULL,
    content_forms VARCHAR(512),
    fans_min INT NOT NULL,
    fans_max INT NOT NULL,
    cooperation_type VARCHAR(64) NOT NULL DEFAULT '种草',
    status VARCHAR(32) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_product_budget (budget_min, budget_max),
    INDEX idx_product_type (type),
    INDEX idx_product_platform (platform)
);

CREATE TABLE IF NOT EXISTS unlock_record (
    id BIGINT PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    influencer_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    amount_cent INT NOT NULL,
    status VARCHAR(32) NOT NULL,
    unlock_type VARCHAR(32) NOT NULL DEFAULT 'CONTACT',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_unlock_once (merchant_id, influencer_id, product_id)
);
