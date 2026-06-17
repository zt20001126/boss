package com.boss.matching.persistence;

import com.boss.matching.domain.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@ConditionalOnProperty(prefix = "app.data", name = "provider", havingValue = "memory", matchIfMissing = true)
public class MemoryMarketplaceStore implements MarketplaceStore {
    private final AtomicLong ids = new AtomicLong(1000);
    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Merchant> merchants = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Influencer> influencers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, List<InfluencerPortfolio>> portfolios = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Product> products = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, UnlockRecord> unlocks = new ConcurrentHashMap<>();

    @Override
    public long nextId() {
        return ids.incrementAndGet();
    }

    @Override
    public void saveUser(User user) {
        users.put(user.id(), user);
    }

    @Override
    public Optional<User> findUser(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findUserByPhone(String phone) {
        return users.values().stream().filter(user -> phone.equals(user.phone())).findFirst();
    }

    @Override
    public Optional<User> findUserByOpenid(String openid) {
        return users.values().stream().filter(user -> openid.equals(user.openid())).findFirst();
    }

    @Override
    public void saveMerchant(Merchant merchant) {
        merchants.put(merchant.id(), merchant);
    }

    @Override
    public Optional<Merchant> findMerchant(long id) {
        return Optional.ofNullable(merchants.get(id));
    }

    @Override
    public Optional<Merchant> findMerchantByUserId(long userId) {
        return merchants.values().stream().filter(item -> item.userId() == userId).findFirst();
    }

    @Override
    public void saveInfluencer(Influencer influencer) {
        influencers.put(influencer.id(), influencer);
    }

    @Override
    public Optional<Influencer> findInfluencer(long id) {
        return Optional.ofNullable(influencers.get(id));
    }

    @Override
    public Optional<Influencer> findInfluencerByUserId(long userId) {
        return influencers.values().stream().filter(item -> item.userId() == userId).findFirst();
    }

    @Override
    public List<Influencer> listInfluencers() {
        return List.copyOf(influencers.values());
    }

    @Override
    public void saveInfluencerPortfolios(long influencerId, List<InfluencerPortfolio> nextPortfolios) {
        portfolios.put(influencerId, List.copyOf(nextPortfolios));
    }

    @Override
    public List<InfluencerPortfolio> listInfluencerPortfolios(long influencerId) {
        return portfolios.getOrDefault(influencerId, List.of());
    }

    @Override
    public void saveProduct(Product product) {
        products.put(product.id(), product);
    }

    @Override
    public Optional<Product> findProduct(long id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> listProducts() {
        return List.copyOf(products.values());
    }

    @Override
    public void saveUnlock(UnlockRecord record) {
        unlocks.put(key(record.merchantId(), record.influencerId(), record.productId()), record);
    }

    @Override
    public Optional<UnlockRecord> findUnlock(long merchantId, long influencerId, long productId) {
        return Optional.ofNullable(unlocks.get(key(merchantId, influencerId, productId)));
    }

    private String key(long merchantId, long influencerId, long productId) {
        return merchantId + ":" + influencerId + ":" + productId;
    }
}
