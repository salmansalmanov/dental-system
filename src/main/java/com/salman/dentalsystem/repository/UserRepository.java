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
    SELECT u FROM User u
    WHERE (:role IS NULL OR u.role = :role)
      AND (:status IS NULL OR u.status = :status)
      AND (
           :search IS NULL
           OR LOWER(u.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
           OR LOWER(u.surname) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
           OR LOWER(COALESCE(u.patronymic, '')) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
           OR LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
           OR LOWER(CONCAT(u.name, ' ', u.surname)) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
           OR LOWER(CONCAT(u.surname, ' ', u.name)) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
           OR LOWER(CONCAT(u.name, ' ', u.surname, ' ', COALESCE(u.patronymic, ''))) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
      )
""")
    Page<User> findAllFiltered(
            @Param("search") String search,
            @Param("status") EntityStatus status,
            @Param("role") Role role,
            Pageable pageable
    );

    Optional<User> findByIdAndRoleAndStatus(UUID id, Role role, EntityStatus status);

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);
}
