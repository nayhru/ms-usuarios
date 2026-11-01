package com.foro.usuarios.controller;

import com.foro.usuarios.dto.UsuarioRequest;
import com.foro.usuarios.dto.UsuarioResponse;
import com.foro.usuarios.dto.UsuarioUpdateRequest;
import com.foro.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioResponse> getAllUsuarios(@RequestParam(required = false) String rol) {
        return usuarioService.getAllUsuarios(rol);
    }

    @GetMapping("/{id}")
    public UsuarioResponse getUsuarioById(@PathVariable Long id, @AuthenticationPrincipal Jwt solicitante) {
        return usuarioService.getUsuarioById(id, solicitante);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse createUsuario(@Valid @RequestBody UsuarioRequest usuario,
                                         @AuthenticationPrincipal Jwt solicitante) {
        return usuarioService.createUsuario(usuario, solicitante);
    }

    @PutMapping("/{id}")
    public UsuarioResponse updateUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateRequest usuario,
                                         @AuthenticationPrincipal Jwt solicitante) {
        return usuarioService.updateUsuario(id, usuario, solicitante);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUsuario(@PathVariable Long id, @AuthenticationPrincipal Jwt solicitante) {
        usuarioService.deleteUsuario(id, solicitante);
    }
}
