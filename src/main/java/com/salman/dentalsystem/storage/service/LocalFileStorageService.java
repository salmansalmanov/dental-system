package com.salman.dentalsystem.storage.service;

import com.salman.dentalsystem.storage.model.StoredFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {
    private final Path rootLocation;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png");

    public LocalFileStorageService(@Value("${spring.file.storage.path}") String storagePath) {
        this.rootLocation = Paths.get(storagePath);
        initStorage();
    }

    private void initStorage() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not initialize storage directory: " + rootLocation, e);
        }
    }

    @Override
    public StoredFile store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        String originalFileName = file.getOriginalFilename();
        String extension = extractExtension(originalFileName).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Unsupported file extension: " + extension);
        }

        String contentType = resolveContentType(extension);
        String storedFileName = UUID.randomUUID() + "." + extension;

        Path destination = rootLocation.resolve(storedFileName).normalize();

        if (!destination.getParent().equals(rootLocation)) {
            throw new SecurityException("Cannot store file outside storage directory");
        }

        try (var inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store file " + originalFileName, e);
        }

        return new StoredFile(
                originalFileName,
                storedFileName,
                destination.toString(),
                contentType,
                file.getSize()
        );
    }

    private String resolveContentType(String extension) {
        return switch (extension) {
            case "png" -> "image/png";
            default -> throw new IllegalArgumentException("Unsupported file extension: " + extension);
        };
    }

    @Override
    public byte[] load(String storedFileName) {
        Path filePath = rootLocation.resolve(storedFileName).normalize();

        if (!filePath.getParent().equals(rootLocation)) {
            throw new SecurityException("Invalid file path");
        }

        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read file " + storedFileName, e);
        }
    }

    @Override
    public void delete(String storedFileName) {
        Path filePath = rootLocation.resolve(storedFileName).normalize();
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to delete file " + storedFileName, e);
        }
    }

    private String extractExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}