package com.example.mycloud.files;

import com.example.mycloud.exceptions.FileNotAttachedException;
import com.example.mycloud.files.dto.FileDownloadDto;
import com.example.mycloud.files.dto.FileResponse;
import com.example.mycloud.security.CustomUserDetails;
import jakarta.annotation.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
//GET /api/files/{fileId}/download - скачивание файла
//GET /api/files/{fileId}/info - метаинформация о файле (размер, тип, дата создания)
//PUT /api/files/{fileId} - переименование/перемещение файла

@RestController
@RequestMapping("/api/v1/files")
public class FilesController {
    private FilesService filesService;

    public FilesController(FilesService filesService) {
        this.filesService = filesService;
    }

    @GetMapping("/info/{fileId}")
    public ResponseEntity<FileResponse> getFileInfo(@PathVariable("fileId") Long fileId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        File fileInfo = filesService.getFileInfo(fileId, userDetails.getUserId());

        FileResponse fileResponse = new FileResponse(fileInfo.getFileId(), fileInfo.getFileName(), fileInfo.getFileSize(), fileInfo.getFileType(), fileInfo.getFolder() != null ? fileInfo.getFolder().getFolderId() : null, userDetails.getUserId(), fileInfo.getCreatedAt(), fileInfo.getUpdatedAt());

        return ResponseEntity.status(HttpStatus.OK).body(fileResponse);
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponse> uploadFileToRoot(@RequestParam(value = "file", required = false) MultipartFile file, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if(file == null){
            throw new FileNotAttachedException("Вы не прикрепили файл");
        }
        File uploadedFile = filesService.uploadFileToRoot(file, userDetails.getUserId());

        FileResponse uploadFileResponse = new FileResponse(uploadedFile.getFileId(), uploadedFile.getFileName(), uploadedFile.getFileSize(), uploadedFile.getFileType(), null, uploadedFile.getUser().getUserId(), uploadedFile.getCreatedAt(), uploadedFile.getUpdatedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(uploadFileResponse);
    }
    @GetMapping("/{fileId}")
    public ResponseEntity<UrlResource> getFile(@PathVariable("fileId") Long fileId, @RequestParam(value = "download", defaultValue = "false") Boolean isDownload, @AuthenticationPrincipal CustomUserDetails userDetails) {
        FileDownloadDto fileDownloadData = filesService.getFile(fileId, userDetails.getUserId());
        MediaType mediaType = (fileDownloadData.getFile().getFileType() != null) ? MediaType.parseMediaType(fileDownloadData.getFile().getFileType()) : MediaType.APPLICATION_OCTET_STREAM;
        String contentDispositionHeader = "inline; filename=\"" + fileDownloadData.getFile().getFileName() + "\""; // с attachment файл будет сразу скачиваться
        if(isDownload != null && isDownload){
            contentDispositionHeader = "attachment; filename=\"" + fileDownloadData.getFile().getFileName() + "\"";
        }
        return ResponseEntity.ok()
                .contentType(mediaType)
                // attachment заставляет браузер скачивать файл
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionHeader)
                .body(fileDownloadData.getResource());
    }
    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable("fileId") Long fileId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        filesService.deleteFile(fileId, userDetails.getUserId());
        return  ResponseEntity.noContent().build();
    }
}
