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