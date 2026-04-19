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
    public ResponseEntity<ErrorResponse> handleException(UserAlreadyExists e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
    }
    @ExceptionHandler({UserByEmailNotFound.class, UserByIdNotFound.class, UserNotFound.class})
    public ResponseEntity<ErrorResponse> handleException(UserByIdNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }
    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ErrorResponse> handleException(WrongPasswordException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
    }
    @ExceptionHandler(FolderNameAlreadyExists.class)
    public ResponseEntity<ErrorResponse> handleException(FolderNameAlreadyExists e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
    }
    @ExceptionHandler(FolderNotFound.class)
    public ResponseEntity<ErrorResponse> handleException(FolderNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }
    @ExceptionHandler(FailedUploadFileException.class)
    public ResponseEntity<ErrorResponse> handleException(FailedUploadFileException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
    }
    @ExceptionHandler(AccessForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleException(AccessForbiddenException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
    }
    @ExceptionHandler(FileNotFound.class)
    public ResponseEntity<ErrorResponse> handleException(FileNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }
    @ExceptionHandler(FileNotAttachedException.class)
    public ResponseEntity<ErrorResponse> handleException(FileNotAttachedException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
    }
    @ExceptionHandler(FileDownloadException.class)
    public ResponseEntity<ErrorResponse> handleException(FileDownloadException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
    }
    @ExceptionHandler(DeleteFileFromDiskException.class)
    public ResponseEntity<ErrorResponse> handleException(DeleteFileFromDiskException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
    }
    @ExceptionHandler(ClientAppNotFound.class)
    public ResponseEntity<ErrorResponse> handleException(ClientAppNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }
    @ExceptionHandler(OAuthException.class)
    public ResponseEntity<ErrorResponse> handleException(OAuthException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
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
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();
        String type = ex.getRequiredType().getSimpleName();
        Object value = ex.getValue();
        String message = String.format("Параметр '%s' должен быть типа %s. Получено: '%s'", name, type, value);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(message));
    }
}
