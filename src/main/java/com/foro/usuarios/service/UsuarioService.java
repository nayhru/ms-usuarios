package com.foro.usuarios.service;

import com.foro.usuarios.dto.UsuarioRequest;
import com.foro.usuarios.dto.UsuarioResponse;
import com.foro.usuarios.dto.UsuarioUpdateRequest;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface UsuarioService {

    List<UsuarioResponse> getAllUsuarios(String rol);

    UsuarioResponse getUsuarioById(Long id, Jwt solicitante);

    UsuarioResponse createUsuario(UsuarioRequest usuario, Jwt solicitante);

    UsuarioResponse updateUsuario(Long id, UsuarioUpdateRequest usuario, Jwt solicitante);

    void deleteUsuario(Long id, Jwt solicitante);
}
