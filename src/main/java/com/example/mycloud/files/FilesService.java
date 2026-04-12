package com.example.mycloud.files;

import com.example.mycloud.exceptions.*;
import com.example.mycloud.files.dto.FileDownloadDto;
import com.example.mycloud.users.User;
import com.example.mycloud.users.UserRepository;
import com.example.mycloud.utils.FilesManager;
import jakarta.transaction.Transactional;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

@Service
public class FilesService {
    private final FilesRepository filesRepository;
    private final UserRepository userRepository;
    private final FilesManager filesManager;

    public FilesService(FilesRepository filesRepository, UserRepository userRepository, FilesManager filesManager) {
        this.filesRepository = filesRepository;
        this.filesManager = filesManager;
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
        User user = userRepository.findById(userId).orElseThrow(() -> new UserByIdNotFound(userId));

        return filesManager.uploadFile(file, null, user);
    }
    public FileDownloadDto getFile(Long fileId, Long userId) {
        File file = filesRepository.findById(fileId).orElseThrow(() -> new FileNotFound("Файл с id " + fileId + " не найден"));

        if(!file.getUser().getUserId().equals(userId)){
            throw new AccessForbiddenException("Пользователь с id " + userId + " не имеет доступа с этому файлу");
        }
        try{
            UrlResource resource = filesManager.downloadFile(file.getFilePath());

            if (resource.exists() || resource.isReadable()) {
                return new FileDownloadDto(resource, file);
            } else {
                throw new FileNotFound("Файл для скачивания не найден");
            }
        }catch (MalformedURLException e){
            throw new FileDownloadException("Произошла ошибка при скачивании файла");
        }
    }

    @Transactional
    public void deleteFile(Long fileId, Long userId){
        File fileToDelete = filesRepository.checkFileBelongUser(fileId, userId).orElseThrow(() -> new FileNotFound("Файл с id " + fileId + " не существует или у пользователя нет доступа"));

        filesRepository.delete(fileToDelete);

        filesManager.deleteFile(fileToDelete.getFilePath());
    }
}
