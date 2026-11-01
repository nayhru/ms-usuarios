package com.foro.usuarios.service;

import com.foro.usuarios.dto.LoginRequest;
import com.foro.usuarios.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest credenciales);
}
