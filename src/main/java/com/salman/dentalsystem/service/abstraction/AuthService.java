package com.salman.dentalsystem.service.abstraction;

import com.salman.dentalsystem.model.dto.request.LoginRequest;
import com.salman.dentalsystem.model.dto.request.RefreshRequest;
import com.salman.dentalsystem.model.dto.response.LoginResponse;
import com.salman.dentalsystem.model.dto.response.RefreshResponse;
import com.salman.dentalsystem.result.DataResult;
import jakarta.validation.Valid;

public interface AuthService {
    DataResult<LoginResponse> login(LoginRequest request);

    DataResult<RefreshResponse> refresh(RefreshRequest request);
}
