package com.boss.matching.service;

import com.boss.matching.config.AppProperties;
import com.boss.matching.domain.User;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

/**
 * Creates and verifies JWT tokens used by the mini-program API.
 */
@Service
public class AuthTokenService {
    private static final Pattern USER_ID_PATTERN = Pattern.compile("\"userId\":(\\d+)");
    private static final Pattern ROLE_PATTERN = Pattern.compile("\"role\":\"([A-Z_]+)\"");
    private static final Pattern EXP_PATTERN = Pattern.compile("\"exp\":(\\d+)");

    private final AppProperties properties;

    /**
     * Creates a AuthTokenService instance.
     * @param properties input value
     */
    public AuthTokenService(AppProperties properties) {
        this.properties = properties;
    }

    /**
     * Creates data for create token.
     * @param user input value
     * @return result value
     */
    public String createToken(User user) {
        long expiresAt = Instant.now().plusSeconds(properties.getAuth().getTokenTtlSeconds()).getEpochSecond();
        String header = encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = encode("{\"userId\":" + user.id() + ",\"role\":\"" + user.role() + "\",\"exp\":" + expiresAt + "}");
        String signature = sign(header + "." + payload);
        return header + "." + payload + "." + signature;
    }

    /**
     * Verifies data for verify.
     * @param token input value
     * @return result value
     */
    public Optional<TokenClaims> verify(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3 || !sign(parts[0] + "." + parts[1]).equals(parts[2])) {
            return Optional.empty();
        }

        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        long expiresAt = longValue(payload, EXP_PATTERN);
        if (expiresAt < Instant.now().getEpochSecond()) {
            return Optional.empty();
        }

        return Optional.of(new TokenClaims(longValue(payload, USER_ID_PATTERN), stringValue(payload, ROLE_PATTERN)));
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(properties.getAuth().getJwtSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("JWT 签名生成失败", ex);
        }
    }

    private String encode(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private long longValue(String payload, Pattern pattern) {
        Matcher matcher = pattern.matcher(payload);
        return matcher.find() ? Long.parseLong(matcher.group(1)) : 0;
    }

    private String stringValue(String payload, Pattern pattern) {
        Matcher matcher = pattern.matcher(payload);
        return matcher.find() ? matcher.group(1) : "";
    }

    /**
     * Verified token claims extracted from a JWT.
     */
    public record TokenClaims(long userId, String role) {
    }
}
