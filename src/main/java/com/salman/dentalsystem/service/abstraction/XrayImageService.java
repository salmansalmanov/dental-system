package com.salman.dentalsystem.service.abstraction;

import com.salman.dentalsystem.model.dto.response.XrayImageResponse;
import com.salman.dentalsystem.model.entity.Appointment;
import com.salman.dentalsystem.model.enums.XrayAgent;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface XrayImageService {
    DataResult<XrayImageResponse> uploadXrayImage(XrayAgent agentId, MultipartFile file);

    DataResult<List<XrayImageResponse>> getXrayImagesByAppointment(UUID appointmentId);

    DataResult<XrayImageResponse> getXrayImageById(UUID id);

    Result deleteXrayImageById(UUID id);

    void deleteS3FilesForAppointments(List<Appointment> appointments);
}
