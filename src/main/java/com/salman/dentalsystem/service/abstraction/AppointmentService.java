package com.salman.dentalsystem.service.abstraction;

import com.salman.dentalsystem.model.dto.request.AppointmentCreateRequest;
import com.salman.dentalsystem.model.dto.request.AppointmentUpdateRequest;
import com.salman.dentalsystem.model.dto.response.AppointmentDetailedResponse;
import com.salman.dentalsystem.model.dto.response.AppointmentResponse;
import com.salman.dentalsystem.model.dto.response.AppointmentTodayCountResponse;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.PageData;

import java.util.UUID;

public interface AppointmentService {
    DataResult<AppointmentDetailedResponse> create(UUID patientId, AppointmentCreateRequest request);

    DataResult<AppointmentDetailedResponse> getById(UUID id);

    DataResult<PageData<AppointmentResponse>> getAllByPatientId(UUID patientId, EntityStatus status, int page, int size);

    DataResult<AppointmentDetailedResponse> updateById(UUID id, AppointmentUpdateRequest request);

    DataResult<AppointmentDetailedResponse> cancelById(UUID id);

    DataResult<AppointmentDetailedResponse> activateById(UUID id);

    DataResult<AppointmentTodayCountResponse> getTodayAppointmentCount();
}
