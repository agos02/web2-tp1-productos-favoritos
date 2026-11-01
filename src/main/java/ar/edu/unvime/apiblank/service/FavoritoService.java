package ar.edu.unvime.apiblank.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import ar.edu.unvime.apiblank.dto.FavoritoRequestDto;
import ar.edu.unvime.apiblank.dto.FavoritoResponseDto;
import ar.edu.unvime.apiblank.exception.FavoritoNoEncontradoException;
import ar.edu.unvime.apiblank.exception.ListaNoEncontradaException;
import ar.edu.unvime.apiblank.model.Favorito;
import ar.edu.unvime.apiblank.model.Lista;
import ar.edu.unvime.apiblank.repository.FavoritoRepository;
import ar.edu.unvime.apiblank.repository.ListaRepository;
import ar.edu.unvime.apiblank.security.UsuarioAutenticado;

/**
 * Contiene la lógica de negocio de los favoritos. Un favorito no tiene dueño propio:
 * pertenece a quien sea dueño de su lista, así que la autorización se resuelve por la lista.
 */
@Service
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final ListaRepository listaRepository;
    private final ControlAccesoListas controlAcceso;

    public FavoritoService(FavoritoRepository favoritoRepository,
                           ListaRepository listaRepository,
                           ControlAccesoListas controlAcceso) {
        this.favoritoRepository = favoritoRepository;
        this.listaRepository = listaRepository;
        this.controlAcceso = controlAcceso;
    }

    public FavoritoResponseDto crear(FavoritoRequestDto request, UsuarioAutenticado usuario) {
        // La lista tiene que existir (404) y ser del usuario o este ser ADMIN (403).
        autorizarSobreLista(request.listaId(), usuario);

        Favorito favorito = new Favorito(
                null,
                request.productoId(),
                request.nota(),
                LocalDateTime.now(),
                request.listaId()
        );
        return mapearAResponseDto(favoritoRepository.save(favorito));
    }

    /** Devuelve los favoritos de las listas del usuario, o todos si es ADMIN. */
    public List<FavoritoResponseDto> listar(UsuarioAutenticado usuario) {
        List<Favorito> favoritos;

        if (controlAcceso.veTodasLasListas(usuario)) {
            favoritos = favoritoRepository.findAll();
        } else {
            favoritos = listaRepository.findByUsuarioId(usuario.id()).stream()
                    .flatMap(lista -> favoritoRepository.findByListaId(lista.getId()).stream())
                    .toList();
        }

        return favoritos.stream()
                .map(this::mapearAResponseDto)
                .toList();
    }

    public FavoritoResponseDto obtenerPorId(Long id, UsuarioAutenticado usuario) {
        Favorito favorito = buscarOFallar(id);
        autorizarSobreLista(favorito.getListaId(), usuario);
        return mapearAResponseDto(favorito);
    }

    public FavoritoResponseDto actualizar(Long id, FavoritoRequestDto request, UsuarioAutenticado usuario) {
        Favorito favorito = buscarOFallar(id);

        // Hay que poder operar tanto la lista actual del favorito como la lista a la que se lo quiere mover.
        autorizarSobreLista(favorito.getListaId(), usuario);
        autorizarSobreLista(request.listaId(), usuario);

        favorito.setProductoId(request.productoId());
        favorito.setNota(request.nota());
        favorito.setListaId(request.listaId());
        return mapearAResponseDto(favoritoRepository.save(favorito));
    }

    public void eliminar(Long id, UsuarioAutenticado usuario) {
        Favorito favorito = buscarOFallar(id);
        autorizarSobreLista(favorito.getListaId(), usuario);
        favoritoRepository.deleteById(id);
    }

    private Favorito buscarOFallar(Long id) {
        return favoritoRepository.findById(id)
                .orElseThrow(() -> new FavoritoNoEncontradoException(id));
    }

    /** Busca la lista (404 si no existe) y verifica que el usuario pueda operarla (403 si no). */
    private void autorizarSobreLista(Long listaId, UsuarioAutenticado usuario) {
        Lista lista = listaRepository.findById(listaId)
                .orElseThrow(() -> new ListaNoEncontradaException(listaId));
        controlAcceso.verificarAcceso(lista, usuario);
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