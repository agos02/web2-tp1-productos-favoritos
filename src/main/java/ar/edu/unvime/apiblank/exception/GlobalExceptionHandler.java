package ar.edu.unvime.apiblank.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;

/** Centraliza el manejo de errores de la API, devolviendo respuestas consistentes. */

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FavoritoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDto> manejarFavoritoNoEncontrado(
            FavoritoNoEncontradoException ex, WebRequest request) {

        ErrorResponseDto error = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                extraerPath(request)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDto> manejarValidacion(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                campos.put(fieldError.getField(), fieldError.getDefaultMessage())
        );

        ValidationErrorResponseDto error = new ValidationErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Uno o más campos no son válidos",
                extraerPath(request),
                campos
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorResponseDto> manejarErrorServicioExterno(
            RestClientException ex, WebRequest request) {

        ErrorResponseDto error = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.BAD_GATEWAY.value(),
                HttpStatus.BAD_GATEWAY.getReasonPhrase(),
                "No se pudo obtener la información desde el servicio externo de productos",
                extraerPath(request)
        );
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    private String extraerPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}