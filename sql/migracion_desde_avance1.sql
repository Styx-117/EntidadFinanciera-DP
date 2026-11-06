ALTER TABLE usuario ADD COLUMN IF NOT EXISTS nombre_completo VARCHAR(100);
ALTER TABLE credito ADD COLUMN IF NOT EXISTS fecha_evaluacion TIMESTAMP;

CREATE TABLE IF NOT EXISTS notificacion (
    id_notificacion SERIAL PRIMARY KEY,
    id_cliente      INT NOT NULL REFERENCES cliente(id_cliente),
    mensaje         VARCHAR(400) NOT NULL,
    leida           BOOLEAN NOT NULL DEFAULT FALSE,
    fecha           TIMESTAMP NOT NULL DEFAULT NOW()
);
