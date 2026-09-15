package ar.edu.unvime.apiblank.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unvime.apiblank.dto.ProductoDto;
import ar.edu.unvime.apiblank.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;

/** Expone el catálogo de productos como endpoints HTTP. */

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @Operation(summary = "Lista todos los productos del catálogo")
    @GetMapping
    public List<ProductoDto> listar() {
        return productoService.obtenerTodos();
    }

    @Operation(summary = "Obtiene un producto por su id")
    @GetMapping("/{id}")
    public ProductoDto obtenerPorId(@PathVariable Integer id) {
        return productoService.obtenerPorId(id);
    }
}