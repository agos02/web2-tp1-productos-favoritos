# TP2 - API Productos, Favoritos y Listas (Web 2)

API REST con Spring Boot que expone:
- Catálogo de productos, consumiendo la API externa DummyJSON (solo lectura, sin persistencia propia).
- CRUD de favoritos, persistido en PostgreSQL mediante JPA/Hibernate.
- Listas para organizar favoritos, con una operación transaccional para mover favoritos entre listas.

## Estado
Completo

## Requisitos
- Java 25
- Docker (para PostgreSQL) o una instalación local de PostgreSQL
- Maven (no hace falta instalarlo: el proyecto incluye Maven Wrapper)

## Cómo levantar PostgreSQL

La forma recomendada es Docker Compose. Desde la raíz del proyecto:

```bash
docker compose up -d
```

Esto levanta PostgreSQL 17 con estos datos de conexión (los mismos que usa `application.properties`):

| Dato | Valor |
|---|---|
| Base | `webii_tp2` |
| Usuario | `webii_tp2` |
| Contraseña | `webii_tp2` |
| Puerto | `5432` |

Si no se puede usar Docker, sirve una instalación local de PostgreSQL con esos mismos datos de conexión.

Para empezar con la base vacía (borra todos los datos):

```bash
docker compose down -v
docker compose up -d
```

## Cómo levantar la aplicación

Windows:
```bash
.\mvnw.cmd spring-boot:run
```

macOS/Linux:
```bash
./mvnw spring-boot:run
```

## Cómo confirmar que las migraciones corrieron

Al arrancar, el log de Flyway debe mostrar algo como:

```
Successfully applied 4 migrations to schema "public", now at version v4
```

También se puede consultar la tabla que Flyway crea sola:

```bash
docker exec -it demo-postgres psql -U webii_tp2 -d webii_tp2 -c "SELECT version, description, success FROM flyway_schema_history;"
```

Deben aparecer 4 filas con `success = t`.

## Migraciones

El esquema lo maneja Flyway (`spring.jpa.hibernate.ddl-auto=validate`), no Hibernate. Las migraciones están en `src/main/resources/db/migration`:

| Archivo | Qué hace |
|---|---|
| `V1__create_favoritos.sql` | Crea la tabla `favoritos` |
| `V2__create_listas.sql` | Crea la tabla `listas` |
| `V3__add_lista_id_a_favoritos.sql` | Agrega `lista_id` (admite NULL) a `favoritos`, con clave foránea a `listas` |
| `V4__lista_id_obligatorio.sql` | Crea la lista "Sin clasificar", se la asigna a los favoritos sin lista y vuelve `lista_id` NOT NULL |

## Documentación de la API
Una vez levantado el proyecto, Swagger UI disponible en:
`http://localhost:8080/swagger-ui.html`

## Endpoints

### Health check
- `GET /health`

### Productos
- `GET /api/productos`
- `GET /api/productos/{id}`

### Favoritos
- `POST /api/favoritos`
- `GET /api/favoritos`
- `GET /api/favoritos/{id}`
- `PUT /api/favoritos/{id}`
- `DELETE /api/favoritos/{id}`

### Listas
| Operación | Método | Código de éxito |
|---|---|---|
| Crear lista | `POST /api/listas` | 201 |
| Listar listas | `GET /api/listas` | 200 |
| Obtener una lista | `GET /api/listas/{id}` | 200 |
| Favoritos de una lista | `GET /api/listas/{id}/favoritos` | 200 |
| Eliminar una lista vacía | `DELETE /api/listas/{id}` | 204 (409 si tiene favoritos) |
| Mover favoritos a otra lista | `POST /api/listas/{origenId}/mover-favoritos` | 204 |

El cuerpo de `mover-favoritos` es `{ "listaDestinoId": 4 }`. Mueve todos los favoritos de la lista origen a la destino y elimina la lista origen. Responde 404 si alguna de las dos listas no existe y 400 si origen y destino son la misma lista.

## Pruebas
El archivo `requests.http` contiene casos de éxito y error para todos los recursos (health, productos, favoritos, listas y mover favoritos). Se puede ejecutar desde VS Code con la extensión REST Client, de arriba hacia abajo y sobre una base limpia, porque los ids de los requests asumen que las listas se crean primero.

## Persistencia: de memoria a JPA (puertos y adaptadores)

En el TP1 los favoritos se guardaban en un `Map` en memoria. En el TP2 pasan a PostgreSQL usando JPA/Hibernate.

**Clases agregadas o eliminadas (solo en la capa de persistencia):**
- Agregadas: `FavoritoEntity`, `FavoritoJpaRepository` y `FavoritoRepositoryAdapter` (paquete `persistence`).
- Eliminada: `FavoritoRepositoryImpl`, el repository en memoria.
- Infraestructura nueva: `docker-compose.yml`, dependencias en `pom.xml`, datos de conexión en `application.properties` y la migración `V1__create_favoritos.sql`.

**Clases que quedaron exactamente iguales en esa migración de memoria a JPA:** `Favorito` (dominio), `FavoritoRepository` (puerto), `FavoritoService`, `FavoritoController`, `FavoritoRequestDto`, `FavoritoResponseDto` y `GlobalExceptionHandler`. Más adelante, al agregar las listas (punto 5), se les sumó `listaId`, pero eso es un cambio de dominio, no de persistencia.

**Por qué fue posible:** `FavoritoRepository` es un *puerto*: un contrato que define qué operaciones necesita la aplicación (guardar, listar, buscar, borrar) sin decir cómo se hacen. `FavoritoService` depende solo de esa interfaz, así que no sabe si detrás hay un `Map` o una base de datos. La versión en memoria del TP1 y `FavoritoRepositoryAdapter` son *adapters*: implementaciones concretas e intercambiables de ese mismo contrato. Para cambiar de almacenamiento alcanzó con escribir un adapter nuevo y borrar el viejo, sin modificar el dominio, el Service ni el Controller.

## Evolución del esquema: por qué V4 es una migración nueva

Los favoritos creados antes de agregar las listas no tienen `lista_id`, y no se puede declarar una columna `NOT NULL` si ya hay filas nulas. Se resolvió con la migración nueva `V4`, que en orden crea la lista "Sin clasificar", se la asigna a los favoritos sin lista y recién ahí pone la columna `NOT NULL`.

No se editó V1, V2 ni V3 porque Flyway guarda un checksum de cada migración ya aplicada, y si el archivo cambia se niega a arrancar. Además, en otras bases (la de un compañero, la de producción) esas migraciones ya corrieron y no se volverían a ejecutar, así que el cambio solo existiría en una máquina. Una migración nueva es la única forma de que todas las bases evolucionen igual y quede un historial versionado.

## Transacciones: mover favoritos entre listas

`ListaService.moverFavoritos` hace varias escrituras: reasigna cada favorito de la lista origen a la destino y después elimina la lista origen. Está marcado con `@Transactional`, así que Spring las ejecuta como una sola unidad: si una falla, todas se deshacen y la base queda como estaba.

**Qué pasaría sin `@Transactional`:** cada `save` y el `deleteById` se confirmarían (commit) por separado, cada uno en su propia transacción. Si se movieran, por ejemplo, 2 de 5 favoritos y después ocurriera un error (caída de la base, una excepción, un corte de conexión), esos 2 quedarían en la lista destino y los otros 3 en la origen, y la lista origen seguiría existiendo. La base quedaría en un estado intermedio que no corresponde ni a "antes" ni a "después" de la operación.

Relacionado con ACID:
- **Atomicidad:** es la propiedad que se rompería. Una operación de varios pasos debe aplicarse completa o no aplicarse nada; sin la transacción habría cambios parciales confirmados.
- **Consistencia:** la base pasaría por un estado que el negocio no admite (favoritos repartidos entre dos listas por una operación que se pretendía indivisible).
- **Aislamiento:** otro cliente podría consultar en medio de la operación y ver una parte de los favoritos ya movidos y otra no.
- **Durabilidad:** los cambios parciales quedarían guardados de forma permanente, por lo que no se podrían descartar solos.

Con `@Transactional` ninguno de esos estados intermedios es visible ni queda guardado.