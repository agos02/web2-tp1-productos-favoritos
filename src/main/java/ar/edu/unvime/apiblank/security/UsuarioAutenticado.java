package ar.edu.unvime.apiblank.security;

import ar.edu.unvime.apiblank.model.Rol;

/** Identidad del usuario que hizo la petición, leída del token: su id y su rol. */
public record UsuarioAutenticado(Long id, Rol rol) {
}