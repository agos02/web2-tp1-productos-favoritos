package ar.edu.unvime.apiblank.repository;

import ar.edu.unvime.apiblank.model.Favorito;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** Implementación en memoria de FavoritoRepository, usando un mapa como almacenamiento temporal. */

@Repository
public class FavoritoRepositoryImpl implements FavoritoRepository {

    private final Map<Long, Favorito> favoritos = new ConcurrentHashMap<>();
    private final AtomicLong secuenciaId = new AtomicLong(1);

    @Override
    public Favorito save(Favorito favorito) {
        if (favorito.getId() == null) {
            favorito.setId(secuenciaId.getAndIncrement());
        }
        favoritos.put(favorito.getId(), favorito);
        return favorito;
    }

    @Override
    public List<Favorito> findAll() {
        return List.copyOf(favoritos.values());
    }

    @Override
    public Optional<Favorito> findById(Long id) {
        return Optional.ofNullable(favoritos.get(id));
    }

    @Override
    public void deleteById(Long id) {
        favoritos.remove(id);
    }
}