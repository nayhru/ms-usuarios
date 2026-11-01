package com.foro.usuarios.dto;

public record LoginResponse(
        String token,
        String tipo,
        long expiraEnMinutos,
        Long usuarioId,
        String nombreUsuario,
        String rol) {
}
