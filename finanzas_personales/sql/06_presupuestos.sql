-- =====================================================================
-- PROYECTO: Finanzas Personales Inteligentes
-- ENTREGA 3 - Presupuestos mensuales por categoria
-- ARCHIVO: 06_presupuestos.sql
-- =====================================================================
-- Crea la tabla presupuestos y carga limites de ejemplo para marzo 2026.
--
-- Un presupuesto es un PLAN: fija cuanto se pretende gastar como maximo en
-- una categoria durante un mes. El gasto real NO se guarda en esta tabla;
-- se calcula desde movimientos con la consulta de comparacion.
--
-- Ejecutar DESPUES de 01_esquema.sql y 02_datos.sql.
-- El script es re-ejecutable: si el presupuesto ya existe, actualiza el
-- monto y las observaciones en lugar de duplicarlo.
--
-- IMPORTANTE: los ids de categoria del ejemplo corresponden al script
-- 02_datos.sql de la Entrega 1. Verificar con:
--   SELECT id_categoria, nombre, tipo_permitido FROM categorias;
-- =====================================================================

USE finanzas_personales;
SET NAMES utf8mb4;

-- ---------------------------------------------------------------------
-- 1) TABLA PRESUPUESTOS
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS presupuestos (
    id_presupuesto    INT           NOT NULL AUTO_INCREMENT,
    id_usuario        INT           NOT NULL,
    id_categoria      INT           NOT NULL,
    anio              SMALLINT      NOT NULL,
    mes               TINYINT       NOT NULL,
    monto_planificado DECIMAL(12,2) NOT NULL,
    observaciones     VARCHAR(180)  NULL,
    PRIMARY KEY (id_presupuesto),
    CONSTRAINT fk_pres_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_pres_categoria FOREIGN KEY (id_categoria)
        REFERENCES categorias (id_categoria)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT uq_presupuesto UNIQUE (id_usuario, id_categoria, anio, mes),
    CONSTRAINT chk_pres_mes CHECK (mes BETWEEN 1 AND 12),
    CONSTRAINT chk_pres_monto CHECK (monto_planificado >= 0)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 2) DATOS DE PRUEBA
--    Ana Gomez (usuario 1) y Bruno Diaz (usuario 2), marzo 2026.
--    Se eligen categorias de GASTO/AMBOS y montos que permiten observar
--    los tres estados: EXCEDIDO, AL LIMITE y DENTRO DEL PRESUPUESTO.
-- ---------------------------------------------------------------------
INSERT INTO presupuestos
    (id_usuario, id_categoria, anio, mes, monto_planificado, observaciones)
VALUES
    -- Ana Gomez (usuario 1)
    (1, 3, 2026, 3, 320000.00, 'Vivienda: alquiler y expensas'),
    (1, 4, 2026, 3, 120000.00, 'Alimentacion: supermercado y comidas'),
    (1, 5, 2026, 3,  50000.00, 'Transporte: SUBE, taxi y combustible'),
    (1, 6, 2026, 3,  60000.00, 'Servicios: luz, gas, internet y telefono'),
    (1, 7, 2026, 3,  80000.00, 'Ocio: cine, salidas y entretenimiento'),
    -- Presupuesto de un mes sin movimientos: sirve para probar el caso
    -- "presupuesto sin gastos" (gasto real 0, estado dentro del presupuesto).
    (1, 8, 2026, 6,  50000.00, 'Presupuesto de prueba sin gastos (junio 2026)'),
    -- Bruno Diaz (usuario 2)
    (2, 3, 2026, 3, 250000.00, 'Vivienda: alquiler'),
    (2, 4, 2026, 3, 100000.00, 'Alimentacion: supermercado y delivery')
ON DUPLICATE KEY UPDATE
    monto_planificado = VALUES(monto_planificado),
    observaciones     = VALUES(observaciones);

-- ---------------------------------------------------------------------
-- 3) CONTROL DE LA CARGA
-- ---------------------------------------------------------------------
SELECT p.id_presupuesto,
       u.nombre  AS usuario,
       c.nombre  AS categoria,
       p.anio, p.mes,
       p.monto_planificado,
       p.observaciones
FROM presupuestos p
JOIN usuarios u   ON u.id_usuario = p.id_usuario
JOIN categorias c ON c.id_categoria = p.id_categoria
ORDER BY u.nombre, p.anio, p.mes, c.nombre;

-- Estructura final de la tabla (para documentar la entrega).
SHOW CREATE TABLE presupuestos;
