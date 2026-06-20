package com.boss.matching.service;

import com.boss.matching.config.AppProperties;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Provides application health and provider metadata.
 */
@Service
public class HealthService {
    private final AppProperties properties;

    /**
     * Creates a health service.
     *
     * @param properties application provider configuration
     */
    public HealthService(AppProperties properties) {
        this.properties = properties;
    }

    /**
     * Returns application health and active provider settings.
     *
     * @return health metadata map
     */
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "mockEnabled", properties.isMockEnabled(),
                "cacheProvider", properties.getCache().getProvider(),
                "paymentProvider", properties.getPayment().getProvider()
        );
    }
}
