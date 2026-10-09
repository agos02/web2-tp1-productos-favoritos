package ar.edu.unvime.apiblank.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ar.edu.unvime.apiblank.model.Favorito;
import ar.edu.unvime.apiblank.repository.FavoritoRepository;

/** Adapter que cumple el puerto FavoritoRepository guardando los favoritos en PostgreSQL mediante JPA. */
@Repository
public class FavoritoRepositoryAdapter implements FavoritoRepository {

    private final FavoritoJpaRepository jpaRepository;

    public FavoritoRepositoryAdapter(FavoritoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Favorito save(Favorito favorito) {
        FavoritoEntity guardada = jpaRepository.save(aEntidad(favorito));
        return aDominio(guardada);
    }

    @Override
    public List<Favorito> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::aDominio)
                .toList();
    }

    @Override
    public Optional<Favorito> findById(Long id) {
        return jpaRepository.findById(id).map(this::aDominio);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private FavoritoEntity aEntidad(Favorito favorito) {
        return new FavoritoEntity(
                favorito.getId(),
                favorito.getProductoId(),
                favorito.getNota(),
                favorito.getFechaAgregado()
        );
    }

    private Favorito aDominio(FavoritoEntity entidad) {
        return new Favorito(
                entidad.getId(),
                entidad.getProductoId(),
                entidad.getNota(),
                entidad.getFechaAlta()
        );
    }
}