package com.example.mycloud.files;

import com.example.mycloud.folders.Folder;
import com.example.mycloud.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FilesRepository extends JpaRepository<File, Long> {
    @Query("SELECT f FROM File f WHERE f.folder.folderId = :folderId")
    List<File> findAllFilesByFolderId(@Param("folderId") Long folderId);

    @Query("SELECT f FROM File f WHERE f.folder IS NULL AND f.user.userId = :userId")
    List<File> findRootFilesByUserId(@Param("userId") Long userId);

    List<File> findFilesByFolder(Folder folder);

    @Query("SELECT f from File f WHERE f.fileId = :fileId AND f.user.userId = :userId")
    Optional<File> checkFileBelongUser(@Param("fileId") Long fileId, @Param("userId") Long userId);
}
