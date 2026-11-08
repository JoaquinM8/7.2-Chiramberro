<?php
/**
 * pedidos/guardar.php - procesa el alta de un pedido.
 * POST + CSRF + validación de servidor + consulta preparada.
 */

declare(strict_types=1);

require_once __DIR__ . '/../config/conexion.php';
require_once __DIR__ . '/../includes/auth.php';
require_once __DIR__ . '/../includes/funciones.php';
requiereRol(['admin', 'operador']);

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    exit('Método no permitido.');
}

requiere_csrf();

$clienteId = filter_input(INPUT_POST, 'cliente_id', FILTER_VALIDATE_INT);
$clienteId = ($clienteId !== false && $clienteId !== null && $clienteId > 0)
    ? (int) $clienteId
    : 0;

$descripcion = texto_post('descripcion');
$importeTexto = str_replace(',', '.', texto_post('importe'));
$fechaPedido = texto_post('fecha_pedido');
$estado = texto_post('estado');

if ($estado === '') {
    $estado = 'pendiente';
}

$errores = [];

if ($clienteId === 0) {
    $errores[] = 'Debe seleccionar un cliente válido.';
}

if ($descripcion === '') {
    $errores[] = 'La descripción es obligatoria.';
} elseif (longitud_texto($descripcion) > 150) {
    $errores[] = 'La descripción supera los 150 caracteres permitidos.';
}

if ($importeTexto === '') {
    $errores[] = 'El importe es obligatorio.';
} elseif (!preg_match('/^\d{1,8}(?:\.\d{1,2})?$/', $importeTexto)) {
    $errores[] = 'El importe debe tener hasta 8 enteros y 2 decimales.';
} else {
    $importe = (float) $importeTexto;
    if ($importe <= 0) {
        $errores[] = 'El importe debe ser mayor que cero.';
    } elseif ($importe > 99999999.99) {
        $errores[] = 'El importe supera el máximo permitido.';
    }
}

if (!fecha_iso_valida($fechaPedido)) {
    $errores[] = 'La fecha del pedido no es válida.';
}

if (!estado_pedido_valido($estado)) {
    $errores[] = 'El estado seleccionado no es válido.';
}

if ($clienteId > 0) {
    try {
        $stmtCliente = $pdo->prepare(
            'SELECT id FROM clientes WHERE id = :id LIMIT 1'
        );
        $stmtCliente->execute([':id' => $clienteId]);
        if (!$stmtCliente->fetch()) {
            $errores[] = 'El cliente seleccionado no existe.';
        }
    } catch (PDOException $error) {
        registrar_error($error, 'pedidos/guardar/verificar-cliente');
        http_response_code(500);
        exit('No se pudo verificar el cliente. Intente nuevamente.');
    }
}

if ($errores) {
    $_SESSION['errores_pedido'] = $errores;
    $_SESSION['datos_pedido'] = [
        'cliente_id' => $clienteId,
        'descripcion' => $descripcion,
        'importe' => $importeTexto,
        'fecha_pedido' => $fechaPedido,
        'estado' => $estado,
    ];

    $destino = 'nuevo.php';
    if ($clienteId > 0) {
        $destino .= '?cliente_id=' . $clienteId;
    }
    header('Location: ' . $destino);
    exit;
}

try {
    $stmt = $pdo->prepare(
        'INSERT INTO pedidos
            (cliente_id, descripcion, importe, fecha_pedido, estado, usuario_id)
         VALUES
            (:cliente_id, :descripcion, :importe, :fecha_pedido, :estado, :usuario_id)'
    );
    $stmt->execute([
        ':cliente_id' => $clienteId,
        ':descripcion' => $descripcion,
        ':importe' => number_format((float) $importe, 2, '.', ''),
        ':fecha_pedido' => $fechaPedido,
        ':estado' => $estado,
        ':usuario_id' => (int) $_SESSION['usuario_id'],
    ]);

    $nuevoId = (int) $pdo->lastInsertId();
} catch (PDOException $error) {
    registrar_error($error, 'pedidos/guardar');
    http_response_code(500);
    exit('No se pudo registrar el pedido. Intente nuevamente.');
}

avisar('exito', 'Pedido registrado correctamente.');
header('Location: detalle.php?id=' . $nuevoId);
exit;
