package com.catalog.service;

import com.catalog.dto.LoginRequest;
import com.catalog.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
