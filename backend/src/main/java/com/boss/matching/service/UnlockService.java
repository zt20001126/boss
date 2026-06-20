package com.boss.matching.service;

import com.boss.matching.domain.Influencer;
import com.boss.matching.domain.Merchant;
import com.boss.matching.domain.Product;
import com.boss.matching.domain.UnlockRecord;
import com.boss.matching.payment.PaymentResult;
import com.boss.matching.payment.PaymentService;
import com.boss.matching.persistence.MarketplaceStore;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

/**
 * Handles paid unlock validation and payment confirmation for influencer contact data.
 */
@Service
public class UnlockService {
    private final MarketplaceStore store;
    private final PaymentService paymentService;

    /**
     * Creates an unlock service.
     *
     * @param store marketplace persistence abstraction
     * @param paymentService payment adapter for unlock orders
     */
    public UnlockService(MarketplaceStore store, PaymentService paymentService) {
        this.store = store;
        this.paymentService = paymentService;
    }

    /**
     * Unlocks influencer contact data for a merchant product.
     *
     * @param request unlock request
     * @return existing or newly paid unlock record
     */
    public UnlockRecord unlock(UnlockRequest request) {
        Optional<UnlockRecord> current = store.findUnlock(request.merchantId(), request.influencerId(), request.productId());
        if (current.isPresent()) {
            return current.get();
        }

        Optional<Merchant> merchant = store.findMerchant(request.merchantId());
        Optional<Product> product = store.findProduct(request.productId());
        Optional<Influencer> influencer = store.findInfluencer(request.influencerId());

        if (merchant.isEmpty()) {
            throw new IllegalArgumentException("商家不存在");
        }
        if (product.isEmpty() || product.get().merchantId() != request.merchantId()) {
            throw new IllegalArgumentException("产品不属于当前商家");
        }
        if (influencer.isEmpty() || !influencer.get().isPublic()) {
            throw new IllegalArgumentException("达人未公开，无法解锁");
        }

        PaymentResult order = paymentService.createUnlockOrder(request.merchantId(), request.influencerId(), request.productId());
        // MVP mock payments are confirmed immediately; real WeChat Pay can move confirmation to a callback later.
        PaymentResult result = paymentService.confirmUnlockPayment(order);
        UnlockRecord record = new UnlockRecord(store.nextId(), request.merchantId(), request.influencerId(), request.productId(), result.amountCent(), result.status(), Instant.now());
        store.saveUnlock(record);
        return record;
    }

    /**
     * Unlock request payload.
     *
     * @param merchantId merchant id
     * @param influencerId influencer id
     * @param productId product id
     */
    public record UnlockRequest(long merchantId, long influencerId, long productId) {
    }
}
