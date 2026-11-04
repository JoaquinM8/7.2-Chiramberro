<?php
/**
 * cliente_eliminar.php - compatibilidad. La eliminacion segura vive en
 * clientes/eliminar.php y exige POST + CSRF + rol admin.
 */

declare(strict_types=1);

$id = filter_input(INPUT_GET, 'id', FILTER_VALIDATE_INT);
$destino = 'clientes/eliminar.php' . ($id !== false && $id !== null && $id > 0 ? '?id=' . (int) $id : '');

header('Location: ' . $destino);
exit;
