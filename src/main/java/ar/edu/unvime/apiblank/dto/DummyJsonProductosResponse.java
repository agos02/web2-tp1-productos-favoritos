package ar.edu.unvime.apiblank.dto;
import java.util.List;

/** Envoltorio de la respuesta de DummyJSON para el listado de productos. */

public record DummyJsonProductosResponse(
        List<DummyJsonProducto> products
) {
}