package com.salman.dentalsystem.service.concrete;

import com.salman.dentalsystem.exception.custom.NotFoundException;
import com.salman.dentalsystem.mapper.XrayImageMapper;
import com.salman.dentalsystem.model.dto.response.XrayImageResponse;
import com.salman.dentalsystem.model.entity.Appointment;
import com.salman.dentalsystem.model.entity.XrayImage;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.model.enums.ErrorCode;
import com.salman.dentalsystem.model.enums.XrayAgent;
import com.salman.dentalsystem.repository.AppointmentRepository;
import com.salman.dentalsystem.repository.XrayImageRepository;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.Result;
import com.salman.dentalsystem.result.SuccessDataResult;
import com.salman.dentalsystem.result.SuccessResult;
import com.salman.dentalsystem.service.abstraction.XrayImageService;
import com.salman.dentalsystem.storage.model.StoredFile;
import com.salman.dentalsystem.storage.service.S3FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class XrayImageServiceImpl implements XrayImageService {
    private final XrayImageRepository xrayImageRepository;
    private final AppointmentRepository appointmentRepository;
    private final XrayImageMapper xrayImageMapper;
    private final S3FileStorageService s3FileStorageService;

    @Override
    @Transactional
    public DataResult<XrayImageResponse> uploadXrayImage(XrayAgent agentId, MultipartFile file) {
        Appointment appointment = appointmentRepository.findCurrentAppointment(
                        agentId,
                        LocalDate.now(),
                        LocalTime.now(),
                        EntityStatus.ACTIVE
                )
                .orElseThrow(() -> new NotFoundException("Appointment not found", ErrorCode.APPOINTMENT_NOT_FOUND));

        StoredFile storedFile = s3FileStorageService.uploadFile(file);
        XrayImage xrayImage = xrayImageMapper.toEntity(storedFile);
        xrayImage.setAppointment(appointment);
        XrayImage savedImage = xrayImageRepository.save(xrayImage);
        XrayImageResponse response = xrayImageMapper.toResponse(savedImage);
        response.setImageUrl(s3FileStorageService.generatePresignedUrl(savedImage.getStoredFileName(), 15));
        response.setThumbnailUrl(s3FileStorageService.generatePresignedUrl(savedImage.getThumbnailFileName(), 60));
        return new SuccessDataResult<>(response, "X-ray image uploaded successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public DataResult<List<XrayImageResponse>> getXrayImagesByAppointment(UUID appointmentId) {
        List<XrayImage> images = xrayImageRepository.findAllByAppointmentId(appointmentId);
        List<XrayImageResponse> responseList = images.stream()
                .map(image -> {
                    XrayImageResponse response = xrayImageMapper.toResponse(image);
                    response.setImageUrl(s3FileStorageService.generatePresignedUrl(image.getStoredFileName(), 15));
                    response.setThumbnailUrl(s3FileStorageService.generatePresignedUrl(image.getThumbnailFileName(), 60));
                    return response;
                })
                .toList();
        return new SuccessDataResult<>(responseList, "X-ray images retrieved successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public DataResult<XrayImageResponse> getXrayImageById(UUID id) {
        XrayImage xrayImage = xrayImageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("X-ray image not found", ErrorCode.IMAGE_NOT_FOUND));
        XrayImageResponse response = xrayImageMapper.toResponse(xrayImage);
        response.setImageUrl(s3FileStorageService.generatePresignedUrl(xrayImage.getStoredFileName(), 15));
        response.setThumbnailUrl(s3FileStorageService.generatePresignedUrl(xrayImage.getThumbnailFileName(), 60));
        return new SuccessDataResult<>(response, "X-ray image retrieved successfully");
    }

    @Override
    @Transactional
    public Result deleteXrayImageById(UUID id) {
        XrayImage xrayImage = xrayImageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("X-ray image not found", ErrorCode.IMAGE_NOT_FOUND));
        s3FileStorageService.deleteFile(xrayImage.getStoredFileName(), xrayImage.getThumbnailFileName());
        xrayImageRepository.delete(xrayImage);

        return new SuccessResult("X-ray image deleted successfully");
    }

    @Override
    public void deleteS3FilesForAppointments(List<Appointment> appointments) {
        for (Appointment appointment : appointments) {
            if (appointment.getXrayImages() != null && !appointment.getXrayImages().isEmpty()) {
                for (XrayImage image : appointment.getXrayImages()) {
                    try {
                        s3FileStorageService.deleteFile(image.getStoredFileName(), image.getThumbnailFileName());
                    } catch (Exception e) {
                        System.err.println("Error occurred while deleting file from S3: " + e.getMessage());
                    }
                }
            }
        }
    }
}
