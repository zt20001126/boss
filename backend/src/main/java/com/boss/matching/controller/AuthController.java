package com.boss.matching.controller;

import com.boss.matching.domain.UserRole;
import com.boss.matching.service.AuthService;
import com.boss.matching.service.AuthTokenService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Exposes authentication endpoints while delegating all login and binding logic to {@link AuthService}.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final String AUTH_EXPIRED_MESSAGE = "登录已过期，请重新登录";
    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;
    private final AuthTokenService authTokenService;

    /**
     * Creates an authentication controller.
     *
     * @param authService authentication business service
     * @param authTokenService JWT verifier for role binding
     */
    public AuthController(AuthService authService, AuthTokenService authTokenService) {
        this.authService = authService;
        this.authTokenService = authTokenService;
    }

    /**
     * Creates a development-only mock login session.
     *
     * @param request mock role and optional nickname
     * @return token, user, and profile payload or a forbidden response when mock login is disabled
     */
    @PostMapping("/mock-login")
    public ResponseEntity<?> mockLogin(@Valid @RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.mockLogin(request.role(), request.nickname()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(403).body(Map.of("message", ex.getMessage()));
        }
    }

    /**
     * Registers a phone-code account.
     *
     * @param request registration payload
     * @return session payload
     */
    @PostMapping("/register")
    public Map<String, Object> register(@Valid @RequestBody AuthService.AuthRegisterRequest request) {
        return authService.register(request);
    }

    /**
     * Logs in a phone-code account.
     *
     * @param request login payload
     * @return session payload
     */
    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody AuthService.AuthLoginRequest request) {
        return authService.login(request);
    }

    /**
     * Logs in or creates an unbound user from a WeChat login code.
     *
     * @param request WeChat login payload
     * @return session payload
     */
    @PostMapping("/wx-login")
    public Map<String, Object> wxLogin(@Valid @RequestBody AuthService.WxLoginRequest request) {
        return authService.wxLogin(request);
    }

    /**
     * Binds the authenticated unbound user to a selected role.
     *
     * @param authorization bearer token header
     * @param request selected role payload
     * @return session payload or unauthorized response
     */
    @PostMapping("/bind-role")
    public ResponseEntity<?> bindRole(@RequestHeader(value = "Authorization", required = false) String authorization, @Valid @RequestBody AuthService.BindRoleRequest request) {
        return authTokenService.verify(bearerToken(authorization))
                .map(claims -> ResponseEntity.ok(authService.bindRole(claims.userId(), request)))
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("message", AUTH_EXPIRED_MESSAGE)));
    }

    private String bearerToken(String authorization) {
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) return "";
        return authorization.substring(BEARER_PREFIX.length());
    }

    /**
     * Request body for development mock login.
     *
     * @param role role to create
     * @param nickname optional nickname for the generated profile
     */
    public record LoginRequest(@NotNull UserRole role, String nickname) {
    }
}
