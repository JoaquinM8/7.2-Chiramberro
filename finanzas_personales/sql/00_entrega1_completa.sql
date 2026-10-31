-- =====================================================================
-- PROYECTO: Finanzas Personales Inteligentes
-- ENTREGA 1 - SCRIPT UNICO DE ENTREGA (esquema + datos de prueba)
-- ARCHIVO: 00_entrega1_completa.sql
-- =====================================================================
-- Este archivo reune 01_esquema.sql y 02_datos.sql en un solo script,
-- tal como pide la consigna. Ejecutarlo completo en phpMyAdmin (pestana
-- SQL / Importar) o por consola:
--
--   mysql -u root --default-character-set=utf8mb4 < 00_entrega1_completa.sql
--
-- ADVERTENCIA: elimina y recrea la base finanzas_personales.
-- Las consultas de analisis se ejecutan despues con 03_consultas.sql.
-- =====================================================================
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


-- =====================================================================
-- PROYECTO: Finanzas Personales Inteligentes
-- ENTREGA 1 - Datos de prueba
-- ARCHIVO: 02_datos.sql
-- CONTENIDO: 2 usuarios, 4 cuentas (2 por usuario), 10 categorías
--            y 29 movimientos coherentes con flujos de caja reales
--            (16 de Ana y 13 de Bruno).
-- =====================================================================
-- Los IDs se indican de forma explícita para que los resultados de las
-- consultas sean siempre los mismos (reproducibilidad del informe).
-- Ejecutar SIEMPRE después de 01_esquema.sql (que recrea la base).
-- =====================================================================

USE finanzas_personales;
SET NAMES utf8mb4;

-- ---------------------------------------------------------------------
-- USUARIOS
-- ---------------------------------------------------------------------
INSERT INTO usuarios (id_usuario, nombre, email, fecha_alta) VALUES
    (1, 'Ana Gómez',    'ana.gomez@correo.com',   '2025-01-15'),
    (2, 'Bruno Díaz',   'bruno.diaz@correo.com',  '2025-03-02');

-- ---------------------------------------------------------------------
-- CUENTAS (2 por usuario)
-- ---------------------------------------------------------------------
INSERT INTO cuentas (id_cuenta, id_usuario, nombre, tipo, saldo_inicial, activa) VALUES
    (1, 1, 'Efectivo Ana',        'Efectivo',         200000.00, 1),
    (2, 1, 'Cuenta bancaria Ana', 'Cuenta bancaria',  180000.00, 1),
    (3, 2, 'Efectivo Bruno',      'Efectivo',         120000.00, 1),
    (4, 2, 'Billetera virtual',   'Billetera digital',45000.00, 1);

-- ---------------------------------------------------------------------
-- CATEGORIAS (10)
-- ---------------------------------------------------------------------
INSERT INTO categorias (id_categoria, nombre, tipo_permitido, descripcion) VALUES
    (1, 'Sueldo',        'INGRESO', 'Haberes percibidos por relación de dependencia'),
    (2, 'Freelance',     'INGRESO', 'Trabajos independientes y servicios puntuales'),
    (3, 'Vivienda',      'GASTO',   'Alquiler, expensas y mantenimiento del hogar'),
    (4, 'Alimentación',  'GASTO',   'Supermercado, verdulería y comidas fuera de casa'),
    (5, 'Transporte',    'GASTO',   'Transporte público, combustible y aplicaciones'),
    (6, 'Servicios',     'GASTO',   'Luz, gas, agua, internet y telefonía'),
    (7, 'Ocio',          'GASTO',   'Cine, recitales, salidas y entretenimiento'),
    (8, 'Salud',         'GASTO',   'Farmacia, consultas y copagos'),
    (9, 'Educación',     'GASTO',   'Cursos, libros y capacitaciones'),
    (10,'Ahorro/Inversión','AMBOS', 'Transferencias a plazo fijo o fondo común');

-- ---------------------------------------------------------------------
-- MOVIMIENTOS (29)
--   16 de Ana (cuentas 1 y 2) y 13 de Bruno (cuentas 3 y 4).
--   La mayoría cae en marzo de 2026 (consulta de rango de fechas),
--   con algunos registros en enero, febrero y abril para probar filtros.
--   Los dos últimos omiten medio_pago / es_recurrente a propósito para
--   demostrar el funcionamiento de los valores DEFAULT.
-- ---------------------------------------------------------------------
INSERT INTO movimientos
    (id_movimiento, id_cuenta, id_categoria, tipo, fecha, importe, descripcion, medio_pago, es_recurrente)
VALUES
    -- ================= ANA GÓMEZ =================
    ( 1, 2,  1, 'INGRESO', '2026-03-01', 1250000.00, 'Sueldo marzo 2026',                    'Transferencia',   1),
    ( 2, 2,  3, 'GASTO',   '2026-03-02',  320000.00, 'Alquiler marzo',                       'Débito automático',1),
    ( 3, 1,  4, 'GASTO',   '2026-03-03',   48500.00, 'Compra semanal en supermercado',        'Efectivo',        0),
    ( 4, 2,  6, 'GASTO',   '2026-03-05',   35200.00, 'Factura de luz y gas',                 'Débito automático',1),
    ( 5, 1,  5, 'GASTO',   '2026-03-07',   22000.00, 'Carga de SUBE y un taxi',              'Efectivo',        0),
    ( 6, 2,  7, 'GASTO',   '2026-03-09',   30000.00, 'Cine y cena con amigas',               'Tarjeta de débito',0),
    ( 7, 2,  2, 'INGRESO', '2026-03-12',  180000.00, 'Diseño de landing page para cliente',  'Transferencia',   0),
    ( 8, 2,  4, 'GASTO',   '2026-03-15',   62300.00, 'Supermercado mensual',                 'Tarjeta de débito',0),
    ( 9, 1,  8, 'GASTO',   '2026-03-18',   28000.00, 'Farmacia: medicamentos',               'Efectivo',        0),
    (10, 1,  5, 'GASTO',   '2026-03-20',   18000.00, 'Combustible',                          'Efectivo',        0),
    (11, 2,  6, 'GASTO',   '2026-03-24',   28900.00, 'Internet y telefonía celular',         'Débito automático',1),
    (12, 2,  7, 'GASTO',   '2026-03-28',   45000.00, 'Salida de fin de semana',              'Tarjeta de débito',0),
    (13, 2,  1, 'INGRESO', '2026-04-01', 1312500.00, 'Sueldo abril 2026',                    'Transferencia',   1),
    -- fuera de marzo: sirven para probar filtros por rango
    (14, 1,  4, 'GASTO',   '2026-02-25',   39800.00, 'Feria y verdulería',                   'Efectivo',        0),
    (15, 2,  9, 'GASTO',   '2026-01-15',   55000.00, 'Curso de SQL',                         'Tarjeta de crédito',0),
    (16, 2, 10, 'GASTO',   '2026-03-31',   80000.00, 'Transferencia a plazo fijo',           'Transferencia',   1),

    -- ================ BRUNO DÍAZ =================
    (17, 4,  1, 'INGRESO', '2026-03-01',  980000.00, 'Sueldo marzo 2026',                    'Transferencia',   1),
    (18, 4,  3, 'GASTO',   '2026-03-04',  250000.00, 'Alquiler marzo',                       'Transferencia',   1),
    (19, 3,  4, 'GASTO',   '2026-03-06',   55000.00, 'Supermercado del barrio',              'Efectivo',        0),
    (20, 4,  5, 'GASTO',   '2026-03-10',   15500.00, 'SUBE y colectivo',                     'Billetera virtual',0),
    (21, 4,  6, 'GASTO',   '2026-03-14',   22400.00, 'Agua y gas',                           'Débito automático',1),
    (22, 4,  7, 'GASTO',   '2026-03-19',   38000.00, 'Entradas para un recital',             'Tarjeta de débito',0),
    (23, 4,  2, 'INGRESO', '2026-03-22',   95000.00, 'Reparación de PC (freelance)',         'Transferencia',   0),
    (24, 3,  4, 'GASTO',   '2026-03-26',   41200.00, 'Delivery de comida',                   'Efectivo',        0),
    (25, 3,  8, 'GASTO',   '2026-03-30',   12000.00, 'Copago de kinesiología',               'Efectivo',        0),
    (26, 4,  3, 'GASTO',   '2026-04-02',  250000.00, 'Alquiler abril',                       'Transferencia',   1),
    (27, 4,  5, 'GASTO',   '2026-02-28',   14000.00, 'Carga de SUBE',                         'Billetera virtual',0),
    (28, 4,  7, 'GASTO',   '2026-03-29',   21000.00, 'Streaming y música',                    DEFAULT,           0);

-- Los dos movimientos siguientes demuestran los valores por defecto
-- (medio_pago = 'No especificado', es_recurrente = 0, fecha_registro = NOW()).
INSERT INTO movimientos (id_cuenta, id_categoria, tipo, fecha, importe, descripcion)
VALUES
    (1, 4, 'GASTO', '2026-03-21', 9800.00, 'Panadería (sin medio de pago informado)');

-- ---------------------------------------------------------------------
-- CONTROL DE LA CARGA
-- ---------------------------------------------------------------------
SELECT 'usuarios'   AS tabla, COUNT(*) AS filas FROM usuarios
UNION ALL SELECT 'cuentas',    COUNT(*) FROM cuentas
UNION ALL SELECT 'categorias', COUNT(*) FROM categorias
UNION ALL SELECT 'movimientos',COUNT(*) FROM movimientos;
