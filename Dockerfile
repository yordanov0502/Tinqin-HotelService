FROM amazoncorretto:21-alpine
LABEL authors="yordanov0502"
WORKDIR /app
EXPOSE 8080
COPY rest/target/rest-0.0.1-SNAPSHOT.jar /app/hotel.jar

ENTRYPOINT ["java", "-jar", "/app/hotel.jar"]
