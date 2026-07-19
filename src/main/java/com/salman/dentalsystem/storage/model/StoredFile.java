package com.salman.dentalsystem.storage.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoredFile {
    private String storedFileName;
    private String originalFileName;
    private String filePath;
    private String contentType;
    private long fileSize;
}
