package com.boss.matching.controller;

import com.boss.matching.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Exposes service health endpoints.
 */
@RestController
@RequestMapping("/api")
public class HealthController {
    private final HealthService healthService;

    /**
     * Creates a health controller.
     *
     * @param healthService health metadata service
     */
    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    /**
     * Returns service health and provider metadata.
     *
     * @return health response map
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return healthService.health();
    }
}
