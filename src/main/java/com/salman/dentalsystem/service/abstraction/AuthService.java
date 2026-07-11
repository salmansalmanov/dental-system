package com.salman.dentalsystem.service.abstraction;

import com.salman.dentalsystem.model.dto.request.LoginRequest;
import com.salman.dentalsystem.model.dto.response.LoginResponse;
import com.salman.dentalsystem.result.DataResult;

public interface AuthService {
    DataResult<LoginResponse> login(LoginRequest request);
}
