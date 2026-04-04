package com.example.mycloud.exceptions;

public class FolderNameAlreadyExists extends RuntimeException {
    public FolderNameAlreadyExists(String folderName) {
        super("Папка с именем " +  folderName + " уже существует в данном окружении");
    }
}
