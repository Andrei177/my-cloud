package com.example.mycloud.exceptions;

public class UserByEmailNotFound extends RuntimeException {
    public UserByEmailNotFound(String email) {
        super("Пользователя с " + email + " не существует");
    }
}
