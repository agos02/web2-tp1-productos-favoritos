package ar.edu.unvime.apiblank.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ar.edu.unvime.apiblank.dto.LoginRequestDto;
import ar.edu.unvime.apiblank.dto.LoginResponseDto;
import ar.edu.unvime.apiblank.dto.RegistroRequestDto;
import ar.edu.unvime.apiblank.dto.UsuarioResponseDto;
import ar.edu.unvime.apiblank.exception.CredencialesInvalidasException;
import ar.edu.unvime.apiblank.exception.EmailYaRegistradoException;
import ar.edu.unvime.apiblank.model.Rol;
import ar.edu.unvime.apiblank.model.Usuario;
import ar.edu.unvime.apiblank.repository.UsuarioRepository;
import ar.edu.unvime.apiblank.security.JwtService;

/** Contiene la lógica de negocio del registro y el login de usuarios. */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              PasswordEncoder passwordEncoder,
                              JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UsuarioResponseDto registrar(RegistroRequestDto request) {
        // Se normaliza el email para que "Ana@mail.com" y "ana@mail.com" no cuenten como usuarios distintos.
        String email = normalizar(request.email());

        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailYaRegistradoException(email);
        }

        // La contraseña nunca se guarda en texto plano: se guarda solo su hash.
        String hash = passwordEncoder.encode(request.password());

        Usuario guardado = usuarioRepository.save(new Usuario(null, email, hash, Rol.USER));
        return new UsuarioResponseDto(guardado.getId(), guardado.getEmail(), guardado.getRol());
    }

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        // Si el email no existe o la contraseña no coincide se lanza el mismo error,
        // para no revelar qué emails están registrados.
        Usuario usuario = usuarioRepository.findByEmail(normalizar(request.email()))
                .orElseThrow(CredencialesInvalidasException::new);

        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }

        return new LoginResponseDto(jwtService.generarToken(usuario), "Bearer");
    }

    private String normalizar(String email) {
        return email.trim().toLowerCase();
    }
}