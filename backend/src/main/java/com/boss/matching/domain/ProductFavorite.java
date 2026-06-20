package com.boss.matching.domain;

import java.time.Instant;

/**
 * 达人收藏产品的领域记录。
 *
 * @param id 收藏记录主键
 * @param influencerId 达人资料 ID
 * @param productId 产品 ID
 * @param createdAt 收藏时间，用于按最近收藏排序
 */
public record ProductFavorite(long id, long influencerId, long productId, Instant createdAt) {
}
