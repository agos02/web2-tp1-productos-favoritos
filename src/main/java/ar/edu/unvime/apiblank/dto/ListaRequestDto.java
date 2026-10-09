package ar.edu.unvime.apiblank.dto;

import jakarta.validation.constraints.NotBlank;

/** Datos que el cliente envía para crear una lista. */
public record ListaRequestDto(
        @NotBlank(message = "El nombre de la lista no puede estar vacío")
        String nombre
) {
}