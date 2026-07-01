package com.salman.dentalsystem.service.concrete;

import com.salman.dentalsystem.exception.custom.NotFoundException;
import com.salman.dentalsystem.mapper.DoctorMapper;
import com.salman.dentalsystem.model.dto.request.DoctorCreateRequest;
import com.salman.dentalsystem.model.dto.request.DoctorUpdateRequest;
import com.salman.dentalsystem.model.dto.response.DoctorDetailedResponse;
import com.salman.dentalsystem.model.dto.response.DoctorResponse;
import com.salman.dentalsystem.model.entity.Doctor;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.model.enums.ErrorCode;
import com.salman.dentalsystem.repository.DoctorRepository;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.PageData;
import com.salman.dentalsystem.result.SuccessDataResult;
import com.salman.dentalsystem.service.abstraction.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorMapper doctorMapper;
    private final DoctorRepository doctorRepository;

    @Override
    public DataResult<DoctorDetailedResponse> create(DoctorCreateRequest request) {
        Doctor doctor = doctorMapper.createRequestToEntity(request);
        doctor.setStatus(EntityStatus.ACTIVE);
        Doctor savedDoctor = doctorRepository.save(doctor);
        DoctorDetailedResponse response = doctorMapper.toDetailedResponse(savedDoctor);
        return new SuccessDataResult<>(response, "Doctor created successfully");
    }

    @Override
    public DataResult<DoctorDetailedResponse> getById(UUID id) {
        Doctor doctor = doctorRepository.findByIdAndStatusNot(id, EntityStatus.DELETED)
                .orElseThrow(() -> new NotFoundException("Doctor not found with ID: " + id, ErrorCode.DOCTOR_NOT_FOUND));
        DoctorDetailedResponse response = doctorMapper.toDetailedResponse(doctor);
        return new SuccessDataResult<>(response, "Doctor found successfully");
    }

    @Override
    public DataResult<PageData<DoctorResponse>> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Doctor> doctorPage = doctorRepository.findAllByStatusNot(EntityStatus.DELETED, pageable);
        PageData<DoctorResponse> pageData = PageData.<DoctorResponse>builder()
                .totalPages(doctorPage.getTotalPages())
                .totalElements(doctorPage.getTotalElements())
                .firstPage(doctorPage.isFirst())
                .lastPage(doctorPage.isLast())
                .page(doctorPage.getNumber())
                .size(doctorPage.getSize())
                .content(doctorPage.getContent().stream().map(doctorMapper::toResponse).toList())
                .build();
        return new SuccessDataResult<>(pageData, "Doctors found successfully");
    }

    @Override
    public DataResult<DoctorDetailedResponse> updateById(UUID id, DoctorUpdateRequest request) {
        Doctor existingDoctor = doctorRepository.findByIdAndStatusNot(id, EntityStatus.DELETED)
                .orElseThrow(() -> new NotFoundException("Doctor not found with ID: " + id, ErrorCode.DOCTOR_NOT_FOUND));
        Doctor updatedDoctor = doctorMapper.updateRequestToEntity(request, existingDoctor);
        Doctor savedDoctor = doctorRepository.save(updatedDoctor);
        DoctorDetailedResponse response = doctorMapper.toDetailedResponse(savedDoctor);
        return new SuccessDataResult<>(response, "Doctor updated successfully");
    }

    @Override
    public DataResult<DoctorDetailedResponse> deleteById(UUID id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Doctor not found with ID: " + id, ErrorCode.DOCTOR_NOT_FOUND));
        doctor.setStatus(EntityStatus.DELETED);
        Doctor savedDoctor = doctorRepository.save(doctor);
        DoctorDetailedResponse response = doctorMapper.toDetailedResponse(savedDoctor);
        return new SuccessDataResult<>(response, "Doctor deleted successfully");
    }

    @Override
    public Doctor getEntity(UUID id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Doctor not found with ID: " + id, ErrorCode.DOCTOR_NOT_FOUND));
    }
}
