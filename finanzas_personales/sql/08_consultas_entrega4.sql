-- =====================================================================
-- PROYECTO: Finanzas Personales Inteligentes
-- ENTREGA 4 - Aplicacion grafica con Java Swing
-- ARCHIVO: 08_consultas_entrega4.sql
-- =====================================================================
-- Consultas de verificacion de los datos que consume la interfaz:
-- combos, tabla de movimientos, filtros, resumen mensual y presupuestos.
--
-- Son consultas de solo lectura. Se pueden ejecutar en phpMyAdmin o por
-- consola despues de 01_esquema.sql, 02_datos.sql y 06_presupuestos.sql.
-- =====================================================================

USE finanzas_personales;
SET NAMES utf8mb4;

-- ---------------------------------------------------------------------
-- 1) DATOS PARA LOS COMBOS DE LA VISTA
-- ---------------------------------------------------------------------
SELECT 'CUENTAS ACTIVAS' AS bloque;
SELECT id_cuenta, nombre
FROM cuentas
WHERE activa = 1
ORDER BY nombre;

SELECT 'CATEGORIAS (TODAS)' AS bloque;
SELECT id_categoria, nombre, tipo_permitido
FROM categorias
ORDER BY nombre;

SELECT 'CATEGORIAS PARA GASTO/AMBOS' AS bloque;
SELECT id_categoria, nombre
FROM categorias
WHERE tipo_permitido IN ('GASTO', 'AMBOS')
ORDER BY nombre;

SELECT 'USUARIOS PARA PRESUPUESTOS' AS bloque;
SELECT id_usuario, nombre
FROM usuarios
ORDER BY nombre;

-- ---------------------------------------------------------------------
-- 2) TABLA DE MOVIMIENTOS (equivale a MovimientoDAO.listarTodos)
-- ---------------------------------------------------------------------
SELECT 'MOVIMIENTOS ORDENADOS PARA LA JTABLE' AS bloque;
SELECT m.id_movimiento,
       m.fecha,
       m.tipo,
       c.nombre  AS cuenta,
       cat.nombre AS categoria,
       m.descripcion,
       m.importe,
       m.medio_pago,
       m.es_recurrente
FROM movimientos m
JOIN cuentas c     ON c.id_cuenta = m.id_cuenta
JOIN categorias cat ON cat.id_categoria = m.id_categoria
ORDER BY m.fecha DESC, m.id_movimiento DESC;

-- ---------------------------------------------------------------------
-- 3) FILTRO POR TIPO Y FECHAS (marzo 2026, GASTOS)
-- ---------------------------------------------------------------------
SELECT 'FILTRO GASTOS MARZO 2026' AS bloque;
SELECT id_movimiento, fecha, tipo, descripcion, importe
FROM movimientos
WHERE tipo = 'GASTO'
  AND fecha >= '2026-03-01'
  AND fecha <  '2026-04-01'
ORDER BY fecha DESC, id_movimiento DESC;

-- ---------------------------------------------------------------------
-- 4) FILTRO POR DESCRIPCION
-- ---------------------------------------------------------------------
SELECT 'FILTRO POR DESCRIPCION: luz' AS bloque;
SELECT id_movimiento, fecha, tipo, descripcion, importe
FROM movimientos
WHERE descripcion LIKE '%luz%'
ORDER BY fecha DESC, id_movimiento DESC;

-- ---------------------------------------------------------------------
-- 5) RESUMEN MENSUAL (marzo 2026)
-- ---------------------------------------------------------------------
SELECT 'RESUMEN MENSUAL MARZO 2026' AS bloque;
SELECT SUM(CASE WHEN tipo = 'INGRESO' THEN importe ELSE 0 END) AS ingresos,
       SUM(CASE WHEN tipo = 'GASTO'   THEN importe ELSE 0 END) AS gastos,
       SUM(CASE WHEN tipo = 'INGRESO' THEN importe ELSE -importe END) AS resultado
FROM movimientos
WHERE fecha >= '2026-03-01'
  AND fecha <  '2026-04-01';

-- ---------------------------------------------------------------------
-- 6) PRESUPUESTO PLANIFICADO VS GASTO REAL (Ana, marzo 2026)
--    Es la consulta que usa PresupuestoDAO.compararPresupuestos.
-- ---------------------------------------------------------------------
SELECT 'PRESUPUESTO VS REAL - ANA MARZO 2026' AS bloque;
SELECT p.id_presupuesto,
       cat.nombre AS categoria,
       p.monto_planificado,
       COALESCE(SUM(m.importe), 0) AS gasto_real,
       p.monto_planificado - COALESCE(SUM(m.importe), 0) AS disponible,
       CASE
           WHEN COALESCE(SUM(m.importe), 0) > p.monto_planificado THEN 'EXCEDIDO'
           WHEN COALESCE(SUM(m.importe), 0) = p.monto_planificado THEN 'AL LIMITE'
           ELSE 'DENTRO DEL PRESUPUESTO'
       END AS estado
FROM presupuestos p
JOIN categorias cat ON cat.id_categoria = p.id_categoria
LEFT JOIN cuentas c ON c.id_usuario = p.id_usuario
LEFT JOIN movimientos m
       ON m.id_cuenta = c.id_cuenta
      AND m.id_categoria = p.id_categoria
      AND m.tipo = 'GASTO'
      AND YEAR(m.fecha) = p.anio
      AND MONTH(m.fecha) = p.mes
WHERE p.id_usuario = 1
  AND p.anio = 2026
  AND p.mes = 3
GROUP BY p.id_presupuesto, cat.nombre, p.monto_planificado
ORDER BY cat.nombre;

-- ---------------------------------------------------------------------
-- 7) CONTROL FINAL: NO DEBEN QUEDAR DATOS TEMPORALES DE LA PRUEBA
-- ---------------------------------------------------------------------
SELECT 'CONTROL DE DATOS TEMPORALES' AS bloque;
SELECT COUNT(*) AS movimientos_temporales
FROM movimientos
WHERE descripcion LIKE 'Prueba GUI Entrega 4%';

SELECT COUNT(*) AS presupuestos_temporales
FROM presupuestos
WHERE id_usuario = 1
  AND id_categoria = 9
  AND anio = 2026
  AND mes = 10;
