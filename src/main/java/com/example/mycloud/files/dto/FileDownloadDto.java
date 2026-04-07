package com.example.mycloud.files.dto;

import com.example.mycloud.files.File;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.io.UrlResource;

@Data
@AllArgsConstructor
public class FileDownloadDto {
    private UrlResource resource;
    private File file;
}
