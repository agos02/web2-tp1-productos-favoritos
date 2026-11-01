package ar.edu.unvime.apiblank.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/** Interfaz que Spring Data implementa sola para leer y escribir ListaEntity en PostgreSQL. */
public interface ListaJpaRepository extends JpaRepository<ListaEntity, Long> {

    List<ListaEntity> findByUsuarioId(Long usuarioId);
}