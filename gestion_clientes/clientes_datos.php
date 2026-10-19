<?php
/**
 * clientes_datos.php
 * -----------------------------------------------------------------
 * Capa de acceso a datos (DAL) del modulo de busqueda de clientes.
 * Aca vive TODA la consulta a la base de datos; el archivo listar.php
 * unicamente le pide los datos y se encarga de pintar el HTML.
 *
 * Todas las consultas se ejecutan con PDO y consultas preparadas
 * (nunca se concatena texto del usuario dentro del SQL).
 * Las columnas de ordenamiento se controlan con una lista blanca.
 */

declare(strict_types=1);

/**
 * Lista blanca de columnas que se pueden ordenar.
 * La clave es el valor del parametro GET "orden" y el valor es el
 * nombre real de la columna en la tabla "clientes". De este modo el
 * usuario NUNCA escribe directamente una columna dentro del SQL.
 */
const COLUMNAS_ORDEN_CLIENTES = [
    'id'     => 'id',
    'nombre' => 'nombre',
    'email'  => 'email',
    'ciudad' => 'ciudad',
];

/**
 * Ciudades distintas, para cargar el combo del filtro.
 */
function ciudades_disponibles(PDO $conexion): array
{
    $sql = "SELECT DISTINCT ciudad
            FROM clientes
            WHERE ciudad IS NOT NULL AND ciudad <> ''
            ORDER BY ciudad ASC";

    return $conexion->query($sql)->fetchAll(PDO::FETCH_COLUMN);
}

/**
 * Arma la parte WHERE + los parametros, segun los filtros activos.
 * Devuelve un array con dos elementos:
 *   [0] => texto del WHERE (o cadena vacia si no hay filtros)
 *   [1] => array de parametros para la consulta preparada
 */
function armar_filtros_clientes(array $filtros): array
{
    $condiciones = [];
    $parametros  = [];

    $buscar = trim((string) ($filtros['buscar'] ?? ''));
    $email  = trim((string) ($filtros['email'] ?? ''));
    $ciudad = trim((string) ($filtros['ciudad'] ?? ''));

    if ($buscar !== '') {
        $condiciones[]            = 'nombre LIKE :buscar';
        $parametros[':buscar']    = '%' . $buscar . '%';
    }

    if ($email !== '') {
        $condiciones[]            = 'email LIKE :email';
        $parametros[':email']     = '%' . $email . '%';
    }

    if ($ciudad !== '') {
        $condiciones[]            = 'ciudad = :ciudad';
        $parametros[':ciudad']    = $ciudad;
    }

    $where = empty($condiciones) ? '' : ' WHERE ' . implode(' AND ', $condiciones);

    return [$where, $parametros];
}

/**
 * Cantidad TOTAL de clientes que cumplen los filtros.
 * (Se usa para calcular cuantas paginas hay.)
 */
function contar_clientes(PDO $conexion, array $filtros): int
{
    [$where, $parametros] = armar_filtros_clientes($filtros);

    $sql = "SELECT COUNT(*) FROM clientes" . $where;

    $stmt = $conexion->prepare($sql);
    $stmt->execute($parametros);

    return (int) $stmt->fetchColumn();
}

/**
 * Devuelve UNA pagina de clientes (el trozo de resultados a mostrar).
 *
 * $orden      : clave de la lista blanca COLUMNAS_ORDEN_CLIENTES
 * $direccion  : 'ASC' o 'DESC' (se normaliza adentro)
 * $inicio     : desplazamiento (OFFSET), pagina 1 => 0
 * $porPagina  : tamaño de la pagina (LIMIT)
 */
function buscar_clientes(
    PDO $conexion,
    array $filtros,
    string $orden,
    string $direccion,
    int $inicio,
    int $porPagina
): array {
    [$where, $parametros] = armar_filtros_clientes($filtros);

    // Lista blanca: solo se aceptan columnas conocidas
    $columna = COLUMNAS_ORDEN_CLIENTES[$orden] ?? 'nombre';
    $sentido = (strtoupper($direccion) === 'DESC') ? 'DESC' : 'ASC';

    $sql = "SELECT id, nombre, email, telefono, ciudad, fecha_alta
            FROM clientes" . $where .
           " ORDER BY $columna $sentido, id ASC
            LIMIT :inicio, :cantidad";

    $stmt = $conexion->prepare($sql);

    // Se bindea cada parametro con su tipo
    foreach ($parametros as $nombre => $valor) {
        $stmt->bindValue($nombre, $valor, PDO::PARAM_STR);
    }
    $stmt->bindValue(':inicio',   $inicio,    PDO::PARAM_INT);
    $stmt->bindValue(':cantidad', $porPagina, PDO::PARAM_INT);

    $stmt->execute();

    return $stmt->fetchAll(PDO::FETCH_ASSOC);
}

/**
 * Trae un cliente completo por su id (para la vista "Ver").
 */
function cliente_por_id(PDO $conexion, int $id): ?array
{
    $sql = "SELECT id, nombre, email, telefono, ciudad, fecha_alta
            FROM clientes
            WHERE id = :id";

    $stmt = $conexion->prepare($sql);
    $stmt->execute([':id' => $id]);

    $cliente = $stmt->fetch(PDO::FETCH_ASSOC);

    return $cliente === false ? null : $cliente;
}
