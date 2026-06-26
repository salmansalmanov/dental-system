package com.salman.dentalsystem.service.concrete;

import com.salman.dentalsystem.exception.custom.NotFoundException;
import com.salman.dentalsystem.mapper.PatientMapper;
import com.salman.dentalsystem.model.dto.request.PatientCreateRequest;
import com.salman.dentalsystem.model.dto.response.PatientDetailedResponse;
import com.salman.dentalsystem.model.entity.Patient;
import com.salman.dentalsystem.model.enums.ErrorCode;
import com.salman.dentalsystem.repository.PatientRepository;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.SuccessDataResult;
import com.salman.dentalsystem.service.abstraction.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientMapper patientMapper;
    private final PatientRepository patientRepository;

    @Override
    public DataResult<PatientDetailedResponse> create(PatientCreateRequest request) {
        Patient patient = patientMapper.toEntity(request);
        Patient savedPatient = patientRepository.save(patient);
        PatientDetailedResponse patientDetailedResponse = patientMapper.toDetailedResponse(savedPatient);
        return new SuccessDataResult<>(patientDetailedResponse, "Pasiyent uğurla yaradıldı");
    }

    @Override
    public DataResult<PatientDetailedResponse> getById(UUID id) {
        Patient foundPatient = patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pasiyent tapılmadı: " + id, ErrorCode.PATIENT_NOT_FOUND));
        PatientDetailedResponse patientDetailedResponse = patientMapper.toDetailedResponse(foundPatient);
        return new SuccessDataResult<>(patientDetailedResponse, "Pasiyent tapıldı");
    }
}
