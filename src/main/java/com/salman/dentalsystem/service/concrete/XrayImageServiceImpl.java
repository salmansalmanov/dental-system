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
import com.salman.dentalsystem.result.SuccessDataResult;
import com.salman.dentalsystem.service.abstraction.XrayImageService;
import com.salman.dentalsystem.storage.model.StoredFile;
import com.salman.dentalsystem.storage.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class XrayImageServiceImpl implements XrayImageService {
    private final FileStorageService fileStorageService;
    private final XrayImageRepository xrayImageRepository;
    private final AppointmentRepository appointmentRepository;
    private final XrayImageMapper xrayImageMapper;

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
        StoredFile storedFile = fileStorageService.store(file);
        XrayImage xrayImage = xrayImageMapper.toEntity(storedFile);
        xrayImage.setAppointment(appointment);
        XrayImage savedImage = xrayImageRepository.save(xrayImage);
        XrayImageResponse response = xrayImageMapper.toResponse(savedImage);
        return new SuccessDataResult<>(response, "X-ray image uploaded successfully");
    }
}
