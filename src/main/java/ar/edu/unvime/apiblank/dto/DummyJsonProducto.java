//un producto, tal como lo modela DummyJSON

package ar.edu.unvime.apiblank.dto;

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