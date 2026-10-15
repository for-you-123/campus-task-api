package com.an.campustaskapi.task;


public class InvalidTokenException
        extends RuntimeException {


    public InvalidTokenException() {

        super(
                "登录凭证无效或已过期"
        );
    }
}