package com.salman.dentalsystem.repository;

import com.salman.dentalsystem.model.entity.XrayImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface XrayImageRepository extends JpaRepository<XrayImage, UUID> {
    List<XrayImage> findAllByAppointmentId(UUID appointmentId);
}
