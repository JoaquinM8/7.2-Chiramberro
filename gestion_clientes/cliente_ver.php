<?php
/**
 * cliente_ver.php - compatibilidad. La ficha vive en clientes/ver.php.
 */

declare(strict_types=1);

$id = filter_input(INPUT_GET, 'id', FILTER_VALIDATE_INT);
$destino = 'clientes/ver.php' . ($id !== false && $id !== null && $id > 0 ? '?id=' . (int) $id : '');

header('Location: ' . $destino);
exit;
