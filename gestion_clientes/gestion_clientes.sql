-- =============================================================
-- Sistema de Gestión Comercial - instalación completa (Semana 8)
-- =============================================================
-- Este script crea la base y las tablas si no existen. No elimina
-- información: sobre una instalación anterior usar
-- `sql/migracion_semana_8.sql` para conservar los datos.
--
-- Credenciales de laboratorio (cambiar en producción):
--   admin     / Admin2026!     -> Administrador
--   operador  / Operador2026!  -> Operador
--   consulta  / Consulta2026!  -> Operador (cuenta histórica)
-- =============================================================

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS `gestion_clientes`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE `gestion_clientes`;

-- -------------------------------------------------------------
-- Clientes
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `clientes` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(80) NOT NULL,
  `email` VARCHAR(120) NOT NULL,
  `telefono` VARCHAR(30) DEFAULT NULL,
  `ciudad` VARCHAR(60) DEFAULT NULL,
  `fecha_alta` DATE NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Usuarios
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `usuarios` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `usuario` VARCHAR(50) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `nombre` VARCHAR(100) NOT NULL,
  `activo` TINYINT(1) NOT NULL DEFAULT 1,
  `rol` ENUM('admin', 'operador') NOT NULL DEFAULT 'operador',
  `creado_en` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `actualizado_en` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `usuario` (`usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Pedidos
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `pedidos` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `cliente_id` INT(11) NOT NULL,
  `descripcion` VARCHAR(150) NOT NULL,
  `importe` DECIMAL(10,2) NOT NULL,
  `fecha_pedido` DATE NOT NULL,
  `estado` VARCHAR(20) NOT NULL DEFAULT 'pendiente',
  `usuario_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_pedido_cliente` (`cliente_id`),
  KEY `fk_pedidos_usuario` (`usuario_id`),
  KEY `idx_pedidos_estado` (`estado`),
  KEY `idx_pedidos_fecha` (`fecha_pedido`),
  CONSTRAINT `fk_pedido_cliente`
    FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_pedidos_usuario`
    FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_pedidos_importe` CHECK (`importe` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Datos de demostración
-- -------------------------------------------------------------
INSERT IGNORE INTO `clientes`
  (`id`, `nombre`, `email`, `telefono`, `ciudad`, `fecha_alta`)
VALUES
  (1, 'Lucía Fernández', 'lucia.fernandez@correo.com', '11-4567-8890', 'Buenos Aires', '2025-03-12'),
  (2, 'Martín Gómez', 'martin.gomez@correo.com', '11-5234-1122', 'Córdoba', '2025-04-02'),
  (3, 'Sofía Ramírez', 'sofia.ramirez@correo.com', '11-3987-6543', 'Rosario', '2025-05-19'),
  (4, 'Diego Morales', 'diego.morales@correo.com', '11-4765-2030', 'La Plata', '2025-06-07'),
  (5, 'Camila Ortiz', 'camila.ortiz@correo.com', '11-6123-7788', 'Mendoza', '2025-07-21'),
  (6, 'Joaquín Peralta', 'joaquin.peralta@correo.com', '11-3344-9900', 'Mar del Plata', '2025-08-30'),
  (7, 'Valentina Ruiz', 'valentina.ruiz@correo.com', '11-5877-3322', 'Bahía Blanca', '2025-09-14'),
  (8, 'Nicolás Herrera', 'nicolas.herrera@correo.com', '11-7012-4455', 'Salta', '2025-10-05'),
  (9, 'Brenda Sosa', 'brenda.sosa@correo.com', '11-4688-1234', 'Tucumán', '2025-11-18'),
  (10, 'Agustín Núñez', 'agustin.nunez@correo.com', '11-9223-5566', 'Neuquén', '2025-12-01'),
  (11, 'María José Paz', 'maria.paz@correo.com', '11-4555-7788', 'San Juan', '2026-01-15'),
  (12, 'Tomás Álvarez', 'tomas.alvarez@correo.com', '11-6777-1122', 'Santa Fe', '2026-02-20');

INSERT IGNORE INTO `usuarios`
  (`id`, `usuario`, `password`, `nombre`, `activo`, `rol`)
VALUES
  (1, 'admin', '$2y$10$mDoawv7cJc4DvGYgJEybouaHD8kVjC/dC97vAJFwtT7eflQ5lpGhO', 'Administrador', 1, 'admin'),
  (2, 'consulta', '$2y$10$GNc3oaFWEPN40.//8ACOSOjrKJ2wNl2cW68d1kU/zmrY/i9U0DjnC', 'Operador histórico', 1, 'operador'),
  (3, 'operador', '$2y$10$6/F5sRySYxeTsWiBpHp.1OEfxoJvwdIt.HbaHJtk4ZHKztmzXELua', 'Operador de ventas', 1, 'operador');

INSERT IGNORE INTO `pedidos`
  (`id`, `cliente_id`, `descripcion`, `importe`, `fecha_pedido`, `estado`, `usuario_id`)
VALUES
  (1, 1, 'Notebook Lenovo IdeaPad', 789999.90, '2026-02-03', 'pagado', 1),
  (2, 1, 'Mouse inalámbrico Logitech', 45990.00, '2026-06-17', 'pagado', 1),
  (3, 2, 'Planilla contable anual 2026', 125000.00, '2026-01-22', 'pendiente', 1),
  (4, 2, 'Suscripción Office 365 (anual)', 189900.00, '2026-07-09', 'pagado', 1),
  (5, 3, 'Cámara de seguridad Hikvision', 215400.50, '2026-03-11', 'enviado', 1),
  (6, 3, 'Impresora HP multifunción', 389999.00, '2026-08-25', 'enviado', 1),
  (7, 4, 'Servicio de mantenimiento PC x3', 95000.00, '2026-04-19', 'pagado', 1),
  (8, 4, 'Kit de herramientas de red', 67300.75, '2026-09-02', 'pendiente', 1),
  (9, 5, 'Escritorio de madera 140 cm', 168750.00, '2026-05-06', 'pagado', 1),
  (10, 6, 'Notebook Acer para estudio', 949999.00, '2026-02-27', 'pagado', 1),
  (11, 6, 'Auriculares HyperX Cloud II', 97800.00, '2026-09-14', 'enviado', 1),
  (12, 7, 'Vestuario del uniforme (8 unidades)', 245600.00, '2026-03-30', 'pagado', 1),
  (13, 8, 'Curso de capacitación en hojas de cálculo', 86500.00, '2026-06-05', 'pendiente', 1),
  (14, 9, 'Cámaras IP para depósito (x4)', 540300.40, '2026-07-28', 'enviado', 1),
  (15, 10, 'Abono mensual de almacenaje', 62000.00, '2026-08-10', 'pendiente', 1);

-- Fin del script.
