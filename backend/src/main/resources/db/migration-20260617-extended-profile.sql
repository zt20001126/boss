ALTER TABLE influencer ADD COLUMN avatar_url VARCHAR(512) NULL AFTER nickname;
ALTER TABLE influencer ADD COLUMN city VARCHAR(64) NULL AFTER avatar_url;
ALTER TABLE influencer ADD COLUMN categories VARCHAR(512) NULL AFTER category;
ALTER TABLE influencer ADD COLUMN style_tags VARCHAR(512) NULL AFTER categories;
ALTER TABLE influencer ADD COLUMN content_forms VARCHAR(512) NULL AFTER style_tags;
ALTER TABLE influencer ADD COLUMN price_image_text INT NOT NULL DEFAULT 0 AFTER price_range;
ALTER TABLE influencer ADD COLUMN price_video INT NOT NULL DEFAULT 0 AFTER price_image_text;
ALTER TABLE influencer ADD COLUMN price_detail VARCHAR(1024) NULL AFTER price_video;
ALTER TABLE influencer ADD COLUMN contact_wechat VARCHAR(128) NULL AFTER price_detail;
ALTER TABLE influencer ADD COLUMN contact_phone VARCHAR(128) NULL AFTER contact_wechat;

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

ALTER TABLE product ADD COLUMN target_categories VARCHAR(512) NULL AFTER type;
ALTER TABLE product ADD COLUMN max_quote_per_influencer INT NOT NULL DEFAULT 0 AFTER budget_max;
ALTER TABLE product ADD COLUMN content_forms VARCHAR(512) NULL AFTER platform;
