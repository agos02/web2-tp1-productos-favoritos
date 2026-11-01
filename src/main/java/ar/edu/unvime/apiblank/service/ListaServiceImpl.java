package ar.edu.unvime.apiblank.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unvime.apiblank.dto.FavoritoResponseDto;
import ar.edu.unvime.apiblank.dto.ListaRequestDto;
import ar.edu.unvime.apiblank.dto.ListaResponseDto;
import ar.edu.unvime.apiblank.exception.ListaConFavoritosException;
import ar.edu.unvime.apiblank.exception.ListaNoEncontradaException;
import ar.edu.unvime.apiblank.exception.MovimientoInvalidoException;
import ar.edu.unvime.apiblank.model.Favorito;
import ar.edu.unvime.apiblank.model.Lista;
import ar.edu.unvime.apiblank.repository.FavoritoRepository;
import ar.edu.unvime.apiblank.repository.ListaRepository;
import ar.edu.unvime.apiblank.security.UsuarioAutenticado;

/** Contiene la lógica de negocio de las listas, incluida la autorización por dueño o rol. */
@Service
public class ListaServiceImpl implements ListaService {

    private final ListaRepository listaRepository;
    private final FavoritoRepository favoritoRepository;
    private final ControlAccesoListas controlAcceso;

    public ListaServiceImpl(ListaRepository listaRepository,
                            FavoritoRepository favoritoRepository,
                            ControlAccesoListas controlAcceso) {
        this.listaRepository = listaRepository;
        this.favoritoRepository = favoritoRepository;
        this.controlAcceso = controlAcceso;
    }

    @Override
    public ListaResponseDto crear(ListaRequestDto request, UsuarioAutenticado usuario) {
        // El dueño es siempre el usuario autenticado: nunca un dato que mande el cliente.
        Lista guardada = listaRepository.save(new Lista(null, request.nombre(), usuario.id()));
        return mapearAResponseDto(guardada);
    }

    @Override
    public List<ListaResponseDto> listar(UsuarioAutenticado usuario) {
        List<Lista> listas = controlAcceso.veTodasLasListas(usuario)
                ? listaRepository.findAll()
                : listaRepository.findByUsuarioId(usuario.id());

        return listas.stream()
                .map(this::mapearAResponseDto)
                .toList();
    }

    @Override
    public ListaResponseDto obtenerPorId(Long id, UsuarioAutenticado usuario) {
        return mapearAResponseDto(buscarYAutorizar(id, usuario));
    }

    @Override
    public List<FavoritoResponseDto> listarFavoritos(Long id, UsuarioAutenticado usuario) {
        buscarYAutorizar(id, usuario);
        return favoritoRepository.findByListaId(id).stream()
                .map(this::mapearFavoritoAResponseDto)
                .toList();
    }

    @Override
    public void eliminar(Long id, UsuarioAutenticado usuario) {
        buscarYAutorizar(id, usuario);
        if (!favoritoRepository.findByListaId(id).isEmpty()) {
            throw new ListaConFavoritosException(id);
        }
        listaRepository.deleteById(id);
    }

    /**
     * Mueve todos los favoritos de la lista origen a la destino y elimina la origen.
     * Es transaccional: si algo falla a mitad de camino, no queda ningún cambio aplicado.
     * Ambas listas tienen que ser del usuario (o el usuario ser ADMIN).
     */
    @Override
    @Transactional
    public void moverFavoritos(Long origenId, Long destinoId, UsuarioAutenticado usuario) {
        if (origenId.equals(destinoId)) {
            throw new MovimientoInvalidoException("La lista destino debe ser distinta de la lista origen");
        }
        buscarYAutorizar(origenId, usuario);
        buscarYAutorizar(destinoId, usuario);

        List<Favorito> favoritos = favoritoRepository.findByListaId(origenId);
        for (Favorito favorito : favoritos) {
            favorito.setListaId(destinoId);
            favoritoRepository.save(favorito);
        }

        listaRepository.deleteById(origenId);
    }

    /** Busca la lista (404 si no existe) y verifica que el usuario pueda operarla (403 si no). */
    private Lista buscarYAutorizar(Long id, UsuarioAutenticado usuario) {
        Lista lista = listaRepository.findById(id)
                .orElseThrow(() -> new ListaNoEncontradaException(id));
        controlAcceso.verificarAcceso(lista, usuario);
        return lista;
    }

    private ListaResponseDto mapearAResponseDto(Lista lista) {
        return new ListaResponseDto(lista.getId(), lista.getNombre());
    }

    private FavoritoResponseDto mapearFavoritoAResponseDto(Favorito favorito) {
        return new FavoritoResponseDto(
                favorito.getId(),
                favorito.getProductoId(),
                favorito.getNota(),
                favorito.getFechaAgregado(),
                favorito.getListaId()
        );
    }
}