package com.example.mycloud.exceptions;

public class UserNotFound extends RuntimeException {
    public UserNotFound(Long userId) {
        super("Пользователь с id " + userId + "не найден");
    }
}
