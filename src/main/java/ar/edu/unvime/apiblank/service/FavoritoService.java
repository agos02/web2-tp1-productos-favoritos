package ar.edu.unvime.apiblank.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import ar.edu.unvime.apiblank.dto.FavoritoRequestDto;
import ar.edu.unvime.apiblank.dto.FavoritoResponseDto;
import ar.edu.unvime.apiblank.exception.FavoritoNoEncontradoException;
import ar.edu.unvime.apiblank.exception.ListaNoEncontradaException;
import ar.edu.unvime.apiblank.model.Favorito;
import ar.edu.unvime.apiblank.repository.FavoritoRepository;
import ar.edu.unvime.apiblank.repository.ListaRepository;

/** Contiene la lógica de negocio para crear, consultar, actualizar y eliminar favoritos. */
@Service
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final ListaRepository listaRepository;

    public FavoritoService(FavoritoRepository favoritoRepository, ListaRepository listaRepository) {
        this.favoritoRepository = favoritoRepository;
        this.listaRepository = listaRepository;
    }

    public FavoritoResponseDto crear(FavoritoRequestDto request) {
        validarQueExisteLista(request.listaId());
        Favorito favorito = new Favorito(
                null,
                request.productoId(),
                request.nota(),
                LocalDateTime.now(),
                request.listaId()
        );
        Favorito guardado = favoritoRepository.save(favorito);
        return mapearAResponseDto(guardado);
    }

    public List<FavoritoResponseDto> listar() {
        return favoritoRepository.findAll().stream()
                .map(this::mapearAResponseDto)
                .toList();
    }

    public List<FavoritoResponseDto> listarPorLista(Long listaId) {
        validarQueExisteLista(listaId);
        return favoritoRepository.findByListaId(listaId).stream()
                .map(this::mapearAResponseDto)
                .toList();
    }

    public FavoritoResponseDto obtenerPorId(Long id) {
        return mapearAResponseDto(buscarOFallar(id));
    }

    public FavoritoResponseDto actualizar(Long id, FavoritoRequestDto request) {
        Favorito favorito = buscarOFallar(id);
        validarQueExisteLista(request.listaId());
        favorito.setProductoId(request.productoId());
        favorito.setNota(request.nota());
        favorito.setListaId(request.listaId());
        return mapearAResponseDto(favoritoRepository.save(favorito));
    }

    public void eliminar(Long id) {
        buscarOFallar(id);
        favoritoRepository.deleteById(id);
    }

    private Favorito buscarOFallar(Long id) {
        return favoritoRepository.findById(id)
                .orElseThrow(() -> new FavoritoNoEncontradoException(id));
    }

    private void validarQueExisteLista(Long listaId) {
        if (listaRepository.findById(listaId).isEmpty()) {
            throw new ListaNoEncontradaException(listaId);
        }
    }

    private FavoritoResponseDto mapearAResponseDto(Favorito favorito) {
        return new FavoritoResponseDto(
                favorito.getId(),
                favorito.getProductoId(),
                favorito.getNota(),
                favorito.getFechaAgregado(),
                favorito.getListaId()
        );
    }
}