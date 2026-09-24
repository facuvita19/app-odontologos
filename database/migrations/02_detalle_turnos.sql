-- Migracion: motivo de consulta y detalle de turnos
-- Ejecutar una sola vez sobre clinica_odontologica.

USE clinica_odontologica;

ALTER TABLE turnos
    ADD COLUMN motivo_consulta VARCHAR(500) NULL
    AFTER estado;

-- La columna observaciones ya existe y se utiliza para
-- observaciones administrativas internas.

SELECT
    id,
    motivo_consulta,
    observaciones,
    fecha_creacion
FROM turnos
ORDER BY id;
