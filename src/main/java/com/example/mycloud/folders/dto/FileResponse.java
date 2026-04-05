package com.example.mycloud.folders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FileResponse {
    private Long fileId;

    private String fileName;

    private Long fileSize;

    private String fileType;

    private Long folderId;

    private Long userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
