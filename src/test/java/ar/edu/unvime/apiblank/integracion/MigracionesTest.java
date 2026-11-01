package ar.edu.unvime.apiblank.integracion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/** Comprueba que Flyway aplica todas las migraciones sobre una base completamente limpia. */
class MigracionesTest extends BaseIntegracionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void flywayAplicaTodasLasMigracionesSobreUnaBaseLimpia() {
        Integer aplicadas = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history WHERE success = true", Integer.class);
        Integer fallidas = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history WHERE success = false", Integer.class);

        assertTrue(aplicadas >= 6, "Se esperaban al menos 6 migraciones aplicadas y hubo " + aplicadas);
        assertEquals(0, fallidas);
    }
}