package com.example.mycloud.folders;

import com.example.mycloud.exceptions.*;
import com.example.mycloud.files.File;
import com.example.mycloud.files.FilesRepository;
import com.example.mycloud.folders.dto.CreateFolderRequest;
import com.example.mycloud.folders.dto.CreateFolderResponse;
import com.example.mycloud.folders.dto.FilesAndFoldersDto;
import com.example.mycloud.users.User;
import com.example.mycloud.users.UserRepository;
import com.example.mycloud.utils.FilesUploader;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FoldersService {
    private final FoldersRepository foldersRepository;
    private final UserRepository userRepository;
    private final FilesRepository filesRepository;
    private final FilesUploader filesUploader;

    FoldersService(FoldersRepository foldersRepository, UserRepository userRepository, FilesRepository filesRepository, FilesUploader filesUploader) {
        this.foldersRepository = foldersRepository;
        this.userRepository = userRepository;
        this.filesRepository = filesRepository;
        this.filesUploader = filesUploader;
    }

    public Folder createFolder(CreateFolderRequest createFolderRequest, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFound(userId));
        Optional<Folder> candidate;
        if(createFolderRequest.getParentFolderId() == null){
            candidate = foldersRepository.findUserFolderByNameInRoot(createFolderRequest.getFolderName(), userId);
        }else{
            candidate = foldersRepository.findUserFolderByNameInParentFolder(createFolderRequest.getFolderName(), userId, createFolderRequest.getParentFolderId());
        }
        if (candidate.isPresent()) {
            throw new FolderNameAlreadyExists(candidate.get().getFolderName());
        }
        Folder folder = new Folder();
        folder.setFolderName(createFolderRequest.getFolderName());
        folder.setUser(user);
        if(createFolderRequest.getParentFolderId() != null) {
            Folder parentFolder = foldersRepository.findById(createFolderRequest.getParentFolderId()).orElseThrow(() -> new FolderNotFound(createFolderRequest.getParentFolderId()));
            folder.setParentFolder(parentFolder);
        }

        return foldersRepository.save(folder);
    }
    public File uploadFileToFolder(MultipartFile file, Long folderId, Long userId) {
        Folder folder = foldersRepository.findById(folderId).orElseThrow(() -> new FolderNotFound(folderId));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFound(userId));
        if(!folder.getUser().getUserId().equals(userId)) {
            throw new AccessForbiddenException("Папка с id " + folder.getFolderId() + " не принадлежит пользователю " + user.getUserName());
        }

        return filesUploader.uploadFile(file, folder, user);
    }
    public FilesAndFoldersDto getFilesAndFolders(Long folderId, Boolean includeFiles, Boolean includeFolders, Long userId){
        if(folderId == null){
            List<Folder> rootFolders = new ArrayList<>();
            if(includeFolders){
                rootFolders = foldersRepository.findRootFoldersByUserId(userId);
            }
            List<File> rootFiles = new ArrayList<>();
            if(includeFiles){
                rootFiles = filesRepository.findRootFilesByUserId(userId);
            }

            return new FilesAndFoldersDto(rootFiles, rootFolders);
        }
        Folder folder = foldersRepository.findById(folderId).orElseThrow(() -> new FolderNotFound(folderId));
        if(!folder.getUser().getUserId().equals(userId)){
            throw new AccessForbiddenException("У пользователя с id " + userId + " нет доступа к этой папке");
        }
        List<Folder> folders = new ArrayList<>();
        if(includeFolders){
            folders = foldersRepository.findByParentFolder(folder);
        }
        List<File> files = new ArrayList<>();
        if(includeFiles){
            files = filesRepository.findFilesByFolder(folder);
        }

        return new FilesAndFoldersDto(files, folders);
    }
}
