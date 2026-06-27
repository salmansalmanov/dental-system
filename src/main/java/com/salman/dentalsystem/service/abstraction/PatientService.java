package com.salman.dentalsystem.service.abstraction;

import com.salman.dentalsystem.model.dto.request.PatientCreateRequest;
import com.salman.dentalsystem.model.dto.request.PatientUpdateRequest;
import com.salman.dentalsystem.model.dto.response.PatientDetailedResponse;
import com.salman.dentalsystem.model.dto.response.PatientResponse;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.PageData;

import java.util.UUID;

public interface PatientService {
    DataResult<PatientDetailedResponse> create(PatientCreateRequest request);

    DataResult<PatientDetailedResponse> getById(UUID id);

    DataResult<PageData<PatientResponse>> getAll(int page, int size);

    DataResult<PatientDetailedResponse> updateById(UUID id, PatientUpdateRequest request);
}
