<?php
/**
 * entorno.php - configuracion comun de errores.
 * En produccion no se muestran detalles al usuario; quedan en el log.
 * Para depurar en local: definir GESTION_DEBUG=1 en el entorno.
 */

declare(strict_types=1);

if (!defined('APP_DEBUG')) {
    define(
        'APP_DEBUG',
        filter_var(getenv('GESTION_DEBUG') ?: '0', FILTER_VALIDATE_BOOLEAN)
    );
}

error_reporting(E_ALL);
ini_set('display_errors', APP_DEBUG ? '1' : '0');
ini_set('log_errors', '1');
