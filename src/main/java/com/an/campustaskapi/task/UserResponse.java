package com.an.campustaskapi.task;


public class UserResponse {


    private Long id;


    private String username;


    public UserResponse(
            Long id,
            String username
    ) {

        this.id = id;
        this.username = username;
    }


    public static UserResponse from(
            User user
    ) {

        return new UserResponse(
                user.getId(),
                user.getUsername()
        );
    }


    public Long getId() {

        return id;
    }


    public String getUsername() {

        return username;
    }
}