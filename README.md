# Proyecto Base Implementando Clean Architecture

## Antes de Iniciar

Empezaremos por explicar los diferentes componentes del proyectos y partiremos de los componentes externos, continuando con los componentes core de negocio (dominio) y por último el inicio y configuración de la aplicación.

Lee el artículo [Clean Architecture — Aislando los detalles](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)

# Arquitectura

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

## Domain

Es el módulo más interno de la arquitectura, pertenece a la capa del dominio y encapsula la lógica y reglas del negocio mediante modelos y entidades del dominio.

## Usecases

Este módulo gradle perteneciente a la capa del dominio, implementa los casos de uso del sistema, define lógica de aplicación y reacciona a las invocaciones desde el módulo de entry points, orquestando los flujos hacia el módulo de entities.

## Infrastructure

### Helpers

En el apartado de helpers tendremos utilidades generales para los Driven Adapters y Entry Points.

Estas utilidades no están arraigadas a objetos concretos, se realiza el uso de generics para modelar comportamientos
genéricos de los diferentes objetos de persistencia que puedan existir, este tipo de implementaciones se realizan
basadas en el patrón de diseño [Unit of Work y Repository](https://medium.com/@krzychukosobudzki/repository-design-pattern-bc490b256006)

Estas clases no puede existir solas y debe heredarse su compartimiento en los **Driven Adapters**

### Driven Adapters

Los driven adapter representan implementaciones externas a nuestro sistema, como lo son conexiones a servicios rest,
soap, bases de datos, lectura de archivos planos, y en concreto cualquier origen y fuente de datos con la que debamos
interactuar.

### Entry Points

Los entry points representan los puntos de entrada de la aplicación o el inicio de los flujos de negocio.

## Application

Este módulo es el más externo de la arquitectura, es el encargado de ensamblar los distintos módulos, resolver las dependencias y crear los beans de los casos de use (UseCases) de forma automática, inyectando en éstos instancias concretas de las dependencias declaradas. Además inicia la aplicación (es el único módulo del proyecto donde encontraremos la función “public static void main(String[] args)”.

**Los beans de los casos de uso se disponibilizan automaticamente gracias a un '@ComponentScan' ubicado en esta capa.**

# Gestión de Contenedores Docker

## Requisitos Previos
- Docker instalado en su sistema
- Docker Compose instalado en su sistema
- Gradle (para compilación del proyecto)

## Proceso Completo: Recompilación y Despliegue

### 1. Recompilar el Proyecto (Con Cambios Locales)

Cuando hagas cambios en tu código local y necesites reflejarlos en el contenedor:

```powershell
# Limpiar compilaciones anteriores
./gradlew clean

# Compilar el proyecto (saltando tests si hay errores)
./gradlew build -x test

# Copiar el JAR actualizado al directorio de deployment
cp applications/app-service/build/libs/CrediyaReporting.jar deployment/

# Verificar que el JAR se copió correctamente
ls deployment/
```

### 2. Eliminar el Contenedor y la Imagen Existente

Para asegurar que uses el nuevo JAR compilado:

```powershell
# Detener el contenedor
docker stop crediya-reporting-service

# Eliminar el contenedor
docker rm crediya-reporting-service

# Eliminar la imagen anterior para forzar reconstrucción
docker image rm reporting-service-crediya-reporting-service:latest
```

### 3. Limpiar Caché de Docker (Opcional)

Para una limpieza más completa:

```powershell
# Eliminar todas las imágenes no utilizadas
docker image prune -a -f

# Eliminar la caché de construcción
docker builder prune -a -f

# Limpieza completa (usar con precaución)
docker system prune -a -f --volumes
```

### 4. Construir el Nuevo Contenedor

Reconstruir la imagen con el JAR actualizado:

```powershell
docker-compose build --no-cache
```

### 5. Iniciar el Contenedor Actualizado

```powershell
docker-compose up -d
```

### 6. Verificar el Estado del Contenedor

```powershell
# Ver contenedores en ejecución
docker ps

# Ver logs del contenedor
docker logs crediya-reporting-service

# Ver logs en tiempo real
docker logs -f crediya-reporting-service

# Verificar variables de entorno Security
docker exec crediya-reporting-service env | findstr SECURITY
```

## Comandos Rápidos para Desarrollo

### Proceso Completo en Una Secuencia

```powershell
# 1. Recompilar proyecto
./gradlew clean && ./gradlew build -x test

# 2. Copiar JAR
cp applications/app-service/build/libs/CrediyaReporting.jar deployment/

# 3. Reiniciar contenedor con nueva imagen
docker stop crediya-reporting-service
docker rm crediya-reporting-service
docker image rm reporting-service-crediya-reporting-service:latest
docker-compose build --no-cache
docker-compose up -d

# 4. Verificar
docker ps
docker logs crediya-reporting-service --tail 20
```

### Solo Reiniciar Contenedor (Sin Cambios de Código)

Si solo necesitas reiniciar sin recompilar:

```powershell
docker stop crediya-reporting-service
docker rm crediya-reporting-service
docker-compose up -d
```

## Scripts de Troubleshooting

### Verificar Estado Completo del Contenedor

```powershell
# Estado general
docker ps -a | findstr crediya

# Health check status
docker inspect crediya-reporting-service --format='{{.State.Health.Status}}'

# Ver todas las variables de entorno
docker exec crediya-reporting-service env

# Verificar espacio en disco del contenedor
docker exec crediya-reporting-service df -h

# Ver procesos dentro del contenedor
docker exec crediya-reporting-service ps aux
```

### Debug de la Aplicación

```powershell
# Ver logs con timestamp
docker logs crediya-reporting-service --timestamps

# Ver logs desde una fecha específica
docker logs crediya-reporting-service --since="2025-01-01T10:00:00"

# Acceder al contenedor para debugging
docker exec -it crediya-reporting-service sh

# Verificar conectividad de red
docker exec crediya-reporting-service ping google.com

# Test del endpoint de health
curl http://localhost:8080/actuator/health
```

### Limpieza Completa del Sistema Docker

```powershell
# PRECAUCIÓN: Esto eliminará TODOS los contenedores, imágenes y volúmenes no utilizados
docker system prune -a -f --volumes

# Ver espacio ocupado por Docker
docker system df

# Ver estadísticas de uso de Docker
docker stats crediya-reporting-service
```

### Backup y Restauración

```powershell
# Crear backup de la imagen
docker save reporting-service-crediya-reporting-service:latest -o crediya-backup.tar

# Restaurar imagen desde backup
docker load -i crediya-backup.tar

# Exportar logs a archivo
docker logs crediya-reporting-service > logs-backup.txt
```

## Scripts de Automatización

### Script PowerShell para Deploy Completo

Puedes crear un archivo `deploy.ps1` con el siguiente contenido:

```powershell
# deploy.ps1
Write-Host "Iniciando proceso de deploy..." -ForegroundColor Green

# 1. Recompilar proyecto
Write-Host "Recompilando proyecto..." -ForegroundColor Yellow
./gradlew clean
if ($LASTEXITCODE -ne 0) { Write-Error "Error en clean"; exit 1 }

./gradlew build -x test
if ($LASTEXITCODE -ne 0) { Write-Error "Error en build"; exit 1 }

# 2. Copiar JAR
Write-Host "Copiando JAR..." -ForegroundColor Yellow
cp applications/app-service/build/libs/CrediyaReporting.jar deployment/

# 3. Reiniciar contenedor
Write-Host "Reiniciando contenedor..." -ForegroundColor Yellow
docker stop crediya-reporting-service 2>$null
docker rm crediya-reporting-service 2>$null
docker image rm reporting-service-crediya-reporting-service:latest 2>$null

# 4. Construir y levantar
docker-compose build --no-cache
if ($LASTEXITCODE -ne 0) { Write-Error "Error en build de Docker"; exit 1 }

docker-compose up -d
if ($LASTEXITCODE -ne 0) { Write-Error "Error al levantar contenedor"; exit 1 }

# 5. Verificar
Write-Host "Verificando contenedor..." -ForegroundColor Yellow
Start-Sleep -Seconds 10
$status = docker inspect crediya-reporting-service --format='{{.State.Health.Status}}' 2>$null

if ($status -eq "healthy") {
    Write-Host "Deploy exitoso! Contenedor está healthy" -ForegroundColor Green
    docker ps | findstr crediya
} else {
    Write-Host "Advertencia: Contenedor no está healthy. Revisando logs..." -ForegroundColor Red
    docker logs crediya-reporting-service --tail 20
}
```

### Scripts Incluidos

En el directorio raíz del proyecto tienes disponibles los siguientes scripts:

#### 1. Script de Deploy Completo (`deploy.ps1`)
Proceso completo con recompilación:

```powershell
# Ejecutar deploy completo (recompila y despliega)
.\deploy.ps1
```

#### 2. Script de Reinicio Rápido (`quick-restart.ps1`)
Solo reinicia el contenedor sin recompilar:

```powershell
# Reinicio rápido sin recompilación
.\quick-restart.ps1
```

### Resumen de Comandos Principales

```powershell
# Deploy completo con cambios de código
.\deploy.ps1

# Reinicio rápido sin cambios
.\quick-restart.ps1

# Verificar estado
docker ps | findstr crediya
docker logs crediya-reporting-service --tail 20

# Verificar configuración Security
docker exec crediya-reporting-service env | findstr SECURITY
```

## Configuración JWT

Se ha actualizado el archivo `docker-compose.yml` para incluir la configuración Security (JWT). Las siguientes variables de entorno se han añadido:

```yaml
# Security Configuration (JWT)
- SECURITY_SECRET=${SECURITY_SECRET:-QnE1T2lXbVRhV3RzR2VOUXlHaFZ2d2dyU2p2a1R2TnM=}
- SECURITY_EXPIRATION_SEC=${SECURITY_EXPIRATION_SEC:-3600}
- SECURITY_ISSUER=${SECURITY_ISSUER:-autenticacion-service}
- SECURITY_ISSUER_URI=${SECURITY_ISSUER_URI:-}
```

### Configuración de Variables de Entorno JWT

Para configurar las variables de entorno JWT antes de iniciar el contenedor, puede:

1. Crear un archivo `.env` en el mismo directorio que el `docker-compose.yml` con el siguiente contenido:

```
SECURITY_SECRET=QnE1T2lXbVRhV3RzR2VOUXlHaFZ2d2dyU2p2a1R2TnM=
SECURITY_EXPIRATION_SEC=3600
SECURITY_ISSUER=autenticacion-service
SECURITY_ISSUER_URI=
```

2. O establecer las variables directamente al iniciar el contenedor:

```powershell
$env:SECURITY_SECRET="QnE1T2lXbVRhV3RzR2VOUXlHaFZ2d2dyU2p2a1R2TnM="; $env:SECURITY_EXPIRATION_SEC="3600"; $env:SECURITY_ISSUER="autenticacion-service"; docker-compose up -d
```

## Resolución de Problemas Comunes

### El Contenedor No Inicia
- Verificar los logs: `docker logs crediya-reporting-service`
- Comprobar si hay conflictos de puertos: `netstat -ano | findstr 8080`
- Verificar que todas las variables de entorno requeridas están configuradas

### Problemas de Conexión
- Verificar que el contenedor está en ejecución: `docker ps`
- Comprobar la configuración de red: `docker network ls`
- Intentar acceder directamente al contenedor: `docker exec -it crediya-reporting-service sh`

### Errores de Security/JWT
- Verificar que la variable SECURITY_SECRET está correctamente configurada
- Comprobar los logs para errores relacionados con JWT
- Asegurarse de que la aplicación está configurada para usar las variables de entorno Security
