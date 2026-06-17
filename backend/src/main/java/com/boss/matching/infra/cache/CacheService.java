package com.boss.matching.infra.cache;

import java.util.Optional;

public interface CacheService {
    <T> Optional<T> get(String key, Class<T> type);

    void put(String key, Object value);

    void evict(String key);
}
