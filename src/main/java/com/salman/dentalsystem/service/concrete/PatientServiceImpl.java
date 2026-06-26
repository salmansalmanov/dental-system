package com.salman.dentalsystem.service.concrete;

import com.salman.dentalsystem.mapper.PatientMapper;
import com.salman.dentalsystem.model.dto.request.PatientCreateRequest;
import com.salman.dentalsystem.model.dto.response.PatientDetailedResponse;
import com.salman.dentalsystem.model.entity.Patient;
import com.salman.dentalsystem.repository.PatientRepository;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.SuccessDataResult;
import com.salman.dentalsystem.service.abstraction.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
