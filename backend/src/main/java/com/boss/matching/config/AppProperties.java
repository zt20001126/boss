package com.boss.matching.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds boss.* configuration used by storage, cache, payment, WeChat, and authentication adapters.
 */
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private boolean mockEnabled = true;
    private Data data = new Data();
    private Storage storage = new Storage();
    private Cache cache = new Cache();
    private Payment payment = new Payment();
    private Wechat wechat = new Wechat();
    private Auth auth = new Auth();

    /**
     * Returns the mock enabled value.
     * @return result value
     */
    public boolean isMockEnabled() {
        return mockEnabled;
    }

    /**
     * Sets the mock enabled value.
     * @param mockEnabled input value
     */
    public void setMockEnabled(boolean mockEnabled) {
        this.mockEnabled = mockEnabled;
    }

    /**
     * Returns the data value.
     * @return result value
     */
    public Data getData() {
        return data;
    }

    /**
     * Sets the data value.
     * @param data input value
     */
    public void setData(Data data) {
        this.data = data;
    }

    /**
     * Returns the storage value.
     * @return result value
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     * Sets the storage value.
     * @param storage input value
     */
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    /**
     * Returns the cache value.
     * @return result value
     */
    public Cache getCache() {
        return cache;
    }

    /**
     * Sets the cache value.
     * @param cache input value
     */
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    /**
     * Returns the payment value.
     * @return result value
     */
    public Payment getPayment() {
        return payment;
    }

    /**
     * Sets the payment value.
     * @param payment input value
     */
    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    /**
     * Returns the wechat value.
     * @return result value
     */
    public Wechat getWechat() {
        return wechat;
    }

    /**
     * Sets the wechat value.
     * @param wechat input value
     */
    public void setWechat(Wechat wechat) {
        this.wechat = wechat;
    }

    /**
     * Returns the auth value.
     * @return result value
     */
    public Auth getAuth() {
        return auth;
    }

    /**
     * Sets the auth value.
     * @param auth input value
     */
    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    /**
     * Storage adapter configuration.
     */
    public static class Storage {
        private String provider = "local";
        private String localRoot = "./data/uploads";

        /**
         * Returns the provider value.
         * @return result value
         */
        public String getProvider() {
            return provider;
        }

        /**
         * Sets the provider value.
         * @param provider input value
         */
        public void setProvider(String provider) {
            this.provider = provider;
        }

        /**
         * Returns the local root value.
         * @return result value
         */
        public String getLocalRoot() {
            return localRoot;
        }

        /**
         * Sets the local root value.
         * @param localRoot input value
         */
        public void setLocalRoot(String localRoot) {
            this.localRoot = localRoot;
        }
    }

    /**
     * Persistence provider configuration.
     */
    public static class Data {
        private String provider = "memory";

        /**
         * Returns the provider value.
         * @return result value
         */
        public String getProvider() {
            return provider;
        }

        /**
         * Sets the provider value.
         * @param provider input value
         */
        public void setProvider(String provider) {
            this.provider = provider;
        }
    }

    /**
     * Cache provider configuration.
     */
    public static class Cache {
        private String provider = "memory";

        /**
         * Returns the provider value.
         * @return result value
         */
        public String getProvider() {
            return provider;
        }

        /**
         * Sets the provider value.
         * @param provider input value
         */
        public void setProvider(String provider) {
            this.provider = provider;
        }
    }

    /**
     * Payment provider configuration.
     */
    public static class Payment {
        private String provider = "mock";
        private int unlockAmountCent = 990;

        /**
         * Returns the provider value.
         * @return result value
         */
        public String getProvider() {
            return provider;
        }

        /**
         * Sets the provider value.
         * @param provider input value
         */
        public void setProvider(String provider) {
            this.provider = provider;
        }

        /**
         * Returns the unlock amount cent value.
         * @return result value
         */
        public int getUnlockAmountCent() {
            return unlockAmountCent;
        }

        /**
         * Sets the unlock amount cent value.
         * @param unlockAmountCent input value
         */
        public void setUnlockAmountCent(int unlockAmountCent) {
            this.unlockAmountCent = unlockAmountCent;
        }
    }

    /**
     * WeChat mini-program integration configuration.
     */
    public static class Wechat {
        private String appId = "mock-wechat-app-id";
        private String appSecret = "mock-wechat-app-secret";

        /**
         * Returns the app id value.
         * @return result value
         */
        public String getAppId() {
            return appId;
        }

        /**
         * Sets the app id value.
         * @param appId input value
         */
        public void setAppId(String appId) {
            this.appId = appId;
        }

        /**
         * Returns the app secret value.
         * @return result value
         */
        public String getAppSecret() {
            return appSecret;
        }

        /**
         * Sets the app secret value.
         * @param appSecret input value
         */
        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }
    }

    /**
     * Authentication and token configuration.
     */
    public static class Auth {
        private String mockCode = "123456";
        private String jwtSecret = "dev-secret-change-me";
        private long tokenTtlSeconds = 86400;

        /**
         * Returns the mock code value.
         * @return result value
         */
        public String getMockCode() {
            return mockCode;
        }

        /**
         * Sets the mock code value.
         * @param mockCode input value
         */
        public void setMockCode(String mockCode) {
            this.mockCode = mockCode;
        }

        /**
         * Returns the jwt secret value.
         * @return result value
         */
        public String getJwtSecret() {
            return jwtSecret;
        }

        /**
         * Sets the jwt secret value.
         * @param jwtSecret input value
         */
        public void setJwtSecret(String jwtSecret) {
            this.jwtSecret = jwtSecret;
        }

        /**
         * Returns the token ttl seconds value.
         * @return result value
         */
        public long getTokenTtlSeconds() {
            return tokenTtlSeconds;
        }

        /**
         * Sets the token ttl seconds value.
         * @param tokenTtlSeconds input value
         */
        public void setTokenTtlSeconds(long tokenTtlSeconds) {
            this.tokenTtlSeconds = tokenTtlSeconds;
        }
    }
}
