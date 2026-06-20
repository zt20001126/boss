package com.boss.matching.service;

import com.boss.matching.config.AppProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * Resolves WeChat login codes into stable openid and unionid identifiers.
 */
@Service
public class WechatAuthService {
    private static final String CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    private final AppProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Creates a WechatAuthService instance.
     * @param properties input value
     */
    public WechatAuthService(AppProperties properties) {
        this.properties = properties;
    }

    /**
     * Exchanges a WeChat login code for session identity data.
     * @param code input value
     * @return result value
     */
    public WechatSession code2Session(String code) {
        if (!StringUtils.hasText(code)) {
            throw new IllegalArgumentException("微信登录 code 不能为空");
        }

        if (properties.isMockEnabled()) {
            String suffix = code.replaceAll("[^a-zA-Z0-9_-]", "");
            return new WechatSession("mock-openid-" + suffix, null);
        }

        String url = UriComponentsBuilder.fromHttpUrl(CODE2SESSION_URL)
                .queryParam("appid", properties.getWechat().getAppId())
                .queryParam("secret", properties.getWechat().getAppSecret())
                .queryParam("js_code", code)
                .queryParam("grant_type", "authorization_code")
                .toUriString();
        Map<?, ?> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !StringUtils.hasText((String) response.get("openid"))) {
            Object message = response == null ? "empty response" : response.get("errmsg");
            if (message == null) message = "unknown error";
            throw new IllegalArgumentException("微信登录失败：" + message);
        }

        return new WechatSession((String) response.get("openid"), (String) response.get("unionid"));
    }

    /**
     * WeChat identity returned after code-to-session resolution.
     */
    public record WechatSession(String openid, String unionid) {
    }
}
