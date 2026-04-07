package com.example.mycloud.folders.dto;

import com.example.mycloud.files.dto.FileResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FilesAndFoldersResponse {
    private List<FolderResponse> folders;
    private List<FileResponse> files;
}
