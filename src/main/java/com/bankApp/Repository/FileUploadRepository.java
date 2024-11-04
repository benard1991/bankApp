package com.bankApp.Repository;

import com.bankApp.model.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {
}
