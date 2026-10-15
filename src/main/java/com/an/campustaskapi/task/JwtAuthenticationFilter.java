package com.an.campustaskapi.task;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public JwtAuthenticationFilter(
            AuthService authService,
            AuthenticationEntryPoint authenticationEntryPoint
    ) {
        this.authService = authService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    // Register and login do not need an existing access token.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        boolean isPost = HttpMethod.POST.matches(request.getMethod());
        String path = request.getServletPath();

        return isPost && (
                "/auth/login".equals(path)
                || "/users".equals(path)
        );
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization == null || authorization.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!authorization.startsWith("Bearer ")) {
            reject(request, response, null);
            return;
        }

        String jwt = authorization.substring(7).trim();
        if (jwt.isEmpty()) {
            reject(request, response, null);
            return;
        }

        User user;
        try {
            // Reuse signature and expiration checks from Day27/Day28.
            user = authService.getUserFromToken(jwt);
        } catch (InvalidTokenException ex) {
            reject(request, response, ex);
            return;
        }

        // No role means the database/account data is not yet in the expected state.
        if (user.getRole() == null) {
            reject(request, response, new InvalidTokenException());
            return;
        }

        AuthenticatedUser principal = new AuthenticatedUser(
                user.getId(),
                user.getUsername()
        );

        // Day30: ADMIN => ROLE_ADMIN, USER => ROLE_USER.
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                "ROLE_" + user.getRole().name()
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(authority)
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Keep outside token validation try/catch: downstream business errors aren't JWT errors.
        filterChain.doFilter(request, response);
    }

    private void reject(
            HttpServletRequest request,
            HttpServletResponse response,
            InvalidTokenException cause
    ) throws ServletException, IOException {

        SecurityContextHolder.clearContext();
        authenticationEntryPoint.commence(
                request,
                response,
                new BadCredentialsException("无效的 JWT 登录凭证", cause)
        );
    }
}
