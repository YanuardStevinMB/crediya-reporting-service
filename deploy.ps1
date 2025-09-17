# deploy.ps1 - Script de deploy automatizado para Crediya Reporting Service
# Autor: Generado automáticamente
# Fecha: 2025-09-17

Write-Host "==============================================" -ForegroundColor Cyan
Write-Host "   CREDIYA REPORTING SERVICE - DEPLOY" -ForegroundColor Cyan  
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host ""

# Verificar que estamos en el directorio correcto
if (-not (Test-Path "docker-compose.yml")) {
    Write-Error "Error: No se encontró docker-compose.yml. Ejecuta este script desde la raíz del proyecto."
    exit 1
}

Write-Host "✓ Verificando directorio del proyecto..." -ForegroundColor Green
Write-Host "✓ Iniciando proceso de deploy..." -ForegroundColor Green
Write-Host ""

try {
    # 1. Recompilar proyecto
    Write-Host "📦 PASO 1: Recompilando proyecto..." -ForegroundColor Yellow
    Write-Host "   Limpiando compilaciones anteriores..."
    ./gradlew clean
    if ($LASTEXITCODE -ne 0) { 
        throw "Error en gradle clean"
    }

    Write-Host "   Compilando proyecto (sin tests)..."
    ./gradlew build -x test
    if ($LASTEXITCODE -ne 0) { 
        throw "Error en gradle build"
    }
    
    Write-Host "✓ Proyecto recompilado exitosamente" -ForegroundColor Green
    Write-Host ""

    # 2. Copiar JAR
    Write-Host "📋 PASO 2: Copiando JAR al directorio de deployment..." -ForegroundColor Yellow
    if (Test-Path "applications/app-service/build/libs/CrediyaReporting.jar") {
        Copy-Item "applications/app-service/build/libs/CrediyaReporting.jar" "deployment/" -Force
        Write-Host "✓ JAR copiado exitosamente" -ForegroundColor Green
    } else {
        throw "Error: No se encontró el archivo JAR compilado"
    }
    Write-Host ""

    # 3. Detener y eliminar contenedor existente
    Write-Host "🛑 PASO 3: Deteniendo contenedor existente..." -ForegroundColor Yellow
    $containerExists = docker ps -a --format "table {{.Names}}" | Select-String "crediya-reporting-service"
    
    if ($containerExists) {
        Write-Host "   Deteniendo contenedor..."
        docker stop crediya-reporting-service 2>$null
        
        Write-Host "   Eliminando contenedor..."
        docker rm crediya-reporting-service 2>$null
        
        Write-Host "✓ Contenedor eliminado" -ForegroundColor Green
    } else {
        Write-Host "✓ No hay contenedor existente que eliminar" -ForegroundColor Green
    }
    
    # Eliminar imagen existente
    Write-Host "   Eliminando imagen anterior..."
    docker image rm reporting-service-crediya-reporting-service:latest 2>$null
    Write-Host ""

    # 4. Construir nueva imagen
    Write-Host "🔨 PASO 4: Construyendo nueva imagen Docker..." -ForegroundColor Yellow
    docker-compose build --no-cache
    if ($LASTEXITCODE -ne 0) { 
        throw "Error en docker-compose build"
    }
    Write-Host "✓ Imagen construida exitosamente" -ForegroundColor Green
    Write-Host ""

    # 5. Levantar contenedor
    Write-Host "🚀 PASO 5: Iniciando contenedor..." -ForegroundColor Yellow
    docker-compose up -d
    if ($LASTEXITCODE -ne 0) { 
        throw "Error al iniciar contenedor"
    }
    Write-Host "✓ Contenedor iniciado" -ForegroundColor Green
    Write-Host ""

    # 6. Verificar estado
    Write-Host "🔍 PASO 6: Verificando estado del contenedor..." -ForegroundColor Yellow
    Write-Host "   Esperando que el contenedor esté completamente listo..."
    
    $maxAttempts = 12
    $attempt = 0
    $isHealthy = $false
    
    do {
        Start-Sleep -Seconds 5
        $attempt++
        $status = docker inspect crediya-reporting-service --format='{{.State.Health.Status}}' 2>$null
        
        Write-Host "   Intento $attempt/$maxAttempts - Estado: $status"
        
        if ($status -eq "healthy") {
            $isHealthy = $true
            break
        } elseif ($status -eq "unhealthy") {
            break
        }
    } while ($attempt -lt $maxAttempts)

    Write-Host ""
    
    if ($isHealthy) {
        Write-Host "🎉 ¡DEPLOY EXITOSO!" -ForegroundColor Green
        Write-Host "✓ Contenedor está healthy y funcionando correctamente" -ForegroundColor Green
        Write-Host ""
        Write-Host "📊 Estado del contenedor:" -ForegroundColor Cyan
        docker ps | Select-String "crediya"
        Write-Host ""
        Write-Host "🔗 URLs disponibles:" -ForegroundColor Cyan
        Write-Host "   • Aplicación: http://localhost:8080" -ForegroundColor White
        Write-Host "   • Health Check: http://localhost:8080/actuator/health" -ForegroundColor White
        Write-Host "   • Swagger UI: http://localhost:8080/swagger-ui.html" -ForegroundColor White
        
    } else {
        Write-Host "⚠️  ADVERTENCIA: Contenedor no está healthy" -ForegroundColor Yellow
        Write-Host "📋 Logs del contenedor:" -ForegroundColor Yellow
        docker logs crediya-reporting-service --tail 30
        
        Write-Host ""
        Write-Host "🔧 Comandos de troubleshooting:" -ForegroundColor Cyan
        Write-Host "   docker logs crediya-reporting-service" -ForegroundColor White
        Write-Host "   docker exec crediya-reporting-service env | findstr SECURITY" -ForegroundColor White
    }
    
} catch {
    Write-Host ""
    Write-Host "❌ ERROR EN EL DEPLOY:" -ForegroundColor Red
    Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "🔧 Para debugging:" -ForegroundColor Yellow
    Write-Host "   docker logs crediya-reporting-service" -ForegroundColor White
    Write-Host "   docker ps -a" -ForegroundColor White
    exit 1
}

Write-Host ""
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host "         DEPLOY COMPLETADO" -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan