package ar.edu.unvime.apiblank.service;

import ar.edu.unvime.apiblank.dto.DummyJsonProducto;
import ar.edu.unvime.apiblank.dto.DummyJsonProductosResponse;
import ar.edu.unvime.apiblank.dto.ProductoDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ProductoService {

    private final RestClient restClient;

    public ProductoService(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<ProductoDto> obtenerTodos() {
        DummyJsonProductosResponse response = restClient.get()
                .uri("/products")
                .retrieve()
                .body(DummyJsonProductosResponse.class);

        return response.products().stream()
                .map(this::mapearADto)
                .toList();
    }

    public ProductoDto obtenerPorId(Integer id) {
        DummyJsonProducto producto = restClient.get()
                .uri("/products/{id}", id)
                .retrieve()
                .body(DummyJsonProducto.class);

        return mapearADto(producto);
    }

    private ProductoDto mapearADto(DummyJsonProducto p) {
        return new ProductoDto(
                p.id(),
                p.title(),
                p.description(),
                p.category(),
                p.price(),
                p.stock(),
                p.brand()
        );
    }
}