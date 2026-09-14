package ar.edu.unvime.apiblank.repository;

import ar.edu.unvime.apiblank.model.Favorito;

import java.util.List;
import java.util.Optional;

/** Define las operaciones de persistencia para Favorito, sin comprometerse a una implementación concreta. */

public interface FavoritoRepository {

    Favorito save(Favorito favorito);

    List<Favorito> findAll();

    Optional<Favorito> findById(Long id);

    void deleteById(Long id);
}