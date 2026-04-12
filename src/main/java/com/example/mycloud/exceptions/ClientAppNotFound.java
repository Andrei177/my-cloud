package com.example.mycloud.exceptions;

public class ClientAppNotFound extends RuntimeException{
    public ClientAppNotFound(String message) {
        super(message);
    }
}
