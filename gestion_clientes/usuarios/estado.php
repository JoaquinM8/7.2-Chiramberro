<?php
/**
 * usuarios/estado.php - activación / desactivación lógica de usuarios.
 * Solo admin, POST + CSRF. Protege la propia cuenta y el último admin activo.
 */

declare(strict_types=1);

require_once __DIR__ . '/../config/conexion.php';
require_once __DIR__ . '/../includes/auth.php';
require_once __DIR__ . '/../includes/funciones.php';
requiereRol('admin');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    exit('Método no permitido.');
}

requiere_csrf();

$id = filter_input(INPUT_POST, 'id', FILTER_VALIDATE_INT);
$id = ($id !== false && $id !== null && $id > 0) ? (int) $id : 0;
$accion = texto_post('accion');

if ($id === 0) {
    http_response_code(400);
    exit('Identificador inválido.');
}

if (!in_array($accion, ['activar', 'desactivar'], true)) {
    http_response_code(400);
    exit('Acción inválida.');
}

$usuarioActualId = (int) ($_SESSION['usuario_id'] ?? 0);

try {
    $pdo->beginTransaction();

    $stmt = $pdo->prepare(
        'SELECT id, usuario, nombre, rol, activo
         FROM usuarios
         WHERE id = :id
         FOR UPDATE'
    );
    $stmt->execute([':id' => $id]);
    $objetivo = $stmt->fetch();

    if (!$objetivo) {
        $pdo->rollBack();
        avisar('error', 'El usuario indicado no existe.');
        header('Location: listar.php');
        exit;
    }

    if ($accion === 'desactivar' && $id === $usuarioActualId) {
        $pdo->rollBack();
        avisar('error', 'No puede desactivar su propia cuenta.');
        header('Location: listar.php');
        exit;
    }

    if ($accion === 'desactivar'
        && $objetivo['rol'] === 'admin'
        && (int) $objetivo['activo'] === 1) {
        $stmtAdmins = $pdo->prepare(
            "SELECT id
             FROM usuarios
             WHERE rol = 'admin' AND activo = 1 AND id <> :id
             FOR UPDATE"
        );
        $stmtAdmins->execute([':id' => $id]);
        $otrosAdmins = $stmtAdmins->fetchAll();

        if (!$otrosAdmins) {
            $pdo->rollBack();
            avisar(
                'error',
                'No se puede desactivar el último administrador activo.'
            );
            header('Location: listar.php');
            exit;
        }
    }

    $nuevoEstado = $accion === 'activar' ? 1 : 0;

    $stmtActualizar = $pdo->prepare(
        'UPDATE usuarios SET activo = :activo WHERE id = :id'
    );
    $stmtActualizar->execute([
        ':activo' => $nuevoEstado,
        ':id' => $id,
    ]);

    $pdo->commit();

    avisar(
        'exito',
        $accion === 'activar'
            ? 'Usuario activado correctamente.'
            : 'Usuario desactivado correctamente. La cuenta se conserva para trazabilidad.'
    );
} catch (PDOException $error) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    registrar_error($error, 'usuarios/estado');
    avisar('error', 'No se pudo cambiar el estado del usuario. Intente nuevamente.');
}

header('Location: listar.php');
exit;
