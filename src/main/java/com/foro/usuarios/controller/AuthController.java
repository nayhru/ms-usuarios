package com.foro.usuarios.controller;

import com.foro.usuarios.dto.LoginRequest;
import com.foro.usuarios.dto.LoginResponse;
import com.foro.usuarios.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest credenciales) {
        return authService.login(credenciales);
    }
}
