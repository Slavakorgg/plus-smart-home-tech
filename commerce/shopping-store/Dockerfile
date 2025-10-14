FROM amazoncorretto:21-alpine
LABEL authors="slavakorg"
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]