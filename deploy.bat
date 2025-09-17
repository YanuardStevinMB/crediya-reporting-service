@echo off
echo ================================================
echo CREDIYA REPORTING SERVICE - DEPLOYMENT SCRIPT
echo ================================================

echo.
echo [1/4] Limpiando proyecto...
call gradlew clean

if %errorlevel% neq 0 (
    echo Error: No se pudo limpiar el proyecto
    pause
    exit /b 1
)

echo.
echo [2/4] Construyendo aplicacion...
call gradlew build -x test

if %errorlevel% neq 0 (
    echo Error: No se pudo construir la aplicacion
    pause
    exit /b 1
)

echo.
echo [3/4] Copiando JAR al directorio de deployment...
copy "applications\app-service\build\libs\*.jar" "deployment\"

if %errorlevel% neq 0 (
    echo Error: No se pudo copiar el JAR
    pause
    exit /b 1
)

echo.
echo [4/4] Construyendo y ejecutando contenedor Docker...
docker-compose up --build -d

if %errorlevel% neq 0 (
    echo Error: No se pudo construir o ejecutar el contenedor
    pause
    exit /b 1
)

echo.
echo ================================================
echo DESPLIEGUE COMPLETADO EXITOSAMENTE!
echo ================================================
echo.
echo Servicios disponibles:
echo - API Endpoints: http://localhost:8080/api/
echo - Swagger UI: http://localhost:8080/swagger-ui.html
echo - OpenAPI JSON: http://localhost:8080/v3/api-docs
echo - Health Check: http://localhost:8080/actuator/health
echo.
echo Para ver los logs: docker-compose logs -f
echo Para parar el servicio: docker-compose down
echo.
pause