package com.salman.dentalsystem.service.concrete;

import com.salman.dentalsystem.exception.custom.InvalidTokenException;
import com.salman.dentalsystem.exception.custom.NotFoundException;
import com.salman.dentalsystem.model.dto.request.LoginRequest;
import com.salman.dentalsystem.model.dto.request.RefreshRequest;
import com.salman.dentalsystem.model.dto.response.LoginResponse;
import com.salman.dentalsystem.model.dto.response.RefreshResponse;
import com.salman.dentalsystem.model.entity.RefreshToken;
import com.salman.dentalsystem.model.entity.User;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.model.enums.ErrorCode;
import com.salman.dentalsystem.repository.RefreshTokenRepository;
import com.salman.dentalsystem.repository.UserRepository;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.Result;
import com.salman.dentalsystem.result.SuccessDataResult;
import com.salman.dentalsystem.result.SuccessResult;
import com.salman.dentalsystem.security.service.JwtService;
import com.salman.dentalsystem.service.abstraction.AuthService;
import com.salman.dentalsystem.service.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Value("${spring.jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    @Override
    public DataResult<LoginResponse> login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found with username: " + request.getUsername(), ErrorCode.USER_NOT_FOUND));
        if (user.getStatus() != EntityStatus.ACTIVE) {
            throw new NotFoundException("User not found", ErrorCode.USER_NOT_FOUND);
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        String accessToken = jwtService.generateAccessToken(user.getUsername(), user.getRole());
        UUID refreshToken = UUID.randomUUID();

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUser(user)
                .map(foundRefreshTokenEntity -> {
                    foundRefreshTokenEntity.setToken(refreshToken);
                    foundRefreshTokenEntity.setExpiresAt(LocalDateTime.now().plus(refreshTokenExpiration, ChronoUnit.MILLIS));
                    foundRefreshTokenEntity.setRevoked(false);
                    return foundRefreshTokenEntity;
                })
                .orElseGet(() -> new RefreshToken(refreshToken, user, false, LocalDateTime.now().plus(refreshTokenExpiration, ChronoUnit.MILLIS)));
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshTokenEntity);
        LoginResponse response = new LoginResponse(accessToken, savedRefreshToken.getToken());
        return new SuccessDataResult<>(response, "User logged in successfully");
    }

    @Override
    public DataResult<RefreshResponse> refresh(RefreshRequest request) {
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token", ErrorCode.INVALID_REFRESH_TOKEN));
        validateRefreshToken(refreshTokenEntity);
        User user = refreshTokenEntity.getUser();
        if (user.getStatus() != EntityStatus.ACTIVE) {
            throw new NotFoundException("User not found", ErrorCode.USER_NOT_FOUND);
        }
        String accessToken = jwtService.generateAccessToken(user.getUsername(), user.getRole());
        UUID refreshToken = UUID.randomUUID();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plus(refreshTokenExpiration, ChronoUnit.MILLIS));
        refreshTokenRepository.save(refreshTokenEntity);
        RefreshResponse response = new RefreshResponse(accessToken, refreshToken);
        return new SuccessDataResult<>(response, "Tokens refreshed successfully");
    }

    @Override
    public void revokeRefreshToken(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Result logout() {
        User user = userService.getCurrentUser();
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("User not found", ErrorCode.USER_NOT_FOUND));
        revokeRefreshToken(refreshToken);
        return new SuccessResult("User logged out");
    }

    private void validateRefreshToken(RefreshToken refreshTokenEntity) {
        if (refreshTokenEntity.getRevoked()) {
            throw new InvalidTokenException("Refresh token has been revoked", ErrorCode.REVOKED_REFRESH_TOKEN);
        }
        if (refreshTokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Refresh token expired", ErrorCode.EXPIRED_REFRESH_TOKEN);
        }
    }
}
