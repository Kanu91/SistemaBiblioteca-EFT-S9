# Sistema de Gestión de Biblioteca Escolar

Aplicación de escritorio desarrollada con Java, Swing (JFrame), MySQL y JDBC. Organizada en paquetes que separan modelos, vistas, controladores, acceso a datos y servicios.

## Tecnologías

- Java 17 o superior
- Maven
- MySQL 8
- MySQL Connector/J
- Java Swing

## Requisitos

1. JDK 17 o superior e IntelliJ IDEA.
2. MySQL Server en ejecución y una base de datos `biblioteca`.
3. Credenciales de un usuario MySQL con permisos para consultar y modificar las tablas del sistema.

## Instalación

1. Abrir la carpeta `SistemaBiblioteca` como proyecto Maven en IntelliJ IDEA.
2. Preparar la base de datos con los scripts de la carpeta `sql/`:
   - Para una base de datos existente con las tablas del ejercicio: ejecutar `00_EJECUTAR_AHORA.sql`.
   - Para una instalación desde cero: ejecutar `01_crear_bd_desde_cero.sql` y luego `03_datos_demo.sql`.
3. Configurar en **Run > Edit Configurations > Main > Environment variables** la variable `MYSQL_PASSWORD` con la contraseña del usuario MySQL.
4. Ejecutar la clase `org.example.Main`.

Parámetros de conexión predeterminados: servidor `localhost`, puerto `3306`, usuario `root` y base de datos `biblioteca`. Es posible definir `MYSQL_HOST`, `MYSQL_PORT`, `MYSQL_USER` y `MYSQL_DATABASE` para utilizar valores distintos.

Las credenciales reales deben mantenerse fuera del repositorio. El archivo `.gitignore` excluye archivos de entorno y configuraciones locales frecuentes.

## Acceso de demostración

Los scripts de datos de prueba crean, cuando no existen, las siguientes cuentas:

| Rol | Correo | Contraseña de prueba |
|---|---|---|
| Bibliotecario | `admin@biblioteca.local` | `Biblioteca2026!` |
| Estudiante | `estudiante@biblioteca.local` | `Estudiante2026!` |

Estas credenciales son solo para un entorno de pruebas.

## Funcionalidades

- Autenticación y gestión de permisos según el rol del usuario.
- Gestión de libros y categorías.
- Gestión de estudiantes y usuarios.
- Registro de préstamos y devoluciones.
- Control de disponibilidad y actualización del stock.
- Historial de préstamos y reportes.
- Procesamiento de préstamos en segundo plano para mantener la interfaz disponible.

## Diseño y estructura

```text
SistemaBiblioteca/
├── pom.xml
├── sql/
│   ├── 00_EJECUTAR_AHORA.sql
│   ├── 00_PREPARAR_BD_EXISTENTE.sql
│   ├── 01_crear_bd_desde_cero.sql
│   └── 03_datos_demo.sql
└── src/
    ├── main/java/org/example/
    │   ├── Main.java
    │   ├── config/
    │   ├── controller/
    │   ├── dao/
    │   ├── model/
    │   ├── service/
    │   └── view/
    └── test/java/org/example/
```

El diseño utiliza MVC para organizar las responsabilidades de la aplicación, DAO para el acceso a datos y Singleton para administrar la configuración de conexión.

Las operaciones de préstamo utilizan `SwingWorker`, sincronización y transacciones con bloqueo de filas para controlar accesos simultáneos al stock. La fecha efectiva de devolución y los días de atraso se registran en una tabla auxiliar.

## Compilación y ejecución

Desde Maven:

```bash
mvn clean package
```

El ejecutable se obtiene en:

```text
target/SistemaBiblioteca-1.0.0.jar
```

Para ejecutarlo con el servidor MySQL configurado:

```bash
java -jar target/SistemaBiblioteca-1.0.0.jar
```

Antes de iniciar, la variable de entorno `MYSQL_PASSWORD` debe estar disponible en el proceso que ejecuta el JAR.

## Pruebas

El proyecto incorpora las clases `SmokeChecks` y `PruebaConcurrencia` en `src/test/java/org/example/` para verificar elementos del sistema. La prueba de concurrencia opera con registros de la base de datos; se recomienda utilizar datos destinados a pruebas.

Para una verificación funcional completa, comprobar inicio de sesión, operaciones CRUD, préstamo, devolución, stock y reportes con MySQL en funcionamiento.

## Entrega

El repositorio contiene el código fuente, archivos Maven, scripts SQL y documentación de ejecución. Los archivos de compilación de `target/` y las contraseñas locales no deben publicarse.
