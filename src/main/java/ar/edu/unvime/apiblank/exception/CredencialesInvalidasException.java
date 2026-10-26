package ar.edu.unvime.apiblank.exception;

/** Se lanza cuando el email o la contraseña del login no son correctos. */
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException() {
        super("Email o contraseña incorrectos");
    }
}