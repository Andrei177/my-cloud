package com.example.mycloud.folders;

import com.example.mycloud.files.File;
import com.example.mycloud.folders.dto.CreateFolderRequest;
import com.example.mycloud.folders.dto.CreateFolderResponse;
import com.example.mycloud.folders.dto.UploadFileResponse;
import com.example.mycloud.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/folders")
public class FoldersController {
    private FoldersService foldersService;

    FoldersController(FoldersService foldersService) {
        this.foldersService = foldersService;
    }

    @PostMapping("/")
    public ResponseEntity<CreateFolderResponse> createFolder(@RequestBody CreateFolderRequest createFolderRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Folder createdFolder = foldersService.createFolder(createFolderRequest, userDetails.getUserId());

        CreateFolderResponse createFolderResponse = new CreateFolderResponse(createdFolder.getFolderId(), createdFolder.getFolderName(), createdFolder.getParentFolder() != null ? createdFolder.getParentFolder().getFolderId() : null, createdFolder.getCreatedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(createFolderResponse);
    }

    @PostMapping(value = "/{folderId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadFileResponse> uploadFileToFolder(@PathVariable("folderId") Long folderId, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal CustomUserDetails userDetails) {
        File uploadedFile = foldersService.uploadFileToFolder(file, folderId, userDetails.getUserId());

        UploadFileResponse uploadFileResponse = new UploadFileResponse(uploadedFile.getFileId(), uploadedFile.getFileName(), uploadedFile.getFileSize(), uploadedFile.getFileType(), uploadedFile.getFolder().getFolderId(), uploadedFile.getUser().getUserId(), uploadedFile.getCreatedAt(), uploadedFile.getUpdatedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(uploadFileResponse);
    }
}
