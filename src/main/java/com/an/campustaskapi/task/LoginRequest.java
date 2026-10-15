package com.an.campustaskapi.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class LoginRequest {


    @NotBlank(
            message = "用户名不能为空"
    )
    @Size(
            max = 50,
            message = "用户名不能超过50个字符"
    )
    private String username;


    @NotBlank(
            message = "密码不能为空"
    )
    @Size(
            max = 64,
            message = "密码不能超过64个字符"
    )
    private String password;


    public String getUsername() {

        return username;
    }


    public void setUsername(
            String username
    ) {

        this.username = username;
    }


    public String getPassword() {

        return password;
    }


    public void setPassword(
            String password
    ) {

        this.password = password;
    }
}