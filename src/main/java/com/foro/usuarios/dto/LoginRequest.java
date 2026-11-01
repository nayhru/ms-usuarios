package com.foro.usuarios.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        String nombreUsuario,

        @NotBlank(message = "La clave es obligatoria")
        String clave) {
}
