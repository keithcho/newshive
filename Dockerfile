FROM maven:3.9.9-eclipse-temurin-23

WORKDIR /app

COPY pom.xml .
COPY src src

RUN mvn package -Dmaven.test.skip=true

ENV PORT=8080

EXPOSE ${PORT}

ENTRYPOINT [ "java", "-jar", "target/mini_project-0.0.1-SNAPSHOT.jar" ]