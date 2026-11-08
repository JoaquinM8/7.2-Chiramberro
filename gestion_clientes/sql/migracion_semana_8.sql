-- =============================================================
-- Sistema de Gestión Comercial - migración Semana 8
-- =============================================================
-- Este script adapta una base de datos creada en semanas anteriores.
-- NO elimina tablas ni datos: agrega columnas, normaliza roles y
-- reemplaza la regla de borrado en cascada de pedidos por RESTRICT.
--
-- Antes de ejecutar:
--   1. Hacer una copia de seguridad de la base (mysqldump).
--   2. Verificar que exista al menos un usuario con rol 'admin'.
--   3. Ejecutarlo una sola vez sobre la base `gestion_clientes`.
--
-- Si se instala desde cero, usar `gestion_clientes.sql`, que ya incluye
-- el esquema final. Este archivo es para instalaciones existentes.
-- =============================================================

SET NAMES utf8mb4;
USE `gestion_clientes`;

-- -------------------------------------------------------------
-- 1. Usuarios: roles admin/operador y marcas de trazabilidad
-- -------------------------------------------------------------
-- Se agrega temporalmente 'consulta' al ENUM para poder convertir
-- los usuarios históricos sin perder filas.
ALTER TABLE `usuarios`
  MODIFY COLUMN `rol` ENUM('admin', 'operador', 'consulta')
  NOT NULL DEFAULT 'operador';

UPDATE `usuarios`
SET `rol` = 'operador'
WHERE `rol` = 'consulta';

-- Ahora el modelo solo admite los dos roles del sistema.
ALTER TABLE `usuarios`
  MODIFY COLUMN `rol` ENUM('admin', 'operador')
  NOT NULL DEFAULT 'operador';

ALTER TABLE `usuarios`
  ADD COLUMN IF NOT EXISTS `creado_en` TIMESTAMP NOT NULL
    DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN IF NOT EXISTS `actualizado_en` TIMESTAMP NOT NULL
    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Cuenta de operador para laboratorio. No reemplaza usuarios existentes.
INSERT INTO `usuarios` (`usuario`, `password`, `nombre`, `activo`, `rol`)
SELECT 'operador',
       '$2y$10$6/F5sRySYxeTsWiBpHp.1OEfxoJvwdIt.HbaHJtk4ZHKztmzXELua',
       'Operador de ventas',
       1,
       'operador'
WHERE NOT EXISTS (
  SELECT 1 FROM `usuarios` WHERE `usuario` = 'operador'
);

-- -------------------------------------------------------------
-- 2. Pedidos: estado, usuario responsable y relación con clientes
-- -------------------------------------------------------------
ALTER TABLE `pedidos`
  ADD COLUMN IF NOT EXISTS `estado` VARCHAR(20) NOT NULL
    DEFAULT 'pendiente' AFTER `fecha_pedido`,
  ADD COLUMN IF NOT EXISTS `usuario_id` INT NULL
    AFTER `estado`;

-- Asigna los pedidos históricos al primer administrador activo.
-- Si no hubiera administrador, usa el primer usuario disponible.
SET @admin_id := (
  SELECT `id`
  FROM `usuarios`
  WHERE `rol` = 'admin' AND `activo` = 1
  ORDER BY `id`
  LIMIT 1
);
SET @fallback_id := (
  SELECT `id`
  FROM `usuarios`
  ORDER BY `id`
  LIMIT 1
);
UPDATE `pedidos`
SET `usuario_id` = COALESCE(@admin_id, @fallback_id)
WHERE `usuario_id` IS NULL;

-- El responsable es obligatorio para conservar trazabilidad.
ALTER TABLE `pedidos`
  MODIFY COLUMN `usuario_id` INT NOT NULL;

-- -------------------------------------------------------------
-- 3. Claves foráneas: pedidos no se eliminan al borrar un cliente
-- -------------------------------------------------------------
-- Se elimina la FK histórica con CASCADE si existe y se recrea con
-- RESTRICT. La aplicación además comprueba dependencias antes de borrar.
SET @fk_cliente := (
  SELECT COUNT(*)
  FROM information_schema.TABLE_CONSTRAINTS
  WHERE CONSTRAINT_SCHEMA = DATABASE()
    AND TABLE_NAME = 'pedidos'
    AND CONSTRAINT_NAME = 'fk_pedido_cliente'
    AND CONSTRAINT_TYPE = 'FOREIGN KEY'
);
SET @sql := IF(
  @fk_cliente > 0,
  'ALTER TABLE `pedidos` DROP FOREIGN KEY `fk_pedido_cliente`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE `pedidos`
  ADD CONSTRAINT `fk_pedido_cliente`
  FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`)
  ON DELETE RESTRICT ON UPDATE CASCADE;

-- FK del usuario responsable. Se crea solo si no existe.
SET @fk_usuario := (
  SELECT COUNT(*)
  FROM information_schema.TABLE_CONSTRAINTS
  WHERE CONSTRAINT_SCHEMA = DATABASE()
    AND TABLE_NAME = 'pedidos'
    AND CONSTRAINT_NAME = 'fk_pedidos_usuario'
    AND CONSTRAINT_TYPE = 'FOREIGN KEY'
);
SET @sql := IF(
  @fk_usuario = 0,
  'ALTER TABLE `pedidos` ADD CONSTRAINT `fk_pedidos_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- -------------------------------------------------------------
-- 4. Índices de apoyo para filtros y reportes
-- -------------------------------------------------------------
SET @idx_estado := (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'pedidos'
    AND INDEX_NAME = 'idx_pedidos_estado'
);
SET @sql := IF(
  @idx_estado = 0,
  'ALTER TABLE `pedidos` ADD INDEX `idx_pedidos_estado` (`estado`)',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx_fecha := (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'pedidos'
    AND INDEX_NAME = 'idx_pedidos_fecha'
);
SET @sql := IF(
  @idx_fecha = 0,
  'ALTER TABLE `pedidos` ADD INDEX `idx_pedidos_fecha` (`fecha_pedido`)',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- -------------------------------------------------------------
-- 5. Verificación rápida
-- -------------------------------------------------------------
SELECT
  (SELECT COUNT(*) FROM `clientes`) AS clientes,
  (SELECT COUNT(*) FROM `pedidos`) AS pedidos,
  (SELECT COUNT(*) FROM `usuarios`) AS usuarios,
  (SELECT COUNT(*) FROM `pedidos` WHERE `usuario_id` IS NULL) AS pedidos_sin_usuario;

-- Fin del script.
