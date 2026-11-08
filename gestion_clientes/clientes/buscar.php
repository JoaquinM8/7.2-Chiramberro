<?php
/**
 * clientes/buscar.php - buscador asincrónico de clientes (Semana 7).
 * -----------------------------------------------------------------
 * La página no consulta clientes en el servidor: entrega el formulario y
 * deja que JavaScript consuma api/clientes.php y actualice la tabla.
 * La lista de ciudades se obtiene una sola vez para poblar el filtro.
 */

declare(strict_types=1);

require_once __DIR__ . '/../config/conexion.php';
require_once __DIR__ . '/../includes/auth.php';
require_once __DIR__ . '/../includes/funciones.php';
requiereLogin();

$q = recortar_texto(texto_get('q'), 80);
$ciudad = recortar_texto(texto_get('ciudad'), 60);

try {
    $ciudades = $pdo->query(
        "SELECT DISTINCT ciudad
         FROM clientes
         WHERE ciudad IS NOT NULL AND ciudad <> ''
         ORDER BY ciudad ASC"
    )->fetchAll();
} catch (PDOException $error) {
    registrar_error($error, 'clientes/buscar');
    http_response_code(500);
    exit('No se pudo cargar el buscador de clientes.');
}

$ciudadesValidas = array_column($ciudades, 'ciudad');
if ($ciudad !== '' && !in_array($ciudad, $ciudadesValidas, true)) {
    $ciudad = '';
}

$base = '../';
$tituloPagina = 'Buscador de clientes';
$activa = 'clientes';
require_once __DIR__ . '/../includes/header.php';
require_once __DIR__ . '/../includes/menu.php';
require_once __DIR__ . '/../includes/breadcrumbs.php';
?>
<main id="contenidoPrincipal" class="contenedor">
    <?php renderizar_migas([
        ['texto' => 'Inicio', 'url' => 'panel.php'],
        ['texto' => 'Clientes', 'url' => 'clientes/listar.php'],
        ['texto' => 'Buscador asincrónico'],
    ], $base); ?>

    <div class="d-flex flex-column flex-lg-row justify-content-between
                align-items-lg-center gap-3 mb-4">
        <div>
            <div class="d-flex flex-wrap align-items-center gap-2 mb-1">
                <h2 class="mb-0">Buscador de clientes</h2>
                <span class="badge text-bg-primary">AJAX + JSON</span>
            </div>
            <p class="text-secondary mb-0">
                Buscá por nombre o ciudad y actualizá la tabla sin recargar la página.
            </p>
        </div>
        <a href="listar.php" class="btn btn-outline-primary align-self-start">
            <i class="bi bi-table me-1" aria-hidden="true"></i>
            Listado completo
        </a>
    </div>

    <?php renderizar_flash(); ?>

    <noscript>
        <div class="alert alert-warning" role="alert">
            <i class="bi bi-exclamation-triangle me-1" aria-hidden="true"></i>
            El buscador asincrónico requiere JavaScript.
            <a href="listar.php" class="alert-link">Usá el listado tradicional</a>.
        </div>
    </noscript>

    <div id="buscadorApp"
         data-endpoint="<?= e(APP_BASE_URL . '/api/clientes.php') ?>"
         data-detalle-base="<?= e(APP_BASE_URL . '/clientes/ver.php') ?>"
         data-login-url="<?= e(APP_BASE_URL . '/login.php') ?>"
         data-max-resultados="50">

        <form id="formBuscador" method="GET" action="buscar.php"
              class="card border-0 shadow-sm mb-4 filtros-card" role="search">
            <div class="card-body p-4">
                <div class="filtros-grid">
                    <div class="filtro-campo filtro-buscar">
                        <label for="busqueda" class="form-label">
                            Nombre o ciudad
                        </label>
                        <div class="input-group input-group-lg">
                            <span class="input-group-text" aria-hidden="true">
                                <i class="bi bi-search"></i>
                            </span>
                            <input id="busqueda" name="q" type="search"
                                   class="form-control"
                                   value="<?= e($q) ?>" maxlength="80"
                                   placeholder="Ej.: Lucía, Córdoba, Núñez..."
                                   autocomplete="off" spellcheck="false"
                                   enterkeyhint="search"
                                   aria-describedby="ayudaBusqueda estadoBusqueda"
                                   aria-controls="resultados">
                        </div>
                    </div>

                    <div class="filtro-campo filtro-ciudad">
                        <label for="ciudadFiltro" class="form-label">Ciudad</label>
                        <select id="ciudadFiltro" name="ciudad"
                                class="form-select form-select-lg"
                                aria-describedby="ayudaBusqueda">
                            <option value="">Todas</option>
                            <?php foreach ($ciudades as $item): ?>
                                <option value="<?= e($item['ciudad']) ?>"
                                        <?= $ciudad === $item['ciudad'] ? 'selected' : '' ?>>
                                    <?= e($item['ciudad']) ?>
                                </option>
                            <?php endforeach; ?>
                        </select>
                    </div>

                    <div class="filtro-acciones">
                        <button id="btnActualizar" type="submit"
                                class="btn btn-primary btn-lg">
                            <i class="bi bi-arrow-clockwise me-1" aria-hidden="true"></i>
                            Actualizar
                        </button>
                        <button id="btnLimpiar" type="button"
                                class="btn btn-outline-secondary btn-lg">
                            <i class="bi bi-eraser me-1" aria-hidden="true"></i>
                            Limpiar
                        </button>
                    </div>
                </div>

                <p id="ayudaBusqueda" class="form-text mt-3 mb-0">
                    La búsqueda se realiza por nombre o ciudad. Los resultados se
                    actualizan automáticamente después de una pausa al escribir.
                </p>
            </div>
        </form>

        <div class="d-flex flex-column flex-sm-row align-items-sm-center
                    justify-content-between gap-2 mb-3">
            <div class="d-flex align-items-center gap-2">
                <p id="estadoBusqueda" class="resumen buscador-estado mb-0"
                   role="status" aria-live="polite" aria-atomic="true">
                    Cargando clientes...
                </p>
                <span id="spinnerBusqueda"
                      class="spinner-border spinner-border-sm text-primary d-none"
                      aria-hidden="true"></span>
            </div>
            <p class="small text-secondary mb-0">
                <i class="bi bi-shield-check me-1" aria-hidden="true"></i>
                Endpoint privado con sesión y consultas preparadas.
            </p>
        </div>

        <div id="errorBusqueda" class="alert alert-danger" role="alert" hidden>
            <div class="d-flex align-items-start gap-2">
                <i class="bi bi-exclamation-triangle-fill mt-1"
                   aria-hidden="true"></i>
                <div>
                    <p id="errorBusquedaTexto" class="mb-0"></p>
                    <a id="errorBusquedaLogin" class="alert-link"
                       href="<?= e(APP_BASE_URL . '/login.php') ?>" hidden>
                        Iniciar sesión
                    </a>
                </div>
            </div>
        </div>

        <div id="contenedorResultados"
             class="card border-0 shadow-sm overflow-hidden"
             aria-busy="false">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0 buscador-tabla">
                    <caption class="visually-hidden">
                        Resultados de la búsqueda de clientes
                    </caption>
                    <thead class="table-light">
                        <tr>
                            <th scope="col">N.°</th>
                            <th scope="col">Nombre</th>
                            <th scope="col" class="d-none d-md-table-cell">Email</th>
                            <th scope="col">Ciudad</th>
                            <th scope="col" class="text-end">Acciones</th>
                        </tr>
                    </thead>
                    <tbody id="resultados">
                        <tr>
                            <td colspan="5" class="text-center text-secondary py-4">
                                Cargando resultados...
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <p class="small text-secondary mt-3 mb-0">
            <i class="bi bi-info-circle me-1" aria-hidden="true"></i>
            Si el buscador no responde,
            <a href="listar.php">usá el listado tradicional</a>.
        </p>
    </div>
</main>

<script src="<?= e(APP_BASE_URL . '/js/buscar-clientes.js') ?>" defer></script>
<?php require_once __DIR__ . '/../includes/footer.php'; ?>
