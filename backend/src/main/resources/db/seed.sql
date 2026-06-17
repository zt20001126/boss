INSERT INTO user (id, role, openid) VALUES
    (1, 'MERCHANT', 'seed-merchant'),
    (2, 'INFLUENCER', 'seed-influencer-1'),
    (3, 'INFLUENCER', 'seed-influencer-2'),
    (4, 'INFLUENCER', 'seed-influencer-hidden'),
    (5, 'INFLUENCER', 'seed-influencer-3')
ON DUPLICATE KEY UPDATE role = VALUES(role);

INSERT INTO merchant (id, user_id, name, industry, description, contact) VALUES
    (1, 1, '星芒美妆', '美妆', '专注新品种草和内容投放', 'brand@example.com')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO influencer (id, user_id, nickname, platform, fans_range, fans_count, category, price_range, contact, social_account, is_public) VALUES
    (1, 2, '小鹿测评', '小红书', '1w-5w', 35000, '美妆', '800-1500', 'lulu@example.com', '@lulu', 1),
    (2, 3, '阿辰探店', '抖音', '5w-10w', 76000, '本地生活', '1000-2500', 'achen@example.com', '@achen', 1),
    (3, 4, '暂不公开达人', 'B站', '1w-5w', 28000, '数码', '600-1200', 'hidden@example.com', '@hidden', 0),
    (4, 5, '南风开箱', 'B站', '5w-10w', 92000, '数码', '1500-3500', 'nanfeng@example.com', '@nanfeng', 1)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);

INSERT INTO product (id, merchant_id, name, type, description, goal, budget_min, budget_max, platform, fans_min, fans_max, cooperation_type, status) VALUES
    (1, 1, '修护面膜推广', '美妆', '新品修护面膜，适合小红书和抖音种草。', '曝光', 800, 3000, '小红书', 10000, 120000, '种草', 'ACTIVE'),
    (2, 1, '咖啡店探店合作', '本地生活', '周末探店短视频合作，强调到店转化。', '引流', 300, 1500, '抖音', 5000, 80000, '探店', 'ACTIVE'),
    (3, 1, '数码新品开箱', '数码', '新品耳机开箱测评合作，适合 B站 或抖音视频。', '转化', 1000, 5000, 'B站', 10000, 150000, '测评', 'ACTIVE')
ON DUPLICATE KEY UPDATE name = VALUES(name);
