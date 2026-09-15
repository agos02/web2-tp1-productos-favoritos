package ar.edu.unvime.apiblank.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unvime.apiblank.dto.FavoritoRequestDto;
import ar.edu.unvime.apiblank.dto.FavoritoResponseDto;
import ar.edu.unvime.apiblank.service.FavoritoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

/** Expone el CRUD de favoritos como endpoints HTTP. */

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    @Operation(summary = "Crea un nuevo favorito")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FavoritoResponseDto crear(@Valid @RequestBody FavoritoRequestDto request) {
        return favoritoService.crear(request);
    }

    @Operation(summary = "Lista todos los favoritos")
    @GetMapping
    public List<FavoritoResponseDto> listar() {
        return favoritoService.listar();
    }

    @Operation(summary = "Obtiene un favorito por su id")
    @GetMapping("/{id}")
    public FavoritoResponseDto obtenerPorId(@PathVariable Long id) {
        return favoritoService.obtenerPorId(id);
    }

    @Operation(summary = "Actualiza un favorito existente")
    @PutMapping("/{id}")
    public FavoritoResponseDto actualizar(@PathVariable Long id, @Valid @RequestBody FavoritoRequestDto request) {
        return favoritoService.actualizar(id, request);
    }

    @Operation(summary = "Elimina un favorito")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        favoritoService.eliminar(id);
    }
}