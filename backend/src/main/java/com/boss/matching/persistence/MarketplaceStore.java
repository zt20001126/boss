package com.boss.matching.persistence;

import com.boss.matching.domain.*;

import java.util.List;
import java.util.Optional;

/**
 * Persistence port used by services to access marketplace data without depending on a concrete storage provider.
 */
public interface MarketplaceStore {
    /**
     * Allocates the next application-level id.
     *
     * @return next id value
     */
    long nextId();

    /**
     * Saves a user account.
     *
     * @param user user to save
     */
    void saveUser(User user);

    /**
     * Finds a user by id.
     *
     * @param id user id
     * @return user when present
     */
    Optional<User> findUser(long id);

    /**
     * Finds a user by phone number.
     *
     * @param phone phone number
     * @return user when present
     */
    Optional<User> findUserByPhone(String phone);

    /**
     * Finds a user by WeChat openid.
     *
     * @param openid WeChat openid
     * @return user when present
     */
    Optional<User> findUserByOpenid(String openid);

    /**
     * Saves a merchant profile.
     *
     * @param merchant merchant to save
     */
    void saveMerchant(Merchant merchant);

    /**
     * Finds a merchant by id.
     *
     * @param id merchant id
     * @return merchant when present
     */
    Optional<Merchant> findMerchant(long id);

    /**
     * Finds a merchant by owning user id.
     *
     * @param userId user id
     * @return merchant when present
     */
    Optional<Merchant> findMerchantByUserId(long userId);

    /**
     * Saves an influencer profile.
     *
     * @param influencer influencer to save
     */
    void saveInfluencer(Influencer influencer);

    /**
     * Finds an influencer by id.
     *
     * @param id influencer id
     * @return influencer when present
     */
    Optional<Influencer> findInfluencer(long id);

    /**
     * Finds an influencer by owning user id.
     *
     * @param userId user id
     * @return influencer when present
     */
    Optional<Influencer> findInfluencerByUserId(long userId);

    /**
     * Lists all influencer profiles.
     *
     * @return influencer list
     */
    List<Influencer> listInfluencers();

    /**
     * Replaces portfolio entries for an influencer.
     *
     * @param influencerId influencer id
     * @param portfolios next portfolio entries
     */
    void saveInfluencerPortfolios(long influencerId, List<InfluencerPortfolio> portfolios);

    /**
     * Lists portfolio entries for an influencer.
     *
     * @param influencerId influencer id
     * @return portfolio entries
     */
    List<InfluencerPortfolio> listInfluencerPortfolios(long influencerId);

    /**
     * Saves a product demand.
     *
     * @param product product to save
     */
    void saveProduct(Product product);

    /**
     * Finds a product by id.
     *
     * @param id product id
     * @return product when present
     */
    Optional<Product> findProduct(long id);

    /**
     * Lists all product demands.
     *
     * @return product list
     */
    List<Product> listProducts();

    /**
     * Saves a paid unlock record.
     *
     * @param record unlock record to save
     */
    void saveUnlock(UnlockRecord record);

    /**
     * Finds an unlock record for a merchant/influencer/product tuple.
     *
     * @param merchantId merchant id
     * @param influencerId influencer id
     * @param productId product id
     * @return unlock record when present
     */
    Optional<UnlockRecord> findUnlock(long merchantId, long influencerId, long productId);
}
