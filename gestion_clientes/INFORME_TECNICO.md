# Informe técnico - Semana 8

## 1. Objetivo

Convertir la aplicación de gestión de clientes de las semanas anteriores en un
sistema comercial integrado, conservando los datos existentes y agregando:

- panel con indicadores comerciales;
- módulo de pedidos asociado a clientes;
- reportes con agregaciones SQL y filtros;
- administración de usuarios con roles;
- controles de seguridad en servidor;
- pruebas y documentación de defensa.

El alcance funcional se mantiene deliberadamente acotado: un pedido tiene una
descripción, un importe, una fecha, un estado y un usuario responsable. No se
incorporaron productos, stock ni líneas de pedido.

---

## 2. Estado de partida y estrategia de migración

La base existente contaba con:

- 12 clientes;
- 15 pedidos;
- 3 usuarios;
- roles históricos `admin` y `consulta`.

Antes de modificar se generaron copias de seguridad de la base y del repositorio.
La migración `sql/migracion_semana_8.sql` se diseñó para ser **no destructiva**:

- no elimina tablas ni filas;
- convierte el rol `consulta` a `operador` sin perder la cuenta;
- agrega `estado` y `usuario_id` a `pedidos`;
- asigna los pedidos históricos al primer administrador activo;
- reemplaza la FK de cliente de `CASCADE` a `RESTRICT`;
- agrega índices para estado y fecha;
- crea la cuenta `operador` solo si no existe.

Para instalaciones nuevas se reescribió `gestion_clientes.sql` con el esquema
final y datos de demostración. Usa `CREATE TABLE IF NOT EXISTS` e `INSERT IGNORE`
para no destruir información si se ejecuta sobre una base existente.

### Verificación posterior

```sql
SELECT COUNT(*) FROM clientes;                       -- 12
SELECT COUNT(*) FROM pedidos;                        -- 15
SELECT COUNT(*) FROM usuarios;                       -- 3
SELECT COUNT(*) FROM pedidos WHERE usuario_id IS NULL; -- 0
```

---

## 3. Arquitectura

La aplicación mantiene el estilo de las semanas anteriores: PHP procedural con
includes, PDO y Bootstrap 5.3. No se introdujo un framework ni un enrutador
central.

### Capas

```text
Navegador
   │
   ├── PHP por pantalla (login, panel, clientes, pedidos, reportes, usuarios)
   │       │
   │       ├── config/conexion.php   → PDO único por request
   │       ├── includes/auth.php     → sesión, cuenta activa, roles
   │       ├── includes/funciones.php→ CSRF, entrada, escape, formato
   │       ├── includes/header.php   → documento y Bootstrap
   │       ├── includes/menu.php     → navegación por rol
   │       └── includes/footer.php   → cierre y JS
   │
   └── api/clientes.php → JSON de solo lectura
```

### Decisiones de diseño

| Decisión | Motivo |
|---|---|
| Mantener PHP procedural | Continuidad con Weeks 1–7 y menor riesgo de regresión. |
| Centralizar permisos en `auth.php` | Evitar controles duplicados o inconsistentes. |
| Verificar cuenta activa en cada request | La baja lógica debe tener efecto inmediato. |
| Usar transacciones en bajas y estados | Evitar carreras y operaciones parciales. |
| `ON DELETE RESTRICT` | Impedir pedidos huérfanos. |
| Reportes separados por dominio | Consultas más claras y páginas explicables. |
| Roles `admin`/`operador` | Modelo simple y suficiente para el enunciado. |

---

## 4. Modelo de datos

### `clientes`

| Columna | Tipo | Restricción |
|---|---|---|
| `id` | INT | PK, autoincremental |
| `nombre` | VARCHAR(80) | NOT NULL |
| `email` | VARCHAR(120) | NOT NULL |
| `telefono` | VARCHAR(30) | NULL |
| `ciudad` | VARCHAR(60) | NULL |
| `fecha_alta` | DATE | NOT NULL |

### `usuarios`

| Columna | Tipo | Restricción |
|---|---|---|
| `id` | INT | PK, autoincremental |
| `usuario` | VARCHAR(50) | NOT NULL, UNIQUE |
| `password` | VARCHAR(255) | NOT NULL, hash bcrypt |
| `nombre` | VARCHAR(100) | NOT NULL |
| `activo` | TINYINT(1) | NOT NULL, default 1 |
| `rol` | ENUM('admin','operador') | NOT NULL, default operador |
| `creado_en` | TIMESTAMP | default CURRENT_TIMESTAMP |
| `actualizado_en` | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP |

### `pedidos`

| Columna | Tipo | Restricción |
|---|---|---|
| `id` | INT | PK, autoincremental |
| `cliente_id` | INT | NOT NULL, FK clientes |
| `descripcion` | VARCHAR(150) | NOT NULL |
| `importe` | DECIMAL(10,2) | NOT NULL, > 0 |
| `fecha_pedido` | DATE | NOT NULL |
| `estado` | VARCHAR(20) | NOT NULL, default pendiente |
| `usuario_id` | INT | NOT NULL, FK usuarios |

### Relaciones

```text
clientes 1 ─────── N pedidos
usuarios 1 ─────── N pedidos
```

- `fk_pedido_cliente`: `ON DELETE RESTRICT ON UPDATE CASCADE`.
- `fk_pedidos_usuario`: `ON DELETE RESTRICT ON UPDATE CASCADE`.
- `chk_pedidos_importe`: `importe > 0` en la instalación nueva.
- Índices: `idx_pedidos_estado`, `idx_pedidos_fecha`.

La regla `RESTRICT` es una segunda barrera. La aplicación también comprueba
dependencias antes de eliminar y muestra un mensaje explicativo.

---

## 5. Implementación por módulo

### 5.1 Autenticación y sesión

`includes/auth.php` concentra:

- inicio de sesión con configuración segura de cookie;
- `APP_BASE_URL` calculada sin hardcodear la carpeta;
- `destruir_sesion_actual()`;
- `verificar_cuenta_activa(PDO)` que consulta `usuarios.activo` y refresca rol;
- `requiereLogin()` que redirige a login si no hay sesión o la cuenta fue
  desactivada;
- `requiereRol()` que responde `403` genérico;
- `esAdmin()`, `esOperador()` y `puedeGestionarComercial()`.

`autenticar.php` valida POST, CSRF, credenciales y estado activo. Usa
`password_verify()`, rehash automático si corresponde y `session_regenerate_id(true)`
antes de guardar los datos del usuario en sesión.

`logout.php` muestra una confirmación por `GET` y destruye la sesión solo por
`POST` con CSRF.

### 5.2 Panel

`panel.php` obtiene de la base:

- total de clientes;
- total de pedidos;
- suma de importes;
- promedio por pedido;
- últimos cinco pedidos con cliente, fecha, estado e importe.

Incluye accesos rápidos a clientes, pedidos, reportes y usuarios según el rol.

### 5.3 Clientes

- `clientes/listar.php`: búsqueda, filtro por ciudad, orden, paginación y acciones
  por rol.
- `clientes/nuevo.php` y `guardar.php`: alta con validación de nombre, email,
  teléfono y ciudad.
- `clientes/editar.php` y `actualizar.php`: edición con las mismas reglas.
- `clientes/ver.php`: ficha con datos, resumen de pedidos e historial.
- `clientes/eliminar.php`: confirmación por `GET`; baja por `POST` con CSRF,
  transacción, `FOR UPDATE` y comprobación de pedidos.

La baja de clientes es solo para `admin`. Si existen pedidos, se rechaza y se
redirige a la ficha con un mensaje. La FK `RESTRICT` impide además el borrado
directo desde SQL.

### 5.4 Pedidos

- `pedidos/nuevo.php`: formulario con cliente, descripción, importe, fecha y
  estado.
- `pedidos/guardar.php`: valida método, CSRF, cliente existente, descripción
  (máx. 150), importe positivo con hasta 8 enteros y 2 decimales, fecha ISO y
  estado permitido. Inserta con consulta preparada y guarda `usuario_id` de la
  sesión.
- `pedidos/listar.php`: filtros por cliente, estado, rango de fechas y paginación.
- `pedidos/detalle.php`: datos del pedido, cliente, usuario responsable y acciones.

Estados permitidos: `pendiente`, `pagado`, `enviado`.

### 5.5 Reportes

#### Ventas

```sql
SELECT COUNT(*) AS cantidad,
       COALESCE(SUM(importe), 0) AS total,
       COALESCE(AVG(importe), 0) AS promedio,
       COUNT(DISTINCT cliente_id) AS clientes
FROM pedidos
WHERE fecha_pedido BETWEEN :desde AND :hasta;
```

Además:

- agrupación mensual con `DATE_FORMAT`;
- agrupación por estado;
- ranking de clientes con `INNER JOIN` y `GROUP BY`.

#### Clientes

```sql
SELECT c.id, c.nombre, c.email, c.ciudad, c.fecha_alta,
       COUNT(p.id) AS cantidad,
       COALESCE(SUM(p.importe), 0) AS total,
       MAX(p.fecha_pedido) AS ultima_compra
FROM clientes c
LEFT JOIN pedidos p
       ON p.cliente_id = c.id
      AND p.fecha_pedido BETWEEN :desde AND :hasta
GROUP BY c.id, c.nombre, c.email, c.ciudad, c.fecha_alta
ORDER BY total DESC, cantidad DESC, c.nombre ASC;
```

El `LEFT JOIN` conserva clientes sin compras. Los indicadores se calculan en PHP
a partir de las filas ya agregadas.

#### Pedidos

- resumen con `COUNT`, `SUM`, `AVG`;
- agrupación por estado;
- detalle con `JOIN` a clientes y `LEFT JOIN` a usuarios;
- filtros por fecha y estado;
- límite de 300 filas.

`periodo_reportes()` valida las fechas y, si faltan o son inválidas, usa el año
actual como período por defecto.

### 5.6 Usuarios

- `usuarios/listar.php`: solo admin; muestra estado, rol y acciones.
- `usuarios/nuevo.php`: formulario de alta.
- `usuarios/guardar.php`: valida usuario, nombre, contraseña y rol; verifica
  unicidad; usa `password_hash()`.
- `usuarios/estado.php`: activa o desactiva por `POST` + CSRF, con transacción,
  `FOR UPDATE`, bloqueo de auto-desactivación y bloqueo del último admin activo.

La baja es lógica: `activo = 0`; la fila se conserva para trazabilidad.

---

## 6. Seguridad

### 6.1 Autenticación

- Contraseñas hasheadas con `PASSWORD_DEFAULT` (bcrypt en el entorno probado).
- Comparación con `password_verify()`.
- Mensaje genérico ante credenciales inválidas.
- Rehash automático si cambia el algoritmo.
- Regeneración de ID de sesión al autenticar.

### 6.2 Sesión

- `session.use_strict_mode = 1`.
- `session.use_only_cookies = 1`.
- Cookie `HttpOnly`.
- `SameSite=Lax`.
- `Secure` cuando la solicitud es HTTPS.
- Destrucción completa de sesión y cookie en logout.
- Verificación de cuenta activa en cada request protegido.

### 6.3 Autorización

| Endpoint | Regla |
|---|---|
| `clientes/guardar.php`, `actualizar.php` | `admin`, `operador` |
| `clientes/eliminar.php` | `admin` |
| `pedidos/guardar.php` | `admin`, `operador` |
| `usuarios/*` | `admin` |
| `api/clientes.php` | `admin`, `operador` + cuenta activa |

La autorización se aplica antes de procesar el cuerpo o ejecutar la operación.

### 6.4 CSRF

`csrf_token()` genera un token de 32 bytes; `csrf_campo()` lo inserta en los
formularios; `csrf_valido()` usa `hash_equals()` y rechaza arrays; `requiere_csrf()`
responde `403`. Se aplica en:

- login;
- alta y edición de clientes;
- baja de clientes;
- alta de pedidos;
- alta de usuarios;
- activación/desactivación de usuarios;
- logout.

### 6.5 Inyección SQL

Todas las consultas con datos del usuario usan PDO y parámetros vinculados. No se
concatenan valores en el SQL. Los `LIKE` usan `preparar_like()` para escapar
comodines.

### 6.6 XSS

Toda salida dinámica pasa por `e()` (`htmlspecialchars` con `ENT_QUOTES` y
UTF-8). Los atributos `data-*` también se escapan. La API no devuelve HTML.

### 6.7 Validación y errores

- Validación de tipo, longitud, formato de email, fecha ISO, estado e importe.
- `filter_input()` para IDs.
- Errores genéricos al usuario; detalle en `error_log`.
- `display_errors` desactivado salvo `GESTION_DEBUG=1`.

### 6.8 Integridad y transacciones

- FK `RESTRICT` en pedidos.
- `chk_pedidos_importe` en instalación nueva.
- `SELECT ... FOR UPDATE` y transacción en baja de clientes.
- `SELECT ... FOR UPDATE` y transacción en cambio de estado de usuarios.
- Bloqueo del último admin activo y de la auto-desactivación.

---

## 7. Pruebas

### 7.1 Método

Se usó una sesión HTTP real contra Apache/XAMPP y consultas directas a MySQL para
verificar el estado final. También se usó Chrome headless para las capturas.

Ambiente:

- PHP 8.x en XAMPP;
- MariaDB/MySQL de XAMPP;
- Apache en `http://localhost`;
- aplicación en `/gestion_clientes`.

### 7.2 Resultado

**21 pruebas ejecutadas: 21 PASS, 0 FAIL.**

| ID | Caso | Resultado |
|---|---|---|
| T01 | Login correcto | 200, panel |
| T02 | Login inválido | redirección `error=1` |
| T03 | Sin sesión | redirección a login |
| T04 | Alta de cliente | fila creada |
| T05 | Edición de cliente | dato actualizado |
| T06 | Búsqueda con/sin resultados | ambos estados correctos |
| T07 | Alta de pedido | importe `1234.56` guardado |
| T08 | Importe negativo | rechazado, sin inserción |
| T09 | Baja con pedidos | bloqueada, filas conservadas |
| T10 | Reportes | totales y páginas correctos |
| T11 | POST sin CSRF | 403 |
| T12 | XSS almacenado | escapado |
| T13 | Operador en usuarios | 403 |
| T14 | Operador elimina cliente | 403 |
| T15 | API 200/401/405 | correctos |
| T16 | API parámetros inválidos | 400 |
| T17 | Alta de usuario | hash bcrypt |
| T18 | Baja lógica | sesión invalidada |
| T19 | Auto-desactivación admin | bloqueada |
| T20 | Sesión admin | sigue operativa |
| T21 | API fuera de servicio | 404 controlado |

Durante T10 se creó un pedido temporal de `1.234,56`; por eso el total verificado
fue `4.070.774,11`. La base entregada, sin datos de prueba, queda en
`4.069.539,55` (15 pedidos).

### 7.3 Evidencias

- `evidencias/resultados_t01_t21.json`: resultados estructurados.
- `evidencias/pruebas_t01_t21.txt`: resumen legible.
- `evidencias/capturas_semana_8.json`: inventario de capturas.
- `capturas/`: 18 imágenes de las pantallas principales y casos de error.

---

## 8. Hallazgos y correcciones

| Hallazgo | Corrección |
|---|---|
| `menu.php` usaba `esConsulta()`, eliminada al migrar roles. | Se reemplazó por el rol `operador` y `etiqueta_rol()`. |
| La baja de clientes podía dejar pedidos huérfanos por `CASCADE`. | Se migró a `RESTRICT` y se agregó comprobación transaccional. |
| Usuarios desactivados conservaban la sesión. | `requiereLogin()` verifica `activo` en cada request. |
| Roles históricos `consulta` en la API. | Se actualizó la allowlist a `admin`/`operador`. |
| Reportes mezclados en archivos heredados. | Se crearon tres reportes separados y se enlazaron desde el índice. |
| Marca visible "Gestión de Clientes". | Se cambió a "Sistema de Gestión Comercial" en header, login, menú y footer. |

---

## 9. Limitaciones

- No hay edición, anulación ni detalle de líneas de pedido.
- No se implementó auditoría de cambios.
- No hay rate limiting ni bloqueo por intentos fallidos.
- Los reportes no tienen exportación a PDF/CSV.
- La paginación de pedidos y reportes es básica.
- Las credenciales de laboratorio deben rotarse en producción.
- La aplicación no incluye HTTPS; debe configurarse en el servidor.

---

## 10. Conclusión

La entrega cumple el objetivo de integrar clientes, pedidos, reportes y usuarios
en un sistema coherente, con datos preservados y controles de seguridad en
servidor. La separación de responsabilidades, el uso de consultas preparadas, la
verificación de cuenta activa y las pruebas automatizadas permiten defender el
funcionamiento y justificar las decisiones técnicas.
