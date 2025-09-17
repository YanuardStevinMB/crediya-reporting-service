FROM eclipse-temurin:21-jdk-alpine

# Crear usuario no-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Crear directorios de trabajo
VOLUME /tmp
WORKDIR /app

# Copiar el JAR
COPY deployment/*.jar CrediyaReporting.jar

# Cambiar permisos del archivo JAR
RUN chown appuser:appgroup CrediyaReporting.jar

# Variables de entorno optimizadas
ENV JAVA_OPTS="-Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
ENV SERVER_PORT=8080

# Exponer puerto
EXPOSE 8080

# Cambiar a usuario no-root
USER appuser

# Punto de entrada
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar CrediyaReporting.jar" ]
