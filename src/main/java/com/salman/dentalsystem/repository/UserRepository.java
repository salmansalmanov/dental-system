package com.salman.dentalsystem.repository;

import com.salman.dentalsystem.model.entity.User;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByIdAndStatusNot(UUID id, EntityStatus status);

    @Query("""
            SELECT u
            FROM User u
            WHERE (:role IS NULL OR u.role = :role)
              AND (:status IS NULL OR u.status = :status)
              AND (
                   :search IS NULL
                   OR LOWER(u.name) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                   OR LOWER(u.surname) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                   OR LOWER(u.patronymic) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                   OR LOWER(u.phoneNumber) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                   OR LOWER(u.pin) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
              )
            """)
    Page<User> findAllFiltered(
            @Param("search") String search,
            @Param("status") EntityStatus status,
            @Param("role") Role role,
            Pageable pageable
    );

    Optional<User> findByIdAndRoleAndStatus(UUID id, Role role, EntityStatus status);

    boolean existsByPin(String pin);
}
