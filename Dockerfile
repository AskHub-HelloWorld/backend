FROM amazoncorretto:21
WORKDIR /app
COPY *.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]