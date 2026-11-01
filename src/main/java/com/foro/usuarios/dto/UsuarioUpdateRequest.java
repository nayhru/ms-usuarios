package com.foro.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato valido")
        @Size(max = 120, message = "El correo no puede superar los 120 caracteres")
        String correo,

        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 100, message = "El nombre completo no puede superar los 100 caracteres")
        String nombreCompleto,

        // Opcional: si viene, reemplaza la clave actual
        @Size(min = 8, max = 60, message = "La clave debe tener entre 8 y 60 caracteres")
        String clave,

        // Opcionales: solo un ADMIN puede cambiarlos
        Long rolId,
        Boolean activo) {
}
