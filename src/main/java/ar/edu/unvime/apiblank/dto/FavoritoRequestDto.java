package ar.edu.unvime.apiblank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Datos que el cliente envía para crear o actualizar un favorito. */
public record FavoritoRequestDto(
        @NotNull(message = "El productoId es obligatorio")
        Integer productoId,

        @NotBlank(message = "La nota no puede estar vacía")
        String nota
) {
}