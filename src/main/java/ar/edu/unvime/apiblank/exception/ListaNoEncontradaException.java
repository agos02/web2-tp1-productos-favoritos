package ar.edu.unvime.apiblank.exception;

/** Se lanza cuando se busca una lista por un id que no existe. */
public class ListaNoEncontradaException extends RuntimeException {

    public ListaNoEncontradaException(Long id) {
        super("No existe una lista con id " + id);
    }
}