package ar.edu.unvime.apiblank.dto;

/** Datos que la API devuelve al cliente al consultar una lista. */
public record ListaResponseDto(
        Long id,
        String nombre
) {
}