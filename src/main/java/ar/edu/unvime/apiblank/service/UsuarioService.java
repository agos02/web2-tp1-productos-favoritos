package ar.edu.unvime.apiblank.service;

import ar.edu.unvime.apiblank.dto.RegistroRequestDto;
import ar.edu.unvime.apiblank.dto.UsuarioResponseDto;

/** Define las operaciones de negocio sobre usuarios. */
public interface UsuarioService {

    UsuarioResponseDto registrar(RegistroRequestDto request);
}