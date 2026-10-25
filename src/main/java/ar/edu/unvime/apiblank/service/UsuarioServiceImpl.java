package ar.edu.unvime.apiblank.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ar.edu.unvime.apiblank.dto.RegistroRequestDto;
import ar.edu.unvime.apiblank.dto.UsuarioResponseDto;
import ar.edu.unvime.apiblank.exception.EmailYaRegistradoException;
import ar.edu.unvime.apiblank.model.Rol;
import ar.edu.unvime.apiblank.model.Usuario;
import ar.edu.unvime.apiblank.repository.UsuarioRepository;

/** Contiene la lógica de negocio del registro de usuarios. */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioResponseDto registrar(RegistroRequestDto request) {
        // Se normaliza el email para que "Ana@mail.com" y "ana@mail.com" no cuenten como usuarios distintos.
        String email = request.email().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailYaRegistradoException(email);
        }

        // La contraseña nunca se guarda en texto plano: se guarda solo su hash.
        String hash = passwordEncoder.encode(request.password());

        Usuario guardado = usuarioRepository.save(new Usuario(null, email, hash, Rol.USER));
        return new UsuarioResponseDto(guardado.getId(), guardado.getEmail(), guardado.getRol());
    }
}