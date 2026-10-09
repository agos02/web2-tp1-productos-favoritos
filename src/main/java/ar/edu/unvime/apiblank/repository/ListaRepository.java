package ar.edu.unvime.apiblank.repository;

import java.util.List;
import java.util.Optional;

import ar.edu.unvime.apiblank.model.Lista;

/** Define las operaciones de persistencia para Lista, sin comprometerse a una implementación concreta. */
public interface ListaRepository {

    Lista save(Lista lista);

    List<Lista> findAll();

    Optional<Lista> findById(Long id);

    void deleteById(Long id);
}