-- =====================================================================
-- PROYECTO: Finanzas Personales Inteligentes
-- ENTREGA 1 - Prueba de integridad
-- ARCHIVO: 04_integridad.sql
-- =====================================================================
-- Cada bloque provoca un error A PROPÓSITO. El objetivo es demostrar que
-- la base rechaza datos inválidos y documentar el mensaje del motor.
--
-- CÓMO EJECUTARLO
--   Opción A (consola, continúa aunque haya errores):
--     mysql -u root -p --force --table finanzas_personales < 04_integridad.sql
--   Opción B (phpMyAdmin): ejecutar cada bloque por separado, porque
--     después de un error la pestaña SQL no sigue con el resto.
--
-- Los mensajes reales obtenidos con MariaDB 10.4.32 se encuentran en
-- 04_integridad_salida.txt, para adjuntar como evidencia.
-- =====================================================================

USE finanzas_personales;

-- Se activa el modo estricto para que los ENUM inválidos y los datos
-- truncados también fallen (XAMPP viene con un sql_mode permisivo).
SET SESSION sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';


-- =====================================================================
-- PRUEBA 1 - CHECK: importe de un movimiento debe ser > 0
-- Esperado: ERROR 4025 - CONSTRAINT `chk_movimientos_importe` failed
-- =====================================================================
INSERT INTO movimientos (id_cuenta, id_categoria, tipo, fecha, importe, descripcion)
VALUES (1, 4, 'GASTO', '2026-03-31', -5000.00, 'Prueba: gasto con importe negativo');


-- =====================================================================
-- PRUEBA 2 - CHECK: el saldo inicial no puede ser negativo
-- Esperado: ERROR 4025 - CONSTRAINT `chk_cuentas_saldo_inicial` failed
-- =====================================================================
INSERT INTO cuentas (id_usuario, nombre, tipo, saldo_inicial)
VALUES (1, 'Cuenta inválida', 'Efectivo', -1000.00);


-- =====================================================================
-- PRUEBA 3 - INTEGRIDAD REFERENCIAL: la cuenta debe existir
-- Esperado: ERROR 1452 - Cannot add or update a child row (fk_movimientos_cuenta)
-- =====================================================================
INSERT INTO movimientos (id_cuenta, id_categoria, tipo, fecha, importe, descripcion)
VALUES (99, 4, 'GASTO', '2026-03-31', 15000.00, 'Prueba: cuenta inexistente');


-- =====================================================================
-- PRUEBA 4 - UNIQUE: no puede repetirse el email de un usuario
-- Esperado: ERROR 1062 - Duplicate entry 'ana.gomez@correo.com'
-- =====================================================================
INSERT INTO usuarios (nombre, email, fecha_alta)
VALUES ('Otra Ana', 'ana.gomez@correo.com', '2026-03-01');


-- =====================================================================
-- PRUEBA 5 - ENUM: sólo admite INGRESO / GASTO / AMBOS
-- Esperado: ERROR 1265 - Data truncated for column 'tipo_permitido'
-- =====================================================================
INSERT INTO categorias (nombre, tipo_permitido)
VALUES ('Transferencia', 'TRANSFERENCIA');


-- =====================================================================
-- PRUEBA 6 - DELETE restringido: no se puede borrar un usuario que
--            todavía tiene cuentas asociadas (ON DELETE RESTRICT)
-- Esperado: ERROR 1451 - Cannot delete or update a parent row
-- =====================================================================
DELETE FROM usuarios WHERE id_usuario = 1;


-- =====================================================================
-- PRUEBA 7 - CHECK en un UPDATE: no se puede "corregir" un movimiento
--            dejándolo en importe 0
-- Esperado: ERROR 4025 - CONSTRAINT `chk_movimientos_importe` failed
-- =====================================================================
UPDATE movimientos SET importe = 0 WHERE id_movimiento = 3;


-- =====================================================================
-- CONTROL FINAL: la base quedó intacta (los INSERT/UPDATE/DELETE
-- anteriores fallaron y no dejaron datos basura)
-- =====================================================================
SELECT 'usuarios' AS tabla, COUNT(*) AS filas FROM usuarios
UNION ALL SELECT 'cuentas',    COUNT(*) FROM cuentas
UNION ALL SELECT 'categorias', COUNT(*) FROM categorias
UNION ALL SELECT 'movimientos',COUNT(*) FROM movimientos;
