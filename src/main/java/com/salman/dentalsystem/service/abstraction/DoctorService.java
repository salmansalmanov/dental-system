package com.salman.dentalsystem.service.abstraction;

import com.salman.dentalsystem.model.dto.request.DoctorCreateRequest;
import com.salman.dentalsystem.model.dto.response.DoctorDetailedResponse;
import com.salman.dentalsystem.result.DataResult;

import java.util.UUID;

public interface DoctorService {
    DataResult<DoctorDetailedResponse> create(DoctorCreateRequest request);

    DataResult<DoctorDetailedResponse> getById(UUID id);
}
