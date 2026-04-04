package com.example.mycloud.folders;

import com.example.mycloud.exceptions.*;
import com.example.mycloud.files.File;
import com.example.mycloud.files.FilesRepository;
import com.example.mycloud.folders.dto.CreateFolderRequest;
import com.example.mycloud.folders.dto.CreateFolderResponse;
import com.example.mycloud.users.User;
import com.example.mycloud.users.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class FoldersService {
    private final FoldersRepository foldersRepository;
    private final UserRepository userRepository;
    private final FilesRepository filesRepository;

    @Value("${storage.root-path}")
    private String rootPath;


    FoldersService(FoldersRepository foldersRepository, UserRepository userRepository, FilesRepository filesRepository) {
        this.foldersRepository = foldersRepository;
        this.userRepository = userRepository;
        this.filesRepository = filesRepository;
    }

    public Folder createFolder(CreateFolderRequest createFolderRequest, Long userId) {
        Optional<Folder> candidate = foldersRepository.findByFolderNameForUser(createFolderRequest.getFolderName(), userId);
        if (candidate.isPresent()) {
            throw new FolderNameAlreadyExists(candidate.get().getFolderName());
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFound(userId));
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

        String originalName = file.getOriginalFilename();
        assert originalName != null;
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String storedName = UUID.randomUUID() + extension;

        Path targetLocation = Paths.get(rootPath).resolve(storedName);

        File fileToUpload = new File();

        try{
            Files.createDirectories(targetLocation.getParent()); // в случае отсутствия каких-то папок создаст их
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            fileToUpload.setFileType(file.getContentType());
            fileToUpload.setFileName(file.getOriginalFilename());
            fileToUpload.setFileSize(file.getSize());
            fileToUpload.setFolder(folder);
            fileToUpload.setUser(user);
            fileToUpload.setFilePath(storedName);
            return filesRepository.save(fileToUpload);
        }catch (IOException e){
            throw new FailedUploadFileException(file.getOriginalFilename());
        }
    }
}
