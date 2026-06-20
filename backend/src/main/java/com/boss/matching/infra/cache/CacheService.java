package com.boss.matching.infra.cache;

import java.util.Optional;

/**
 * Defines the cache abstraction used by infrastructure adapters and services.
 */
public interface CacheService {
    <T> Optional<T> get(String key, Class<T> type);

    void put(String key, Object value);

    void evict(String key);
}
