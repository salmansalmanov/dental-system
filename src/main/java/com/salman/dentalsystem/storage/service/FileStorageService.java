package com.salman.dentalsystem.storage.service;

import com.salman.dentalsystem.storage.model.StoredFile;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    StoredFile store(MultipartFile file);

    byte[] load(String storedFileName);

    void delete(String storedFileName);
}
