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
-- 4) DATOS DE PRUEBA - CURSOS (49 registros)
--    Formato: "<año>°<división>". Años del 1° al 7°, divisiones del 1 al 7.
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
-- 5) DATOS DE PRUEBA - ALUMNOS (10 registros)
--    Los identificadores de curso se toman de los ids generados arriba.
-- ---------------------------------------------------------------------
INSERT INTO alumnos (nombre, apellido, email, edad, curso_id) VALUES
    ('Sofía',   'González',  'sofia.gonzalez@escuela.edu',  13, 1),   -- 1°1
    ('Mateo',   'Rodríguez',  'mateo.rodriguez@escuela.edu', 13, 9),   -- 2°2
    ('Valentina','Fernández', 'valentina.fernandez@escuela.edu', 12, 17), -- 3°3
    ('Lucas',   'Herrera',    'lucas.herrera@escuela.edu',   13, 28),  -- 4°7
    ('Camila',  'Sosa',      'camila.sosa@escuela.edu',     14, 33),   -- 5°5
    ('Tomás',   'Villalba',  'tomas.villalba@escuela.edu',  14, 37),   -- 6°2
    ('Martina', 'Cabrera',   'martina.cabrera@escuela.edu',  13, 46),  -- 7°4
    ('Julián',  'Pereira',   'julian.pereira@escuela.edu',  14, 46),   -- 7°4
    ('Emma',    'Alcorta',   'emma.alcorta@escuela.edu',     12, 8),   -- 2°1
    ('Facundo', 'Núñez',     'facundo.nunez@escuela.edu',    13, 22);  -- 4°1