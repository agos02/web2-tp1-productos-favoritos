package ar.edu.unvime.apiblank.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import ar.edu.unvime.apiblank.model.Usuario;
import ar.edu.unvime.apiblank.repository.UsuarioRepository;

/** Adapter que cumple el puerto UsuarioRepository guardando los usuarios en PostgreSQL mediante JPA. */
@Repository
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity guardado = jpaRepository.save(aEntidad(usuario));
        return aDominio(guardado);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return jpaRepository.findById(id).map(this::aDominio);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::aDominio);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    private UsuarioEntity aEntidad(Usuario usuario) {
        return new UsuarioEntity(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getPasswordHash(),
                usuario.getRol()
        );
    }

    private Usuario aDominio(UsuarioEntity entidad) {
        return new Usuario(
                entidad.getId(),
                entidad.getEmail(),
                entidad.getPasswordHash(),
                entidad.getRol()
        );
    }
}