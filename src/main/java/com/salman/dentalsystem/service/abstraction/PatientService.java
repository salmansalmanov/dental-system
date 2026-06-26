package com.salman.dentalsystem.service.abstraction;

import com.salman.dentalsystem.model.dto.request.PatientCreateRequest;
import com.salman.dentalsystem.model.dto.response.PatientDetailedResponse;
import com.salman.dentalsystem.result.DataResult;

import java.util.UUID;

public interface PatientService {
    DataResult<PatientDetailedResponse> create(PatientCreateRequest request);

    DataResult<PatientDetailedResponse> getById(UUID id);
}
