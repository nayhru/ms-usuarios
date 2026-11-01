package com.foro.usuarios.dto;

import com.foro.usuarios.model.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nombreUsuario,
        String correo,
        String nombreCompleto,
        Boolean activo,
        LocalDateTime fechaRegistro,
        String rol) {

    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombreUsuario(),
                usuario.getCorreo(),
                usuario.getNombreCompleto(),
                usuario.getActivo(),
                usuario.getFechaRegistro(),
                usuario.getRol().getNombre());
    }
}
