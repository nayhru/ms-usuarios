package com.foro.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Pattern(regexp = "^[a-zA-Z0-9_.]{3,30}$",
                message = "El nombre de usuario debe tener entre 3 y 30 letras, numeros, punto o guion bajo")
        String nombreUsuario,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato valido")
        @Size(max = 120, message = "El correo no puede superar los 120 caracteres")
        String correo,

        @NotBlank(message = "La clave es obligatoria")
        @Size(min = 8, max = 60, message = "La clave debe tener entre 8 y 60 caracteres")
        String clave,

        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 100, message = "El nombre completo no puede superar los 100 caracteres")
        String nombreCompleto,

        // Opcional: solo un ADMIN puede asignar un rol distinto a USUARIO
        Long rolId) {
}
