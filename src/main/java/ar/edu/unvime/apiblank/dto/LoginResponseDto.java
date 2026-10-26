package ar.edu.unvime.apiblank.dto;

/** Respuesta del login: el token que el cliente debe enviar en el header Authorization. */
public record LoginResponseDto(
        String token,
        String tipo
) {
}