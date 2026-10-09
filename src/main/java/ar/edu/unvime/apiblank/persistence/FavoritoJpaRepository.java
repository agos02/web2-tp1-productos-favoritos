package ar.edu.unvime.apiblank.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/** Interfaz que Spring Data implementa sola para leer y escribir FavoritoEntity en PostgreSQL. */
public interface FavoritoJpaRepository extends JpaRepository<FavoritoEntity, Long> {
}