<?php
/**
 * clientes_listado.php - compatibilidad con enlaces anteriores.
 */

declare(strict_types=1);

$consulta = http_build_query($_GET, '', '&', PHP_QUERY_RFC3986);
$destino = 'clientes/listar.php' . ($consulta !== '' ? '?' . $consulta : '');

header('Location: ' . $destino);
exit;
