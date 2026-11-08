# Manual de usuario - Sistema de Gestión Comercial

## 1. Ingreso al sistema

1. Abrir `http://localhost/gestion_clientes/`.
2. Escribir usuario y contraseña.
3. Presionar **Ingresar**.

Usuarios de laboratorio:

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `Admin2026!` | Administrador |
| `operador` | `Operador2026!` | Operador |
| `consulta` | `Consulta2026!` | Operador histórico |

Si las credenciales son incorrectas, el sistema muestra un mensaje genérico. No
indica si el usuario existe. Si la cuenta fue desactivada, el ingreso se rechaza.

Para salir, usar **Cerrar sesión** en la barra superior y confirmar. El cierre se
ejecuta por `POST` con token CSRF.

---

## 2. Roles

### Administrador (`admin`)

Puede:

- ver el panel, clientes, pedidos y reportes;
- crear y editar clientes;
- eliminar clientes sin pedidos;
- crear pedidos;
- administrar usuarios;
- activar y desactivar cuentas.

No puede desactivar su propia cuenta ni dejar el sistema sin administrador activo.

### Operador (`operador`)

Puede:

- ver el panel, clientes, pedidos y reportes;
- crear y editar clientes;
- crear pedidos.

No puede:

- eliminar clientes;
- administrar usuarios;
- activar o desactivar cuentas.

Si intenta acceder a una ruta restringida, el servidor responde `403`.

---

## 3. Panel principal

Después de iniciar sesión se muestra el panel con:

- **Clientes**: cantidad total registrada.
- **Pedidos**: cantidad total registrada.
- **Total vendido**: suma de los importes.
- **Ticket promedio**: importe medio por pedido.
- **Últimos pedidos**: cinco pedidos recientes con cliente, fecha, estado e
  importe.
- **Accesos rápidos**: nuevo cliente, nuevo pedido, listado de pedidos, reportes y
  usuarios (solo admin).

Desde el panel se puede navegar a todos los módulos con la barra superior.

---

## 4. Clientes

### 4.1 Listado

Ruta: `clientes/listar.php`.

Permite:

- buscar por nombre, email o ciudad;
- filtrar por ciudad;
- ordenar por ID, nombre, email o ciudad;
- elegir 5, 10 o 20 filas por página;
- abrir la ficha con **Ver**;
- editar con **Editar** (admin y operador);
- eliminar con **Eliminar** (solo admin).

Si la búsqueda no encuentra coincidencias, se muestra un mensaje informativo.

### 4.2 Alta

Ruta: `clientes/nuevo.php`.

Campos:

| Campo | Obligatorio | Regla |
|---|---|---|
| Nombre | Sí | Hasta 80 caracteres. |
| Email | Sí | Formato válido, hasta 120 caracteres. |
| Teléfono | No | Hasta 30 caracteres. |
| Ciudad | No | Hasta 60 caracteres. |

Al guardar, el sistema valida en el servidor. Si hay errores, vuelve al formulario
con los datos cargados y los mensajes correspondientes.

### 4.3 Edición

Ruta: `clientes/editar.php?id=...`.

Se aplican las mismas validaciones que en el alta. Al guardar correctamente se
vuelve a la ficha del cliente.

### 4.4 Ficha del cliente

Ruta: `clientes/ver.php?id=...`.

Muestra:

- datos personales;
- cantidad de pedidos;
- total comprado;
- historial de pedidos con fecha, descripción, estado e importe;
- botón para crear un pedido para ese cliente;
- acciones de edición y eliminación según el rol.

### 4.5 Eliminación

Ruta: `clientes/eliminar.php?id=...`.

La eliminación es solo para administradores. El sistema:

1. pide confirmación;
2. comprueba si el cliente tiene pedidos;
3. si tiene pedidos, rechaza la operación y explica el motivo;
4. si no tiene pedidos, elimina con `POST` y CSRF.

No se eliminan pedidos en cascada. La integridad referencial se conserva.

---

## 5. Pedidos

### 5.1 Listado

Ruta: `pedidos/listar.php`.

Permite filtrar por:

- cliente (nombre o ID);
- estado (`pendiente`, `pagado`, `enviado`);
- fecha desde y hasta;
- página.

Cada fila muestra ID, fecha, cliente, descripción, estado e importe. El botón
**Ver** abre el detalle.

### 5.2 Alta

Ruta: `pedidos/nuevo.php`.

Campos:

| Campo | Obligatorio | Regla |
|---|---|---|
| Cliente | Sí | Debe existir en la base. |
| Descripción | Sí | Hasta 150 caracteres. |
| Importe | Sí | Mayor que cero, hasta 8 enteros y 2 decimales. Se acepta coma o punto decimal. |
| Fecha | Sí | Fecha válida en formato `YYYY-MM-DD`. |
| Estado | Sí | `pendiente`, `pagado` o `enviado`. |

El sistema guarda el usuario de la sesión como responsable del pedido. Si el
cliente no existe, el importe es negativo o la fecha es inválida, no se inserta
nada y se vuelve al formulario con el error.

### 5.3 Detalle

Ruta: `pedidos/detalle.php?id=...`.

Muestra:

- número de pedido;
- fecha;
- cliente y email;
- descripción;
- estado;
- importe;
- usuario responsable;
- accesos a la ficha del cliente y a un nuevo pedido.

---

## 6. Reportes

Ruta principal: `reportes/index.php`.

Los reportes son de solo lectura. Se puede filtrar por fecha inicial y final. Si
no se envían fechas válidas, se usa el año actual.

### 6.1 Ventas

Ruta: `reportes/ventas.php`.

Indicadores:

- **Cantidad de pedidos**: `COUNT(*)`.
- **Total vendido**: `SUM(importe)`.
- **Ticket promedio**: `AVG(importe)`.
- **Clientes con compras**: `COUNT(DISTINCT cliente_id)`.

Tablas:

- evolución mensual;
- ventas por estado;
- ranking de clientes.

### 6.2 Clientes

Ruta: `reportes/clientes.php`.

Indicadores:

- clientes totales;
- clientes con compras;
- clientes sin compras;
- total vendido;
- promedio por cliente con compras.

La tabla muestra pedidos, total comprado y última compra por cliente. Los clientes
sin pedidos aparecen con cero.

### 6.3 Pedidos

Ruta: `reportes/pedidos.php`.

Permite filtrar por fecha y estado. Muestra:

- cantidad de pedidos;
- total facturado;
- ticket promedio;
- resumen por estado;
- detalle de hasta 300 pedidos.

---

## 7. Usuarios (solo administrador)

### 7.1 Listado

Ruta: `usuarios/listar.php`.

Muestra:

- usuario;
- nombre;
- rol;
- estado (activo/inactivo);
- fecha de alta;
- acciones.

Resumen: total, activos, inactivos, administradores y operadores.

### 7.2 Alta

Ruta: `usuarios/nuevo.php`.

Campos:

| Campo | Obligatorio | Regla |
|---|---|---|
| Usuario | Sí | 3–50 caracteres: letras, números, punto, guion o guion bajo. |
| Nombre completo | Sí | Hasta 100 caracteres. |
| Contraseña | Sí | Mínimo 8 caracteres. |
| Confirmar contraseña | Sí | Debe coincidir. |
| Rol | Sí | `admin` u `operador`. |

La contraseña se guarda con `password_hash()`. Nunca se muestra ni se guarda en
texto plano.

### 7.3 Activación y desactivación

En el listado, el administrador puede activar o desactivar una cuenta. La baja es
lógica: la fila se conserva y `activo` pasa a `0`.

Reglas:

- no se puede desactivar la propia cuenta;
- no se puede desactivar el último administrador activo;
- una cuenta desactivada pierde la sesión en la siguiente solicitud;
- la cuenta puede reactivarse sin crear otra fila.

---

## 8. API de clientes

La API es privada y de solo lectura.

```http
GET /gestion_clientes/api/clientes.php?q=Rosario
```

Parámetros:

- `q`: busca en nombre o ciudad (máximo 80 caracteres);
- `ciudad`: filtro exacto (máximo 60 caracteres).

Respuesta correcta: array JSON con `id`, `nombre`, `email` y `ciudad`.

Códigos:

- `200`: correcto;
- `400`: parámetro inválido;
- `401`: sin sesión o cuenta inactiva;
- `403`: rol no permitido;
- `405`: método distinto de `GET`;
- `500`: error interno.

La API solo acepta `GET`. No se exponen teléfono ni fecha de alta.

---

## 9. Mensajes y errores frecuentes

| Mensaje | Causa | Solución |
|---|---|---|
| Usuario o contraseña incorrectos | Credenciales inválidas o cuenta inactiva | Verificar datos o contactar al administrador. |
| La sesión expiró o la cuenta fue desactivada | Baja lógica o sesión vencida | Volver a iniciar sesión. |
| No tiene permisos | Rol insuficiente | Usar una cuenta con el rol adecuado. |
| No se puede eliminar el cliente porque tiene pedidos | Integridad referencial | Reasignar o eliminar los pedidos primero. |
| El cliente seleccionado no existe | ID manipulado o cliente eliminado | Elegir un cliente válido. |
| El importe debe ser mayor que cero | Importe negativo o cero | Ingresar un importe positivo. |
| El estado seleccionado no es válido | Valor fuera de la lista | Elegir pendiente, pagado o enviado. |
| Solicitud no válida | CSRF ausente o vencido | Recargar la página y volver a intentar. |

---

## 10. Recomendaciones de uso

- Cerrar sesión al terminar, especialmente en equipos compartidos.
- No compartir credenciales.
- Usar el rol operador para tareas diarias y reservar admin para administración.
- Verificar el cliente antes de registrar un pedido.
- Revisar los reportes con un rango de fechas acotado.
- No eliminar clientes con historial comercial; la aplicación lo impide.
- En producción, cambiar las contraseñas de laboratorio y habilitar HTTPS.
