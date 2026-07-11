package com.salman.dentalsystem.repository;

import com.salman.dentalsystem.model.entity.RefreshToken;
import com.salman.dentalsystem.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByUser(User user);

    Optional<RefreshToken> findByToken(UUID token);
}
