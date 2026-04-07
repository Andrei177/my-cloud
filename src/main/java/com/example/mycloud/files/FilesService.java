package com.example.mycloud.files;

import com.example.mycloud.exceptions.*;
import com.example.mycloud.files.dto.FileDownloadDto;
import com.example.mycloud.folders.Folder;
import com.example.mycloud.users.User;
import com.example.mycloud.users.UserRepository;
import com.example.mycloud.utils.FilesUploader;
import jakarta.annotation.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FilesService {
    private final FilesRepository filesRepository;
    private final UserRepository userRepository;
    private final FilesUploader filesUploader;

    public FilesService(FilesRepository filesRepository, UserRepository userRepository, FilesUploader filesUploader) {
        this.filesRepository = filesRepository;
        this.filesUploader = filesUploader;
        this.userRepository = userRepository;
    }

    public File getFileInfo(Long fileId, Long userId) {
        File fileInfo = filesRepository.findById(fileId).orElseThrow(() -> new FileNotFound("Файл с id " + fileId + " не найден"));
        if(!fileInfo.getUser().getUserId().equals(userId)){
            throw new AccessForbiddenException("У пользователя с id " + userId + " нет доступа к этому файлу");
        }
        return fileInfo;
    }

    public File uploadFileToRoot(MultipartFile file, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFound(userId));

        return filesUploader.uploadFile(file, null, user);
    }
    public FileDownloadDto downloadFile(Long fileId, Long userId) {
        File file = filesRepository.findById(fileId).orElseThrow(() -> new FileNotFound("Файл с id " + fileId + " не найден"));

        if(!file.getUser().getUserId().equals(userId)){
            throw new AccessForbiddenException("Пользователь с id " + userId + " не имеет доступа с этому файлу");
        }
        try{
            UrlResource resource = filesUploader.downloadFile(file.getFilePath());

            if (resource.exists() || resource.isReadable()) {
                return new FileDownloadDto(resource, file);
            } else {
                throw new FileNotFound("Файл для скачивания не найден");
            }
        }catch (MalformedURLException e){
            throw new FileDownloadException("Произошла ошибка при скачивании файла");
        }
    }
}
