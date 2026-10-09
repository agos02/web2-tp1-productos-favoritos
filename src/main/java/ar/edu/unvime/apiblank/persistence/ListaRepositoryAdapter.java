package ar.edu.unvime.apiblank.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ar.edu.unvime.apiblank.model.Lista;
import ar.edu.unvime.apiblank.repository.ListaRepository;

/** Adapter que cumple el puerto ListaRepository guardando las listas en PostgreSQL mediante JPA. */
@Repository
public class ListaRepositoryAdapter implements ListaRepository {

    private final ListaJpaRepository jpaRepository;

    public ListaRepositoryAdapter(ListaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Lista save(Lista lista) {
        ListaEntity guardada = jpaRepository.save(aEntidad(lista));
        return aDominio(guardada);
    }

    @Override
    public List<Lista> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::aDominio)
                .toList();
    }

    @Override
    public Optional<Lista> findById(Long id) {
        return jpaRepository.findById(id).map(this::aDominio);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private ListaEntity aEntidad(Lista lista) {
        return new ListaEntity(lista.getId(), lista.getNombre());
    }

    private Lista aDominio(ListaEntity entidad) {
        return new Lista(entidad.getId(), entidad.getNombre());
    }
}