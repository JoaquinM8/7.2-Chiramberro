<?php
/**
 * cliente_editar.php
 * -----------------------------------------------------------------
 *  - Recibe el id por GET, busca los datos del cliente y muestra el
 *    formulario "precargado".
 *  - Al enviar el formulario (POST) valida los datos y hace el UPDATE.
 */

declare(strict_types=1);

require_once 'funciones.php';
require_once 'conexion.php';

$conexion = obtener_conexion();

$id    = isset($_GET['id']) ? (int) $_GET['id'] : 0;
$errores = [];

/* =============================================================
   1) DATOS PARA MOSTRAR EN EL FORMULARIO
   -------------------------------------------------------------
   Si viene un POST, se usan los valores enviados (asi no se pierde
   lo que el usuario escribio). Si no, se busca el cliente en la BD.
   ============================================================= */
$nombre   = '';
$email    = '';
$telefono = '';
$ciudad   = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    // El id tambien viaja oculto dentro del formulario
    $id       = (int) ($_POST['id'] ?? 0);
    $nombre   = trim($_POST['nombre'] ?? '');
    $email    = trim($_POST['email'] ?? '');
    $telefono = trim($_POST['telefono'] ?? '');
    $ciudad   = trim($_POST['ciudad'] ?? '');

    // ----- Validaciones -----
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

    // ----- UPDATE con consulta preparada -----
    if (count($errores) === 0) {

        $sql = "UPDATE clientes
                SET nombre   = :nombre,
                    email    = :email,
                    telefono = :telefono,
                    ciudad   = :ciudad
                WHERE id = :id";

        try {
            $stmt = $conexion->prepare($sql);
            $stmt->execute([
                ':nombre'   => $nombre,
                ':email'    => $email,
                ':telefono' => $telefono !== '' ? $telefono : null,
                ':ciudad'   => $ciudad   !== '' ? $ciudad   : null,
                ':id'       => $id,
            ]);

            avisar('exito', 'Los datos del cliente se actualizaron correctamente.');
            redirigir('clientes_listado.php');

        } catch (PDOException $error) {
            $errores[] = 'No se pudo actualizar el cliente: ' . $error->getMessage();
        }
    }

} else {

    // ----- Primera vez que se abre la pagina: SELECT del cliente -----
    if ($id <= 0) {
        avisar('error', 'No se indico que cliente editar.');
        redirigir('clientes_listado.php');
    }

    $sql = "SELECT id, nombre, email, telefono, ciudad
            FROM clientes
            WHERE id = :id";
    $stmt = $conexion->prepare($sql);
    $stmt->execute([':id' => $id]);
    $cliente = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$cliente) {
        avisar('error', 'El cliente solicitado no existe.');
        redirigir('clientes_listado.php');
    }

    $nombre   = $cliente['nombre'];
    $email    = $cliente['email'];
    $telefono = $cliente['telefono'] ?? '';
    $ciudad   = $cliente['ciudad'] ?? '';
}

cabecera('Editar cliente', 'listado');
?>

<h2>Editar cliente</h2>

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

    <form action="cliente_editar.php" method="POST">

        <!-- Campo oculto para saber que registro se actualiza -->
        <input type="hidden" name="id" value="<?= e($id) ?>">

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

        <input type="submit" value="Guardar cambios">
        &nbsp;&nbsp;
        <a href="clientes_listado.php">Cancelar</a>

    </form>
</div>

<?php pie(); ?>