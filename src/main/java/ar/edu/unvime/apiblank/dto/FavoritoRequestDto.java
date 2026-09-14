package ar.edu.unvime.apiblank.dto;

/** Datos que el cliente envía para crear o actualizar un favorito. */

public record FavoritoRequestDto(
        Integer productoId,
        String nota
) {
}