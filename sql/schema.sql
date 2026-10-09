-- Banca Digital - Esquema PostgreSQL (Avance 1)
-- Uso: createdb banca_digital && psql -d banca_digital -f sql/schema.sql

CREATE TABLE rol (
    id_rol      SERIAL PRIMARY KEY,
    nombre      VARCHAR(30) NOT NULL UNIQUE   -- ADMIN, CLIENTE, ASESOR, CAJERO, AUDITOR
);

CREATE TABLE usuario (
    id_usuario        SERIAL PRIMARY KEY,
    username          VARCHAR(50)  NOT NULL UNIQUE,
    password_hash     VARCHAR(255) NOT NULL,          -- contraseña cifrada (nunca texto plano)
    id_rol            INT NOT NULL REFERENCES rol(id_rol),
    intentos_fallidos INT NOT NULL DEFAULT 0,
    bloqueado         BOOLEAN NOT NULL DEFAULT FALSE,
    activo            BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE cliente (
    id_cliente  SERIAL PRIMARY KEY,
    id_usuario  INT UNIQUE REFERENCES usuario(id_usuario),
    dni         CHAR(8)      NOT NULL UNIQUE,
    nombres     VARCHAR(80)  NOT NULL,
    apellidos   VARCHAR(80)  NOT NULL,
    email       VARCHAR(100) UNIQUE,
    telefono    VARCHAR(15)
);

CREATE TABLE cuenta (
    id_cuenta     SERIAL PRIMARY KEY,
    id_cliente    INT NOT NULL REFERENCES cliente(id_cliente),
    numero_cuenta VARCHAR(20) NOT NULL UNIQUE,
    tipo          VARCHAR(10) NOT NULL CHECK (tipo IN ('AHORRO','CORRIENTE')),
    saldo         NUMERIC(14,2) NOT NULL DEFAULT 0 CHECK (saldo >= 0),
    estado        VARCHAR(10) NOT NULL DEFAULT 'ACTIVA' CHECK (estado IN ('ACTIVA','BLOQUEADA','CERRADA')),
    fecha_apertura TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE movimiento (
    id_movimiento  SERIAL PRIMARY KEY,
    id_cuenta      INT NOT NULL REFERENCES cuenta(id_cuenta),
    tipo           VARCHAR(15) NOT NULL CHECK (tipo IN ('DEPOSITO','RETIRO','TRANSFERENCIA','PAGO_SERVICIO')),
    monto          NUMERIC(14,2) NOT NULL CHECK (monto > 0),
    descripcion    VARCHAR(200),
    fecha          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE credito (
    id_credito     SERIAL PRIMARY KEY,
    id_cliente     INT NOT NULL REFERENCES cliente(id_cliente),
    id_asesor      INT REFERENCES usuario(id_usuario),
    monto          NUMERIC(14,2) NOT NULL CHECK (monto > 0),
    plazo_meses    INT NOT NULL CHECK (plazo_meses > 0),
    tasa_anual     NUMERIC(5,2) NOT NULL,
    estado         VARCHAR(15) NOT NULL DEFAULT 'SOLICITADO'
                   CHECK (estado IN ('SOLICITADO','EN_EVALUACION','APROBADO','RECHAZADO','DESEMBOLSADO')),
    observaciones  VARCHAR(300),
    fecha_solicitud TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE bitacora (
    id_bitacora SERIAL PRIMARY KEY,
    id_usuario  INT REFERENCES usuario(id_usuario),
    accion      VARCHAR(100) NOT NULL,
    detalle     VARCHAR(300),
    fecha       TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO rol(nombre) VALUES ('ADMIN'),('CLIENTE'),('ASESOR'),('CAJERO'),('AUDITOR');
