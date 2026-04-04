package com.example.mycloud.exceptions;

public class FolderNotFound extends RuntimeException {
    public FolderNotFound(Long folderId) {
        super("Папку с id " + folderId + " не существует");
    }
}
