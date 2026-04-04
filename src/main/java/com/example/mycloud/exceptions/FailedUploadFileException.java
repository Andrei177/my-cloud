package com.example.mycloud.exceptions;

public class FailedUploadFileException extends RuntimeException {
    public FailedUploadFileException(String fileName) {
        super("Произошла ошибка при загрузке файла " + fileName);
    }
}
