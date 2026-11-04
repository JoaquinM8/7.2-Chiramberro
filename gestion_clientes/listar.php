<?php
/**
 * listar.php (raiz) - compatibilidad con enlaces anteriores.
 * El listado real vive en clientes/listar.php.
 */

declare(strict_types=1);

$consulta = http_build_query($_GET, '', '&', PHP_QUERY_RFC3986);
$destino = 'clientes/listar.php' . ($consulta !== '' ? '?' . $consulta : '');

header('Location: ' . $destino);
exit;
