package com.example.mycloud.folders.dto;

import com.example.mycloud.files.File;
import com.example.mycloud.folders.Folder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FilesAndFoldersDto {
    private List<File> files;
    private List<Folder> folders;
}
