package ar.edu.unvime.apiblank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ar.edu.unvime.apiblank.dto.RegistroRequestDto;
import ar.edu.unvime.apiblank.exception.EmailYaRegistradoException;
import ar.edu.unvime.apiblank.model.Rol;
import ar.edu.unvime.apiblank.model.Usuario;
import ar.edu.unvime.apiblank.repository.UsuarioRepository;
import ar.edu.unvime.apiblank.security.JwtService;

/** Pruebas unitarias del registro de usuarios, sin base de datos ni servidor. */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UsuarioServiceImpl usuarioService;

    @BeforeEach
    void setUp() {
        // Se usan el encoder y el JwtService reales: son clases simples y así el test verifica un hash de verdad.
        JwtService jwtService = new JwtService("secreto-de-prueba-de-al-menos-32-caracteres-123", 60);
        usuarioService = new UsuarioServiceImpl(usuarioRepository, passwordEncoder, jwtService);
    }

    @Test
    void registrarNuncaGuardaLaContrasenaEnTextoPlano() {
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        usuarioService.registrar(new RegistroRequestDto("ana@mail.com", "clave12345"));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        String guardado = captor.getValue().getPasswordHash();

        assertNotEquals("clave12345", guardado);
        assertTrue(passwordEncoder.matches("clave12345", guardado));
    }

    @Test
    void registrarAsignaRolUserPorDefecto() {
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        usuarioService.registrar(new RegistroRequestDto("ana@mail.com", "clave12345"));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals(Rol.USER, captor.getValue().getRol());
    }

    @Test
    void registrarConEmailExistenteLanzaExcepcionYNoGuarda() {
        when(usuarioRepository.existsByEmail("ana@mail.com")).thenReturn(true);

        assertThrows(EmailYaRegistradoException.class,
                () -> usuarioService.registrar(new RegistroRequestDto("ana@mail.com", "clave12345")));

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}