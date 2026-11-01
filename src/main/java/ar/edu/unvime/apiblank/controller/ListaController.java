package ar.edu.unvime.apiblank.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import ar.edu.unvime.apiblank.security.UsuarioAutenticado;
import ar.edu.unvime.apiblank.service.ListaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

/** Expone las listas de favoritos como endpoints HTTP. Requiere un token JWT válido. */
@RestController
@RequestMapping("/api/listas")
public class ListaController {

    private final ListaService listaService;

    public ListaController(ListaService listaService) {
        this.listaService = listaService;
    }

    @Operation(summary = "Crea una nueva lista cuyo dueño es el usuario autenticado (requiere token)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListaResponseDto crear(@Valid @RequestBody ListaRequestDto request,
                                  @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return listaService.crear(request, usuario);
    }

    @Operation(summary = "Lista las listas del usuario autenticado, o todas si es ADMIN (requiere token)")
    @GetMapping
    public List<ListaResponseDto> listar(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return listaService.listar(usuario);
    }

    @Operation(summary = "Obtiene una lista por su id (403 si no es del usuario y no es ADMIN; requiere token)")
    @GetMapping("/{id}")
    public ListaResponseDto obtenerPorId(@PathVariable Long id,
                                         @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return listaService.obtenerPorId(id, usuario);
    }

    @Operation(summary = "Lista los favoritos de una lista (403 si no es del usuario y no es ADMIN; requiere token)")
    @GetMapping("/{id}/favoritos")
    public List<FavoritoResponseDto> favoritosDeLista(@PathVariable Long id,
                                                      @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return listaService.listarFavoritos(id, usuario);
    }

    @Operation(summary = "Mueve todos los favoritos de una lista a otra y elimina la lista origen (requiere token)")
    @PostMapping("/{origenId}/mover-favoritos")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void moverFavoritos(@PathVariable Long origenId,
                               @Valid @RequestBody MoverFavoritosRequestDto request,
                               @AuthenticationPrincipal UsuarioAutenticado usuario) {
        listaService.moverFavoritos(origenId, request.listaDestinoId(), usuario);
    }

    @Operation(summary = "Elimina una lista vacía (409 si tiene favoritos, 403 si no es suya; requiere token)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id,
                         @AuthenticationPrincipal UsuarioAutenticado usuario) {
        listaService.eliminar(id, usuario);
    }
}