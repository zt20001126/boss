package com.boss.matching.controller;

import com.boss.matching.service.MerchantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Exposes merchant profile APIs while delegating business logic to {@link MerchantService}.
 */
@RestController
@RequestMapping("/api/merchant")
public class MerchantController {
    private final MerchantService merchantService;

    /**
     * Creates a merchant controller.
     *
     * @param merchantService merchant profile service
     */
    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    /**
     * Saves a merchant profile.
     *
     * @param request merchant profile payload
     * @return saved item response
     */
    @PostMapping("/profile")
    public Map<String, Object> saveMerchantProfile(@Valid @RequestBody MerchantService.MerchantRequest request) {
        return Map.of("item", merchantService.saveMerchantProfile(request));
    }

    /**
     * Finds a merchant profile by user id.
     *
     * @param userId user id
     * @return profile response or 404
     */
    @GetMapping("/profile")
    public ResponseEntity<?> merchantProfile(@RequestParam long userId) {
        return merchantService.findMerchantProfile(userId)
                .map(item -> ResponseEntity.ok(Map.of("item", item)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
