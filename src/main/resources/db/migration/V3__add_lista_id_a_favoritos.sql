-- Cada favorito pertenece a una lista.
-- La columna admite NULL por ahora porque ya existen favoritos sin lista;
-- se vuelve obligatoria en una migración posterior.
ALTER TABLE favoritos
    ADD COLUMN lista_id BIGINT REFERENCES listas (id);