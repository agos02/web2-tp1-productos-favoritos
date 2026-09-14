package ar.edu.unvime.apiblank.dto;
/** Representa un producto tal como lo modela la API externa DummyJSON. */

public record DummyJsonProducto(
        Integer id,
        String title,
        String description,
        String category,
        Double price,
        Integer stock,
        String brand
) {
}