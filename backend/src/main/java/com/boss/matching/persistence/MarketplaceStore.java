package com.boss.matching.persistence;

import com.boss.matching.domain.*;

import java.util.List;
import java.util.Optional;

public interface MarketplaceStore {
    long nextId();

    void saveUser(User user);

    Optional<User> findUser(long id);

    Optional<User> findUserByPhone(String phone);

    Optional<User> findUserByOpenid(String openid);

    void saveMerchant(Merchant merchant);

    Optional<Merchant> findMerchant(long id);

    Optional<Merchant> findMerchantByUserId(long userId);

    void saveInfluencer(Influencer influencer);

    Optional<Influencer> findInfluencer(long id);

    Optional<Influencer> findInfluencerByUserId(long userId);

    List<Influencer> listInfluencers();

    void saveInfluencerPortfolios(long influencerId, List<InfluencerPortfolio> portfolios);

    List<InfluencerPortfolio> listInfluencerPortfolios(long influencerId);

    void saveProduct(Product product);

    Optional<Product> findProduct(long id);

    List<Product> listProducts();

    void saveUnlock(UnlockRecord record);

    Optional<UnlockRecord> findUnlock(long merchantId, long influencerId, long productId);
}
