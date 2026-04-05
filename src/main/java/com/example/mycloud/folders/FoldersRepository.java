package com.example.mycloud.folders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoldersRepository extends JpaRepository<Folder, Long> {
    @Query("SELECT f FROM Folder f WHERE f.folderName = :folderName AND f.user.userId = :userId")
    Optional<Folder> findByFolderNameForUser(
            @Param("folderName") String folderName,
            @Param("userId") Long userId
    );
    @Query("SELECT f FROM Folder f WHERE f.parentFolder IS NULL AND f.user.userId = :userId")
    List<Folder> findRootFoldersByUserId(@Param("userId") Long userId);

    List<Folder> findByParentFolder(Folder parent);
}
