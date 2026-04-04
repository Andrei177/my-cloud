package com.example.mycloud.files;

import org.apache.tomcat.jni.FileInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//GET /api/files/{fileId}/download - скачивание файла
//GET /api/files/{fileId}/info - метаинформация о файле (размер, тип, дата создания)
//PUT /api/files/{fileId} - переименование/перемещение файла

@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    @GetMapping("/info/{fileId}")
    public ResponseEntity<?> getFileInfo(@PathVariable("fileId") String fileId) {
        return ResponseEntity.status(200).body("Инфа о файле");
    }
}
