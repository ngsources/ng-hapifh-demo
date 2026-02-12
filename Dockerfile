# 1.: build image
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY checkstyle.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 2.: running image
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# JVM memory settings
ENV JAVA_OPTS="-Xmx512m"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
