package ar.edu.unvime.apiblank.dto;

import jakarta.validation.constraints.NotBlank;

/** Datos que el cliente envía para iniciar sesión. */
public record LoginRequestDto(
        @NotBlank(message = "El email no puede estar vacío")
        String email,

        @NotBlank(message = "La contraseña no puede estar vacía")
        String password
) {
}