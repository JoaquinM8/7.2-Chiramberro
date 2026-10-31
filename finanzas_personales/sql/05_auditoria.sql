-- =====================================================================
-- PROYECTO: Finanzas Personales Inteligentes
-- ENTREGA 1 - Consultas de auditoría de los datos cargados
-- ARCHIVO: 05_auditoria.sql
-- =====================================================================
-- Sirven para validar cualquier script de datos (el propio o el del
-- docente) antes de sacar conclusiones. Ideal: todas devuelven 0 filas
-- o los valores esperados.
-- =====================================================================

USE finanzas_personales;

-- A01. Conteo de filas por tabla (mínimos de la consigna)
SELECT 'usuarios' AS tabla, COUNT(*) AS filas FROM usuarios
UNION ALL SELECT 'cuentas',    COUNT(*) FROM cuentas
UNION ALL SELECT 'categorias', COUNT(*) FROM categorias
UNION ALL SELECT 'movimientos',COUNT(*) FROM movimientos;

-- A02. Cuentas huérfanas (usuario inexistente) -> debe dar 0 filas
SELECT c.id_cuenta, c.id_usuario
FROM cuentas c
LEFT JOIN usuarios u ON u.id_usuario = c.id_usuario
WHERE u.id_usuario IS NULL;

-- A03. Movimientos huérfanos (cuenta o categoría inexistente) -> 0 filas
SELECT m.id_movimiento, m.id_cuenta, m.id_categoria
FROM movimientos m
LEFT JOIN cuentas    c ON c.id_cuenta     = m.id_cuenta
LEFT JOIN categorias k ON k.id_categoria  = m.id_categoria
WHERE c.id_cuenta IS NULL OR k.id_categoria IS NULL;

-- A04. Incoherencia tipo de movimiento vs tipo_permitido de la categoría.
--      El modelo NO la impide (limitación documentada); esta consulta la detecta.
SELECT m.id_movimiento, m.tipo, ca.nombre AS categoria, ca.tipo_permitido
FROM movimientos m
INNER JOIN categorias ca ON ca.id_categoria = m.id_categoria
WHERE ca.tipo_permitido <> 'AMBOS'
  AND ca.tipo_permitido <> m.tipo;

-- A05. Posibles duplicados por doble carga del script -> 0 filas
SELECT id_cuenta, fecha, importe, descripcion, COUNT(*) AS repeticiones
FROM movimientos
GROUP BY id_cuenta, fecha, importe, descripcion
HAVING COUNT(*) > 1;

-- A06. Importes no positivos -> 0 filas (el CHECK debería impedirlos)
SELECT id_movimiento, tipo, importe
FROM movimientos
WHERE importe <= 0;

-- A07. Movimientos fuera del rango temporal previsto (2026) -> 0 filas
SELECT id_movimiento, fecha, descripcion
FROM movimientos
WHERE fecha < '2026-01-01' OR fecha >= '2027-01-01';

-- A08. Cuadre global: saldos iniciales + ingresos - gastos
--      debe coincidir con la suma de los saldos por cuenta.
SELECT (SELECT SUM(saldo_inicial) FROM cuentas)                        AS saldos_iniciales,
       (SELECT SUM(importe) FROM movimientos WHERE tipo = 'INGRESO')   AS ingresos,
       (SELECT SUM(importe) FROM movimientos WHERE tipo = 'GASTO')     AS gastos,
       (SELECT SUM(saldo_inicial) FROM cuentas)
       + (SELECT SUM(importe) FROM movimientos WHERE tipo = 'INGRESO')
       - (SELECT SUM(importe) FROM movimientos WHERE tipo = 'GASTO')   AS saldo_total_calculado;

-- A09. Alerta de liquidez: cuentas con saldo estimado negativo
--      (puede ser real o síntoma de movimientos faltantes)
SELECT u.nombre AS usuario, c.nombre AS cuenta,
       ROUND(c.saldo_inicial
             + COALESCE(SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.importe
                                 ELSE -m.importe END), 0), 2) AS saldo_estimado
FROM cuentas c
INNER JOIN usuarios u ON u.id_usuario = c.id_usuario
LEFT  JOIN movimientos m ON m.id_cuenta = c.id_cuenta
GROUP BY c.id_cuenta, u.nombre, c.nombre, c.saldo_inicial
HAVING saldo_estimado < 0;

-- A10. Todos los usuarios deben tener al menos dos cuentas.
--      Devuelve los que tengan menos de 2 (en este dataset: 0 filas).
SELECT u.nombre, COUNT(c.id_cuenta) AS cuentas
FROM usuarios u
LEFT JOIN cuentas c ON c.id_usuario = u.id_usuario
GROUP BY u.id_usuario, u.nombre
HAVING COUNT(c.id_cuenta) < 2;
