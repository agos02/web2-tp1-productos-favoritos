-- Usuario "sistema": dueño de las listas que existían antes de que las listas tuvieran dueño.
-- Su password_hash no es un BCrypt válido, así que nadie puede iniciar sesión con esta cuenta.
INSERT INTO usuarios (email, password_hash, rol)
SELECT 'sistema@sistema.local', 'cuenta-sin-login', 'USER'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'sistema@sistema.local');

-- Cada lista pertenece a un usuario. Por ahora admite NULL porque ya existen listas sin dueño.
ALTER TABLE listas
    ADD COLUMN usuario_id BIGINT REFERENCES usuarios (id);

-- Las listas existentes pasan a ser del usuario sistema.
UPDATE listas
SET usuario_id = (SELECT id FROM usuarios WHERE email = 'sistema@sistema.local')
WHERE usuario_id IS NULL;

-- Recién ahora que no quedan nulos, la columna pasa a ser obligatoria.
ALTER TABLE listas
    ALTER COLUMN usuario_id SET NOT NULL;