package ar.edu.unvime.apiblank.integracion;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Base de los tests de integración: levanta la aplicación completa contra un PostgreSQL real
 * (un contenedor descartable) y Flyway corre todas las migraciones sobre esa base limpia.
 */
@SpringBootTest
@AutoConfigureMockMvc
abstract class BaseIntegracionTest {

    // Un único contenedor compartido por todos los tests de integración: se levanta una sola vez
    // (arrancar uno por clase de test sería mucho más lento) y se descarta al terminar la ejecución.
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17");

    static {
        POSTGRES.start();
    }

    /** Apunta la conexión de la aplicación al contenedor en lugar de a la base local. */
    @DynamicPropertySource
    static void configurarBaseDeDatos(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}