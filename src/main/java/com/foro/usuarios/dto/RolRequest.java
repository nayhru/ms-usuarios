package com.foro.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RolRequest(
        @NotBlank(message = "El nombre del rol es obligatorio")
        @Pattern(regexp = "^[A-Z_]{3,30}$", message = "El nombre debe tener entre 3 y 30 letras mayusculas")
        String nombre,

        @Size(max = 200, message = "La descripcion no puede superar los 200 caracteres")
        String descripcion) {
}
