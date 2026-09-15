package ar.edu.unvime.apiblank.exception;

import java.time.LocalDateTime;

/** Formato uniforme de error devuelto por la API. */

public record ErrorResponseDto(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
}