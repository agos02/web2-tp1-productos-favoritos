package ar.edu.unvime.apiblank.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unvime.apiblank.dto.FavoritoResponseDto;
import ar.edu.unvime.apiblank.dto.ListaRequestDto;
import ar.edu.unvime.apiblank.dto.ListaResponseDto;
import ar.edu.unvime.apiblank.dto.MoverFavoritosRequestDto;
import ar.edu.unvime.apiblank.service.FavoritoService;
import ar.edu.unvime.apiblank.service.ListaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;


/** Expone las listas de favoritos como endpoints HTTP. */
@RestController
@RequestMapping("/api/listas")
public class ListaController {

    private final ListaService listaService;
    private final FavoritoService favoritoService;

    public ListaController(ListaService listaService, FavoritoService favoritoService) {
        this.listaService = listaService;
        this.favoritoService = favoritoService;
    }

    @Operation(summary = "Crea una nueva lista")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListaResponseDto crear(@Valid @RequestBody ListaRequestDto request) {
        return listaService.crear(request);
    }

    @Operation(summary = "Lista todas las listas")
    @GetMapping
    public List<ListaResponseDto> listar() {
        return listaService.listar();
    }

    @Operation(summary = "Obtiene una lista por su id")
    @GetMapping("/{id}")
    public ListaResponseDto obtenerPorId(@PathVariable Long id) {
        return listaService.obtenerPorId(id);
    }

    @Operation(summary = "Lista los favoritos que pertenecen a una lista")
    @GetMapping("/{id}/favoritos")
    public List<FavoritoResponseDto> favoritosDeLista(@PathVariable Long id) {
        return favoritoService.listarPorLista(id);
    }

    @Operation(summary = "Mueve todos los favoritos de una lista a otra y elimina la lista origen")
    @PostMapping("/{origenId}/mover-favoritos")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void moverFavoritos(@PathVariable Long origenId,
    @Valid @RequestBody MoverFavoritosRequestDto request) {
    listaService.moverFavoritos(origenId, request.listaDestinoId());
    }

    @Operation(summary = "Elimina una lista vacía (si tiene favoritos responde 409)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        listaService.eliminar(id);
    }
}