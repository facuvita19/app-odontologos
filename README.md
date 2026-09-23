# Sistema de Gestión Odontológica

Aplicación de escritorio desarrollada en Java Swing para administrar pacientes, odontólogos, usuarios y turnos de una clínica odontológica.

El sistema utiliza MySQL mediante JDBC, Maven para la gestión de dependencias y Git para el control de versiones.

## Funcionalidades

### Usuarios

- Registro de cuentas de usuario.
- Inicio de sesión con roles de administrador y usuario.
- Contraseñas protegidas con PBKDF2 y salt.
- Vinculación entre una cuenta común y su ficha de paciente.
- Nombre de usuario único.

### Pacientes

- Alta y modificación de fichas.
- DNI único.
- Fecha de alta generada automáticamente.
- Sección "Mis datos" para usuarios comunes.
- Desactivación lógica para conservar el historial.

### Odontólogos

- Alta y modificación de profesionales.
- Matrícula única.
- Validación de edad.
- Desactivación lógica para conservar turnos históricos.

### Turnos

- Reserva mediante selección de odontólogo.
- Selección manual de paciente para administradores.
- Asignación automática del paciente para usuarios comunes.
- Horarios en intervalos de 30 minutos.
- Control de fechas pasadas y horarios superpuestos.
- Horarios consecutivos permitidos.
- Reutilización de horarios cuyos turnos fueron cancelados.
- Estados: Pendiente, Confirmado, Atendido, Cancelado y Ausente.
- Cancelación lógica y conservación del historial.

### Interfaz

- Interfaz gráfica desarrollada con Java Swing.
- Diseño visual centralizado mediante `EstilosUI`.
- Tablas ordenables y adaptables al tamaño de la ventana.
- Navegación diferente según el rol.
- Formularios con validaciones y navegación por teclado.

## Tecnologías utilizadas

- Java 17
- Java Swing
- Maven
- JDBC
- MySQL
- MySQL Connector/J
- Git
- Eclipse IDE

## Requisitos

Antes de ejecutar el proyecto se necesita:

- JDK 17 o superior.
- MySQL Server.
- Maven, integrado en Eclipse o instalado localmente.
- Una base de datos MySQL accesible desde la computadora.

## Configuración de la base de datos

### 1. Crear las tablas

Abrir MySQL Workbench y ejecutar el archivo:

```text
database/schema.sql
```

El script crea la base `clinica_odontologica` y las tablas:

```text
pacientes
odontologos
usuarios
turnos
```

### 2. Configurar la conexión local

En la raíz del proyecto, copiar:

```text
database.properties.example
```

con el nombre:

```text
database.properties
```

Completar el archivo con la configuración local:

```properties
db.url=jdbc:mysql://localhost:3306/clinica_odontologica?serverTimezone=America/Argentina/Buenos_Aires&useSSL=false
db.user=root
db.password=PASSWORD_LOCAL
```

`database.properties` contiene credenciales privadas y está excluido mediante `.gitignore`.

## Configuración del administrador

Después de crear las tablas, ejecutar una vez la clase:

```text
config.ConfigurarAdministrador
```

La utilidad solicita una contraseña y crea o actualiza la cuenta administrativa con una contraseña protegida.

Luego se puede ingresar con:

```text
Usuario: admin
Contraseña: la configurada localmente
```

## Ejecución

1. Importar el proyecto en Eclipse como proyecto Maven existente.
2. Esperar a que Maven descargue las dependencias.
3. Crear `database.properties` a partir del archivo de ejemplo.
4. Ejecutar `database/schema.sql` en MySQL.
5. Ejecutar `config.ConfigurarAdministrador` una vez.
6. Ejecutar la clase principal de la aplicación como Java Application.

## Estructura general

```text
src/
├── config/       Conexión y utilidades de configuración
├── dao/          Acceso a datos mediante MySQL
├── negocio/      Modelos y enumeraciones del dominio
├── servicio/     Reglas de negocio y validaciones
├── util/         Utilidades, como protección de contraseñas
└── vista/        Paneles y componentes de Java Swing

database/
└── schema.sql    Estructura completa de la base de datos
```

## Arquitectura

La aplicación utiliza una separación en capas:

```text
Vista Swing
    ↓
Servicios
    ↓
Interfaces DAO
    ↓
Implementaciones DAO MySQL
    ↓
Base de datos MySQL
```

Esta estructura permite separar la interfaz, las reglas de negocio y la persistencia.

## Seguridad

- Las contraseñas de usuarios no se guardan como texto legible.
- Se utiliza PBKDF2 con salt para generar los hashes.
- Las credenciales de MySQL se encuentran fuera del código fuente.
- El archivo privado `database.properties` no debe publicarse.
- Las consultas usan `PreparedStatement`.

## Datos locales excluidos

Los siguientes archivos no deben versionarse:

```text
database.properties
target/
pacientes.txt
odontologos.txt
usuarios.txt
turnos.txt
```

Los archivos `.txt` pertenecen a la versión anterior basada en serialización y se conservan únicamente como respaldo local.

## Autor

Facundo Vitale
