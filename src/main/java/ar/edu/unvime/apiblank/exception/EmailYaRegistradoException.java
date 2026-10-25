package ar.edu.unvime.apiblank.exception;

/** Se lanza cuando se intenta registrar un usuario con un email que ya existe. */
public class EmailYaRegistradoException extends RuntimeException {

    public EmailYaRegistradoException(String email) {
        super("Ya existe un usuario registrado con el email " + email);
    }
}