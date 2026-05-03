FROM eclipse-temurin:21-jdk

# Carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el JAR generado por Maven
COPY target/daw2026-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto por defecto de Spring Boot
EXPOSE 8080

# Ejecutamos la app
ENTRYPOINT ["java","-jar","app.jar"]
