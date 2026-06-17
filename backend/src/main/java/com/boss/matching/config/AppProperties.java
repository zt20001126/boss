package com.boss.matching.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private boolean mockEnabled = true;
    private Data data = new Data();
    private Storage storage = new Storage();
    private Cache cache = new Cache();
    private Payment payment = new Payment();
    private Wechat wechat = new Wechat();
    private Auth auth = new Auth();

    public boolean isMockEnabled() {
        return mockEnabled;
    }

    public void setMockEnabled(boolean mockEnabled) {
        this.mockEnabled = mockEnabled;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Wechat getWechat() {
        return wechat;
    }

    public void setWechat(Wechat wechat) {
        this.wechat = wechat;
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public static class Storage {
        private String provider = "local";
        private String localRoot = "./data/uploads";

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getLocalRoot() {
            return localRoot;
        }

        public void setLocalRoot(String localRoot) {
            this.localRoot = localRoot;
        }
    }

    public static class Data {
        private String provider = "memory";

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }
    }

    public static class Cache {
        private String provider = "memory";

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }
    }

    public static class Payment {
        private String provider = "mock";
        private int unlockAmountCent = 990;

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public int getUnlockAmountCent() {
            return unlockAmountCent;
        }

        public void setUnlockAmountCent(int unlockAmountCent) {
            this.unlockAmountCent = unlockAmountCent;
        }
    }

    public static class Wechat {
        private String appId = "mock-wechat-app-id";
        private String appSecret = "mock-wechat-app-secret";

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }
    }

    public static class Auth {
        private String mockCode = "123456";
        private String jwtSecret = "dev-secret-change-me";
        private long tokenTtlSeconds = 86400;

        public String getMockCode() {
            return mockCode;
        }

        public void setMockCode(String mockCode) {
            this.mockCode = mockCode;
        }

        public String getJwtSecret() {
            return jwtSecret;
        }

        public void setJwtSecret(String jwtSecret) {
            this.jwtSecret = jwtSecret;
        }

        public long getTokenTtlSeconds() {
            return tokenTtlSeconds;
        }

        public void setTokenTtlSeconds(long tokenTtlSeconds) {
            this.tokenTtlSeconds = tokenTtlSeconds;
        }
    }
}
