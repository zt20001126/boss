package com.boss.matching.controller;

import com.boss.matching.service.ProductFavoriteService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 达人产品收藏接口，达人身份统一从已校验的 JWT 用户信息中获取。
 */
@RestController
@RequestMapping("/api/influencer/favorites")
public class ProductFavoriteController {
    private final ProductFavoriteService favoriteService;

    public ProductFavoriteController(ProductFavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /** 获取当前达人的全部收藏产品。 */
    @GetMapping
    public Map<String, Object> favorites(@RequestAttribute("authUserId") long userId) {
        return Map.of("items", favoriteService.listFavorites(userId));
    }

    /** 查询指定产品的收藏状态。 */
    @GetMapping("/{productId}")
    public Map<String, Boolean> favoriteStatus(@RequestAttribute("authUserId") long userId, @PathVariable long productId) {
        return Map.of("favorited", favoriteService.isFavorite(userId, productId));
    }

    /** 幂等收藏指定产品。 */
    @PostMapping("/{productId}")
    public Map<String, Boolean> favorite(@RequestAttribute("authUserId") long userId, @PathVariable long productId) {
        return Map.of("favorited", favoriteService.favorite(userId, productId));
    }

    /** 幂等取消指定产品的收藏。 */
    @DeleteMapping("/{productId}")
    public Map<String, Boolean> unfavorite(@RequestAttribute("authUserId") long userId, @PathVariable long productId) {
        return Map.of("favorited", favoriteService.unfavorite(userId, productId));
    }
}
