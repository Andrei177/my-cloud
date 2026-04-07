package com.example.mycloud.exceptions;

public class FileNotAttachedException extends RuntimeException {
    public FileNotAttachedException(String message) {
        super(message);
    }
}
