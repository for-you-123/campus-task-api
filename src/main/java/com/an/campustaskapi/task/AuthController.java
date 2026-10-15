package com.an.campustaskapi.task;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 保留原有的 BCrypt 登录与 JWT 颁发逻辑。
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        User user = authService.login(
                request.getUsername(),
                request.getPassword()
        );
        String token = authService.createToken(user);
        return new LoginResponse(token, UserResponse.from(user));
    }

    // 获取由 Filter 放进 SecurityContext 的用户身份。
    @GetMapping("/me")
    public UserResponse getCurrentUser(
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        return new UserResponse(currentUser.id(), currentUser.username());
    }
}
