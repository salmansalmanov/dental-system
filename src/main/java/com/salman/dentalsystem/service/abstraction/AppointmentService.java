package com.salman.dentalsystem.service.abstraction;

import com.salman.dentalsystem.model.dto.request.AppointmentCreateRequest;
import com.salman.dentalsystem.model.dto.response.AppointmentDetailedResponse;
import com.salman.dentalsystem.result.DataResult;

import java.util.UUID;

public interface AppointmentService {
    DataResult<AppointmentDetailedResponse> create(AppointmentCreateRequest request);

    DataResult<AppointmentDetailedResponse> getById(UUID id);
}
