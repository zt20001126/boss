package com.boss.matching.service;

import com.boss.matching.domain.Influencer;
import com.boss.matching.domain.Product;
import com.boss.matching.persistence.MarketplaceStore;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Handles product demand creation, updates, listing, and influencer matching.
 */
@Service
public class ProductService {
    private static final String DEFAULT_STATUS_ACTIVE = "ACTIVE";
    private static final String DEFAULT_COOPERATION_TYPE = "种草";

    private final MarketplaceStore store;
    private final InfluencerProfileViewService influencerProfileViewService;

    /**
     * Creates a product service.
     *
     * @param store marketplace persistence abstraction
     * @param influencerProfileViewService mapper for masked influencer response views
     */
    public ProductService(MarketplaceStore store, InfluencerProfileViewService influencerProfileViewService) {
        this.store = store;
        this.influencerProfileViewService = influencerProfileViewService;
    }

    /**
     * Creates a merchant product demand.
     *
     * @param request product creation request
     * @return created product
     */
    public Product createProduct(ProductRequest request) {
        Product product = new Product(
                store.nextId(),
                request.merchantId(),
                request.name(),
                request.type(),
                defaultText(request.targetCategories(), request.type()),
                request.description(),
                request.goal(),
                request.budgetMin(),
                request.budgetMax(),
                request.maxQuotePerInfluencer(),
                request.platform(),
                defaultText(request.contentForms(), request.cooperationType()),
                request.fansMin(),
                request.fansMax(),
                defaultText(request.cooperationType(), DEFAULT_COOPERATION_TYPE),
                DEFAULT_STATUS_ACTIVE,
                Instant.now()
        );
        store.saveProduct(product);
        return product;
    }

    /**
     * Updates an existing merchant product when the merchant id matches.
     *
     * @param id product id
     * @param request replacement product data
     * @return updated product or empty when missing/not owned by the merchant
     */
    public Optional<Product> updateProduct(long id, ProductRequest request) {
        Optional<Product> currentValue = store.findProduct(id);
        if (currentValue.isEmpty() || currentValue.get().merchantId() != request.merchantId()) {
            return Optional.empty();
        }
        Product current = currentValue.get();

        Product updated = new Product(
                current.id(),
                current.merchantId(),
                request.name(),
                request.type(),
                defaultText(request.targetCategories(), request.type()),
                request.description(),
                request.goal(),
                request.budgetMin(),
                request.budgetMax(),
                request.maxQuotePerInfluencer(),
                request.platform(),
                defaultText(request.contentForms(), request.cooperationType()),
                request.fansMin(),
                request.fansMax(),
                defaultText(request.cooperationType(), current.cooperationType()),
                current.status(),
                current.createdAt()
        );
        store.saveProduct(updated);
        return Optional.of(updated);
    }

    /**
     * Updates product publication status when the merchant id matches.
     *
     * @param id product id
     * @param request status update request
     * @return updated product or empty when missing/not owned by the merchant
     */
    public Optional<Product> updateProductStatus(long id, ProductStatusRequest request) {
        Optional<Product> currentValue = store.findProduct(id);
        if (currentValue.isEmpty() || currentValue.get().merchantId() != request.merchantId()) {
            return Optional.empty();
        }
        Product current = currentValue.get();

        Product updated = new Product(
                current.id(),
                current.merchantId(),
                current.name(),
                current.type(),
                current.targetCategories(),
                current.description(),
                current.goal(),
                current.budgetMin(),
                current.budgetMax(),
                current.maxQuotePerInfluencer(),
                current.platform(),
                current.contentForms(),
                current.fansMin(),
                current.fansMax(),
                current.cooperationType(),
                request.status(),
                current.createdAt()
        );
        store.saveProduct(updated);
        return Optional.of(updated);
    }

    /**
     * Lists active product demands that match optional filters.
     *
     * @param type optional industry/type filter
     * @param platform optional platform filter
     * @param budgetMin optional minimum budget filter
     * @param fansMin optional minimum follower requirement filter
     * @param cooperationType optional cooperation type filter
     * @return matching products sorted newest first
     */
    public List<Product> listProducts(Optional<String> type, Optional<String> platform, Optional<Integer> budgetMin, Optional<Integer> fansMin, Optional<String> cooperationType) {
        return store.listProducts().stream()
                .filter(product -> DEFAULT_STATUS_ACTIVE.equals(product.status()))
                .filter(product -> type.map(value -> product.type().contains(value)).orElse(true))
                .filter(product -> platform.map(value -> product.platform().contains(value)).orElse(true))
                .filter(product -> budgetMin.map(value -> product.budgetMax() >= value).orElse(true))
                .filter(product -> fansMin.map(value -> product.fansMax() >= value).orElse(true))
                .filter(product -> cooperationType.map(value -> product.cooperationType().contains(value)).orElse(true))
                .sorted(Comparator.comparing(Product::createdAt).reversed())
                .toList();
    }

    /**
     * Finds a product by id.
     *
     * @param id product id
     * @return product when present
     */
    public Optional<Product> findProduct(long id) {
        return store.findProduct(id);
    }

    /**
     * Lists all products belonging to a merchant.
     *
     * @param merchantId merchant id
     * @return merchant products sorted newest first
     */
    public List<Product> listMerchantProducts(long merchantId) {
        return store.listProducts().stream()
                .filter(product -> product.merchantId() == merchantId)
                .sorted(Comparator.comparing(Product::createdAt).reversed())
                .toList();
    }

    /**
     * Scores public influencers against a product demand.
     *
     * @param productId product id
     * @param limit maximum number of matches
     * @return sorted match maps containing score and masked influencer
     */
    public List<Map<String, Object>> matchInfluencers(long productId, int limit) {
        Optional<Product> productValue = store.findProduct(productId);
        if (productValue.isEmpty()) {
            return List.of();
        }
        Product product = productValue.get();

        return store.listInfluencers().stream()
                .filter(Influencer::isPublic)
                .map(influencer -> Map.of(
                        "score", score(product, influencer),
                        "influencer", influencerProfileViewService.maskInfluencer(influencer, false)
                ))
                .sorted((left, right) -> Double.compare((Double) right.get("score"), (Double) left.get("score")))
                .limit(limit)
                .toList();
    }

    private double score(Product product, Influencer influencer) {
        double category = product.type().equals(influencer.category()) ? 0.4 : 0;
        double fans = influencer.fansCount() >= product.fansMin() && influencer.fansCount() <= product.fansMax() ? 0.3 : 0;
        double budget = StringUtils.hasText(influencer.priceRange()) ? 0.2 : 0;
        double platform = product.platform().contains(influencer.platform()) || influencer.platform().contains(product.platform()) ? 0.1 : 0;
        return category + fans + budget + platform;
    }

    private String defaultText(String value, String fallback) {
        return StringUtils.hasText(value) ? value : fallback;
    }

    /**
     * Product demand payload.
     *
     * @param merchantId owning merchant id
     * @param name product name
     * @param type industry or product type
     * @param targetCategories target influencer categories
     * @param description product description
     * @param goal campaign goal
     * @param budgetMin minimum campaign budget
     * @param budgetMax maximum campaign budget
     * @param maxQuotePerInfluencer maximum quote per influencer
     * @param platform target platform
     * @param contentForms desired content forms
     * @param fansMin minimum follower count
     * @param fansMax maximum follower count
     * @param cooperationType cooperation type
     */
    public record ProductRequest(long merchantId, String name, String type, String targetCategories, String description, String goal, int budgetMin, int budgetMax, int maxQuotePerInfluencer, String platform, String contentForms, int fansMin, int fansMax, String cooperationType) {
    }

    /**
     * Product status update payload.
     *
     * @param merchantId owning merchant id
     * @param status next product status
     */
    public record ProductStatusRequest(long merchantId, @NotBlank String status) {
    }
}
