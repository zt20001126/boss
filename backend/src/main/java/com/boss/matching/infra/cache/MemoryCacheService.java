package com.boss.matching.infra.cache;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory cache implementation used for local development and tests.
 */
@Component
public class MemoryCacheService implements CacheService {
    private final ConcurrentMap<String, Object> values = new ConcurrentHashMap<>();

    /** {@inheritDoc} */
    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        Object value = values.get(key);
        if (type.isInstance(value)) {
            return Optional.of(type.cast(value));
        }
        return Optional.empty();
    }

    /** {@inheritDoc} */
    @Override
    public void put(String key, Object value) {
        values.put(key, value);
    }

    /** {@inheritDoc} */
    @Override
    public void evict(String key) {
        values.remove(key);
    }
}
