-- =====================================================================
-- PROYECTO: Finanzas Personales Inteligentes
-- ENTREGA 1 - Consultas de análisis
-- ARCHIVO: 03_consultas.sql
-- =====================================================================
-- Todas las consultas están numeradas y comentadas con el punto de la
-- consigna que demuestran. Se pueden ejecutar todas juntas o una por una.
-- Período de análisis del informe: MARZO 2026.
-- =====================================================================

USE finanzas_personales;

-- =====================================================================
-- C01. LISTADO GENERAL DE USUARIOS Y MOVIMIENTOS
-- =====================================================================
-- 1.a Usuarios ordenados por antigüedad
SELECT id_usuario, nombre, email, fecha_alta
FROM usuarios
ORDER BY fecha_alta;

-- 1.b Todos los movimientos, del más reciente al más antiguo
SELECT id_movimiento, fecha, tipo, importe, descripcion, id_cuenta, id_categoria
FROM movimientos
ORDER BY fecha DESC, id_movimiento DESC;


-- =====================================================================
-- C02. FILTRADO POR TIPO = 'GASTO'
-- =====================================================================
SELECT id_movimiento, fecha, importe, descripcion
FROM movimientos
WHERE tipo = 'GASTO'
ORDER BY fecha;


-- =====================================================================
-- C03. OPERADORES LÓGICOS (AND, >=) Y ORDER BY DESC
-- =====================================================================
-- Gastos importantes (>= $40.000) ordenados de mayor a menor.
-- Se usa >= para descartar los gastos pequeños y AND para combinar
-- las dos condiciones (tipo y monto).
SELECT fecha, importe, descripcion
FROM movimientos
WHERE tipo = 'GASTO'
  AND importe >= 40000
ORDER BY importe DESC;


-- =====================================================================
-- C04. BÚSQUEDA DE TEXTO CON LIKE
-- =====================================================================
-- 4.a Descripciones que contienen "super" (supermercado / super)
SELECT fecha, descripcion, importe
FROM movimientos
WHERE descripcion LIKE '%super%'
ORDER BY fecha;

-- 4.b Descripciones que mencionan luz, agua, internet o teléfono
SELECT fecha, descripcion, importe
FROM movimientos
WHERE descripcion LIKE '%luz%'
   OR descripcion LIKE '%agua%'
   OR descripcion LIKE '%internet%'
   OR descripcion LIKE '%tel%'
ORDER BY fecha;

-- 4.c Categorías cuyo nombre empieza con "A"
SELECT nombre, tipo_permitido
FROM categorias
WHERE nombre LIKE 'A%';


-- =====================================================================
-- C05. FUNCIONES DE AGREGACIÓN (COUNT, SUM, AVG, MIN, MAX)
-- =====================================================================
-- 5.a Totales generales del período cargado
SELECT COUNT(*)        AS cantidad_movimientos,
       SUM(importe)    AS suma_importes,
       ROUND(AVG(importe), 2) AS promedio_importe,
       MIN(importe)    AS minimo,
       MAX(importe)    AS maximo
FROM movimientos;

-- 5.b Resumen por tipo de movimiento
SELECT tipo,
       COUNT(*)            AS cantidad,
       SUM(importe)        AS total,
       ROUND(AVG(importe), 2) AS promedio
FROM movimientos
GROUP BY tipo;

-- 5.c Ticket promedio de un gasto (sólo GASTOS)
SELECT COUNT(*)               AS cantidad_gastos,
       ROUND(AVG(importe), 2) AS gasto_promedio
FROM movimientos
WHERE tipo = 'GASTO';


-- =====================================================================
-- C06. CONSULTAS MULTITABLA CON JOIN
-- =====================================================================
-- Une movimientos -> cuentas -> usuarios y movimientos -> categorias
-- para mostrar datos legibles en lugar de IDs.
SELECT m.fecha,
       u.nombre        AS usuario,
       c.nombre        AS cuenta,
       ca.nombre       AS categoria,
       m.tipo,
       m.importe,
       m.descripcion,
       m.medio_pago,
       m.es_recurrente
FROM movimientos m
INNER JOIN cuentas    c  ON c.id_cuenta     = m.id_cuenta
INNER JOIN usuarios   u  ON u.id_usuario    = c.id_usuario
INNER JOIN categorias ca ON ca.id_categoria = m.id_categoria
ORDER BY m.fecha, m.id_movimiento;

-- 6.b Cantidad de movimientos y total por usuario (JOIN + GROUP BY)
SELECT u.nombre AS usuario,
       COUNT(m.id_movimiento) AS movimientos,
       SUM(m.importe)         AS importe_total
FROM usuarios u
INNER JOIN cuentas c     ON c.id_usuario = u.id_usuario
INNER JOIN movimientos m ON m.id_cuenta  = c.id_cuenta
GROUP BY u.id_usuario, u.nombre
ORDER BY importe_total DESC;


-- =====================================================================
-- C07. RANGOS DE FECHAS: TODO MARZO 2026
-- =====================================================================
-- 7.a Con BETWEEN (incluye ambos extremos)
SELECT fecha, tipo, importe, descripcion
FROM movimientos
WHERE fecha BETWEEN '2026-03-01' AND '2026-03-31'
ORDER BY fecha;

-- 7.b Con >= y < (recomendado para columnas DATETIME: evita perder
--     registros del último día que tengan hora)
SELECT fecha, tipo, importe, descripcion
FROM movimientos
WHERE fecha >= '2026-03-01'
  AND fecha <  '2026-04-01'
ORDER BY fecha;

-- 7.c Totales de marzo por tipo
SELECT tipo,
       COUNT(*)     AS cantidad,
       SUM(importe) AS total_marzo
FROM movimientos
WHERE fecha >= '2026-03-01'
  AND fecha <  '2026-04-01'
GROUP BY tipo;


-- =====================================================================
-- C08. RESULTADO POR USUARIO CON CASE WHEN
-- =====================================================================
-- Clasifica el perfil financiero de marzo según la tasa de ahorro.
SELECT u.nombre AS usuario,
       SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.importe ELSE 0 END)            AS ingresos,
       SUM(CASE WHEN m.tipo = 'GASTO'   THEN m.importe ELSE 0 END)            AS gastos,
       ROUND(SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.importe
                      ELSE -m.importe END), 2)                                AS balance,
       ROUND(SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.importe
                      ELSE -m.importe END)
             / NULLIF(SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.importe ELSE 0 END), 0)
             * 100, 2)                                                        AS tasa_ahorro_pct,
       CASE
           WHEN SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.importe ELSE 0 END) = 0
                THEN 'Sin ingresos registrados'
           WHEN SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.importe
                          ELSE -m.importe END) < 0
                THEN 'En déficit'
           WHEN SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.importe
                          ELSE -m.importe END)
                / SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.importe ELSE 0 END) >= 0.20
                THEN 'Ahorrador'
           ELSE 'Equilibrado'
       END AS perfil_financiero
FROM usuarios u
INNER JOIN cuentas c     ON c.id_usuario = u.id_usuario
INNER JOIN movimientos m ON m.id_cuenta  = c.id_cuenta
WHERE m.fecha >= '2026-03-01'
  AND m.fecha <  '2026-04-01'
GROUP BY u.id_usuario, u.nombre
ORDER BY tasa_ahorro_pct DESC;


-- =====================================================================
-- C09. PORCENTAJE DE GASTO POR CATEGORÍA (SUBCONSULTA + ROUND)
-- =====================================================================
-- El denominador es una subconsulta escalar: el total de gastos
-- del mismo período. ROUND(...,2) deja dos decimales.
SELECT ca.nombre AS categoria,
       SUM(m.importe) AS total_gastado,
       COUNT(*)       AS cantidad,
       ROUND(SUM(m.importe)
             / (SELECT SUM(m2.importe)
                FROM movimientos m2
                WHERE m2.tipo = 'GASTO'
                  AND m2.fecha >= '2026-03-01'
                  AND m2.fecha <  '2026-04-01')
             * 100, 2)     AS porcentaje
FROM movimientos m
INNER JOIN categorias ca ON ca.id_categoria = m.id_categoria
WHERE m.tipo = 'GASTO'
  AND m.fecha >= '2026-03-01'
  AND m.fecha <  '2026-04-01'
GROUP BY ca.id_categoria, ca.nombre
ORDER BY total_gastado DESC;

-- 9.b Porcentaje acumulado (útil para ver si pocas categorías
--     concentran el gasto). MariaDB 10.4 no tiene QUALIFY, por eso
--     se usa una subconsulta en el FROM.
SELECT categoria, total_gastado, porcentaje,
       ROUND(SUM(porcentaje) OVER (ORDER BY total_gastado DESC), 2) AS acumulado_pct
FROM (
    SELECT ca.nombre AS categoria,
           SUM(m.importe) AS total_gastado,
           ROUND(SUM(m.importe)
                 / (SELECT SUM(m2.importe)
                    FROM movimientos m2
                    WHERE m2.tipo = 'GASTO'
                      AND m2.fecha >= '2026-03-01'
                      AND m2.fecha <  '2026-04-01')
                 * 100, 2) AS porcentaje
    FROM movimientos m
    INNER JOIN categorias ca ON ca.id_categoria = m.id_categoria
    WHERE m.tipo = 'GASTO'
      AND m.fecha >= '2026-03-01'
      AND m.fecha <  '2026-04-01'
    GROUP BY ca.id_categoria, ca.nombre
) AS gastos_por_categoria
ORDER BY total_gastado DESC;


-- =====================================================================
-- C10. SALDO ESTIMADO POR CUENTA
--      saldo_inicial + ingresos - gastos  (LEFT JOIN + COALESCE)
-- =====================================================================
-- LEFT JOIN: una cuenta sin movimientos igual aparece en el listado.
-- COALESCE: convierte el NULL de SUM() en 0.
-- 10.a Saldo con todos los movimientos cargados
SELECT u.nombre  AS usuario,
       c.nombre  AS cuenta,
       c.tipo,
       c.saldo_inicial,
       COALESCE(SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.importe END), 0) AS ingresos,
       COALESCE(SUM(CASE WHEN m.tipo = 'GASTO'   THEN m.importe END), 0) AS gastos,
       ROUND(c.saldo_inicial
             + COALESCE(SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.importe
                                 ELSE -m.importe END), 0), 2)             AS saldo_estimado
FROM cuentas c
INNER JOIN usuarios u ON u.id_usuario = c.id_usuario
LEFT  JOIN movimientos m ON m.id_cuenta = c.id_cuenta
GROUP BY c.id_cuenta, u.nombre, c.nombre, c.tipo, c.saldo_inicial
ORDER BY u.nombre, c.id_cuenta;

-- 10.b Saldo al cierre de marzo (2026-03-31).
--      IMPORTANTE: el filtro de fecha va en el ON del LEFT JOIN, no en
--      el WHERE; si fuera en el WHERE se descartarían las cuentas sin
--      movimientos y el LEFT JOIN se comportaría como INNER JOIN.
SELECT u.nombre  AS usuario,
       c.nombre  AS cuenta,
       c.saldo_inicial,
       ROUND(c.saldo_inicial
             + COALESCE(SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.importe
                                 ELSE -m.importe END), 0), 2) AS saldo_al_31_03
FROM cuentas c
INNER JOIN usuarios u ON u.id_usuario = c.id_usuario
LEFT  JOIN movimientos m
       ON m.id_cuenta = c.id_cuenta
      AND m.fecha <= '2026-03-31'
GROUP BY c.id_cuenta, u.nombre, c.nombre, c.saldo_inicial
ORDER BY u.nombre, c.id_cuenta;

-- 10.c Control: cuentas sin ningún movimiento (hoy no hay, devuelve
--     0 filas; sirve para detectar cuentas "vacías" o mal cargadas)
SELECT c.id_cuenta, c.nombre, u.nombre AS usuario
FROM cuentas c
INNER JOIN usuarios u ON u.id_usuario = c.id_usuario
LEFT  JOIN movimientos m ON m.id_cuenta = c.id_cuenta
WHERE m.id_movimiento IS NULL;


-- =====================================================================
-- C11. CONSULTA PROPIA: MOVIMIENTOS ENTRE DOS FECHAS
--      (ventana del 10/03/2026 al 20/03/2026)
-- =====================================================================
-- 11.a Detalle de la ventana, con nombres en lugar de IDs
SELECT m.fecha,
       u.nombre  AS usuario,
       c.nombre  AS cuenta,
       ca.nombre AS categoria,
       m.tipo,
       m.importe,
       m.descripcion
FROM movimientos m
INNER JOIN cuentas    c  ON c.id_cuenta     = m.id_cuenta
INNER JOIN usuarios   u  ON u.id_usuario    = c.id_usuario
INNER JOIN categorias ca ON ca.id_categoria = m.id_categoria
WHERE m.fecha BETWEEN '2026-03-10' AND '2026-03-20'
ORDER BY m.fecha, m.importe DESC;

-- 11.b Resumen de esa misma ventana agrupado por usuario y tipo
SELECT u.nombre AS usuario,
       m.tipo,
       COUNT(*)     AS cantidad,
       SUM(m.importe) AS total
FROM movimientos m
INNER JOIN cuentas  c ON c.id_cuenta  = m.id_cuenta
INNER JOIN usuarios u ON u.id_usuario = c.id_usuario
WHERE m.fecha BETWEEN '2026-03-10' AND '2026-03-20'
GROUP BY u.id_usuario, u.nombre, m.tipo
ORDER BY u.nombre, m.tipo;

-- 11.c Versión parametrizable (la que usará Java en la Entrega 2).
--     En phpMyAdmin no se ejecuta tal cual: hay que reemplazar los
--     valores. En la aplicación se envían con PreparedStatement.
-- SELECT ... FROM movimientos
--  WHERE fecha BETWEEN ? AND ?;   -- fecha_desde, fecha_hasta


-- =====================================================================
-- C12. EXTRAS ÚTILES PARA EL INFORME
-- =====================================================================
-- 12.a Gasto por medio de pago (GROUP BY + HAVING)
SELECT medio_pago,
       COUNT(*)       AS cantidad,
       SUM(importe)   AS total,
       ROUND(SUM(importe)
             / (SELECT SUM(importe) FROM movimientos
                WHERE tipo = 'GASTO'
                  AND fecha >= '2026-03-01' AND fecha < '2026-04-01')
             * 100, 2) AS porcentaje
FROM movimientos
WHERE tipo = 'GASTO'
  AND fecha >= '2026-03-01'
  AND fecha <  '2026-04-01'
GROUP BY medio_pago
HAVING COUNT(*) >= 1
ORDER BY total DESC;

-- 12.b Ranking de categorías de gasto (ORDER BY + LIMIT)
SELECT ca.nombre AS categoria, SUM(m.importe) AS total
FROM movimientos m
INNER JOIN categorias ca ON ca.id_categoria = m.id_categoria
WHERE m.tipo = 'GASTO'
  AND m.fecha >= '2026-03-01'
  AND m.fecha <  '2026-04-01'
GROUP BY ca.id_categoria, ca.nombre
ORDER BY total DESC
LIMIT 3;

-- 12.c Gasto diario promedio de marzo (días con al menos un gasto)
SELECT ROUND(SUM(importe) / COUNT(DISTINCT fecha), 2) AS gasto_promedio_por_dia
FROM movimientos
WHERE tipo = 'GASTO'
  AND fecha >= '2026-03-01'
  AND fecha <  '2026-04-01';
