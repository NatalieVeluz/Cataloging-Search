package com.catalog.service;

import com.catalog.dto.*;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    void register(RegisterRequest request);

    void resetPassword(ResetPasswordRequest request);
}