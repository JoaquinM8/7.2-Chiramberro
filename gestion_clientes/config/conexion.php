<?php
/**
 * conexion.php - conexion PDO a MariaDB/MySQL
 * -----------------------------------------------------------------
 * Los valores se pueden sobrescribir con variables de entorno para no
 * guardar credenciales reales en el repositorio. En XAMPP los valores
 * por defecto funcionan sin configuracion adicional.
 */

declare(strict_types=1);

require_once __DIR__ . '/entorno.php';

define('DB_HOST', getenv('GESTION_DB_HOST') ?: '127.0.0.1');
define('DB_NAME', getenv('GESTION_DB_NAME') ?: 'gestion_clientes');
define('DB_USER', getenv('GESTION_DB_USER') ?: 'root');
define('DB_PASS', getenv('GESTION_DB_PASS') ?: '');
define('DB_CHARSET', 'utf8mb4');

function obtener_conexion(): PDO
{
    static $conexion = null;

    if ($conexion === null) {
        $dsn = 'mysql:host=' . DB_HOST
            . ';dbname=' . DB_NAME
            . ';charset=' . DB_CHARSET;

        try {
            $conexion = new PDO($dsn, DB_USER, DB_PASS, [
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                PDO::ATTR_EMULATE_PREPARES => false,
            ]);
        } catch (PDOException $error) {
            // El detalle tecnico va al log; el usuario recibe un mensaje generico.
            error_log('[gestion_clientes] Error de conexion a la base de datos: ' . $error->getMessage());
            http_response_code(500);
            exit('No se pudo conectar con la base de datos. Contacte al administrador.');
        }
    }

    return $conexion;
}

// Compatibilidad con codigo anterior que espera la variable global $pdo.
$pdo = obtener_conexion();
