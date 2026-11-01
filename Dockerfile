# Etapa 1: compilar el proyecto con Maven y Java 25
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline
COPY src ./src
# Los tests ya corren en el CI; acá solo se arma el jar.
RUN ./mvnw -B package -Dmaven.test.skip=true

# Etapa 2: imagen liviana que solo ejecuta el jar
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# MaxRAMPercentage limita la memoria de Java: los planes gratuitos tienen poca RAM.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]