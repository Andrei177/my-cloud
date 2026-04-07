package com.example.mycloud.exceptions;

public class FileNotFound extends RuntimeException {
    public FileNotFound(String message) {
        super(message);
    }
}
