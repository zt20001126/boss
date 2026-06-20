package com.boss.matching.service;

import com.boss.matching.domain.Merchant;
import com.boss.matching.persistence.MarketplaceStore;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Handles merchant profile persistence and lookup.
 */
@Service
public class MerchantService {
    private final MarketplaceStore store;

    /**
     * Creates a merchant profile service.
     *
     * @param store marketplace persistence abstraction
     */
    public MerchantService(MarketplaceStore store) {
        this.store = store;
    }

    /**
     * Creates or updates a merchant profile for a user.
     *
     * @param request merchant profile request
     * @return saved merchant profile
     */
    public Merchant saveMerchantProfile(MerchantRequest request) {
        Optional<Merchant> current = store.findMerchantByUserId(request.userId());

        Merchant merchant = new Merchant(
                current.map(Merchant::id).orElseGet(store::nextId),
                request.userId(),
                request.name(),
                request.industry(),
                request.description(),
                request.contact()
        );
        store.saveMerchant(merchant);
        return merchant;
    }

    /**
     * Finds a merchant profile by user id.
     *
     * @param userId user id
     * @return merchant profile when present
     */
    public Optional<Merchant> findMerchantProfile(long userId) {
        return store.findMerchantByUserId(userId);
    }

    /**
     * Merchant profile payload.
     *
     * @param userId owning user id
     * @param name merchant name
     * @param industry merchant industry
     * @param description merchant description
     * @param contact merchant contact
     */
    public record MerchantRequest(long userId, @NotBlank String name, @NotBlank String industry, String description, String contact) {
    }
}
