package com.salman.dentalsystem.service.abstraction;

import com.salman.dentalsystem.model.dto.request.PatientCreateRequest;
import com.salman.dentalsystem.model.dto.request.PatientUpdateRequest;
import com.salman.dentalsystem.model.dto.response.PatientCountResponse;
import com.salman.dentalsystem.model.dto.response.PatientDetailedResponse;
import com.salman.dentalsystem.model.dto.response.PatientResponse;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.PageData;
import com.salman.dentalsystem.result.Result;

import java.util.UUID;

public interface PatientService {
    DataResult<PatientDetailedResponse> create(PatientCreateRequest request);

    DataResult<PatientDetailedResponse> getById(UUID id);

    DataResult<PageData<PatientResponse>> getAll(String search, EntityStatus status, int page, int size);

    DataResult<PatientDetailedResponse> updateById(UUID id, PatientUpdateRequest request);

    DataResult<PatientDetailedResponse> deactivateById(UUID id);

    DataResult<PatientDetailedResponse> activateById(UUID id);

    DataResult<PatientCountResponse> getAllPatientsCount();

    Result deleteById(UUID id);

    Result deleteAllTrashed();
}
