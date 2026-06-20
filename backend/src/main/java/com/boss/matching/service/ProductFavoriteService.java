package com.boss.matching.service;

import com.boss.matching.domain.Influencer;
import com.boss.matching.domain.Product;
import com.boss.matching.domain.ProductFavorite;
import com.boss.matching.persistence.MarketplaceStore;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * 管理达人账号的产品收藏，所有操作都先由登录用户解析出自己的达人资料。
 */
@Service
public class ProductFavoriteService {
    private final MarketplaceStore store;

    public ProductFavoriteService(MarketplaceStore store) {
        this.store = store;
    }

    /**
     * 返回当前达人收藏的产品；下架产品仍然保留，缺失的产品记录会被跳过。
     */
    public List<Product> listFavorites(long userId) {
        long influencerId = requireInfluencer(userId).id();
        return store.listProductFavorites(influencerId).stream()
                .sorted(Comparator.comparing(ProductFavorite::createdAt).reversed())
                .map(favorite -> store.findProduct(favorite.productId()))
                .flatMap(java.util.Optional::stream)
                .toList();
    }

    /**
     * 查询当前达人是否已经收藏指定产品。
     */
    public boolean isFavorite(long userId, long productId) {
        long influencerId = requireInfluencer(userId).id();
        return store.findProductFavorite(influencerId, productId).isPresent();
    }

    /**
     * 幂等收藏：已有收藏时直接返回成功，不重复插入记录。
     */
    public boolean favorite(long userId, long productId) {
        long influencerId = requireInfluencer(userId).id();
        if (store.findProduct(productId).isEmpty()) {
            throw new IllegalArgumentException("产品不存在");
        }
        if (store.findProductFavorite(influencerId, productId).isEmpty()) {
            store.saveProductFavorite(new ProductFavorite(store.nextId(), influencerId, productId, Instant.now()));
        }
        return true;
    }

    /**
     * 幂等取消收藏：记录不存在时也视为取消成功。
     */
    public boolean unfavorite(long userId, long productId) {
        long influencerId = requireInfluencer(userId).id();
        store.deleteProductFavorite(influencerId, productId);
        return false;
    }

    private Influencer requireInfluencer(long userId) {
        return store.findInfluencerByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("当前账号未创建达人资料"));
    }
}
