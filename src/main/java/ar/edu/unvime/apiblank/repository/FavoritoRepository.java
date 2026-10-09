package ar.edu.unvime.apiblank.repository;

import java.util.List;
import java.util.Optional;

import ar.edu.unvime.apiblank.model.Favorito;

/** Define las operaciones de persistencia para Favorito, sin comprometerse a una implementación concreta. */
public interface FavoritoRepository {

    Favorito save(Favorito favorito);

    List<Favorito> findAll();

    Optional<Favorito> findById(Long id);

    List<Favorito> findByListaId(Long listaId);

    void deleteById(Long id);
}