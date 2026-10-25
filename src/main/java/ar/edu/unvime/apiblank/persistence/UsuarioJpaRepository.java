package ar.edu.unvime.apiblank.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/** Interfaz que Spring Data implementa sola para leer y escribir UsuarioEntity en PostgreSQL. */
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}