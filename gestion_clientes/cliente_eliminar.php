<?php
/**
 * cliente_eliminar.php
 * -----------------------------------------------------------------
 * Recibe el id por GET y elimina el cliente con un DELETE usando
 * consulta preparada. Gracias a ON DELETE CASCADE, sus pedidos
 * tambien se borran automaticamente.
 * Al terminar redirige al listado (nunca se queda en esta pagina).
 */

declare(strict_types=1);

require_once 'funciones.php';
require_once 'conexion.php';

$conexion = obtener_conexion();

$id = isset($_GET['id']) ? (int) $_GET['id'] : 0;

if ($id <= 0) {
    avisar('error', 'No se indico que cliente eliminar.');
    redirigir('clientes_listado.php');
}

// Primero comprobamos que el cliente exista (para saber su nombre)
$sql = "SELECT nombre FROM clientes WHERE id = :id";
$stmt = $conexion->prepare($sql);
$stmt->execute([':id' => $id]);
$cliente = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$cliente) {
    avisar('error', 'El cliente que se quiso eliminar no existe.');
    redirigir('clientes_listado.php');
}

// DELETE con consulta preparada
$sql = "DELETE FROM clientes WHERE id = :id";

try {
    $stmt = $conexion->prepare($sql);
    $stmt->execute([':id' => $id]);

    avisar('exito', 'El cliente "' . $cliente['nombre'] . '" se elimino correctamente.');
} catch (PDOException $error) {
    avisar('error', 'No se pudo eliminar el cliente: ' . $error->getMessage());
}

redirigir('clientes_listado.php');