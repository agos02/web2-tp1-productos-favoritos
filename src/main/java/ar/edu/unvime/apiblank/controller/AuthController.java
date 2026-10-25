package ar.edu.unvime.apiblank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unvime.apiblank.dto.RegistroRequestDto;
import ar.edu.unvime.apiblank.dto.UsuarioResponseDto;
import ar.edu.unvime.apiblank.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

/** Expone el registro de usuarios (y más adelante el login) como endpoints HTTP públicos. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Registra un nuevo usuario con rol USER (responde 409 si el email ya existe)")
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDto registrar(@Valid @RequestBody RegistroRequestDto request) {
        return usuarioService.registrar(request);
    }
}