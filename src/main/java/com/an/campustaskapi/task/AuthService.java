package com.an.campustaskapi.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // 验证用户名和密码
    public User login(
            String username,
            String rawPassword
    ) {

        String normalizedUsername = username.trim();

        log.info(
                "开始验证用户登录，username={}",
                normalizedUsername
        );

        User user = userRepository
                .findByUsername(normalizedUsername)
                .orElseThrow(
                        () -> new InvalidCredentialsException()
                );

        String passwordHash = user.getPasswordHash();

        if (passwordHash == null
                || !passwordEncoder.matches(
                rawPassword,
                passwordHash
        )) {

            log.warn(
                    "用户登录验证失败，username={}",
                    normalizedUsername
            );

            throw new InvalidCredentialsException();
        }

        log.info(
                "用户登录验证成功，userId={}",
                user.getId()
        );

        return user;
    }

    // 登录成功后生成 JWT
    public String createToken(User user) {

        return jwtService.generateToken(user);
    }

    // 根据已经提取出的 JWT 查询用户
    public User getUserFromToken(String token) {

        Long userId = jwtService.getUserId(token);

        return userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new InvalidTokenException()
                );
    }

    // Day28：统一验证 Authorization 请求头
    public User requireCurrentUser(
            String authorization
    ) {

        if (authorization == null
                || !authorization.startsWith("Bearer ")) {

            throw new InvalidTokenException();
        }

        String token = authorization
                .substring(7)
                .trim();

        if (token.isEmpty()) {
            throw new InvalidTokenException();
        }

        return getUserFromToken(token);
    }
}