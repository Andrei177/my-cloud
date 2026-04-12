package com.example.mycloud.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExists.class)
    public ResponseEntity<Map<String, String>> handleException(UserAlreadyExists e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }
    @ExceptionHandler({UserByEmailNotFound.class, UserByIdNotFound.class, UserNotFound.class})
    public ResponseEntity<Map<String, String>> handleException(UserByIdNotFound e) {
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
    @ExceptionHandler(AccessForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleException(AccessForbiddenException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
    }
    @ExceptionHandler(FileNotFound.class)
    public ResponseEntity<Map<String, String>> handleException(FileNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }
    @ExceptionHandler(FileNotAttachedException.class)
    public ResponseEntity<Map<String, String>> handleException(FileNotAttachedException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    }
    @ExceptionHandler(FileDownloadException.class)
    public ResponseEntity<Map<String, String>> handleException(FileDownloadException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
    }
    @ExceptionHandler(DeleteFileFromDiskException.class)
    public ResponseEntity<Map<String, String>> handleException(DeleteFileFromDiskException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
    }
    @ExceptionHandler(ClientAppNotFound.class)
    public ResponseEntity<Map<String, String>> handleException(ClientAppNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }
    @ExceptionHandler(OAuthException.class)
    public ResponseEntity<Map<String, String>> handleException(OAuthException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();
        String type = ex.getRequiredType().getSimpleName();
        Object value = ex.getValue();
        String message = String.format("Параметр '%s' должен быть типа %s. Получено: '%s'", name, type, value);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", message));
    }
}
