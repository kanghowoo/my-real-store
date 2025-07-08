FROM gradle:8.7-jdk17 AS builder

WORKDIR /app

#COPY build.gradle settings.gradle gradlew gradle/ ./
COPY build.gradle .
COPY settings.gradle .

RUN gradle dependencies --no-daemon

COPY src ./src

RUN gradle build -x test --no-daemon

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
