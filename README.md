# Biblioteca Servicios API

API REST para una **biblioteca universitaria** organizada en servicios: administración de estudiantes, libros, préstamos, pagos y notificaciones, **devolución de libros** y **trámite de constancia de estudios** (verificación de inscripción, pago, generación del documento y envío por correo).

> **Sin autenticación.** Todos los endpoints son públicos. La generación del PDF y el envío de correo del trámite de constancia están **simulados**: solo escriben en el log de la aplicación.

**Autor:** Sergio Alvarado Carrasco

---

## Tecnologías

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje |
| Spring Boot 4.1.1 | Framework (Web MVC, Data JPA) |
| Hibernate / JPA | Persistencia |
| MySQL 8 | Base de datos |
| Maven (wrapper incluido) | Compilación y ejecución |

## Requisitos previos

- JDK 21
- MySQL 8 en ejecución
- Postman (o similar) para probar la API

## Instalación y ejecución

```bash
# 1. Clonar el repositorio
git clone <URL-DEL-REPOSITORIO>
cd biblioteca-servicios

# 2. Configurar las credenciales de MySQL (ver la sección siguiente)

# 3. Ejecutar
./mvnw spring-boot:run          # Linux / macOS
mvnw.cmd spring-boot:run        # Windows
```

La API queda en `http://localhost:8080`.

No hace falta crear las tablas a mano: Hibernate las genera al arrancar (`ddl-auto=update`). La base `biblioteca` se crea sola si no existe (`createDatabaseIfNotExist=true` en la URL).

### Configurar la conexión a la base de datos

El proyecto necesita un usuario de MySQL con permiso para crear la base `biblioteca` en `localhost:3306`. Hay dos formas de indicar las credenciales; elige una.

#### Opción A: variables de entorno (recomendada)

No hay que tocar ningún archivo, así las credenciales nunca terminan en el repositorio.

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_USERNAME` | Usuario de MySQL | *(sin valor: obligatoria)* |
| `DB_PASSWORD` | Contraseña de MySQL | *(sin valor: obligatoria; si tu usuario no tiene contraseña, defínela vacía)* |

Si alguna de las dos no está definida, la aplicación no arranca.

```bash
# Linux / macOS
export DB_USERNAME=mi_usuario
export DB_PASSWORD=mi_password
./mvnw spring-boot:run
```

```powershell
# Windows (PowerShell)
$env:DB_USERNAME="mi_usuario"
$env:DB_PASSWORD="mi_password"
.\mvnw.cmd spring-boot:run
```

#### Opción B: editar `application.properties`

Abre `src/main/resources/application.properties` y sustituye las referencias a las variables por tus datos:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/biblioteca?createDatabaseIfNotExist=true
spring.datasource.username=mi_usuario
spring.datasource.password=mi_password
```

Si tu MySQL no está en `localhost:3306`, cambia también la URL.

Después ejecuta normalmente con `./mvnw spring-boot:run`.

> **Importante:** si usas esta opción, no subas el archivo con tus credenciales reales a GitHub. Deja `${DB_USERNAME}` y `${DB_PASSWORD}` en el repositorio y cambia los valores solo en tu copia local.

### Parámetros de negocio configurables

En `application.properties`:

| Propiedad | Descripción | Valor por defecto |
|---|---|---|
| `prestamo.dias-plazo` | Días de plazo de un préstamo; si se devuelve después, el estatus es `DEVUELTO_CON_RETRASO` | `7` |
| `constancia.costo` | Costo del trámite de constancia de estudios | `50.00` |

## Estructura del proyecto

```
src/main/java/com/api/biblioteca
├── controllers/     Controladores REST y manejo global de errores
├── dto/             Objetos de petición y respuesta
├── models/          Entidades JPA, convertidor del estado del libro y evento LibroDevuelto
├── repositories/    Repositorios Spring Data JPA
└── services/        Servicios de negocio, validaciones, códigos de error y esquema canónico
```

## Arquitectura de servicios

El proyecto separa los servicios según su responsabilidad:

| Tipo | Servicio | Operaciones principales |
|---|---|---|
| Entidad | `EstudianteService` | CRUD, `consultarEstudiante`, `verificarInscripcion` |
| Entidad | `LibroService` | CRUD, `consultarDisponibilidad`, `marcarPrestado`, `marcarDisponible` |
| Entidad | `PrestamoService` | CRUD, `devolverLibro` |
| Entidad | `PagoService` | CRUD, `procesarPago` |
| Utilidad | `NotificacionService` | CRUD, `enviarCorreo` (simulado) |
| Utilidad | `DocumentoService` | `generarPDF` (simulado) |
| Tarea | `TramiteConstanciaService` | `solicitarConstancia`: orquesta estudiante → pago → documento → notificación |

Otros elementos:

- **Esquema canónico** (`EsquemaCanonico`): toda matrícula y código de libro que llega a la API se normaliza (sin espacios y en mayúsculas). `s22004567` y `S22004567` son la misma matrícula.
- **Evento `LibroDevuelto`**: al registrar una devolución, `PrestamoService` publica el evento y `LibroService` lo escucha para marcar el libro como disponible.
- **Códigos de error** (`CodigoError`): cada error de negocio tiene un código y un estado HTTP fijos.

## Modelo de datos

| Entidad | Tabla | Descripción |
|---|---|---|
| `Estudiante` | `estudiante` | Estudiante identificado por su `matricula` |
| `Libro` | `categoria_libro` | Libro del catálogo identificado por su `codigo` |
| `Prestamo` | `prestamo` | Préstamo de un libro a un estudiante |
| `Pago` | `pago` | Pago hecho por un estudiante |
| `Notificacion` | `notificacion` | Notificación dirigida a un estudiante |

Relaciones: un préstamo pertenece a **un estudiante** y a **un libro**; una notificación pertenece a **un estudiante**. Un pago guarda la matrícula del estudiante que lo hizo.

La tabla `categoria_libro` usa nombres de columna propios. En el JSON se exponen con nombres legibles:

| Columna | Campo JSON | Valores |
|---|---|---|
| `cve_lib` | `codigo` | Clave del libro |
| `tit_lib` | `titulo` | Título |
| `aut_lib` | `autor` | Autor |
| `edo_lib` | `disponible` | `'D'` en la base → `true`; `'P'` (prestado) → `false` |

Formatos de fecha: fecha simple `yyyy-MM-dd` (por ejemplo `2026-10-20`) y fecha con hora `yyyy-MM-ddTHH:mm:ss` (por ejemplo `2026-10-23T10:15:30`; puede incluir fracciones de segundo).

## Reglas de negocio

- **Identificadores.** La `matricula` (máx. 9 caracteres) y el `codigo` del libro (máx. 20) los elige quien los crea y no se pueden cambiar. El `id` de préstamos, pagos y notificaciones lo genera la base de datos. En un `PUT`, el identificador se toma de la URL; si viene en el cuerpo, se ignora.
- **Campos de texto.** Todos se guardan sin espacios al inicio ni al final. Si un campo obligatorio falta o supera su longitud máxima, la API responde `400`.
- **Estudiantes.** Requieren `matricula`, `nombre` y `correo`. `activo` es opcional: al crear vale `true` si no se envía, y al actualizar se conserva el valor anterior si no se envía.
- **Libros.** Requieren `codigo` y `titulo`; `autor` es opcional. Un libro nuevo nace disponible. La disponibilidad no se edita a mano: la cambian los préstamos.
- **Préstamos.** Se crean con `matricula` y `codigoLibro`. El estudiante debe estar activo y el libro disponible; al prestarlo, el libro queda como no disponible. `fechaPrestamo` es opcional (por defecto, hoy) y no puede ser futura.
- **Modificar un préstamo.** Se puede cambiar el estudiante, el libro o la fecha de préstamo mientras el préstamo siga activo. Si cambia el libro, el anterior queda disponible y el nuevo debe estar disponible. Un préstamo ya devuelto no se puede modificar.
- **Devolución.** Sigue la especificación de `devolverLibro` (ver [la sección siguiente](#especificación-de-devolverlibro)): el estudiante debe haber realizado el préstamo del libro que devuelve. Al devolver, se guarda la `fechaDevolucion` enviada y el libro queda disponible otra vez.
- **Pagos.** Requieren la `matricula` de un estudiante existente, `concepto` y un `monto` mayor a 0. Al crearlos se guardan con la fecha actual y estado `APROBADO`. Al actualizar se puede cambiar el `estado` (se guarda en mayúsculas).
- **Notificaciones.** Requieren la `matricula` de un estudiante existente y un `mensaje` (máx. 200 caracteres). La fecha se asigna sola al crearlas.
- **Constancia de estudios.** Solo la puede pedir un estudiante activo. El trámite registra un pago aprobado por `constancia.costo`, genera el documento y lo "envía" al correo del estudiante.
- **Borrados.** No se puede eliminar un estudiante con préstamos o notificaciones registrados, ni un libro con préstamos registrados. Eliminar un préstamo activo deja el libro disponible.

## Especificación de devolverLibro

| Elemento | Descripción |
|---|---|
| Servicio | Préstamo (dominio Biblioteca) |
| Capacidad | `devolverLibro`: registra la devolución de un libro por parte de un estudiante |
| Entrada | `matricula` (texto, 9 caracteres), `codigoLibro` (texto), `fechaDevolucion` (fecha), `folioPrestamo` (número) |
| Salida | `folioPrestamo` (número), `estatus` (texto), `fechaDevolucion` (fecha) |
| Reglas | El estudiante debe haber realizado el préstamo del libro que va a devolver |
| Errores | `PRESTAMO_NO_ENCONTRADO`, `LIBRO_YA_DEVUELTO` |

Cómo se aplica en la API (`POST /api/v1/prestamos/devoluciones`):

- Los cuatro campos de entrada son obligatorios. La `matricula` debe tener exactamente 9 caracteres.
- `folioPrestamo` es el `id` del préstamo. Si no existe, o si no pertenece a esa `matricula` y ese `codigoLibro`, la respuesta es `PRESTAMO_NO_ENCONTRADO` (404).
- Si el préstamo ya tiene fecha de devolución, la respuesta es `LIBRO_YA_DEVUELTO` (409).
- `fechaDevolucion` no puede ser futura ni anterior a la fecha del préstamo.
- `estatus` vale `DEVUELTO` si se devuelve dentro del plazo (`fechaPrestamo + prestamo.dias-plazo`) o `DEVUELTO_CON_RETRASO` si se devuelve después.
- Si faltan datos o son inválidos, la respuesta es `DATOS_DEVOLUCION_INVALIDOS` (400).

## Endpoints

Base URL: `http://localhost:8080/api/v1`. Los cuerpos van en JSON (`Content-Type: application/json`).

### Estudiantes — `/api/v1/estudiantes`

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/v1/estudiantes` | Listar estudiantes | 200 |
| GET | `/api/v1/estudiantes/{matricula}` | Obtener un estudiante | 200 / 404 |
| POST | `/api/v1/estudiantes` | Crear estudiante | 201 / 400 / 409 |
| PUT | `/api/v1/estudiantes/{matricula}` | Actualizar estudiante | 200 / 400 / 404 |
| DELETE | `/api/v1/estudiantes/{matricula}` | Eliminar estudiante | 204 / 404 / 409 |

### Libros — `/api/v1/libros`

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/v1/libros` | Listar libros | 200 |
| GET | `/api/v1/libros/{codigo}` | Obtener un libro | 200 / 404 |
| GET | `/api/v1/libros/{codigo}/disponibilidad` | Consultar si un libro está disponible | 200 / 404 |
| POST | `/api/v1/libros` | Crear libro | 201 / 400 / 409 |
| PUT | `/api/v1/libros/{codigo}` | Actualizar libro | 200 / 400 / 404 |
| DELETE | `/api/v1/libros/{codigo}` | Eliminar libro | 204 / 404 / 409 |

### Préstamos — `/api/v1/prestamos`

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/v1/prestamos` | Listar préstamos | 200 |
| GET | `/api/v1/prestamos/{id}` | Obtener un préstamo | 200 / 404 |
| POST | `/api/v1/prestamos` | Registrar préstamo | 201 / 400 / 404 / 409 |
| PUT | `/api/v1/prestamos/{id}` | Cambiar estudiante, libro o fecha de un préstamo activo | 200 / 400 / 404 / 409 |
| DELETE | `/api/v1/prestamos/{id}` | Eliminar préstamo | 204 / 404 |
| POST | `/api/v1/prestamos/devoluciones` | Registrar la devolución de un libro (`devolverLibro`) | 200 / 400 / 404 / 409 |

### Pagos — `/api/v1/pagos`

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/v1/pagos` | Listar pagos | 200 |
| GET | `/api/v1/pagos/{id}` | Obtener un pago | 200 / 404 |
| POST | `/api/v1/pagos` | Registrar pago | 201 / 400 / 402 / 404 |
| PUT | `/api/v1/pagos/{id}` | Actualizar pago | 200 / 400 / 404 |
| DELETE | `/api/v1/pagos/{id}` | Eliminar pago | 204 / 404 |

### Notificaciones — `/api/v1/notificaciones`

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/v1/notificaciones` | Listar notificaciones | 200 |
| GET | `/api/v1/notificaciones/{id}` | Obtener una notificación | 200 / 404 |
| POST | `/api/v1/notificaciones` | Crear notificación | 201 / 400 / 404 |
| PUT | `/api/v1/notificaciones/{id}` | Actualizar notificación | 200 / 400 / 404 |
| DELETE | `/api/v1/notificaciones/{id}` | Eliminar notificación | 204 / 404 |

### Constancias — `/api/v1/constancias`

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| POST | `/api/v1/constancias/{matricula}` | Solicitar constancia de estudios (sin cuerpo) | 201 / 404 / 409 |

## Manejo de errores

Los errores devuelven un JSON con el `codigo` del error y un `mensaje`:

```json
{ "codigo": "LIBRO_NO_ENCONTRADO", "mensaje": "Libro no encontrado" }
```

| Código HTTP | `codigo` | Cuándo |
|---|---|---|
| 400 Bad Request | `DATOS_INVALIDOS` | Falta un campo obligatorio, supera su longitud, monto no válido o fecha de préstamo futura. El `mensaje` indica el campo |
| 400 Bad Request | `JSON_INVALIDO` | Falta el cuerpo, el JSON está mal formado o una fecha o número tiene formato incorrecto |
| 400 Bad Request | `DATOS_DEVOLUCION_INVALIDOS` | En la devolución falta un campo, la matrícula no tiene 9 caracteres o la fecha de devolución es futura o anterior al préstamo |
| 402 Payment Required | `PAGO_RECHAZADO` | Se registra un pago con monto vacío, cero o negativo |
| 404 Not Found | `ESTUDIANTE_NO_ENCONTRADO` | La matrícula no existe |
| 404 Not Found | `LIBRO_NO_ENCONTRADO` | El código de libro no existe |
| 404 Not Found | `PRESTAMO_NO_ENCONTRADO` | El préstamo no existe, o en la devolución el folio no corresponde a esa matrícula y ese libro |
| 404 Not Found | `PAGO_NO_ENCONTRADO` | El pago no existe |
| 404 Not Found | `NOTIFICACION_NO_ENCONTRADA` | La notificación no existe |
| 409 Conflict | `ESTUDIANTE_YA_EXISTE` | Ya hay un estudiante con esa matrícula |
| 409 Conflict | `LIBRO_YA_EXISTE` | Ya hay un libro con ese código |
| 409 Conflict | `ESTUDIANTE_NO_INSCRITO` | El estudiante existe pero no está activo (préstamo o constancia) |
| 409 Conflict | `LIBRO_NO_DISPONIBLE` | Se intenta prestar un libro que ya está prestado |
| 409 Conflict | `LIBRO_YA_DEVUELTO` | Se intenta devolver un préstamo que ya fue devuelto |
| 409 Conflict | `PRESTAMO_YA_DEVUELTO` | Se intenta modificar un préstamo que ya fue devuelto |
| 409 Conflict | `ESTUDIANTE_CON_REGISTROS` | Se intenta eliminar un estudiante con préstamos o notificaciones |
| 409 Conflict | `LIBRO_CON_PRESTAMOS` | Se intenta eliminar un libro con préstamos |

## Ejemplos de peticiones y respuestas

Flujo completo de uso. Los IDs dependen de tu base de datos.

### 1. Crear un estudiante

`POST /api/v1/estudiantes`

```json
{
  "matricula": "S22004567",
  "nombre": "Laura Méndez Cruz",
  "correo": "laura.mendez@correo.com"
}
```

Respuesta `201 Created`:

```json
{
  "matricula": "S22004567",
  "nombre": "Laura Méndez Cruz",
  "correo": "laura.mendez@correo.com",
  "activo": true
}
```

### 2. Crear un libro

`POST /api/v1/libros`

```json
{
  "codigo": "LIB001",
  "titulo": "Cien años de soledad",
  "autor": "Gabriel García Márquez"
}
```

Respuesta `201 Created`:

```json
{
  "codigo": "LIB001",
  "titulo": "Cien años de soledad",
  "autor": "Gabriel García Márquez",
  "disponible": true
}
```

### 3. Registrar un préstamo

`POST /api/v1/prestamos`

```json
{
  "matricula": "S22004567",
  "codigoLibro": "LIB001",
  "fechaPrestamo": "2026-10-10"
}
```

`fechaPrestamo` es opcional; aquí se envía una fecha pasada para que la devolución del siguiente paso quede con retraso. El `id` de la respuesta es el **folio del préstamo**.

Respuesta `201 Created`:

```json
{
  "id": 1,
  "estudiante": {
    "matricula": "S22004567",
    "nombre": "Laura Méndez Cruz",
    "correo": "laura.mendez@correo.com",
    "activo": true
  },
  "libro": {
    "codigo": "LIB001",
    "titulo": "Cien años de soledad",
    "autor": "Gabriel García Márquez",
    "disponible": false
  },
  "fechaPrestamo": "2026-10-10",
  "fechaDevolucion": null
}
```

### 4. Devolver un libro

`POST /api/v1/prestamos/devoluciones`

```json
{
  "matricula": "S22004567",
  "codigoLibro": "LIB001",
  "fechaDevolucion": "2026-10-20",
  "folioPrestamo": 1
}
```

Respuesta `200 OK` (la fecha límite era el `2026-10-17`):

```json
{
  "folioPrestamo": 1,
  "estatus": "DEVUELTO_CON_RETRASO",
  "fechaDevolucion": "2026-10-20"
}
```

Si se devuelve dentro del plazo, `estatus` es `"DEVUELTO"`. Después de la devolución, `GET /api/v1/libros/LIB001/disponibilidad` responde `"disponible": true`.

### 5. Registrar un pago

`POST /api/v1/pagos`

```json
{
  "matricula": "S22004567",
  "concepto": "Reposición de credencial",
  "monto": 15.00
}
```

Respuesta `201 Created`:

```json
{
  "id": 1,
  "matricula": "S22004567",
  "concepto": "Reposición de credencial",
  "monto": 15.00,
  "fecha": "2026-10-20T16:40:00",
  "estado": "APROBADO"
}
```

### 6. Crear una notificación

`POST /api/v1/notificaciones`

```json
{
  "matricula": "S22004567",
  "mensaje": "Tu préstamo de Cien años de soledad vence mañana."
}
```

Respuesta `201 Created`:

```json
{
  "id": 1,
  "estudiante": {
    "matricula": "S22004567",
    "nombre": "Laura Méndez Cruz",
    "correo": "laura.mendez@correo.com",
    "activo": true
  },
  "mensaje": "Tu préstamo de Cien años de soledad vence mañana.",
  "fecha": "2026-10-20T16:45:00"
}
```

### 7. Solicitar una constancia de estudios

`POST /api/v1/constancias/S22004567` — sin cuerpo.

Respuesta `201 Created`:

```json
{
  "matricula": "S22004567",
  "folioPago": 2,
  "correoDestino": "laura.mendez@correo.com",
  "mensaje": "Constancia enviada correctamente"
}
```

En el log de la aplicación aparece cada paso del trámite:

```
[Trámite] Solicitud de constancia para S22004567
[Pago] Pago 2 aprobado para S22004567 por $50.00
[Documento] Documento generado: constancia de estudios de Laura Méndez Cruz (S22004567), folio de pago 2
[Notificación] Correo enviado a laura.mendez@correo.com | Asunto: Tu constancia de estudios | Adjunto: ...
```

### 8. Actualizar un estudiante

`PUT /api/v1/estudiantes/S22004567`

```json
{
  "nombre": "Laura Méndez Cruz",
  "correo": "laura.mendez@uni.mx",
  "activo": false
}
```

Respuesta `200 OK` con el estudiante actualizado.

### Ejemplos de error

`POST /api/v1/libros` sin título → `400 Bad Request`:

```json
{ "codigo": "DATOS_INVALIDOS", "mensaje": "El campo titulo es obligatorio" }
```

`POST /api/v1/estudiantes` con una matrícula ya registrada → `409 Conflict`:

```json
{ "codigo": "ESTUDIANTE_YA_EXISTE", "mensaje": "Ya existe un estudiante con esa matrícula" }
```

`POST /api/v1/prestamos` con un libro que ya está prestado → `409 Conflict`:

```json
{ "codigo": "LIBRO_NO_DISPONIBLE", "mensaje": "El libro no está disponible" }
```

`POST /api/v1/prestamos/devoluciones` repitiendo la devolución del ejemplo 4 → `409 Conflict`:

```json
{ "codigo": "LIBRO_YA_DEVUELTO", "mensaje": "El libro de este préstamo ya fue devuelto" }
```

`POST /api/v1/prestamos/devoluciones` con un folio que es de otro estudiante o de otro libro → `404 Not Found`:

```json
{ "codigo": "PRESTAMO_NO_ENCONTRADO", "mensaje": "El estudiante no tiene un préstamo de ese libro con el folio indicado" }
```

`POST /api/v1/prestamos/devoluciones` con una fecha anterior al préstamo → `400 Bad Request`:

```json
{ "codigo": "DATOS_DEVOLUCION_INVALIDOS", "mensaje": "La fecha de devolución no puede ser anterior a la fecha de préstamo" }
```

`PUT /api/v1/prestamos/1` sobre un préstamo ya devuelto → `409 Conflict`:

```json
{ "codigo": "PRESTAMO_YA_DEVUELTO", "mensaje": "No se puede modificar un préstamo que ya fue devuelto" }
```

`DELETE /api/v1/libros/LIB001` cuando tiene préstamos → `409 Conflict`:

```json
{ "codigo": "LIBRO_CON_PRESTAMOS", "mensaje": "No se puede eliminar el libro porque tiene préstamos registrados" }
```

`POST /api/v1/constancias/S22004567` con el estudiante inactivo → `409 Conflict`:

```json
{ "codigo": "ESTUDIANTE_NO_INSCRITO", "mensaje": "El estudiante no está inscrito" }
```

`GET /api/v1/pagos/99` → `404 Not Found`:

```json
{ "codigo": "PAGO_NO_ENCONTRADO", "mensaje": "Pago no encontrado" }
```
