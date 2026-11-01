# ms-usuarios

Microservicio de control de usuarios e inicio de sesión del **Sistema de Foros en Línea**.
Trabaja junto con [ms-foro](../ms-foro), que valida los tokens emitidos aquí.

Tecnologías: Java 21, Spring Boot, Spring Data JPA, Spring Security (JWT), Oracle Database y Maven.
Puerto: `8081`.

## Estructura

```
src/main/java/com/foro/usuarios
├── config       Seguridad, CORS y JWT
├── controller   Controladores REST
├── dto          Datos de entrada y salida, con sus validaciones
├── exception    Excepciones y manejo global de errores (@RestControllerAdvice)
├── model        Entidades JPA
├── repository   Repositorios JPA
└── service      Interfaces e implementaciones con la lógica de negocio
```

## Base de datos

Tablas `ROL` y `USUARIO`, definidas en `database/script_ms_usuarios.sql`. Las claves se guardan cifradas con BCrypt.

| Usuario de prueba | Clave | Rol |
|---|---|---|
| admin | Admin2026* | ADMIN |
| moderador | Moderador2026* | MODERADOR |
| camila.rojas | Usuario2026* | USUARIO |
| diego.soto | Usuario2026* | USUARIO |

## Roles

| Rol | Privilegios |
|---|---|
| ADMIN | Gestiona usuarios y roles; asigna roles y desactiva cuentas |
| MODERADOR | En este servicio, igual que USUARIO; en ms-foro modera contenido ajeno |
| USUARIO | Administra solo su propia cuenta |

## Endpoints

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/auth/login` | Público | Inicia sesión y entrega el token |
| POST | `/api/usuarios` | Público | Registra un usuario con rol USUARIO |
| GET | `/api/usuarios?rol=` | ADMIN | Lista usuarios, con filtro opcional por rol |
| GET | `/api/usuarios/{id}` | Dueño o ADMIN | Obtiene un usuario |
| PUT | `/api/usuarios/{id}` | Dueño o ADMIN | Actualiza un usuario; rol y estado solo ADMIN |
| DELETE | `/api/usuarios/{id}` | Dueño o ADMIN | Elimina un usuario |
| GET | `/api/roles`, `/api/roles/{id}` | ADMIN | Consulta roles |
| POST | `/api/roles` | ADMIN | Crea un rol |
| PUT | `/api/roles/{id}` | ADMIN | Actualiza un rol |
| DELETE | `/api/roles/{id}` | ADMIN | Elimina un rol sin usuarios asignados |

Las pruebas están en `postman/ms-usuarios.postman_collection.json`. Los logins guardan el token automáticamente.

## Configuración

Requisitos: JDK 21, una base Oracle Autonomous Database y su wallet.

1. Descomprime el wallet en la raíz del disco, por ejemplo `C:/Wallet_foro`. Las rutas con espacios fallan con `TNS_ADMIN`.
2. Ejecuta `database/script_ms_usuarios.sql` en la base de datos.
3. Copia `.env.example` como `.env` y completa la conexión. Este archivo no se sube a Git.
4. Levanta el servicio:

```bash
./mvnw spring-boot:run
```

Para correr las pruebas unitarias (no necesitan base de datos):

```bash
./mvnw test
```
