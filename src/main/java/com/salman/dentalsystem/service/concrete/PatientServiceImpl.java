package com.salman.dentalsystem.service.concrete;

import com.salman.dentalsystem.exception.custom.NotFoundException;
import com.salman.dentalsystem.mapper.PatientMapper;
import com.salman.dentalsystem.model.dto.request.PatientCreateRequest;
import com.salman.dentalsystem.model.dto.request.PatientUpdateRequest;
import com.salman.dentalsystem.model.dto.response.PatientDetailedResponse;
import com.salman.dentalsystem.model.dto.response.PatientResponse;
import com.salman.dentalsystem.model.entity.Patient;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.model.enums.ErrorCode;
import com.salman.dentalsystem.repository.PatientRepository;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.PageData;
import com.salman.dentalsystem.result.SuccessDataResult;
import com.salman.dentalsystem.service.abstraction.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientMapper patientMapper;
    private final PatientRepository patientRepository;

    @Override
    public DataResult<PatientDetailedResponse> create(PatientCreateRequest request) {
        Patient patient = patientMapper.createRequestToEntity(request);
        patient.setStatus(EntityStatus.ACTIVE);
        Patient savedPatient = patientRepository.save(patient);
        PatientDetailedResponse patientDetailedResponse = patientMapper.toDetailedResponse(savedPatient);
        return new SuccessDataResult<>(patientDetailedResponse, "Patient created successfully");
    }

    @Override
    public DataResult<PatientDetailedResponse> getById(UUID id) {
        Patient foundPatient = patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + id, ErrorCode.PATIENT_NOT_FOUND));
        PatientDetailedResponse patientDetailedResponse = patientMapper.toDetailedResponse(foundPatient);
        return new SuccessDataResult<>(patientDetailedResponse, "Patient found successfully");
    }

    @Override
    public DataResult<PageData<PatientResponse>> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Patient> patientPage = patientRepository.findAll(pageable);
        PageData<PatientResponse> pageData = PageData.<PatientResponse>builder()
                .totalPages(patientPage.getTotalPages())
                .totalElements(patientPage.getTotalElements())
                .firstPage(patientPage.isFirst())
                .lastPage(patientPage.isLast())
                .page(patientPage.getNumber())
                .size(patientPage.getSize())
                .content(patientPage.getContent().stream().map(patientMapper::toResponse).toList())
                .build();
        return new SuccessDataResult<>(pageData, "Patients found successfully");
    }

    @Override
    public DataResult<PatientDetailedResponse> updateById(UUID id, PatientUpdateRequest request) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + id, ErrorCode.PATIENT_NOT_FOUND));
        Patient updatedPatient = patientMapper.updateRequestToEntity(request, existingPatient);
        Patient savedPatient = patientRepository.save(updatedPatient);
        return new SuccessDataResult<>(patientMapper.toDetailedResponse(savedPatient), "Patient updated successfully");
    }

    @Override
    public DataResult<PatientDetailedResponse> deleteById(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + id, ErrorCode.PATIENT_NOT_FOUND));
        patient.setStatus(EntityStatus.DELETED);
        Patient savedPatient = patientRepository.save(patient);
        return new SuccessDataResult<>(patientMapper.toDetailedResponse(savedPatient), "Patient deleted successfully");
    }

    @Override
    public Patient getEntity(UUID id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + id, ErrorCode.PATIENT_NOT_FOUND));
    }
}
