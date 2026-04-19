package com.example.mycloud.files;

import com.example.mycloud.exceptions.ErrorResponse;
import com.example.mycloud.exceptions.FileNotAttachedException;
import com.example.mycloud.files.dto.FileDownloadDto;
import com.example.mycloud.files.dto.FileResponse;
import com.example.mycloud.security.CustomUserDetails;
import com.example.mycloud.utils.OperationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "Получение информации о файле",
            description = "По fileId запрашивается метаинформация о файле",
            extensions = @Extension(properties = {
                    @ExtensionProperty(name = "x-operation-group", value = OperationGroups.READ_FILES_INFO)
            })
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение информации о файле"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет доступа к этому файлу", content = @Content(mediaType = "application/json", schema = @Schema(contentSchema = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Файл не найден", content = @Content(mediaType = "application/json", schema = @Schema(contentSchema = ErrorResponse.class)))
    })
    @GetMapping(value = "/{fileId}/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileResponse> getFileInfo(@PathVariable("fileId") Long fileId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        File fileInfo = filesService.getFileInfo(fileId, userDetails.getUserId());

        FileResponse fileResponse = new FileResponse(fileInfo.getFileId(), fileInfo.getFileName(), fileInfo.getFileSize(), fileInfo.getFileType(), fileInfo.getFolder() != null ? fileInfo.getFolder().getFolderId() : null, userDetails.getUserId(), fileInfo.getCreatedAt(), fileInfo.getUpdatedAt());

        return ResponseEntity.status(HttpStatus.OK).body(fileResponse);
    }

    @Operation(
            summary = "Загрузка файла в корень облачного хранилища",
            description = "Загрузка файла в корень облачного хранилища, а не в какую-то папку",
            extensions = @Extension(properties = {
                    @ExtensionProperty(name = "x-operation-group", value = OperationGroups.CREATE_FILES)
            })
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Успешная загрузка"),
            @ApiResponse(responseCode = "400", description = "Не прикреплён файл для загрузки", content = @Content(mediaType = "application/json", schema = @Schema(contentSchema = ErrorResponse.class))),
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponse> uploadFileToRoot(@RequestParam(value = "file", required = false) MultipartFile file, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if(file == null){
            throw new FileNotAttachedException("Вы не прикрепили файл");
        }
        File uploadedFile = filesService.uploadFileToRoot(file, userDetails.getUserId());

        FileResponse uploadFileResponse = new FileResponse(uploadedFile.getFileId(), uploadedFile.getFileName(), uploadedFile.getFileSize(), uploadedFile.getFileType(), null, uploadedFile.getUser().getUserId(), uploadedFile.getCreatedAt(), uploadedFile.getUpdatedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(uploadFileResponse);
    }

    @Operation(
            summary = "Запрос файла для отображения или для скачивания",
            description = "В зависимости о параметра запроса download результатом запроса будет либо скаычивание файла, либо отображение в браузере",
            extensions = @Extension(properties = {
                    @ExtensionProperty(name = "x-operation-group", value = OperationGroups.DOWNLOAD_FILES)
            })
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение файла для скачивания или для отображения"),
            @ApiResponse(responseCode = "404", description = "Файл для скачивания не найден", content = @Content(mediaType = "application/json", schema = @Schema(contentSchema = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "У пользователя нет доступа к этому файлу", content = @Content(mediaType = "application/json", schema = @Schema(contentSchema = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера при скачивании файла", content = @Content(mediaType = "application/json", schema = @Schema(contentSchema = ErrorResponse.class))),
    })
    @GetMapping(value = "/{fileId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
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

    @Operation(
            summary = "Удаление файла",
            description = "Удаление файла по fileId",
            extensions = @Extension(properties = {
                    @ExtensionProperty(name = "x-operation-group", value = OperationGroups.DELETE_FILES)
            })
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Успешное удаление файла"),
            @ApiResponse(responseCode = "404", description = "Файл для удаления не найден или у пользователя нет доступа к этому файлу", content = @Content(mediaType = "application/json", schema = @Schema(contentSchema = ErrorResponse.class))),
    })
    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable("fileId") Long fileId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        filesService.deleteFile(fileId, userDetails.getUserId());
        return  ResponseEntity.noContent().build();
    }
}
