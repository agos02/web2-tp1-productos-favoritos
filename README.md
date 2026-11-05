# TP3 - API Productos, Favoritos y Listas con seguridad (Web 2)

API REST con Spring Boot que expone:
- Catálogo de productos, consumiendo la API externa DummyJSON (público, solo lectura).
- Registro y login de usuarios, con contraseñas hasheadas y tokens JWT.
- Listas y favoritos persistidos en PostgreSQL, donde cada usuario solo opera sobre lo suyo (los ADMIN pueden ver todo).

## Estado
Completo

## Requisitos
- Java 25
- Docker (para PostgreSQL local y para los tests de integración)
- Maven (no hace falta instalarlo: el proyecto incluye Maven Wrapper)

## Cómo levantar PostgreSQL local

Desde la raíz del proyecto:

```bash
docker compose up -d
```

Levanta PostgreSQL 17 con estos datos de conexión (los mismos que usa `application.properties` por defecto):

| Dato | Valor |
|---|---|
| Base | `webii_tp2` |
| Usuario | `webii_tp2` |
| Contraseña | `webii_tp2` |
| Puerto | `5432` |

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

## Variables de entorno

Los datos sensibles no están fijos en el código: `application.properties` los lee de variables de entorno y, si no existen, usa un valor por defecto que sirve solo para desarrollo local.

| Variable | Para qué sirve | Valor por defecto (solo local) |
|---|---|---|
| `DB_URL` | URL JDBC de la base de datos | `jdbc:postgresql://localhost:5432/webii_tp2` |
| `DB_USERNAME` | Usuario de la base | `webii_tp2` |
| `DB_PASSWORD` | Contraseña de la base | `webii_tp2` |
| `JWT_SECRET` | Clave con la que se firman los tokens (mínimo 32 caracteres) | una clave de desarrollo |
| `JWT_EXPIRATION_MINUTES` | Duración de los tokens | `60` |
| `PORT` | Puerto HTTP (lo define el proveedor en la nube) | `8080` |

En producción hay que definir siempre `JWT_SECRET`: el valor por defecto es público porque está en el repositorio.

## Documentación de la API
Una vez levantado el proyecto, Swagger UI está disponible en:
`http://localhost:8080/swagger-ui.html`

## Endpoints

### Públicos (sin token)
- `GET /health`
- `GET /api/productos`
- `GET /api/productos/{id}`
- `POST /api/auth/registro`: crea un usuario con rol `USER` (409 si el email ya existe)
- `POST /api/auth/login`: devuelve un token JWT (401 si las credenciales son incorrectas)

### Protegidos (requieren `Authorization: Bearer <token>`)

| Operación | Método | Éxito | Errores |
|---|---|---|---|
| Crear lista (el dueño es el usuario autenticado) | `POST /api/listas` | 201 | 401 |
| Listar listas (las propias; todas si es ADMIN) | `GET /api/listas` | 200 | 401 |
| Obtener una lista | `GET /api/listas/{id}` | 200 | 401, 403, 404 |
| Favoritos de una lista | `GET /api/listas/{id}/favoritos` | 200 | 401, 403, 404 |
| Eliminar una lista vacía | `DELETE /api/listas/{id}` | 204 | 401, 403, 404, 409 |
| Mover favoritos a otra lista | `POST /api/listas/{origenId}/mover-favoritos` | 204 | 400, 401, 403, 404 |
| Crear favorito | `POST /api/favoritos` | 201 | 400, 401, 403, 404 |
| Listar favoritos (los propios; todos si es ADMIN) | `GET /api/favoritos` | 200 | 401 |
| Obtener un favorito | `GET /api/favoritos/{id}` | 200 | 401, 403, 404 |
| Actualizar un favorito | `PUT /api/favoritos/{id}` | 200 | 400, 401, 403, 404 |
| Eliminar un favorito | `DELETE /api/favoritos/{id}` | 204 | 401, 403, 404 |

Significado de los errores de seguridad:
- **401 Unauthorized:** falta el token, es inválido o está vencido.
- **403 Forbidden:** el token es válido pero la lista no es del usuario y este no es ADMIN.
- Cuando el recurso no existe se responde 404 antes de evaluar el permiso, así "no existe" y "no es tuya" no se confunden.

### Cómo probar los endpoints protegidos
1. `POST /api/auth/registro` con `{ "email": "...", "password": "..." }` (la contraseña debe tener al menos 8 caracteres).
2. `POST /api/auth/login` con las mismas credenciales: devuelve `{ "token": "...", "tipo": "Bearer" }`.
3. Enviar `Authorization: Bearer <token>` en cada pedido.

El archivo `requests.http` contiene todos estos pasos, incluidas las pruebas de acceso permitido y denegado con tres usuarios (Ana, Beto y un ADMIN). Se ejecuta desde VS Code con la extensión REST Client, de arriba hacia abajo.

### Cómo crear un usuario ADMIN
El registro siempre crea usuarios `USER`, para que nadie pueda registrarse como administrador. El rol se asigna directamente en la base:

```bash
docker exec -it demo-postgres psql -U webii_tp2 -d webii_tp2 -c "UPDATE usuarios SET rol = 'ADMIN' WHERE email = 'admin@mail.com';"
```

El rol viaja dentro del token, por lo que el usuario debe iniciar sesión **después** del cambio para recibir un token con el rol nuevo.

## Migraciones

El esquema lo maneja Flyway (`spring.jpa.hibernate.ddl-auto=validate`), no Hibernate. Las migraciones están en `src/main/resources/db/migration`:

| Archivo | Qué hace |
|---|---|
| `V1__create_favoritos.sql` | Crea la tabla `favoritos` |
| `V2__create_listas.sql` | Crea la tabla `listas` |
| `V3__add_lista_id_a_favoritos.sql` | Agrega `lista_id` (admite NULL) a `favoritos`, con clave foránea a `listas` |
| `V4__lista_id_obligatorio.sql` | Crea la lista "Sin clasificar", se la asigna a los favoritos sin lista y vuelve `lista_id` NOT NULL |
| `V5__create_usuarios.sql` | Crea la tabla `usuarios` (email único, hash de contraseña, rol) |
| `V6__add_usuario_id_a_listas.sql` | Crea el usuario "sistema", le asigna las listas existentes y agrega `usuario_id` NOT NULL a `listas` |

Para confirmar que corrieron:

```bash
docker exec -it demo-postgres psql -U webii_tp2 -d webii_tp2 -c "SELECT version, description, success FROM flyway_schema_history;"
```

Deben aparecer 6 filas con `success = t`.

## Dónde vive la regla de autorización

La regla ("una lista solo la puede operar su dueño o un ADMIN") vive en la **capa de Service**, concentrada en la clase `ControlAccesoListas`, que usan `ListaServiceImpl` y `FavoritoService` en cada operación. Los favoritos no tienen dueño propio: se autorizan a través de la lista a la que pertenecen.

Por qué ahí y no en otro lado:

- **No en el Controller:** el Controller solo traduce HTTP (rutas, códigos de estado, JSON). Si la regla estuviera ahí, cualquier otra entrada a la lógica de negocio (otro controller, una tarea programada, un test) podría saltearla. En el Service la regla se aplica sin importar quién llame al método.
- **No en el filtro JWT:** el filtro solo puede saber *quién* es el usuario (el token trae su id y su rol). Decidir si una lista es suya exige consultar la lista en la base para ver quién es el dueño, y eso es lógica de negocio, no de autenticación.
- **No con anotaciones tipo `@PreAuthorize`:** funcionarían para reglas por rol, pero para la regla por propiedad hay que cargar el recurso antes de decidir, de modo que terminaría igualmente implementada en el Service y con una capa extra de configuración.
- **Testeable sin HTTP:** al estar en el Service, la regla se prueba con tests unitarios simples (ver `ListaServiceImplTest`), sin levantar servidor ni base de datos.

La identidad del usuario llega al Service como parámetro (`UsuarioAutenticado`, con id y rol), que el Controller obtiene del token con `@AuthenticationPrincipal`. Al crear una lista, el dueño es siempre ese usuario: nunca un dato que mande el cliente.

## Tests

Hay 13 tests:

| Clase | Tipo | Cantidad | Qué cubre |
|---|---|---|---|
| `ListaServiceImplTest` | Unitario | 3 | La lista creada queda asociada al usuario autenticado; acceso a lista ajena (403) y excepción del ADMIN |
| `UsuarioServiceImplTest` | Unitario | 3 | Nunca se guarda la contraseña en texto plano; rol `USER` por defecto; email repetido |
| `MigracionesTest` | Integración | 1 | Flyway aplica todas las migraciones sobre una base limpia |
| `AutorizacionIntegracionTest` | Integración | 6 | Login válido (200), login inválido (401), lista propia (200), lista ajena (403), sin token (401), ADMIN sobre lista ajena (200) |

Los tests de integración usan **Testcontainers**: levantan un PostgreSQL real y descartable en Docker, y Flyway corre todas las migraciones contra esa base limpia en cada ejecución. No tocan la base local de `docker-compose`.

Para correrlos (con Docker abierto):

```bash
./mvnw test
```

Para correr una sola clase:

```bash
./mvnw test -Dtest=ListaServiceImplTest
```

El resultado esperado termina con `Tests run: 13, Failures: 0, Errors: 0` y `BUILD SUCCESS`.

## Integración continua (GitHub Actions)

El workflow `.github/workflows/ci.yml` se ejecuta en cada push y en cada pull request: instala Java 25, compila el proyecto y corre `./mvnw test`, que incluye las migraciones contra una base limpia (Testcontainers) y todos los tests anteriores.

Cómo leer el resultado:
1. Abrir la pestaña **Actions** del repositorio: https://github.com/agos02/web2-tp1-productos-favoritos/actions
2. Cada corrida muestra el mensaje de su commit y un estado: tilde verde (todo pasó), cruz roja (algo falló) o círculo amarillo (en curso).
3. Al abrir una corrida y el job `compilar-y-testear`, el paso **Correr los tests** muestra el detalle. Si hubo una falla, ahí figura el nombre del test que falló. Al final del paso exitoso aparece `Tests run: 13, Failures: 0, Errors: 0`.

Verificación de que el CI detecta errores: se rompió un test a propósito (corrida #2, cruz roja) y se arregló en el commit siguiente (corrida #3, tilde verde).

## Despliegue

La aplicación está desplegada en **Render** (servicio web con Docker, plan gratuito) usando una base **PostgreSQL en Neon** (plan gratuito). Se descartó Koyeb, sugerido en la consigna, porque su consola solo mostraba un aviso de transición y no permitía crear servicios.

- URL pública: https://web2-practica.onrender.com
- Estado: `GET https://web2-practica.onrender.com/health`

El plan gratuito de Render duerme el servicio tras 15 minutos sin tráfico: la primera petición después de ese tiempo puede tardar cerca de un minuto.

Pasos que se siguieron:

1. **Preparar la app:** se agregó `server.port=${PORT:8080}` a `application.properties`, un `Dockerfile` multietapa (compila con Maven y Java 25, y ejecuta el jar en una imagen liviana) y un `.dockerignore`. Se verificó con `docker build` y `docker run` en local.
2. **Crear la base remota en Neon:** proyecto en la región de São Paulo, PostgreSQL 17, con la conexión directa (sin *connection pooling*, porque Flyway necesita una conexión directa).
3. **Probar la conexión remota desde local:** se corrió la imagen de Docker apuntando a Neon con las variables de entorno. Flyway aplicó las 6 migraciones en la base remota.
4. **Crear el servicio en Render:** *New > Web Service*, repositorio del proyecto, rama `tp3`, *Language: Docker*, instancia gratuita.
5. **Cargar las variables de entorno en Render** (sección *Environment*): `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` y `JWT_SECRET`. El `DB_URL` tiene el formato `jdbc:postgresql://<host>/<base>?sslmode=require`, sin usuario ni contraseña adentro.
6. **Desplegar y verificar:**
   - El log de Render muestra `Started ApiBlankApplication` sin errores.
   - `GET /health` responde `{"service":"api-blank","status":"ok"}` desde la URL pública.
   - `GET /api/listas` sin token responde 401, y con un token válido responde 200.
   - En Neon, la consulta `SELECT version, description, success FROM flyway_schema_history;` muestra las 6 migraciones con `success = t`.

## Persistencia: de memoria a JPA (puertos y adaptadores)

En el TP1 los favoritos se guardaban en un `Map` en memoria. En el TP2 pasan a PostgreSQL usando JPA/Hibernate.

**Clases agregadas o eliminadas (solo en la capa de persistencia):**
- Agregadas: `FavoritoEntity`, `FavoritoJpaRepository` y `FavoritoRepositoryAdapter` (paquete `persistence`).
- Eliminada: `FavoritoRepositoryImpl`, el repository en memoria.
- Infraestructura nueva: `docker-compose.yml`, dependencias en `pom.xml`, datos de conexión en `application.properties` y la migración `V1__create_favoritos.sql`.

**Clases que quedaron exactamente iguales en esa migración de memoria a JPA:** `Favorito` (dominio), `FavoritoRepository` (puerto), `FavoritoService`, `FavoritoController`, `FavoritoRequestDto`, `FavoritoResponseDto` y `GlobalExceptionHandler`. Más adelante, al agregar las listas y la seguridad, se les sumaron `listaId` y la autorización, pero eso son cambios de dominio, no de persistencia.

**Por qué fue posible:** `FavoritoRepository` es un *puerto*: un contrato que define qué operaciones necesita la aplicación (guardar, listar, buscar, borrar) sin decir cómo se hacen. `FavoritoService` depende solo de esa interfaz, así que no sabe si detrás hay un `Map` o una base de datos. La versión en memoria del TP1 y `FavoritoRepositoryAdapter` son *adapters*: implementaciones concretas e intercambiables de ese mismo contrato. Para cambiar de almacenamiento alcanzó con escribir un adapter nuevo y borrar el viejo, sin modificar el dominio, el Service ni el Controller.

## Evolución del esquema: por qué las correcciones son migraciones nuevas

Los favoritos creados antes de agregar las listas no tienen `lista_id`, y no se puede declarar una columna `NOT NULL` si ya hay filas nulas. Se resolvió con la migración nueva `V4`, que en orden crea la lista "Sin clasificar", se la asigna a los favoritos sin lista y recién ahí pone la columna `NOT NULL`. Lo mismo ocurrió con `usuario_id` en las listas (`V6`), resuelto con un usuario "sistema".

No se editó ninguna migración ya aplicada porque Flyway guarda un checksum de cada una, y si el archivo cambia se niega a arrancar. Además, en otras bases (la de un compañero, la de producción) esas migraciones ya corrieron y no se volverían a ejecutar, así que el cambio solo existiría en una máquina. Una migración nueva es la única forma de que todas las bases evolucionen igual y quede un historial versionado.

## Transacciones: mover favoritos entre listas

`ListaServiceImpl.moverFavoritos` hace varias escrituras: reasigna cada favorito de la lista origen a la destino y después elimina la lista origen. Está marcado con `@Transactional`, así que Spring las ejecuta como una sola unidad: si una falla, todas se deshacen y la base queda como estaba. Además verifica que ambas listas sean del usuario (o que sea ADMIN) antes de escribir nada.

**Qué pasaría sin `@Transactional`:** cada `save` y el `deleteById` se confirmarían (commit) por separado, cada uno en su propia transacción. Si se movieran, por ejemplo, 2 de 5 favoritos y después ocurriera un error (caída de la base, una excepción, un corte de conexión), esos 2 quedarían en la lista destino y los otros 3 en la origen, y la lista origen seguiría existiendo. La base quedaría en un estado intermedio que no corresponde ni a "antes" ni a "después" de la operación.

Relacionado con ACID:
- **Atomicidad:** es la propiedad que se rompería. Una operación de varios pasos debe aplicarse completa o no aplicarse nada; sin la transacción habría cambios parciales confirmados.
- **Consistencia:** la base pasaría por un estado que el negocio no admite (favoritos repartidos entre dos listas por una operación que se pretendía indivisible).
- **Aislamiento:** otro cliente podría consultar en medio de la operación y ver una parte de los favoritos ya movidos y otra no.
- **Durabilidad:** los cambios parciales quedarían guardados de forma permanente, por lo que no se podrían descartar solos.

Con `@Transactional` ninguno de esos estados intermedios es visible ni queda guardado.