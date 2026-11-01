package ar.edu.unvime.apiblank.service;

import org.springframework.stereotype.Component;

import ar.edu.unvime.apiblank.exception.AccesoDenegadoException;
import ar.edu.unvime.apiblank.model.Lista;
import ar.edu.unvime.apiblank.model.Rol;
import ar.edu.unvime.apiblank.security.UsuarioAutenticado;

/**
 * Concentra la regla de autorización sobre listas: una lista solo la puede operar su dueño o un ADMIN.
 * Los services la usan en cada operación, así la regla vive en un único lugar.
 */
@Component
public class ControlAccesoListas {

    /** Lanza AccesoDenegadoException (403) si el usuario no es el dueño de la lista ni ADMIN. */
    public void verificarAcceso(Lista lista, UsuarioAutenticado usuario) {
        if (usuario.rol() == Rol.ADMIN) {
            return;
        }
        if (!lista.getUsuarioId().equals(usuario.id())) {
            throw new AccesoDenegadoException();
        }
    }

    /** Indica si el usuario puede ver todas las listas (solo los ADMIN). */
    public boolean veTodasLasListas(UsuarioAutenticado usuario) {
        return usuario.rol() == Rol.ADMIN;
    }
}