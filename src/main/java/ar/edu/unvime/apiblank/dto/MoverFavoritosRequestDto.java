package ar.edu.unvime.apiblank.dto;

import jakarta.validation.constraints.NotNull;

/** Datos que el cliente envía para mover los favoritos de una lista a otra. */
public record MoverFavoritosRequestDto(
        @NotNull(message = "El listaDestinoId es obligatorio")
        Long listaDestinoId
) {
}