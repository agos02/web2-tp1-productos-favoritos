package ar.edu.unvime.apiblank.dto;

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