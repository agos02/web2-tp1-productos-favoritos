package ar.edu.unvime.apiblank.exception;

/** Se lanza cuando se pide mover favoritos a la misma lista de origen. */
public class MovimientoInvalidoException extends RuntimeException {

    public MovimientoInvalidoException(String mensaje) {
        super(mensaje);
    }
}