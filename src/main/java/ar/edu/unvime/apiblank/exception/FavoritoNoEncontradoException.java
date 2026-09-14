package ar.edu.unvime.apiblank.exception;

/** Se lanza cuando se busca un favorito por un id que no existe. */

public class FavoritoNoEncontradoException extends RuntimeException {

    public FavoritoNoEncontradoException(Long id) {
        super("No existe un favorito con id " + id);
    }
}