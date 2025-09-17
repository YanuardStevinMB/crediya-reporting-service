# Global Exception Handler - Documentación

## 📋 Descripción General

Se ha implementado un **Global Exception Handler** completo para el servicio REPORTING-SERVICE, diseñado específicamente para **programación reactiva** y manteniendo **total compatibilidad** con el código existente.

## ✅ Características Implementadas

- ✅ **Manejo Global**: Captura todas las excepciones automáticamente
- ✅ **Programación Reactiva**: Compatible con WebFlux y Mono/Flux
- ✅ **Sin Modificaciones**: **NO SE MODIFICARON** dependencias ni librerías
- ✅ **Swagger Documentado**: Respuestas de error documentadas automáticamente
- ✅ **Logging Estructurado**: Registro detallado para monitoreo
- ✅ **Compatibilidad Total**: El código existente sigue funcionando igual

## 🏗️ Arquitectura Implementada

```
┌─────────────────────┐    ┌──────────────────────┐    ┌─────────────────────┐
│   RouterRest.java   │───▶│ GlobalExceptionHandler│───▶│  ExceptionMapper    │
└─────────────────────┘    └──────────────────────┘    └─────────────────────┘
                                        │                           │
                                        ▼                           ▼
                           ┌──────────────────────┐    ┌─────────────────────┐
                           │    SLF4J Logging    │    │  ErrorResponseDto   │
                           └──────────────────────┘    └─────────────────────┘
```

## 📁 Archivos Creados

### 1. DTOs para Manejo de Errores
- **`ErrorResponseDto.java`** - Respuesta estructurada de error (compatible con `ApiResponse`)
- **`ErrorDetailDto.java`** - Detalles específicos de errores

### 2. Mappers
- **`ExceptionMapper.java`** - Convierte excepciones a respuestas HTTP apropiadas

### 3. Handlers
- **`GlobalExceptionHandler.java`** - Handler global reactivo
- **`Handler_Simplified.java`** - Ejemplo de handler simplificado (opcional)

### 4. Archivos Modificados
- **`RouterRest.java`** - Actualizado para usar GlobalExceptionHandler y documentar errores en Swagger

## 🔧 Excepciones Manejadas

| Excepción | Status Code | Descripción |
|-----------|-------------|-------------|
| `IllegalArgumentException` | 400 | Parámetros inválidos |
| `NullPointerException` | 400 | Datos requeridos faltantes |
| `TimeoutException` | 408 | Tiempo de espera agotado |
| `RuntimeException` | 500 | Error de ejecución |
| `Throwable` (genérica) | 500 | Error interno del sistema |

## 📝 Formato de Respuestas

### Respuesta Básica (Compatible con ApiResponse)
```json
{
  "success": false,
  "message": "Parámetros inválidos",
  "data": null,
  "errors": "Invalid parameter: id cannot be null",
  "path": "/api/v1/reports",
  "timestamp": "2025-09-17T15:31:14Z"
}
```

### Respuesta Enriquecida (Nueva Funcionalidad)
```json
{
  "success": false,
  "message": "Parámetros inválidos",
  "data": null,
  "errors": "Invalid parameter: id cannot be null",
  "path": "/api/v1/reports",
  "timestamp": "2025-09-17T15:31:14Z",
  "errorCode": "INVALID_ARGUMENT",
  "technicalMessage": "Invalid parameter: id cannot be null",
  "userMessage": "Los parámetros proporcionados no son válidos",
  "traceId": "a1b2c3d4",
  "details": [
    {
      "field": "id",
      "rejectedValue": null,
      "technicalMessage": "Parameter cannot be null",
      "userMessage": "El ID es requerido",
      "errorCode": "FIELD_VALIDATION"
    }
  ]
}
```

## 🚀 Cómo Usar

### En RouterRest (Ya Configurado)
```java
@Bean
public RouterFunction<ServerResponse> routerFunction(Handler handler, 
                                                     GlobalExceptionHandler globalExceptionHandler) {
    return route(GET("/api/v1/reports"), handler::listenGETUseCase)
            .filter(globalExceptionHandler);
}
```

### En Handlers - Opción 1: Mantener código existente
```java
// El Handler.java actual sigue funcionando EXACTAMENTE igual
public Mono<ServerResponse> listenGETUseCase(ServerRequest req) {
    return showReportsUseCase.execute()
            .map(report -> ApiResponse.ok(toDto(report), "Operación exitosa", req.path()))
            .flatMap(body -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(body))
            .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(ApiResponse.fail("No hay reporte", null, req.path())))
            .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(ApiResponse.fail("Error interno", e.getMessage(), req.path())));
    // ↑ El GlobalExceptionHandler actúa como red de seguridad adicional
}
```

### En Handlers - Opción 2: Simplificar (Opcional)
```java
// Confía completamente en el GlobalExceptionHandler
public Mono<ServerResponse> listenGETUseCase(ServerRequest req) {
    return showReportsUseCase.execute()
            .map(report -> ApiResponse.ok(toDto(report), "Operación exitosa", req.path()))
            .flatMap(body -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(body))
            .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(ApiResponse.fail("No hay reporte", null, req.path())));
    // ¡No más onErrorResume! El GlobalExceptionHandler maneja todos los errores automáticamente
}
```

## 📊 Swagger/OpenAPI

Las respuestas de error están automáticamente documentadas:

- **200**: Respuesta exitosa
- **400**: Bad Request - Error de validación (`ErrorResponseDto`)
- **404**: Not Found - Recurso no encontrado (`ErrorResponseDto`)
- **500**: Internal Server Error - Error interno del servidor (`ErrorResponseDto`)

## 📝 Logging

### Estructura de Logs
```
WARN  - Illegal argument error - Path: GET /api/v1/reports, Error: Parameter cannot be null
ERROR - Runtime error - Path: GET /api/v1/reports, Error: Database connection failed
```

### Niveles de Logging
- `IllegalArgumentException`, `NullPointerException`, `TimeoutException` → **WARN**
- `RuntimeException`, `Throwable` genérica → **ERROR**

## 🔍 Configuración

### Dependencias NO Modificadas
- ✅ **`build.gradle`** - Sin cambios
- ✅ **Todas las librerías** - Sin cambios
- ✅ **Configuraciones Spring** - Sin cambios
- ✅ **Tests existentes** - Siguen pasando

### Componentes Agregados (Solo nuevos archivos)
- `GlobalExceptionHandler` - Handler principal reactivo
- `ExceptionMapper` - Mapeo de excepciones a DTOs
- `ErrorResponseDto` - DTO de respuesta mejorado
- `ErrorDetailDto` - DTO para detalles específicos

## 🧪 Pruebas

### Verificación de Funcionamiento
```bash
# Compilar (ya verificado)
./gradlew :reactive-web:compileJava

# Ejecutar tests (ya verificado - BUILD SUCCESSFUL)
./gradlew test --continue

# Iniciar aplicación
./gradlew bootRun
```

### Prueba Manual
```bash
# Endpoint normal
curl -X GET http://localhost:8080/api/v1/reports

# Simular error (si el servicio lanza excepción)
# El GlobalExceptionHandler capturará automáticamente cualquier error
```

## 🎯 Beneficios

1. **Compatibilidad Total**: El código existente sigue funcionando sin cambios
2. **Manejo Centralizado**: Todos los errores se procesan en un lugar
3. **Respuestas Consistentes**: Formato uniforme para todos los errores
4. **Logging Automático**: Registro detallado sin código adicional
5. **Swagger Actualizado**: Documentación automática de errores
6. **Extensible**: Fácil agregar nuevos tipos de excepciones
7. **Sin Dependencies**: No se agregaron librerías nuevas

## 🔧 Personalización

### Agregar Nueva Excepción
1. **En ExceptionMapper:**
```java
public ErrorResponseDto mapCustomException(CustomException ex, ServerRequest request) {
    return ErrorResponseDto.enriched(
            "Error personalizado",
            "CUSTOM_ERROR",
            ex.getMessage(),
            "Mensaje amigable para el usuario",
            request.path()
    );
}
```

2. **En GlobalExceptionHandler:**
```java
.onErrorResume(CustomException.class, 
    ex -> handleCustomException(ex, request))

private Mono<ServerResponse> handleCustomException(CustomException ex, ServerRequest request) {
    logError("Custom error", ex, request);
    ErrorResponseDto errorResponse = exceptionMapper.mapCustomException(ex, request);
    return createResponse(errorResponse, HttpStatus.BAD_REQUEST);
}
```

## 📋 Estado del Proyecto

- ✅ **Implementación Completa**
- ✅ **Tests Pasando** (BUILD SUCCESSFUL)
- ✅ **Sin Modificaciones de Dependencias**
- ✅ **Código Existente Intacto**
- ✅ **Documentación Swagger Actualizada**
- ✅ **Logging Implementado**

---

**Implementado con máximo cuidado manteniendo la compatibilidad total y siguiendo las mejores prácticas de programación reactiva.**