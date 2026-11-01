package com.foro.usuarios.exception;

import com.foro.usuarios.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> detalles = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(campo -> detalles.putIfAbsent(campo.getField(), campo.getDefaultMessage()));
        log.warn("Datos invalidos en {} {}: {}", request.getMethod(), request.getRequestURI(), detalles);
        return responder(HttpStatus.BAD_REQUEST, "Los datos enviados no son validos", request, detalles);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> noEncontrado(RecursoNoEncontradoException ex, HttpServletRequest request) {
        log.warn("Recurso no encontrado en {}: {}", request.getRequestURI(), ex.getMessage());
        return responder(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ErrorResponse> conflicto(ConflictoException ex, HttpServletRequest request) {
        log.warn("Conflicto en {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return responder(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<ErrorResponse> accesoDenegado(AccesoDenegadoException ex, HttpServletRequest request) {
        log.warn("Acceso denegado en {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return responder(HttpStatus.FORBIDDEN, ex.getMessage(), request, null);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponse> credenciales(CredencialesInvalidasException ex, HttpServletRequest request) {
        return responder(HttpStatus.UNAUTHORIZED, ex.getMessage(), request, null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> jsonInvalido(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("JSON no legible en {} {}", request.getMethod(), request.getRequestURI());
        return responder(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no es un JSON valido", request, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> tipoInvalido(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String mensaje = "El valor '" + ex.getValue() + "' no es valido para el parametro " + ex.getName();
        log.warn("{} en {}", mensaje, request.getRequestURI());
        return responder(HttpStatus.BAD_REQUEST, mensaje, request, null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> rutaInexistente(NoResourceFoundException ex, HttpServletRequest request) {
        return responder(HttpStatus.NOT_FOUND, "La ruta solicitada no existe", request, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> metodoNoPermitido(HttpRequestMethodNotSupportedException ex,
                                                           HttpServletRequest request) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED,
                "El metodo " + ex.getMethod() + " no esta permitido en esta ruta", request, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> integridad(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Restriccion de base de datos en {} {}: {}", request.getMethod(), request.getRequestURI(),
                ex.getMostSpecificCause().getMessage());
        return responder(HttpStatus.CONFLICT, "La operacion viola una restriccion de la base de datos", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> general(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado en {} {}", request.getMethod(), request.getRequestURI(), ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error interno en el servidor", request, null);
    }

    private ResponseEntity<ErrorResponse> responder(HttpStatus estado, String mensaje, HttpServletRequest request,
                                                    Map<String, String> detalles) {
        return ResponseEntity.status(estado).body(ErrorResponse.de(
                estado.value(), estado.getReasonPhrase(), mensaje, request.getRequestURI(), detalles));
    }
}
