package com.boss.matching.controller;

import com.boss.matching.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

/**
 * Exposes product demand APIs while delegating business rules to {@link ProductService}.
 */
@RestController
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;

    /**
     * Creates a product controller.
     *
     * @param productService product business service
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Lists active products with optional filters.
     *
     * @param type optional industry filter
     * @param platform optional platform filter
     * @param budgetMin optional minimum budget filter
     * @param fansMin optional minimum follower requirement filter
     * @param cooperationType optional cooperation type filter
     * @return item list response
     */
    @GetMapping("/products")
    public Map<String, Object> products(
            @RequestParam Optional<String> type,
            @RequestParam Optional<String> platform,
            @RequestParam Optional<Integer> budgetMin,
            @RequestParam Optional<Integer> fansMin,
            @RequestParam Optional<String> cooperationType
    ) {
        return Map.of("items", productService.listProducts(type, platform, budgetMin, fansMin, cooperationType));
    }

    /**
     * Creates a product demand.
     *
     * @param request product payload
     * @return created item response
     */
    @PostMapping("/products")
    public Map<String, Object> createProduct(@Valid @RequestBody ProductService.ProductRequest request) {
        return Map.of("item", productService.createProduct(request));
    }

    /**
     * Updates a product demand.
     *
     * @param id product id
     * @param request product payload
     * @return updated item response or 404 when not found
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable long id, @Valid @RequestBody ProductService.ProductRequest request) {
        return productService.updateProduct(id, request)
                .map(item -> ResponseEntity.ok(Map.of("item", item)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Updates product status.
     *
     * @param id product id
     * @param request status payload
     * @return updated item response or 404 when not found
     */
    @PatchMapping("/products/{id}/status")
    public ResponseEntity<?> updateProductStatus(@PathVariable long id, @Valid @RequestBody ProductService.ProductStatusRequest request) {
        return productService.updateProductStatus(id, request)
                .map(item -> ResponseEntity.ok(Map.of("item", item)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Returns a product detail.
     *
     * @param id product id
     * @return product response or 404
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<?> productDetail(@PathVariable long id) {
        return productService.findProduct(id)
                .map(product -> ResponseEntity.ok(Map.of("item", product)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Returns matched influencers for a product.
     *
     * @param id product id
     * @param limit maximum number of matches
     * @return match list response
     */
    @GetMapping("/products/{id}/matches")
    public Map<String, Object> productMatches(@PathVariable long id, @RequestParam(defaultValue = "5") int limit) {
        return Map.of("items", productService.matchInfluencers(id, limit));
    }

    /**
     * Lists products belonging to a merchant.
     *
     * @param merchantId merchant id
     * @return item list response
     */
    @GetMapping("/merchant/{merchantId}/products")
    public Map<String, Object> merchantProducts(@PathVariable long merchantId) {
        return Map.of("items", productService.listMerchantProducts(merchantId));
    }
}
