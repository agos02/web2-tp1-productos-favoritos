package ar.edu.unvime.apiblank.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.unvime.apiblank.dto.ListaRequestDto;
import ar.edu.unvime.apiblank.exception.AccesoDenegadoException;
import ar.edu.unvime.apiblank.model.Lista;
import ar.edu.unvime.apiblank.model.Rol;
import ar.edu.unvime.apiblank.repository.FavoritoRepository;
import ar.edu.unvime.apiblank.repository.ListaRepository;
import ar.edu.unvime.apiblank.security.UsuarioAutenticado;

/** Pruebas unitarias de la lógica de negocio de las listas, sin base de datos ni servidor. */
@ExtendWith(MockitoExtension.class)
class ListaServiceImplTest {

    @Mock
    private ListaRepository listaRepository;

    @Mock
    private FavoritoRepository favoritoRepository;

    private ListaServiceImpl listaService;

    @BeforeEach
    void setUp() {
        // ControlAccesoListas es una clase simple sin dependencias: se usa la real, no un doble.
        listaService = new ListaServiceImpl(listaRepository, favoritoRepository, new ControlAccesoListas());
    }

    @Test
    void crearAsociaLaListaAlUsuarioAutenticado() {
        UsuarioAutenticado usuario = new UsuarioAutenticado(7L, Rol.USER);
        when(listaRepository.save(any(Lista.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        listaService.crear(new ListaRequestDto("Regalos"), usuario);

        ArgumentCaptor<Lista> captor = ArgumentCaptor.forClass(Lista.class);
        verify(listaRepository).save(captor.capture());
      //corregimos a como estaba antes:
        assertEquals(7L, captor.getValue().getUsuarioId());
        assertEquals("Regalos", captor.getValue().getNombre());
    }

    @Test
    void obtenerListaAjenaLanzaAccesoDenegado() {
        Lista listaDeOtro = new Lista(5L, "Lista de otro", 1L);
        when(listaRepository.findById(5L)).thenReturn(Optional.of(listaDeOtro));
        UsuarioAutenticado usuario = new UsuarioAutenticado(2L, Rol.USER);

        assertThrows(AccesoDenegadoException.class, () -> listaService.obtenerPorId(5L, usuario));
    }

    @Test
    void adminPuedeObtenerListaAjena() {
        Lista listaDeOtro = new Lista(5L, "Lista de otro", 1L);
        when(listaRepository.findById(5L)).thenReturn(Optional.of(listaDeOtro));
        UsuarioAutenticado admin = new UsuarioAutenticado(99L, Rol.ADMIN);

        assertDoesNotThrow(() -> listaService.obtenerPorId(5L, admin));
    }
}