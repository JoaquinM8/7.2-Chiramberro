<?php
/**
 * cliente_editar.php - compatibilidad. La edicion vive en clientes/editar.php.
 */

declare(strict_types=1);

$id = filter_input(INPUT_GET, 'id', FILTER_VALIDATE_INT);
$destino = 'clientes/editar.php' . ($id !== false && $id !== null && $id > 0 ? '?id=' . (int) $id : '');

header('Location: ' . $destino);
exit;
