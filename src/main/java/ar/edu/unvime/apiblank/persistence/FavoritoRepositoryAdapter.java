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
    private final ListaJpaRepository listaJpaRepository;

    public FavoritoRepositoryAdapter(FavoritoJpaRepository jpaRepository, ListaJpaRepository listaJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.listaJpaRepository = listaJpaRepository;
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
    public List<Favorito> findByListaId(Long listaId) {
        return jpaRepository.findByListaId(listaId).stream()
                .map(this::aDominio)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private FavoritoEntity aEntidad(Favorito favorito) {
        // getReferenceById arma una referencia a la lista usando solo su id, sin consultar la base.
        // Puede ser null en los favoritos viejos, que todavía no tienen lista.
        ListaEntity lista = favorito.getListaId() != null
                ? listaJpaRepository.getReferenceById(favorito.getListaId())
                : null;

        return new FavoritoEntity(
                favorito.getId(),
                favorito.getProductoId(),
                favorito.getNota(),
                favorito.getFechaAgregado(),
                lista
        );
    }

    private Favorito aDominio(FavoritoEntity entidad) {
        Long listaId = entidad.getLista() != null ? entidad.getLista().getId() : null;

        return new Favorito(
                entidad.getId(),
                entidad.getProductoId(),
                entidad.getNota(),
                entidad.getFechaAlta(),
                listaId
        );
    }
}