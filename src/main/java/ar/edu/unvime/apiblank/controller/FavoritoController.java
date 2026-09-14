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

/** Expone el CRUD de favoritos como endpoints HTTP. */

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FavoritoResponseDto crear(@RequestBody FavoritoRequestDto request) {
        return favoritoService.crear(request);
    }

    @GetMapping
    public List<FavoritoResponseDto> listar() {
        return favoritoService.listar();
    }

    @GetMapping("/{id}")
    public FavoritoResponseDto obtenerPorId(@PathVariable Long id) {
        return favoritoService.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public FavoritoResponseDto actualizar(@PathVariable Long id, @RequestBody FavoritoRequestDto request) {
        return favoritoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        favoritoService.eliminar(id);
    }
}