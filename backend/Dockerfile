FROM maven:3-jdk-8-onbuild-alpine AS builder

FROM openjdk:8-jre-alpine
COPY --from=builder /usr/src/app/target/mc-fitbit-refuel.jar /app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]