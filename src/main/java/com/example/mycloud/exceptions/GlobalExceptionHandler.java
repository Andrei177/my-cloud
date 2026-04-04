package com.example.mycloud.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExists.class)
    public ResponseEntity<Map<String, String>> handleException(UserAlreadyExists e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }
    @ExceptionHandler(UserByEmailNotFound.class)
    public ResponseEntity<Map<String, String>> handleException(UserByEmailNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }
    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<Map<String, String>> handleException(UserNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }
    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<Map<String, String>> handleException(WrongPasswordException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
    }
    @ExceptionHandler(FolderNameAlreadyExists.class)
    public ResponseEntity<Map<String, String>> handleException(FolderNameAlreadyExists e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }
    @ExceptionHandler(FolderNotFound.class)
    public ResponseEntity<Map<String, String>> handleException(FolderNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }
    @ExceptionHandler(FailedUploadFileException.class)
    public ResponseEntity<Map<String, String>> handleException(FailedUploadFileException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
    }
}
