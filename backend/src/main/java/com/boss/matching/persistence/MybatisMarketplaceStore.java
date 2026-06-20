package com.boss.matching.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.boss.matching.domain.*;
import com.boss.matching.persistence.entity.*;
import com.boss.matching.persistence.mapper.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * MyBatis backed MarketplaceStore implementation that delegates database access to mapper interfaces.
 */
@Repository
@ConditionalOnProperty(prefix = "app.data", name = "provider", havingValue = "mysql")
public class MybatisMarketplaceStore implements MarketplaceStore {
    private final AtomicLong ids = new AtomicLong(System.currentTimeMillis());
    private final UserMapper userMapper;
    private final MerchantMapper merchantMapper;
    private final InfluencerMapper influencerMapper;
    private final InfluencerPortfolioMapper influencerPortfolioMapper;
    private final ProductMapper productMapper;
    private final UnlockRecordMapper unlockRecordMapper;

    /**
     * Creates a MybatisMarketplaceStore instance.
     * @param userMapper input value
     * @param merchantMapper input value
     * @param influencerMapper input value
     * @param influencerPortfolioMapper input value
     * @param productMapper input value
     * @param unlockRecordMapper input value
     */
    public MybatisMarketplaceStore(UserMapper userMapper, MerchantMapper merchantMapper, InfluencerMapper influencerMapper, InfluencerPortfolioMapper influencerPortfolioMapper, ProductMapper productMapper, UnlockRecordMapper unlockRecordMapper) {
        this.userMapper = userMapper;
        this.merchantMapper = merchantMapper;
        this.influencerMapper = influencerMapper;
        this.influencerPortfolioMapper = influencerPortfolioMapper;
        this.productMapper = productMapper;
        this.unlockRecordMapper = unlockRecordMapper;
    }

    /** {@inheritDoc} */
    @Override
    public long nextId() {
        return ids.incrementAndGet();
    }

    /** {@inheritDoc} */
    @Override
    public void saveUser(User user) {
        if (userMapper.selectById(user.id()) == null) {
            userMapper.insert(toEntity(user));
        } else {
            userMapper.updateById(toEntity(user));
        }
    }

    /** {@inheritDoc} */
    @Override
    public Optional<User> findUser(long id) {
        return Optional.ofNullable(userMapper.selectById(id)).map(this::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<User> findUserByPhone(String phone) {
        return Optional.ofNullable(userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getPhone, phone))).map(this::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<User> findUserByOpenid(String openid) {
        return Optional.ofNullable(userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getOpenid, openid))).map(this::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public void saveMerchant(Merchant merchant) {
        if (merchantMapper.selectById(merchant.id()) == null) {
            merchantMapper.insert(toEntity(merchant));
        } else {
            merchantMapper.updateById(toEntity(merchant));
        }
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Merchant> findMerchant(long id) {
        return Optional.ofNullable(merchantMapper.selectById(id)).map(this::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Merchant> findMerchantByUserId(long userId) {
        return Optional.ofNullable(merchantMapper.selectOne(new LambdaQueryWrapper<MerchantEntity>().eq(MerchantEntity::getUserId, userId))).map(this::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public void saveInfluencer(Influencer influencer) {
        if (influencerMapper.selectById(influencer.id()) == null) {
            influencerMapper.insert(toEntity(influencer));
        } else {
            influencerMapper.updateById(toEntity(influencer));
        }
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Influencer> findInfluencer(long id) {
        return Optional.ofNullable(influencerMapper.selectById(id)).map(this::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Influencer> findInfluencerByUserId(long userId) {
        return Optional.ofNullable(influencerMapper.selectOne(new LambdaQueryWrapper<InfluencerEntity>().eq(InfluencerEntity::getUserId, userId))).map(this::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public List<Influencer> listInfluencers() {
        return influencerMapper.selectList(null).stream().map(this::toDomain).toList();
    }

    /** {@inheritDoc} */
    @Override
    public void saveInfluencerPortfolios(long influencerId, List<InfluencerPortfolio> portfolios) {
        influencerPortfolioMapper.delete(new LambdaQueryWrapper<InfluencerPortfolioEntity>().eq(InfluencerPortfolioEntity::getInfluencerId, influencerId));
        portfolios.forEach(item -> influencerPortfolioMapper.insert(toEntity(item)));
    }

    /** {@inheritDoc} */
    @Override
    public List<InfluencerPortfolio> listInfluencerPortfolios(long influencerId) {
        return influencerPortfolioMapper.selectList(new LambdaQueryWrapper<InfluencerPortfolioEntity>()
                        .eq(InfluencerPortfolioEntity::getInfluencerId, influencerId)
                        .orderByAsc(InfluencerPortfolioEntity::getSortOrder))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    /** {@inheritDoc} */
    @Override
    public void saveProduct(Product product) {
        if (productMapper.selectById(product.id()) == null) {
            productMapper.insert(toEntity(product));
        } else {
            productMapper.updateById(toEntity(product));
        }
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Product> findProduct(long id) {
        return Optional.ofNullable(productMapper.selectById(id)).map(this::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public List<Product> listProducts() {
        return productMapper.selectList(null).stream().map(this::toDomain).toList();
    }

    /** {@inheritDoc} */
    @Override
    public void saveUnlock(UnlockRecord record) {
        unlockRecordMapper.insert(toEntity(record));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<UnlockRecord> findUnlock(long merchantId, long influencerId, long productId) {
        LambdaQueryWrapper<UnlockRecordEntity> query = new LambdaQueryWrapper<UnlockRecordEntity>()
                .eq(UnlockRecordEntity::getMerchantId, merchantId)
                .eq(UnlockRecordEntity::getInfluencerId, influencerId)
                .eq(UnlockRecordEntity::getProductId, productId);
        return Optional.ofNullable(unlockRecordMapper.selectOne(query)).map(this::toDomain);
    }

    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.id());
        entity.setRole(user.role().name());
        entity.setPhone(user.phone());
        entity.setOpenid(user.openid());
        entity.setUnionid(user.unionid());
        entity.setLoginType(user.loginType());
        entity.setStatus("ACTIVE");
        entity.setCreatedTime(toLocal(user.createdTime()));
        return entity;
    }

    private User toDomain(UserEntity entity) {
        return new User(entity.getId(), UserRole.valueOf(entity.getRole()), entity.getPhone(), entity.getOpenid(), entity.getUnionid(), entity.getLoginType(), toInstant(entity.getCreatedTime()));
    }

    private MerchantEntity toEntity(Merchant merchant) {
        MerchantEntity entity = new MerchantEntity();
        entity.setId(merchant.id());
        entity.setUserId(merchant.userId());
        entity.setName(merchant.name());
        entity.setIndustry(merchant.industry());
        entity.setDescription(merchant.description());
        entity.setContact(merchant.contact());
        return entity;
    }

    private Merchant toDomain(MerchantEntity entity) {
        return new Merchant(entity.getId(), entity.getUserId(), entity.getName(), entity.getIndustry(), entity.getDescription(), entity.getContact());
    }

    private InfluencerEntity toEntity(Influencer influencer) {
        InfluencerEntity entity = new InfluencerEntity();
        entity.setId(influencer.id());
        entity.setUserId(influencer.userId());
        entity.setNickname(influencer.nickname());
        entity.setAvatarUrl(influencer.avatarUrl());
        entity.setCity(influencer.city());
        entity.setPlatform(influencer.platform());
        entity.setFansRange(influencer.fansRange());
        entity.setFansCount(influencer.fansCount());
        entity.setCategory(influencer.category());
        entity.setCategories(influencer.categories());
        entity.setStyleTags(influencer.styleTags());
        entity.setContentForms(influencer.contentForms());
        entity.setPriceRange(influencer.priceRange());
        entity.setPriceImageText(influencer.priceImageText());
        entity.setPriceVideo(influencer.priceVideo());
        entity.setPriceDetail(influencer.priceDetail());
        entity.setContactWechat(influencer.contactWechat());
        entity.setContactPhone(influencer.contactPhone());
        entity.setContact(influencer.contact());
        entity.setSocialAccount(influencer.socialAccount());
        entity.setIsPublic(influencer.isPublic());
        return entity;
    }

    private Influencer toDomain(InfluencerEntity entity) {
        return new Influencer(entity.getId(), entity.getUserId(), entity.getNickname(), entity.getAvatarUrl(), entity.getCity(), entity.getPlatform(), entity.getFansRange(), value(entity.getFansCount()), entity.getCategory(), entity.getCategories(), entity.getStyleTags(), entity.getContentForms(), entity.getPriceRange(), value(entity.getPriceImageText()), value(entity.getPriceVideo()), entity.getPriceDetail(), entity.getContactWechat(), entity.getContactPhone(), entity.getContact(), entity.getSocialAccount(), Boolean.TRUE.equals(entity.getIsPublic()));
    }

    private ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setId(product.id());
        entity.setMerchantId(product.merchantId());
        entity.setName(product.name());
        entity.setType(product.type());
        entity.setTargetCategories(product.targetCategories());
        entity.setDescription(product.description());
        entity.setGoal(product.goal());
        entity.setBudgetMin(product.budgetMin());
        entity.setBudgetMax(product.budgetMax());
        entity.setMaxQuotePerInfluencer(product.maxQuotePerInfluencer());
        entity.setPlatform(product.platform());
        entity.setContentForms(product.contentForms());
        entity.setFansMin(product.fansMin());
        entity.setFansMax(product.fansMax());
        entity.setCooperationType(product.cooperationType());
        entity.setStatus(product.status());
        entity.setCreatedAt(toLocal(product.createdAt()));
        return entity;
    }

    private Product toDomain(ProductEntity entity) {
        return new Product(entity.getId(), entity.getMerchantId(), entity.getName(), entity.getType(), entity.getTargetCategories(), entity.getDescription(), entity.getGoal(), value(entity.getBudgetMin()), value(entity.getBudgetMax()), value(entity.getMaxQuotePerInfluencer()), entity.getPlatform(), entity.getContentForms(), value(entity.getFansMin()), value(entity.getFansMax()), entity.getCooperationType(), entity.getStatus(), toInstant(entity.getCreatedAt()));
    }

    private InfluencerPortfolioEntity toEntity(InfluencerPortfolio portfolio) {
        InfluencerPortfolioEntity entity = new InfluencerPortfolioEntity();
        entity.setId(portfolio.id());
        entity.setInfluencerId(portfolio.influencerId());
        entity.setTitle(portfolio.title());
        entity.setCoverUrl(portfolio.coverUrl());
        entity.setContentUrl(portfolio.contentUrl());
        entity.setPlatform(portfolio.platform());
        entity.setSortOrder(portfolio.sortOrder());
        entity.setCreatedTime(toLocal(portfolio.createdTime()));
        return entity;
    }

    private InfluencerPortfolio toDomain(InfluencerPortfolioEntity entity) {
        return new InfluencerPortfolio(entity.getId(), entity.getInfluencerId(), entity.getTitle(), entity.getCoverUrl(), entity.getContentUrl(), entity.getPlatform(), value(entity.getSortOrder()), toInstant(entity.getCreatedTime()));
    }

    private UnlockRecordEntity toEntity(UnlockRecord record) {
        UnlockRecordEntity entity = new UnlockRecordEntity();
        entity.setId(record.id());
        entity.setMerchantId(record.merchantId());
        entity.setInfluencerId(record.influencerId());
        entity.setProductId(record.productId());
        entity.setAmountCent(record.amountCent());
        entity.setStatus(record.status());
        entity.setUnlockType("CONTACT");
        entity.setCreatedAt(toLocal(record.createdAt()));
        return entity;
    }

    private UnlockRecord toDomain(UnlockRecordEntity entity) {
        return new UnlockRecord(entity.getId(), entity.getMerchantId(), entity.getInfluencerId(), entity.getProductId(), entity.getAmountCent(), entity.getStatus(), toInstant(entity.getCreatedAt()));
    }

    private LocalDateTime toLocal(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    private Instant toInstant(LocalDateTime time) {
        return time == null ? Instant.now() : time.atZone(ZoneId.systemDefault()).toInstant();
    }

    private int value(Integer value) {
        return value == null ? 0 : value;
    }
}
