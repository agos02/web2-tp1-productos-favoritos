package ar.edu.unvime.apiblank.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import ar.edu.unvime.apiblank.security.UsuarioAutenticado;
import ar.edu.unvime.apiblank.service.FavoritoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

/** Expone el CRUD de favoritos como endpoints HTTP. Requiere un token JWT válido. */
@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    @Operation(summary = "Crea un nuevo favorito en una lista del usuario (403 si la lista no es suya; requiere token)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FavoritoResponseDto crear(@Valid @RequestBody FavoritoRequestDto request,
                                     @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return favoritoService.crear(request, usuario);
    }

    @Operation(summary = "Lista los favoritos del usuario autenticado, o todos si es ADMIN (requiere token)")
    @GetMapping
    public List<FavoritoResponseDto> listar(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return favoritoService.listar(usuario);
    }

    @Operation(summary = "Obtiene un favorito por su id (403 si su lista no es del usuario; requiere token)")
    @GetMapping("/{id}")
    public FavoritoResponseDto obtenerPorId(@PathVariable Long id,
                                            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return favoritoService.obtenerPorId(id, usuario);
    }

    @Operation(summary = "Actualiza un favorito existente (403 si su lista o la nueva no son del usuario; requiere token)")
    @PutMapping("/{id}")
    public FavoritoResponseDto actualizar(@PathVariable Long id,
                                          @Valid @RequestBody FavoritoRequestDto request,
                                          @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return favoritoService.actualizar(id, request, usuario);
    }

    @Operation(summary = "Elimina un favorito (403 si su lista no es del usuario; requiere token)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id,
                         @AuthenticationPrincipal UsuarioAutenticado usuario) {
        favoritoService.eliminar(id, usuario);
    }
}