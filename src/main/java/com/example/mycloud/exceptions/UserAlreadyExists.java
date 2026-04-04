package com.example.mycloud.exceptions;

public class UserAlreadyExists extends RuntimeException{
    public UserAlreadyExists(String userEmail){
        super("Пользователь с email " + userEmail + " уже существует");
    }
}
