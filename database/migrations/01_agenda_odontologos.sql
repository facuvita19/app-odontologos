-- Migracion: agenda laboral de odontologos
-- Ejecutar una sola vez sobre clinica_odontologica.

USE clinica_odontologica;

ALTER TABLE odontologos
    ADD COLUMN especialidad VARCHAR(80) NOT NULL DEFAULT 'ODONTOLOGIA_GENERAL' AFTER edad,
    ADD COLUMN hora_inicio TIME NOT NULL DEFAULT '08:00:00' AFTER especialidad,
    ADD COLUMN hora_fin TIME NOT NULL DEFAULT '20:00:00' AFTER hora_inicio,
    ADD COLUMN duracion_turno INT NOT NULL DEFAULT 30 AFTER hora_fin;

CREATE TABLE odontologo_dias_atencion (
    odontologo_id BIGINT NOT NULL,
    dia_semana VARCHAR(10) NOT NULL,

    PRIMARY KEY (odontologo_id, dia_semana),

    CONSTRAINT fk_dias_atencion_odontologo
        FOREIGN KEY (odontologo_id)
        REFERENCES odontologos(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT chk_dias_atencion_dia
        CHECK (dia_semana IN (
            'MONDAY',
            'TUESDAY',
            'WEDNESDAY',
            'THURSDAY',
            'FRIDAY',
            'SATURDAY',
            'SUNDAY'
        ))
) ENGINE = InnoDB;

-- Disponibilidad inicial para odontologos existentes:
-- lunes a viernes. Se puede modificar luego desde la aplicacion.
INSERT IGNORE INTO odontologo_dias_atencion (odontologo_id, dia_semana)
SELECT id, 'MONDAY' FROM odontologos;

INSERT IGNORE INTO odontologo_dias_atencion (odontologo_id, dia_semana)
SELECT id, 'TUESDAY' FROM odontologos;

INSERT IGNORE INTO odontologo_dias_atencion (odontologo_id, dia_semana)
SELECT id, 'WEDNESDAY' FROM odontologos;

INSERT IGNORE INTO odontologo_dias_atencion (odontologo_id, dia_semana)
SELECT id, 'THURSDAY' FROM odontologos;

INSERT IGNORE INTO odontologo_dias_atencion (odontologo_id, dia_semana)
SELECT id, 'FRIDAY' FROM odontologos;

-- Verificacion
SELECT
    id,
    nombre,
    apellido,
    especialidad,
    hora_inicio,
    hora_fin,
    duracion_turno
FROM odontologos
ORDER BY id;

SELECT
    odontologo_id,
    dia_semana
FROM odontologo_dias_atencion
ORDER BY odontologo_id, dia_semana;
