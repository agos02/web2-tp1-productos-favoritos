package ar.edu.unvime.apiblank.integracion;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;

/**
 * Pruebas de integración de autenticación y autorización: aplicación completa, PostgreSQL real
 * (Testcontainers) y migraciones corridas sobre una base limpia.
 */
class AutorizacionIntegracionTest extends BaseIntegracionTest {

    private static final String PASSWORD = "clave12345";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void loginConCredencialesValidasDevuelve200ConToken() throws Exception {
        String email = emailUnico();
        registrar(email);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credenciales(email, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void loginConCredencialesInvalidasDevuelve401() throws Exception {
        String email = emailUnico();
        registrar(email);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credenciales(email, "contrasena-equivocada")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accederAUnaListaPropiaDevuelve200() throws Exception {
        String email = emailUnico();
        registrar(email);
        String token = login(email);
        long listaId = crearLista(token, "Mi lista");

        mockMvc.perform(get("/api/listas/" + listaId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Mi lista"));
    }

    @Test
    void accederAUnaListaAjenaDevuelve403() throws Exception {
        String emailDueno = emailUnico();
        registrar(emailDueno);
        long listaId = crearLista(login(emailDueno), "Lista del dueño");

        String emailOtro = emailUnico();
        registrar(emailOtro);
        String tokenOtro = login(emailOtro);

        mockMvc.perform(get("/api/listas/" + listaId)
                        .header("Authorization", "Bearer " + tokenOtro))
                .andExpect(status().isForbidden());
    }

    @Test
    void accederAListasSinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/api/listas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminPuedeAccederAUnaListaAjena() throws Exception {
        String emailDueno = emailUnico();
        registrar(emailDueno);
        long listaId = crearLista(login(emailDueno), "Lista del dueño");

        String emailAdmin = emailUnico();
        registrar(emailAdmin);
        // El registro siempre crea usuarios USER: el ADMIN se asigna directo en la base.
        // El login va DESPUÉS, porque el rol viaja adentro del token.
        jdbcTemplate.update("UPDATE usuarios SET rol = 'ADMIN' WHERE email = ?", emailAdmin);
        String tokenAdmin = login(emailAdmin);

        mockMvc.perform(get("/api/listas/" + listaId)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    // ------------------------------------------------------------------
    // Métodos auxiliares
    // ------------------------------------------------------------------

    /** Cada test usa emails propios para no chocar con los datos de otros tests (la base se comparte). */
    private String emailUnico() {
        return "test-" + UUID.randomUUID() + "@mail.com";
    }

    private String credenciales(String email, String password) {
        return """
                {"email":"%s","password":"%s"}""".formatted(email, password);
    }

    private void registrar(String email) throws Exception {
        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credenciales(email, PASSWORD)))
                .andExpect(status().isCreated());
    }

    private String login(String email) throws Exception {
        String respuesta = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credenciales(email, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(respuesta, "$.token");
    }

    private long crearLista(String token, String nombre) throws Exception {
        String respuesta = mockMvc.perform(post("/api/listas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"%s"}""".formatted(nombre)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Number id = JsonPath.read(respuesta, "$.id");
        return id.longValue();
    }
}