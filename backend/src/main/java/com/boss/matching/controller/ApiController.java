package com.boss.matching.controller;

import com.boss.matching.domain.UserRole;
import com.boss.matching.service.AuthTokenService;
import com.boss.matching.service.MarketplaceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final MarketplaceService service;
    private final AuthTokenService authTokenService;

    public ApiController(MarketplaceService service, AuthTokenService authTokenService) {
        this.service = service;
        this.authTokenService = authTokenService;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return service.health();
    }

    @PostMapping("/auth/mock-login")
    public ResponseEntity<?> mockLogin(@Valid @RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(service.mockLogin(request.role(), request.nickname()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(403).body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@Valid @RequestBody MarketplaceService.AuthRegisterRequest request) {
        try {
            return ResponseEntity.ok(service.register(request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody MarketplaceService.AuthLoginRequest request) {
        try {
            return ResponseEntity.ok(service.login(request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/auth/wx-login")
    public ResponseEntity<?> wxLogin(@Valid @RequestBody MarketplaceService.WxLoginRequest request) {
        try {
            return ResponseEntity.ok(service.wxLogin(request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/auth/bind-role")
    public ResponseEntity<?> bindRole(@RequestHeader(value = "Authorization", required = false) String authorization, @Valid @RequestBody MarketplaceService.BindRoleRequest request) {
        return authTokenService.verify(bearerToken(authorization))
                .map(claims -> {
                    try {
                        return ResponseEntity.ok(service.bindRole(claims.userId(), request));
                    } catch (IllegalArgumentException ex) {
                        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
                    }
                })
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("message", "登录已过期，请重新登录")));
    }

    @GetMapping("/products")
    public Map<String, Object> products(
            @RequestParam Optional<String> type,
            @RequestParam Optional<String> platform,
            @RequestParam Optional<Integer> budgetMin,
            @RequestParam Optional<Integer> fansMin,
            @RequestParam Optional<String> cooperationType
    ) {
        return Map.of("items", service.listProducts(type, platform, budgetMin, fansMin, cooperationType));
    }

    @PostMapping("/products")
    public Map<String, Object> createProduct(@Valid @RequestBody MarketplaceService.ProductRequest request) {
        return Map.of("item", service.createProduct(request));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable long id, @Valid @RequestBody MarketplaceService.ProductRequest request) {
        return service.updateProduct(id, request)
                .map(item -> ResponseEntity.ok(Map.of("item", item)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/products/{id}/status")
    public ResponseEntity<?> updateProductStatus(@PathVariable long id, @Valid @RequestBody MarketplaceService.ProductStatusRequest request) {
        return service.updateProductStatus(id, request)
                .map(item -> ResponseEntity.ok(Map.of("item", item)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/merchant/profile")
    public Map<String, Object> saveMerchantProfile(@Valid @RequestBody MarketplaceService.MerchantRequest request) {
        return Map.of("item", service.saveMerchantProfile(request));
    }

    @GetMapping("/merchant/profile")
    public ResponseEntity<?> merchantProfile(@RequestParam long userId) {
        return service.findMerchantProfile(userId)
                .map(item -> ResponseEntity.ok(Map.of("item", item)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> productDetail(@PathVariable long id) {
        return service.findProduct(id)
                .map(product -> ResponseEntity.ok(Map.of("item", product)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/products/{id}/matches")
    public Map<String, Object> productMatches(@PathVariable long id, @RequestParam(defaultValue = "5") int limit) {
        return Map.of("items", service.matchInfluencers(id, limit));
    }

    @GetMapping("/merchant/{merchantId}/products")
    public Map<String, Object> merchantProducts(@PathVariable long merchantId) {
        return Map.of("items", service.listMerchantProducts(merchantId));
    }

    @PostMapping("/influencers/profile")
    public Map<String, Object> saveInfluencer(@Valid @RequestBody MarketplaceService.InfluencerRequest request) {
        return Map.of("item", service.saveInfluencer(request));
    }

    @GetMapping("/influencers/profile")
    public ResponseEntity<?> influencerProfile(@RequestParam long userId) {
        return service.findInfluencerProfile(userId)
                .map(item -> ResponseEntity.ok(Map.of("item", item)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/influencers/{id}/public")
    public ResponseEntity<?> updateInfluencerPublic(@PathVariable long id, @RequestBody PublicRequest request) {
        return service.updatePublic(id, request.isPublic())
                .map(item -> ResponseEntity.ok(Map.of("item", item)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/influencers")
    public Map<String, Object> influencers(
            @RequestParam Optional<String> category,
            @RequestParam Optional<String> platform,
            @RequestParam Optional<Integer> fansMin,
            @RequestParam Optional<String> priceRange
    ) {
        return Map.of("items", service.listPublicInfluencers(category, platform, fansMin, priceRange));
    }

    @GetMapping("/influencers/{id}")
    public ResponseEntity<?> influencerDetail(@PathVariable long id, @RequestParam long merchantId, @RequestParam long productId) {
        return service.findInfluencer(id, merchantId, productId)
                .map(item -> ResponseEntity.ok(Map.of("item", item)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping({"/payments/unlock", "/unlock/influencer"})
    public ResponseEntity<?> unlock(@Valid @RequestBody MarketplaceService.UnlockRequest request) {
        try {
            return ResponseEntity.ok(Map.of("item", service.unlock(request)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    public record LoginRequest(@NotNull UserRole role, String nickname) {
    }

    public record PublicRequest(boolean isPublic) {
    }

    private String bearerToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) return "";
        return authorization.substring("Bearer ".length());
    }
}
