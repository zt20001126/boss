package com.boss.matching.controller;

import com.boss.matching.service.InfluencerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

/**
 * Exposes influencer APIs while delegating profile and listing logic to {@link InfluencerService}.
 */
@RestController
@RequestMapping("/api/influencers")
public class InfluencerController {
    private final InfluencerService influencerService;

    /**
     * Creates an influencer controller.
     *
     * @param influencerService influencer business service
     */
    public InfluencerController(InfluencerService influencerService) {
        this.influencerService = influencerService;
    }

    /**
     * Saves an influencer profile.
     *
     * @param request influencer profile payload
     * @return saved profile response
     */
    @PostMapping("/profile")
    public Map<String, Object> saveInfluencer(@Valid @RequestBody InfluencerService.InfluencerRequest request) {
        return Map.of("item", influencerService.saveInfluencer(request));
    }

    /**
     * Finds an influencer profile by user id.
     *
     * @param userId user id
     * @return profile response or 404
     */
    @GetMapping("/profile")
    public ResponseEntity<?> influencerProfile(@RequestParam long userId) {
        return influencerService.findInfluencerProfile(userId)
                .map(item -> ResponseEntity.ok(Map.of("item", item)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Updates influencer public visibility.
     *
     * @param id influencer id
     * @param request public flag payload
     * @return updated influencer response or 404
     */
    @PatchMapping("/{id}/public")
    public ResponseEntity<?> updateInfluencerPublic(@PathVariable long id, @RequestBody PublicRequest request) {
        return influencerService.updatePublic(id, request.isPublic())
                .map(item -> ResponseEntity.ok(Map.of("item", item)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Lists public influencers with optional filters.
     *
     * @param category optional category filter
     * @param platform optional platform filter
     * @param fansMin optional minimum follower filter
     * @param priceRange optional price range filter
     * @return item list response
     */
    @GetMapping
    public Map<String, Object> influencers(
            @RequestParam Optional<String> category,
            @RequestParam Optional<String> platform,
            @RequestParam Optional<Integer> fansMin,
            @RequestParam Optional<String> priceRange
    ) {
        return Map.of("items", influencerService.listPublicInfluencers(category, platform, fansMin, priceRange));
    }

    /**
     * Returns influencer detail with paid fields masked unless unlocked.
     *
     * @param id influencer id
     * @param merchantId merchant id
     * @param productId product id
     * @return influencer response or 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> influencerDetail(@PathVariable long id, @RequestParam long merchantId, @RequestParam long productId) {
        return influencerService.findInfluencer(id, merchantId, productId)
                .map(item -> ResponseEntity.ok(Map.of("item", item)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Request body for public visibility updates.
     *
     * @param isPublic whether the influencer is publicly listed
     */
    public record PublicRequest(boolean isPublic) {
    }
}
