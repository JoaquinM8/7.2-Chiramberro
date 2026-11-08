-- =====================================================================
-- PROYECTO: Finanzas Personales Inteligentes
-- ENTREGA 3 - Consultas de analisis financiero
-- ARCHIVO: 07_consultas_entrega3.sql
-- =====================================================================
-- Consultas de la Entrega 3 para el periodo MARZO 2026.
-- Se pueden ejecutar todas juntas en phpMyAdmin o por consola.
--
-- Requiere:
--   - 01_esquema.sql y 02_datos.sql (Entrega 1).
--   - 06_presupuestos.sql (tabla y datos de presupuesto).
--
-- Las consultas usan parametros fijos porque son para analisis manual.
-- En la aplicacion Java los mismos SQL se ejecutan con PreparedStatement
-- y los valores viajan como marcadores ?.
-- =====================================================================

USE finanzas_personales;
SET NAMES utf8mb4;

-- =====================================================================
-- C01. RESUMEN MENSUAL: INGRESOS, GASTOS Y RESULTADO
-- =====================================================================
-- El resultado se calcula sobre movimientos registrados. No incluye
-- saldo inicial ni movimientos de otros meses.
SELECT
    COALESCE(SUM(CASE WHEN tipo = 'INGRESO' THEN importe ELSE 0 END), 0) AS ingresos,
    COALESCE(SUM(CASE WHEN tipo = 'GASTO'   THEN importe ELSE 0 END), 0) AS gastos,
    COALESCE(SUM(CASE WHEN tipo = 'INGRESO' THEN importe ELSE -importe END), 0) AS resultado
FROM movimientos
WHERE fecha >= '2026-03-01' AND fecha < '2026-04-01';

-- Variante por usuario (ampliacion util para el informe).
SELECT u.nombre AS usuario,
       COALESCE(SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.importe ELSE 0 END), 0) AS ingresos,
       COALESCE(SUM(CASE WHEN m.tipo = 'GASTO'   THEN m.importe ELSE 0 END), 0) AS gastos,
       COALESCE(SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.importe ELSE -m.importe END), 0) AS resultado
FROM usuarios u
JOIN cuentas c     ON c.id_usuario = u.id_usuario
JOIN movimientos m ON m.id_cuenta = c.id_cuenta
WHERE m.fecha >= '2026-03-01' AND m.fecha < '2026-04-01'
GROUP BY u.id_usuario, u.nombre
ORDER BY u.nombre;

-- =====================================================================
-- C02. GASTOS POR CATEGORIA
-- =====================================================================
SELECT c.id_categoria, c.nombre,
       SUM(m.importe) AS total_gastado,
       COUNT(*) AS cantidad_movimientos
FROM movimientos m
JOIN categorias c ON c.id_categoria = m.id_categoria
WHERE m.tipo = 'GASTO'
  AND m.fecha >= '2026-03-01' AND m.fecha < '2026-04-01'
GROUP BY c.id_categoria, c.nombre
ORDER BY total_gastado DESC;

-- =====================================================================
-- C03. PORCENTAJE DE CADA CATEGORIA SOBRE EL TOTAL DE GASTOS
-- =====================================================================
-- NULLIF evita dividir por cero cuando no hay gastos en el periodo.
SELECT c.nombre,
       SUM(m.importe) AS total_gastado,
       ROUND(
           100 * SUM(m.importe) / NULLIF((
               SELECT SUM(importe) FROM movimientos
               WHERE tipo = 'GASTO'
                 AND fecha >= '2026-03-01' AND fecha < '2026-04-01'
           ), 0), 2) AS porcentaje
FROM movimientos m
JOIN categorias c ON c.id_categoria = m.id_categoria
WHERE m.tipo = 'GASTO'
  AND m.fecha >= '2026-03-01' AND m.fecha < '2026-04-01'
GROUP BY c.id_categoria, c.nombre
ORDER BY total_gastado DESC;

-- =====================================================================
-- C04. INGRESOS Y GASTOS POR DIA
-- =====================================================================
SELECT fecha,
       SUM(CASE WHEN tipo = 'INGRESO' THEN importe ELSE 0 END) AS ingresos,
       SUM(CASE WHEN tipo = 'GASTO'   THEN importe ELSE 0 END) AS gastos
FROM movimientos
WHERE fecha >= '2026-03-01' AND fecha < '2026-04-01'
GROUP BY fecha
ORDER BY fecha;

-- =====================================================================
-- C05. GASTO PROMEDIO POR MOVIMIENTO
-- =====================================================================
SELECT COUNT(*) AS cantidad_gastos,
       ROUND(AVG(importe), 2) AS gasto_promedio
FROM movimientos
WHERE tipo = 'GASTO'
  AND fecha >= '2026-03-01' AND fecha < '2026-04-01';

-- =====================================================================
-- C06. COMPARACION PRESUPUESTO VS. GASTO REAL (TODOS)
-- =====================================================================
-- LEFT JOIN: un presupuesto aparece aunque no haya tenido gastos.
-- El gasto real solo suma movimientos GASTO de las cuentas del usuario,
-- en la categoria y el mes presupuestados.
SELECT u.nombre AS usuario,
       cat.nombre AS categoria,
       p.anio, p.mes,
       p.monto_planificado,
       COALESCE(SUM(m.importe), 0) AS gasto_real,
       p.monto_planificado - COALESCE(SUM(m.importe), 0) AS disponible,
       CASE
           WHEN COALESCE(SUM(m.importe), 0) > p.monto_planificado THEN 'EXCEDIDO'
           WHEN COALESCE(SUM(m.importe), 0) = p.monto_planificado THEN 'AL LIMITE'
           ELSE 'DENTRO DEL PRESUPUESTO'
       END AS estado
FROM presupuestos p
JOIN usuarios u         ON u.id_usuario = p.id_usuario
JOIN categorias cat     ON cat.id_categoria = p.id_categoria
LEFT JOIN cuentas c     ON c.id_usuario = u.id_usuario
LEFT JOIN movimientos m ON m.id_cuenta = c.id_cuenta
                       AND m.id_categoria = p.id_categoria
                       AND m.tipo = 'GASTO'
                       AND YEAR(m.fecha) = p.anio
                       AND MONTH(m.fecha) = p.mes
GROUP BY p.id_presupuesto, u.nombre, cat.nombre,
         p.anio, p.mes, p.monto_planificado
ORDER BY u.nombre, cat.nombre;

-- Variante solo para Ana Gomez (usuario 1) en marzo 2026.
SELECT u.nombre AS usuario,
       cat.nombre AS categoria,
       p.anio, p.mes,
       p.monto_planificado,
       COALESCE(SUM(m.importe), 0) AS gasto_real,
       p.monto_planificado - COALESCE(SUM(m.importe), 0) AS disponible,
       ROUND(
           100 * COALESCE(SUM(m.importe), 0) / NULLIF(p.monto_planificado, 0), 2
       ) AS porcentaje_ejecutado,
       CASE
           WHEN COALESCE(SUM(m.importe), 0) > p.monto_planificado THEN 'EXCEDIDO'
           WHEN COALESCE(SUM(m.importe), 0) = p.monto_planificado THEN 'AL LIMITE'
           ELSE 'DENTRO DEL PRESUPUESTO'
       END AS estado
FROM presupuestos p
JOIN usuarios u         ON u.id_usuario = p.id_usuario
JOIN categorias cat     ON cat.id_categoria = p.id_categoria
LEFT JOIN cuentas c     ON c.id_usuario = u.id_usuario
LEFT JOIN movimientos m ON m.id_cuenta = c.id_cuenta
                       AND m.id_categoria = p.id_categoria
                       AND m.tipo = 'GASTO'
                       AND YEAR(m.fecha) = p.anio
                       AND MONTH(m.fecha) = p.mes
WHERE p.id_usuario = 1 AND p.anio = 2026 AND p.mes = 3
GROUP BY p.id_presupuesto, u.nombre, cat.nombre,
         p.anio, p.mes, p.monto_planificado
ORDER BY cat.nombre;

-- =====================================================================
-- C07. CASOS DE PRUEBA
-- =====================================================================

-- 7.a Mes sin movimientos: el resumen debe devolver todo en cero.
SELECT
    COALESCE(SUM(CASE WHEN tipo = 'INGRESO' THEN importe ELSE 0 END), 0) AS ingresos,
    COALESCE(SUM(CASE WHEN tipo = 'GASTO'   THEN importe ELSE 0 END), 0) AS gastos,
    COALESCE(SUM(CASE WHEN tipo = 'INGRESO' THEN importe ELSE -importe END), 0) AS resultado
FROM movimientos
WHERE fecha >= '2026-05-01' AND fecha < '2026-06-01';

-- 7.b Presupuesto sin gastos: gasto real 0 y estado dentro del presupuesto.
--     Se usa un presupuesto ficticio de junio 2026 para la categoria Salud.
SELECT u.nombre AS usuario, cat.nombre AS categoria, p.anio, p.mes,
       p.monto_planificado,
       COALESCE(SUM(m.importe), 0) AS gasto_real,
       p.monto_planificado - COALESCE(SUM(m.importe), 0) AS disponible,
       CASE
           WHEN COALESCE(SUM(m.importe), 0) > p.monto_planificado THEN 'EXCEDIDO'
           WHEN COALESCE(SUM(m.importe), 0) = p.monto_planificado THEN 'AL LIMITE'
           ELSE 'DENTRO DEL PRESUPUESTO'
       END AS estado
FROM presupuestos p
JOIN usuarios u         ON u.id_usuario = p.id_usuario
JOIN categorias cat     ON cat.id_categoria = p.id_categoria
LEFT JOIN cuentas c     ON c.id_usuario = u.id_usuario
LEFT JOIN movimientos m ON m.id_cuenta = c.id_cuenta
                       AND m.id_categoria = p.id_categoria
                       AND m.tipo = 'GASTO'
                       AND YEAR(m.fecha) = p.anio
                       AND MONTH(m.fecha) = p.mes
WHERE p.id_usuario = 1 AND p.anio = 2026 AND p.mes = 6
GROUP BY p.id_presupuesto, u.nombre, cat.nombre,
         p.anio, p.mes, p.monto_planificado
ORDER BY cat.nombre;

-- 7.c Incoherencias categoria/tipo: debe devolver 0 filas.
SELECT m.id_movimiento, m.tipo, c.nombre AS categoria, c.tipo_permitido
FROM movimientos m
JOIN categorias c ON c.id_categoria = m.id_categoria
WHERE c.tipo_permitido <> 'AMBOS'
  AND c.tipo_permitido <> m.tipo;

-- =====================================================================
-- C08. AMPLIACIONES
-- =====================================================================

-- 8.a Porcentaje que representa el gasto total sobre los ingresos.
SELECT ingresos, gastos,
       ROUND(100 * gastos / NULLIF(ingresos, 0), 2) AS porcentaje_gasto_sobre_ingresos
FROM (
    SELECT
        COALESCE(SUM(CASE WHEN tipo = 'INGRESO' THEN importe ELSE 0 END), 0) AS ingresos,
        COALESCE(SUM(CASE WHEN tipo = 'GASTO'   THEN importe ELSE 0 END), 0) AS gastos
    FROM movimientos
    WHERE fecha >= '2026-03-01' AND fecha < '2026-04-01'
) AS resumen;

-- 8.b Ranking de categorias de gasto (ORDER BY + LIMIT).
SELECT c.nombre AS categoria, SUM(m.importe) AS total
FROM movimientos m
JOIN categorias c ON c.id_categoria = m.id_categoria
WHERE m.tipo = 'GASTO'
  AND m.fecha >= '2026-03-01' AND m.fecha < '2026-04-01'
GROUP BY c.id_categoria, c.nombre
ORDER BY total DESC
LIMIT 3;

-- 8.c Gasto diario promedio de marzo (solo dias con al menos un gasto).
SELECT ROUND(SUM(importe) / COUNT(DISTINCT fecha), 2) AS gasto_promedio_por_dia
FROM movimientos
WHERE tipo = 'GASTO'
  AND fecha >= '2026-03-01' AND fecha < '2026-04-01';
