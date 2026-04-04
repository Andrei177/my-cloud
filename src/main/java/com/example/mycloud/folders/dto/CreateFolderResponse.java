package com.example.mycloud.folders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CreateFolderResponse {
    private Long folderId;
    private String folderName;
    private Long parentFolderId;
    private LocalDateTime createdAt;
}
