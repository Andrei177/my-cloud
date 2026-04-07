package com.example.mycloud.exceptions;

public class FolderNotFound extends RuntimeException {
    public FolderNotFound(Long folderId) {
        super("Папка с id " + folderId + " не существует");
    }
}
