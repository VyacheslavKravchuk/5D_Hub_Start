FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./target/eureka_server-0.0.1-SNAPSHOT.jar /app/eureka_server.jar

ENTRYPOINT ["java", "-jar", "app.jar"]