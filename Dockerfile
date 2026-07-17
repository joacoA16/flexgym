# 1. Usamos una imagen de Java 17 (o 21 si usas esa versión)
FROM eclipse-temurin:21-jdk AS build

# 2. Instalamos Maven para compilar
RUN apt-get update && apt-get install -y maven

# 3. Copiamos el código
WORKDIR /app
COPY . .

# 4. Compilamos el proyecto (sin tests para ir más rápido)
RUN mvn clean package -DskipTests

# 5. Imagen final más ligera para ejecutar
# ¡AQUÍ ESTÁ EL CAMBIO! Quitamos el "AS build"
FROM eclipse-temurin:21-jre 
WORKDIR /app
# Copiamos solo el jar generado en la etapa "build"
COPY --from=build /app/target/*.jar app.jar

# 6. Exponemos el puerto 8080
EXPOSE 8080

# 7. Comando para arrancar
ENTRYPOINT ["java", "-jar", "app.jar"]