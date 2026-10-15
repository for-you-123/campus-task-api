package com.an.campustaskapi.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Public registration: role comes exclusively from User constructor (USER).
    public User createUser(String username, String rawPassword) {

        String normalizedUsername = username.trim();

        log.info("开始创建用户，username={}", normalizedUsername);

        if (userRepository.existsByUsername(normalizedUsername)) {
            log.warn("创建用户失败，用户名已存在，username={}", normalizedUsername);
            throw new DuplicateUsernameException(normalizedUsername);
        }

        String passwordHash = passwordEncoder.encode(rawPassword);

        User user = new User(null, normalizedUsername, passwordHash);
        User savedUser = userRepository.save(user);

        log.info("用户创建成功，id={}, username={}",
                savedUser.getId(), savedUser.getUsername());

        return savedUser;
    }

    // Day30: other users' profiles are visible only to ADMIN.
    @PreAuthorize("hasRole('ADMIN')")
    public User getUserById(Long id) {
        log.debug("根据id查询用户，id={}", id);

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    // Day30: listing every user requires ADMIN.
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        log.debug("查询全部用户");
        return userRepository.findAll();
    }
}
