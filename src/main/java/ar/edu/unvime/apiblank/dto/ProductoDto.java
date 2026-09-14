package ar.edu.unvime.apiblank.dto;
/** Contrato propio de la API para representar un producto hacia el cliente. */

public record ProductoDto(
        Integer id,
        String title,
        String description,
        String category,
        Double price,
        Integer stock,
        String brand
) {
}