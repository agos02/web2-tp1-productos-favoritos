package ar.edu.unvime.apiblank.dto;

import java.time.LocalDateTime;

/** Datos que la API devuelve al cliente al consultar un favorito. */

public record FavoritoResponseDto(
        Long id,
        Integer productoId,
        String nota,
        LocalDateTime fechaAgregado
) {
}