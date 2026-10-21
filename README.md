# API Fintech

API REST de ejemplo para gestionar cuentas y usuarios, desarrollada con Java 21,
Spring Boot 4.1.1 y MySQL 8.4. La API y la base de datos se ejecutan en Docker.
No hace falta instalar Java, Maven ni MySQL en la computadora.

## Prerrequisitos

- Docker Engine funcionando
- Docker Compose (plugin `docker compose`)
- Make

## Clonar, iniciar y probar

```bash
git clone <URL_DEL_REPOSITORIO>
cd api-fintech
make up
```

`make up` construye la imagen de la API y levanta ambos servicios en segundo
plano. Compose espera a que el healthcheck de MySQL esté saludable antes de
iniciar la API. En el primer inicio, Docker puede tardar unos minutos en descargar
las imágenes y construir la aplicación. La construcción ejecuta `./mvnw verify`
dentro de la etapa de compilación, por lo que las pruebas deben pasar para que se
genere la imagen de la API.

Comprueba los servicios con:

```bash
docker compose ps
```

La API está lista cuando aparece `api` como `Up` y responde en
<http://localhost:8080/v3/api-docs>. También puedes abrir Swagger UI en
<http://localhost:8080/swagger-ui.html>. Para ver el arranque:

```bash
make logs
```

### Importar Postman

1. Abre Postman y selecciona **Import**.
2. Importa `postman/api-fintech.postman_collection.json`.
3. Ejecuta las solicitudes de la colección en orden. La solicitud **Crear cuenta**
   guarda el ID devuelto para que consulta, actualización y eliminación usen esa
   cuenta automáticamente. El número de cuenta de prueba es único en cada corrida.

La URL base de la colección es `http://localhost:8080`. Los endpoints son:

| Método | Ruta | Acción |
|---|---|---|
| GET | `/cuentas` | Listar |
| POST | `/cuentas` | Crear |
| GET | `/cuentas/{id}` | Consultar |
| PUT | `/cuentas/{id}` | Actualizar |
| DELETE | `/cuentas/{id}` | Eliminar |

### Respuestas de error

Los errores usan un formato JSON común. Por ejemplo, una cuenta inexistente
devuelve HTTP 404:

```json
{
  "timestamp": "2026-10-20T20:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "No existe la cuenta 99",
  "path": "/cuentas/99",
  "details": {}
}
```

Una entrada inválida devuelve HTTP 400 e identifica los campos con problemas en
`details`. Una cuenta duplicada o una violación de integridad devuelve HTTP 409.

## Base de datos

Compose crea automáticamente la base `fintech_db` y el usuario de desarrollo
`fintech` con contraseña `fintech`. MySQL escucha dentro de Compose en `3306` y
se publica en `localhost:3307`, evitando el puerto `3306` que puede usar un MySQL
instalado en WSL.

No se deben crear manualmente la base, el usuario, las tablas ni los datos.
Al iniciar la API, Spring ejecuta `schema-mysql.sql` y `data-mysql.sql`. Las
tablas se validan con JPA y los inserts pueden repetirse sin duplicar las dos
cuentas ni los dos usuarios de ejemplo. El contenedor conserva los datos en el
volumen nombrado `mysql-data`.

### MySQL Workbench

Crea una conexión **Standard TCP/IP** con estos valores:

- Hostname: `127.0.0.1`
- Port: `3307`
- Username: `fintech`
- Password: `fintech`
- Default Schema: `fintech_db`

## Comandos

```bash
make up      # Construye y levanta API y MySQL en segundo plano
make down    # Detiene y elimina contenedores; conserva el volumen
make logs    # Sigue los logs de ambos servicios
```

Para ejecutar la suite desde el equipo de desarrollo, con JDK 21 instalado:

```bash
make test
```

Este comando usa Maven Wrapper, así que no requiere instalar Maven. La suite
incluye pruebas unitarias del servicio, pruebas HTTP de validación y errores, y
una prueba JPA de persistencia y relación usando una base H2 en memoria. La
aplicación ejecutada por Compose sigue usando MySQL.

`make down` conserva la base. Para iniciar nuevamente, ejecuta `make up`; los
scripts de Spring volverán a ejecutarse de forma idempotente.
