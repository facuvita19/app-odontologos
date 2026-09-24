-- ============================================================
-- Sistema de Gestion Odontologica
-- Esquema completo para instalaciones nuevas en MySQL
-- ============================================================

CREATE DATABASE IF NOT EXISTS clinica_odontologica
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE clinica_odontologica;

-- ------------------------------------------------------------
-- Pacientes
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pacientes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL,
    domicilio VARCHAR(255),
    edad INT NOT NULL,
    fecha_alta DATE NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT uq_pacientes_dni
        UNIQUE (dni),

    CONSTRAINT chk_pacientes_edad
        CHECK (edad >= 0 AND edad <= 120)
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- Odontologos
-- La especialidad se guarda con el nombre tecnico del enum Java.
-- Los horarios indican la jornada habitual del profesional.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS odontologos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    matricula VARCHAR(30) NOT NULL,
    edad INT NOT NULL,
    especialidad VARCHAR(80) NOT NULL
        DEFAULT 'ODONTOLOGIA_GENERAL',
    hora_inicio TIME NOT NULL DEFAULT '08:00:00',
    hora_fin TIME NOT NULL DEFAULT '20:00:00',
    duracion_turno INT NOT NULL DEFAULT 30,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT uq_odontologos_matricula
        UNIQUE (matricula),

    CONSTRAINT chk_odontologos_edad
        CHECK (edad >= 21 AND edad <= 100),

    CONSTRAINT chk_odontologos_horario
        CHECK (hora_fin > hora_inicio),

    CONSTRAINT chk_odontologos_duracion
        CHECK (
            duracion_turno >= 15
            AND duracion_turno <= 180
            AND MOD(duracion_turno, 15) = 0
        ),

    CONSTRAINT chk_odontologos_especialidad
        CHECK (
            especialidad IN (
                'ODONTOLOGIA_GENERAL',
                'ORTODONCIA',
                'ENDODONCIA',
                'CIRUGIA',
                'ODONTOPEDIATRIA',
                'PERIODONCIA',
                'PROTESIS'
            )
        )
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- Dias de atencion de odontologos
-- Un odontologo puede atender uno o varios dias por semana.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS odontologo_dias_atencion (
    odontologo_id BIGINT NOT NULL,
    dia_semana VARCHAR(10) NOT NULL,

    PRIMARY KEY (odontologo_id, dia_semana),

    CONSTRAINT fk_dias_atencion_odontologo
        FOREIGN KEY (odontologo_id)
        REFERENCES odontologos(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT chk_dias_atencion_dia
        CHECK (
            dia_semana IN (
                'MONDAY',
                'TUESDAY',
                'WEDNESDAY',
                'THURSDAY',
                'FRIDAY',
                'SATURDAY',
                'SUNDAY'
            )
        )
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- Usuarios
-- paciente_id es NULL para administradores y obligatorio a
-- nivel funcional para usuarios que deseen reservar turnos.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre_usuario VARCHAR(30) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    rol ENUM(
        'ADMINISTRADOR',
        'USUARIO'
    ) NOT NULL DEFAULT 'USUARIO',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    paciente_id BIGINT NULL,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT uq_usuarios_nombre
        UNIQUE (nombre_usuario),

    CONSTRAINT uq_usuarios_paciente
        UNIQUE (paciente_id),

    CONSTRAINT fk_usuarios_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES pacientes(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- Turnos
-- No se crea un indice UNIQUE para odontologo, fecha e inicio.
-- La superposicion se valida considerando intervalos y estado.
-- Esto permite reutilizar horarios de turnos cancelados.
-- motivo_consulta es visible para el usuario y el administrador.
-- observaciones contiene informacion administrativa interna.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS turnos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    paciente_id BIGINT NOT NULL,
    odontologo_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado ENUM(
        'PENDIENTE',
        'CONFIRMADO',
        'ATENDIDO',
        'CANCELADO',
        'AUSENTE'
    ) NOT NULL DEFAULT 'PENDIENTE',
    motivo_consulta VARCHAR(500),
    observaciones VARCHAR(500),
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT chk_turnos_horario
        CHECK (hora_fin > hora_inicio),

    CONSTRAINT fk_turnos_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES pacientes(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_turnos_odontologo
        FOREIGN KEY (odontologo_id)
        REFERENCES odontologos(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_turnos_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    INDEX idx_turnos_paciente (paciente_id),
    INDEX idx_turnos_odontologo (odontologo_id),
    INDEX idx_turnos_usuario (usuario_id),
    INDEX idx_turnos_fecha (fecha),
    INDEX idx_turnos_estado (estado),
    INDEX idx_turnos_fecha_estado (fecha, estado),
    INDEX idx_turnos_disponibilidad (
        odontologo_id,
        fecha,
        hora_inicio,
        hora_fin,
        estado
    )
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- Datos iniciales
-- ------------------------------------------------------------
-- Este esquema no inserta un administrador predeterminado porque
-- password_hash debe generarse con el algoritmo usado por la
-- aplicacion. Cree el administrador desde el flujo seguro previsto
-- por el proyecto o inserte un hash compatible ya generado.

-- ------------------------------------------------------------
-- Verificacion del esquema
-- ------------------------------------------------------------
SHOW TABLES;

SHOW COLUMNS FROM odontologos;
SHOW COLUMNS FROM odontologo_dias_atencion;
SHOW COLUMNS FROM turnos;
