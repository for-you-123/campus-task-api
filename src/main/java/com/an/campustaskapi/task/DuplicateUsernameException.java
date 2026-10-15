package com.an.campustaskapi.task;


public class DuplicateUsernameException
        extends RuntimeException {


    public DuplicateUsernameException(
            String username
    ) {

        super(
                "用户名已存在，username="
                        + username
        );
    }
}