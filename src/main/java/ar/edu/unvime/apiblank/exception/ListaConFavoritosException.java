package ar.edu.unvime.apiblank.exception;

/** Se lanza cuando se intenta borrar una lista que todavía tiene favoritos asociados. */
public class ListaConFavoritosException extends RuntimeException {

    public ListaConFavoritosException(Long id) {
        super("No se puede eliminar la lista con id " + id + " porque todavía tiene favoritos");
    }
}