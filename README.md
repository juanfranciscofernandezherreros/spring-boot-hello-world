# Spring Boot Hello World

Proyecto Spring Boot con:

- **Java 17** (compilación) / JRE 21 (runtime Docker — compatible por retrocompatibilidad de bytecode)
- **Spring Boot 3.2.5** + Spring Cloud 2023.0.1
- Planificación dinámica de tareas HTTP (cron configurable por properties)
- Lombok 1.18.32
- AOP para logging de peticiones
- Tests unitarios e integración (JUnit 4/5 + Mockito + MockMvc)
- Perfiles por entorno (`dev`, `int`, `qa`, `prod`) + fichero de entorno
- Docker listo para `build` y `run`

---

## 1) Requisitos

- **Java 17** o superior (JDK)
- **Maven 3.8+**
- **Docker** (opcional, para despliegue con contenedores)

---

## 2) Estructura del proyecto

```
spring-boot-hello-world/
├── Dockerfile
├── README.md
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/fernandez/
    │   │   ├── HelloWorldApplication.java          # Punto de entrada
    │   │   ├── HelloWorldController.java           # GET /
    │   │   ├── aop/
    │   │   │   └── RequestLoggingAspect.java       # Logging AOP
    │   │   ├── constants/
    │   │   │   └── UrlConstants.java
    │   │   ├── exception/
    │   │   │   └── ApiExceptionHandler.java        # Handler global
    │   │   └── scheduler/
    │   │       ├── config/
    │   │       │   ├── SchedulerConfig.java        # TaskScheduler bean
    │   │       │   └── SchedulerTaskProperties.java # Config properties
    │   │       └── service/
    │   │           └── DynamicSchedulerService.java # Planificador dinámico
    │   └── resources/
    │       ├── application.properties              # Configuración base
    │       ├── application-dev.properties
    │       ├── application-int.properties
    │       ├── application-qa.properties
    │       ├── application-prod.properties
    │       └── application-env.properties
    └── test/
        └── java/com/fernandez/
            ├── HelloWorldControllerTest.java
            ├── HelloWorldIntegrationTest.java
            └── scheduler/
                ├── DynamicSchedulerServiceTest.java
                └── SchedulerTaskPropertiesTest.java
```

---

## 3) Clases de soporte

| Clase | Paquete | Función |
|-------|---------|---------|
| `ApiExceptionHandler` | `exception` | Handler global `@RestControllerAdvice` |
| `RequestLoggingAspect` | `aop` | Logging AOP de peticiones entrantes/salientes |
| `UrlConstants` | `constants` | Constantes de rutas URL |

---

## 4) Perfiles y properties

Archivos en `src/main/resources`:

| Fichero | Descripción |
|---------|-------------|
| `application.properties` | Configuración base (puerto, scheduler, actuator) |
| `application-env.properties` | Variables comunes de entorno |
| `application-dev.properties` | H2 en memoria, `ddl-auto=create-drop` |
| `application-int.properties` | H2 en memoria, `ddl-auto=validate` |
| `application-qa.properties` | H2 en memoria, `ddl-auto=validate` |
| `application-prod.properties` | Datasource por variables de entorno, `ddl-auto=validate` |

El perfil activo se controla con la variable de entorno `APP_ENV` (por defecto: `dev`):

```properties
spring.profiles.active=${APP_ENV:dev}
```

### Variables de entorno

| Variable | Defecto | Descripción |
|----------|---------|-------------|
| `APP_ENV` | `dev` | Perfil activo (`dev`, `int`, `qa`, `prod`) |
| `APP_LOG_LEVEL` | `INFO` | Nivel de log |
| `DB_URL` | `jdbc:h2:mem:proddb` | URL de BD (solo perfil `prod`) |
| `DB_USER` | `sa` | Usuario de BD (solo perfil `prod`) |
| `DB_PASSWORD` | _(vacío)_ | Contraseña de BD (solo perfil `prod`) |

---

## 5) Cómo compilar, ejecutar y testear

### 5.1 Compilar

```bash
mvn clean compile
```

### 5.2 Ejecutar en local

```bash
mvn spring-boot:run -DskipTests \
  -Dspring-boot.run.jvmArguments="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED"
```

La aplicación arranca en **http://localhost:8080**.

Para seleccionar un perfil diferente:

```bash
APP_ENV=qa mvn spring-boot:run -DskipTests \
  -Dspring-boot.run.jvmArguments="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED"
```

### 5.3 Empaquetar JAR

```bash
mvn clean package -DskipTests
```

Genera: `target/spring-boot-hello-world-0.0.1.jar`

### 5.4 Ejecutar el JAR directamente

```bash
java --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/java.io=ALL-UNNAMED \
     -jar target/spring-boot-hello-world-0.0.1.jar
```

### 5.5 Tests

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests del scheduler
mvn test -Dtest=DynamicSchedulerServiceTest
```

---

## 6) Endpoints y curl (request/response)

### 6.1 Hello World

```bash
curl http://localhost:8080/
```

Respuesta de ejemplo:

```json
{
  "hostname": "mi-host",
  "ip": "172.17.0.1",
  "message": "Hello World!"
}
```

---

## 7) Planificación dinámica (Dynamic Scheduler)

El proyecto incluye un sistema de planificación dinámica que ejecuta llamadas HTTP en base a expresiones cron configuradas en `application.properties`.

### Configuración

Cada tarea se define con el prefijo `component-config.dynamic-scheduling.tasks.<nombre>`:

```properties
component-config.dynamic-scheduling.tasks.alert.enabled=true
component-config.dynamic-scheduling.tasks.alert.cron=0 0/30 * * * *
component-config.dynamic-scheduling.tasks.alert.url=https://localhost:8443/api/endpoint
```

| Propiedad | Descripción |
|-----------|-------------|
| `enabled` | `true`/`false` — activa o desactiva la tarea |
| `cron` | Expresión cron de Spring (6 campos: seg min hora día mes díaSemana) |
| `url` | URL HTTP a la que se hace GET en cada ejecución |

### Tareas preconfiguradas

| Tarea | Cron | Horario | URL destino |
|-------|------|---------|-------------|
| `alert` | `0 0/30 * * * *` | Cada 30 minutos | `https://localhost:8443/observability-alert-observer/api/sla/status-advice-feedback` |
| `item33` | `0 0 8 * * MON-FRI` | 08:00 L-V | `https://localhost:8443/observability-item-orchestrator/api/item/item33` |
| `item35` | `0 0 7 * * MON-FRI` | 07:00 L-V | `https://localhost:8443/observability-item-orchestrator/api/item/item35` |
| `item36` | `0 0 8 * * MON-FRI` | 08:00 L-V | `https://localhost:8443/observability-item-orchestrator/api/item/item36` |
| `alert-eof-started-customer` | `0 0 3 * * 2-6` | 03:00 Mar-Sáb | `https://localhost:8443/observability-alert-observer/api/sla/end-of-day` |
| `alert-eof-started-authority` | `0 0 7 * * 2-6` | 07:00 Mar-Sáb | `https://localhost:8443/observability-alert-observer/api/sla/end-of-day` |
| `alert-eof-customer` | `0 0 6 * * 2-6` | 06:00 Mar-Sáb | `https://localhost:8443/observability-alert-observer/api/sla/end-of-day` |
| `alert-eof-authority` | `0 0 12 * * 2-6` | 12:00 Mar-Sáb | `https://localhost:8443/observability-alert-observer/api/sla/end-of-day` |

### Arquitectura del scheduler

- **`SchedulerConfig`**: Configura un `ThreadPoolTaskScheduler` con pool de 5 hilos y shutdown graceful (30s).
- **`SchedulerTaskProperties`**: Clase `@ConfigurationProperties` que mapea las tareas desde properties.
- **`DynamicSchedulerService`**: Al iniciar la app (`@PostConstruct`), programa las tareas habilitadas con `CronTrigger`. Soporta cancelación en runtime.

---

## 8) Docker

### Build de la imagen

```bash
# 1. Empaquetar el JAR
mvn clean package -DskipTests

# 2. Construir la imagen Docker
docker build -t spring-boot-hello-world:0.0.1 .
```

### Ejecutar con Docker

```bash
# Perfil dev (por defecto)
docker run --rm -p 8080:8080 spring-boot-hello-world:0.0.1

# Perfil específico
docker run --rm -p 8080:8080 -e APP_ENV=dev  spring-boot-hello-world:0.0.1
docker run --rm -p 8080:8080 -e APP_ENV=int  spring-boot-hello-world:0.0.1
docker run --rm -p 8080:8080 -e APP_ENV=qa   spring-boot-hello-world:0.0.1
docker run --rm -p 8080:8080 -e APP_ENV=prod spring-boot-hello-world:0.0.1

# Perfil prod con variables de base de datos
docker run --rm -p 8080:8080 \
  -e APP_ENV=prod \
  -e DB_URL=jdbc:postgresql://db-host:5432/mydb \
  -e DB_USER=admin \
  -e DB_PASSWORD=secret \
  spring-boot-hello-world:0.0.1
```

### Dockerfile

La imagen usa **Eclipse Temurin JRE 21** como runtime. Variables de entorno disponibles:

| Variable | Defecto | Descripción |
|----------|---------|-------------|
| `APP_ENV` | `dev` | Perfil de Spring activo |
| `JAVA_OPTS` | `--add-opens ...` | Opciones JVM |
| `JAR_OPTS` | _(vacío)_ | Argumentos adicionales para el JAR |

---

## 9) Tests

El proyecto incluye tests unitarios e integración:

| Clase de test | Tipo | Qué verifica |
|---------------|------|--------------|
| `HelloWorldControllerTest` | Unitario | Respuesta del controlador HelloWorld |
| `HelloWorldIntegrationTest` | Integración | GET `/` devuelve "Hello World!" (MockMvc) |
| `DynamicSchedulerServiceTest` | Unitario | Planificación, cancelación y ejecución de tareas |
| `SchedulerTaskPropertiesTest` | Integración | Binding de properties a configuración |

Ejecutar todos:

```bash
mvn test
```

---

## 10) Stack tecnológico

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 17 | Lenguaje y compilación |
| Spring Boot | 3.2.5 | Framework principal |
| Spring Cloud | 2023.0.1 | Gestión de dependencias cloud |
| Spring Data JPA | (gestionado por Boot) | Acceso a datos |
| H2 Database | (gestionado por Boot) | Base de datos en memoria |
| MapStruct | 1.5.5.Final | Mapeo DTO ↔ Entidad |
| Lombok | 1.18.32 | Reducción de boilerplate |
| JUnit 4/5 | (gestionado por Boot) | Tests unitarios e integración |
| Mockito | (gestionado por Boot) | Mocking en tests |
| Docker | — | Contenerización (Temurin JRE 21) |
