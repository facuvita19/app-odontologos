# Arquitectura del sistema

Este documento describe la arquitectura general del Sistema de Gestion Odontologica y la responsabilidad de cada capa.

## Diagrama de arquitectura

```mermaid
flowchart TB
    subgraph UI[Interfaz de usuario]
        LOGIN[Inicio de sesion]
        ADMIN[Panel administrativo]
        USER[Panel de usuario]
        FORMS[Formularios y listados Swing]
        STATS[Estadisticas y graficos]
    end

    subgraph SERVICES[Servicios y reglas de negocio]
        US[UsuarioService]
        PS[PacienteService]
        OS[OdontologoService]
        TS[TurnoService]
        ES[EstadisticasService]
    end

    subgraph DAO[Acceso a datos]
        IDAO[Interfaces DAO]
        MYSQLDAO[Implementaciones DAO MySQL]
    end

    subgraph INFRA[Infraestructura]
        CONFIG[ConexionBD y database.properties]
        SECURITY[PBKDF2 y salt]
        DB[(MySQL clinica_odontologica)]
    end

    subgraph BUILD[Construccion y calidad]
        MAVEN[Maven]
        JUNIT[JUnit 5]
        SUREFIRE[Maven Surefire]
        SHADE[Maven Shade]
        CHARTS[JFreeChart]
    end

    LOGIN --> US
    ADMIN --> FORMS
    USER --> FORMS
    ADMIN --> STATS
    STATS --> ES
    STATS --> CHARTS

    FORMS --> PS
    FORMS --> OS
    FORMS --> TS

    US --> IDAO
    PS --> IDAO
    OS --> IDAO
    TS --> IDAO
    ES --> IDAO

    IDAO --> MYSQLDAO
    MYSQLDAO --> CONFIG
    CONFIG --> DB
    US --> SECURITY

    MAVEN --> JUNIT
    JUNIT --> SUREFIRE
    MAVEN --> SHADE
    MAVEN --> CHARTS
```

## Responsabilidades por capa

### Vista

La capa `vista` contiene los paneles, formularios, tablas y componentes desarrollados con Java Swing. Esta capa presenta la informacion y delega las operaciones en los servicios.

### Servicios

La capa `servicio` concentra las reglas de negocio y las validaciones, entre ellas:

- Validacion de pacientes y odontologos.
- Agenda laboral de profesionales.
- Calculo de horarios disponibles.
- Prevencion de superposiciones.
- Validacion de transiciones de estado.
- Proteccion del acceso a turnos por usuario.

### DAO

La capa `dao` define interfaces de persistencia e implementaciones para MySQL. Las consultas utilizan `PreparedStatement` y mantienen separada la logica SQL de la interfaz grafica.

### Negocio

La capa `negocio` contiene los modelos y enumeraciones del dominio, como pacientes, odontologos, turnos, especialidades y estados.

### Configuracion y seguridad

- `ConexionBD` obtiene la configuracion desde `database.properties`.
- Las credenciales no se incluyen en el repositorio.
- Las contrasenas se protegen mediante PBKDF2 y salt.

### Construccion y pruebas

- Maven administra dependencias y construccion.
- JUnit 5 ejecuta las pruebas unitarias.
- Maven Surefire integra las pruebas con el ciclo de construccion.
- Maven Shade genera un JAR ejecutable con dependencias.
- JFreeChart genera los graficos administrativos.
