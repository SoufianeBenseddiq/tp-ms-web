# ---------- Étape 1 : build du jar ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Dépendances d'abord (mises en cache tant que le pom.xml ne change pas)
COPY pom.xml .
RUN mvn -q dependency:go-offline

COPY src ./src
RUN mvn -q package -DskipTests

# ---------- Étape 2 : image d'exécution ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
