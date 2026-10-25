package ar.edu.unvime.apiblank.repository;

import java.util.Optional;

import ar.edu.unvime.apiblank.model.Usuario;

/** Define las operaciones de persistencia para Usuario, sin comprometerse a una implementación concreta. */
public interface UsuarioRepository {

    Usuario save(Usuario usuario);

    Optional<Usuario> findById(Long id);

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}