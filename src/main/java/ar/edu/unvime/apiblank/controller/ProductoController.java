package ar.edu.unvime.apiblank.controller;

import ar.edu.unvime.apiblank.dto.ProductoDto;
import ar.edu.unvime.apiblank.service.ProductoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")

public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ProductoDto> listar() {
        return productoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ProductoDto obtenerPorId(@PathVariable Integer id) {
        return productoService.obtenerPorId(id);
    }
}