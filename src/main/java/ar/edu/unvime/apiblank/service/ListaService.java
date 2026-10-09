package ar.edu.unvime.apiblank.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ar.edu.unvime.apiblank.dto.ListaRequestDto;
import ar.edu.unvime.apiblank.dto.ListaResponseDto;
import ar.edu.unvime.apiblank.exception.ListaConFavoritosException;
import ar.edu.unvime.apiblank.exception.ListaNoEncontradaException;
import ar.edu.unvime.apiblank.model.Lista;
import ar.edu.unvime.apiblank.repository.FavoritoRepository;
import ar.edu.unvime.apiblank.repository.ListaRepository;

/** Contiene la lógica de negocio para crear, consultar y eliminar listas de favoritos. */
@Service
public class ListaService {

    private final ListaRepository listaRepository;
    private final FavoritoRepository favoritoRepository;

    public ListaService(ListaRepository listaRepository, FavoritoRepository favoritoRepository) {
        this.listaRepository = listaRepository;
        this.favoritoRepository = favoritoRepository;
    }

    public ListaResponseDto crear(ListaRequestDto request) {
        Lista guardada = listaRepository.save(new Lista(null, request.nombre()));
        return mapearAResponseDto(guardada);
    }

    public List<ListaResponseDto> listar() {
        return listaRepository.findAll().stream()
                .map(this::mapearAResponseDto)
                .toList();
    }

    public ListaResponseDto obtenerPorId(Long id) {
        return mapearAResponseDto(buscarOFallar(id));
    }

    public void eliminar(Long id) {
        buscarOFallar(id);
        if (!favoritoRepository.findByListaId(id).isEmpty()) {
            throw new ListaConFavoritosException(id);
        }
        listaRepository.deleteById(id);
    }

    private Lista buscarOFallar(Long id) {
        return listaRepository.findById(id)
                .orElseThrow(() -> new ListaNoEncontradaException(id));
    }

    private ListaResponseDto mapearAResponseDto(Lista lista) {
        return new ListaResponseDto(lista.getId(), lista.getNombre());
    }
}