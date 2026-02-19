# Spring Boot Hello World + Producto API

Proyecto Spring Boot con:

- Java 21
- Arquitectura hexagonal (aplicada al módulo `Producto`)
- CRUD de `Producto` + filtros por todos los campos
- MapStruct + Lombok
- AOP para logging de peticiones
- Swagger/OpenAPI 3
- Tests unitarios/integración de controlador
- Perfiles por entorno (`dev`, `int`, `qa`, `prod`) + fichero de entorno
- Docker listo para `build` y `run`

---

## 1) Requisitos

- Java 21
- Maven
- Docker (opcional)

---

## 2) Arquitectura (hexagonal)

Se estructuró el flujo de `Producto` en capas hexagonales:

- **In (casos de uso)**: `com.fernandez.application.port.in.ProductoUseCase`
- **Out (puerto persistencia)**: `com.fernandez.application.port.out.ProductoPersistencePort`
- **Aplicación/servicio**: `com.fernandez.service.ProductoServiceImpl`
- **Adaptador persistencia**: `com.fernandez.infrastructure.persistence.ProductoPersistenceAdapter`
- **Adaptador web**: `com.fernandez.ProductoController`

Soporte adicional:

- DTO: `com.fernandez.dto.ProductoDto`
- Mapper: `com.fernandez.mapper.ProductoMapper`
- Excepciones: `com.fernandez.exception.*`
- AOP logging: `com.fernandez.aop.RequestLoggingAspect`

---

## 3) Perfiles y properties

Archivos en `src/main/resources`:

- `application.properties` (base)
- `application-env.properties` (fichero de entorno común)
- `application-dev.properties`
- `application-int.properties`
- `application-qa.properties`
- `application-prod.properties`

Perfil activo por variable:

```properties
spring.profiles.active=${APP_ENV:dev}
```

---

## 4) Build y ejecución local

### Compilar

```bash
mvn clean compile
```

### Ejecutar

```bash
mvn spring-boot:run -DskipTests -Dspring-boot.run.jvmArguments="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED"
```

### Tests

```bash
mvn test
```

---

## 5) Swagger / OpenAPI 3

Con la app levantada:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

## 6) Endpoints y curl (request/response)

### 6.1 Hello World

```bash
curl http://localhost:8080/
```

Ejemplo:
```json
{
  "hostname": "mi-host",
  "ip": "172.17.0.1",
  "message": "Hello World!"
}
```

### 6.2 Producto API

Base URL: `http://localhost:8080/api/productos`

#### Crear

```bash
curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre":"Laptop",
    "descripcion":"Ultrabook",
    "precio":1299.99,
    "stock":5
  }'
```

Ejemplo:
```json
{
  "id": 1,
  "nombre": "Laptop",
  "descripcion": "Ultrabook",
  "precio": 1299.99,
  "stock": 5
}
```

#### Listar todos

```bash
curl http://localhost:8080/api/productos
```

#### Filtrar por todos los campos

```bash
curl "http://localhost:8080/api/productos?id=1&nombre=lap&descripcion=ultra&precio=1299.99&stock=5"
```

#### Buscar por id

```bash
curl http://localhost:8080/api/productos/1
```

#### Actualizar

```bash
curl -X PUT http://localhost:8080/api/productos/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre":"Laptop",
    "descripcion":"Ultrabook Pro",
    "precio":1399.99,
    "stock":3
  }'
```

#### Borrar

```bash
curl -X DELETE http://localhost:8080/api/productos/1
```

Respuesta esperada: `204 No Content`

---

## 7) Docker

### Build del jar

```bash
mvn clean package -DskipTests
```

### Docker build

```bash
docker build -t spring-boot-hello-world:0.0.1 .
```

### Docker run con perfil (properties)

```bash
docker run --rm -p 8080:8080 -e APP_ENV=dev spring-boot-hello-world:0.0.1
```

Ejemplos de perfil:

```bash
docker run --rm -p 8080:8080 -e APP_ENV=int  spring-boot-hello-world:0.0.1
docker run --rm -p 8080:8080 -e APP_ENV=qa   spring-boot-hello-world:0.0.1
docker run --rm -p 8080:8080 -e APP_ENV=prod spring-boot-hello-world:0.0.1
```

---

## 8) Cobertura y calidad

- Tests unitarios actualizados.
- Tests de controlador actualizados.
- JaCoCo actualizado para compatibilidad con Java 21.
