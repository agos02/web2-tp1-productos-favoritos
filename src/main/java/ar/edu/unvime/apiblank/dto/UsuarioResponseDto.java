package ar.edu.unvime.apiblank.dto;

import ar.edu.unvime.apiblank.model.Rol;

/** Datos que la API devuelve al cliente sobre un usuario. Nunca incluye la contraseña ni su hash. */
public record UsuarioResponseDto(
        Long id,
        String email,
        Rol rol
) {
}