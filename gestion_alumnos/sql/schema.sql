-- =====================================================================
-- PROYECTO 7° - "gestion_alumnos"
-- Script de creación de la base de datos (MySQL / MariaDB - XAMPP)
-- ---------------------------------------------------------------------
-- Uso (desde la consola de MySQL de XAMPP):
--     mysql -u root < sql/schema.sql
--
-- O bien desde phpMyAdmin:_importar -> seleccionar este archivo.
--
-- El script es "idempotente": se puede volver a ejecutar en cualquier
-- momento porque primero elimina la base si ya existe.
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1) BASE DE DATOS
-- ---------------------------------------------------------------------
DROP DATABASE IF EXISTS escuela_java;
CREATE DATABASE escuela_java DEFAULT CHARACTER SET utf8mb4;
USE escuela_java;

-- ---------------------------------------------------------------------
-- 2) TABLA: cursos
-- ---------------------------------------------------------------------
CREATE TABLE cursos (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    nombre  VARCHAR(50) NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ---------------------------------------------------------------------
-- 3) TABLA: alumnos
--    La clave foranea fk_alumno_curso garantiza que todo alumno
--    pertenezca a un curso existente (integridad referencial).
-- ---------------------------------------------------------------------
CREATE TABLE alumnos (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(60)  NOT NULL,
    apellido  VARCHAR(60)  NOT NULL,
    email     VARCHAR(120) NOT NULL UNIQUE,
    edad      INT          NOT NULL,
    curso_id  INT          NOT NULL,
    CONSTRAINT fk_alumno_curso
        FOREIGN KEY (curso_id) REFERENCES cursos (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ---------------------------------------------------------------------
-- 4) DATOS DE PRUEBA - CURSOS (40 registros)
--    Formato: "<año>°<división>". Años del 1° al 7°, divisiones del 1 al 7.
--    Se insertan 6 divisiones en 1°, 2° y 3°; 7 en 4°; y 5 en 5°, 6° y 7°.
-- ---------------------------------------------------------------------
INSERT INTO cursos (nombre) VALUES
    ('1°1'), ('1°2'), ('1°3'), ('1°4'), ('1°5'), ('1°6'),
    ('2°1'), ('2°2'), ('2°3'), ('2°4'), ('2°5'), ('2°6'),
    ('3°1'), ('3°2'), ('3°3'), ('3°4'), ('3°5'), ('3°6'),
    ('4°1'), ('4°2'), ('4°3'), ('4°4'), ('4°5'), ('4°6'), ('4°7'),
    ('5°1'), ('5°2'), ('5°3'), ('5°4'), ('5°5'),
    ('6°1'), ('6°2'), ('6°3'), ('6°4'), ('6°5'),
    ('7°1'), ('7°2'), ('7°3'), ('7°4'), ('7°5');

-- ---------------------------------------------------------------------
-- 5) DATOS DE PRUEBA - ALUMNOS (80 registros: 2 por cada curso)
--    El id del curso se resuelve por NOMBRE (subconsulta), nunca con un
--    id fijo: asi el script funciona aunque cambien los ids de la tabla
--    cursos (evita el error 1452 de clave foranea).
--    EDAD = 11 + año que cursa (el alumno ingresa a 1° con 12 años y no
--    repite): 1°=12, 2°=13, 3°=14, 4°=15, 5°=16, 6°=17, 7°=18.
-- ---------------------------------------------------------------------
INSERT IGNORE INTO alumnos (nombre, apellido, email, edad, curso_id)
SELECT v.nombre, v.apellido, v.email, v.edad, c.id
FROM (
    SELECT 'Adrian' AS nombre, 'Acosta' AS apellido, 'adrian.acosta@escuela.edu' AS email, 12 AS edad, '1°1' AS curso
    UNION ALL SELECT 'Karina', 'Peralta', 'karina.peralta@escuela.edu', 12, '1°1'
    UNION ALL SELECT 'Agustin', 'Benitez', 'agustin.benitez@escuela.edu', 12, '1°2'
    UNION ALL SELECT 'Kevin', 'Quiroga', 'kevin.quiroga@escuela.edu', 12, '1°2'
    UNION ALL SELECT 'Belen', 'Cabrera', 'belen.cabrera@escuela.edu', 12, '1°3'
    UNION ALL SELECT 'Laura', 'Ramirez', 'laura.ramirez@escuela.edu', 12, '1°3'
    UNION ALL SELECT 'Bianca', 'Dominguez', 'bianca.dominguez@escuela.edu', 12, '1°4'
    UNION ALL SELECT 'Marcelo', 'Suarez', 'marcelo.suarez@escuela.edu', 12, '1°4'
    UNION ALL SELECT 'Camilo', 'Ferreyra', 'camilo.ferreyra@escuela.edu', 12, '1°5'
    UNION ALL SELECT 'Monica', 'Torres', 'monica.torres@escuela.edu', 12, '1°5'
    UNION ALL SELECT 'Claudio', 'Gimenez', 'claudio.gimenez@escuela.edu', 12, '1°6'
    UNION ALL SELECT 'Natalia', 'Vargas', 'natalia.vargas@escuela.edu', 12, '1°6'
    UNION ALL SELECT 'Daiana', 'Hernandez', 'daiana.hernandez@escuela.edu', 13, '2°1'
    UNION ALL SELECT 'Nicolas', 'Zambrano', 'nicolas.zambrano@escuela.edu', 13, '2°1'
    UNION ALL SELECT 'Delfina', 'Ibarra', 'delfina.ibarra@escuela.edu', 13, '2°2'
    UNION ALL SELECT 'Omar', 'Aguirre', 'omar.aguirre@escuela.edu', 13, '2°2'
    UNION ALL SELECT 'Esteban', 'Juarez', 'esteban.juarez@escuela.edu', 13, '2°3'
    UNION ALL SELECT 'Oscar', 'Barrera', 'oscar.barrera@escuela.edu', 13, '2°3'
    UNION ALL SELECT 'Ezequiel', 'Lara', 'ezequiel.lara@escuela.edu', 13, '2°4'
    UNION ALL SELECT 'Pablo', 'Carrizo', 'pablo.carrizo@escuela.edu', 13, '2°4'
    UNION ALL SELECT 'Fernanda', 'Medina', 'fernanda.medina@escuela.edu', 13, '2°5'
    UNION ALL SELECT 'Paula', 'Figueroa', 'paula.figueroa@escuela.edu', 13, '2°5'
    UNION ALL SELECT 'Flavia', 'Navarro', 'flavia.navarro@escuela.edu', 13, '2°6'
    UNION ALL SELECT 'Ramiro', 'Guzman', 'ramiro.guzman@escuela.edu', 13, '2°6'
    UNION ALL SELECT 'Gabriel', 'Olmedo', 'gabriel.olmedo@escuela.edu', 14, '3°1'
    UNION ALL SELECT 'Roger', 'Ledesma', 'roger.ledesma@escuela.edu', 14, '3°1'
    UNION ALL SELECT 'Gustavo', 'Peralta', 'gustavo.peralta@escuela.edu', 14, '3°2'
    UNION ALL SELECT 'Silvia', 'Molina', 'silvia.molina@escuela.edu', 14, '3°2'
    UNION ALL SELECT 'Hernan', 'Quiroga', 'hernan.quiroga@escuela.edu', 14, '3°3'
    UNION ALL SELECT 'Tobias', 'Ocampo', 'tobias.ocampo@escuela.edu', 14, '3°3'
    UNION ALL SELECT 'Horacio', 'Ramirez', 'horacio.ramirez@escuela.edu', 14, '3°4'
    UNION ALL SELECT 'Ursula', 'Paz', 'ursula.paz@escuela.edu', 14, '3°4'
    UNION ALL SELECT 'Ignacio', 'Suarez', 'ignacio.suarez@escuela.edu', 14, '3°5'
    UNION ALL SELECT 'Vicente', 'Roldan', 'vicente.roldan@escuela.edu', 14, '3°5'
    UNION ALL SELECT 'Ines', 'Torres', 'ines.torres@escuela.edu', 14, '3°6'
    UNION ALL SELECT 'Walter', 'Salas', 'walter.salas@escuela.edu', 14, '3°6'
    UNION ALL SELECT 'Jazmin', 'Vargas', 'jazmin.vargas@escuela.edu', 15, '4°1'
    UNION ALL SELECT 'Yanina', 'Tejeda', 'yanina.tejeda@escuela.edu', 15, '4°1'
    UNION ALL SELECT 'Joaquin', 'Zambrano', 'joaquin.zambrano@escuela.edu', 15, '4°2'
    UNION ALL SELECT 'Zaira', 'Villalba', 'zaira.villalba@escuela.edu', 15, '4°2'
    UNION ALL SELECT 'Karina', 'Aguirre', 'karina.aguirre@escuela.edu', 15, '4°3'
    UNION ALL SELECT 'Adrian', 'Ayala', 'adrian.ayala@escuela.edu', 15, '4°3'
    UNION ALL SELECT 'Kevin', 'Barrera', 'kevin.barrera@escuela.edu', 15, '4°4'
    UNION ALL SELECT 'Agustin', 'Bravo', 'agustin.bravo@escuela.edu', 15, '4°4'
    UNION ALL SELECT 'Laura', 'Carrizo', 'laura.carrizo@escuela.edu', 15, '4°5'
    UNION ALL SELECT 'Belen', 'Castro', 'belen.castro@escuela.edu', 15, '4°5'
    UNION ALL SELECT 'Marcelo', 'Figueroa', 'marcelo.figueroa@escuela.edu', 15, '4°6'
    UNION ALL SELECT 'Bianca', 'Duarte', 'bianca.duarte@escuela.edu', 15, '4°6'
    UNION ALL SELECT 'Monica', 'Guzman', 'monica.guzman@escuela.edu', 15, '4°7'
    UNION ALL SELECT 'Camilo', 'Espinosa', 'camilo.espinosa@escuela.edu', 15, '4°7'
    UNION ALL SELECT 'Natalia', 'Ledesma', 'natalia.ledesma@escuela.edu', 16, '5°1'
    UNION ALL SELECT 'Claudio', 'Fuentes', 'claudio.fuentes@escuela.edu', 16, '5°1'
    UNION ALL SELECT 'Nicolas', 'Molina', 'nicolas.molina@escuela.edu', 16, '5°2'
    UNION ALL SELECT 'Daiana', 'Gallardo', 'daiana.gallardo@escuela.edu', 16, '5°2'
    UNION ALL SELECT 'Omar', 'Ocampo', 'omar.ocampo@escuela.edu', 16, '5°3'
    UNION ALL SELECT 'Delfina', 'Acosta', 'delfina.acosta@escuela.edu', 16, '5°3'
    UNION ALL SELECT 'Oscar', 'Paz', 'oscar.paz@escuela.edu', 16, '5°4'
    UNION ALL SELECT 'Esteban', 'Benitez', 'esteban.benitez@escuela.edu', 16, '5°4'
    UNION ALL SELECT 'Pablo', 'Roldan', 'pablo.roldan@escuela.edu', 16, '5°5'
    UNION ALL SELECT 'Ezequiel', 'Cabrera', 'ezequiel.cabrera@escuela.edu', 16, '5°5'
    UNION ALL SELECT 'Paula', 'Salas', 'paula.salas@escuela.edu', 17, '6°1'
    UNION ALL SELECT 'Fernanda', 'Dominguez', 'fernanda.dominguez@escuela.edu', 17, '6°1'
    UNION ALL SELECT 'Ramiro', 'Tejeda', 'ramiro.tejeda@escuela.edu', 17, '6°2'
    UNION ALL SELECT 'Flavia', 'Ferreyra', 'flavia.ferreyra@escuela.edu', 17, '6°2'
    UNION ALL SELECT 'Roger', 'Villalba', 'roger.villalba@escuela.edu', 17, '6°3'
    UNION ALL SELECT 'Gabriel', 'Gimenez', 'gabriel.gimenez@escuela.edu', 17, '6°3'
    UNION ALL SELECT 'Silvia', 'Ayala', 'silvia.ayala@escuela.edu', 17, '6°4'
    UNION ALL SELECT 'Gustavo', 'Hernandez', 'gustavo.hernandez@escuela.edu', 17, '6°4'
    UNION ALL SELECT 'Tobias', 'Bravo', 'tobias.bravo@escuela.edu', 17, '6°5'
    UNION ALL SELECT 'Hernan', 'Ibarra', 'hernan.ibarra@escuela.edu', 17, '6°5'
    UNION ALL SELECT 'Ursula', 'Castro', 'ursula.castro@escuela.edu', 18, '7°1'
    UNION ALL SELECT 'Horacio', 'Juarez', 'horacio.juarez@escuela.edu', 18, '7°1'
    UNION ALL SELECT 'Vicente', 'Duarte', 'vicente.duarte@escuela.edu', 18, '7°2'
    UNION ALL SELECT 'Ignacio', 'Lara', 'ignacio.lara@escuela.edu', 18, '7°2'
    UNION ALL SELECT 'Walter', 'Espinosa', 'walter.espinosa@escuela.edu', 18, '7°3'
    UNION ALL SELECT 'Ines', 'Medina', 'ines.medina@escuela.edu', 18, '7°3'
    UNION ALL SELECT 'Yanina', 'Fuentes', 'yanina.fuentes@escuela.edu', 18, '7°4'
    UNION ALL SELECT 'Jazmin', 'Navarro', 'jazmin.navarro@escuela.edu', 18, '7°4'
    UNION ALL SELECT 'Zaira', 'Gallardo', 'zaira.gallardo@escuela.edu', 18, '7°5'
    UNION ALL SELECT 'Joaquin', 'Olmedo', 'joaquin.olmedo@escuela.edu', 18, '7°5'
) v
INNER JOIN cursos c ON c.nombre = v.curso;
