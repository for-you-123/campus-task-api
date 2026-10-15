package com.an.campustaskapi.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class CreateUserRequest {


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
            min = 8,
            max = 64,
            message = "密码长度必须在8到64个字符之间"
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