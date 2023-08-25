FROM openjdk:17-ea-17-jdk-oracle

WORKDIR /app

COPY target/store-management-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]