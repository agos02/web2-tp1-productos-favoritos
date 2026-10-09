# TP1 - API Productos y Favoritos (Web 2)

API REST con Spring Boot que expone:
- Catálogo de productos, consumiendo la API externa DummyJSON.
- CRUD de favoritos, con persistencia en memoria (sin base de datos).

## Estado
Completo

## Requisitos
- Java 25
- Maven (no hace falta instalarlo: el proyecto incluye Maven Wrapper)

## Cómo levantar el proyecto

Windows:
```bash
.\mvnw.cmd spring-boot:run
```

macOS/Linux:
```bash
./mvnw spring-boot:run
```

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

## Pruebas
El archivo `requests.http` contiene casos de éxito y error para los tres recursos (health, productos, favoritos). Se puede ejecutar desde VS Code con la extensión REST Client.

## Persistencia: de memoria a JPA (puertos y adaptadores)

En el TP1 los favoritos se guardaban en un `Map` en memoria. En el TP2 pasan a PostgreSQL usando JPA/Hibernate.

**Clases agregadas o eliminadas (solo en la capa de persistencia):**
- Agregadas: `FavoritoEntity`, `FavoritoJpaRepository` y `FavoritoRepositoryAdapter` (paquete `persistence`).
- Eliminada: `FavoritoRepositoryImpl`, el repository en memoria.
- Infraestructura nueva: `docker-compose.yml`, dependencias en `pom.xml`, datos de conexión en `application.properties` y la migración `V1__create_favoritos.sql`.

**Clases que quedaron exactamente iguales:** `Favorito` (dominio), `FavoritoRepository` (puerto), `FavoritoService`, `FavoritoController`, `FavoritoRequestDto`, `FavoritoResponseDto` y `GlobalExceptionHandler`.

**Por qué fue posible:** `FavoritoRepository` es un *puerto*: un contrato que define qué operaciones necesita la aplicación (guardar, listar, buscar, borrar) sin decir cómo se hacen. `FavoritoService` depende solo de esa interfaz, así que no sabe si detrás hay un `Map` o una base de datos. La versión en memoria del TP1 y `FavoritoRepositoryAdapter` son *adapters*: implementaciones concretas e intercambiables de ese mismo contrato. Para cambiar de almacenamiento alcanzó con escribir un adapter nuevo y borrar el viejo, sin modificar el dominio, el Service ni el Controller.