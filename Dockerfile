FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-Xmx256m", "-jar", "app.jar"]