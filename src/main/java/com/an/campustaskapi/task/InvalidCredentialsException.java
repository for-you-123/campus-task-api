package com.an.campustaskapi.task;


public class InvalidCredentialsException
        extends RuntimeException {


    public InvalidCredentialsException() {

        super(
                "用户名或密码错误"
        );
    }
}