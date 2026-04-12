package com.example.mycloud.exceptions;

public class UserByIdNotFound extends RuntimeException {
    public UserByIdNotFound(Long userId) {
        super("Пользователь с id " + userId + "не найден");
    }
}
