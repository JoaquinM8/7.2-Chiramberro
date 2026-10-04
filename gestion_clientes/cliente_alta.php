<?php
/**
 * cliente_alta.php
 * -----------------------------------------------------------------
 * Formulario para cargar un cliente nuevo.
 *  - Si el formulario se envia (POST) valida los datos y hace el INSERT.
 *  - Si no, solo muestra el formulario vacio.
 */

declare(strict_types=1);

require_once 'funciones.php';
require_once 'conexion.php';

$conexion = obtener_conexion();

// Valores que se muestran en el formulario (al fallar la validacion se mantienen)
$nombre   = '';
$email    = '';
$telefono = '';
$ciudad   = '';

$errores = [];

// ---------------------------------------------------------------
// 1) ¿Se esta enviando el formulario?  ->  metodo POST
// ---------------------------------------------------------------
if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    $nombre   = trim($_POST['nombre'] ?? '');
    $email    = trim($_POST['email'] ?? '');
    $telefono = trim($_POST['telefono'] ?? '');
    $ciudad   = trim($_POST['ciudad'] ?? '');

    // ----- Validaciones del lado del servidor -----
    if ($nombre === '') {
        $errores[] = 'El nombre es obligatorio.';
    } elseif (mb_strlen($nombre) > 80) {
        $errores[] = 'El nombre no puede superar los 80 caracteres.';
    }

    if ($email === '') {
        $errores[] = 'El email es obligatorio.';
    } elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $errores[] = 'El email no tiene un formato valido.';
    } elseif (mb_strlen($email) > 120) {
        $errores[] = 'El email no puede superar los 120 caracteres.';
    }

    if (mb_strlen($telefono) > 30) {
        $errores[] = 'El telefono no puede superar los 30 caracteres.';
    }

    if (mb_strlen($ciudad) > 60) {
        $errores[] = 'La ciudad no puede superar los 60 caracteres.';
    }

    // ----- Si no hay errores, guardamos el cliente -----
    if (count($errores) === 0) {

        // Consulta preparada: los valores se envian aparte de la consulta
        $sql = "INSERT INTO clientes (nombre, email, telefono, ciudad, fecha_alta)
                VALUES (:nombre, :email, :telefono, :ciudad, :fecha_alta)";

        try {
            $stmt = $conexion->prepare($sql);
            $stmt->execute([
                ':nombre'     => $nombre,
                ':email'      => $email,
                ':telefono'   => $telefono !== '' ? $telefono : null,
                ':ciudad'     => $ciudad   !== '' ? $ciudad   : null,
                ':fecha_alta' => date('Y-m-d'),   // fecha actual del servidor
            ]);

            avisar('exito', 'El cliente "' . $nombre . '" se guardo correctamente.');
            redirigir('clientes_listado.php');   // vuelve al listado

        } catch (PDOException $error) {
            $errores[] = 'No se pudo guardar el cliente: ' . $error->getMessage();
        }
    }
}

cabecera('Alta de cliente', 'alta');
?>

<h2>Dar de alta un cliente</h2>

<div class="tarjeta">

    <?php if (count($errores) > 0) { ?>
        <div class="aviso aviso-error">Datos incorrectos, revisa lo siguiente:</div>
        <ul class="lista-errores">
            <?php foreach ($errores as $error) { ?>
                <li><?= e($error) ?></li>
            <?php } ?>
        </ul>
        <br>
    <?php } ?>

    <form action="cliente_alta.php" method="POST">

        <div class="campo">
            <label for="nombre">Nombre y apellido *</label>
            <input type="text" id="nombre" name="nombre" maxlength="80"
                   value="<?= e($nombre) ?>" required>
        </div>

        <div class="campo">
            <label for="email">Email *</label>
            <input type="email" id="email" name="email" maxlength="120"
                   value="<?= e($email) ?>" required>
        </div>

        <div class="campo">
            <label for="telefono">Telefono</label>
            <input type="text" id="telefono" name="telefono" maxlength="30"
                   value="<?= e($telefono) ?>">
        </div>

        <div class="campo">
            <label for="ciudad">Ciudad</label>
            <input type="text" id="ciudad" name="ciudad" maxlength="60"
                   value="<?= e($ciudad) ?>">
        </div>

        <input type="submit" value="Guardar cliente">
        &nbsp;&nbsp;
        <a href="clientes_listado.php">Volver al listado</a>

    </form>
</div>

<?php pie(); ?>