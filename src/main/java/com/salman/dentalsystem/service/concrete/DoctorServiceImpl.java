package com.salman.dentalsystem.service.concrete;

import com.salman.dentalsystem.exception.custom.NotFoundException;
import com.salman.dentalsystem.mapper.DoctorMapper;
import com.salman.dentalsystem.model.dto.request.DoctorCreateRequest;
import com.salman.dentalsystem.model.dto.response.DoctorDetailedResponse;
import com.salman.dentalsystem.model.dto.response.DoctorResponse;
import com.salman.dentalsystem.model.entity.Doctor;
import com.salman.dentalsystem.model.enums.ErrorCode;
import com.salman.dentalsystem.model.enums.UserStatus;
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
        doctor.setStatus(UserStatus.ACTIVE);
        Doctor savedDoctor = doctorRepository.save(doctor);
        DoctorDetailedResponse response = doctorMapper.toDetailedResponse(savedDoctor);
        return new SuccessDataResult<>(response, "Həkim yaradıldı");
    }

    @Override
    public DataResult<DoctorDetailedResponse> getById(UUID id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Həkim tapılmadı: " + id, ErrorCode.DOCTOR_NOT_FOUND));
        DoctorDetailedResponse response = doctorMapper.toDetailedResponse(doctor);
        return new SuccessDataResult<>(response, "Həkim tapıldı");
    }

    @Override
    public DataResult<PageData<DoctorResponse>> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Doctor> doctorPage = doctorRepository.findAll(pageable);
        PageData<DoctorResponse> pageData = PageData.<DoctorResponse>builder()
                .totalPages(doctorPage.getTotalPages())
                .totalElements(doctorPage.getTotalElements())
                .firstPage(doctorPage.isFirst())
                .lastPage(doctorPage.isLast())
                .page(doctorPage.getNumber())
                .size(doctorPage.getSize())
                .content(doctorPage.getContent().stream().map(doctorMapper::toResponse).toList())
                .build();
        return new SuccessDataResult<>(pageData, "Həkimlər tapıldı");
    }
}
