package ar.edu.unvime.apiblank.exception;

import java.time.LocalDateTime;
import java.util.Map;

/** Formato de error para fallos de validación, con el detalle de cada campo inválido. */

public record ValidationErrorResponseDto(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> campos
) {
}