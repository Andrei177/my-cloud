package com.example.mycloud.utils;

import com.example.mycloud.exceptions.FailedUploadFileException;
import com.example.mycloud.files.File;
import com.example.mycloud.files.FilesRepository;
import com.example.mycloud.folders.Folder;
import com.example.mycloud.folders.FoldersRepository;
import com.example.mycloud.users.User;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class FilesUploader {
    @Value("${storage.root-path}")
    private String rootPath;
    private final FilesRepository filesRepository;

    public FilesUploader(FilesRepository filesRepository, FoldersRepository foldersRepository) {
        this.filesRepository = filesRepository;
    }

    public File uploadFile(MultipartFile file, Folder folder, User user) {
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
    public UrlResource downloadFile(String fileName) throws MalformedURLException {
        Path storageLocation = Paths.get(rootPath).toAbsolutePath().normalize();
        // Формируем полный путь к файлу
        Path filePath = storageLocation.resolve(fileName).normalize();
        return new UrlResource(filePath.toUri());
    }
}
