package com.foro.usuarios.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime fecha,
        int estado,
        String error,
        String mensaje,
        String ruta,
        Map<String, String> detalles) {

    public static ErrorResponse de(int estado, String error, String mensaje, String ruta, Map<String, String> detalles) {
        return new ErrorResponse(LocalDateTime.now(), estado, error, mensaje, ruta, detalles);
    }
}
