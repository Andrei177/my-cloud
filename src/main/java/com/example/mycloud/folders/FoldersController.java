package com.example.mycloud.folders;

import com.example.mycloud.files.File;
import com.example.mycloud.files.dto.FileResponse;
import com.example.mycloud.folders.dto.*;
import com.example.mycloud.security.CustomUserDetails;
import com.example.mycloud.utils.OperationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/folders")
public class FoldersController {
    private FoldersService foldersService;

    FoldersController(FoldersService foldersService) {
        this.foldersService = foldersService;
    }

    @Operation(
            summary = "Создание папки",
            description = "Пользователь создаёт папку у себя в аккаунте",
            extensions = @Extension(properties = {
                    @ExtensionProperty(name = "x-operation-group", value = OperationGroups.CREATE_FILES)
            })
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Успешное создание папки"),
            @ApiResponse(responseCode = "409", description = "Папка с таким именем в этом окружении уже существует", content = @Content(mediaType = "application/json", schema = @Schema(contentSchema = ErrorResponse.class))),
    })
    @PostMapping(produces =  MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateFolderResponse> createFolder(@Valid @RequestBody CreateFolderRequest createFolderRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Folder createdFolder = foldersService.createFolder(createFolderRequest, userDetails.getUserId());

        CreateFolderResponse createFolderResponse = new CreateFolderResponse(createdFolder.getFolderId(), createdFolder.getFolderName(), createdFolder.getParentFolder() != null ? createdFolder.getParentFolder().getFolderId() : null, createdFolder.getCreatedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(createFolderResponse);
    }

    @Operation(
            summary = "Загрузка файла в папку",
            description = "Загрузка файла в папку с folderId",
            extensions = @Extension(properties = {
                    @ExtensionProperty(name = "x-operation-group", value = OperationGroups.CREATE_FILES)
            })
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Успешное создание папки"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет доступа к этой папке", content = @Content(mediaType = "application/json", schema = @Schema(contentSchema = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Папка не найдена", content = @Content(mediaType = "application/json", schema = @Schema(contentSchema = ErrorResponse.class)))
    })
    @PostMapping(value = "/{folderId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileResponse> uploadFileToFolder(@PathVariable("folderId") Long folderId, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal CustomUserDetails userDetails) {
        File uploadedFile = foldersService.uploadFileToFolder(file, folderId, userDetails.getUserId());

        FileResponse uploadFileResponse = new FileResponse(uploadedFile.getFileId(), uploadedFile.getFileName(), uploadedFile.getFileSize(), uploadedFile.getFileType(), uploadedFile.getFolder().getFolderId(), uploadedFile.getUser().getUserId(), uploadedFile.getCreatedAt(), uploadedFile.getUpdatedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(uploadFileResponse);
    }

    @Operation(
            summary = "Получение файлов и папок пользователя",
            description = "Получение информации о файлах или/и папках пользователя по folderId (может быть null, тогда будут запрашиваться файлы и папки из корня)",
            extensions = @Extension(properties = {
                    @ExtensionProperty(name = "x-operation-group", value = OperationGroups.READ_FILES_INFO)
            })
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение информации о файлах и папках"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет доступа к этой папке", content = @Content(mediaType = "application/json", schema = @Schema(contentSchema = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Папка не найдена", content = @Content(mediaType = "application/json", schema = @Schema(contentSchema = ErrorResponse.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FilesAndFoldersResponse> getFilesAndFolders(@RequestParam(value = "folderId", required = false) Long folderId, @RequestParam(value = "files", defaultValue = "true") Boolean includeFiles, @RequestParam(value = "folders", defaultValue = "true") Boolean includeFolders, @AuthenticationPrincipal CustomUserDetails userDetails) {
        FilesAndFoldersDto filesAndFolders = foldersService.getFilesAndFolders(folderId, includeFiles, includeFolders, userDetails.getUserId());

        List<FileResponse> fileResponseList = new ArrayList<>();
        filesAndFolders.getFiles().forEach(file -> fileResponseList.add(new FileResponse(file.getFileId(), file.getFileName(), file.getFileSize(), file.getFileType(), file.getFolder() == null ? null : file.getFolder().getFolderId(), file.getUser().getUserId(), file.getCreatedAt(), file.getUpdatedAt())));

        List<FolderResponse> folderResponseList = new ArrayList<>();
        filesAndFolders.getFolders().forEach(folder -> folderResponseList.add(new FolderResponse(folder.getFolderId(), folder.getFolderName(), folder.getUser().getUserId(), folder.getParentFolder() != null ? folder.getParentFolder().getFolderId() : null, folder.getCreatedAt(), folder.getUpdatedAt())));
        return ResponseEntity.status(HttpStatus.OK).body(new FilesAndFoldersResponse(folderResponseList, fileResponseList));
    }
}
