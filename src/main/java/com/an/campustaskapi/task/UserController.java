package com.an.campustaskapi.task;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {


    private final UserService userService;


    public UserController(
            UserService userService
    ) {

        this.userService =
                userService;
    }


    @PostMapping
    public UserResponse createUser(
            @Valid
            @RequestBody
            CreateUserRequest request
    ) {

        User user =
                userService.createUser(
                        request.getUsername(),
                        request.getPassword()
                );


        return UserResponse.from(
                user
        );
    }


    @GetMapping("/{id}")
    public UserResponse getUserById(
            @PathVariable Long id
    ) {

        User user =
                userService.getUserById(
                        id
                );


        return UserResponse.from(
                user
        );
    }


    @GetMapping
    public List<UserResponse> getAllUsers() {


        List<User> users =
                userService.getAllUsers();


        List<UserResponse> responses =
                new ArrayList<>();


        for (
                User user : users
        ) {

            responses.add(
                    UserResponse.from(
                            user
                    )
            );
        }


        return responses;
    }
}