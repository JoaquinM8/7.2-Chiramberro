<?php
/**
 * index.php - punto de entrada. Redirige segun el estado de la sesion.
 */

declare(strict_types=1);

require_once __DIR__ . '/includes/auth.php';

if (empty($_SESSION['usuario_id'])) {
    header('Location: login.php');
    exit;
}

header('Location: panel.php');
exit;
