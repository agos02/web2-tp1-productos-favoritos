-- Tabla de favoritos: reemplaza al almacenamiento en memoria del TP1.
CREATE TABLE favoritos (
    id          BIGSERIAL PRIMARY KEY,
    producto_id INTEGER      NOT NULL,
    nota        VARCHAR(255) NOT NULL,
    fecha_alta  TIMESTAMP    NOT NULL
);