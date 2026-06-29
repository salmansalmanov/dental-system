package com.salman.dentalsystem.service.abstraction;

import com.salman.dentalsystem.model.dto.request.AppointmentCreateRequest;
import com.salman.dentalsystem.model.dto.response.AppointmentDetailedResponse;
import com.salman.dentalsystem.model.dto.response.AppointmentResponse;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.PageData;

import java.util.UUID;

public interface AppointmentService {
    DataResult<AppointmentDetailedResponse> createAppointment(AppointmentCreateRequest request);

    DataResult<PageData<AppointmentResponse>> getAll(int page, int size);

    DataResult<AppointmentDetailedResponse> getById(UUID id);
}
