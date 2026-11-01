package ar.edu.unvime.apiblank.service;

import java.util.List;

import ar.edu.unvime.apiblank.dto.FavoritoResponseDto;
import ar.edu.unvime.apiblank.dto.ListaRequestDto;
import ar.edu.unvime.apiblank.dto.ListaResponseDto;
import ar.edu.unvime.apiblank.security.UsuarioAutenticado;

/** Define las operaciones de negocio sobre listas; todas reciben al usuario que hace la petición. */
public interface ListaService {

    ListaResponseDto crear(ListaRequestDto request, UsuarioAutenticado usuario);

    List<ListaResponseDto> listar(UsuarioAutenticado usuario);

    ListaResponseDto obtenerPorId(Long id, UsuarioAutenticado usuario);

    List<FavoritoResponseDto> listarFavoritos(Long id, UsuarioAutenticado usuario);

    void eliminar(Long id, UsuarioAutenticado usuario);

    void moverFavoritos(Long origenId, Long destinoId, UsuarioAutenticado usuario);
}