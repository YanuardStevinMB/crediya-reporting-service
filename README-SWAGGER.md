# Crediya Reporting Service - Configuración Swagger

## 🚀 Resumen de Implementación

Se ha configurado exitosamente **Swagger/OpenAPI 3.0** en tu proyecto Spring WebFlux con arquitectura limpia. Ahora puedes documentar y probar tus APIs fácilmente.

## 📋 Cambios Realizados

### 1. Dependencias Agregadas
- `springdoc-openapi-starter-webflux-ui:2.6.0`
- `springdoc-openapi-starter-common:2.6.0`

### 2. Configuración de Swagger
- **SwaggerConfig.java**: Configuración personalizada de OpenAPI
- **Handler.java**: Documentación de endpoints con anotaciones
- **application-docker.yml**: Configuración específica para Docker

### 3. Dockerización
- **Dockerfile mejorado**: Optimizado para seguridad y rendimiento
- **docker-compose.yml**: Despliegue simplificado
- **deploy.bat**: Script automatizado de construcción y despliegue

## 🔧 Cómo Ejecutar

### Opción 1: Con Script Automatizado (Recomendado)
```bash
./deploy.bat
```

### Opción 2: Paso a Paso
1. **Construir el proyecto**:
   ```bash
   ./gradlew clean build -x test
   ```

2. **Copiar JAR**:
   ```bash
   copy "applications\app-service\build\libs\*.jar" "deployment\"
   ```

3. **Ejecutar con Docker**:
   ```bash
   docker-compose up --build -d
   ```

## 🌐 URLs Disponibles

Una vez ejecutado, tendrás acceso a:

| Servicio | URL | Descripción |
|----------|-----|-------------|
| **Swagger UI** | http://localhost:8080/swagger-ui.html | Interfaz interactiva de documentación |
| **OpenAPI JSON** | http://localhost:8080/v3/api-docs | Especificación OpenAPI en JSON |
| **OpenAPI YAML** | http://localhost:8080/v3/api-docs.yaml | Especificación OpenAPI en YAML |
| **Health Check** | http://localhost:8080/actuator/health | Estado de la aplicación |
| **API Endpoints** | http://localhost:8080/api/ | Endpoints de tu API |

## 📄 Endpoints Documentados

### GET `/api/usecase/path`
- **Descripción**: Obtener datos del primer caso de uso
- **Respuestas**: 200 (OK), 400 (Bad Request), 500 (Internal Error)

### POST `/api/usecase/otherpath`
- **Descripción**: Crear o procesar datos
- **Respuestas**: 200 (OK), 201 (Created), 400 (Bad Request), 500 (Internal Error)

### GET `/api/otherusercase/path`
- **Descripción**: Obtener datos del segundo caso de uso
- **Respuestas**: 200 (OK), 400 (Bad Request), 500 (Internal Error)

## 🐳 Comandos Docker Útiles

```bash
# Ver logs en tiempo real
docker-compose logs -f

# Parar el servicio
docker-compose down

# Reconstruir completamente
docker-compose down && docker-compose up --build -d

# Ver contenedores ejecutándose
docker ps

# Acceder al contenedor
docker exec -it crediya-reporting-service sh
```

## 📝 Estructura de Archivos Agregados/Modificados

```
REPORTING-SERVICE/
├── infrastructure/entry-points/reactive-web/
│   ├── build.gradle                          # ← Dependencias Swagger agregadas
│   └── src/main/java/com/crediya/
│       ├── api/
│       │   ├── Handler.java                  # ← Anotaciones Swagger agregadas
│       │   └── RouterRest.java               # ← Documentación agregada
│       └── config/
│           └── SwaggerConfig.java            # ← NUEVO: Configuración Swagger
├── applications/app-service/src/main/resources/
│   └── application-docker.yml                # ← NUEVO: Config para Docker
├── deployment/
│   └── Dockerfile                            # ← Mejorado: Puerto expuesto y seguridad
├── docker-compose.yml                        # ← NUEVO: Orquestación Docker
├── deploy.bat                                # ← NUEVO: Script de despliegue
└── README-SWAGGER.md                         # ← NUEVO: Esta documentación
```

## 🎯 Próximos Pasos

1. **Probar los endpoints**: Ve a http://localhost:8080/swagger-ui.html
2. **Personalizar documentación**: Modifica las anotaciones en `Handler.java`
3. **Agregar nuevos endpoints**: Sigue el mismo patrón de documentación
4. **Configurar ambientes**: Crea profiles para dev, test, prod

## 🛠️ Personalización

### Modificar información de la API
Edita `SwaggerConfig.java` para cambiar:
- Título y descripción
- Información de contacto
- Servidores disponibles
- Licencia

### Agregar más documentación
En tus handlers, usa estas anotaciones:
```java
@Operation(summary = "Resumen", description = "Descripción detallada")
@Parameter(name = "param", description = "Descripción del parámetro")
@ApiResponse(responseCode = "200", description = "Éxito")
```

¡Listo! Ya tienes Swagger completamente configurado y dockerizado. 🎉