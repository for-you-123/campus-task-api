package com.an.campustaskapi.task;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthService authService;

    public SecurityConfig(AuthService authService) {
        this.authService = authService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        AuthenticationEntryPoint entryPoint = (request, response, exception) ->
                writeJsonError(response, 401, "登录凭证无效或已过期");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/users", "/auth/login")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler((request, response, exception) ->
                                writeJsonError(response, 403, "没有访问权限")))
                .addFilterBefore(
                        new JwtAuthenticationFilter(authService, entryPoint),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // Security Filter 中的异常不经过 MVC 的 GlobalExceptionHandler。
    private static void writeJsonError(
            HttpServletResponse response,
            int status,
            String message
    ) throws IOException {

        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                "{\"status\":" + status
                        + ",\"message\":\"" + message + "\""
                        + ",\"timestamp\":\"" + LocalDateTime.now() + "\"}"
        );
    }
}
