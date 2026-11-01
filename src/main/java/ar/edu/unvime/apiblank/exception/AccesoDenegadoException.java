package ar.edu.unvime.apiblank.exception;

/** Se lanza cuando un usuario intenta operar sobre una lista que no es suya y no es ADMIN. */
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException() {
        super("No tenés permiso para operar sobre esta lista");
    }
}