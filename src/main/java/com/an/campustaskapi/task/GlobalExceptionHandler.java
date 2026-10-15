package com.an.campustaskapi.task;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(
            TaskNotFoundException.class
    )
    @ResponseStatus(
            HttpStatus.NOT_FOUND
    )
    public Map<String, Object> handleTaskNotFound(
            TaskNotFoundException e
    ) {

        return Map.of(
                "status", 404,
                "message", e.getMessage(),
                "timestamp",
                LocalDateTime.now().toString()
        );
    }


    @ExceptionHandler(
            UserNotFoundException.class
    )
    @ResponseStatus(
            HttpStatus.NOT_FOUND
    )
    public Map<String, Object> handleUserNotFound(
            UserNotFoundException e
    ) {

        return Map.of(
                "status", 404,
                "message", e.getMessage(),
                "timestamp",
                LocalDateTime.now().toString()
        );
    }


    @ExceptionHandler(
            DuplicateUsernameException.class
    )
    @ResponseStatus(
            HttpStatus.CONFLICT
    )
    public Map<String, Object> handleDuplicateUsername(
            DuplicateUsernameException e
    ) {

        return Map.of(
                "status", 409,
                "message", e.getMessage(),
                "timestamp",
                LocalDateTime.now().toString()
        );
    }


    @ExceptionHandler(
            InvalidCredentialsException.class
    )
    @ResponseStatus(
            HttpStatus.UNAUTHORIZED
    )
    public Map<String, Object> handleInvalidCredentials(
            InvalidCredentialsException e
    ) {

        return Map.of(
                "status", 401,
                "message", e.getMessage(),
                "timestamp",
                LocalDateTime.now().toString()
        );
    }


    @ExceptionHandler(
            InvalidTokenException.class
    )
    @ResponseStatus(
            HttpStatus.UNAUTHORIZED
    )
    public Map<String, Object> handleInvalidToken(
            InvalidTokenException e
    ) {

        return Map.of(
                "status", 401,
                "message", e.getMessage(),
                "timestamp",
                LocalDateTime.now().toString()
        );
    }


    @ExceptionHandler(
            IllegalArgumentException.class
    )
    @ResponseStatus(
            HttpStatus.BAD_REQUEST
    )
    public Map<String, Object> handleIllegalArgument(
            IllegalArgumentException e
    ) {

        return Map.of(
                "status", 400,
                "message", e.getMessage(),
                "timestamp",
                LocalDateTime.now().toString()
        );
    }


    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    @ResponseStatus(
            HttpStatus.BAD_REQUEST
    )
    public Map<String, Object> handleValidationException(
            MethodArgumentNotValidException e
    ) {

        String message =
                e.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .findFirst()
                        .map(
                                error ->
                                        error.getField()
                                                + ": "
                                                + error.getDefaultMessage()
                        )
                        .orElse(
                                "请求参数不合法"
                        );


        return Map.of(
                "status", 400,
                "message", message,
                "timestamp",
                LocalDateTime.now().toString()
        );
    }


    @ExceptionHandler(
            HttpMessageNotReadableException.class
    )
    @ResponseStatus(
            HttpStatus.BAD_REQUEST
    )
    public Map<String, Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e
    ) {

        return Map.of(
                "status", 400,
                "message",
                "请求体JSON格式错误或字段类型不匹配",
                "timestamp",
                LocalDateTime.now().toString()
        );
    }


    @ExceptionHandler(
            MethodArgumentTypeMismatchException.class
    )
    @ResponseStatus(
            HttpStatus.BAD_REQUEST
    )
    public Map<String, Object> handleTypeMismatch(
            MethodArgumentTypeMismatchException e
    ) {

        String parameterName =
                e.getName();


        String message;


        if (
                "page".equals(
                        parameterName
                )
        ) {

            message =
                    "page 参数必须是整数";

        } else if (
                "size".equals(
                        parameterName
                )
        ) {

            message =
                    "size 参数必须是整数";

        } else if (
                "status".equals(
                        parameterName
                )
        ) {

            message =
                    "status 参数不合法，可选值：TODO、DOING、DONE";

        } else {

            message =
                    parameterName
                            + " 参数类型不正确";
        }


        return Map.of(
                "status", 400,
                "message", message,
                "timestamp",
                LocalDateTime.now().toString()
        );
    }
}