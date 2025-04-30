FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
VOLUME /tmp
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

ENV JAVA_OPTS="-DIGNITE_INSTANCE_NAME=ignite-node"
EXPOSE 8080 47500-47509

ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
