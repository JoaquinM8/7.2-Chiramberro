<?php
/**
 * conexion.php
 * -----------------------------------------------------------------
 * Proyecto : Gestion de Clientes
 * Entorno  : XAMPP (Apache + PHP + MariaDB/MySQL)
 * -----------------------------------------------------------------
 * Abre UNA unica conexion a la base de datos "gestion_clientes"
 * usando PDO. Todas las paginas del proyecto necesitan este archivo.
 */

declare(strict_types=1);

/* =============================================================
   1) DATOS DE CONEXION (valores por defecto de XAMPP)
   ============================================================= */
define('DB_HOST',    'localhost');
define('DB_NAME',    'gestion_clientes');
define('DB_USER',    'root');
define('DB_PASS',    '');          // en XAMPP el usuario root no tiene clave
define('DB_CHARSET', 'utf8mb4');   // permite acentos y la letra "ñ"

/* =============================================================
   2) FUNCION QUE DEVUELVE LA CONEXION PDO
   -------------------------------------------------------------
   La palabra clave "static" hace que la conexion se cree una sola
   vez por pagina, aunque la funcion se llame varias veces.
   ============================================================= */
function obtener_conexion(): PDO
{
    static $conexion = null;

    if ($conexion === null) {
        // DSN = Data Source Name (cadena de conexion de MySQL)
        $dsn = 'mysql:host=' . DB_HOST . ';dbname=' . DB_NAME . ';charset=' . DB_CHARSET;

        try {
            $conexion = new PDO($dsn, DB_USER, DB_PASS, [
                // Convertimos los errores de MySQL en excepciones de PHP
                PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
                // Cada fila llega como array asociativo (['id' => 1, ...])
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                // Prepared statements reales (no emulados) = mas seguro
                PDO::ATTR_EMULATE_PREPARES   => false,
            ]);
        } catch (PDOException $error) {
            // Si falla la conexion, se avisa y se corta el script
            die('Error de conexion a la base de datos: ' . $error->getMessage()
                . '<br>Revisa que MySQL este iniciado en XAMPP y que la base '
                . DB_NAME . ' exista (schema.sql).');
        }
    }

    return $conexion;
}
