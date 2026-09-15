package ar.edu.unvime.apiblank.exception;

/** Se lanza cuando no se encuentra un producto en el catálogo externo. */

public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException(Integer id) {
        super("No existe un producto con id " + id);
    }
}