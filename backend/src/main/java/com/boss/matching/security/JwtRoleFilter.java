package com.boss.matching.security;

import com.boss.matching.service.AuthTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Authenticates API requests by reading JWT claims and installing Spring Security authorities.
 */
@Component
public class JwtRoleFilter extends OncePerRequestFilter {
    private final AuthTokenService tokenService;

    /**
     * Creates a JwtRoleFilter instance.
     * @param tokenService input value
     */
    public JwtRoleFilter(AuthTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!requiresAuth(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        var claims = tokenService.verify(bearerToken(request));
        if (claims.isEmpty()) {
            reject(response, HttpServletResponse.SC_UNAUTHORIZED, "登录已过期，请重新登录");
            return;
        }

        if (!hasRoleAccess(request, claims.get().role())) {
            reject(response, HttpServletResponse.SC_FORBIDDEN, "暂无操作权限");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean requiresAuth(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (!path.startsWith("/api/")) return false;
        if (path.equals("/api/health") || path.startsWith("/api/auth/")) return false;
        if (HttpMethod.GET.matches(request.getMethod())) return false;
        return true;
    }

    private boolean hasRoleAccess(HttpServletRequest request, String role) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (path.startsWith("/api/influencers/profile") || path.matches("/api/influencers/\\d+/public")) {
            return "INFLUENCER".equals(role);
        }

        if (path.startsWith("/api/products") || path.startsWith("/api/merchant") || path.startsWith("/api/payments") || path.startsWith("/api/unlock")) {
            return "MERCHANT".equals(role);
        }

        return !HttpMethod.POST.matches(method) || "MERCHANT".equals(role) || "INFLUENCER".equals(role);
    }

    private String bearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return "";
        return header.substring("Bearer ".length());
    }

    private void reject(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
