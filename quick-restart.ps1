# quick-restart.ps1 - Restart rápido sin recompilación
# Para usar cuando solo necesitas reiniciar el contenedor sin cambios de código

Write-Host "🔄 Reinicio rápido del contenedor..." -ForegroundColor Cyan

# Detener contenedor
Write-Host "🛑 Deteniendo contenedor..." -ForegroundColor Yellow
docker stop crediya-reporting-service 2>$null

# Eliminar contenedor  
Write-Host "🗑️ Eliminando contenedor..." -ForegroundColor Yellow
docker rm crediya-reporting-service 2>$null

# Iniciar contenedor
Write-Host "🚀 Iniciando contenedor..." -ForegroundColor Yellow
docker-compose up -d

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Contenedor reiniciado exitosamente" -ForegroundColor Green
    
    # Esperar un poco y mostrar estado
    Start-Sleep -Seconds 3
    Write-Host "📊 Estado actual:" -ForegroundColor Cyan
    docker ps | Select-String "crediya"
} else {
    Write-Host "❌ Error al reiniciar contenedor" -ForegroundColor Red
    docker logs crediya-reporting-service --tail 10
}