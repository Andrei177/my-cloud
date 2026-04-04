package com.example.mycloud.folders.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateFolderRequest {
    @NotBlank(message = "Имя папки при создании обязательно")
    private String folderName;
    private Long parentFolderId;
}
