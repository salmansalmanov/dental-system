package com.salman.dentalsystem.storage.service;

import com.salman.dentalsystem.storage.model.StoredFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3FileStorageService {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${spring.aws.s3.bucket}")
    private String bucket;

    public StoredFile uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        try {
            String originalFileName = file.getOriginalFilename();
            String extension = validateAndExtractPngExtension(originalFileName);

            String uuid = UUID.randomUUID().toString();
            String storedFileName = uuid + "." + extension;
            String thumbnailFileName = uuid + "_thumb." + extension;

            byte[] originalBytes = file.getBytes();
            putObjectToS3(storedFileName, originalBytes, file.getContentType());

            byte[] thumbnailBytes = createThumbnail(originalBytes, 200);
            putObjectToS3(thumbnailFileName, thumbnailBytes, "image/png");

            return new StoredFile(
                    storedFileName,
                    thumbnailFileName,
                    originalFileName,
                    storedFileName,
                    file.getContentType(),
                    file.getSize()
            );
        } catch (IOException e) {
            throw new RuntimeException("Error uploading file to S3: " + e.getMessage());
        }
    }

    public String generatePresignedUrl(String storedFileName, long durationMinutes) {
        if (storedFileName == null || storedFileName.isBlank()) {
            return null;
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(storedFileName)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(durationMinutes))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    public void deleteFile(String storedFileName, String thumbnailFileName) {
        if (storedFileName != null && !storedFileName.isBlank()) {
            deleteSingleFileFromS3(storedFileName);
        }
        if (thumbnailFileName != null && !thumbnailFileName.isBlank()) {
            deleteSingleFileFromS3(thumbnailFileName);
        }
    }

    private String validateAndExtractPngExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("File does not exist or has no extension.");
        }
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (!"png".equalsIgnoreCase(extension)) {
            throw new IllegalArgumentException("Invalid file type. Only PNG files are allowed.");
        }
        return extension;
    }

    private void putObjectToS3(String key, byte[] data, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));
    }

    private byte[] createThumbnail(byte[] originalImageBytes, int targetWidth) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(originalImageBytes);
        BufferedImage originalImage = ImageIO.read(inputStream);

        if (originalImage == null) {
            throw new IllegalArgumentException("Invalid image file.");
        }

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int targetHeight = (int) ((double) originalHeight / originalWidth * targetWidth);

        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "png", outputStream);
        return outputStream.toByteArray();
    }

    private void deleteSingleFileFromS3(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }
}
