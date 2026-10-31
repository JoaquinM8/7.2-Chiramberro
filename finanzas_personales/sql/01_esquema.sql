-- =====================================================================
-- PROYECTO: Finanzas Personales Inteligentes
-- ENTREGA 1 - Base de datos relacional (MariaDB / MySQL)
-- ARCHIVO: 01_esquema.sql
-- CONTENIDO: creación de la base, las 4 tablas, restricciones e índices
-- =====================================================================
-- Ejecutar en phpMyAdmin (pestaña SQL) o por consola:
--   mysql -u root -p --default-character-set=utf8mb4 < 01_esquema.sql
-- =====================================================================

DROP DATABASE IF EXISTS finanzas_personales;

CREATE DATABASE finanzas_personales
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE finanzas_personales;

-- ---------------------------------------------------------------------
-- 1) USUARIOS
-- ---------------------------------------------------------------------
CREATE TABLE usuarios (
    id_usuario  INT           NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(80)   NOT NULL,
    email       VARCHAR(120)  NOT NULL,
    fecha_alta  DATE          NOT NULL,
    PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuarios_email UNIQUE (email)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 2) CUENTAS
--    Cada cuenta pertenece a un usuario (1:N).
--    El saldo NO se almacena: se calcula con saldo_inicial + movimientos
--    (ver consulta 10), así se evita la duplicación de información.
-- ---------------------------------------------------------------------
CREATE TABLE cuentas (
    id_cuenta     INT           NOT NULL AUTO_INCREMENT,
    id_usuario    INT           NOT NULL,
    nombre        VARCHAR(60)   NOT NULL,
    tipo          VARCHAR(30)   NOT NULL,
    saldo_inicial DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    activa        TINYINT(1)    NOT NULL DEFAULT 1,
    PRIMARY KEY (id_cuenta),
    CONSTRAINT fk_cuentas_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT chk_cuentas_saldo_inicial CHECK (saldo_inicial >= 0),
    CONSTRAINT chk_cuentas_activa        CHECK (activa IN (0, 1))
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 3) CATEGORIAS
--    tipo_permitido indica para qué tipo de movimiento puede usarse.
-- ---------------------------------------------------------------------
CREATE TABLE categorias (
    id_categoria   INT          NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(60)  NOT NULL,
    tipo_permitido ENUM('INGRESO','GASTO','AMBOS') NOT NULL DEFAULT 'AMBOS',
    descripcion    VARCHAR(180) NULL,
    PRIMARY KEY (id_categoria),
    CONSTRAINT uq_categorias_nombre UNIQUE (nombre)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 4) MOVIMIENTOS
--    Hecho central del modelo: cada fila es un ingreso o un gasto
--    imputado a UNA cuenta y a UNA categoría.
-- ---------------------------------------------------------------------
CREATE TABLE movimientos (
    id_movimiento  INT           NOT NULL AUTO_INCREMENT,
    id_cuenta      INT           NOT NULL,
    id_categoria   INT           NOT NULL,
    tipo           ENUM('INGRESO','GASTO') NOT NULL,
    fecha          DATE          NOT NULL,
    importe        DECIMAL(12,2) NOT NULL,
    descripcion    VARCHAR(180)  NOT NULL,
    medio_pago     VARCHAR(30)   NOT NULL DEFAULT 'No especificado',
    es_recurrente  TINYINT(1)    NOT NULL DEFAULT 0,
    fecha_registro TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_movimiento),
    CONSTRAINT fk_movimientos_cuenta
        FOREIGN KEY (id_cuenta) REFERENCES cuentas (id_cuenta)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_movimientos_categoria
        FOREIGN KEY (id_categoria) REFERENCES categorias (id_categoria)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT chk_movimientos_importe CHECK (importe > 0),
    CONSTRAINT chk_movimientos_recurrente CHECK (es_recurrente IN (0, 1)),
    INDEX idx_movimientos_fecha (fecha),
    INDEX idx_movimientos_tipo (tipo),
    INDEX idx_movimientos_cuenta (id_cuenta),
    INDEX idx_movimientos_categoria (id_categoria)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Verificación del esquema creado
-- ---------------------------------------------------------------------
SHOW TABLES;
