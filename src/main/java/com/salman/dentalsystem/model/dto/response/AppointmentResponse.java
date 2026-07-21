package com.salman.dentalsystem.model.dto.response;

import com.salman.dentalsystem.model.enums.AppointmentStatus;
import com.salman.dentalsystem.model.enums.EntityStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private UUID id;
    private String date;
    private String startTime;
    private String endTime;
    private AppointmentStatus appointmentStatus;
}
