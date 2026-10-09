-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 04-10-2026 a las 18:06:44
-- Versión del servidor: 12.3.2-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `gestion_clientes`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `clientes`
--

CREATE TABLE `clientes` (
  `id` int(11) NOT NULL,
  `nombre` varchar(80) NOT NULL,
  `email` varchar(120) NOT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `ciudad` varchar(60) DEFAULT NULL,
  `fecha_alta` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `clientes`
--

INSERT INTO `clientes` (`id`, `nombre`, `email`, `telefono`, `ciudad`, `fecha_alta`) VALUES
(1, 'Lucía Fernández', 'lucia.fernandez@correo.com', '11-4567-8890', 'Buenos Aires', '2025-03-12'),
(2, 'Martín Gómez', 'martin.gomez@correo.com', '11-5234-1122', 'Córdoba', '2025-04-02'),
(3, 'Sofía Ramírez', 'sofia.ramirez@correo.com', '11-3987-6543', 'Rosario', '2025-05-19'),
(4, 'Diego Morales', 'diego.morales@correo.com', '11-4765-2030', 'La Plata', '2025-06-07'),
(5, 'Camila Ortiz', 'camila.ortiz@correo.com', '11-6123-7788', 'Mendoza', '2025-07-21'),
(6, 'Joaquín Peralta', 'joaquin.peralta@correo.com', '11-3344-9900', 'Mar del Plata', '2025-08-30'),
(7, 'Valentina Ruiz', 'valentina.ruiz@correo.com', '11-5877-3322', 'Bahía Blanca', '2025-09-14'),
(8, 'Nicolás Herrera', 'nicolas.herrera@correo.com', '11-7012-4455', 'Salta', '2025-10-05'),
(9, 'Brenda Sosa', 'brenda.sosa@correo.com', '11-4688-1234', 'Tucumán', '2025-11-18'),
(10, 'Agustín Núñez', 'agustin.nunez@correo.com', '11-9223-5566', 'Neuquén', '2025-12-01');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedidos`
--

CREATE TABLE `pedidos` (
  `id` int(11) NOT NULL,
  `cliente_id` int(11) NOT NULL,
  `descripcion` varchar(150) NOT NULL,
  `importe` decimal(10,2) NOT NULL,
  `fecha_pedido` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `pedidos`
--

INSERT INTO `pedidos` (`id`, `cliente_id`, `descripcion`, `importe`, `fecha_pedido`) VALUES
(1, 1, 'Notebook Lenovo IdeaPad', 789999.90, '2026-02-03'),
(2, 1, 'Mouse inalámbrico Logitech', 45990.00, '2026-06-17'),
(3, 2, 'Planilla contable anual 2026', 125000.00, '2026-01-22'),
(4, 2, 'Suscripción Office 365 (anual)', 189900.00, '2026-07-09'),
(5, 3, 'Camara de seguridad Hikvision', 215400.50, '2026-03-11'),
(6, 3, 'Impresora HP multifuncion', 389999.00, '2026-08-25'),
(7, 4, 'Servicio de mantenimiento PC x3', 95000.00, '2026-04-19'),
(8, 4, 'Kit de herramientas de red', 67300.75, '2026-09-02'),
(9, 5, 'Escritorio de madera 140 cm', 168750.00, '2026-05-06'),
(10, 6, 'Notebook Acer para estudio', 949999.00, '2026-02-27'),
(11, 6, 'Auriculares HyperX Cloud II', 97800.00, '2026-09-14'),
(12, 7, 'Vestuario del uniforme (8 unidades)', 245600.00, '2026-03-30'),
(13, 8, 'Curso de capacitación en hojas de cálculo', 86500.00, '2026-06-05'),
(14, 9, 'Cámaras IP para depósito (x4)', 540300.40, '2026-07-28'),
(15, 10, 'Abono mensual de almacenaje', 62000.00, '2026-08-10');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `clientes`
--
ALTER TABLE `clientes`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `pedidos`
--
ALTER TABLE `pedidos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_pedido_cliente` (`cliente_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `clientes`
--
ALTER TABLE `clientes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT de la tabla `pedidos`
--
ALTER TABLE `pedidos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `pedidos`
--
ALTER TABLE `pedidos`
  ADD CONSTRAINT `fk_pedido_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
