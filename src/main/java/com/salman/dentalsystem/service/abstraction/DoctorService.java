package com.salman.dentalsystem.service.abstraction;

import com.salman.dentalsystem.model.dto.request.DoctorCreateRequest;
import com.salman.dentalsystem.model.dto.request.DoctorUpdateRequest;
import com.salman.dentalsystem.model.dto.response.DoctorDetailedResponse;
import com.salman.dentalsystem.model.dto.response.DoctorResponse;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.PageData;

import java.util.UUID;

public interface DoctorService {
    DataResult<DoctorDetailedResponse> create(DoctorCreateRequest request);

    DataResult<DoctorDetailedResponse> getById(UUID id);

    DataResult<PageData<DoctorResponse>> getAll(int page, int size);

    DataResult<DoctorDetailedResponse> updateById(UUID id, DoctorUpdateRequest request);
}
