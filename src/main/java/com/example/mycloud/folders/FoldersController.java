package com.example.mycloud.folders;

import com.example.mycloud.files.File;
import com.example.mycloud.files.dto.FileResponse;
import com.example.mycloud.folders.dto.*;
import com.example.mycloud.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping()
    public ResponseEntity<CreateFolderResponse> createFolder(@Valid @RequestBody CreateFolderRequest createFolderRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Folder createdFolder = foldersService.createFolder(createFolderRequest, userDetails.getUserId());

        CreateFolderResponse createFolderResponse = new CreateFolderResponse(createdFolder.getFolderId(), createdFolder.getFolderName(), createdFolder.getParentFolder() != null ? createdFolder.getParentFolder().getFolderId() : null, createdFolder.getCreatedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(createFolderResponse);
    }

    @PostMapping(value = "/{folderId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponse> uploadFileToFolder(@PathVariable("folderId") Long folderId, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal CustomUserDetails userDetails) {
        File uploadedFile = foldersService.uploadFileToFolder(file, folderId, userDetails.getUserId());

        FileResponse uploadFileResponse = new FileResponse(uploadedFile.getFileId(), uploadedFile.getFileName(), uploadedFile.getFileSize(), uploadedFile.getFileType(), uploadedFile.getFolder().getFolderId(), uploadedFile.getUser().getUserId(), uploadedFile.getCreatedAt(), uploadedFile.getUpdatedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(uploadFileResponse);
    }

    @GetMapping()
    public ResponseEntity<FilesAndFoldersResponse> getFilesAndFolders(@RequestParam(value = "folderId", required = false) Long folderId, @RequestParam(value = "files", defaultValue = "true") Boolean includeFiles, @RequestParam(value = "folders", defaultValue = "true") Boolean includeFolders, @AuthenticationPrincipal CustomUserDetails userDetails) {
        FilesAndFoldersDto filesAndFolders = foldersService.getFilesAndFolders(folderId, includeFiles, includeFolders, userDetails.getUserId());

        List<FileResponse> fileResponseList = new ArrayList<>();
        filesAndFolders.getFiles().forEach(file -> fileResponseList.add(new FileResponse(file.getFileId(), file.getFileName(), file.getFileSize(), file.getFileType(), file.getFolder() == null ? null : file.getFolder().getFolderId(), file.getUser().getUserId(), file.getCreatedAt(), file.getUpdatedAt())));

        List<FolderResponse> folderResponseList = new ArrayList<>();
        filesAndFolders.getFolders().forEach(folder -> folderResponseList.add(new FolderResponse(folder.getFolderId(), folder.getFolderName(), folder.getUser().getUserId(), folder.getParentFolder() != null ? folder.getParentFolder().getFolderId() : null, folder.getCreatedAt(), folder.getUpdatedAt())));
        return ResponseEntity.status(HttpStatus.OK).body(new FilesAndFoldersResponse(folderResponseList, fileResponseList));
    }
}
