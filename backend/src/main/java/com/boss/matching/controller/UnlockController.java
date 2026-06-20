package com.boss.matching.controller;

import com.boss.matching.service.UnlockService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Exposes unlock payment APIs while delegating validation and payment flow to {@link UnlockService}.
 */
@RestController
@RequestMapping("/api")
public class UnlockController {
    private final UnlockService unlockService;

    /**
     * Creates an unlock controller.
     *
     * @param unlockService unlock business service
     */
    public UnlockController(UnlockService unlockService) {
        this.unlockService = unlockService;
    }

    /**
     * Unlocks an influencer contact for a merchant product.
     *
     * @param request unlock payload
     * @return unlock record response
     */
    @PostMapping({"/payments/unlock", "/unlock/influencer"})
    public ResponseEntity<?> unlock(@Valid @RequestBody UnlockService.UnlockRequest request) {
        try {
            return ResponseEntity.ok(Map.of("item", unlockService.unlock(request)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }
}
