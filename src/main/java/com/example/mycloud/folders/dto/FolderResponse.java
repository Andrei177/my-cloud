package com.example.mycloud.folders.dto;

import com.example.mycloud.folders.Folder;
import com.example.mycloud.users.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FolderResponse {
    private Long folderId;

    private String folderName;

    private Long userId;

    private Long parentFolderId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
